package com.video.newqu.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.util.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-22 12:29
 * @des 首页分享弹窗适配器
 */

public class HomeShareAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ShareMenuItemInfo> homeItemInfos;
    private final int mItemHeight;
    private final LayoutInflater mInflater;

    public HomeShareAdapter(Context context, List<ShareMenuItemInfo> homeItemInfos) {
        this.mContext=context;
        mInflater = LayoutInflater.from(context);
        this.homeItemInfos=homeItemInfos;
        int screenWidth = ScreenUtils.getScreenWidth();
        mItemHeight = (screenWidth- ScreenUtils.dpToPxInt(105))/6;
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
            convertView=mInflater.inflate(R.layout.home_share_item,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_item_icon= (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHolder.ll_item_view= (LinearLayout) convertView.findViewById(R.id.ll_item_view);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.ll_item_view.getLayoutParams();
        layoutParams.height=mItemHeight;
        layoutParams.width=mItemHeight;
        viewHolder.ll_item_view.setLayoutParams(layoutParams);
        try {
            ShareMenuItemInfo homeItemInfo = homeItemInfos.get(position);
            if(null!=homeItemInfo){
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

    public List<ShareMenuItemInfo> getData() {
        return homeItemInfos;
    }

    private class ViewHolder{
        private ImageView iv_item_icon;
        private LinearLayout ll_item_view;
    }
}
