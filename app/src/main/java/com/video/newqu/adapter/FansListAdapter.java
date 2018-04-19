package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.FansInfo;
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
 * @des $我的粉丝列表
 */
public class FansListAdapter extends BaseQuickAdapter<FansInfo.DataBean.ListBean,BaseViewHolder> {

    private final int objectType;
    private final OnFansClickListener onFansClickListener;


    public interface OnFollowListener{
        void onFollowUser(int poistion, FansInfo.DataBean.ListBean data);
    }

    private OnFollowListener mOnFollowListener;

    public void setOnFollowListener(OnFollowListener onFollowListener) {
        mOnFollowListener = onFollowListener;
    }

    public FansListAdapter(List<FansInfo.DataBean.ListBean> listBeanList, int objectType, OnFansClickListener onFansClickListener) {
        super(R.layout.fans_list_item,listBeanList);
        this.objectType=objectType;
        this.onFansClickListener=onFansClickListener;
    }



    @Override
    protected void convert(final BaseViewHolder helper, final FansInfo.DataBean.ListBean item) {

        try {
            if(null!=item){
                helper.setText(R.id.tv_item_title,item.getFans_nickname())
                        .setText(R.id.tv_item_follow,1==item.getBoth_fans()?"已互粉":"关注");
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(item.getSignature())?"":item.getSignature(), "UTF-8");
                    helper.setText(R.id.tv_item_desp,decode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                helper.setImageResource(R.id.iv_item_follow,1==item.getBoth_fans()?R.drawable.iv_follow_app_style:R.drawable.ic_min_add_orgin);
//                helper.setVisible(R.id.view_line,helper.getPosition()==getData().size()-1?false:true);
                helper.setVisible(R.id.btn_re_follow,1==objectType?true:false);//0： 其他的粉丝1：我的粉丝

                //粉丝
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
                //关注按钮的点击事件
                helper.setOnClickListener(R.id.btn_re_follow, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null==mOnFollowListener){
                            onFansClickListener.onFollowFans(helper.getPosition(),item);
                        }
                    }
                });
                //条目的点击事件
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFansClickListener.onItemClick(helper.getPosition());
                    }
                });
            }
        }catch (Exception e){

        }

    }
}
