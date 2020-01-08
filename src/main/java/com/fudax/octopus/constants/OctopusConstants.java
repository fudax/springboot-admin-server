package com.fudax.octopus.constants;

public final class OctopusConstants {

    private OctopusConstants() {
        throw new IllegalAccessError();
    }

    /**
     * 为哪些服务开启特殊权限
     */
    public static final String[] SPECIAL_CONTEXT = new String[]{"sqcs", "sepp"};

}
