package com.example.pilipili.utils;

import android.content.Context;

/**
 * Android > 23 : Handle uri safety
 */
public final class ProviderUtil {
    private ProviderUtil(){

    }
    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
