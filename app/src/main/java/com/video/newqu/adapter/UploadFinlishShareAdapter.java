package com.video.newqu.adapter;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-22 12:29
 * @des 首页分享弹窗适配器
 */

public class UploadFinlishShareAdapter extends BaseQuickAdapter<ShareMenuItemInfo,BaseViewHolder> {

    public UploadFinlishShareAdapter(List<ShareMenuItemInfo> data) {
        super(R.layout.re_item_upload_finlish,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareMenuItemInfo item) {
        if(null!=item){
            helper.setText(R.id.tv_item_title,item.getItemName());
            Glide.with(mContext)
                    .load(item.getItemLogo())
                    .error(R.drawable.error_big)
                    .crossFade()//渐变
                    .fitCenter()
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.iv_item_icon));
        }
    }
}
