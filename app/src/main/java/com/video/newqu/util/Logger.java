package com.video.newqu.util;

import android.util.Log;

import com.video.newqu.bean.Video;
import com.video.newqu.contants.ConfigSet;

/**
 * TinyHung@outlook.com
 * 2017/6/19 9:28
 */

public class Logger {

    public static void pd(String TAG, String message){
        if(ConfigSet.IS_DEBUG){
            Log.println(Log.DEBUG,TAG,message);
        }
    }

    public static void pe(String TAG, String message){
        if(ConfigSet.IS_DEBUG){
            Log.println(Log.ERROR,TAG,message);
        }
    }

    public static void pw(String TAG, String message){
        if(ConfigSet.IS_DEBUG){
            Log.println(Log.WARN,TAG,message);
        }
    }

    public static void pi(String TAG, String message){
        if(ConfigSet.IS_DEBUG){
            Log.println(Log.INFO,TAG,message);
        }
    }
    public static void d(String TAG, String message) {
        if(ConfigSet.IS_DEBUG){
            Log.d(TAG,message);
        }
    }

    public static void e(String TAG, String message) {
        if(ConfigSet.IS_DEBUG){
            Log.e(TAG,message);
        }
    }

    public static void v(String TAG, String message) {
        if(ConfigSet.IS_DEBUG){
            Log.e(TAG,message);
        }
    }

    public static void w(String TAG, String message) {
        if(ConfigSet.IS_DEBUG){
            Log.w(TAG,message);
        }
    }

    public static void i(String TAG, String message) {
        if(ConfigSet.IS_DEBUG){
            Log.i(TAG,message);
        }
    }
}
