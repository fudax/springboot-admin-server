package com.fudax.sep.octopus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liuyi4
 */
@ConfigurationProperties(prefix = "system.metric.monitor")
public class SystemMetricProperties {

    private double cpu;
    private Double disk;

    /**
     * 每隔多久告警一次
     */
    private Integer intervalMinutes = 2;

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public Double getDisk() {
        return disk;
    }

    public void setDisk(Double disk) {
        this.disk = disk;
    }

    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }
}
