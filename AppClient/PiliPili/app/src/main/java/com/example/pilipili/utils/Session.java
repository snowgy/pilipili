package com.example.pilipili.utils;

/**
 * Used to save user session
 */
public class Session {
    public static boolean isLogin = false;
    public static int user_id = 0;
    public static void setSession() {
        isLogin = false;
    }
}
