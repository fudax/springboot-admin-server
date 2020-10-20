package com.fudax.sep.octopus.constants;

/**
 * @author liuyi4
 */
public final class OctopusConstants {

    private OctopusConstants() {
        throw new IllegalAccessError();
    }

    /**
     * 为哪些服务开启特殊权限
     */
    public static final String[] SPECIAL_CONTEXT = new String[]{"sep"};

}
