package com.video.newqu.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.view.widget.GlideCircleTransform;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-07 19:55
 * 搜索结果适配器
 * 横向的用户列表适配器
 */


public class SearchResultHorUserListAdapter extends BaseQuickAdapter<SearchResultInfo.DataBean.UserListBean,BaseViewHolder>{

    public SearchResultHorUserListAdapter(List<SearchResultInfo.DataBean.UserListBean> data) {
        super(R.layout.search_result_user_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResultInfo.DataBean.UserListBean item) {
        if(null==item) return;
        try {
            helper.setText(R.id.tv_item_user_name,item.getNickname());
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
                    .into((ImageView) helper.getView(R.id.iv_item_user_icon));
        }catch (Exception e){

        }
    }
}
