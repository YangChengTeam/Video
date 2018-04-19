package com.video.newqu.adapter;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.VideoComentClickListener;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.TextViewSamllTopicSpan;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
/**
 * TinyHung@outlook.com
 * 2017/5/26 11:55
 * 搜索结果适配器,因为有头部，取消自动播放功能改为手动播放
 */

public class SearchVideoListAdapter extends BaseQuickAdapter<SearchResultInfo.DataBean.VideoListBean,BaseViewHolder> {

    private final VideoComentClickListener videoComentClickListener;
    private  int mItemHeight;

    public SearchVideoListAdapter(List<SearchResultInfo.DataBean.VideoListBean> video_list, VideoComentClickListener videoComentClickListener) {
        super(R.layout.re_search_video_list_item, video_list);
        this.videoComentClickListener=videoComentClickListener;
        int screenHeight = ScreenUtils.getScreenHeight();
        mItemHeight = ScreenUtils.dpToPxInt(Constant.VIDEO_ITEM_DEFAULTP);
        if(screenHeight>1920){
            mItemHeight =ScreenUtils.dpToPxInt(Constant.VIDEO_ITEM_1080P);
        }else if(screenHeight>=1280){
            mItemHeight =ScreenUtils.dpToPxInt(Constant.VIDEO_ITEM_1280P);
        }
    }


    @Override
    protected void convert(final BaseViewHolder helper, final SearchResultInfo.DataBean.VideoListBean item) {
        RelativeLayout re_item_video = helper.getView(R.id.re_item_video);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) re_item_video.getLayoutParams();
        layoutParams.height=mItemHeight;
        re_item_video.setLayoutParams(layoutParams);
        if(null!=item&&!TextUtils.isEmpty(item.getVideo_id())){
            helper.setVisible(R.id.ll_commend_view,true);
            helper.setVisible(R.id.ll_follow_view,false);
            helper.setText(R.id.tv_item_commend_count,TextUtils.isEmpty(item.getComment_times())?"0":item.getComment_times());
            helper.setText(R.id.tv_item_author_name,item.getNickname());

            TextView tv_item_desp = helper.getView(R.id.tv_item_desp);
            try {
                String decode = URLDecoder.decode(TextUtils.isEmpty(item.getDesp())?"":item.getDesp(), "UTF-8");
                //设置视频介绍，需要单独处理
                SpannableString topicStyleContent = TextViewSamllTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.white), tv_item_desp,null,null);
                tv_item_desp.setText(topicStyleContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int imageID = Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0, 5)];
            //视频封面
            Glide.with(mContext)
                    .load(item.getCover())
                    .thumbnail(0.1f)
                    .placeholder(imageID)
                    .error(imageID)
                    .crossFade()//渐变
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.iv_item_icon));
            //作者封面
            Glide.with(mContext)
                    .load(item.getLogo())
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.iv_item_author_icon));

            helper.setOnClickListener(R.id.iv_item_author_icon, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoComentClickListener.onAuthorClick(item.getUser_id());
                }
            });

            helper.setOnClickListener(R.id.re_item_video, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=videoComentClickListener){
                        videoComentClickListener.onItemClick(helper.getPosition());
                    }
                }
            });
        }
    }
}
