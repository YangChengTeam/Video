package com.video.newqu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.util.Utils;

/**
 * YuyeTinyHung@outlook.com
 * 2017/7/16
 * 静态的监听网络变化
 */

public class NetWorkChangeReivcer extends BroadcastReceiver{

    private static final String TAG = NetWorkChangeReivcer.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        org.greenrobot.eventbus.EventBus.getDefault().post(new MessageEvent("event_home_updload_weicacht"));
    }
}
