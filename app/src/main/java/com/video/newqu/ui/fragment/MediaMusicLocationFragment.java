package com.video.newqu.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.adapter.MediaLocationMusicAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.bean.MusicInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentLocationMusicBinding;
import com.video.newqu.databinding.ReEmptyMarginLayoutBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.model.MusicComparator;
import com.video.newqu.ui.activity.MediaMusicActivity;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.AudioUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.view.layout.MineDataChangeMarginView;
import com.video.newqu.view.widget.SideBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.xinqu.videoplayer.full.WindowVideoPlayer;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 本地音乐选择
 */

public class MediaMusicLocationFragment extends BaseFragment<FragmentLocationMusicBinding,MainPresenter> implements OnMediaMusicClickListener {

    private MediaLocationMusicAdapter mMusicAdapter;
    private boolean isScanIng=false;//是否正在刷新
    private boolean isRefresh=true;//是否需要刷新
    private HashMap<String, Integer> positionMap = new HashMap<>();
    private MediaMusicActivity mMusicActivity;
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMusicActivity = (MediaMusicActivity) context;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_location_music;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&!isScanIng&&null!=bindingView&&null!=mMusicAdapter&&null!=mEmptyViewbindView){
            mEmptyViewbindView.emptyView.showLoadingView();
            queryLocationMusic();
        }
    }

    private void initAdapter() {
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mMusicAdapter = new MediaLocationMusicAdapter(null,this);
        mMusicAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                bindingView.recyerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mMusicAdapter.loadMoreEnd();
                    }
                });
            }
        }, bindingView.recyerView);
        //加载中、数据为空、加载失败布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_margin_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new MineDataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyViewbindView.emptyView.showLoadingView();
                queryLocationMusic();
            }

            @Override
            public void onClickView(View v) {

            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mMusicAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        bindingView.recyerView.setAdapter(mMusicAdapter);

        bindingView.sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                bindingView.dialogText.setText(s);
                bindingView.sidebar.setView(bindingView.dialogText);
                if (positionMap.get(s) != null) {
                    int i = positionMap.get(s);
                    ((LinearLayoutManager) bindingView.recyerView.getLayoutManager()).scrollToPositionWithOffset(i + 1, 0);
                }
            }
        });
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 注册刷新界面事件，通常是结束了播放，需要还原列表状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //只处理与自己相关的事件
        if(null!=event&&TextUtils.equals(Constant.EVENT_UPDATA_MUSIC_PLAYER,event.getMessage())&&2==event.getType()){
            WindowVideoPlayer.releaseAllVideos();
            if(null!= mMusicAdapter){
                mMusicAdapter.initialListItem();
            }
        }
    }


    private void queryLocationMusic() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ToastUtils.showCenterToast("SD存储卡准备中");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡使用权限！");
            return;
        }

        new AsyncTask<Void, Void, List<MusicInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isScanIng=true;
            }

            @Override
            protected List<MusicInfo> doInBackground(final Void... unused) {
                ArrayList<MusicInfo> allSongs = AudioUtils.getAllSongs();
                Collections.sort(allSongs, new MusicComparator());
                for (int i = 0; i < allSongs.size(); i++) {
                    if (positionMap.get(allSongs.get(i).getPinyin()) == null)
                        positionMap.put(allSongs.get(i).getPinyin(), i);
                }

                return allSongs;
            }

            @Override
            protected void onPostExecute(List<MusicInfo> data) {
                isScanIng=false;
                if(null!=data&&data.size()>0){
                    isRefresh=false;
                }
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未扫描到本机音乐",R.drawable.iv_work_video_empty,false);
                if(null!= mMusicAdapter){
                    mMusicAdapter.setNewData(data);
                }
            }
        }.execute();
    }

    @Override
    public void onItemClick(int poistion) {

    }

    @Override
    public void onLikeClick(MediaMusicCategoryList.DataBean musicID) {

    }

    @Override
    public void onDetailsClick(String musicID) {

    }

    @Override
    public void onSubmitMusic(MediaMusicCategoryList.DataBean musicPath) {

    }

    @Override
    public void onSubmitLocationMusic(MusicInfo data) {
        WindowVideoPlayer.releaseAllVideos();
        if(null!=data&&!TextUtils.isEmpty(data.getFileUrl())){
            if(null!=mMusicActivity&&!mMusicActivity.isFinishing()){
                mMusicActivity.onResultFilish(null==data?"0":data.getType(),data.getFileUrl());
            }
        }
    }
}
