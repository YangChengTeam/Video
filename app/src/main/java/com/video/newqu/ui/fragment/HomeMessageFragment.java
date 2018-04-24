package com.video.newqu.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.adapter.MessageListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.NetMessageInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.MineFragmentMessageRecylerBinding;
import com.video.newqu.databinding.ReEmptyMarginLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.MediaRecordActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.activity.WebViewActivity;
import com.video.newqu.ui.contract.MessageContract;
import com.video.newqu.ui.presenter.MessagePresenter;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.MineDataChangeMarginView;
import com.video.newqu.view.widget.SwipeLoadingProgress;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/5 10:26
 * 我的消息,推送
 */

public class HomeMessageFragment extends BaseFragment<MineFragmentMessageRecylerBinding,MessagePresenter> implements  MessageContract.View {

    private MessageListAdapter mMessageListAdapter;
    private boolean isRefresh=true;//是否需要刷新
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    @Override
    protected void initViews() {
        mPresenter = new MessagePresenter(getActivity());
        mPresenter.attachView(this);
        bindingView.swiperLayout.setOnSwipeProgressEndListener(new SwipeLoadingProgress.OnSwipeProgressEndListener() {
            @Override
            public void onShowFinlish() {
                getMessageList();
            }

            @Override
            public void onHideFinlish() {
            }
        });
        initAdapter();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mMessageListAdapter&&null!= mPresenter &&!mPresenter.isLoading()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<NetMessageInfo.DataBean.ListBean> data = mMessageListAdapter.getData();
                    if(null==data||data.size()<=0){
                        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                        getMessageList();
                    }else{
                        bindingView.swiperLayout.showLoadingProgress();
                    }
                }
            },Constant.POST_DELAYED_ADD_DATA_TIME);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.mine_fragment_message_recyler;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        List<NetMessageInfo.DataBean.ListBean> list= (List<NetMessageInfo.DataBean.ListBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_MESSAGE_LIST);//读取缓存
        if(null!=list&&list.size()>0){
            Collections.sort(list, new Comparator<NetMessageInfo.DataBean.ListBean>() {
                @Override
                public int compare(NetMessageInfo.DataBean.ListBean o1, NetMessageInfo.DataBean.ListBean o2) {
                    Long addTimeO2 = Long.parseLong(o2.getAdd_time());
                    Long addTimeO1 = Long.parseLong(o1.getAdd_time());
                    return addTimeO2.compareTo(addTimeO1);
                }
            });
        }
        bindingView.recyerView.setLayoutManager( new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mMessageListAdapter = new MessageListAdapter(list);

        //加载中、数据为空、加载失败布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_margin_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new MineDataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyViewbindView.emptyView.showLoadingView();
                getMessageList();
            }

            @Override
            public void onClickView(View v) {
                Intent intent = new Intent(getActivity(), MediaRecordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.menu_enter, 0);//进场动画
            }
        });
        mEmptyViewbindView.emptyView.setBtnText("--");
        mEmptyViewbindView.emptyView.showLoadingView();
        mMessageListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        //加载更多
        mMessageListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mMessageListAdapter){
                    mMessageListAdapter.loadMoreEnd();//没有更多的数据了
                }
            }
        }, bindingView.recyerView);

        mMessageListAdapter.setOnItemClickListener(new MessageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int poistion) {
                if(null!=mMessageListAdapter){
                    List<NetMessageInfo.DataBean.ListBean> data = mMessageListAdapter.getData();
                    if(null!=data&&mMessageListAdapter.getData().size()>0){
                        final NetMessageInfo.DataBean.ListBean listBean = data.get(poistion);
                        if(null!=listBean&&null!=listBean.getType()){
                            String type = listBean.getType();
                            if(!TextUtils.isEmpty(type)){
                                if(TextUtils.equals("1",type)){
                                    MobclickAgent.onEvent(getActivity(), "msg_web");
                                    if(null!=listBean.getAction()&&TextUtils.equals("新趣小视频助手",listBean.getAction())){
                                        WebViewActivity.loadUrl(getActivity(),listBean.getUrl(),listBean.getAction());
                                        return;
                                    }
                                    try {
                                        Intent intent= new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(listBean.getUrl());
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        //若无法正常跳转，在此进行错误处理
                                        ToastUtils.showCenterToast("处理失败："+e.getMessage());
                                    }
                                }else if(TextUtils.equals("2",type)){
                                    MobclickAgent.onEvent(getActivity(), "msg_video");
                                    VideoDetailsActivity.start(getActivity(),listBean.getVideo_id(),listBean.getUser_id(),false);
                                }else if(TextUtils.equals("-1",type)){
                                    if(!TextUtils.isEmpty(listBean.getAction())){
                                        //微信
                                        if(TextUtils.equals("weixin://",listBean.getAction())){
                                            MobclickAgent.onEvent(getActivity(), "msg_weixin");
                                            Utils.copyString("新趣小视频助手");
                                            ToastUtils.showCenterToast("已复制微信号");
                                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                                                    .setTitle("新趣小视频助手")
                                                    .setMessage(getResources().getString(R.string.open_weixin_tips));
                                            builder.setNegativeButton("算了", null);
                                            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    try {
                                                        Uri uri = Uri.parse(listBean.getAction());
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        //若无法正常跳转，在此进行错误处理
                                                        ToastUtils.showCenterToast("无法跳转到微信，请检查设备是否安装了微信！");
                                                    }
                                                }
                                            });
                                            builder.setCancelable(false);
                                            builder.show();
                                            return;
                                        //话题
                                        }else if(TextUtils.equals("com.xinqu.media.topic",listBean.getAction())){
                                            MobclickAgent.onEvent(getActivity(), "msg_topic");
                                            Intent intent=new Intent(listBean.getAction());
                                            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT);
                                            intent.putExtra(Constant.KEY_TITLE,listBean.getUrl());
                                            intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,listBean.getUrl());
                                            startActivity(intent);
                                            return;
                                        //其他类型的
                                        }else {
                                            MobclickAgent.onEvent(getActivity(), "msg_action");
                                            try {
                                                Intent intent = new Intent(listBean.getAction());
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                //若无法正常跳转，在此进行错误处理
                                                ToastUtils.showCenterToast("处理失败："+e.getMessage());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onUserClick(String userID) {
                if(null!=userID){
                    AuthorDetailsActivity.start(getActivity(),userID);
                }
            }

            @Override
            public void onOthorClick(String action) {
                if(null!=action){
                    ToastUtils.showCenterToast(action);
                }
            }
        });
        bindingView.recyerView.setAdapter(mMessageListAdapter);
    }


    /**
     * 第一次加载和加载更多
     */
    private void getMessageList() {
        if(null!= mPresenter &&!mPresenter.isLoading()){
            mPresenter.getMessageList();
        }
    }

    /**
     * 获取消息回调
     * @param data
     */
    @Override
    public void showMessageInfo(List<NetMessageInfo.DataBean.ListBean> data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("暂时没有消息~",R.drawable.iv_message_empty,false);
        Fragment parentFragment = getParentFragment();
        if(null!=parentFragment&&parentFragment instanceof MineFragment){
            ((MineFragment) parentFragment).updataTab(data.size());
        }
        SharedPreferencesUtil.getInstance().putInt(Constant.KEY_MSG_COUNT,data.size());
        ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_MESSAGE_LIST);
        ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_HOME_MESSAGE_LIST, (Serializable) data,Constant.CACHE_TIME);

        if(null!= mMessageListAdapter){
            mMessageListAdapter.loadMoreComplete();//加载完成
            Collections.sort(data, new Comparator<NetMessageInfo.DataBean.ListBean>() {
                @Override
                public int compare(NetMessageInfo.DataBean.ListBean o1, NetMessageInfo.DataBean.ListBean o2) {
                    Long addTimeO2 = Long.parseLong(o2.getAdd_time());
                    Long addTimeO1 = Long.parseLong(o1.getAdd_time());
                    return addTimeO2.compareTo(addTimeO1);
                }
            });
            mMessageListAdapter.setNewData(data);
        }
    }

    @Override
    public void showMessageEmpty(){
        isRefresh=false;
        bindingView.swiperLayout.hideLoadProgress();
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("暂时没有消息~",R.drawable.iv_message_empty,false);
        if(null!=mMessageListAdapter){
            mMessageListAdapter.loadMoreEnd();//没有更多的数据了
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_MESSAGE_LIST);
        }
    }

    @Override
    public void showMessageError(String data) {
        bindingView.swiperLayout.hideLoadProgress();
        if(null!=mMessageListAdapter){
            mMessageListAdapter.loadMoreFail();
            List<NetMessageInfo.DataBean.ListBean> listBeans = mMessageListAdapter.getData();
            if(null==listBeans||listBeans.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
    }

    @Override
    public void showErrorView() {
        isRefresh=false;
    }

    @Override
    public void complete() {

    }

    /**
     * 来自首页的刷新
     */
    public void fromMainUpdata() {
        if(null!=mMessageListAdapter&&null!= mPresenter){
            if(!mPresenter.isLoading()){
                List<NetMessageInfo.DataBean.ListBean> data = mMessageListAdapter.getData();
                if(null!=data&&data.size()>0){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            bindingView.recyerView.scrollToPosition(0);
                        }
                    });
                    bindingView.swiperLayout.showLoadingProgress();
                }else{
                    if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                    getMessageList();
                }
            }else{
                showErrorToast(null,null,"刷新太频繁了");
            }
        }else{
            showErrorToast(null,null,"刷新错误!");
        }
    }
}