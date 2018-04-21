package com.video.newqu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.SubmitEvent;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityContentFragmentBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.ui.fragment.FansListFragment;
import com.video.newqu.ui.fragment.FollowUserListFragment;
import com.video.newqu.ui.fragment.MediaMusicCategroyListFragment;
import com.video.newqu.ui.fragment.NotifcationMessageFragment;
import com.video.newqu.ui.fragment.ServiceMessageFragment;
import com.video.newqu.ui.fragment.SettingFragment;
import com.video.newqu.ui.fragment.TopicVideoListFragment;
import com.video.newqu.ui.fragment.UserVideoHistoryListFragment;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * TinyHung@outlook.com
 * 2017/6/1 11:59
 * 多个Fragment界面通用的父Activity
 */

public class ContentFragmentActivity extends BaseActivity<ActivityContentFragmentBinding>{

    private String mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_fragment);
        showToolBar(false);
        findViewById(R.id.view_state_bar).setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
        StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        switchFragment();
    }

    /**
     * 根据传递的参数打开并配置Fragment
     */
    private void switchFragment() {
        Intent intent = getIntent();
        if(null==intent) {
            ToastUtils.showCenterToast("跳转错误!");
            finish();
            return;
        }
        mTitle= intent.getStringExtra(Constant.KEY_TITLE);
        bindingView.tvTitle.setText(mTitle);
        int fragmentType = intent.getIntExtra(Constant.KEY_FRAGMENT_TYPE, 0);
        switch (fragmentType) {
            //设置中心
            case Constant.KEY_FRAGMENT_TYPE_SETTINGS:
                replaceFragment(R.id.frame_layout,new SettingFragment());
                break;
            //粉丝列表
            case Constant.KEY_FRAGMENT_TYPE_FANS_LIST:
                replaceFragment(R.id.frame_layout, FansListFragment.newInstance(getIntent().getIntExtra(Constant.KEY_AUTHOR_TYPE,0),getIntent().getStringExtra(Constant.KEY_AUTHOR_ID)));
                break;
            //关注的用户列表
            case Constant.KEY_FRAGMENT_TYPE_FOLLOW_USER_LIST:
                replaceFragment(R.id.frame_layout, FollowUserListFragment.newInstance(getIntent().getIntExtra(Constant.KEY_AUTHOR_TYPE,0),getIntent().getStringExtra(Constant.KEY_AUTHOR_ID)));
                break;
            //话题分类视频列表
            case Constant.KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT:
                replaceFragment(R.id.frame_layout, TopicVideoListFragment.newInstance(getIntent().getStringExtra(Constant.KEY_VIDEO_TOPIC_ID)));
                break;
            //播放视频的历史记录
            case Constant.KEY_FRAGMENT_TYPE_PLSYER_HISTORY:
                replaceFragment(R.id.frame_layout, new UserVideoHistoryListFragment());
                break;
            //分类下列表
            case Constant.KEY_FRAGMENT_TYPE_MUSIC_CATEGORY_LIST:
                replaceFragment(R.id.frame_layout, MediaMusicCategroyListFragment.newInstance(getIntent().getStringExtra(Constant.MEDIA_KEY_MUSIC_CATEGORY_ID)));
                break;
            //反馈
            case Constant.KEY_FRAGMENT_SERVICES:
                replaceFragment(R.id.frame_layout, new ServiceMessageFragment());
                break;
            //通知消息
            case Constant.KEY_FRAGMENT_NOTIFACTION:
                replaceFragment(R.id.frame_layout, new NotifcationMessageFragment());
            default:
        }
    }

    /**
     *显示在屏幕底部
     */
    private void initLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager)getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= (systemService.getDefaultDisplay().getHeight()-(ScreenUtils.getScreenHeight()>=1280?ScreenUtils.dpToPxInt(70):ScreenUtils.dpToPxInt(60)));
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.tv_canel_history:
                        EventBus.getDefault().post(new SubmitEvent("caneal_history"));
                        break;
                }
            }
        };
        bindingView.tvCanelHistory.setOnClickListener(onClickListener);
    }

    @Override
    public void initData() {
        //返回
        bindingView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //提交
        bindingView.ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SubmitEvent("submit"));
            }
        });
    }



    public void showCanealHistoryMenu(boolean isShow){
        if(null!=bindingView&&null!=bindingView.tvCanelHistory){
            if(isShow){
                if(bindingView.tvCanelHistory.getVisibility()!=View.VISIBLE){
                    bindingView.tvCanelHistory.setVisibility(View.VISIBLE);
                }
            }else{
                if(bindingView.tvCanelHistory.getVisibility()!=View.GONE){
                    bindingView.tvCanelHistory.setVisibility(View.GONE);
                }
            }
        }
    }


    public void onResultFilish(String musicID, String musicPath) {
        Intent intent=new Intent();
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID,musicID);
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH,musicPath);
        setResult(Constant.MEDIA_START_MUSIC_CATEGORY_RESULT_CODE,intent);
        finish();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


    /**
     * 切换当前显示的Fragment
     * @param fragment
     */
    public void addReplaceFragment(Fragment fragment,String title) {
        bindingView.tvTitle.setText(title);
        try {
            android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "FRAGMENT");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        //更新标题
        if(getSupportFragmentManager().getBackStackEntryCount()>0&&!ContentFragmentActivity.this.isFinishing()){
            if(!TextUtils.isEmpty(mTitle)){
                bindingView.tvTitle.setText(mTitle);
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageEvent messageEvent=new MessageEvent();
        messageEvent.setMessage(Constant.EVENT_UPDATA_MUSIC_PLAYER);
        messageEvent.setType(-1);
        EventBus.getDefault().post(messageEvent);
    }
}
