package com.example.mybatisdemo.timeWheel;

import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */
public class Time {

    public static Long getHiresClockMs() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }
}
