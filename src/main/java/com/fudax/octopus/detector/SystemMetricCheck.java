package com.fudax.octopus.detector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fudax.octopus.configuration.SystemMetricProperties;
import com.fudax.octopus.model.*;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SystemMetricCheck {

    private static final Logger log = LoggerFactory.getLogger(SystemMetricCheck.class);
    private final InstanceRegistry registry;
    private final SystemMetricProperties systemMetricProperties;
    private CustomMailNotifier mailNotifier;
    private SystemMetricTimeIntervalCheck intervalCheck;
    private ObjectMapper mapper;

    public SystemMetricCheck(InstanceRegistry registry,
                             SystemMetricProperties systemMetricProperties,
                             CustomMailNotifier mailNotifier,
                             ObjectMapper mapper) {
        this.mapper = mapper;
        this.mailNotifier = mailNotifier;
        this.systemMetricProperties = systemMetricProperties;
        this.registry = registry;
        this.intervalCheck = new SystemMetricTimeIntervalCheck();
    }

    public void check() {
        registry.getInstances()
                .filter(Instance::isRegistered)
                .filter(this::needCheck)
                .doOnError(Throwable::printStackTrace)
                .subscribe(this::check);
    }

    protected void check(Instance instance) {
        if (intervalCheck.checkMailNotifyInterval(instance.getId())) {
            log.debug("check status for {}", instance);
            try {
                String infoStr = mapper.writeValueAsString(instance.getStatusInfo());
                ContextHealthInfo info = mapper.readValue(infoStr, createTypeReference());
                DiskSpaceMetric(info, instance);
            } catch (Exception e) {
                log.error("Cannot write or read value", e);
            }
        }
    }

    private boolean needCheck(Instance instance) {
        Map<String, String> metadata = instance.getRegistration().getMetadata();
        String sys = metadata.get("sys");
        if (Objects.isNull(sys)) {
            return false;
        }
        return Boolean.valueOf(sys);
    }

    public void DiskSpaceMetric(ContextHealthInfo healthInfo, Instance instance) {
        healthInfo.getDetails()
                .map(HealthDetail::getDiskSpace)
                .map(diskSpace -> diskSpace.map(DiskSpace::getDetails))
                .ifPresent(diskSpaceDetail -> diskSpaceDetail.ifPresent(d -> doNotify(d, instance)));
    }

    public void doNotify(Optional<DiskSpaceDetail> diskSpaceDetailOptional, Instance instance) {
        Double disk = systemMetricProperties.getDisk();
        diskSpaceDetailOptional.filter(diskSpaceDetail -> 1.0 - diskSpaceDetail.freeSpaceRate() >= disk)
                .ifPresent(diskSpaceDetail1 -> {
                    doNotify(diskSpaceDetail1, 1.0 - diskSpaceDetail1.freeSpaceRate(), instance);
                    intervalCheck.updateNotifyTime(instance.getId());
                });
    }

    /**
     * @param diskSpaceDetail
     * @param useDiskRate
     * @param instance
     */
    private void doNotify(DiskSpaceDetail diskSpaceDetail, Double useDiskRate, Instance instance) {
        Context ctx = new Context();
        ContextUi contextUi = new ContextUi();
        contextUi.setType(ContextType.ALERT);
        contextUi.setBaseUrl(instance.getRegistration().getServiceUrl());
        contextUi.setDetails(diskSpaceDetail.detailToMap());
        ctx.setVariable("info", contextUi);
        mailNotifier.doNotify(ctx, String.format("【紧急】磁盘使用率为%s超过了阈值", useDiskRate));
    }

    public static TypeReference<ContextHealthInfo> createTypeReference() {
        return new TypeReference<ContextHealthInfo>() {
        };
    }
}
