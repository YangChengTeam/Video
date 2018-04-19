package com.video.newqu.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.danikula.videocache.HttpProxyCacheServer;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.MediaMusicRecommendAdapter;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.bean.MusicInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.FragmentMediaMusicHomeBinding;
import com.video.newqu.databinding.ReEmptyMarginLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.manager.DownloadFileUtilTask;
import com.video.newqu.ui.activity.MediaMusicActivity;
import com.video.newqu.ui.contract.MediaMusicLikeContract;
import com.video.newqu.ui.dialog.RecordProgressDialog;
import com.video.newqu.ui.presenter.MediaMusicLikePresenter;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.MineDataChangeMarginView;
import com.video.newqu.view.widget.SwipeLoadingProgress;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 音乐选择-收藏列表
 */

public class MediaMusicLikeFragment extends BaseLightWeightFragment<FragmentMediaMusicHomeBinding,MediaMusicLikePresenter> implements
        OnMediaMusicClickListener, MediaMusicLikeContract.View, Observer {

    private MediaMusicActivity mMusicActivity;
    private MediaMusicRecommendAdapter mMusicAdapter;
    private int mPage=0;
    private int pageSize=10;
    private RecordProgressDialog mRecordProgressDialog;
    private MediaMusicCategoryList.DataBean mData;
    private boolean isRefresh=true;
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMusicActivity = (MediaMusicActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new MediaMusicLikePresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        ApplicationManager.getInstance().addObserverToMusic(this);
    }

    @Override
    protected void initViews() {
        bindingView.swiperLayout.setOnSwipeProgressEndListener(new SwipeLoadingProgress.OnSwipeProgressEndListener() {
            @Override
            public void onShowFinlish() {
                mPage=0;
                loadMusicData();
            }
            @Override
            public void onHideFinlish() {

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_media_music_home;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mMusicAdapter&&null!=mPresenter&&!mPresenter.isLikeList()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<MediaMusicCategoryList.DataBean> data = mMusicAdapter.getData();
                    if(null==data||data.size()<=0){
                        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                        mPage=0;
                        loadMusicData();
                    }else{
                        bindingView.swiperLayout.showLoadingProgress();
                    }
                }
            },Constant.POST_DELAYED_ADD_DATA_TIME);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<MediaMusicCategoryList.DataBean> cacheList=(List<MediaMusicCategoryList.DataBean>)ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        bindingView.recyerView.setLayoutManager(linearLayoutManager);
        mMusicAdapter = new MediaMusicRecommendAdapter(cacheList,this);
        mMusicAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mMusicAdapter){
                    List<MediaMusicCategoryList.DataBean> data = mMusicAdapter.getData();
                    if(null!=data&&data.size()>9&&null!=mPresenter&&!mPresenter.isLikeList()){
                        mMusicAdapter.setEnableLoadMore(true);
                        loadMusicData();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mMusicAdapter.loadMoreFail();//模拟加载失败
                                }else{
                                    mMusicAdapter.loadMoreEnd();//模拟加载完成
                                }
                            }
                        });
                    }
                }
            }
        }, bindingView.recyerView);

        //加载中、数据为空、加载失败布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_margin_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new MineDataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyViewbindView.emptyView.showLoadingView();
                loadMusicData();
            }

            @Override
            public void onClickView(View v) {

            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mMusicAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        bindingView.recyerView.setAdapter(mMusicAdapter);
    }


    private void loadMusicData() {
        if(null!= mPresenter &&!mPresenter.isLikeList()){
            mPage++;
            mPresenter.getLikeMusicList(null,mPage,pageSize);
        }
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}

    @Override
    public void showLikeMusicList(List<MediaMusicCategoryList.DataBean> data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("还没有收藏音乐~",R.drawable.iv_work_video_empty,false);
        if(null!=mMusicAdapter){
            mMusicAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                WindowVideoPlayer.releaseAllVideos();
                mMusicAdapter.setNewData(data);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC, (Serializable) data, Constant.CACHE_TIME);
                //添加数据
            }else{
                mMusicAdapter.addData(data);
            }
        }
    }

    @Override
    public void showLikeMusicEmpty(String data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("还没有收藏音乐~",R.drawable.iv_work_video_empty,false);
        if(null!=mMusicAdapter){
            mMusicAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mMusicAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    @Override
    public void showLikeMusicError(String data) {
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mMusicAdapter){
            mMusicAdapter.loadMoreFail();
            List<MediaMusicCategoryList.DataBean> dataList = mMusicAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 点赞结果
     * @param data
     */
    @Override
    public void showLikeResultResult(String data) {
        closeProgressDialog();
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(null!=jsonObject&&jsonObject.length()>0){
                    if(1==jsonObject.getInt("code")){
                        String music_id = jsonObject.getString("music_id");
                        if(null!=music_id){
                            if(null!= mMusicAdapter){
                                List<MediaMusicCategoryList.DataBean> musicList = mMusicAdapter.getData();
                                if(null!=musicList&&musicList.size()>0){
                                    int poistion=0;
                                    for (int i = 0; i < musicList.size(); i++) {
                                        if(TextUtils.equals(music_id,musicList.get(i).getId())){
                                            musicList.get(i).setIs_collect(Integer.parseInt(jsonObject.getString("res")));
                                            poistion=i;
                                            break;
                                        }
                                    }
                                    //直接删除取消收藏成功的那个音乐条目
                                    mMusicAdapter.remove(poistion);
                                    //替换最新缓存
                                    ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC);
                                    ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MEDIA_RECORED_LIKE_MUSIC, (Serializable)  mMusicAdapter.getData(), Constant.CACHE_TIME);
                                }
                            }
                        }
                        ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_MUSIC_FOLLOW_CHANGED);//通知让订阅者通知观察者刷新
                    }else{
                        ToastUtils.showCenterToast("取消收藏失败");
                    }
                }else{
                    ToastUtils.showCenterToast("取消收藏异常");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showCenterToast("取消收藏异常"+e.getMessage());
            }
        }
    }

    @Override
    public void showLikeResultError(String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 注册刷新界面事件，通常是结束了播放，需要还原列表状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //只处理与自己相关的事件
        if(null!=event&&TextUtils.equals(Constant.EVENT_UPDATA_MUSIC_PLAYER,event.getMessage())&&1==event.getType()){
            WindowVideoPlayer.releaseAllVideos();
            if(null!= mMusicAdapter){
                mMusicAdapter.initialListItem();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserverToMusic(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WindowVideoPlayer.releaseAllVideos();
    }

    /**
     * 仅处理刷新UI事件
     * @param poistion
     */
    @Override
    public void onItemClick(int poistion) {

    }

    /**
     * 收藏事件
     * @param data
     */
    @Override
    public void onLikeClick(MediaMusicCategoryList.DataBean data) {
        if(!getActivity().isFinishing()&&null!= mPresenter &&!mPresenter.isLikeIng()){
            showProgressDialog("操作中...",true);
            mPresenter.likeMusic(data.getId(),data.getIs_collect());
        }
    }

    /**
     * 查看关于本歌曲的详情事件
     * @param musicID
     */
    @Override
    public void onDetailsClick(String musicID) {

    }

    /**
     * 选中了某首音乐
     * @param data
     */
    @Override
    public void onSubmitMusic(MediaMusicCategoryList.DataBean data) {
        //先检查缓存文件是否是完整文件
        HttpProxyCacheServer proxy = VideoApplication.getProxy();
        File cacheFile = proxy.getCacheFile(data.getUrl());
        if(null!=cacheFile&&cacheFile.exists()&&cacheFile.isFile()){
            //完整的音乐文件
            if(Utils.isFileToMp3(cacheFile.getAbsolutePath())){
                if(null!=mMusicActivity&&!mMusicActivity.isFinishing()){
                    WindowVideoPlayer.releaseAllVideos();
                    mMusicActivity.onResultFilish(null==data?"0":data.getId(),cacheFile.getAbsolutePath());
                    return;
                }
            //需要下载
            }else{
                showDownloadProgress();
                downloadFile(data);
            }
        }
    }

    @Override
    public void onSubmitLocationMusic(MusicInfo musicPath) {

    }

    private void downloadFile(MediaMusicCategoryList.DataBean data) {
        if(!Utils.isCheckNetwork()){
            return;
        }
        this.mData=data;
        File file=new File(Constant.DOWNLOAD_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        new DownloadFileUtilTask(Constant.DOWNLOAD_PATH, new DownloadFileUtilTask.OnDownloadListener() {
            @Override
            public void onStartDownload() {

            }

            @Override
            public void onDownloadError(final String e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=mMusicActivity&&!mMusicActivity.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                            mRecordProgressDialog.dismiss();
                        }
                        ToastUtils.showCenterToast(e);
                    }
                });
            }

            @Override
            public void onDownloadProgress(int progress) {
                if(null!=mMusicActivity&&!mMusicActivity.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                    mRecordProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onWownloadFilish(File file) {
                WindowVideoPlayer.releaseAllVideos();
                if(null!=mMusicActivity&&!mMusicActivity.isFinishing()){
                    if(null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                        if(null!=file&&file.exists()&&file.isFile()){
                            mRecordProgressDialog.setProgress(100);
                            mRecordProgressDialog.setTipsMessage("下载完成");
                            mRecordProgressDialog.dismiss();
                            mRecordProgressDialog=null;
                            if(Utils.isFileToMp3(file.getAbsolutePath())){
                                mMusicActivity.onResultFilish(null==mData?"0":mData.getId(),file.getAbsolutePath());
                            }
                            return;
                        }
                        mRecordProgressDialog.dismiss();
                        mRecordProgressDialog=null;
                    }
                }
            }
        }).execute(data.getUrl());
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadProgress() {
        if(null== mRecordProgressDialog){
            mRecordProgressDialog = new RecordProgressDialog(getActivity());
            mRecordProgressDialog.setMode(RecordProgressDialog.SHOW_MODE1);
            mRecordProgressDialog.setOnDialogBackListener(new RecordProgressDialog.OnDialogBackListener() {
                @Override
                public void onBack() {

                }
            });
        }
        mRecordProgressDialog.setTipsMessage("下载中，请稍后...");
        mRecordProgressDialog.setProgress(0);
        mRecordProgressDialog.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    case Constant.OBSERVABLE_ACTION_MUSIC_FOLLOW_CHANGED:
                        isRefresh=true;
                        if(null!= mPresenter &&!mPresenter.isLikeList()&&null!=bindingView){
                            bindingView.swiperLayout.showLoadingProgress();
                        }
                        break;
                }
            }
        }
    }
}
