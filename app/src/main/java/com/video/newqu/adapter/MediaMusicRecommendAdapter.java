package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danikula.videocache.HttpProxyCacheServer;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.util.DateUtil;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import com.xinqu.videoplayer.full.XinQuMusicPlayerStandard;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 音乐选择-推荐列表
 */

public class MediaMusicRecommendAdapter extends BaseQuickAdapter<MediaMusicCategoryList.DataBean,BaseViewHolder> {

    private final OnMediaMusicClickListener onMediaMusicClickListener;
    private int cureenPlayingItemIndex=-1;

    public MediaMusicRecommendAdapter(List<MediaMusicCategoryList.DataBean> data, OnMediaMusicClickListener onMediaMusicClickListener) {
        super(R.layout.re_media_music_list_item, data);
        this.onMediaMusicClickListener=onMediaMusicClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MediaMusicCategoryList.DataBean item) {

        String seconds = item.getSeconds();
        if(TextUtils.isEmpty(seconds)){
            seconds="1.0";
        }
        float second = Float.parseFloat(seconds);
        helper.setText(R.id.tv_item_name,item.getTitle())
                .setText(R.id.tv_item_author,item.getAuthor())
                .setText(R.id.tv_item_drutaion, DateUtil.getTimeLengthString((int) (second)));
        //是否正在播放
        helper.setVisible(R.id.re_item_make,item.isPlayerIng()?true:false);
        helper.setVisible(R.id.iv_item_like,item.isPlayerIng()?false:true);
        ImageView btn_play = (ImageView) helper.getView(R.id.btn_play);
        btn_play.setImageResource(item.isPlayerIng()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);
        helper.setImageResource(R.id.iv_item_like,1==item.getIs_collect()?R.drawable.btn_nav_like_selector_red:R.drawable.btn_nav_like_selector_gray);
        XinQuMusicPlayerStandard musicPlayer = helper.getView(R.id.music_player);
        musicPlayer.setOnPlayStateListener(new XinQuMusicPlayerStandard.OnPlayStateListener() {
            @Override
            public void onError() {
                initialListItem();
            }
        });
        //封面
        Glide.with(mContext)
                .load(item.getCover())
                .crossFade()//渐变
                .thumbnail(0.1f)
                .error(R.drawable.ic_music_empty)
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into(musicPlayer.thumbImageView);

        HttpProxyCacheServer proxy = VideoApplication.getProxy();
        String proxyUrl = proxy.getProxyUrl(item.getUrl());
        musicPlayer.setUp(proxyUrl, WindowVideoPlayer.SCREEN_WINDOW_NORMAL,true,new Object[0]);
        helper.setOnClickListener(R.id.re_item_view,new OnPlayMusicClickListener(helper,item));
        helper.setOnClickListener(R.id.iv_item_like,new OnItemClickListener(helper.getPosition(),item));
        helper.setOnClickListener(R.id.re_item_make,new OnItemClickListener(helper.getPosition(),item));
    }

    /**
     * 还原界面状态
     */
    public void initialListItem() {
        if(-1!=cureenPlayingItemIndex){
            List<MediaMusicCategoryList.DataBean> data = getData();
            if(null!=data&&data.size()>0){
                MediaMusicCategoryList.DataBean mediaRecoredMusicInfo = data.get(cureenPlayingItemIndex);
                if(null!=mediaRecoredMusicInfo){
                    mediaRecoredMusicInfo.setPlayerIng(false);
                    notifyItemChanged(cureenPlayingItemIndex);
                }
            }
        }
    }


    /**
     * 播放音乐的点击事件
     */

    private class OnPlayMusicClickListener implements View.OnClickListener{

        private final BaseViewHolder itemView;
        private final MediaMusicCategoryList.DataBean data;

        public OnPlayMusicClickListener(BaseViewHolder helper, MediaMusicCategoryList.DataBean item) {
            this.itemView=helper;
            this.data=item;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //条目单击事件
                case R.id.re_item_view:
                    checkedPlayer();
                    break;
            }
        }

        private void checkedPlayer() {
            if(!Utils.isCheckNetwork()){
                ToastUtils.showCenterToast("无可用网络!");
                return;
            }
            int position = itemView.getAdapterPosition();
            List<MediaMusicCategoryList.DataBean> recoredMusicInfos = getData();
            if(null!=recoredMusicInfos&&recoredMusicInfos.size()>0){
                WindowVideoPlayer.loop=true;//到这里总是开启自动循环播放
                XinQuMusicPlayerStandard musicPlayer = itemView.getView(R.id.music_player);
                //处理刚才选中的项
                if(-1!=cureenPlayingItemIndex){
                    //重复在点击
                    if(cureenPlayingItemIndex==position){
                        if(null!=data){
                            data.setPlayerIng(!data.isPlayerIng());//取反
                            itemView.setVisible(R.id.re_item_make,data.isPlayerIng()?true:false);
                            itemView.setVisible(R.id.iv_item_like,data.isPlayerIng()?false:true);
                            ImageView btn_play = (ImageView) itemView.getView(R.id.btn_play);
                            btn_play.setImageResource(data.isPlayerIng()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);
                            if(data.isPlayerIng()){
                                if(null!=musicPlayer){
                                    musicPlayer.startVideo();
                                }
                            }else{
                                WindowVideoPlayer.releaseAllVideos();
                            }
                        }
                        return;
                    }
                    //处理旧的选中的
                    MediaMusicCategoryList.DataBean oldMediaRecoredMusicInfo = recoredMusicInfos.get(cureenPlayingItemIndex);
                    if(null!=oldMediaRecoredMusicInfo){
                        //过去选中的
                        if(-1!=cureenPlayingItemIndex){
                            WindowVideoPlayer.releaseAllVideos();
                            oldMediaRecoredMusicInfo.setPlayerIng(false);//还原刚才选中的
                            notifyItemChanged(cureenPlayingItemIndex);
                        }
                    }
                }
                //处理新的选中的
                if(null!=data){
                    WindowVideoPlayer.releaseAllVideos();
                    data.setPlayerIng(!data.isPlayerIng());//取反
                    itemView.setVisible(R.id.re_item_make,data.isPlayerIng()?true:false);
                    itemView.setVisible(R.id.iv_item_like,data.isPlayerIng()?false:true);
                    ImageView btn_play = (ImageView) itemView.getView(R.id.btn_play);
                    btn_play.setImageResource(data.isPlayerIng()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);
                    if(null!=musicPlayer){
                        musicPlayer.startVideo();
                    }
                }
                cureenPlayingItemIndex=position;
            }
        }
    }

    /**
     * 一般性点击事件
     */

    private class OnItemClickListener implements View.OnClickListener{

        private final int position;
        private final MediaMusicCategoryList.DataBean data;

        public OnItemClickListener(int position, MediaMusicCategoryList.DataBean item) {
            this.position=position;
            this.data=item;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //收藏歌曲事件
                case R.id.iv_item_like:
                    if(null!=onMediaMusicClickListener){
                        onMediaMusicClickListener.onLikeClick(data);
                    }
                    break;

                //查看更多相关的事件
//                case R.id.iv_item_details:
//                    if(null!=onMediaMusicClickListener){
//                        onMediaMusicClickListener.onDetailsClick(data.getId());
//                    }
//                    break;
                //选中音乐
                case R.id.re_item_make:
                    if(null!=onMediaMusicClickListener){
                        onMediaMusicClickListener.onSubmitMusic(data);
                    }
                    break;
            }
        }
    }
}
