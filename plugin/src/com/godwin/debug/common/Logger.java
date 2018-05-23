package com.godwin.debug.common;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class Logger {
    public static void i(String tag, String message) {
        System.out.println(new StringBuilder().append("i: ").append(tag).append(": ").append(message));
    }

    public static void d(String tag, String message) {
        System.out.println(new StringBuilder().append("d: ").append(tag).append(": ").append(message));
    }

    public static void e(String tag, String message) {
        System.out.println(new StringBuilder().append("e: ").append(tag).append(": ").append(message));
    }

    public static void e(Throwable throwable) {
        throwable.printStackTrace();
    }

}
