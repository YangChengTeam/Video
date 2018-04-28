package com.video.newqu.ui.fragment;

import android.content.Context;
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
import com.video.newqu.adapter.HomeVideoListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentVideoFollowBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.mode.GridSpacesItemDecoration;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.FollowContract;
import com.video.newqu.ui.presenter.FollowPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@outlook.com
 * 2017/5/22 18:09
 * 我的关注视频列表
 */

public class HomeFollowVideoFragment extends BaseFragment<FragmentVideoFollowBinding,FollowPresenter> implements FollowContract.View, Observer {

    private int mPage=0;
    private int pageSize=10;
    private MainActivity mMainActivity;
    private HomeVideoListAdapter mVideoListAdapter;
    private boolean isRefresh=true;//是否自动刷新
    private GridLayoutManager mGridLayoutManager;
    private ReEmptyLayoutBinding mEmptyViewbindView;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_follow;
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new FollowPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();//初始化普通列表
        ApplicationManager.getInstance().addObserver(this);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<FollowVideoList.DataBean.ListsBean> cacheList= (List<FollowVideoList.DataBean.ListsBean>)  ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_FOOLOW_VIDEO_LIST);//读取缓存
        mGridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL,false);
        bindingView.recyerView.setLayoutManager(mGridLayoutManager);
        bindingView.recyerView.addItemDecoration(new GridSpacesItemDecoration(3));
        mVideoListAdapter = new HomeVideoListAdapter(cacheList);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>6){
                        if(null!= mPresenter &&!mPresenter.isLoading()){
                            bindingView.swiperefreshLayout.setRefreshing(false);
                            mVideoListAdapter.setEnableLoadMore(true);
                            loadVideoList();
                        }
                    }else{
                        bindingView.swiperefreshLayout.post(new Runnable() {
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
        mEmptyViewbindView.emptyView.setOnSubmitClickListener(new DataChangeView.OnSubmitClickListener() {
            @Override
            public void onSubmitClick(int clickType) {
                if(0==clickType){
                    MainActivity activity = (MainActivity) getActivity();
                    if(null!=activity){
                        activity.currentHomeFragmentChildItemView(0,2);
                    }
                }else if(1==clickType){
                    if(null!=mMainActivity){
                        if(!Utils.isCheckNetwork()){
                            showNetWorkTips();
                            return;
                        }
                        mMainActivity.login();
                    }
                }
            }
        });
        //已登录
        if(null!=VideoApplication.getInstance().getUserData()){
            mEmptyViewbindView.emptyView.showLoadingView();
        //未登录
        }else{
            mEmptyViewbindView.emptyView.showLoginView(true);
        }

        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getActivity(), "click_play");
                if(null!= mVideoListAdapter){
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
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_FOLLOW);
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


    @Override
    protected void initViews() {
        //刷新监听器
        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //未登录情况下不允许刷新
                if(null==VideoApplication.getInstance().getUserData()){
                    bindingView.swiperefreshLayout.setRefreshing(false);
                    ToastUtils.showCenterToast("请先登录");
                    if(null!=mMainActivity){
                        mMainActivity.login();
                    }
                    return;
                }
                //还原当前页数
                mPage=0;
                loadVideoList();
            }
        });
    }


    /**
     * 加载数据
     */
    private void loadVideoList() {
        if(null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getFollowVideoList(VideoApplication.getLoginUserID(),mPage+"",pageSize+"");
        }
    }

//===========================================数据加载回调============================================
    /**
     * 显示新消息小圆点
     */
    private void showNewMessageDot(List<FollowVideoList.DataBean.ListsBean> oldList, List<FollowVideoList.DataBean.ListsBean> newList) {
        int count = Utils.compareToDataHasNewData(oldList, newList);
        if(count>0&&null!=mMainActivity){
            mMainActivity.showNewMessageDot(count);
        }
    }

    /**
     * 获取关注列表成功
     * @param data
     */
    @Override
    public void showloadFollowVideoList(FollowVideoList data) {
        isRefresh=false;
        bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未找到作品~",R.drawable.iv_work_video_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                bindingView.swiperefreshLayout.setRefreshing(false);
                List<FollowVideoList.DataBean.ListsBean> listsBeen=new ArrayList<>();
                for (FollowVideoList.DataBean.ListsBean listsBean : data.getData().getLists()) {
                    listsBeen.add(listsBean);
                }
                showNewMessageDot(mVideoListAdapter.getData(),listsBeen);
                mVideoListAdapter.setNewData(data.getData().getLists());
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_FOOLOW_VIDEO_LIST);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_FOOLOW_VIDEO_LIST, (Serializable) data.getData().getLists(), Constant.CACHE_TIME);
                //添加数据
            }else{
                mVideoListAdapter.addData(data.getData().getLists());
            }
        }
    }

    @Override
    public void showloadFollowListEmptry(String response) {
        isRefresh=false;
        bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未找到作品~",R.drawable.iv_work_video_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_FOOLOW_VIDEO_LIST);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 获取关注列表失败
     */
    @Override
    public void showloadFollowListError() {
        if(1==mPage){
            bindingView.swiperefreshLayout.setRefreshing(false,-1);
        }
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
     * 热门列表回调，这里不需要
     * @param data
     */
    @Override
    public void showHotVideoList(FollowVideoList data) {

    }

    @Override
    public void showHotVideoListEmpty(String data) {

    }

    @Override
    public void showHotVideoListError(String data) {

    }

    /**
     * 举报用户回调
     * @param data
     */
    @Override
    public void showReportUserResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(jsonObject.getString("msg"),Constant.REPORT_USER_RESULT)){
                showFinlishToast(null,null,jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 举报视频回调
     * @param data
     */
    @Override
    public void showReportVideoResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(jsonObject.getString("msg"),Constant.REPORT_USER_RESULT)){
                showFinlishToast(null,null,jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 其他联网错误回调
     */
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    /**
     * 其他联网完成回调
     */
    @Override
    public void complete() {

    }



    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity=null;
    }

    @Override
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        mVideoListAdapter=null;mEmptyViewbindView=null;
        super.onDestroy();
    }

    /**
     * 来自外界的刷新命令
     */
    public void fromMainUpdata() {
        if(null==VideoApplication.getInstance().getUserData()){
            ToastUtils.showCenterToast("请先登录");
            if(null!=mMainActivity&&!mMainActivity.isFinishing()){
                mMainActivity.login();
            }
            return;
        }
        if(null!=mVideoListAdapter&&null!= mPresenter){
            if(!mPresenter.isLoading()){
                List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
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

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                //已登录
                if(null!=VideoApplication.getInstance().getUserData()){
                    mEmptyViewbindView.emptyView.showLoadingView();
                    //未登录
                }else{
                    mEmptyViewbindView.emptyView.showLoginView(true);
                }
                switch (action) {
                    //登录
                    case Constant.OBSERVABLE_ACTION_LOGIN:
                        isRefresh=true;
                        if(null!= mPresenter &&!mPresenter.isLoading()){
                            mPage=0;
                            if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                            loadVideoList();
                        }
                        break;
                    //登出
                    case Constant.OBSERVABLE_ACTION_UNLOGIN:
                        if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
                        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoginView(true);
                        break;
                    //关注、取关
                    case Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED:
                        if(null!= mPresenter &&!mPresenter.isLoading()){
                            mPage=0;
                            loadVideoList();
                        }
                        break;
                }
            //播放进度观察
            }else if(arg instanceof ChangingViewEvent){
                ChangingViewEvent changingViewEvent= (ChangingViewEvent) arg;
                if(Constant.FRAGMENT_TYPE_FOLLOW==changingViewEvent.getFragmentType()&&null!=mVideoListAdapter&&null!=mGridLayoutManager){
                    List<FollowVideoList.DataBean.ListsBean> listsBeanList = changingViewEvent.getListsBeanList();
                    mPage=changingViewEvent.getPage();
                    if(null!=listsBeanList){
                        mVideoListAdapter.setNewData(listsBeanList);
                    }
                    if(null!= mVideoListAdapter.getData()&& mVideoListAdapter.getData().size()>(changingViewEvent.getPoistion()-1)){
                        mGridLayoutManager.scrollToPosition(changingViewEvent.getPoistion());
                    }
                }
            }
        }
    }
}
