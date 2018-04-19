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
import com.video.newqu.adapter.HomeVideoListAdapter;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentHotRecylerBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.mode.GridSpacesItemDecoration;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.HotVideoContract;
import com.video.newqu.ui.presenter.HotVideoPresenter;
import com.video.newqu.util.Logger;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@outlook.com
 * 2017/5/23 16:10
 * 热门视频，观察者模式更新实时的列表播放进度
 */

public class HomeHotVideoFragment extends BaseLightWeightFragment<FragmentHotRecylerBinding,HotVideoPresenter> implements  HotVideoContract.View, Observer {

    private static final String TAG = "HomeHotVideoFragment";
    private HomeVideoListAdapter mVideoListAdapter;
    private int mPage=0;//当前页数
    private GridLayoutManager mGridLayoutManager;
    private boolean isRefresh=true;//是否需要刷新
    private ReEmptyLayoutBinding mEmptyViewbindView;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_hot_recyler;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new HotVideoPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        ApplicationManager.getInstance().addObserver(this);
        ApplicationManager.getInstance().addObserverToMusic(this);
        //第一次使用弹出使用提示
        if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_HOT_CODE)&&null!= mVideoListAdapter &&null!= mVideoListAdapter.getData()&& mVideoListAdapter.getData().size()>0){
            bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
            bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
            });
            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_HOT_CODE,1);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!= mVideoListAdapter &&null!= mPresenter &&!mPresenter.isLoading()){
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
    protected void onInvisible() {
        super.onInvisible();
    }


    @Override
    protected void initViews() {

        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=bindingView.tvTipsMessage&&bindingView.tvTipsMessage.getVisibility()==View.VISIBLE){
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
                mPage=0;
                loadVideoList();
            }
        });
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<FollowVideoList.DataBean.ListsBean> cacheList=(List<FollowVideoList.DataBean.ListsBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOT_VIDEO_LIST);//读取缓存
        mGridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL,false);
        bindingView.recyerView.setLayoutManager(mGridLayoutManager);
        bindingView.recyerView.addItemDecoration(new GridSpacesItemDecoration(3));
        mVideoListAdapter = new HomeVideoListAdapter(cacheList);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>6&&null!= mPresenter &&!mPresenter.isLoading()){
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
//                                    VerticalVideoPlayActivity.start(getActivity(),Constant.FRAGMENT_TYPE_HOT,position,mPage,VideoApplication.getLoginUserID(),json,view);
                                    Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_HOT);
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
     * 加载数据
     */
    private void loadVideoList() {
        if(null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            //加载热门数据的时候，如果当前是未登录用户，则使用设备号
            mPresenter.getHotVideoList(mPage+"",VideoApplication.getLoginUserID());
        }
    }


    /**
     * 其他加载错误
     */
    @Override
    public void showErrorView() {

    }

    /**
     * 加载完成
     */
    @Override
    public void complete() {

    }

    /**
     * 视频列表加载成功
     * @param data
     */
    @Override
    public void showHotVideoList(FollowVideoList data) {
        isRefresh=false;
        if(0==VideoApplication.mBuildChanleType){
            bindingView.swiperefreshLayout.setRefreshing(false,data.getData().getLists().size());
        }else{
            bindingView.swiperefreshLayout.setRefreshing(false);
        }
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有视频，下拉刷新试试~",R.drawable.iv_fans_empty,false);
        if(null!= mVideoListAdapter){
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mVideoListAdapter.setNewData(data.getData().getLists());
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOT_VIDEO_LIST);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_HOT_VIDEO_LIST, (Serializable) data.getData().getLists(), Constant.CACHE_TIME);
                if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_HOT_CODE)&&null!= mVideoListAdapter.getData()&& mVideoListAdapter.getData().size()>0){
                    bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
                    bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bindingView.tvTipsMessage.setVisibility(View.GONE);
                        }
                    });
                    SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_HOT_CODE,1);
                }
                //添加数据
            }else{
                mVideoListAdapter.addData(data.getData().getLists());
            }
        }
    }



    /**
     * 视频列表加载为空
     * @param data
     */
    @Override
    public void showHotVideoListEmpty(String data) {
        isRefresh=false;
        bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有视频，下拉刷新试试~",R.drawable.iv_fans_empty,false);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOT_VIDEO_LIST);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 视频列表加载失败
     * @param data
     */
    @Override
    public void showHotVideoListError(String data) {
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
     * 来自外界的刷新命令
     */
    public void fromMainUpdata() {
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
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        ApplicationManager.getInstance().removeObserverToMusic(this);
        if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    //登录
                    case Constant.OBSERVABLE_ACTION_LOGIN:
                        //登出
                    case Constant.OBSERVABLE_ACTION_UNLOGIN:
                        isRefresh=true;
                        if(null!= mPresenter &&!mPresenter.isLoading()){
                            mPage=0;
                            loadVideoList();
                        }
                        break;
                }
            }else if(arg instanceof ChangingViewEvent){
                ChangingViewEvent changingViewEvent= (ChangingViewEvent) arg;
                if(null!=changingViewEvent&&Constant.FRAGMENT_TYPE_HOT==changingViewEvent.getFragmentType()&&null!=mVideoListAdapter&&null!=mGridLayoutManager){
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
