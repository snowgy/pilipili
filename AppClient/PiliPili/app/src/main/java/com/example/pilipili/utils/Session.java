package com.example.pilipili.utils;

/**
 * Used to save user session
 */
public class Session {
    public static boolean isLogin = false;
    public static String userName = "";
    public static void setSession() {
        isLogin = false;
    }
}
