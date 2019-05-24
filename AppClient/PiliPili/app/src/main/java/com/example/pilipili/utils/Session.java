package com.example.pilipili.utils;

/**
 * Used to save user session
 */
public final class Session {
    public static boolean isLogin = false;
    public static String userName = "";
    public static void setSession() {
        isLogin = false;
    }
    public static void clear() {
        isLogin = false;
        userName = "";
    }
    private Session(){

    }
}
