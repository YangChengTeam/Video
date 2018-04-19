package com.video.newqu.listener;

import com.video.newqu.bean.CaptionsInfo;

/**
 * TinyHung@Outlook.com
 * 2017/9/12.
 */

public interface OnMediaCaptionsListener {

    void onCapsionsItemClick(String path, CaptionsInfo.DataBean dataBean);
    void onRemoveAllCaptions();
    void onEmptyCaptions();
}
