package com.video.newqu.listener;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;

/**
 * 避免在2.5秒内出发多次点击
 */
public abstract class PerfectClickViewListener implements OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 600;
    private long lastClickTime = 0;
    private int id = -1;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int mId = v.getId();
        if (id != mId) {
            id = mId;
            lastClickTime = currentTime;
            onClickView(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onClickView(v);
        }
    }

    protected abstract void onClickView(View view);
}
