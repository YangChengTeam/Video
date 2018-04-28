package com.video.newqu.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.SubmitEvent;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityContentFragmentBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.ui.fragment.AppAboutFragment;
import com.video.newqu.ui.fragment.FansListFragment;
import com.video.newqu.ui.fragment.FollowUserListFragment;
import com.video.newqu.ui.fragment.MediaMusicCategroyListFragment;
import com.video.newqu.ui.fragment.NotifcationMessageFragment;
import com.video.newqu.ui.fragment.ServiceMessageFragment;
import com.video.newqu.ui.fragment.SettingFragment;
import com.video.newqu.ui.fragment.TopicVideoListFragment;
import com.video.newqu.ui.fragment.VideoPlayHistoryFragment;
import com.video.newqu.util.ToastUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * TinyHung@outlook.com
 * 2017/6/1 11:59
 * Fragment载体
 */

public class ContentFragmentActivity extends BaseActivity<ActivityContentFragmentBinding>{

    private String mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_fragment);
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
        setTitle(mTitle);
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
                replaceFragment(R.id.frame_layout, new VideoPlayHistoryFragment());
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
                break;
            //关于，关于界面是沉浸式的
            case Constant.FRAGMENT_TYPE_ABOUT:
                showToolBar(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
                replaceFragment(R.id.frame_layout, new AppAboutFragment());
                break;
            default:
        }
    }


    @Override
    public void initViews() {

    }

    @Override
    protected void onSubmitTitleClick(View view) {
        super.onSubmitTitleClick(view);
        EventBus.getDefault().post(new SubmitEvent("caneal_history"));
    }

    @Override
    protected void onMenuClick(View view) {
        super.onMenuClick(view);
        EventBus.getDefault().post(new SubmitEvent("submit"));
    }

    @Override
    public void initData() {
    }

    public void showCanealHistoryMenu(boolean isShow){
        showSubmitTitle(isShow);
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
        setTitle(title);
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
                setTitle(mTitle);
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
