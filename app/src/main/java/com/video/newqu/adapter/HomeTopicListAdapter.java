package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.FindVideoListInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.Utils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/10/20.
 * 首页话题列表内部横向条目适配器
 */

public class HomeTopicListAdapter extends BaseQuickAdapter<FindVideoListInfo.DataBean.VideosBean,BaseViewHolder> {

    private int mItemHeight;
    private int mItemWidth;

    public interface OnItemClickListener{
        void onItemClick(int poistion);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public HomeTopicListAdapter(List<FindVideoListInfo.DataBean.VideosBean> data) {
        super( R.layout.re_home_topic_list_item,data);
        int screenHeight = ScreenUtils.getScreenHeight();
        mItemHeight = ScreenUtils.dpToPxInt(165);
        mItemWidth = ScreenUtils.dpToPxInt(110);
        if(screenHeight>=2500){
            mItemHeight =ScreenUtils.dpToPxInt(200);
            mItemWidth =ScreenUtils.dpToPxInt(130);
        }else if(screenHeight>=1280){
            mItemHeight =ScreenUtils.dpToPxInt(156);
            mItemWidth =ScreenUtils.dpToPxInt(110);
        }else{
            mItemHeight =ScreenUtils.dpToPxInt(146);
            mItemWidth =ScreenUtils.dpToPxInt(110);
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, FindVideoListInfo.DataBean.VideosBean item) {

        RelativeLayout view = helper.getView(R.id.re_item_view);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height=mItemHeight;
        layoutParams.width=mItemWidth;
        view.setLayoutParams(layoutParams);
        if(null!=item){
            helper.setText(R.id.tv_item_play_count, Utils.formatW(Integer.parseInt(TextUtils.isEmpty(item.getPlay_times())?"0":item.getPlay_times())));
            //视频封面
            int imageID = Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0, 5)];
            Glide.with(mContext)
                    .load(item.getCover())
                    .error(imageID)
                    .placeholder(imageID)
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.iv_item_icon));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null!=mOnItemClickListener){
                        mOnItemClickListener.onItemClick(helper.getPosition());
                    }
                }
            });
        }
    }
}
