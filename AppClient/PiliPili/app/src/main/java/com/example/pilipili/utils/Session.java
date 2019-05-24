package com.example.pilipili.utils;

/**
 * Used to save user session
 */
public final class Session {
    private static boolean isLogin;
    private static String userName;
    public static void setSession() {
        isLogin = false;
    }
    public static void clear() {
        isLogin = false;
        userName = "";
    }
    private Session(){
        isLogin = false;
        userName = "";
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setLogin(boolean isLogin) {
        Session.isLogin = isLogin;
    }

    public static void setUserName(String userName) {
        Session.userName = userName;
    }
}
