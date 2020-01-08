package com.fudax.octopus.constants;


import static org.springframework.util.StringUtils.isEmpty;

public enum Env {
    //
    TEST("测试环境"),
    PROD("生产环境"),
    UNKNOWN("未知环境");
    private String name;

    Env(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Env valueOfName(String envName) {
        Env env;
        if (isEmpty(envName)) {
            env = Env.UNKNOWN;
        } else {
            env = Env.valueOf(envName);
        }
        return env;
    }
}
