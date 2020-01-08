package com.fudax.octopus.model;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;

import java.io.Serializable;
import java.util.Map;

public class ContextUi implements Serializable {

    private Instance instance;
    private InstanceEvent event;
    private String lastStatus;
    private String baseUrl;

    private ContextType type;

    private Map<String, Object> details;


    public static ContextUi eventApply(InstanceEvent event, Instance instance,
                                       String baseUrl, String lastStatus) {
        ContextUi contextUi = new ContextUi();
        contextUi.setType(ContextType.EVENT);
        contextUi.setInstance(instance);
        contextUi.setEvent(event);
        contextUi.setLastStatus(lastStatus);
        return contextUi;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public InstanceEvent getEvent() {
        return event;
    }

    public void setEvent(InstanceEvent event) {
        this.event = event;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getType() {
        return type.name();
    }

    public void setType(ContextType type) {
        this.type = type;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
