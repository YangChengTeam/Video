package com.video.newqu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSONArray;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.HistorySearchAdapter;
import com.video.newqu.adapter.SearchResultHorUserListAdapter;
import com.video.newqu.adapter.SearchVideoListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.ChangingViewEvent;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.SearchAutoResult;
import com.video.newqu.bean.SearchParams;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentSearchLayoutBinding;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.databinding.SearchHistryCanelLayoutBinding;
import com.video.newqu.databinding.SearchResultHeaderLayoutBinding;
import com.video.newqu.listener.VideoComentClickListener;
import com.video.newqu.manager.SearchCacheManager;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.SearchActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.SearchVideoContract;
import com.video.newqu.ui.presenter.SearchVideoPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.xinqu.videoplayer.full.WindowVideoPlayer;


/**
 * TinyHung@outlook.com
 * 2017/6/8 16:20
 * 搜索结果展示，全部
 */

public class SearchResultAllFragment extends BaseFragment<FragmentSearchLayoutBinding,SearchVideoPresenter> implements BaseQuickAdapter.RequestLoadMoreListener, SearchVideoContract.View
        ,VideoComentClickListener {

    private HistorySearchAdapter mHistorySearchAdapter;
    private SearchVideoListAdapter mSearchVideoListAdapter;
    private SearchResultHorUserListAdapter mHorUserListAdapter;
    private SearchActivity mSearchActivity;
    private List<SearchResultInfo.DataBean.VideoListBean> mvideo_list=new ArrayList<>();
    private List<SearchResultInfo.DataBean.UserListBean> mUser_list;
    private SearchResultHeaderLayoutBinding headerLayoutBinding;
    private List<SearchAutoResult> mSearchHistoeyList;
    //加载更多的参数
    private String searchKey;
    private int searchKeyType;
    private int mPage=0;
    private int mPageSize=10;
    private SearchHistryCanelLayoutBinding mCanelLayoutBinding;
    private GridLayoutManager mGridLayoutManager;


    public static SearchResultAllFragment newInstance(){
        SearchResultAllFragment listFragment=new SearchResultAllFragment();
        return listFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSearchActivity= (SearchActivity) context;
    }

    @Override
    protected void initViews() {
        mPresenter = new SearchVideoPresenter(getActivity());
        mPresenter.attachView(this);
        try {
            initHistoryListAdapter();
            initSearchResultAdapter(0);
        }catch (Exception e){

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_layout;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mSearchActivity=null;
    }

    /**
     * 初始化适配器
     * @param page 当前的页数
     */
    private void initSearchResultAdapter(int page) {
        if(null!=mSearchActivity&&!mSearchActivity.isFinishing()){
            if(null!=mSearchActivity.getSearchResultData()){
                mvideo_list = mSearchActivity.getSearchResultData().getVideo_list();
                mUser_list = mSearchActivity.getSearchResultData().getUser_list();
            }
            if(null==mSearchVideoListAdapter){
                mGridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                bindingView.recyerView.setLayoutManager(mGridLayoutManager);
                bindingView.recyerView.setHasFixedSize(true);
                mSearchVideoListAdapter = new SearchVideoListAdapter(mvideo_list,this);
                bindingView.recyerView.setAdapter(mSearchVideoListAdapter);
                mSearchVideoListAdapter.setOnLoadMoreListener(this);
                addHeaderView();
            }else{
                mSearchVideoListAdapter.setNewData(mvideo_list);
                //刷新头部
                initHeaderView();
            }
        }
    }



    /**
     * 刷新新增的数据,只能自己刷新，不可给外界调用
     * @param video_list
     */
    private void updataAddDataAdapter(List<SearchResultInfo.DataBean.VideoListBean> video_list) {
        for (SearchResultInfo.DataBean.VideoListBean videoListBean : video_list) {
            Log.d("SearchResultAllFragment", "视频ID: "+videoListBean.getVideo_id());
        }
        if(null!=video_list&&video_list.size()>0)
        mSearchVideoListAdapter.addData(video_list);
    }


    /**
     * 加载更多搜索结果
     */

    private void loadMoreVideoList() {
        mPage++;
        mPresenter.getMoreVideoList(searchKey,searchKeyType+"",mPage+"",mPageSize+"");
    }

    /**
     * 初始化参数
     * @param searchParams
     */
    private void createParams(SearchParams searchParams) {
        this.searchKey=searchParams.getSearchKey();
        this.searchKeyType=2;//类型固定为视频
        this.mPage=searchParams.getPage();
        this.mPageSize=searchParams.getPageSize();
    }


    /**
     * 添加头部
     */
    private void addHeaderView() {
        if(mSearchVideoListAdapter.getHeaderViewsCount()<=0){
            headerLayoutBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.search_result_header_layout, (ViewGroup) bindingView.recyerView.getParent(), false);
            mSearchVideoListAdapter.addHeaderView(headerLayoutBinding.getRoot());
        }
    }

    /**
     * 横向的
     */
    private void initHeaderView() {

        if(null==mHorUserListAdapter){
            headerLayoutBinding.recyerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
            mHorUserListAdapter = new SearchResultHorUserListAdapter(mUser_list);
            headerLayoutBinding.recyerView.setAdapter(mHorUserListAdapter);
            headerLayoutBinding.recyerView.addOnItemTouchListener(new OnItemClickListener() {
                //点击用户头像
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    SearchResultInfo.DataBean.UserListBean userListBean = mUser_list.get(position);
                    AuthorDetailsActivity.start(getActivity(),userListBean.getId());
                }
            });
        }else{
            mHorUserListAdapter.setNewData(mUser_list);
        }
    }



    /**
     * 初始化搜索历史记录全部的搜索记录
     */
    private void initHistoryListAdapter() {
        mSearchHistoeyList = SearchCacheManager.getInstance().getSearchHistoeyList(0);
        bindingView.histroyRecyerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHistorySearchAdapter = new HistorySearchAdapter(mSearchHistoeyList);
        bindingView.histroyRecyerView.setAdapter(mHistorySearchAdapter);
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.histroyRecyerView.getParent(),false);
        mHistorySearchAdapter.setEmptyView(emptyViewbindView.getRoot());
        emptyViewbindView.ivItemIcon.setImageResource(R.drawable.iv_search_empty);
        emptyViewbindView.tvItemName.setText("没有搜索记录~");
        /**
         * 删除单条搜索记录
         */
        mHistorySearchAdapter.setOnDeleteOneItemListener(new HistorySearchAdapter.OnDeleteOneItemListener() {
            @Override
            public void onDeleteOneData(int poistion) {
                deleteHistoryOne(poistion);
            }

            @Override
            public void onSearch(String key,int type) {
                if(null!=mSearchActivity&&!mSearchActivity.isFinishing()){
                    mSearchActivity.onSearch(key, type,0);
                }
            }
        });
        initFooterView();
    }

    /**
     * 删除和添加脚部
     */
    private void initFooterView() {

        List<SearchAutoResult> data = mHistorySearchAdapter.getData();
        if(mHistorySearchAdapter.getFooterViewsCount()<=0){
            if(null!=data&&data.size()>0){
                mCanelLayoutBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.search_histry_canel_layout, (ViewGroup) bindingView.histroyRecyerView.getParent(),false);
                mHistorySearchAdapter.addFooterView(mCanelLayoutBinding.getRoot());
                mCanelLayoutBinding.llEmpty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emptyHistoryList();
                    }
                });
            }
        }else{
            if(null==data||data.size()<=0){
                if(mHistorySearchAdapter.getFooterViewsCount()>0){
                    mHistorySearchAdapter.removeFooterView(mCanelLayoutBinding.getRoot());
                }
            }
        }
    }


    /**
     * 删除一条搜索记录
     * @param poistion
     */
    private void deleteHistoryOne(int poistion) {
        List<SearchAutoResult> data = mHistorySearchAdapter.getData();
        if(null!=data&&data.size()>0){
            for (int i = 0; i < data.size(); i++) {
                if(poistion==i){
                    mHistorySearchAdapter.remove(poistion);
                }
            }
        }
        //保存最新的历史纪录
        SearchCacheManager.getInstance().saveHistoryList(mHistorySearchAdapter.getData(),0);
        initFooterView();
    }

    /**
     * 清空所有搜索记录
     */
    private void emptyHistoryList() {
        if(null!=mSearchHistoeyList){
            mSearchHistoeyList.clear();
        }
        //将集合置空
        mHistorySearchAdapter.setNewData(mSearchHistoeyList);
        SearchCacheManager.getInstance().saveHistoryList(mHistorySearchAdapter.getData(),0);
        initFooterView();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        if(null!=mvideo_list&&mvideo_list.size()>=3){
            mSearchVideoListAdapter.setEnableLoadMore(true);
            loadMoreVideoList();
        }else{
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    if(!Utils.isCheckNetwork()){
                        mSearchVideoListAdapter.loadMoreFail();//加载失败
                    }else{
                        mSearchVideoListAdapter.loadMoreEnd();//加载为空
                    }
                }
            });
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        WindowVideoPlayer.releaseAllVideos();
    }


    @Override
    protected void onInvisible() {
        WindowVideoPlayer.releaseAllVideos();
        super.onInvisible();
    }


    /**
     * 举报用户回调
     * @param data
     */
    @Override
    public void showReportUserResult(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(jsonObject.getString("msg"),Constant.REPORT_USER_RESULT)){
                ToastUtils.showCenterToast(jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  举报视频回调
     * @param data
     */
    @Override
    public void showReportVideoResult(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(jsonObject.getString("msg"),Constant.REPORT_USER_RESULT)){
                ToastUtils.showCenterToast(jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载更多视频为空
     * @param data
     */
    @Override
    public void showMoreVideoListEmpty(String data) {

        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchVideoListAdapter.loadMoreEnd();
            }
        });

        if(mPage>1){
            mPage--;
        }
    }

    /**
     * 加载更多视频失败
     */
    @Override
    public void showMoreVideoListError() {

        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchVideoListAdapter.loadMoreFail();
            }
        });

        if(mPage>1){
            mPage--;
        }
    }

    /**
     * 加载更多视频数据的数据成功回调
     * @param data
     */
    @Override
    public void showMoreVideoList(SearchResultInfo data) {

        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchVideoListAdapter.loadMoreComplete();
            }
        });
        updataAddDataAdapter(data.getData().getVideo_list());
    }


    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    /**
     * 切换视图
     * @param b
     */
    private void switchView(boolean  b) {
        if(null!=bindingView){
            bindingView.histroyRecyerView.setVisibility(b?View.GONE:View.VISIBLE);
            bindingView.recyerView.setVisibility(b?View.VISIBLE:View.GONE);
        }
    }

    /**
     * 返回当前界面结果是否正在显示
     * @return
     */
    public boolean resultIsShow() {
        return null==bindingView?false:bindingView.recyerView.getVisibility()==View.VISIBLE;
    }

    /**
     * 刷新历史搜索记录
     */
    public void updataHistoyList() {
        if(null!=mSearchHistoeyList) mSearchHistoeyList.clear();
        mSearchHistoeyList = SearchCacheManager.getInstance().getSearchHistoeyList(0);
        if(null!=mHistorySearchAdapter){
            mHistorySearchAdapter.setNewData(mSearchHistoeyList);
            initFooterView();
        }
    }

    /**
     * 刷新搜索结果
     */
    public void updataSearchResultAdapter(int page) {
        switchView(true);
        initSearchResultAdapter(page);
        //两个中有一个不为空
        boolean size=Utils.changeListVolume(mvideo_list,mUser_list);
        if(!size){
            switchView(false);
            showErrorToast(null,null,"未搜索到相关内容");
        }
        //每次刷新重置参数
        if(null!=mSearchActivity&&!mSearchActivity.isFinishing()){
            SearchParams searchParams = mSearchActivity.getSearchParams();
            createParams(searchParams);
        }
    }

    /**
     * 订阅播放结果，以刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangingViewEvent event) {
        if(null==event) return;
        if(Constant.FRAGMENT_TYPE_SEARCH!=event.getFragmentType())return;
        if(event.isFixedPosition()){
            int poistion = event.getPoistion();
            if(null!=mGridLayoutManager&&null!=mSearchVideoListAdapter&&null!=mSearchVideoListAdapter.getData()&&mSearchVideoListAdapter.getData().size()>(poistion-1)){
                mGridLayoutManager.scrollToPosition(poistion);
            }
        }
    }

    /**
     * 切换显示图层
     */
    public void switchShowView() {
        switchView(false);
    }


    @Override
    public void onDestroy() {
        if(null!=mvideo_list)mvideo_list.clear(); mvideo_list=null;
        if(null!=mUser_list)mUser_list.clear(); mUser_list=null;
        if(null!=mSearchHistoeyList)mSearchHistoeyList.clear(); mSearchHistoeyList=null;
        if(null!=mSearchVideoListAdapter) mSearchVideoListAdapter.setNewData(null);
        if(null!=mHistorySearchAdapter) mHistorySearchAdapter.setNewData(null);
        if(null!=mHorUserListAdapter) mHorUserListAdapter.setNewData(null);
        mHistorySearchAdapter=null;mSearchVideoListAdapter=null;mHorUserListAdapter=null;
        headerLayoutBinding=null;
        mPresenter =null;searchKey=null;mCanelLayoutBinding=null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onAuthorClick(String userID) {
        AuthorDetailsActivity.start(getActivity(),userID);
    }


    @Override
    public void onItemClick(int position) {
        MobclickAgent.onEvent(getActivity(), "click_play");
        if(null!=mSearchVideoListAdapter){
            List<SearchResultInfo.DataBean.VideoListBean> data = mSearchVideoListAdapter.getData();
            if(null!= data && data.size()>0){
                //全屏
                if(ConfigSet.getInstance().isPlayerModel()){
                    //改成获取当前视频的
                    try{
                        FollowVideoList.DataBean dataBean=new FollowVideoList.DataBean();
                        List<FollowVideoList.DataBean.ListsBean> videoListBeenList=new ArrayList<>();
                        for (SearchResultInfo.DataBean.VideoListBean video: data) {
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
                        dataBean.setLists(videoListBeenList);
                        FollowVideoList followVideoList=new FollowVideoList();
                        followVideoList.setData(dataBean);
                        String json = JSONArray.toJSON(followVideoList).toString();
                        //这个界面跳转过去不需要加载更多
                        if(!TextUtils.isEmpty(json)){
                            Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_SEARCH);
                            intent.putExtra(Constant.KEY_POISTION,position-1);
                            intent.putExtra(Constant.KEY_PAGE,mPage);
                            intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                            intent.putExtra(Constant.KEY_JSON,json);
                            startActivity(intent);
                        }
                    }catch (Exception e){

                    }
                    //单个
                }else{
                    SearchResultInfo.DataBean.VideoListBean videoListBean = data.get(position-1);
                    if(null!=videoListBean&&!TextUtils.isEmpty(videoListBean.getVideo_id())){
                        saveLocationHistoryList(videoListBean);
                        VideoDetailsActivity.start(getActivity(),videoListBean.getVideo_id(),videoListBean.getUser_id(),false);
                    }
                }
            }
        }
    }


    private void saveLocationHistoryList(final SearchResultInfo.DataBean.VideoListBean data) {
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
}
