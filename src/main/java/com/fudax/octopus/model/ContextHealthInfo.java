package com.fudax.octopus.model;

import java.util.Objects;
import java.util.Optional;

public class ContextHealthInfo {
    private String status;
    private Optional<HealthDetail> details;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Optional<HealthDetail> getDetails() {
        return details;
    }

    public void setDetails(Optional<HealthDetail> details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextHealthInfo that = (ContextHealthInfo) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, details);
    }
}
