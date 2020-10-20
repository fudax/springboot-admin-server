package com.fudax.sep.octopus.constants;


import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author liuyi4
 */

public enum Environment {
    /**
     * envs
     */
    DEV("开发环境"),
    TEST("测试环境"),
    PROD("生产环境"),
    UNKNOWN("未知环境");
    private String name;

    Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Environment valueOfName(String envName) {
        Environment env;
        if (isEmpty(envName)) {
            env = Environment.UNKNOWN;
        } else {
            env = Environment.valueOf(envName.toUpperCase());
        }
        return env;
    }
}
