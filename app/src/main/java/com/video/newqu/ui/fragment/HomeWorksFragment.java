package com.video.newqu.ui.fragment;

import android.content.DialogInterface;
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
import com.video.newqu.ui.activity.MediaRecordActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.WorksContract;
import com.video.newqu.ui.presenter.WorksPresenter;
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
 * 2017-05-24 19:28
 * 用户发布的视频
 */

public class HomeWorksFragment extends BaseLightWeightFragment<MineFragmentRecylerBinding,WorksPresenter> implements WorksContract.View,OnUserVideoListener, Observer {

    private int mPage=0;
    private int mPageSize=9;
    private UserVideoListAdapter mVideoListAdapter;
    private boolean isRefresh=true;
    private GridLayoutManager mGridLayoutManager;
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    public void setRefresh(boolean refresh) {
        this.isRefresh = refresh;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter = new WorksPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        ApplicationManager.getInstance().addObserver(this);
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=mVideoListAdapter&&null!=bindingView&&null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null==data||data.size()<=0){
                        mPage=0;
                        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                        loadVideoList();
                    }else{
                        bindingView.swiperLayout.showLoadingProgress();
                    }
                }
            },Constant.POST_DELAYED_ADD_DATA_TIME);
        }
    }



    @Override
    protected void initViews() {
        bindingView.swiperLayout.setOnSwipeProgressEndListener(new SwipeLoadingProgress.OnSwipeProgressEndListener() {
            @Override
            public void onShowFinlish() {
                mPage=0;
                loadVideoList();
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

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<FollowVideoList.DataBean.ListsBean> cacheList=(List<FollowVideoList.DataBean.ListsBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MINE_WORKS);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        bindingView.recyerView.setLayoutManager(mGridLayoutManager);
        bindingView.recyerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1)));
        bindingView.recyerView.setHasFixedSize(true);
        mVideoListAdapter = new UserVideoListAdapter(cacheList,1,this);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoListAdapter){
                    List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                    if(null!=data&&data.size()>8){
                        mVideoListAdapter.setEnableLoadMore(true);
                        loadVideoList();
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
                loadVideoList();
            }

            @Override
            public void onClickView(View v) {
                Intent intent = new Intent(getActivity(), MediaRecordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.menu_enter, 0);//进场动画
            }
        });
        mEmptyViewbindView.emptyView.setBtnText("开始制作");
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
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_WORKS);
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
     * 获取我的作品
     */
    private void loadVideoList() {
        if(null!=VideoApplication.getInstance().getUserData()&&null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getUpLoadVideoList(VideoApplication.getLoginUserID(),VideoApplication.getLoginUserID(),mPage+"",mPageSize+"");
        }
    }

    /**
     * 显示视频列表
     * @param data
     */
    @Override
    public void showUpLoadVideoList(FollowVideoList data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("发布视频让更多的人认识你",R.drawable.iv_work_video_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mVideoListAdapter.setNewData(data.getData().getLists());
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_WORKS);
                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_WORKS, (Serializable) data.getData().getLists(), Constant.CACHE_TIME);
                //添加数据
            }else{
                mVideoListAdapter.addData(data.getData().getLists());
            }
        }
    }

    /**
     * 加载视频列表为空
     * @param data
     */
    @Override
    public void showUpLoadVideoListEmpty(FollowVideoList data) {
        isRefresh=false;
        bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有发布作品~",R.drawable.iv_work_video_empty,true);
        if(null!=mVideoListAdapter){
            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mVideoListAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_WORKS);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 加载视频列表失败
     * @param data
     */
    @Override
    public void showUpLoadVideoListError(String data) {
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
     * 其他联网失败
     */
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    /**
     * 用户删除视频回调
     * @param data
     */
    @Override
    public void showDeteleVideoResult(String data) {
        closeProgressDialog();
        int poistion = 0;
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(Constant.DELETE_VIDEO_CONTENT,jsonObject.getString("msg"))){
                //删除成功
                String  videoID= new JSONObject(jsonObject.getString("data")).getString("video_id");
                List<FollowVideoList.DataBean.ListsBean> data1 = mVideoListAdapter.getData();
                if(null!=data1&&data1.size()>0){
                    for (int i = 0; i < data1.size(); i++) {
                        FollowVideoList.DataBean.ListsBean listsBean = data1.get(i);
                        if(TextUtils.equals(videoID,listsBean.getVideo_id())){
                            poistion=i;
                            break;
                        }
                    }

                    ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_WORKS);
                    ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_WORKS, (Serializable) data1,  Constant.CACHE_TIME);
                    mVideoListAdapter.remove(poistion);
                    showFinlishToast(null,null,"删除视频成功");
                    //刷新Mine界面的视频数量
                    Fragment parentFragment = getParentFragment();
                    if(null!=parentFragment&&parentFragment instanceof MineFragment){
                        ((MineFragment) parentFragment).updataMineTabCount(0);
                    }
                }
            }else{
                showErrorToast(null,null,jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            showErrorToast(null,null,"删除视频失败");
            e.printStackTrace();
        }
    }

    /**
     * 请求公开视频结果
     * @param result
     */
    @Override
    public void showPublicResult(String result) {
        closeProgressDialog();
        if(!TextUtils.isEmpty(result)){
            int poistion = 0;
            try {
                JSONObject jsonObject=new JSONObject(result);
                if(jsonObject.length()>0){
                    if(1==jsonObject.getInt("code")){
                        String  videoID= new JSONObject(jsonObject.getString("data")).getString("video_id");
                        if(!TextUtils.isEmpty(videoID)){
                            List<FollowVideoList.DataBean.ListsBean> data1 = mVideoListAdapter.getData();
                            if(null!=data1&&data1.size()>0){
                                for (int i = 0; i < data1.size(); i++) {
                                    FollowVideoList.DataBean.ListsBean listsBean = data1.get(i);
                                    if(TextUtils.equals(videoID,listsBean.getVideo_id())){
                                        listsBean.setIs_private("0");//公开请求成功后刷新缓存和界面状态
                                        poistion=i;
                                        break;
                                    }
                                }
                                mVideoListAdapter.notifyItemChanged(poistion);
                                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_WORKS);
                                ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_WORKS, (Serializable) data1, Constant.CACHE_TIME);
                                showFinlishToast(null,null,"设置成功，视频审核通过后可以分享和评论");
                            }
                        }else{
                            showFinlishToast(null,null,"公开视频失败");
                        }
                    }else{
                        showFinlishToast(null,null,"公开视频失败");
                    }
                }
            } catch (JSONException e) {
                showFinlishToast(null,null,"公开视频失败");
                e.printStackTrace();
            }
        }
    }


//==========================================点击事件回调=============================================
    /**
     * 条目点击事件
     * @param position
     */
    @Override
    public void onItemClick(int position) {

    }

    /**
     * 条目长按事件
     * @param videoID
     */
    @Override
    public void onLongClick(String videoID) {

    }

    /**
     * 删除视频
     * @param videoID
     */
    @Override
    public void onDeleteVideo(String videoID) {
        deleteVideoTips(videoID);
    }

    /**
     * 公开视频
     * @param videoID
     */
    @Override
    public void onPublicVideo(String videoID) {
        publicVideo(videoID);
    }

    /**
     * 取消收藏视频
     * @param videoID
     */
    @Override
    public void onUnFollowVideo(String videoID) {

    }

    /**
     * 点击了头像
     * @param userID
     */
    @Override
    public void onHeaderIcon(String userID) {
        if(!TextUtils.isEmpty(userID)){
            AuthorDetailsActivity.start(getActivity(),userID);
        }
    }

    /**
     * 公开视频
     * @param videoID
     */
    private void publicVideo(String videoID) {
        if(!TextUtils.isEmpty(videoID)&&null!= mPresenter) {
            if(!mPresenter.isPublicing()){
                showProgressDialog("设置公开状态中...",true);
                mPresenter.publicVideo(videoID,VideoApplication.getLoginUserID());
            }else{
                showErrorToast(null,null,"点击太过频繁");
            }
            return;
        }else{
            showErrorToast(null,null,"错误，请刷新重试！");
        }
    }

    /**
     * 删除视频
     * @param videoID
     */
    private void deleteVideoTips(final String videoID) {
        //删除视频提示
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle(R.string.hint)
                .setMessage("确定删除此视频吗？删除后将不可恢复!")
                .setNegativeButton(
                        "删除",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(null!= mPresenter &&!TextUtils.isEmpty(videoID)&&null!= mPresenter){
                                    if(!mPresenter.isDeletecing()){
                                        showProgressDialog("删除视频中...",true);
                                        mPresenter.deleteVideo(VideoApplication.getLoginUserID(),videoID);
                                    }else{
                                        showErrorToast(null,null,"点击太过频繁");
                                    }
                                    return;
                                }else{
                                    showErrorToast(null,null,"错误，请刷新重试！");
                                }
                            }
                        })
                .setPositiveButton("取消",
                        null).setCancelable(false).show();
    }


    /**
     * 来自首页的刷新命令
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
                        }
                    });
                    bindingView.swiperLayout.showLoadingProgress();
                }else{
                    mPage=0;
                    if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                    loadVideoList();
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
        if(Constant.FRAGMENT_TYPE_WORKS!=event.getFragmentType())return;
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
                //删除、上传视频
                case Constant.OBSERVABLE_ACTION_VIDEO_CHANGED:
                    isRefresh=true;
                    if(null!= mPresenter &&!mPresenter.isLoading()){
                        mPage=0;
                        loadVideoList();
                    }
                    break;
            }
        }
    }
}
