package com.fudax.octopus.model;

import java.util.Optional;

public class HealthDetail {

    private Optional<DiskSpace> diskSpace;

    public Optional<DiskSpace> getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(Optional<DiskSpace> diskSpace) {
        this.diskSpace = diskSpace;
    }
}
