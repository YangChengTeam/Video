package com.video.newqu.adapter;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.MediaMusicHomeMenu;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/3
 * 音乐选择列表菜单
 */

public class MediaMusicMenuAdapter extends BaseQuickAdapter<MediaMusicHomeMenu.DataBean,BaseViewHolder>{

    public MediaMusicMenuAdapter(List<MediaMusicHomeMenu.DataBean> data) {
        super(R.layout.lv_media_music_menu_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MediaMusicHomeMenu.DataBean item) {
        if(null!=item) {
            if(0==item.getItemType()){
                //视频封面
                Glide.with(mContext)
                        .load(item.getCover())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(false)//跳过内存缓存
                        .into((ImageView) helper.getView(R.id.iv_item_icon));
            }else if(1==item.getItemType()){
                //视频封面
                Glide.with(mContext)
                        .load(item.getIconID())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(false)//跳过内存缓存
                        .into((ImageView) helper.getView(R.id.iv_item_icon));
            }
            helper.setText(R.id.tv_item_title, item.getName());
        }
    }
}
