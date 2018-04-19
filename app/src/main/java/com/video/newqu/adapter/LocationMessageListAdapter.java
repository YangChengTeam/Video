package com.video.newqu.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.MessageInfo;
import com.video.newqu.bean.NotifactionMessageInfo;
import com.video.newqu.comadapter.BaseMultiItemQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.TimeUtils;
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
 * 2017/6/22 15:11
 * 用户动态消息
 */

public class LocationMessageListAdapter extends BaseMultiItemQuickAdapter<NotifactionMessageInfo,BaseViewHolder>{

    private final com.video.newqu.listener.OnItemClickListener onItemClickListener;

    public LocationMessageListAdapter(List<NotifactionMessageInfo> data ,com.video.newqu.listener.OnItemClickListener onItemClickListener) {
        super(data);
        this.onItemClickListener=onItemClickListener;
        addItemType(NotifactionMessageInfo.ITEM_1, R.layout.list_message_item);//关注
        addItemType(NotifactionMessageInfo.ITEM_2, R.layout.list_message_item);//收藏
        addItemType(NotifactionMessageInfo.ITEM_3, R.layout.list_message_item);//留言
        addItemType(NotifactionMessageInfo.ITEM_4, R.layout.list_message_item);//二次留言
    }

    @Override
    protected void convert(BaseViewHolder helper, NotifactionMessageInfo item) {
        try {
            if(null!=item){
                switch (item.getItemType()) {
                    //关注
                    case MessageInfo.ITEM_1:
                        setItemData_1(helper,item);
                        break;
                    //收藏
                    case MessageInfo.ITEM_2:
                        setItemData_2(helper,item);
                        break;
                    //留言
                    case MessageInfo.ITEM_3:
                        setItemData_3(helper,item);
                        break;
                    //二次留言
                    case MessageInfo.ITEM_4:
                        setItemData_4(helper,item);
                        break;
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 关注
     * @param helper
     * @param item
     */
    private void setItemData_1(BaseViewHolder helper, NotifactionMessageInfo item) {
        if(null==item) return;
        helper.setVisible(R.id.re_item_icon, false);
        if(item.isRead()){
            helper.setText(R.id.tv_state,"已读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.common_h3));
        }else{
            helper.setText(R.id.tv_state,"未读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.magenta));
        }
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

        helper.setText(R.id.tv_item_username,TextUtils.isEmpty(item.getNickname())?"火星人":item.getNickname())
        .setText(R.id.tv_item_content,"关注了你");
        String addTime=item.getAdd_time()+"000";
        if(null==item.getAdd_time()){
            addTime=System.currentTimeMillis()+"";
        }
        helper.setText(R.id.tv_time, TimeUtils.getTilmNow(Long.parseLong(addTime)));

        helper.addOnLongClickListener(R.id.ll_item_bg);
        //关注点击事件
//        helper.setOnClickListener(R.id.tv_item_add,new  OnItemChildViewClickListener(helper,item,0));

        //条目点击事件
        helper.setOnClickListener(R.id.re_item,new OnItemChildViewClickListener(null,null,helper.getPosition()));
        //头像点击事件
        helper.setOnClickListener(R.id.iv_item_user_icon,new OnItemChildViewClickListener(null,item,0));
    }



    /**
     * 收藏
     * @param helper
     * @param item
     */
    private void setItemData_2(BaseViewHolder helper, NotifactionMessageInfo item) {
        if(null==item) return;
        if(item.isRead()){
            helper.setText(R.id.tv_state,"已读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.common_h2));
        }else{
            helper.setText(R.id.tv_state,"未读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.magenta));
        }

        String addTime=item.getAdd_time()+"000";
        if(null==item.getAdd_time()){
            addTime=System.currentTimeMillis()+"";
        }

        helper.setText(R.id.tv_item_username,TextUtils.isEmpty(item.getNickname())?"火星人":item.getNickname())
                .setText(R.id.tv_time, TimeUtils.getTilmNow(Long.parseLong(addTime)));


        try {
            String decode="";
            if(!TextUtils.isEmpty(item.getDesp())){
                decode = URLDecoder.decode(item.getDesp(),"UTF-8");
            }
            helper.setText(R.id.tv_item_content,"收藏了你的视频《"+decode+"》");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
        //封面
        Glide.with(mContext)
                .load(item.getCover())
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into((ImageView) helper.getView(R.id.iv_video_thbum));
        //条目长按事件
        helper.addOnLongClickListener(R.id.ll_item_bg);
        //条目点击事件
        helper.setOnClickListener(R.id.re_item,new OnItemChildViewClickListener(null,null,helper.getPosition()));
        //头像点击事件
        helper.setOnClickListener(R.id.iv_item_user_icon,new OnItemChildViewClickListener(null,item,0));
    }

    /**
     * 留言
     * @param helper
     * @param item
     */
    private void setItemData_3(BaseViewHolder helper, NotifactionMessageInfo item) {
        if(null==item) return;
        helper.setVisible(R.id.re_item_icon,false);
        if(item.isRead()){
            helper.setText(R.id.tv_state,"已读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.common_h2));
        }else{
            helper.setText(R.id.tv_state,"未读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.magenta));
        }

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
        //封面
        Glide.with(mContext)
                .load(item.getCover())
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into((ImageView) helper.getView(R.id.iv_video_thbum));

        String addTime=item.getAdd_time()+"000";
        if(null==item.getAdd_time()){
            addTime=System.currentTimeMillis()+"";
        }
        helper.setText(R.id.tv_item_username,TextUtils.isEmpty(item.getNickname())?"火星人":item.getNickname())
                .setText(R.id.tv_time, TimeUtils.getTilmNow(Long.parseLong(addTime)));

        try {
            String decode="";
            String coment="";
            if(!TextUtils.isEmpty(item.getDesp())){
                decode = URLDecoder.decode(item.getDesp(),"UTF-8");
                coment = URLDecoder.decode(item.getComment(), "UTF-8");
            }
            helper.setText(R.id.tv_item_content, Html.fromHtml("在视频《"+ Utils.subString(decode,12)+"》中"+"<font color='#F57F25'>"+"回复你："+"</font>"+(TextUtils.isEmpty(coment)?"暂无评论内容":coment)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        helper.addOnLongClickListener(R.id.ll_item_bg);
        //条目点击事件
        helper.setOnClickListener(R.id.re_item,new OnItemChildViewClickListener(null,null,helper.getPosition()));
        //头像点击事件
        helper.setOnClickListener(R.id.iv_item_user_icon,new OnItemChildViewClickListener(null,item,0));
    }

    /**
     * 二次留言
     * @param helper
     * @param item
     */
    private void setItemData_4(BaseViewHolder helper, NotifactionMessageInfo item) {
        if(null==item) return;
        if(item.isRead()){
            helper.setText(R.id.tv_state,"已读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.common_h2));
        }else{
            helper.setText(R.id.tv_state,"未读");
            helper.setTextColor(R.id.tv_state, CommonUtils.getColor(R.color.magenta));
        }

        helper.setVisible(R.id.re_item_icon,false);
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
        //封面
        Glide.with(mContext)
                .load(item.getCover())
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into((ImageView) helper.getView(R.id.iv_video_thbum));

        String addTime=item.getAdd_time()+"000";
        if(null==item.getAdd_time()){
            addTime=System.currentTimeMillis()+"";
        }

        helper.setText(R.id.tv_item_username,TextUtils.isEmpty(item.getNickname())?"火星人":item.getNickname())
                .setText(R.id.tv_time, TimeUtils.getTilmNow(Long.parseLong(addTime)));

        try {
            String decode="";
            String coment="";
            if(!TextUtils.isEmpty(item.getDesp())){
                decode = URLDecoder.decode(item.getDesp(),"UTF-8");
                coment = URLDecoder.decode(item.getComment(), "UTF-8");
            }
            helper.setText(R.id.tv_item_content, Html.fromHtml("在视频《"+ Utils.subString(decode,12)+"》中<font color='#F57F25'>"+"回复你："+"</font>"+(TextUtils.isEmpty(coment)?"暂无评论内容":coment)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        helper.addOnLongClickListener(R.id.ll_item_bg);
        //条目点击事件
        helper.setOnClickListener(R.id.re_item,new OnItemChildViewClickListener(null,null,helper.getPosition()));
        //头像点击事件
        helper.setOnClickListener(R.id.iv_item_user_icon,new OnItemChildViewClickListener(null,item,0));
    }


    /**
     * 点击事件的统一处理
     */

    private class OnItemChildViewClickListener implements View.OnClickListener {


        private final BaseViewHolder holder;
        private final NotifactionMessageInfo item;
        private final int position;

        public OnItemChildViewClickListener(BaseViewHolder holder, NotifactionMessageInfo item, int position) {
            this.holder=holder;
            this.item=item;
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //关注
                case R.id.tv_item_add:
                    onFollowUser(holder,item);
                    break;
                //条目
                case R.id.re_item:
                    onItemClickListener.OnItemClick(position);
                    break;
                //点击了头像
                case R.id.iv_item_user_icon:
                    if(null!=item){
                        startUserDetails(item.getUser_id());
                    }
                    break;
            }
        }
    }

    /**
     * 去用户详情界面
     * @param user_id
     */
    private void startUserDetails(String user_id) {
        if(TextUtils.isEmpty(user_id)) return;
        AuthorDetailsActivity.start(mContext,user_id);
    }


    /**
     * 对用户关注，为不影响界面播放，在这里进行
     * @param helper
     * @param data
     */

    private void onFollowUser(final BaseViewHolder helper, NotifactionMessageInfo data) {
        //未登录
        if(null== VideoApplication.getInstance().getUserData()){
            ToastUtils.showCenterToast("请先登录!");
            //已登录
        }else{

            if(TextUtils.equals(VideoApplication.getLoginUserID(),data.getUser_id())){
                ToastUtils.showCenterToast("自己无法关注自己");
                return;
            }
            Map<String,String> params=new HashMap<>();
            params.put("user_id",data.getUser_id());
            params.put("fans_user_id", VideoApplication.getLoginUserID());

            HttpCoreEngin.get(mContext).rxpost(NetContants.BASE_HOST + "follow", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String data) {
                    if(TextUtils.isEmpty(data)){
                        return;
                    }
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
                            RelativeLayout re_follow = helper.getView(R.id.re_follow);
                            re_follow.setVisibility(isFollow?View.GONE:View.VISIBLE);
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }else{
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
