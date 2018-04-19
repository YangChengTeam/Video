package com.video.newqu.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentRecylerBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.listener.OnMediaMusicClickListener;
import com.video.newqu.manager.DownloadFileUtilTask;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.contract.MediaMusicCategoryListContract;
import com.video.newqu.ui.dialog.RecordProgressDialog;
import com.video.newqu.ui.presenter.MediaMusicCategoryListPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.Serializable;
import java.util.List;


/**
 * TinyHung@outlook.com
 * 2017/11/10 14:44
 * 音乐分类下的列表
 */

public class MediaMusicCategroyListFragment extends BaseLightWeightFragment<FragmentRecylerBinding,MediaMusicCategoryListPresenter> implements OnMediaMusicClickListener, MediaMusicCategoryListContract.View {

    private int mPage=0;
    private int mPageSize=10;
    private String mMusicCatrgoryID;
    private MediaMusicRecommendAdapter mMusicAdapter;
    private ContentFragmentActivity mActivity;
    private RecordProgressDialog mRecordProgressDialog;
    private MediaMusicCategoryList.DataBean mData;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ContentFragmentActivity) context;
    }

    public static MediaMusicCategroyListFragment newInstance(String musicCategoryID){
        MediaMusicCategroyListFragment fansListFragment=new MediaMusicCategroyListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.MEDIA_KEY_MUSIC_CATEGORY_ID,musicCategoryID);
        fansListFragment.setArguments(bundle);
        return fansListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取出参数
        Bundle arguments = getArguments();
        if(null!=arguments) {
            mMusicCatrgoryID = arguments.getString(Constant.MEDIA_KEY_MUSIC_CATEGORY_ID);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyler;
    }

    @Override
    protected void initViews() {
        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                loadCategoryMusicListList();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!TextUtils.isEmpty(mMusicCatrgoryID)){
            mPresenter = new MediaMusicCategoryListPresenter(getActivity());
            mPresenter.attachView(this);
            initAdapter();
            List<MediaMusicCategoryList.DataBean> data = mMusicAdapter.getData();
            if(null==data||data.size()<=0){
                bindingView.swiperefreshLayout.setRefreshing(true);
                mPage=0;
                loadCategoryMusicListList();
            }
        }else{
            ToastUtils.showCenterToast("错误!");
            getActivity().finish();
        }
    }


    /**
     * 加载音乐分类下的音乐列表
     */
    private void loadCategoryMusicListList() {
        if(null!= mPresenter &&!mPresenter.isHomeLoading()){
            mPage++;
            mPresenter.getCategoryMusicList(mMusicCatrgoryID,mPage,mPageSize);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        WindowVideoPlayer.releaseAllVideos();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroyView();
    }

    /**
     *初始化适配器
     */
    private void initAdapter() {
        List<MediaMusicCategoryList.DataBean> cacheList=(List<MediaMusicCategoryList.DataBean>)  ApplicationManager.getInstance().getCacheExample().getAsObject("category_"+mMusicCatrgoryID);
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        bindingView.recyerView.setHasFixedSize(true);
        mMusicAdapter = new MediaMusicRecommendAdapter(cacheList,this);
        mMusicAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mMusicAdapter){
                    List<MediaMusicCategoryList.DataBean> data = mMusicAdapter.getData();
                    if(null!= data && data.size()>=10&&null!=mPresenter&&!mPresenter.isHomeLoading()){
                        bindingView.swiperefreshLayout.setRefreshing(false);
                        mMusicAdapter.setEnableLoadMore(true);
                        loadCategoryMusicListList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mMusicAdapter.loadMoreFail();//加载失败
                                }else{
                                    mMusicAdapter.loadMoreEnd();//加载为空
                                }
                            }
                        });
                    }
                }

            }
        }, bindingView.recyerView);
        //占位布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyViewbindView.emptyView.showLoadingView();
                loadCategoryMusicListList();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mMusicAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        bindingView.recyerView.setAdapter(mMusicAdapter);
    }



    //==========================================点击事件=============================================
    /**
     * 条目点击事件
     * @param position
     */
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLikeClick(MediaMusicCategoryList.DataBean data) {
        if(!getActivity().isFinishing()&&null!= mPresenter &&!mPresenter.isLikeIng()){
            showProgressDialog("操作中...",true);
            mPresenter.likeMusic(data.getId(),data.getIs_collect());
        }
    }

    @Override
    public void onDetailsClick(String musicID) {

    }

    @Override
    public void onSubmitMusic(MediaMusicCategoryList.DataBean musicPath) {
        //先检查缓存文件是否是完整文件
        HttpProxyCacheServer proxy = VideoApplication.getProxy();
        File cacheFile = proxy.getCacheFile(musicPath.getUrl());
        if(null!=cacheFile&&cacheFile.exists()&&cacheFile.isFile()){
            //完整的音乐文件
            if(Utils.isFileToMp3(cacheFile.getAbsolutePath())){
                if(null!=mActivity&&!mActivity.isFinishing()){
                    WindowVideoPlayer.releaseAllVideos();
                    mActivity.onResultFilish(null==musicPath?"0":musicPath.getId(),cacheFile.getAbsolutePath());
                }
                return;
                //需要下载
            }else{
                showDownloadProgress();
                downloadFile(musicPath);
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
                        if(null!=mActivity&&!mActivity.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                            mRecordProgressDialog.dismiss();
                        }
                        ToastUtils.showCenterToast(e);
                    }
                });
            }

            @Override
            public void onDownloadProgress(int progress) {
                if(null!=mActivity&&!mActivity.isFinishing()&&null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                    mRecordProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onWownloadFilish(File file) {
                WindowVideoPlayer.releaseAllVideos();
                if(null!=mActivity&&!mActivity.isFinishing()){
                    if(null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
                        if(null!=file&&file.exists()&&file.isFile()){
                            mRecordProgressDialog.setProgress(100);
                            mRecordProgressDialog.setTipsMessage("下载完成");
                            mRecordProgressDialog.dismiss();
                            mRecordProgressDialog=null;
                            if(Utils.isFileToMp3(file.getAbsolutePath())){
                                mActivity.onResultFilish(null==mData?"0":mData.getId(),file.getAbsolutePath());
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




    //======================================加载数据回调==============================================

    @Override
    public void showErrorView() {
        closeProgressDialog();
    }


    @Override
    public void complete() {

    }

    @Override
    public void showCategoryMusicList(List<MediaMusicCategoryList.DataBean> data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("该分类下暂无数据~",R.drawable.iv_work_video_empty,false);
        if(null!=mMusicAdapter){
            mMusicAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                WindowVideoPlayer.releaseAllVideos();
                mMusicAdapter.setNewData(data);
                ApplicationManager.getInstance().getCacheExample().remove("category_"+mMusicCatrgoryID);
                ApplicationManager.getInstance().getCacheExample().put("category_"+mMusicCatrgoryID, (Serializable) data, Constant.CACHE_STICKER_TIME);
            }else{
                mMusicAdapter.addData(data);
            }
        }
    }

    @Override
    public void showCategoryMusicEmpty(String data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("该分类下暂无数据~",R.drawable.iv_work_video_empty,false);
        if(null!=mMusicAdapter){
            mMusicAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mMusicAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove("category_"+mMusicCatrgoryID);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    @Override
    public void showCategoryMusicError(String data) {
        if(1==mPage){
            if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false,-1);
        }
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
                                    //刷新单个条目
                                    mMusicAdapter.notifyItemChanged(poistion);
                                    //替换最新缓存
                                    ApplicationManager.getInstance().getCacheExample().remove("category_"+mMusicCatrgoryID);
                                    ApplicationManager.getInstance().getCacheExample().put("category_"+mMusicCatrgoryID, (Serializable) musicList, Constant.CACHE_TIME);
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
     * 订阅播放结果，以刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(null!=event&&TextUtils.equals(Constant.EVENT_UPDATA_MUSIC_PLAYER,event.getMessage())&&-1==event.getType()){
            WindowVideoPlayer.releaseAllVideos();
            if(null!= mMusicAdapter){
                mMusicAdapter.initialListItem();
            }
        }
    }
}
