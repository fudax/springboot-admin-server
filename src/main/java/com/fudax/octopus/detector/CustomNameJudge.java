package com.fudax.octopus.detector;

import java.util.Objects;

public final class CustomNameJudge {
    private CustomNameJudge() {
        throw new AssertionError();
    }

    public static boolean someContextCanPass(final String name,
                                              final String... names) {
        if (Objects.isNull(name)) return false;
        for (String s : names) {
            if (name.contains(s)) return true;
        }
        return false;
    }
}
