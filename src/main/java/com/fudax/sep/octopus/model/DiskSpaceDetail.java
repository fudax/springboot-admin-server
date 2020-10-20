package com.fudax.sep.octopus.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyi4
 */
public class DiskSpaceDetail {
    private Long total;
    private Long free;
    private Long threshold;

    public Map<String, Object> detailToMap() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>(32);
        try {
            map = mapper.readValue(mapper.writeValueAsString(this), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
        }
        return map;
    }

    public double freeSpaceRate() {
        return rate(free, total);
    }

    public double rate(Long chushu, Long fenmu) {
        return BigDecimal.valueOf(chushu).divide(BigDecimal.valueOf(fenmu), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }
}
