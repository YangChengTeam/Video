package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.TopicVideoListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.TopicVideoList;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentRecylerBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.mode.GridSpacesItemDecoration;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.TopicVideoContract;
import com.video.newqu.ui.presenter.TopicVideoPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/1 14:44
 * 话题列表视频
 */

public class TopicVideoListFragment extends BaseFragment<FragmentRecylerBinding,TopicVideoPresenter> implements  TopicVideoContract.View {

    private int mPage=0;
    private String mTopicID;
    private TopicVideoListAdapter mVideoListAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    public static TopicVideoListFragment newInstance(String topicID){
        TopicVideoListFragment fansListFragment=new TopicVideoListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_VIDEO_TOPIC_ID,topicID);
        fansListFragment.setArguments(bundle);
        return fansListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取出参数
        Bundle arguments = getArguments();
        if(null!=arguments) {
            mTopicID = arguments.getString(Constant.KEY_VIDEO_TOPIC_ID);
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
                loadTopicVideoList();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new TopicVideoPresenter(getActivity());
        mPresenter.attachView(this);
        if(!TextUtils.isEmpty(mTopicID)){
            mTopicID=Utils.slipTopic(mTopicID);
            initAdapter();
            bindingView.swiperefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(null!= mVideoListAdapter){
                        List<TopicVideoList.DataBean.VideoListBean> data = mVideoListAdapter.getData();
                        if(null!=data&&data.size()>0){
                            bindingView.swiperefreshLayout.setRefreshing(true);
                        }
                        mPage=0;
                        loadTopicVideoList();
                    }
                }
            },Constant.POST_DELAYED_ADD_DATA_TIME);
        }else{
            ToastUtils.showCenterToast("错误!");
            getActivity().finish();
        }
    }
    /**
     * 加载话题视频列表
     */
    private void loadTopicVideoList() {
        if(null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getTopicVideoList(VideoApplication.getLoginUserID(),mTopicID,mPage+"");
        }
    }

    @Override
    public void onDestroy() {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroy();
    }

    /**
     *初始化适配器
     */
    private void initAdapter() {
        List<TopicVideoList.DataBean.VideoListBean> cacheList= (List<TopicVideoList.DataBean.VideoListBean>)   ApplicationManager.getInstance().getCacheExample().getAsObject(mTopicID);
        mGridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL,false);
        bindingView.recyerView.setLayoutManager(mGridLayoutManager);
        bindingView.recyerView.addItemDecoration(new GridSpacesItemDecoration(3));
        mVideoListAdapter = new TopicVideoListAdapter(cacheList);
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyViewbindView.emptyView.showLoadingView();
                loadTopicVideoList();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<TopicVideoList.DataBean.VideoListBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>9&&null!=mPresenter&&!mPresenter.isLoading()){
                        bindingView.swiperefreshLayout.setRefreshing(false);
                        loadTopicVideoList();
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
        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getActivity(), "click_play");
                if(null!= mVideoListAdapter){
                    List<TopicVideoList.DataBean.VideoListBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>0){
                        //全屏
                        if(ConfigSet.getInstance().isPlayerModel()){
                            try{
                                FollowVideoList.DataBean followDataBean=new FollowVideoList.DataBean();
                                List<FollowVideoList.DataBean.ListsBean> videoListBeenList=new ArrayList<>();
                                for (TopicVideoList.DataBean.VideoListBean video : data) {
                                    FollowVideoList.DataBean.ListsBean videoListBean=new FollowVideoList.DataBean.ListsBean();
                                    videoListBean.setVideo_id(video.getVideo_id());
                                    videoListBean.setUser_id(video.getUser_id());
                                    videoListBean.setIs_interest(video.getIs_interest());
                                    videoListBean.setPath(video.getPath());
                                    videoListBean.setType(video.getType());
                                    videoListBean.setComment_times(video.getComment_times());
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
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_TOPIC_LIST);
                                    intent.putExtra(Constant.KEY_POISTION,position);
                                    intent.putExtra(Constant.KEY_PAGE,mPage);
                                    intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                                    intent.putExtra(Constant.KEY_JSON,json);
                                    intent.putExtra(Constant.KEY_TOPIC,mTopicID);
                                    startActivity(intent);
                                }
                            }catch (Exception e){

                            }
                            //单个
                        }else{
                            TopicVideoList.DataBean.VideoListBean videoListBean = data.get(position);
                            if(null!=videoListBean&&!TextUtils.isEmpty(videoListBean.getVideo_id())){
                                saveLocationHistoryList(videoListBean);
                                VideoDetailsActivity.start(getActivity(),videoListBean.getVideo_id(),videoListBean.getUser_id(),false);
                            }
                        }
                    }
                }
            }
        });
        bindingView.recyerView.setAdapter(mVideoListAdapter);
    }

    private void saveLocationHistoryList(final TopicVideoList.DataBean.VideoListBean data) {
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
                userLookVideoList.setVideoCommendCount(TextUtils.isEmpty(data.getComment_times())?"0":data.getComment_times());
                userLookVideoList.setVideoShareCount(TextUtils.isEmpty(data.getShare_times())?"0":data.getShare_times());
                userLookVideoList.setUserId(data.getUser_id());
                userLookVideoList.setVideoId(data.getVideo_id());
                userLookVideoList.setVideoCover(data.getCover());
                userLookVideoList.setUploadTime(data.getAdd_time());
                userLookVideoList.setAddTime(System.currentTimeMillis());
                userLookVideoList.setIs_interest(data.getIs_interest());
                userLookVideoList.setItemIndex(0);
                userLookVideoList.setIs_follow(data.getIs_follow());
                userLookVideoList.setVideoPath(data.getPath());
                userLookVideoList.setVideoPlayerCount(TextUtils.isEmpty(data.getPlay_times())?"0":data.getPlay_times());
                userLookVideoList.setVideoType(TextUtils.isEmpty(data.getType())?"2":data.getType());
                userLookVideoList.setDownloadPermiss(data.getDownload_permiss());
                ApplicationManager.getInstance().getUserPlayerDB().insertNewPlayerHistoryOfObject(userLookVideoList);
            }
        }.start();
    }

    //======================================加载数据回调==============================================
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }
    @Override
    public void complete() {}

    @Override
    public void showTopicVideoListFinlish(TopicVideoList data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未发现与此话题相关视频~",R.drawable.ic_list_empty_icon,false);
        if(null!=mVideoListAdapter) {
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if (1 == mPage) {
                mVideoListAdapter.setNewData(data.getData().getVideo_list());
                ApplicationManager.getInstance().getCacheExample().remove(mTopicID);
                ApplicationManager.getInstance().getCacheExample().put(mTopicID, (Serializable) data.getData().getVideo_list(), Constant.CACHE_TIME);
                //添加数据
            } else {
                mVideoListAdapter.addData(data.getData().getVideo_list());
            }
        }
    }

    @Override
    public void showTopicVideoListEmpty(String data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未发现与此话题相关视频~",R.drawable.ic_list_empty_icon,false);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(mTopicID);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    @Override
    public void showTopicVideoListError(String data) {
        if(1==mPage){
            bindingView.swiperefreshLayout.setRefreshing(false,-1);
        }
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreFail();
            List<TopicVideoList.DataBean.VideoListBean> dataList = mVideoListAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void showReportUserResult(String data) {}

    @Override
    public void showReportVideoResult(String data) {}

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
    public void onMessageEvent(ChangingViewEvent event) {
        if(null==event) return;
        if(Constant.FRAGMENT_TYPE_TOPIC_LIST!=event.getFragmentType())return;
        if(null!=mVideoListAdapter&&null!=mGridLayoutManager){
            mPage=event.getPage();
            if(event.isFixedPosition()){
                int poistion = event.getPoistion();
                if(null!= mVideoListAdapter.getData()&& mVideoListAdapter.getData().size()>(poistion-1)){
                    mGridLayoutManager.scrollToPosition(poistion);
                }
            }else{
                List<FollowVideoList.DataBean.ListsBean> listsBeanList = event.getListsBeanList();
                if(null!=listsBeanList&&listsBeanList.size()>0){
                    List<TopicVideoList.DataBean.VideoListBean> data =new ArrayList<>();
                    for (FollowVideoList.DataBean.ListsBean video : listsBeanList) {
                        TopicVideoList.DataBean.VideoListBean videoListBean=new TopicVideoList.DataBean.VideoListBean();
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
                        data.add(videoListBean);
                    }
                    mVideoListAdapter.setNewData(data);
                }
            }
        }
    }
}
