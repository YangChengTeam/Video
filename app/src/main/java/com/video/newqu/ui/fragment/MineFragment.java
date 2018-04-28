package com.video.newqu.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import com.androidkun.xtablayout.XTabLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.XinQuFragmentPagerAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.MineTabInfo;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.NotifactionMessageInfo;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentMineBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.activity.ClipImageActivity;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.activity.MediaPictruePhotoActivity;
import com.video.newqu.ui.contract.UserMineContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.presenter.UserInfoPresenter;
import com.video.newqu.util.AndroidNFileUtils;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.GlideCircleTransform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import me.leolin.shortcutbadger.ShortcutBadger;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/22 16:02
 * 首页-用户中心
 */
public class MineFragment extends BaseFragment<FragmentMineBinding,UserInfoPresenter> implements UserMineContract.View, Observer {

    private  List<MineTabInfo> mMineTabInfos=null;
    private  ArrayList<Fragment> mFragmentList;
    private  MineUserInfo.DataBean.InfoBean mUserInfo;
    private AnimationDrawable mAnimationDrawable;
    private final static int PERMISSION_REQUEST_CAMERA = 1;//摄像
    private boolean isUpdata=true;//默认是否需要刷新
    private boolean isRefreshChild;//书否主动刷新子Fragment
    private int mReTopBarHeight;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new UserInfoPresenter(getActivity());
        mPresenter.attachView(this);
        mUserInfo= (MineUserInfo.DataBean.InfoBean)ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MINE_USER_DATA);
        ApplicationManager.getInstance().addObserver(this);
        //菜单的元素
        mMineTabInfos=new ArrayList<>();
        mMineTabInfos.add(new MineTabInfo(getResources().getString(R.string.mine_fragment_works_title),0, true));
        mMineTabInfos.add(new MineTabInfo(getResources().getString(R.string.mine_fragment_like_title),0, false));
        mMineTabInfos.add(new MineTabInfo(getResources().getString(R.string.mine_fragment_message_title),null==mUserInfo?0:mUserInfo.getMsgCount(), false));
        initTabAdapter();
        initUserData();
    }

    @Override
    protected void initViews() {
        int minHeight=0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            minHeight= SystemUtils.getStatusBarHeight(getActivity());
            if(minHeight<=0){
                minHeight=ScreenUtils.dpToPxInt(25);
            }
        }
        bindingView.collapseToolbar.setMinimumHeight(minHeight);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bindingView.collapseToolbar.measure(width,width);
        //滚动高度的阈值应该是总高度-toolbar-statusHeight
        mReTopBarHeight = bindingView.collapseToolbar.getMeasuredHeight()-bindingView.collapseToolbar.getMinimumHeight();
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int abs = Math.abs(verticalOffset);
                float scale = (float) abs /( (int) (mReTopBarHeight / 1.2));
                float alpha =(scale * 255);
                //用户信息图层网上滑动，所以是相反的透明度
                float barViewAlpha = Utils.absVakue(255f, alpha)/255f;
                //用户头像，登录View,用户资料View
                bindingView.reUserDataView.setAlpha(barViewAlpha);//采坑，这里的透明度必须是1。0-0.0之间的float类型
                bindingView.tvUserDesp.setAlpha(barViewAlpha);
            }
        });
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //设置
                    case R.id.btn_setting:
                        startTargetActivity(Constant.KEY_FRAGMENT_TYPE_SETTINGS,"设置中心",null,0);
                        break;
                    //我的粉丝
                    case R.id.tv_fans_count:
                        if(null==mUserInfo){
                            ToastUtils.showCenterToast("用户信息过期，请重新登录!");
                            return;
                        }
                        startTargetActivity(Constant.KEY_FRAGMENT_TYPE_FANS_LIST,"我的粉丝",VideoApplication.getLoginUserID(),1);
                        break;
                    //我的关注
                    case R.id.tv_follow_count:
                        if(null==mUserInfo){
                            ToastUtils.showCenterToast("用户信息过期，请重新登录!");
                            return;
                        }
                        startTargetActivity(Constant.KEY_FRAGMENT_TYPE_FOLLOW_USER_LIST,"我关注的用户",VideoApplication.getLoginUserID(),1);
                        break;
                    //拍照/选择本地照片
                    case R.id.re_user_bg_cover:
                        if(null==mUserInfo){
                            ToastUtils.showCenterToast("用户信息过期，请重新登录!");
                            return;
                        }
                        showPictureSelectorPop();
                        break;
                    //分享自己的主页
                    case R.id.btn_share:
                        if(null==mUserInfo){
                            ToastUtils.showCenterToast("用户信息过期，请重新登录!");
                            return;
                        }
                        if(null!=VideoApplication.getInstance().getUserData()){
                            try {
                                ShareInfo shareInfo=new ShareInfo();
                                shareInfo.setDesp("我在新趣小视频安家啦！这是我的主页，快来围观我吧！");
                                shareInfo.setTitle(VideoApplication.getInstance().getUserData().getNickname()+"@你，快来加入新趣，我在新趣等你！");
                                shareInfo.setUserID(VideoApplication.getInstance().getUserData().getId());
                                shareInfo.setUrl("http://app.nq6.com/home/user/index?user_id="+shareInfo.getUserID());
                                shareInfo.setImageLogo(VideoApplication.getInstance().getUserData().getLogo());
                                shareInfo.setShareTitle("分享用户主页至");
                                MainActivity activity = (MainActivity) getActivity();
                                if(null!=activity&&!activity.isFinishing()){
                                    activity.shareMineHome(shareInfo);
                                }
                            }catch (Exception e){

                            }
                        }
                        break;
                    //通知消息
                    case R.id.btn_notifaction:
                        MobclickAgent.onEvent(getActivity(), "click_notifaction_msg");
                        startTargetActivity(Constant.KEY_FRAGMENT_NOTIFACTION,"通知消息",null,0);
                        break;
                }
            }
        };
        bindingView.btnSetting.setOnClickListener(onClickListener);
        bindingView.reUserDataView.setOnClickListener(onClickListener);
        bindingView.reUserBgCover.setOnClickListener(onClickListener);
        bindingView.tvFansCount.setOnClickListener(onClickListener);
        bindingView.tvFollowCount.setOnClickListener(onClickListener);
        bindingView.btnShare.setOnClickListener(onClickListener);
        bindingView.btnNotifaction.setOnClickListener(onClickListener);

        PerfectClickListener clickListener = new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (null == mUserInfo) {
                    ToastUtils.showCenterToast("用户信息过期，请重新登录!");
                    return;
                }
                CompleteUserDataDialogFragment fragment = CompleteUserDataDialogFragment.newInstance(mUserInfo, "修改个人信息", Constant.MODE_USER_EDIT);
                fragment.setOnDismissListener(new CompleteUserDataDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss(boolean change) {
                        if (change) {
                            isRefreshChild = false;
                            getUserData();
                        }
                    }
                });
                fragment.show(getChildFragmentManager(), "edit");
            }
        };
        bindingView.ivUserIcon.setOnClickListener(clickListener);//用户头像
        bindingView.reUserDataView.setOnClickListener(clickListener);//用户资料

    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isUpdata&&null!=bindingView&&null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            showFreshLodingView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkedMsgCount();
    }

    /**
     * 获取用户基本信息
     */
    private void getUserData() {
        if(null!= mPresenter &&!mPresenter.isLoading()){
            mPresenter.getUserInfo(VideoApplication.getLoginUserID());
        }
    }

    /**
     * 初始化所有子界面
     */
    private void initTabAdapter() {
        //初始化子界面
        if(null==mFragmentList) mFragmentList=new ArrayList<>();
        mFragmentList.add(new HomeWorksFragment());
        mFragmentList.add(new HomeLikeVideoFragment());
        mFragmentList.add(new HomeMessageFragment());
        bindingView.viewPager.setOffscreenPageLimit(3);
        bindingView.viewPager.setAdapter(new XinQuFragmentPagerAdapter(getChildFragmentManager(), mFragmentList));
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(0);
        for (int i = 0; i < bindingView.tabLayout.getTabCount(); i++) {
            XTabLayout.Tab tab = bindingView.tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        bindingView.tabLayout.getTabAt(0).getCustomView().setSelected(true);
    }


    //充气自定义View
    private View getTabView(int index) {
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            MineTabInfo mineTabInfo = mMineTabInfos.get(index);
            View inflate = View.inflate(getActivity(), R.layout.list_mine_tab_item, null);
            TextView tvItemTitle = (TextView) inflate.findViewById(R.id.tv_item_title);
            tvItemTitle.setText(mineTabInfo.getAboutCount()+" "+mineTabInfo.getTitleName());
            return inflate;
        }
        return null;
    }

    /**
     * 刷新标题栏适配器
     */
    private void updataTabAdapter() {
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            for (int i = 0; i < bindingView.tabLayout.getTabCount(); i++) {
                MineTabInfo mineTabInfo = mMineTabInfos.get(i);
                XTabLayout.Tab tabAt = bindingView.tabLayout.getTabAt(i);
                TextView tvItemTitle = (TextView) tabAt.getCustomView().findViewById(R.id.tv_item_title);
                tvItemTitle.setText(mineTabInfo.getAboutCount()+" "+mineTabInfo.getTitleName());
            }
        }
    }

    /**
     * 初始化用户信息
     */
    private void initUserData() {
        if(null==mUserInfo) return;
        if(null!=bindingView){
            //已登录，刷新
            if(null!=VideoApplication.getInstance().getUserData()&&null!=mUserInfo){
                bindingView.tvUserName.setText(TextUtils.isEmpty(mUserInfo.getNickname())?"火星人":mUserInfo.getNickname());
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(mUserInfo.getSignature())?"本宝宝暂时没有个性签名":mUserInfo.getSignature(), "UTF-8");
                    bindingView.tvUserDesp.setText(decode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                bindingView.tvUserGrade.setText("Lv"+(TextUtils.isEmpty(mUserInfo.getRank())?"1":mUserInfo.getRank()));
                bindingView.tvFansCount.setText(TextUtils.isEmpty(mUserInfo.getFans())?"0粉丝":mUserInfo.getFans()+"粉丝");
                bindingView.tvFollowCount.setText(TextUtils.isEmpty(mUserInfo.getFollows())?"0关注":mUserInfo.getFollows()+"关注");
                bindingView.ivUserSex.setImageResource(TextUtils.isEmpty(mUserInfo.getGender())?R.drawable.ic_sex_not_know:TextUtils.equals("女",mUserInfo.getGender())?R.drawable.iv_icon_sex_women:TextUtils.equals("男",mUserInfo.getGender())?R.drawable.iv_icon_sex_man:R.drawable.ic_sex_not_know);
                //用户头像
                Glide.with(this)
                        .load(TextUtils.isEmpty(mUserInfo.getLogo())?R.drawable.iv_mine:mUserInfo.getLogo())
                        .error(R.drawable.iv_mine)
                        .placeholder(R.drawable.iv_mine)
                        .crossFade()//渐变
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(getActivity()))
                        .into(bindingView.ivUserIcon);
                //设置头部背景封面
                Glide.with(this)
                        .load(TextUtils.isEmpty(mUserInfo.getImage_bg())?R.drawable.iv_mine_bg:mUserInfo.getImage_bg())
                        .thumbnail(0.1f)
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .skipMemoryCache(true)//跳过内存缓存
                        .into(bindingView.ivUserImageBg);

                if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
                    mMineTabInfos.get(0).setAboutCount(Integer.parseInt(TextUtils.isEmpty(mUserInfo.getVideo_count())?"0":mUserInfo.getVideo_count()));
                    mMineTabInfos.get(1).setAboutCount(Integer.parseInt(TextUtils.isEmpty(mUserInfo.getCollect_times())?"0":mUserInfo.getCollect_times()));
                    mMineTabInfos.get(2).setAboutCount(mUserInfo.getMsgCount());
                }
                updataTabAdapter();//刷新标题栏
                //未登录，还原
            }else{
                canelUserData();
            }
        }
    }

    /**
     * 清空用户所有信息
     */
    private void canelUserData() {
        if(null==bindingView) return;
        bindingView.ivUserIcon.setImageResource(R.drawable.iv_mine);
        bindingView.ivUserImageBg.setImageResource(R.drawable.iv_mine_bg);
        bindingView.ivUserSex.setImageResource(R.drawable.iv_icon_sex_women);
        bindingView.tvFansCount.setText(0+"粉丝");
        bindingView.tvFollowCount.setText(0+"关注");
        bindingView.tvUserName.setText("--");
        bindingView.tvUserDesp.setText("--");
        bindingView.tvUserGrade.setText("Lv"+0);
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            mMineTabInfos.get(0).setAboutCount(0);
            mMineTabInfos.get(1).setAboutCount(0);
        }
        updataTabAdapter();//刷新标题栏
        mUserInfo=null;//个人信息信息的所有数据
    }

    /**
     * 主动刷新子Fragment
     * @param poistion 要刷新的界面角标
     */
    private void updataChildViewToPoistion(int poistion) {
        if(null!=mFragmentList&&mFragmentList.size()>0){
            Fragment fragment = mFragmentList.get(poistion);
            if(null!=fragment){
                if(fragment instanceof HomeWorksFragment){
                    ((HomeWorksFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof HomeLikeVideoFragment){
                    ((HomeLikeVideoFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof HomeMessageFragment){
                    ((HomeMessageFragment) fragment).fromMainUpdata();
                }
            }
        }
    }

    /**
     * 显示顶部刷新动画
     */
    private void showFreshLodingView() {
        if(null!=bindingView&&null!=bindingView.loadMoreLoadingView&&bindingView.loadMoreLoadingView.getVisibility()!=View.VISIBLE){
            if(null==mAnimationDrawable) mAnimationDrawable = (AnimationDrawable) bindingView.ivLoadingIcon.getDrawable();
            if(!mAnimationDrawable.isRunning()) mAnimationDrawable.start();
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTopLocation();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isRefreshChild=true;
                    getUserData();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            bindingView.loadMoreLoadingView.setVisibility(View.VISIBLE);
            bindingView.loadMoreLoadingView.startAnimation(translateAnimation);

        }
    }

    /**
     * 隐藏顶部刷新动画
     */
    private void hideFreshLodingView(){
        if(null==bindingView) return;
        if(bindingView.loadMoreLoadingView.getVisibility()!=View.GONE){
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTop();
            bindingView.loadMoreLoadingView.startAnimation(translateAnimation);

            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bindingView.loadMoreLoadingView.setVisibility(View.GONE);
                    if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }


    /**
     * 刷新标题中的消息数量
     * @param count
     */
    public void updataTab(int count) {
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            mMineTabInfos.get(2).setAboutCount(count);
            updataTabAdapter();
        }
        if(null!=mUserInfo){
            mUserInfo.setMsgCount(count);
        }
    }

    /**
     * 刷新TAB数量
     * @param index 刷新第几个Item的Count
     */
    public void updataMineTabCount(int index) {
        if(index<0||index>2) return;
        //我的作品界面
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            if(index>=mMineTabInfos.size()) return;
            MineTabInfo mineTabInfo = mMineTabInfos.get(index);
            if(null!=mineTabInfo){
                int aboutCount = mineTabInfo.getAboutCount();
                if(aboutCount>0){
                    aboutCount--;
                }
                mineTabInfo.setAboutCount(aboutCount);
            }
            updataTabAdapter();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * 刷新通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (null != event) {
            if (TextUtils.equals(Constant.EVENT_UPDATA_MESSAGE_UI, event.getMessage())) {
                bindingView.viewTips.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 检查未读消息数量
     */
    private void checkedMsgCount() {
        if(null== VideoApplication.getInstance().getUserData()) return;
        List<NotifactionMessageInfo> messageList= (List<NotifactionMessageInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(VideoApplication.getLoginUserID()+Constant.CACHE_USER_MESSAGE);
        if(null!=messageList&&messageList.size()>0){
            int badgeCount=0;
            for (NotifactionMessageInfo notifactionMessageInfo : messageList) {
                if(!notifactionMessageInfo.isRead()){
                    badgeCount++;
                }
            }
            MainActivity activity = (MainActivity) getActivity();
            if(null!=activity&&!activity.isFinishing()){
                activity.setMessageCount(badgeCount);
            }
            //处理桌面图标
            if(badgeCount>0){
                ShortcutBadger.applyCount(getActivity().getApplicationContext(), badgeCount); //for 1.1.4+
                bindingView.viewTips.setVisibility(View.VISIBLE);
            }else{
                ShortcutBadger.applyCount(getActivity().getApplicationContext(), 0); //for 1.1.4+
                bindingView.viewTips.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 照片选择弹窗
     */
    private void showPictureSelectorPop() {
        try {
            //初始化
            if(null==mOutFilePath)  mOutFilePath = new File(Constant.IMAGE_PATH + IMAGE_DRR_PATH);

            //删除前面的缓存
            if(mOutFilePath.exists()&&mOutFilePath.isFile()){
                FileUtils.deleteFile(mOutFilePath);
            }
            mTempFile = new File(Constant.IMAGE_PATH + IMAGE_DRR_PATH_TEMP);
            if(mTempFile.exists()&&mTempFile.isFile()){
                FileUtils.deleteFile(mTempFile);
            }

        }catch (Exception e){
            showErrorToast(null,null,e.getMessage());
        }finally {

            List<VideoDetailsMenu> list=new ArrayList<>();
            VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
            videoDetailsMenu1.setItemID(1);
            videoDetailsMenu1.setTextColor("#FF576A8D");
            videoDetailsMenu1.setItemName("从相册选择");
            list.add(videoDetailsMenu1);

            VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
            videoDetailsMenu2.setItemID(2);
            videoDetailsMenu2.setTextColor("#FF576A8D");
            videoDetailsMenu2.setItemName("拍一张");
            list.add(videoDetailsMenu2);
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog((AppCompatActivity) getActivity());
            commonMenuDialog.setData(list);
            commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                @Override
                public void onItemClick(int itemID) {
                    switch (itemID) {
                        case 1:
                            headImageFromGallery();
                            break;
                        case 2:
                            headImageFromCameraCap();
                            break;
                    }
                }
            });
            commonMenuDialog.show();
        }
    }

    /**
     * 显示用户基本信息
     * @param data
     */
    @Override
    public void showUserInfo(MineUserInfo data) {
        hideFreshLodingView();
        isUpdata=false;
        mUserInfo = data.getData().getInfo();
        mUserInfo.setMsgCount(SharedPreferencesUtil.getInstance().getInt(Constant.KEY_MSG_COUNT));
        ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_USER_DATA);
        ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_USER_DATA,mUserInfo);//这个存储期限应该是无限期的
        initUserData();
        if(isRefreshChild){
            updataChildViewToPoistion(bindingView.viewPager.getCurrentItem());
            isRefreshChild=false;
        }
    }

    /**
     * 用户长传背景封面信息回调
     * @param data
     */
    @Override
    public void showPostImageBGResult(String data) {
        try {
            if(null!=mOutFilePath&&mOutFilePath.exists()&&mOutFilePath.isFile()){
                FileUtils.deleteFile(mOutFilePath);
                mOutFilePath=null;
            }
            if(null!=mTempFile&&mTempFile.exists()&&mTempFile.isFile()){
                FileUtils.deleteFile(mTempFile);
                mTempFile=null;
            }
        }catch (Exception e){

        }

        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(1==jsonObject.getInt("code")&&TextUtils.equals(jsonObject.getString("msg"),Constant.UPLOAD_USER_PHOTO_SUCCESS)){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingProgressedView.setResultsCompletes("上传成功", CommonUtils.getColor(R.color.app_style),true,Constant.PROGRESS_CLOSE_DELYAED_TIE);
                            mLoadingProgressedView.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                //等待其动画播放完成消失后就刷新界面
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    isRefreshChild=false;
                                    getUserData();
                                }
                            });
                        }
                    });
                }else{
                    closeProgressDialog();
                    ToastUtils.showCenterToast(jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
                closeProgressDialog();
                ToastUtils.showCenterToast("上传失败");
                e.printStackTrace();
            }
        }else{
            closeProgressDialog();
        }
    }


    @Override
    public void showErrorView() {
        hideFreshLodingView();
        closeProgressDialog();
    }

    @Override
    public void complete() {
        closeProgressDialog();
    }

    //====================================拍摄图片And图片选择=========================================
    private File mTempFile;
    private File mOutFilePath;
    private static final String IMAGE_DRR_PATH = "photo_image.jpg";//最终输出图片
    private static final String IMAGE_DRR_PATH_TEMP = "photo_image_temp.jpg";//临时图片
    private static final int INTENT_CODE_GALLERY_REQUEST = 0xa0;//相册
    private static final int INTENT_CODE_CAMERA_REQUEST = 0xa1;//相册
    // 从本地相册选取图片作为头像
    private void headImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");//选择图片
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, INTENT_CODE_GALLERY_REQUEST);
    }

    // 启动相机拍摄照片
    private void headImageFromCameraCap() {
        //检查SD读写权限
        RxPermissions.getInstance(getActivity()).request(Manifest.permission.CAMERA).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    //判断相机是否可用
                    PackageManager pm = getActivity().getPackageManager();
                    boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                            || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                            || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
                            || Camera.getNumberOfCameras() > 0;
                    //调用系统相机拍摄
                    if(hasACamera){
                        AndroidNFileUtils.startActionCapture(getActivity(),mTempFile,INTENT_CODE_CAMERA_REQUEST);
                        //使用自定义相机拍摄
                    }else{
                        Intent intent=new Intent(getActivity(),MediaPictruePhotoActivity.class);
                        intent.putExtra("output",mOutFilePath.getAbsolutePath());
                        intent.putExtra("output-max-width",800);
                        startActivityForResult(intent,Constant.REQUEST_TAKE_PHOTO);
                    }
                }else{
                    checkedPermission();
                }
            }
        });
    }

    /**
     * 检查拍照权限
     */
    private void checkedPermission() {
        int cameraPerm = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                ToastUtils.showCenterToast("大23，需要检测权限");
                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CAMERA);
            }
        } else {
            headImageFromCameraCap();
        }
    }

    /**
     * 获取权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    headImageFromCameraCap();
                } else {
                    ToastUtils.showCenterToast("要正常使用拍摄功能，请务必授予拍照权限！");
                }
                break;
            }
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_CANCELED){
            return;
        }
        try {
            //拍照和裁剪返回
            if (resultCode == Activity.RESULT_OK && data != null && (requestCode == Constant.REQUEST_CLIP_IMAGE || requestCode == Constant.REQUEST_TAKE_PHOTO)) {
                String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
                if (path != null) {
                    File imageFile = new File(path);
                    if(imageFile.exists()&&imageFile.isFile()){
                        showProgressDialog("上传中...",true);
                        mPresenter.onPostImageBG(VideoApplication.getLoginUserID(),imageFile.getAbsolutePath());
                    }
                }else{
                    showErrorToast(null,null,"操作错误");
                }
                //本地相册选取的图片,转换为Path路径后再交给裁剪界面处理
            }else if(requestCode== INTENT_CODE_GALLERY_REQUEST){
                if(null!=data){
                    ContentResolver resolver =getActivity().getContentResolver();
                    Uri originalUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if(null!=bitmap){
                            String filePath = FileUtils.saveBitmap(bitmap, Constant.IMAGE_PATH + IMAGE_DRR_PATH_TEMP);
                            startClipActivity(filePath,mOutFilePath.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showErrorToast(null,null,"操作错误"+e.getMessage());
                    }
                }
             //系统照相机拍照完成回调
            }else if(requestCode==INTENT_CODE_CAMERA_REQUEST){
                startClipActivity(mTempFile.getAbsolutePath(),mOutFilePath.getAbsolutePath());
            }
        }catch (Exception e){
            showErrorToast(null,null,"操作错误"+e.getMessage());
        }
    }

    /**
     * 系统相机拍摄返回
     */
    public void clippingPictures(){
        if(null!=mTempFile&&mTempFile.exists()&&null!=mOutFilePath){
            startClipActivity(mTempFile.getAbsolutePath(),mOutFilePath.getAbsolutePath());
        }
    }

    /**
     * 去裁剪
     * @param inputFilePath
     * @param outputFilePath
     */
    private void startClipActivity(String inputFilePath, String outputFilePath) {
        Intent intent = new Intent(getActivity(), ClipImageActivity.class);
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        intent.putExtra("maxWidth", 800);
        intent.putExtra("tip", "");
        intent.putExtra("inputPath", inputFilePath);
        intent.putExtra("outputPath", outputFilePath);
        intent.putExtra("clipCircle",false);
        startActivityForResult(intent, Constant.REQUEST_CLIP_IMAGE);
    }

    /**
     * 来自首页的刷新命令
     */
    public void fromMainUpdata() {
        //改成刷新个人信息后直接刷新正在显示的子Fragment
        if(null!=bindingView&&null!=VideoApplication.getInstance().getUserData()){
            if(null!= mPresenter &&!mPresenter.isLoading()){
                bindingView.appBarLayout.setExpanded(true);
                showFreshLodingView();
            }else{
                showErrorToast(null,null,"点击太频繁了");
            }
        }
    }

    /**
     * 切换界面
     * @param childIndex
     */
    public void currentChildView(int childIndex) {
        if(null==mFragmentList||mFragmentList.size()<=0) return;
        if(childIndex<0||childIndex>=mFragmentList.size()) return;
        if(null!=bindingView&&null!=bindingView.viewPager){
            bindingView.viewPager.setCurrentItem(childIndex,true);
        }
    }

    @Override
    public void onDestroy() {
        if(null!=mFragmentList){
            mFragmentList.clear();
        }
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    //登录
                    case Constant.OBSERVABLE_ACTION_LOGIN:
                        isUpdata=true;
                        break;
                    //登出
                    case Constant.OBSERVABLE_ACTION_UNLOGIN:
                        canelUserData();
                        bindingView.viewPager.setCurrentItem(0);
                        break;
                    //关注、取关，只更新基本数据和关注数量
                    case Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED:
                        getUserData();
                        break;
                }
            }else if(arg instanceof ChangingViewEvent){
                ChangingViewEvent changingViewEvent= (ChangingViewEvent) arg;
                if(Constant.FRAGMENT_TYPE_WORKS==changingViewEvent.getFragmentType()||Constant.FRAGMENT_TYPE_LIKE==changingViewEvent.getFragmentType()){
                    if(null!=bindingView) bindingView.appBarLayout.setExpanded(false,false);
                }
            }
        }
    }
}
