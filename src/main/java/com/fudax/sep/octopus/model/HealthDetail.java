package com.fudax.sep.octopus.model;

import java.util.Optional;

/**
 * @author liuyi4
 */
public class HealthDetail {

    private Optional<DiskSpace> diskSpace;

    public Optional<DiskSpace> getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(Optional<DiskSpace> diskSpace) {
        this.diskSpace = diskSpace;
    }
}
