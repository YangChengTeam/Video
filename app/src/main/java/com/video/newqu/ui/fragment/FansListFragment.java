package com.video.newqu.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.FansListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.FansInfo;
import com.video.newqu.bean.FollowUserList;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentRecylerBinding;
import com.video.newqu.listener.OnFansClickListener;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.contract.FansContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.presenter.FansPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/1 14:44
 * 粉丝列表  用户自己的，别人的
 */

public class FansListFragment extends BaseFragment<FragmentRecylerBinding,FansPresenter> implements FansContract.View,OnFansClickListener{

    private int mPage=0;
    private  int mPageSize=20;
    private FansListAdapter mFansListAdapter;
    private String mAuthorID;
    private int mObjectType;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    /**
     * 创造实例
     * @param objectType 0:别人 1:自己
     * @param authorID
     * @return
     */
    public static FansListFragment newInstance(int objectType, String authorID){
        FansListFragment fansListFragment=new FansListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_AUTHOR_ID,authorID);
        bundle.putInt(Constant.KEY_AUTHOR_TYPE,objectType);
        fansListFragment.setArguments(bundle);
        return fansListFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取出参数
        Bundle arguments = getArguments();
        if(null!=arguments) {
            mAuthorID = arguments.getString(Constant.KEY_AUTHOR_ID);
            mObjectType = arguments.getInt(Constant.KEY_AUTHOR_TYPE);
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
                loadFansList();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new FansPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        mPage=0;
        loadFansList();
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
        //1：我的粉丝
        List<FansInfo.DataBean.ListBean> listBeans = null;
        if(1==mObjectType){
            listBeans= (List<FansInfo.DataBean.ListBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MINE_FANS_LIST);
        }
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFansListAdapter = new FansListAdapter(listBeans,mObjectType,this);
        mFansListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mFansListAdapter){
                    List<FansInfo.DataBean.ListBean> data = mFansListAdapter.getData();
                    if(null!=data&&data.size()>=10&&null!= mPresenter &&!mPresenter.isLoading()){
                        bindingView.swiperefreshLayout.setRefreshing(false);
                        mFansListAdapter.setEnableLoadMore(true);
                        loadFansList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mFansListAdapter.loadMoreFail();//加载失败
                                }else{
                                    mFansListAdapter.loadMoreEnd();//加载为空
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
                loadFansList();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mFansListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        bindingView.recyerView.setAdapter(mFansListAdapter);

    }

    /**
     * 加载粉丝列表
     */
    private void loadFansList() {
        mPage++;
        mPresenter.getFanslist(mAuthorID,mPage+"",mPageSize+"");
    }

    /**
     * 关注事件
     * @param listBean
     * @param position
     */
    private void followUser(FansInfo.DataBean.ListBean listBean, int position) {
        if(!Utils.isCheckNetwork()){
            showErrorToast(null,null,"没有网络连接");
            return;
        }
        //已关注，弹出取消关注窗口
        if(1==listBean.getBoth_fans()){
            showUnFollowMenu(listBean);
        //未关注，直接关注
        }else{
            if(TextUtils.equals(VideoApplication.getLoginUserID(),listBean.getFans_user_id())){
                showErrorToast(null,null,"自己无法关注自己");
                return;
            }
            showProgressDialog("关注中...",true);
            mPresenter.onFollowUser(VideoApplication.getLoginUserID(),listBean.getFans_user_id());
        }
    }


    /**
     * 弹出取消关注窗口
     * @param listBean
     */
    private void showUnFollowMenu(final FansInfo.DataBean.ListBean listBean) {
        List<VideoDetailsMenu> list=new ArrayList<>();
        VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
        videoDetailsMenu1.setItemID(1);
        videoDetailsMenu1.setTextColor("#FF576A8D");
        videoDetailsMenu1.setItemName("取消关注");
        list.add(videoDetailsMenu1);
        CommonMenuDialog commonMenuDialog =new CommonMenuDialog((AppCompatActivity) getActivity());
        commonMenuDialog.setData(list);
        commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int itemID) {
                //取消关注
                switch (itemID) {
                    case 1:
                        if(!Utils.isCheckNetwork()){
                            ToastUtils.showCenterToast("没有网络连接");
                            return;
                        }
                        unFollowUser(listBean);
                        break;
                }
            }
        });
        commonMenuDialog.show();
    }

    /**
     * 取消关注用户
     * @param listBean
     */
    private void unFollowUser(FansInfo.DataBean.ListBean listBean) {
        showProgressDialog("取消关注中...",true);
        mPresenter.onFollowUser(VideoApplication.getLoginUserID(),listBean.getFans_user_id());
    }


    //==========================================点击事件=============================================

    /**
     * 条目点击事件
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        try {
            List<FansInfo.DataBean.ListBean>  data = mFansListAdapter.getData();
            if(null!=data&&data.size()>0){
                FansInfo.DataBean.ListBean listBean = data.get(position);
                AuthorDetailsActivity.start(getActivity(),listBean.getFans_user_id());
            }
        }catch (Exception e){

        }
    }

    /**
     * 关注事件
     * @param position
     * @param data
     */
    @Override
    public void onFollowFans(int position, FansInfo.DataBean.ListBean data) {
        followUser(data,position);
    }

    @Override
    public void onFollowUser(int position, FollowUserList.DataBean.ListBean data) {

    }

    @Override
    public void onMenuClick(FollowUserList.DataBean.ListBean data) {

    }

    //======================================加载数据回调==============================================
    /**
     * 粉丝列表加载成功
     * @param data
     */
    @Override
    public void showFansList(FansInfo data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("还没有粉丝呢~",R.drawable.iv_fans_empty,false);
        if(null!=mFansListAdapter){
            mFansListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                //只缓存自己的粉丝
                mFansListAdapter.setNewData(data.getData().getList());
                if(1==mObjectType){
                    ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FANS_LIST);
                    ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_FANS_LIST, (Serializable) data.getData().getList(), Constant.CACHE_TIME);
                }
            }else{
                mFansListAdapter.addData(data.getData().getList());
            }
        }
    }

    /**
     * 粉丝列表加载为空
     * @param data
     */
    @Override
    public void showFansListEmpty(String data) {
        if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false);
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("还没有粉丝呢~",R.drawable.iv_fans_empty,false);
        if(null!=mFansListAdapter){
            mFansListAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mFansListAdapter.setNewData(null);
                if(1==mObjectType){
                    ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FANS_LIST);
                }
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 获取粉丝列表失败
     * @param data
     */
    @Override
    public void showFansListError(String data) {
        if(1==mPage){
            if(null!=bindingView) bindingView.swiperefreshLayout.setRefreshing(false,-1);
        }
        if(null!=mFansListAdapter){
            mFansListAdapter.loadMoreFail();
            List<FansInfo.DataBean.ListBean> dataList = mFansListAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 关注结果
     * @param text
     */
    @Override
    public void showFollowUser(String text) {
        closeProgressDialog();
        List<FansInfo.DataBean.ListBean> data = mFansListAdapter.getData();
        if(null!=data&&data.size()>0){
            int poistion=0;
            try {
                JSONObject jsonObject=new JSONObject(text);
                if(1==jsonObject.getInt("code")&& TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                    String userID = new JSONObject(jsonObject.getString("data")).getString("user_id");
                    for (int i = 0; i < data.size(); i++) {
                        if(TextUtils.equals(userID,data.get(i).getFans_user_id())){
                            poistion=i;
                            data.get(i).setBoth_fans(1);
                            break;
                        }
                    }
                    updateView(poistion,data);
                }else if(1==jsonObject.getInt("code")&& TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                    String userID = new JSONObject(jsonObject.getString("data")).getString("user_id");
                    for (int i = 0; i < data.size(); i++) {
                        if(TextUtils.equals(userID,data.get(i).getFans_user_id())){
                            poistion=i;
                            data.get(i).setBoth_fans(0);
                            break;
                        }
                    }
                    updateView(poistion,data);
                }
                showFinlishToast(null,null,jsonObject.getString("msg"));
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新单个条目
     * @param poistion
     * @param data
     */
    private void updateView(int poistion, List<FansInfo.DataBean.ListBean> data) {
        mFansListAdapter.notifyItemChanged(poistion);
        //如果当前为第一页并且是用户自己的粉丝列表，替换修改数据后的最新缓存
        if(1==mObjectType&&1==mPage){
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FANS_LIST);
            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MINE_FANS_LIST, (Serializable) data, Constant.CACHE_TIME);
        }
    }


    /**
     *关注者列表，这里不用
     * @param data
     */
    @Override
    public void showFollowUserList(FollowUserList data) {

    }

    @Override
    public void showFollowUserListEmpty(String data) {

    }

    @Override
    public void showFollowUserListError(String data) {

    }


    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }
}
