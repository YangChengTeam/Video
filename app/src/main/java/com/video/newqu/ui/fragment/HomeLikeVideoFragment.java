package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.UserVideoListAdapter;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.MineFragmentRecylerBinding;
import com.video.newqu.databinding.ReEmptyMarginLayoutBinding;
import com.video.newqu.listener.OnUserVideoListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.model.RecyclerViewSpacesItem;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.FollowListContract;
import com.video.newqu.ui.presenter.FollowListPresenter;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.MineDataChangeMarginView;
import com.video.newqu.view.widget.SwipeLoadingProgress;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * TinyHung@outlook.com
 * 2017-05-24 17:31
 * 用户收藏的视频
 */

public class HomeLikeVideoFragment extends BaseLightWeightFragment<MineFragmentRecylerBinding,FollowListPresenter> implements FollowListContract.View,OnUserVideoListener, Observer {

    private int mPage=0;
    private int mPageSize=9;
    private UserVideoListAdapter mVideoListAdapter;
    private boolean isRefresh=true;
    private GridLayoutManager mGridLayoutManager;
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    @Override
    protected void initViews() {
        bindingView.swiperLayout.setOnSwipeProgressEndListener(new SwipeLoadingProgress.OnSwipeProgressEndListener() {
            @Override
            public void onShowFinlish() {
                mPage=0;
                loadFollowVideoList();
            }

            @Override
            public void onHideFinlish() {

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.mine_fragment_recyler;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter = new FollowListPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        ApplicationManager.getInstance().addObserver(this);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=mVideoListAdapter&&null!=VideoApplication.getInstance().getUserData()&&null!=bindingView&&null!= mPresenter &&!mPresenter.isLoading()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null==data||data.size()<=0){
                        //这个时候已经是在加载中的状态了
                        mPage=0;
                        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                        loadFollowVideoList();
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
        List<FollowVideoList.DataBean.ListsBean> cacheListsBeans= (List<FollowVideoList.DataBean.ListsBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        bindingView.recyerView.setLayoutManager(mGridLayoutManager);
        bindingView.recyerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1)));
        bindingView.recyerView.setHasFixedSize(true);
        mVideoListAdapter = new UserVideoListAdapter(cacheListsBeans,2,this);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>8){
                        mVideoListAdapter.setEnableLoadMore(true);
                        loadFollowVideoList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mVideoListAdapter.loadMoreFail();//模拟加载失败
                                }else{
                                    mVideoListAdapter.loadMoreEnd();//模拟加载完成
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
                loadFollowVideoList();
            }

            @Override
            public void onClickView(View v) {
                MainActivity  activity = (MainActivity) getActivity();
                if(null!=activity&&!activity.isFinishing()){
                    activity.currentHomeFragmentChildItemView(0,2);
                }
            }
        });
        mEmptyViewbindView.emptyView.setBtnText("去逛逛");
        mEmptyViewbindView.emptyView.showLoadingView();
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getActivity(), "click_play");
                if(null!=mVideoListAdapter){
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null!= data && data.size()>0){
                        //全屏
                        if(ConfigSet.getInstance().isPlayerModel()){
                            //改成获取当前视频的
                            try{
                                FollowVideoList.DataBean dataBean=new FollowVideoList.DataBean();
                                dataBean.setLists(data);
                                FollowVideoList followVideoList=new FollowVideoList();
                                followVideoList.setData(dataBean);
                                String json = JSONArray.toJSON(followVideoList).toString();
                                if(!TextUtils.isEmpty(json)){
                                    Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_LIKE);
                                    intent.putExtra(Constant.KEY_POISTION,position);
                                    intent.putExtra(Constant.KEY_PAGE,mPage);
                                    intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                                    intent.putExtra(Constant.KEY_JSON,json);
                                    startActivity(intent);
                                    return;
                                }
                            }catch (Exception e){
                                ToastUtils.showCenterToast("播放错误"+e.getMessage());
                            }
                            //单个
                        }else{
                            FollowVideoList.DataBean.ListsBean listsBean = data.get(position);
                            if(null!=listsBean&&!TextUtils.isEmpty(listsBean.getVideo_id())){
                                saveLocationHistoryList(listsBean);
                                VideoDetailsActivity.start(getActivity(),listsBean.getVideo_id(),listsBean.getUser_id(),false);
                            }
                        }
                    }
                }
            }
        });
        bindingView.recyerView.setAdapter(mVideoListAdapter);
    }

    /**
     * 获取收藏的视列表
     */
    private void loadFollowVideoList() {
        if(null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getFollowVideoList(VideoApplication.getLoginUserID(),mPage+"",mPageSize+"");
        }
    }

    //=========================================加载数据回调==========================================
    /**
     * 获取收藏列表成功
     * @param data
     */
    @Override
    public void showFollowVideoList(FollowVideoList data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("点赞过的视频会出现在这里",R.drawable.iv_fans_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mVideoListAdapter.setNewData(data.getData().getLists());
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST, (Serializable) data.getData().getLists(), Constant.CACHE_TIME);
                //添加数据
            }else{
                mVideoListAdapter.addData(data.getData().getLists());
            }
        }
    }

    /**
     *获取收藏列表为空,如果当前界面还有数据，就清空数据再刷新界面
     * @param data
     */
    @Override
    public void showFollowVideoListEmpty(String data) {
        isRefresh=false;
        bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("点赞过的视频会出现在这里",R.drawable.iv_fans_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 获取收藏列表失败
     * @param data
     */
    @Override
    public void showFollowVideoListError(String data) {
        bindingView.swiperLayout.hideLoadProgress();
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreFail();
            List<FollowVideoList.DataBean.ListsBean> dataList = mVideoListAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 收藏视频回调
     * @param data
     */
    @Override
    public void showFollowVideoResult(String data) {
        closeProgressDialog();
        if(TextUtils.isEmpty(data)){
            return;
        }
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(jsonObject.length()>0&&1==jsonObject.getInt("code")){
                int poistion=0;
                //取消收藏成功
                if(TextUtils.equals(Constant.PRICE_UNSUCCESS,jsonObject.getString("msg"))){
                    String  videoID= new JSONObject(jsonObject.getString("data")).getString("video_id");
                    if(!TextUtils.isEmpty(videoID)){
                        List<FollowVideoList.DataBean.ListsBean> videoData = mVideoListAdapter.getData();
                        if(null!=videoData&&videoData.size()>0){
                            for (int i = 0; i < videoData.size(); i++) {
                                FollowVideoList.DataBean.ListsBean listsBean = videoData.get(i);
                                if(TextUtils.equals(videoID,listsBean.getVideo_id())){
                                    poistion=i;
                                    break;
                                }
                            }
                            if(null!=mVideoListAdapter) mVideoListAdapter.remove(poistion);
                            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST);
                            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST, (Serializable) videoData, Constant.CACHE_TIME);
                            //刷新Mine界面的视频数量
                            Fragment parentFragment = getParentFragment();
                            if(null!=parentFragment&&parentFragment instanceof MineFragment){
                                ((MineFragment) parentFragment).updataMineTabCount(1);
                            }
                        }
                    }else{
                        showErrorToast(null,null,"取消收藏失败");
                    }
                }else{
                    showErrorToast(null,null,jsonObject.getString("msg"));
                }
            }else{
                showErrorToast(null,null,"取消收藏失败");
            }
        } catch (JSONException e) {
            showErrorToast(null,null,"取消收藏失败");
            e.printStackTrace();
        }
    }


    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    //========================================点击事件===============================================

    @Override
    public void onItemClick(int position) {

    }

    /**
     * 长按事件
     * @param videoID
     */
    @Override
    public void onLongClick(String videoID) {

    }


    @Override
    public void onDeleteVideo(String videoID) {

    }

    @Override
    public void onPublicVideo(String videoID) {

    }

    /**
     * 取消收藏视频
     * @param videoID
     */
    @Override
    public void onUnFollowVideo(String videoID) {
        if(!TextUtils.isEmpty(videoID)&&null!= mPresenter){
            if(!mPresenter.isUnFollowing()){
                showProgressDialog("取消收藏中...",true);
                mPresenter.followVideo(videoID);
            }else{
                showErrorToast(null,null,"点击太过频繁");
            }
            return;
        }else{
            showErrorToast(null,null,"错误，请刷新重试！");
        }
    }

    @Override
    public void onHeaderIcon(String userID) {
        if(!TextUtils.isEmpty(userID)){
            AuthorDetailsActivity.start(getActivity(),userID);
        }else{
            showErrorToast(null,null,"错误，请刷新重试！");
        }
    }


    /**
     * 来自首页的刷新命令
     */
    public void fromMainUpdata() {
        if(null!=mVideoListAdapter&&null!=mPresenter){
            if(!mPresenter.isLoading()){
                List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>0){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            bindingView.recyerView.scrollToPosition(0);
                        }
                    });
                    bindingView.swiperLayout.showLoadingProgress();
                }else{
                    mPage=0;
                    if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                    loadFollowVideoList();
                }
            }else{
                showErrorToast(null,null,"刷新太频繁了");
            }
        }else{
            showErrorToast(null,null,"刷新错误!");
        }
    }

    /**
     * 订阅播放结果，以刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangingViewEvent event) {
        if(null==event) return;
        if(Constant.FRAGMENT_TYPE_LIKE!=event.getFragmentType())return;

        if(null!=mVideoListAdapter&&null!=mGridLayoutManager){
            mPage=event.getPage();
            if(event.isFixedPosition()){
                int poistion = event.getPoistion();
                if(null!=mVideoListAdapter.getData()&&mVideoListAdapter.getData().size()>(poistion-1)){
                    mGridLayoutManager.scrollToPosition(poistion);
                }
            }else{
                List<FollowVideoList.DataBean.ListsBean> listsBeanList = event.getListsBeanList();
                mVideoListAdapter.setNewData(listsBeanList);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=mVideoListAdapter){
            mVideoListAdapter.setNewData(null);
            mVideoListAdapter=null;
        }
        mEmptyViewbindView=null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        ApplicationManager.getInstance().removeObserver(this);//移除订阅者
        super.onDestroy();
    }

    /**
     * 观察者刷新
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            Integer action= (Integer) arg;
            switch (action) {
                //登录
                case Constant.OBSERVABLE_ACTION_LOGIN:
                    isRefresh=true;
                    break;
                //登出
                case Constant.OBSERVABLE_ACTION_UNLOGIN:
                    if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
                    if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                    break;
                //收藏、取收
                case Constant.OBSERVABLE_ACTION_FOLLOW_VIDEO_CHANGED:
                    isRefresh=true;
                    if(null!= mPresenter &&!mPresenter.isLoading()){
                        mPage=0;
                        loadFollowVideoList();
                    }
                    break;
            }
        }
    }
}
