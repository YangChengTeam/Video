package com.video.newqu.camera.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.video.newqu.R;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.view.widget.CircleProgressView;

/**
 * TinyHung@Outlook.com
 * 2017/11/11.
 */

public class StickerViewHolder extends BaseViewHolder {

    public RelativeLayout re_item_sticker;
    public RelativeLayout re_progress;
    public ImageView iv_item_sticker;
    public ImageView iv_download_icon;
    public CircleProgressView circle_progressbar;
    public LinearLayout ll_header;

    public StickerViewHolder(View view) {
        super(view);
        re_item_sticker= (RelativeLayout) view.findViewById(R.id.re_item_sticker);
        re_progress= (RelativeLayout) view.findViewById(R.id.re_progress);
        iv_item_sticker= (ImageView) view.findViewById(R.id.iv_item_sticker);
        iv_download_icon= (ImageView) view.findViewById(R.id.iv_download_icon);
        circle_progressbar= (CircleProgressView) view.findViewById(R.id.circle_progressbar);
        ll_header= (LinearLayout) view.findViewById(R.id.ll_header);
    }
}
