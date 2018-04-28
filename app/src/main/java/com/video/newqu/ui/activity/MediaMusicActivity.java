package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.video.newqu.R;
import com.video.newqu.adapter.MediaMusicMenuAdapter;
import com.video.newqu.adapter.XinQuFragmentPagerAdapter;
import com.video.newqu.base.BaseMusicActivity;
import com.video.newqu.bean.MediaMusicHomeMenu;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemClickListener;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityRecordMusicBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.ui.contract.MediaMusicContract;
import com.video.newqu.ui.fragment.MediaMusicLocationFragment;
import com.video.newqu.ui.fragment.MediaMusicLikeFragment;
import com.video.newqu.ui.fragment.MediaMusicRecommendFragment;
import com.video.newqu.ui.presenter.MediaMusicPresenter;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import org.greenrobot.eventbus.EventBus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 音乐选择模块界面
 */

public class MediaMusicActivity extends BaseMusicActivity<ActivityRecordMusicBinding> implements MediaMusicContract.View {

    private MediaMusicMenuAdapter mMediaMusicMenuAdapter;
    private List<MediaMusicHomeMenu.DataBean> mediaMusicHomeMenus=null;
    private MediaMusicPresenter mMediaMusicPresenter;
    private int cureenIndex=0;
    private ReEmptyLayoutBinding mEmptyViewbindView;
    private int mHeaderViewHeight=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_music);
        mMediaMusicPresenter = new MediaMusicPresenter(MediaMusicActivity.this);
        mMediaMusicPresenter.attachView(MediaMusicActivity.this);
        WindowVideoPlayer.isWifiTips=false;
        initMenuAdapter();
        initFeagment();
    }

    private void loadMenuData() {
        if(null!=mMediaMusicPresenter&&!mMediaMusicPresenter.isHomeLoading()){
            mMediaMusicPresenter.getHomeMusicData();
        }
    }

    @Override
    public void initViews() {
        baseBinding.tvTitle.setText("选择音乐");
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_back:
                        onBackPressed();
                        break;
                    case R.id.ll_search:
                        Intent intent=new Intent(MediaMusicActivity.this,MediaMusicSearchActivity.class);
                        startActivityForResult(intent,Constant.MEDIA_START_MUSIC_CATEGORY_REQUEST_CODE);
                        break;
                }
            }
        };
        baseBinding.ivBack.setOnClickListener(onClickListener);
        baseBinding.llSearch.setOnClickListener(onClickListener);

        //菜单父窗体高度会随时发生变化的
        ViewTreeObserver viewTreeObserver = bindingView.collapseToolbar.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight=bindingView.collapseToolbar.getHeight();
                bindingView.emptyView.getLayoutParams().height=mHeaderViewHeight;
            }
        });
        bindingView.emptyView.setBackgroundColor(Color.parseColor("#F0F2F5"));
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int abs = Math.abs(verticalOffset);
                //用户信息图层网上滑动，所以是相反的透明度
                float scale = (float) abs / mHeaderViewHeight;
                float alpha =(scale * 255);
                float v = alpha / 225f;
                bindingView.emptyView.setAlpha(v);
            }
        });
    }


    private void initFeagment() {
        List<Fragment> mFragmentList=new ArrayList<>();
        mFragmentList.add(new MediaMusicRecommendFragment());
        mFragmentList.add(new MediaMusicLikeFragment());
        mFragmentList.add(new MediaMusicLocationFragment());
        List<String> titles=new ArrayList<>();
        titles.add(getResources().getString(R.string.media_music_fragment_hot_title));
        titles.add(getResources().getString(R.string.media_music_fragment_like_title));
        titles.add(getResources().getString(R.string.media_music_fragment_location_title));
        XinQuFragmentPagerAdapter myXinQuFragmentPagerAdapter =new XinQuFragmentPagerAdapter(getSupportFragmentManager(),mFragmentList,titles);
        bindingView.viewPager.setAdapter(myXinQuFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(3);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(0);
        cureenIndex=0;
        bindingView.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MessageEvent messageEvent=new MessageEvent();
                messageEvent.setMessage(Constant.EVENT_UPDATA_MUSIC_PLAYER);
                int tempCureenIndex=cureenIndex;
                messageEvent.setType(tempCureenIndex);//告诉哪个界面需要刷新
                EventBus.getDefault().post(messageEvent);
                cureenIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {
    }

    /**
     * 初始化分类列表
     */

    private void initMenuAdapter() {
        List<MediaMusicHomeMenu.DataBean> list=(List<MediaMusicHomeMenu.DataBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MEDIA_MUSIC_CATEGORY_LIST);
        mediaMusicHomeMenus=list;
        bindingView.menuRecyerView.setLayoutManager(new GridLayoutManager(MediaMusicActivity.this,5,GridLayoutManager.VERTICAL,false));
        if(null!=list&&list.size()>10){
            List<MediaMusicHomeMenu.DataBean> newMediaMusicHomeMenus= Utils.catMenuItemList(list);
            mMediaMusicMenuAdapter = new MediaMusicMenuAdapter(newMediaMusicHomeMenus);
        }else{
            mMediaMusicMenuAdapter = new MediaMusicMenuAdapter(list);
        }
        //占位布局
        mEmptyViewbindView = DataBindingUtil.inflate(getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.menuRecyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyViewbindView.emptyView.showLoadingView();
                loadMenuData();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mMediaMusicMenuAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        bindingView.menuRecyerView.setAdapter(mMediaMusicMenuAdapter);
        bindingView.menuRecyerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<MediaMusicHomeMenu.DataBean> data = mMediaMusicMenuAdapter.getData();
                if(null!= data && data.size()>0){
                    MediaMusicHomeMenu.DataBean mediaMusicHomeMenu = data.get(position);
                    if(null!=mediaMusicHomeMenu){
                        //更多
                        if(1==mediaMusicHomeMenu.getItemType()){
                            if(null!=mediaMusicHomeMenus&&mediaMusicHomeMenus.size()>0){
                                mMediaMusicMenuAdapter.setNewData(mediaMusicHomeMenus);
                            }
                        //普通的
                        }else{
                            Intent intent=new Intent(MediaMusicActivity.this,ContentFragmentActivity.class);
                            intent.putExtra(Constant.KEY_TITLE,mediaMusicHomeMenu.getName());
                            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.KEY_FRAGMENT_TYPE_MUSIC_CATEGORY_LIST);
                            intent.putExtra(Constant.MEDIA_KEY_MUSIC_CATEGORY_ID,mediaMusicHomeMenu.getId());
                            startActivityForResult(intent,Constant.MEDIA_START_MUSIC_CATEGORY_REQUEST_CODE);
                        }
                    }
                }
            }
        });
        //防止重复请求，当缓存存在，只用缓存
        if(null==mMediaMusicMenuAdapter.getData()||mMediaMusicMenuAdapter.getData().size()<=0){
            loadMenuData();
        }
    }


    /**
     * 提供给子界面的登录方法
     */
    public void login(){
        Intent intent=new Intent(MediaMusicActivity.this,LoginGroupActivity.class);
        startActivityForResult(intent,Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constant.MEDIA_START_MUSIC_CATEGORY_REQUEST_CODE&&resultCode==Constant.MEDIA_START_MUSIC_CATEGORY_RESULT_CODE){
            if(null!=data){
                String mMusicID = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID);
                String mMusicPath = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH);
                if(!TextUtils.isEmpty(mMusicID)&& !TextUtils.isEmpty(mMusicPath)){
                    onResultFilish(mMusicID,mMusicPath);
                }
            }
        }
    }

    /**
     * 调用此方法携带ID回去拍摄
     * @param musicID
     * @param musicPath
     */
    public void onResultFilish(String musicID, String musicPath) {
        Intent intent=new Intent();
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID,musicID);
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH,musicPath);
        setResult(Constant.MEDIA_START_MUSIC_RESULT_CODE,intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查SD读写权限
        RxPermissions.getInstance(MediaMusicActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){

                }else{
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaMusicActivity.this)
                                .setTitle("SD读取权限申请失败")
                                .setMessage("部分权限被拒绝，将无法使用本地视频相册功能，请先授予足够权限再使用视频扫描功能！授权成功后请重启开启本界面。是否现在去设置？");
                        builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SystemUtils.getInstance().startAppDetailsInfoActivity(MediaMusicActivity.this,141);
                            }
                        });
                        builder.show();
                        return;
                    }
                    ToastUtils.showCenterToast("请检查SD卡状态");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        MessageEvent messageEvent=new MessageEvent();
        messageEvent.setMessage(Constant.EVENT_UPDATA_MUSIC_PLAYER);
        int tempCureenIndex=cureenIndex;
        messageEvent.setType(tempCureenIndex);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onDestroy() {
        if(null!=mMediaMusicPresenter){
            mMediaMusicPresenter.detachView();
        }
        WindowVideoPlayer.isWifiTips=true;
        WindowVideoPlayer.releaseAllVideos();
        ApplicationManager.getInstance().removeAllObserverToMusic();
        if(null!=mediaMusicHomeMenus) mediaMusicHomeMenus.clear();
        mediaMusicHomeMenus=null;
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.menu_exit);//出场动画
    }

    @Override
    public void showMusicCategoryList(List<MediaMusicHomeMenu.DataBean> data) {
        if(!this.isFinishing()){
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MEDIA_MUSIC_CATEGORY_LIST);
            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MEDIA_MUSIC_CATEGORY_LIST, (Serializable) data, Constant.CACHE_STICKER_TIME);//存储半个小时，半小时内不再重复请求
            mediaMusicHomeMenus=data;
            if(data.size()>10){
                List<MediaMusicHomeMenu.DataBean> newMediaMusicHomeMenus= Utils.catMenuItemList(data);
                if(null!=mMediaMusicMenuAdapter){
                    mMediaMusicMenuAdapter.setNewData(newMediaMusicHomeMenus);
                }
            }else{
                if(null!=mMediaMusicMenuAdapter){
                    mMediaMusicMenuAdapter.setNewData(data);
                }
            }
            if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("暂时没有分类数据~!",R.drawable.iv_fans_empty,false);
        }
    }

    @Override
    public void showMusicCategoryEmpty(String data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("暂时没有分类数据~!",R.drawable.iv_fans_empty,false);
    }

    @Override
    public void showMusicCategoryError(String data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
    }
}
