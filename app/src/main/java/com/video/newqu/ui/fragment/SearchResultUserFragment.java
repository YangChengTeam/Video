package com.video.newqu.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.HistorySearchAdapter;
import com.video.newqu.adapter.SearchResultUserAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.SearchAutoResult;
import com.video.newqu.bean.SearchParams;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.FragmentSearchLayoutBinding;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.databinding.SearchHistryCanelLayoutBinding;
import com.video.newqu.manager.SearchCacheManager;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.SearchActivity;
import com.video.newqu.ui.contract.SearchUserContract;
import com.video.newqu.ui.presenter.SearchUserPresenter;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/8 16:20
 * 搜索结果展示，全部
 */

public class SearchResultUserFragment extends BaseFragment<FragmentSearchLayoutBinding,SearchUserPresenter> implements BaseQuickAdapter.RequestLoadMoreListener, SearchUserContract.View
            ,com.video.newqu.listener.OnItemClickListener{

    private List<SearchAutoResult> mSearchHistoeyList;
    private HistorySearchAdapter mHistorySearchAdapter;
    private SearchResultUserAdapter mSearchResultUserAdapter;
    private SearchActivity mSearchActivity;
    private List<SearchResultInfo.DataBean.UserListBean> mUser_list;
    private String searchKey;
    private int searchKeyType=1;//用户
    private int mPage=0;
    private int mPageSize=10;
    private SearchHistryCanelLayoutBinding mCanelLayoutBinding;


    public static SearchResultUserFragment newInstance(){
        SearchResultUserFragment listFragment=new SearchResultUserFragment();
        return listFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSearchActivity= (SearchActivity) context;
    }

    @Override
    protected void initViews() {
        mPresenter = new SearchUserPresenter(getActivity());
        mPresenter.attachView(this);
        try {
            initSearchResultAdapter();
            initHistoryListAdapter();
        }catch (Exception e){

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_layout;
    }

    /**
     * 初始化搜索结果适配器
     */
    private void initSearchResultAdapter() {
        if(null!=mSearchActivity&&!mSearchActivity.isFinishing()){
            if(null!=mSearchActivity.getSearchResultData()){
                mUser_list = mSearchActivity.getSearchResultData().getUser_list();
            }
            if(null==mSearchResultUserAdapter){
                bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mSearchResultUserAdapter = new SearchResultUserAdapter(getActivity(),mUser_list,SearchResultUserFragment.this);
                bindingView.recyerView.setAdapter(mSearchResultUserAdapter);
                RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
                mSearchResultUserAdapter.setEmptyView(emptyViewbindView.getRoot());
                emptyViewbindView.ivItemIcon.setImageResource(R.drawable.ic_list_empty_icon);
                emptyViewbindView.tvItemName.setText("没有搜索到相关用户~");
                mSearchResultUserAdapter.setOnLoadMoreListener(this);

                //未登录
                mSearchResultUserAdapter.setOnFollowUserListener(new SearchResultUserAdapter.onFollowUserListener() {
                    @Override
                    public void onFollowUserLogin() {
                        if(null== VideoApplication.getInstance().getUserData()){
                            if(null!=mSearchActivity) mSearchActivity.login();
                        }
                    }
                });
            }else{
                mSearchResultUserAdapter.setNewData(mUser_list);
            }
            if(null==mUser_list||mUser_list.size()<=0){
                switchView(false);
            }
        }
    }

    /**
     * 仅增加新数据
     */
    private void updataAddDataAdapter() {
        mSearchResultUserAdapter.addData(mUser_list);
    }


    /**
     * 条目的点击事件
     * @param position
     */
    @Override
    public void OnItemClick(int position) {
        List<SearchResultInfo.DataBean.UserListBean> userListBeanList =mSearchResultUserAdapter.getData();
        SearchResultInfo.DataBean.UserListBean userListBean = userListBeanList.get(position);
        AuthorDetailsActivity.start(getActivity(),userListBean.getId());
    }


    /**
     * 初始化搜索历史记录全部的搜索记录
     */
    private void initHistoryListAdapter() {
        mSearchHistoeyList = SearchCacheManager.getInstance().getSearchHistoeyList(1);
        bindingView.histroyRecyerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHistorySearchAdapter = new HistorySearchAdapter(mSearchHistoeyList);
        bindingView.histroyRecyerView.setAdapter(mHistorySearchAdapter);
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
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
                    mSearchActivity.onSearch(key, type,1);
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
        SearchCacheManager.getInstance().saveHistoryList(mHistorySearchAdapter.getData(),1);
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
        SearchCacheManager.getInstance().saveHistoryList(mHistorySearchAdapter.getData(),1);
        initFooterView();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        mSearchResultUserAdapter.setEnableLoadMore(true);
        loadMoreUserList();
    }

    /**
     * 加载更多用户
     */
    private void loadMoreUserList() {
        mPage++;
        mPresenter.getMoreUserList(searchKey,searchKeyType+"",mPage+"",mPageSize+"");
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
     * 初始化参数
     * @param searchParams
     */
    private void createParams(SearchParams searchParams) {
        this.searchKey=searchParams.getSearchKey();
        this.searchKeyType=1;//用户
        this.mPage=searchParams.getPage();
        this.mPageSize=searchParams.getPageSize();
    }


    /**
     * 加载更多用户为空
     * @param data
     */
    @Override
    public void showMoreUserListEmpty(String data) {

        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchResultUserAdapter.loadMoreEnd();
            }
        });
        if(mPage>1){
            mPage--;
        }
    }

    /**
     * 加载更多用户失败
     */
    @Override
    public void showMoreUserListError() {

        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchResultUserAdapter.loadMoreFail();
            }
        });
        if(mPage>1){
            mPage--;
        }
    }

    /**
     * 加载更多用户成功
     * @param data
     */
    @Override
    public void showMoreUserList(SearchResultInfo data) {
        bindingView.recyerView.post(new Runnable() {
            @Override
            public void run() {
                mSearchResultUserAdapter.loadMoreComplete();
            }
        });
        if(null!=mUser_list){
            mUser_list.addAll(data.getData().getUser_list());
            updataAddDataAdapter();
        }
    }


    /**
     * 显示关注结果
     * @param text
     */
    @Override
    public void showFollowUser(String text) {

    }

    /**
     * 显示加载数据失败
     */
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    /**
     * 刷新历史搜索记录
     */
    public void updataHistoyList() {
        mSearchHistoeyList = SearchCacheManager.getInstance().getSearchHistoeyList(1);
        if(null!=mHistorySearchAdapter){
            mHistorySearchAdapter.setNewData(mSearchHistoeyList);
            initFooterView();
        }
    }

    /**
     * 刷新搜索结果
     */
    public void updataSearchResultAdapter() {
        switchView(true);
        initSearchResultAdapter();
        //每次刷新重置参数
        if(null!=mSearchActivity&&!mSearchActivity.isFinishing()){
            SearchParams searchParams = mSearchActivity.getSearchParams();
            createParams(searchParams);
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
        if(null!=mUser_list) mUser_list.clear();mUser_list=null;
        if(null!=mSearchResultUserAdapter)mSearchResultUserAdapter.setNewData(null);
        if(null!=mHistorySearchAdapter)mHistorySearchAdapter.setNewData(null);
        if(null!=mSearchHistoeyList) mSearchHistoeyList.clear();mSearchHistoeyList=null;
        mSearchResultUserAdapter=null;mHistorySearchAdapter=null;searchKey=null;
        mPresenter =null;mCanelLayoutBinding=null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchActivity = null;
    }
}
