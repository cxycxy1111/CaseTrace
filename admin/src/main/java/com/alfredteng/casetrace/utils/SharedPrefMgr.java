package com.alfredteng.casetrace.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefMgr {

    public static final int INT = 1;
    public static final int LONG = 2;
    public static final int STRING = 3;

    public SharedPrefMgr() {

    }

    public static void CreateSharedPref(BaseActivity activity, String sharePrefName) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(sharePrefName,Context.MODE_PRIVATE).edit();
        editor.clear().apply();
    }

    public static String getSharedPref(Context context, String fileName, String key, int dataType) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        switch (dataType) {
            case INT:
                return String.valueOf(preferences.getInt(key,0));
            case LONG:
                return String.valueOf(preferences.getLong(key,0));
            case STRING:
                return String.valueOf(preferences.getString(key,""));
            default:return null;
        }
    }

    public static void setSharedPrefLong(Context context,String file_name,String key,long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name, Context.MODE_PRIVATE).edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public static void setSharedPrefInt(Context context,String file_name,String key,int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public static void setSharedPrefStr(Context context,String file_name,String key,String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

}
