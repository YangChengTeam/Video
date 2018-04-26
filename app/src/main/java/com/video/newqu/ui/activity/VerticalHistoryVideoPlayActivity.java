package com.video.newqu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.VerticalPagerAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.bean.VideoEventMessage;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityVideoPlayerBinding;
import com.video.newqu.manager.ActivityCollectorManager;
import com.video.newqu.ui.contract.UserHistoryContract;
import com.video.newqu.ui.pager.VerticalHistoryVidepPlayViewPager;
import com.video.newqu.ui.presenter.UserHistoryPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.view.widget.VerticalViewPager;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@outlook.com
 * 2017/5/25 9:26
 * 历史播放记录界面的可滑动详情列表
 */

public class VerticalHistoryVideoPlayActivity extends BaseActivity<ActivityVideoPlayerBinding> implements UserHistoryContract.View, Observer {


    private  List<UserPlayerVideoHistoryList> mListsBeanList;
    private int mVideoPoistion=0;//默认的现实第几个
    private Map<Integer,VerticalHistoryVidepPlayViewPager> playerViews=new HashMap<>();//存放界面View的集合
    private PlayListVerticalPagerAdapter mPagerAdapter;
    private UserHistoryPresenter mUserHistoryPresenter;
    private Handler mHandler;


    public static void start(Context context, int fragmentType,int position,List<UserPlayerVideoHistoryList> data) {
        Intent intent=new Intent(context,VerticalHistoryVideoPlayActivity.class);
        intent.putExtra(Constant.KEY_POISTION,position);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentType);
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList("history_list", (ArrayList<? extends Parcelable>) data);
        intent.putExtra("bundle",bundle);
        context.startActivity(intent);
    }

    @Override
    public void initViews() {
        mUserHistoryPresenter = new UserHistoryPresenter();
        mUserHistoryPresenter.attachView(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if(null==intent) {
            ToastUtils.showCenterToast("错误!");
            finish();
            return;
        }
        Bundle bundle = intent.getBundleExtra("bundle");
        if(null==bundle){
            finish();
            return;
        }
        mListsBeanList= bundle.getParcelableArrayList("history_list");
        if(null==mListsBeanList||mListsBeanList.size()<=0) {
            ToastUtils.showCenterToast("错误!");
            finish();
            return;
        }

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("SD存储卡准备中");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡授予本软件的使用权限！");
            return;
        }
        mVideoPoistion = intent.getIntExtra(Constant.KEY_POISTION, 0);
        initAdapter();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        showToolBar(false);
        ApplicationManager.getInstance().addObserver(this);
    }

    private void initAdapter() {
        //初始化界面适配器
//        listSize+=mListsBeanList.size();
        mPagerAdapter = new PlayListVerticalPagerAdapter();
        bindingView.viewPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                WindowVideoPlayer.releaseAllVideos();
                onChildPause(mVideoPoistion);//清空上一个界面播放器中资源
                mVideoPoistion=position;
                if(null!=mHandler){
                    mHandler.removeMessages(0);//创建新的任务前，取消所有的定时任务
                }
                waitPlayVideo(300,mVideoPoistion);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bindingView.viewPager.setOffscreenPageLimit(1);
        bindingView.viewPager.setAdapter(mPagerAdapter);
        bindingView.viewPager.setCurrentItem(mVideoPoistion);
        waitPlayVideo(400,mVideoPoistion);
    }


    /**
     * 设置延缓任务
     * @param misTime 需要延缓多久
     * @param waitPoistion 延缓播放视频的目标Poistion
     */
    private void waitPlayVideo(long misTime,int waitPoistion) {
        if(null==mHandler) mHandler=new Handler();
        mHandler.postAtTime(new PlayVideoRunnable(waitPoistion), SystemClock.uptimeMillis()+misTime);//设置延缓任务
    }


    /**
     * 这个Runnable用来执行延缓任务，记录要执行的延缓任务，只有当前显示的viewPager cureenItem与当时提交的cureenItem相等才允许播放
     */
    private class PlayVideoRunnable implements Runnable{
        private final int waitPlayPoistin;

        public PlayVideoRunnable(int waitPoistion){
            this.waitPlayPoistin=waitPoistion;
        }

        @Override
        public void run() {
            if(this.waitPlayPoistin!=mVideoPoistion){
                return;
            }
            playerVideo(waitPlayPoistin);
        }
    }


    /**
     * 播放视频
     */
    private void playerVideo(int poistion) {
        if(-1!=poistion&&null!=playerViews&&playerViews.size()>0){
            Iterator<Map.Entry<Integer, VerticalHistoryVidepPlayViewPager>> iterator = playerViews.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalHistoryVidepPlayViewPager> next = iterator.next();
                if(poistion==next.getKey()){
                    VerticalHistoryVidepPlayViewPager playerTempPager = next.getValue();
                    if(null!=playerTempPager){
                        playerTempPager.onPlaye();
                    }
                }
            }
        }
    }

    //======================================联网加载数据回调==========================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }


    @Override
    public void showVideoHistoryList(List<UserPlayerVideoHistoryList> data) {
//        listSize+=data.size();
        if(null!=mListsBeanList) mListsBeanList.addAll(data);
        if(null!=mPagerAdapter) mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showVideoHistoryListEmpty(String data) {
        ToastUtils.showCenterToast(data);
//        if(mPage>0){
//            mPage--;
//        }
    }


    /**
     * 垂直列表适配器
     */
    private class PlayListVerticalPagerAdapter extends VerticalPagerAdapter {

        @Override
        public int getCount() {
            return null==mListsBeanList?0:mListsBeanList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            UserPlayerVideoHistoryList userPlayerVideoHistoryList = mListsBeanList.get(position);
            if(null!=userPlayerVideoHistoryList){
                VerticalHistoryVidepPlayViewPager videpPlayerViewPager = new VerticalHistoryVidepPlayViewPager(VerticalHistoryVideoPlayActivity.this,userPlayerVideoHistoryList,position);
                View view = videpPlayerViewPager.getView();
                view.setId(position);
                if(null!=playerViews) playerViews.put(position, videpPlayerViewPager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null!=container){
                container.removeView(container.findViewById(position));
                if(null!=playerViews) playerViews.remove(position);
            }
        }
    }


    public void login() {
        Intent intent = new Intent(VerticalHistoryVideoPlayActivity.this, LoginGroupActivity.class);
        startActivityForResult(intent, Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录意图，需进一步确认
        if (Constant.INTENT_LOGIN_EQUESTCODE == requestCode && resultCode == Constant.INTENT_LOGIN_RESULTCODE) {
            if (null != data) {
                if (null!=VideoApplication.getInstance().getUserData()&&!VideoApplication.getInstance().userIsBinDingPhone()) {
                    binDingPhoneNumber("绑定手机号码",Constant.FRAGMENT_TYPE_PHONE_BINDING,"发布视频需要验证手机号");
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowVideoPlayer.goOnPlayOnResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        WindowVideoPlayer.goOnPlayOnPause();
    }


    private void onChildPause(int itemPoistion) {
        if(-1!=itemPoistion&&null!=playerViews&&playerViews.size()>0){
            Iterator<Map.Entry<Integer, VerticalHistoryVidepPlayViewPager>> iterator = playerViews.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalHistoryVidepPlayViewPager> next = iterator.next();
                if(itemPoistion==next.getKey()){
                    VerticalHistoryVidepPlayViewPager playerTempPager = next.getValue();
                    if(null!=playerTempPager){
                        playerTempPager.onPause();
                    }
                }
            }
        }
    }


    /**
     * 生命周期onChildDestroyView
     * @param itemPoistion
     */
    private void onChildDestroyView(int itemPoistion) {
        if(-1!=itemPoistion&&null!=playerViews&&playerViews.size()>0){
            Iterator<Map.Entry<Integer, VerticalHistoryVidepPlayViewPager>> iterator = playerViews.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalHistoryVidepPlayViewPager> next = iterator.next();
                if(itemPoistion==next.getKey()){
                    VerticalHistoryVidepPlayViewPager playerTempPager = next.getValue();
                    if(null!=playerTempPager){
                        playerTempPager.onDestroy();
                    }
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        WindowVideoPlayer.releaseAllVideos();
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mUserHistoryPresenter) mUserHistoryPresenter.detachView();
        onChildDestroyView(mVideoPoistion);
        if(null!=mHandler){
            mHandler.removeMessages(0);//这句话可以移除所有的延缓任务
            mHandler=null;
        }
        if(null!=mListsBeanList) mListsBeanList.clear();
        if(null!=playerViews)playerViews.clear();
        mListsBeanList=null;mListsBeanList=null;
        super.onDestroy();
        ActivityCollectorManager.removeActivity(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof VideoEventMessage){
                VideoEventMessage data= (VideoEventMessage) arg;
                if(null!=data&& TextUtils.equals(Constant.EVENT_HISTORY_VIDEO_PLAY_PAGE_UPDATA,data.getMessage())){
                    UserPlayerVideoHistoryList dataList = data.getData();
                    if(null!=data){
                        if(null!=mListsBeanList&&mListsBeanList.size()>0){
                            int poistion = data.getPoistion();
                            mListsBeanList.remove(poistion);
                            mListsBeanList.add(poistion,dataList);
                            ApplicationManager.getInstance().getUserPlayerDB().updatePlayerHistoryInfo(dataList);
                        }
                    }
                }
            }
        }
    }
}
