package com.video.newqu.adapter;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.video.newqu.R;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.util.DateUtil;
import com.video.newqu.util.ScreenUtils;
import java.io.File;
import java.util.List;

import static com.video.newqu.R.layout.item;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 * 本地视频缩略图列表
 */

public class MoivesListAdapter extends BaseQuickAdapter<WeiXinVideo, BaseViewHolder> {

    private final int mItemHeight;

    public MoivesListAdapter(List<WeiXinVideo> data) {
        super(R.layout.list_item_import_video, data);
        if (ScreenUtils.getScreenHeight() >= 1280) {
            mItemHeight = ScreenUtils.dpToPxInt(166);
        } else {
            mItemHeight = ScreenUtils.dpToPxInt(146);
        }
    }


    @Override
    protected void convert(BaseViewHolder helper, WeiXinVideo item) {

        if (null == item) return;
        RelativeLayout re_item_icon = helper.getView(R.id.re_item_icon);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) re_item_icon.getLayoutParams();
        linearParams.height = mItemHeight;
        linearParams.width=LinearLayout.LayoutParams.MATCH_PARENT;
        re_item_icon.setLayoutParams(linearParams);

        if (0 != item.getVideoDortion()) {
            helper.setText(R.id.tv_duration, DateUtil.getTimeLengthString(item.getVideoDortion() /1000));
        }

        ImageView icon = helper.getView(R.id.iv_item_icon);
        icon.setImageResource(R.drawable.iv_empty_bg_error);
        File file = new File(item.getVideoPath());

        Glide
            .with(mContext)
            .load(Uri.fromFile(file))
            .error(R.drawable.iv_video_errror)
            .animate(R.anim.item_alpha_in)
            .skipMemoryCache(true)
            .into(icon);
    }
}
