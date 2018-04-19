package com.video.newqu.camera.adapter;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.MediaMVInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/9.
 * MV适配器
 */

public class MediaMVAdapter extends BaseQuickAdapter<MediaMVInfo,BaseViewHolder>{

    private  int mItemWidth;
    public MediaMVAdapter(List<MediaMVInfo> data) {
        super(R.layout.media_record_filter_item_layout, data);
        if(ScreenUtils.getScreenWidth()>=1280){
            mItemWidth = (ScreenUtils.dpToPxInt(82));
        }else{
            mItemWidth = (ScreenUtils.dpToPxInt(62));
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, MediaMVInfo item) {
        if(null==item) return;

        RelativeLayout relativeLayout = helper.getView(R.id.re_item_filter);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.width= mItemWidth;
        layoutParams.height=mItemWidth;
        relativeLayout.setLayoutParams(layoutParams);

        TextView tv_item_title = helper.getView(R.id.tv_item_title);
        if(0==helper.getAdapterPosition()){
            tv_item_title.setBackgroundColor(CommonUtils.getColor(R.color.media_text_bg));
            helper.setBackgroundColor(R.id.re_item_selector,CommonUtils.getColor(R.color.media_text_bg));
        }else{
            tv_item_title.setBackgroundColor(CommonUtils.getColor(R.color.media_selector_bg));
            helper.setBackgroundColor(R.id.re_item_selector,CommonUtils.getColor(R.color.media_selector_bg));
        }
        helper.setVisible(R.id.re_item_selector,item.isSelector()?true:false);
        tv_item_title.setText(item.getTitle());
        ImageView view = (ImageView) helper.getView(R.id.iv_item_filter);
        view.setImageDrawable(item.getIcon());
    }


    public void addSingerData(MediaMVInfo mediaMVInfo) {
        if (null!=getData()&&!getData().contains(mediaMVInfo)) {
            getData().add(mediaMVInfo);
            notifyDataSetChanged();
        }
    }
}
