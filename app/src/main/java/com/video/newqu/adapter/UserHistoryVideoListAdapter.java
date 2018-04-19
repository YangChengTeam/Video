package com.video.newqu.adapter;

import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;
import com.video.newqu.view.widget.GlideRoundTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-05-22 21:33
 * 用户观看视频记录
 */

public class UserHistoryVideoListAdapter extends BaseQuickAdapter<UserPlayerVideoHistoryList, BaseViewHolder> {

    private  int mItemHeight;

    public UserHistoryVideoListAdapter(List<UserPlayerVideoHistoryList> listsBeanList) {
        super(R.layout.re_user_plsyer_video_history_list_item, listsBeanList);
        int screenHeight = ScreenUtils.getScreenHeight();
        mItemHeight = ScreenUtils.dpToPxInt(166);
        if(screenHeight>=1280){
            mItemHeight =ScreenUtils.dpToPxInt(170);
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final UserPlayerVideoHistoryList item) {
        try {
            RelativeLayout re_item_video = helper.getView(R.id.re_item_video);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) re_item_video.getLayoutParams();
            layoutParams.height=mItemHeight;
            re_item_video.setLayoutParams(layoutParams);

            if(null!=item){
                helper.setText(R.id.tv_item_author_name, TextUtils.isEmpty(item.getUserName())?"火星人":item.getUserName())
                        .setText(R.id.tv_item_commend_count,TextUtils.isEmpty(item.getVideoCommendCount())?"0":item.getVideoCommendCount());

                TextView tv_item_desp = helper.getView(R.id.tv_item_desp);
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(item.getVideoDesp())?"":item.getVideoDesp(), "UTF-8");
                    //设置视频介绍，需要单独处理
                    SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.app_style_text_color), tv_item_desp,null,null);
                    tv_item_desp.setText(topicStyleContent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //视频封面
                Glide.with(mContext)
                        .load(item.getVideoCover())
                        .thumbnail(0.1f)
                        .placeholder(Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0,5)])
                        .error(Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0,5)])
                        .crossFade()//渐变
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideRoundTransform(mContext))
                        .into((ImageView) helper.getView(R.id.iv_item_icon));

                //作者封面
                Glide.with(mContext)
                        .load(item.getUserCover())
                        .error(R.drawable.iv_mine)
                        .placeholder(R.drawable.iv_mine)
                        .crossFade()//渐变
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(mContext))
                        .into((ImageView) helper.getView(R.id.iv_item_author_icon));

            }
        }catch (Exception e){

        }
    }
}
