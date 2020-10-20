package com.fudax.sep.octopus.detector;


import de.codecentric.boot.admin.server.domain.values.InstanceId;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

/**
 * @author liuyi4
 */
public class SystemMetricTimeIntervalCheck {

    private Map<InstanceId, Instant> lastMailNotifyMap;
    private long intervalSeconds;

    public SystemMetricTimeIntervalCheck() {
        this(30 * 60);
    }

    public SystemMetricTimeIntervalCheck(long interval) {
        this.intervalSeconds = interval;
        lastMailNotifyMap = new ConcurrentHashMap<>();
    }

    public boolean checkMailNotifyInterval(InstanceId instanceId) {
        Instant instant = lastMailNotifyMap.get(instanceId);
        if (isNull(instant)) {
            return true;
        }
        return instant.isBefore(Instant.now());
    }

    public void updateNotifyTime(InstanceId instanceId) {
        lastMailNotifyMap.put(instanceId, Instant.now().plusSeconds(intervalSeconds));
    }

    public void setInterval(long interval) {
        this.intervalSeconds = interval;
    }

}
