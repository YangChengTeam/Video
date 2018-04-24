package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.HomeTopicAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.FindVideoListInfo;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentRecylerBinding;
import com.video.newqu.listener.HomeTopicItemClickListener;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.HomeTopicContract;
import com.video.newqu.ui.presenter.HomeTopicPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/24 9:16
 * 话题
 */

public class HomeTopicFragment extends BaseFragment<FragmentRecylerBinding,HomeTopicPresenter> implements HomeTopicContract.View,HomeTopicItemClickListener {

    private int mPage =0;
    private int pageSize=10;
    private boolean isRefresh=true;
    private HomeTopicAdapter mVideoListAdapter;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyler;
    }

    @Override
    protected void initViews() {
        //刷新监听器
        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage =0;
                loadVideoList();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new HomeTopicPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!= mPresenter &&!mPresenter.isLoading()){
            List<FindVideoListInfo.DataBean> data = mVideoListAdapter.getData();
            if(null==data||data.size()<=0){
                mPage=0;
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                loadVideoList();
            }else{
                bindingView.swiperefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bindingView.swiperefreshLayout.setRefreshing(true);
                        mPage=0;
                        loadVideoList();
                    }
                },Constant.POST_DELAYED_ADD_DATA_TIME);
            }
        }
    }

    /**
     * 加载数据
     */
    private void loadVideoList() {
        mPage++;
        mPresenter.getHomeTopicDataList(VideoApplication.getLoginUserID(), mPage +"",pageSize+"");
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<FindVideoListInfo.DataBean> cacheList= (List<FindVideoListInfo.DataBean>)  ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_FIND_VIDEO_LIST);//读取缓存
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mVideoListAdapter = new HomeTopicAdapter(cacheList,this);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<FindVideoListInfo.DataBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>3&&null!= mPresenter &&!mPresenter.isLoading()){
                        bindingView.swiperefreshLayout.setRefreshing(false);
                        mVideoListAdapter.setEnableLoadMore(true);
                        loadVideoList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mVideoListAdapter.loadMoreFail();//加载失败
                                }else{
                                    mVideoListAdapter.loadMoreEnd();//加载为空
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
                loadVideoList();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        bindingView.recyerView.setAdapter(mVideoListAdapter);
    }

    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
        getActivity().startActivity(intent);
    }

    /**
     * 点击了查看更多
     * @param topicID
     */
    @Override
    public void onGroupItemClick(String topicID) {
        if(!TextUtils.isEmpty(topicID)){
            MobclickAgent.onEvent(getActivity(), "click_topic_cate");
            startTargetActivity(Constant.KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT,topicID,VideoApplication.getLoginUserID(),0,topicID);
        }
    }

    /**
     * 点击了子条目中的某个poistion
     * @param groupPoistion 父条目
     * @param childPoistion 子条目
     */
    @Override
    public void onChildItemClick(String topicID,int groupPoistion, int childPoistion) {
        MobclickAgent.onEvent(getActivity(), "click_play");
        if(null!= mVideoListAdapter){
            List<FindVideoListInfo.DataBean> data = mVideoListAdapter.getData();
            if(null!=data&&data.size()>0){
                //取出父条目元素
                FindVideoListInfo.DataBean dataBean = data.get(groupPoistion);
                if(null!=dataBean){
                    List<FindVideoListInfo.DataBean.VideosBean> videos = dataBean.getVideos();
                    //携带首页某个父元素中的所有子元素数据到播放器界面
                    if(null!=videos&&videos.size()>0){
                        //全屏
                        if(ConfigSet.getInstance().isPlayerModel()){
                            //封装
                            try{
                                FollowVideoList.DataBean followDataBean=new FollowVideoList.DataBean();
                                List<FollowVideoList.DataBean.ListsBean> videoListBeenList=new ArrayList<>();
                                for (FindVideoListInfo.DataBean.VideosBean video : videos) {
                                    FollowVideoList.DataBean.ListsBean videoListBean=new FollowVideoList.DataBean.ListsBean();
                                    videoListBean.setVideo_id(video.getVideo_id());
                                    videoListBean.setUser_id(video.getUser_id());
                                    videoListBean.setIs_interest(video.getIs_interest());
                                    videoListBean.setPath(video.getPath());
                                    videoListBean.setType(video.getType());
                                    videoListBean.setComment_times(video.getComment_count());
                                    videoListBean.setIs_follow(video.getIs_follow());
                                    videoListBean.setCollect_times(video.getCollect_times());
                                    videoListBean.setCover(video.getCover());
                                    videoListBean.setAdd_time(video.getAdd_time());
                                    videoListBean.setDesp(video.getDesp());
                                    videoListBean.setNickname(video.getNickname());
                                    videoListBean.setLogo(video.getLogo());
                                    videoListBean.setPlay_times(video.getPlay_times());
                                    videoListBean.setShare_times(video.getShare_times());
                                    videoListBean.setDownload_permiss(video.getDownload_permiss());
                                    videoListBean.setCate(video.getCate());
                                    videoListBeenList.add(videoListBean);
                                }
                                followDataBean.setLists(videoListBeenList);
                                FollowVideoList followVideoList=new FollowVideoList();
                                followVideoList.setData(followDataBean);
                                String json = JSONArray.toJSON(followVideoList).toString();

                                if(!TextUtils.isEmpty(json)){
                                    Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_HOME_TOPIC);
                                    intent.putExtra(Constant.KEY_POISTION,childPoistion);
                                    intent.putExtra(Constant.KEY_PAGE,1);
                                    intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                                    intent.putExtra(Constant.KEY_JSON,json);
                                    intent.putExtra(Constant.KEY_TOPIC,topicID);
                                    startActivity(intent);
                                    return;
                                }
                            }catch (Exception e){
                                ToastUtils.showCenterToast("播放错误"+e.getMessage());
                            }
                        //单个
                        }else{
                            FindVideoListInfo.DataBean.VideosBean videosBean = videos.get(childPoistion);
                            if(null!=videosBean&&!TextUtils.isEmpty(videosBean.getId())){
                                saveLocationHistoryList(videosBean);
                                VideoDetailsActivity.start(getActivity(),videosBean.getId(),videosBean.getUser_id(),false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void saveLocationHistoryList(final FindVideoListInfo.DataBean.VideosBean data) {
        if(null==data) return;
        new Thread(){
            @Override
            public void run() {
                super.run();
                UserPlayerVideoHistoryList userLookVideoList=new UserPlayerVideoHistoryList();
                userLookVideoList.setUserName(TextUtils.isEmpty(data.getNickname())?"火星人":data.getNickname());
                userLookVideoList.setUserSinger("该宝宝没有个性签名");
                userLookVideoList.setUserCover(data.getLogo());
                userLookVideoList.setVideoDesp(data.getDesp());
                userLookVideoList.setVideoLikeCount(TextUtils.isEmpty(data.getCollect_times())?"0":data.getCollect_times());
                userLookVideoList.setVideoCommendCount(TextUtils.isEmpty(data.getComment_count())?"0":data.getComment_count());
                userLookVideoList.setVideoShareCount(TextUtils.isEmpty(data.getShare_times())?"0":data.getShare_times());
                userLookVideoList.setUserId(data.getUser_id());
                userLookVideoList.setVideoId(data.getVideo_id());
                userLookVideoList.setItemIndex(0);
                userLookVideoList.setVideoCover(data.getCover());
                userLookVideoList.setUploadTime(data.getAdd_time());
                userLookVideoList.setAddTime(System.currentTimeMillis());
                userLookVideoList.setIs_interest(data.getIs_interest());
                userLookVideoList.setIs_follow(data.getIs_follow());
                userLookVideoList.setVideoPath(data.getPath());
                userLookVideoList.setVideoPlayerCount(TextUtils.isEmpty(data.getPlay_times())?"0":data.getPlay_times());
                userLookVideoList.setVideoType(TextUtils.isEmpty(data.getType())?"2":data.getType());
                userLookVideoList.setDownloadPermiss(data.getDownload_permiss());
                ApplicationManager.getInstance().getUserPlayerDB().insertNewPlayerHistoryOfObject(userLookVideoList);
            }
        }.start();
    }

    /**
     * 加载发现列表成功
     * @param data
     */

    @Override
    public void showHomeTopicDataList(FindVideoListInfo data) {
        isRefresh=false;
        bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有发现视频，下拉刷新试试看~",R.drawable.iv_fans_empty,false);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mVideoListAdapter.setNewData(data.getData());
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_FIND_VIDEO_LIST);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_FIND_VIDEO_LIST, (Serializable) data.getData(), Constant.CACHE_TIME);
                //添加数据
            }else{
                mVideoListAdapter.addData(data.getData());
            }
        }
    }

    /**
     * 加载发现列表为空
     * @param data
     */
    @Override
    public void showHomeTopicDataEmpty(String data) {
        isRefresh=false;
        bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有发现视频，下拉刷新试试看~",R.drawable.iv_work_video_empty,false);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_FIND_VIDEO_LIST);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 加载发现失败
     * @param data
     */
    @Override
    public void showHomeTopicDataError(String data) {
        if(1==mPage){
            bindingView.swiperefreshLayout.setRefreshing(false,-1);
        }
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreFail();
            List<FindVideoListInfo.DataBean> dataList = mVideoListAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void showErrorView() {
    }

    @Override
    public void complete() {

    }

    @Override
    public void onDestroy() {
        if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroy();
    }

    /**
     * 来自外界的刷新命令
     */
    public void fromMainUpdata() {
        if(null!=mVideoListAdapter&&null!= mPresenter){
            if(!mPresenter.isLoading()){
                List<FindVideoListInfo.DataBean> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>0){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            bindingView.recyerView.scrollToPosition(0);
                            bindingView.swiperefreshLayout.setRefreshing(true);
                        }
                    });
                }else{
                    if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                }
                mPage=0;
                loadVideoList();
            }else{
                showErrorToast(null,null,"刷新太频繁了");
            }
        }else{
            showErrorToast(null,null,"刷新错误!");
        }
    }
}
