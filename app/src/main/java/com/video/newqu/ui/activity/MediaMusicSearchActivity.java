package com.video.newqu.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.danikula.videocache.HttpProxyCacheServer;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.MediaMusicRecommendAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.bean.MusicInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityMediaMusicSearchBinding;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.manager.DownloadFileUtilTask;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.ui.contract.MediaMusicSearchContract;
import com.video.newqu.ui.dialog.RecordProgressDialog;
import com.video.newqu.ui.presenter.MediaMusicSearchPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.InputTools;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import com.xinqu.videoplayer.full.WindowVideoPlayer;

/**
 * TinyHung@outlook.com
 * 2017-06-07 19:37
 * 音乐搜索
 */

public class MediaMusicSearchActivity extends BaseActivity<ActivityMediaMusicSearchBinding> implements TextWatcher, TextView.OnEditorActionListener, MediaMusicSearchContract.View
        ,OnMediaMusicClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private MediaMusicSearchPresenter mMediaMusicSearchPresenter;
    private Animation mInputAnimation;
    //刚才搜索的关键字
    private String cureenSearchKey;
    private int mPage=0;
    private int mPageSize=10;
    List<MediaMusicCategoryList.DataBean> mMediaMusicInfoList=null;
    private MediaMusicRecommendAdapter mMediaMusicRecommendAdapter;
    private RecordProgressDialog mRecordProgressDialog;
    private MediaMusicCategoryList.DataBean mData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_music_search);
        showToolBar(false);
        findViewById(R.id.view_state_bar).setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
        StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        mMediaMusicSearchPresenter = new MediaMusicSearchPresenter(this);
        mMediaMusicSearchPresenter.attachView(this);
        initAdapter();
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //搜索
                    case R.id.tv_search:
                        searchMusic();
                        break;
                    //清空输入框
                    case R.id.search_iv_delete:
                        bindingView.searchEtInput.setText("");
                        break;
                    //返回
                    case R.id.iv_back:
                        onBackPressed();
                        break;
                }
            }
        };
        bindingView.tvSearch.setOnClickListener(onClickListener);
        bindingView.searchIvDelete.setOnClickListener(onClickListener);
        bindingView.ivBack.setOnClickListener(onClickListener);
        bindingView.searchEtInput.addTextChangedListener(this);
        bindingView.searchEtInput.setOnEditorActionListener(this);

    }

    private void searchMusic() {
        String text = bindingView.searchEtInput.getText().toString().trim();
        if(!TextUtils.isEmpty(text)){
            cureenSearchKey=text;
            mPage=0;
            if(null!=mMediaMusicRecommendAdapter){
                mMediaMusicRecommendAdapter.setNewData(null);
            }
            if(null!=mMediaMusicInfoList){
                mMediaMusicInfoList.clear();
            }
            searchResylt();
        }else{
            if(null!=mInputAnimation) bindingView.searchEtInput.startAnimation(mInputAnimation);
            ToastUtils.showCenterToast("请输入要搜索的关键字");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mMediaMusicSearchPresenter){
            mMediaMusicSearchPresenter.detachView();
        }
        mInputAnimation=null;
        WindowVideoPlayer.releaseAllVideos();
        Runtime.getRuntime().gc();
    }



    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MediaMusicSearchActivity.this, LinearLayoutManager.VERTICAL, false);
        bindingView.recyerView.setLayoutManager(linearLayoutManager);
        mMediaMusicRecommendAdapter = new MediaMusicRecommendAdapter(null,this);
        mMediaMusicRecommendAdapter.setOnLoadMoreListener(this);
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mMediaMusicRecommendAdapter.setEmptyView(emptyViewbindView.getRoot());
        emptyViewbindView.ivItemIcon.setImageResource(R.drawable.iv_work_video_empty);
        emptyViewbindView.tvItemName.setText("可以搜索音乐名称、作者名称");
        bindingView.recyerView.setAdapter(mMediaMusicRecommendAdapter);
    }



    @Override
    public void onLoadMoreRequested() {
        mMediaMusicRecommendAdapter.setEnableLoadMore(true);
        if(!TextUtils.isEmpty(cureenSearchKey)&&null!=mMediaMusicSearchPresenter&&!mMediaMusicSearchPresenter.isSearching()){
            searchResylt();
        }
    }


    /**
     * 开始搜索
     */
    private void searchResylt() {
        InputTools.closeKeybord(bindingView.searchEtInput);//关闭软键盘
        showProgressDialog("搜索中...",true);
        mPage++;
        mMediaMusicSearchPresenter.getMediaSearchResult(cureenSearchKey,mPage,mPageSize);
    }

    @Override
    protected void onPause() {
        WindowVideoPlayer.releaseAllVideos();
        if(null!=mMediaMusicRecommendAdapter){
            mMediaMusicRecommendAdapter.initialListItem();
        }
        super.onPause();
    }


    @Override
    public void initData() {

    }


    /**
     * 为适配器刷新新数据
     */
    private void upDataNewDataAdapter() {
        if(null!= mMediaMusicRecommendAdapter) mMediaMusicRecommendAdapter.setNewData(mMediaMusicInfoList);
    }

    /**
     * 为适配器增加数据
     */
    private void updataAddDataAdapter() {
        if(null!= mMediaMusicRecommendAdapter) mMediaMusicRecommendAdapter.addData(mMediaMusicInfoList);
    }


    public void onResultFilish(String musicID, String musicPath) {
        Intent intent=new Intent();
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID,musicID);
        intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH,musicPath);
        setResult(Constant.MEDIA_START_MUSIC_CATEGORY_RESULT_CODE,intent);
        finish();
    }



    /**
     * 输入框的监听
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence key, int start, int before, int count) {
        if(!TextUtils.isEmpty(key)&&key.length()>0){
            bindingView.searchIvDelete.setVisibility(View.VISIBLE);
        }else{
            bindingView.searchIvDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {
        closeProgressDialog();
    }


    /**
     * 对输入法的回车键监听
     * @param v
     * @param actionId
     * @param event
     * @return
     */

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
            searchMusic();
            return true;
        }
        return false;
    }



    @Override
    public void showMediaSearchList(List<MediaMusicCategoryList.DataBean> data) {
        if(null!= mMediaMusicRecommendAdapter&&!this.isFinishing()){
            closeProgressDialog();
            mMediaMusicRecommendAdapter.loadMoreComplete();
            //更新适配器数据为全新
            if(1==mPage){//替换最新缓存
                if(null!=mMediaMusicInfoList){
                    mMediaMusicInfoList.clear();
                }
                mMediaMusicInfoList=data;
                upDataNewDataAdapter();
                //仅增加新数据
            }else{
                mMediaMusicInfoList=data;
                updataAddDataAdapter();
            }
        }
    }

    @Override
    public void showMediaSearchListEmpty(String data) {

        if(null!= mMediaMusicRecommendAdapter &&!this.isFinishing()){
            closeProgressDialog();
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    mMediaMusicRecommendAdapter.loadMoreEnd();//没有更多的数据了
                }
            });
            if(mPage==1){
                if(null!= mMediaMusicInfoList){
                    mMediaMusicInfoList.clear();
                }
                upDataNewDataAdapter();
            }
            if(mPage>0){
                mPage--;
            }
        }
    }

    @Override
    public void showMediaSearchListError(String data) {

        if(null!= mMediaMusicRecommendAdapter&&!this.isFinishing()){
            closeProgressDialog();
            ToastUtils.showCenterToast(data);
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    mMediaMusicRecommendAdapter.loadMoreFail();
                }
            });
            if(mPage>0){
                mPage--;
            }
        }
    }

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
                            if(null!=mMediaMusicRecommendAdapter){
                                List<MediaMusicCategoryList.DataBean> musicList = mMediaMusicRecommendAdapter.getData();
                                if(null!=musicList&&musicList.size()>0){
                                    int poistion=0;
                                    for (int i = 0; i < musicList.size(); i++) {
                                        if(TextUtils.equals(music_id,musicList.get(i).getId())){
                                            musicList.get(i).setIs_collect(Integer.parseInt(jsonObject.getString("res")));
                                            poistion=i;
                                            break;
                                        }
                                    }
                                    //刷新单个条目
                                    mMediaMusicRecommendAdapter.notifyItemChanged(poistion);
                                }
                            }
                        }
                        ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_MUSIC_FOLLOW_CHANGED);//通知让订阅者通知观察者刷新
                    }else{
                        ToastUtils.showCenterToast("收藏失败");
                    }
                }else{
                    ToastUtils.showCenterToast("收藏异常");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showCenterToast("收藏异常"+e.getMessage());
            }
        }
    }

    @Override
    public void showLikeResultError(String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void onItemClick(int poistion) {

    }

    @Override
    public void onLikeClick(MediaMusicCategoryList.DataBean data) {
        if(!this.isFinishing()&&null!=mMediaMusicSearchPresenter&&!mMediaMusicSearchPresenter.isLikeIng()){
            showProgressDialog("操作中...",true);
            mMediaMusicSearchPresenter.likeMusic(data.getId(),data.getIs_collect());
        }
    }

    @Override
    public void onDetailsClick(String musicID) {

    }

    @Override
    public void onSubmitMusic(MediaMusicCategoryList.DataBean data) {
        //先检查缓存文件是否是完整文件
        HttpProxyCacheServer proxy = VideoApplication.getProxy();
        File cacheFile = proxy.getCacheFile(data.getUrl());
        if(null!=cacheFile&&cacheFile.exists()&&cacheFile.isFile()){
            //完整的音乐文件
            if(Utils.isFileToMp3(cacheFile.getAbsolutePath())){
                if(!this.isFinishing()){
                    WindowVideoPlayer.releaseAllVideos();
                    onResultFilish(null==data?"0":data.getId(),cacheFile.getAbsolutePath());
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!MediaMusicSearchActivity.this.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                            mRecordProgressDialog.dismiss();
                        }
                        ToastUtils.showCenterToast(e);
                    }
                });
            }

            @Override
            public void onDownloadProgress(int progress) {
                if(!MediaMusicSearchActivity.this.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                    mRecordProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onWownloadFilish(File file) {
                WindowVideoPlayer.releaseAllVideos();
                if(!MediaMusicSearchActivity.this.isFinishing()){
                    if(null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                        if(null!=file&&file.exists()&&file.isFile()){
                            mRecordProgressDialog.setProgress(100);
                            mRecordProgressDialog.setTipsMessage("下载完成");
                            mRecordProgressDialog.dismiss();
                            mRecordProgressDialog=null;
                            if(Utils.isFileToMp3(file.getAbsolutePath())){
                                onResultFilish(null==mData?"0":mData.getId(),file.getAbsolutePath());
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
            mRecordProgressDialog = new RecordProgressDialog(this);
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
}
