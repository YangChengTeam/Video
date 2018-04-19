package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.FollowUserList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.listener.OnFansClickListener;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


/**
 * @time 2017/5/22 22:10
 * @des $关注列表适配器
 */
public class FollowUserListAdapter extends BaseQuickAdapter<FollowUserList.DataBean.ListBean,BaseViewHolder> {

    private final int objectType;
    private final OnFansClickListener onFansClickListener;


    public FollowUserListAdapter(List<FollowUserList.DataBean.ListBean> listBeanList, int objectType, OnFansClickListener onFansClickListener) {
        super(R.layout.follow_user_list_item,listBeanList);
        this.objectType=objectType;
        this.onFansClickListener=onFansClickListener;
    }



    @Override
    protected void convert(final BaseViewHolder helper, final FollowUserList.DataBean.ListBean item) {
        try {
            if(null!=item){
                RelativeLayout btnReFollow = (RelativeLayout) helper.getView(R.id.btn_re_follow);
                LinearLayout llBtnMenu = (LinearLayout) helper.getView(R.id.ll_btn_menu);
                //自己的关注列表和已关注
                if(1==objectType&&1==item.getIs_follow()){
                    btnReFollow.setVisibility(View.GONE);
                    llBtnMenu.setVisibility(View.VISIBLE);
                    //自己的关注列表和未关注
                }else if(1==objectType&&0==item.getIs_follow()){
                    llBtnMenu.setVisibility(View.GONE);
                    btnReFollow.setVisibility(View.VISIBLE);
                    //别人的关注列表
                }else{
                    llBtnMenu.setVisibility(View.GONE);
                    btnReFollow.setVisibility(View.GONE);
                }

                helper.setText(R.id.tv_item_title,item.getNickname());
//                helper.setVisible(R.id.view_line,helper.getPosition()==getData().size()-1?false:true);
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(item.getSignature())?"":item.getSignature(), "UTF-8");
                    helper.setText(R.id.tv_item_desp,decode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //用户头像
                Glide.with(mContext)
                        .load(Utils.imageUrlChange(item.getLogo()))
                        .error(R.drawable.iv_mine)
                        .placeholder(R.drawable.iv_mine)
                        .crossFade()//渐变
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(mContext))
                        .into((ImageView) helper.getView(R.id.iv_item_icon));
                //关注按钮
                btnReFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=onFansClickListener) onFansClickListener.onFollowUser(helper.getPosition(),item);
                    }
                });
                //菜单
                helper.setOnClickListener(R.id.iv_item_menu, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=onFansClickListener)  onFansClickListener.onMenuClick(item);
                    }
                });
                //条目的点击事件
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=onFansClickListener) onFansClickListener.onItemClick(helper.getPosition());
                    }
                });
            }
        }catch (Exception e){

        }
    }
}
