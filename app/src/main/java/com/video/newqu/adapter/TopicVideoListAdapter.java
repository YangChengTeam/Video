package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.TopicVideoList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-05-22 21:33
 * 话题列表适配器
 */

public class TopicVideoListAdapter extends BaseQuickAdapter<TopicVideoList.DataBean.VideoListBean, BaseViewHolder> {


    private  int mItemHeight;

    public TopicVideoListAdapter(List<TopicVideoList.DataBean.VideoListBean> listsBeanList) {
        super(R.layout.re_follow_video_list_item, listsBeanList);
        int screenHeight = ScreenUtils.getScreenHeight();
        mItemHeight = ScreenUtils.dpToPxInt(250);
        if(screenHeight>1920){
            mItemHeight =ScreenUtils.dpToPxInt(286);
        }else if(screenHeight>=1280){
            mItemHeight =ScreenUtils.dpToPxInt(266);
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final TopicVideoList.DataBean.VideoListBean item) {
        FrameLayout re_item_video = helper.getView(R.id.re_item_video);
        ViewGroup.LayoutParams layoutParams =re_item_video.getLayoutParams();
        layoutParams.height=mItemHeight;
        re_item_video.setLayoutParams(layoutParams);
        try {
            if (null != item&&!TextUtils.isEmpty(item.getVideo_id())) {
                helper.setText(R.id.tv_item_follow_count, item.getCollect_times());
                if (1 == item.getIs_interest()) {
                    helper.setImageResource(R.id.iv_item_follow, R.drawable.ic_follow_red);
                    helper.setTextColor(R.id.tv_item_follow_count, CommonUtils.getColor(R.color.tips_color));
                } else {
                    helper.setImageResource(R.id.iv_item_follow, R.drawable.ic_follow_white);
                    helper.setTextColor(R.id.tv_item_follow_count, CommonUtils.getColor(R.color.white));
                }
                helper.setText(R.id.tv_item_author_name,item.getNickname());
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(item.getDesp())?"":item.getDesp(), "UTF-8");
                    ((TextView) helper.getView(R.id.tv_item_desp)).setText(decode);
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
                        .skipMemoryCache(true)//跳过内存缓存
                        .into((ImageView) helper.getView(R.id.iv_item_icon));
                ImageView authorImage = (ImageView) helper.getView(R.id.iv_item_author_icon);
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
                        .into(authorImage);
                authorImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthorDetailsActivity.start(mContext,item.getUser_id());
                    }
                });
            }
        } catch (Exception e) {

        }
    }
}
