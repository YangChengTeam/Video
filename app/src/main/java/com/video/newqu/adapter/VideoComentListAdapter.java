package com.video.newqu.adapter;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.ComentList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.listener.VideoComendClickListener;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.TimeUtils;
import com.video.newqu.view.widget.GlideCircleTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @time 2017/5/22 22:10
 * @des $视频列表评论列表
 */

public class VideoComentListAdapter extends BaseQuickAdapter<ComentList.DataBean.CommentListBean,BaseViewHolder> {

    private final TopicClickListener topicClickListener;
    private final VideoComendClickListener mVideoComendClickListener;

    public VideoComentListAdapter(List<ComentList.DataBean.CommentListBean> commentListBeen, TopicClickListener topicClickListener, VideoComendClickListener videoComendClickListener) {
        super(R.layout.video_coment_list_item,commentListBeen);
        this.topicClickListener=topicClickListener;
        this.mVideoComendClickListener=videoComendClickListener;
    }


    @Override
    protected void convert(BaseViewHolder helper,  ComentList.DataBean.CommentListBean item) {
        if(null==item) return;
        try {
            helper.setVisible(R.id.iv_item_line,helper.getAdapterPosition()==getData().size()?false:true);
            String add_time = item.getAdd_time()+"000";
            helper.setText(R.id.tv_item_title,item.getNickname())
                    .setText(R.id.tv_item_time, TimeUtils.getTilmNow(Long.parseLong(add_time)));
            try {
                String decode = URLDecoder.decode(item.getComment(), "utf-8");
                String coment=null;
                //回复留言
                if(!TextUtils.isEmpty(item.getTo_nickname())&&!TextUtils.isEmpty(item.getTo_user_id())){
                    coment="回复@"+item.getTo_nickname()+" :"+decode;
                    //回复视频
                }else{
                    coment=decode;
                }
                TextView view = helper.getView(R.id.tv_item_coment);
                SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(coment, CommonUtils.getColor(R.color.app_text_style), view, topicClickListener,item.getTo_user_id());
                view.setText(topicStyleContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //作者封面
            Glide.with(mContext)
                    .load(TextUtils.isEmpty(item.getLogo())?R.drawable.iv_mine:item.getLogo())
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.iv_item_icon));
            helper.setOnClickListener(R.id.iv_item_icon, new OnItemCLickListener(item));
            helper.setOnClickListener(R.id.ll_content, new OnItemCLickListener(item));
        }catch (Exception e){

        }
    }



    /**
     * 条目点击事件
     */
    private class OnItemCLickListener implements View.OnClickListener {

        private final ComentList.DataBean.CommentListBean data;

        public OnItemCLickListener(ComentList.DataBean.CommentListBean data) {
            this.data=data;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(R.id.iv_item_icon==id){
                if(null!=mVideoComendClickListener){
                    mVideoComendClickListener.onAuthorIconClick(data.getUser_id());
                }
            }else if(R.id.ll_content==id){
                if(null!=mVideoComendClickListener){
                    mVideoComendClickListener.onAuthorItemClick(data);
                }
            }
        }
    }
}
