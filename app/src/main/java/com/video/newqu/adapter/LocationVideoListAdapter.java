package com.video.newqu.adapter;

import android.net.Uri;
import android.view.View;
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

/**
 * @time 2017/5/26 20:01
 * @des 本地视频列表适配器
 */

public class LocationVideoListAdapter extends BaseQuickAdapter<WeiXinVideo,com.video.newqu.comadapter.BaseViewHolder> {
    
    private final com.video.newqu.listener.OnItemClickListener onItemClickListener;
    private final int mItemHeight;

    public LocationVideoListAdapter(List<WeiXinVideo> data, com.video.newqu.listener.OnItemClickListener onItemClickListener) {
        super(R.layout.location_video_item, data);
        this.onItemClickListener=onItemClickListener;
        if (ScreenUtils.getScreenHeight() >= 1280) {
            mItemHeight = ScreenUtils.dpToPxInt(150);
        } else {
            mItemHeight = ScreenUtils.dpToPxInt(130);
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final WeiXinVideo item) {
        RelativeLayout re_item_icon = helper.getView(R.id.re_item_icon);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) re_item_icon.getLayoutParams();
        linearParams.height = mItemHeight;
        linearParams.width= LinearLayout.LayoutParams.MATCH_PARENT;
        re_item_icon.setLayoutParams(linearParams);
        try {
            if(null!=item){
                File file=new File(item.getVideoPath());
                Glide
                    .with(mContext)
                    .load(Uri.fromFile(file))
                    .error(R.drawable.iv_video_errror)
                    .animate(R.anim.item_alpha_in)
                    .skipMemoryCache(true)
                    .into((ImageView) helper.getView(R.id.iv_item_icon));

                helper.setText(R.id.tv_item_duration,DateUtil.getTimeLengthString(item.getVideoDortion()/1000));
                helper.setImageResource(R.id.iv_is_selector,item.getIsSelector()?R.drawable.iv_video_selector_true:R.drawable.iv_video_selector);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setIsSelector(!item.getIsSelector());
                        helper.setImageResource(R.id.iv_is_selector,item.getIsSelector()?R.drawable.iv_video_selector_true:R.drawable.iv_video_selector);
                        onItemClickListener.OnItemClick(helper.getAdapterPosition());
                    }
                });
            }
        }catch (Exception e){

        }
    }
}
