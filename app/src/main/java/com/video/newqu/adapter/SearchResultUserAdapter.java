package com.video.newqu.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.fragment.SearchResultUserFragment;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/6/9 9:29
 * 搜索结果，用户列表
 */
public class SearchResultUserAdapter extends BaseQuickAdapter<SearchResultInfo.DataBean.UserListBean,BaseViewHolder>{


    private final com.video.newqu.listener.OnItemClickListener onItemClickListener;
    private final Activity context;

    public SearchResultUserAdapter(Activity activity, List<SearchResultInfo.DataBean.UserListBean> user_list, SearchResultUserFragment onItemClickListener) {
        super(R.layout.search_user_list_item,user_list);
        this.onItemClickListener=onItemClickListener;
        this.context=activity;
    }

    public interface onFollowUserListener{
        void onFollowUserLogin();
    }
    private onFollowUserListener mOnFollowUserListener;

    public void setOnFollowUserListener(onFollowUserListener onFollowUserListener) {
        mOnFollowUserListener = onFollowUserListener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SearchResultInfo.DataBean.UserListBean item) {
        if(null==item)return;
        try {
            helper.setText(R.id.tv_item_name,item.getNickname());
            helper.setVisible(R.id.re_follow,1==item.getIs_follow()?false:true);
            helper.setVisible(R.id.view_line,helper.getPosition()==getData().size()-1?false:true);
            //作者封面
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

            try {
                String decode = URLDecoder.decode(TextUtils.isEmpty(item.getSignature())?"":item.getSignature(), "UTF-8");
                helper.setText(R.id.tv_item_desp,decode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            helper.setOnClickListener(R.id.re_item,new OnItemChildViewClickListener(helper.getPosition(),helper,item));
            helper.setOnClickListener(R.id.re_follow,new OnItemChildViewClickListener(helper.getPosition(),helper,item));

        }catch (Exception e){

        }
    }

    /**
     * 点击事件的统一处理
     */

    private class OnItemChildViewClickListener implements View.OnClickListener {


        private final int position;
        private final BaseViewHolder helper;
        private final SearchResultInfo.DataBean.UserListBean item;

        public OnItemChildViewClickListener(int position, BaseViewHolder helper, SearchResultInfo.DataBean.UserListBean item) {
            this.position=position;
            this.helper=helper;
            this.item=item;
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //关注
                case R.id.re_follow:
                    onPrice(helper,item);
                    break;
                //条目
                case R.id.re_item:
                    onItemClickListener.OnItemClick(position);
                    break;
            }
        }
    }





    /**
     * 关注用户
     * @param helper
     * @param data
     */
    private void onPrice(final BaseViewHolder helper, SearchResultInfo.DataBean.UserListBean data) {
        //未登录
        if(null== VideoApplication.getInstance().getUserData()){
            //去登录
            if(null!=mOnFollowUserListener){
                mOnFollowUserListener.onFollowUserLogin();
            }
            return;
            //已登录
        }else{

            if(TextUtils.equals(VideoApplication.getLoginUserID(),data.getId())){
                ToastUtils.showCenterToast("自己无法关注自己");
                return;
            }
            Map<String,String> params=new HashMap<>();
            params.put("user_id",data.getId());
            params.put("fans_user_id", VideoApplication.getLoginUserID());
            HttpCoreEngin.get(mContext).rxpost(NetContants.BASE_HOST + "follow", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String data) {
                    boolean isFollow=false;
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(1==jsonObject.getInt("code")){
                            //关注成功
                            if(TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                                isFollow=true;
                            }else if(TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                                isFollow=false;
                            }
                            helper.setVisible(R.id.re_follow,isFollow?false:true);
                            ToastUtils.showFinlishToast(context,null,null,jsonObject.getString("msg"));
                            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED);
                        }else{
                            ToastUtils.showErrorToast(context,null,null,jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
