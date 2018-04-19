package com.video.newqu.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.LocationMessageListAdapter;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.NotifactionMessageInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemChildLongClickListener;
import com.video.newqu.databinding.FragmentRecylerBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.MessageLocationContract;
import com.video.newqu.ui.presenter.MessageLocationPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/5 10:26
 * 通知消息
 */

public class NotifcationMessageFragment extends BaseLightWeightFragment<FragmentRecylerBinding,MessageLocationPresenter> implements MessageLocationContract.View , com.video.newqu.listener.OnItemClickListener{

    private LocationMessageListAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new MessageLocationPresenter();
        mPresenter.attachView(this);
        initAdapter();
        getMessageList();
    }

    @Override
    protected void initViews() {
        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessageList();
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyler;
    }


    @Override
    public void onDestroy() {
        if(null!= mPresenter){
            mPresenter.detachView();
        }
        super.onDestroy();
        mAdapter =null;
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        bindingView.recyerView.setLayoutManager( new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new LocationMessageListAdapter(null,NotifcationMessageFragment.this);
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mAdapter.setEmptyView(emptyViewbindView.getRoot());
        emptyViewbindView.ivItemIcon.setImageResource(R.drawable.iv_message_empty);
        emptyViewbindView.tvItemName.setText("没有动态消息~");
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!= mAdapter){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.loadMoreEnd();
                        }
                    });
                }
            }
        }, bindingView.recyerView);
        bindingView.recyerView.setAdapter(mAdapter);
        //长按事件
        bindingView.recyerView.addOnItemTouchListener(new OnItemChildLongClickListener() {
            @Override
            public void onSimpleItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                showActionMenu(view,position);
            }
        });
    }

    /**
     * 显示菜单
     */
    private void showActionMenu(View view, final int position) {
        if(null!= mAdapter){
            List<NotifactionMessageInfo> data = mAdapter.getData();
            if(null!=data&&data.size()>0){
                NotifactionMessageInfo messageListInfo = data.get(position);
                if(null!=messageListInfo){
                    int menu;
                    if(messageListInfo.isRead()){
                        menu=R.menu.message_action1;
                    }else{
                        menu=R.menu.message_action;
                    }
                    PopupMenu actionMenu = new PopupMenu(getActivity(), view, Gravity.END | Gravity.CENTER_VERTICAL);
                    actionMenu.inflate(menu);
                    actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                //删除消息
                                case R.id.menu_detele:
                                    deleteMessageInfo(position);
                                    break;
                                //标记为已读
                                case R.id.menu_reset_red:
                                    changeStateRead(position);
                                    break;
                            }
                            return false;
                        }
                    });
                    actionMenu.show();
                }
            }
        }
    }

    /**
     * 删除单条消息
     * @param position
     */
    private void deleteMessageInfo(int position) {
        if(null!= mAdapter){
            try {
                List<NotifactionMessageInfo> data = mAdapter.getData();
                if(null!=data&&data.size()>0){
                    NotifactionMessageInfo messageListInfo = data.get(position);
                    if(null!=messageListInfo){
                        mAdapter.remove(position);
                        ApplicationManager.getInstance().getCacheExample().remove(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE);
                        ApplicationManager.getInstance().getCacheExample().put(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE, (Serializable) mAdapter.getData());
                        ToastUtils.showCenterToast("删除成功");
                    }
                }
            }catch (Exception e){

            }
        }
    }

    /**
     * 切换为已读状态
     * @param position
     */
    private void changeStateRead(int position) {
        if(null!= mAdapter){
            List<NotifactionMessageInfo> data = mAdapter.getData();
            if(null!=data&&data.size()>0){
                NotifactionMessageInfo messageListInfo = data.get(position);
                if(messageListInfo.isRead()){
                    return;
                }
                messageListInfo.setRead(true);
                ApplicationManager.getInstance().getCacheExample().remove(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE);
                ApplicationManager.getInstance().getCacheExample().put(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE, (Serializable) mAdapter.getData());
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    /**
     * 根据条目类型进入不同的详情界面
     * @param position
     */
    private void startDetailsView(int position) {
        // TODO: 2017/6/28 打开新的界面前更新数据库和主页新消息标记
        if(null!= mAdapter){
            try {
                List<NotifactionMessageInfo> listInfoList = mAdapter.getData();
                if(null!=listInfoList&&listInfoList.size()>0){
                    NotifactionMessageInfo dataBean = listInfoList.get(position);
                    if(null!=dataBean){
                        dataBean.setRead(true);
                        ApplicationManager.getInstance().getCacheExample().remove(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE);
                        ApplicationManager.getInstance().getCacheExample().put(VideoApplication.getLoginUserID()+ Constant.CACHE_USER_MESSAGE, (Serializable) listInfoList);
                        mAdapter.notifyItemChanged(position);//刷新单个条目
                        switch (dataBean.getItemType()) {
                            //关注
                            case 1:
                                AuthorDetailsActivity.start(getActivity(),dataBean.getUser_id());
                                break;
                            //收藏
                            case 2:
                                VideoDetailsActivity.start(getActivity(),dataBean.getVideo_id(),dataBean.getUser_id(),false);
                                break;
                            //留言
                            case 3:
                                VideoDetailsActivity.start(getActivity(),dataBean.getVideo_id(),dataBean.getUser_id(),false);
                                break;
                            //二次留言
                            case 4:
                                VideoDetailsActivity.start(getActivity(),dataBean.getVideo_id(),dataBean.getUser_id(),false);
                                break;
                        }
                    }
                }
            }catch (Exception e){

            }
        }
    }


    /**
     * 第一次加载和加载更多
     */
    private void getMessageList() {
        if(null!= mPresenter &&null!=VideoApplication.getInstance().getUserData()&&!mPresenter.isLoading()){
            mPresenter.getMessageList();
        }
    }

    @Override
    public void showMessageList(List<NotifactionMessageInfo> messageListInfos) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!= mAdapter){
            mAdapter.loadMoreComplete();//加载完成
            mAdapter.setNewData(messageListInfos);
        }
    }

    @Override
    public void getMessageError(String data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!= mAdapter){
            mAdapter.loadMoreFail();
        }
    }


    @Override
    public void getMessageEmpty(String data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if (null != mAdapter) {
            mAdapter.loadMoreEnd();//没有更多的数据了
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    /**
     * 条目的点击事件
     * @param position
     */
    @Override
    public void OnItemClick(int position) {
        startDetailsView(position);
    }

}