package com.video.newqu.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.video.newqu.R;
import com.video.newqu.bean.MusicInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.util.ImageCache;
import com.video.newqu.util.attach.LoadLocalMusicShortImgTask;
import com.video.newqu.util.Utils;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import com.xinqu.videoplayer.full.XinQuMusicPlayerStandard;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 本地音乐列表
 */

public class MediaLocationMusicAdapter extends BaseQuickAdapter<MusicInfo,BaseViewHolder>{

    private final OnMediaMusicClickListener onMediaMusicClickListener;
    private int cureenPlayingItemIndex=-1;


    public MediaLocationMusicAdapter(List<MusicInfo> data, OnMediaMusicClickListener onMediaMusicClickListener) {
        super(R.layout.re_media_location_music_item, data);
        this.onMediaMusicClickListener=onMediaMusicClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {

        helper.setText(R.id.tv_item_name,item.getTitle())
                .setText(R.id.tv_item_author,item.getSinger())
                .setText(R.id.tv_item_drutaion, Utils.formatDurationForHMS(item.getDuration()));

        //是否正在播放
        helper.setVisible(R.id.re_item_make,item.isPlaying()?true:false);
        ImageView btn_play = (ImageView) helper.getView(R.id.btn_play);
        btn_play.setImageResource(item.isPlaying()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);

        XinQuMusicPlayerStandard musicPlayer = helper.getView(R.id.music_player);
        musicPlayer.setOnPlayStateListener(new XinQuMusicPlayerStandard.OnPlayStateListener() {
            @Override
            public void onError() {
                initialListItem();
            }
        });
        //封面设置
        Bitmap bitmap = ImageCache.getInstance().get(item.getFileUrl());
        if(null!=bitmap){
            musicPlayer.thumbImageView.setImageBitmap(bitmap);
        }else{
            new LoadLocalMusicShortImgTask(musicPlayer.thumbImageView,R.drawable.ic_music_empty,item.getFileUrl()).execute();
        }
        musicPlayer.setUp(item.getFileUrl(), WindowVideoPlayer.SCREEN_WINDOW_NORMAL,true,new Object[0]);
        helper.setOnClickListener(R.id.re_item_view,new OnPlayMusicClickListener(helper,item));
        helper.setOnClickListener(R.id.re_item_make,new OnItemClickListener(helper.getPosition(),item));
    }

    /**
     * 还原界面状态
     */
    public void initialListItem() {
        if(-1!=cureenPlayingItemIndex){
            List<MusicInfo> data = getData();
            if(null!=data&&data.size()>0){
                MusicInfo musicInfo = data.get(cureenPlayingItemIndex);
                if(null!=musicInfo){
                    musicInfo.setPlaying(false);
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
        private final MusicInfo data;

        public OnPlayMusicClickListener(BaseViewHolder helper, MusicInfo item) {
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
            int position = itemView.getAdapterPosition();
            List<MusicInfo> recoredMusicInfos = getData();
            if(null!=recoredMusicInfos&&recoredMusicInfos.size()>0){
                WindowVideoPlayer.loop=true;//到这里总是开启自动循环播放
                XinQuMusicPlayerStandard musicPlayer = itemView.getView(R.id.music_player);
                //处理刚才选中的项
                if(-1!=cureenPlayingItemIndex){
                    //重复在点击
                    if(cureenPlayingItemIndex==position){
                        //处理新的项
                        if(null!=data){
                            data.setPlaying(!data.isPlaying());//取反
                            itemView.setVisible(R.id.re_item_make,data.isPlaying()?true:false);
                            ImageView btn_play = (ImageView) itemView.getView(R.id.btn_play);
                            btn_play.setImageResource(data.isPlaying()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);
                            if(data.isPlaying()){
                                if(null!=musicPlayer){
                                    musicPlayer.startVideo();
                                }
                            }else{
                                WindowVideoPlayer.releaseAllVideos();
                            }
                        }
                        return;
                    }
                    //还原刚才选中的
                    MusicInfo oldMediaRecoredMusicInfo = recoredMusicInfos.get(cureenPlayingItemIndex);
                    if(null!=oldMediaRecoredMusicInfo){
                        //过去选中的
                        WindowVideoPlayer.releaseAllVideos();
                        oldMediaRecoredMusicInfo.setPlaying(false);//还原刚才选中的
                        notifyItemChanged(cureenPlayingItemIndex);
                    }
                }
                //处理新的选中的
                if(null!=data){
                    WindowVideoPlayer.releaseAllVideos();
                    data.setPlaying(!data.isPlaying());//取反
                    itemView.setVisible(R.id.re_item_make,data.isPlaying()?true:false);
                    ImageView btn_play = (ImageView) itemView.getView(R.id.btn_play);
                    btn_play.setImageResource(data.isPlaying()?R.drawable.media_record_music_pause:R.drawable.media_record_music_play);
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

        private final MusicInfo data;

        public OnItemClickListener(int position, MusicInfo item) {
            this.data=item;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //选中音乐
                case R.id.re_item_make:
                    if(null!=onMediaMusicClickListener){
                        onMediaMusicClickListener.onSubmitLocationMusic(data);
                    }
                    break;
            }
        }
    }
}
