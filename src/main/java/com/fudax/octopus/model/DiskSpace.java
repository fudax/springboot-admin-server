package com.fudax.octopus.model;

import java.util.Optional;

public class DiskSpace {

    private String status;
    private Optional<DiskSpaceDetail> details;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Optional<DiskSpaceDetail> getDetails() {
        return details;
    }

    public void setDetails(Optional<DiskSpaceDetail> details) {
        this.details = details;
    }

}
