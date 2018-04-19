package com.video.newqu.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.ShareMenuItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-22 12:29
 * @des 分享弹窗适配器
 */

public class ShareAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ShareMenuItemInfo> homeItemInfos;
    private final LayoutInflater mInflater;

    public ShareAdapter(Context context, List<ShareMenuItemInfo> homeItemInfos) {
        this.mContext=context;
        this.homeItemInfos=homeItemInfos;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return homeItemInfos==null?0:homeItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return homeItemInfos==null?null:homeItemInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView=mInflater.inflate(R.layout.share_item,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_item_icon= (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHolder.tv_item_title= (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        try {
            ShareMenuItemInfo homeItemInfo = homeItemInfos.get(position);
            if(null!=homeItemInfo){
                viewHolder.tv_item_title.setText(homeItemInfo.getItemName());

                Glide.with(mContext)
                        .load(homeItemInfo.getItemLogo())
                        .error(R.drawable.error_big)
                        .crossFade()//渐变
                        .fitCenter()
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .into(viewHolder.iv_item_icon);
            }
        }catch (Exception e){

        }
        return convertView;
    }

    public void setNewData(ArrayList<ShareMenuItemInfo> homeItemInfos) {
        this.homeItemInfos=homeItemInfos;
    }

    private class ViewHolder{
        private ImageView iv_item_icon;
        private TextView tv_item_title;
    }
}
