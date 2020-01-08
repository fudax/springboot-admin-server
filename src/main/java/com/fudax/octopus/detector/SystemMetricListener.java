package com.fudax.octopus.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.security.auth.Destroyable;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/**
 * 被监控应用定制化健康检查并告警{@link SystemMetricCheck}
 */
public class SystemMetricListener implements InitializingBean, Destroyable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemMetricListener.class);
    private final ThreadPoolTaskScheduler taskScheduler;
    private Duration checkPeriod = Duration.ofSeconds(10);
    private volatile ScheduledFuture<?> scheduledTask;
    private final SystemMetricCheck systemMetricCheck;

    public SystemMetricListener(SystemMetricCheck systemMetricCheck) {
        this.systemMetricCheck = systemMetricCheck;
        this.taskScheduler = createThreadPoolTaskScheduler();
    }

    private static ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setBeanName("task-system-metric");
        taskScheduler.setErrorHandler(e -> LOGGER.error("error:", e));
        return taskScheduler;
    }


    @EventListener
    @Order()
    public void onApplicationReady(ApplicationReadyEvent event) {
        startCheckSystemMetrics();
    }

    @EventListener
    @Order()
    public void onClosedContext(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null ||
                "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            stopCheckSystemMetrics();
        }
    }

    private void startCheckSystemMetrics() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            return;
        }
        scheduledTask = taskScheduler.scheduleAtFixedRate(systemMetricCheck::check, checkPeriod);
        LOGGER.debug("Scheduled check task for every {}minutes", checkPeriod);
    }

    public void stopCheckSystemMetrics() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(true);
            LOGGER.debug("Canceled check task");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        taskScheduler.afterPropertiesSet();
    }

    @Override
    public void destroy() {
        taskScheduler.destroy();
    }
}
