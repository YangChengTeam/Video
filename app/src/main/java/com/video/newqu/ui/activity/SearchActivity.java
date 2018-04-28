package com.video.newqu.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.adapter.AutoCompletSearchAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.SearchAutoResult;
import com.video.newqu.bean.SearchParams;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivitySearchBinding;
import com.video.newqu.listener.ShareFinlishListener;
import com.video.newqu.manager.SearchCacheManager;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.ui.contract.SearchContract;
import com.video.newqu.ui.fragment.SearchResultAllFragment;
import com.video.newqu.ui.fragment.SearchResultUserFragment;
import com.video.newqu.ui.presenter.SearchPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.InputTools;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.MyFragmentPagerAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-07 19:37
 * 搜索
 */

public class SearchActivity extends BaseActivity<ActivitySearchBinding> implements SearchContract.View, TextWatcher, TextView.OnEditorActionListener {

    private SearchPresenter mSearchPresenter;
    private Animation mInputAnimation;
    private AutoCompletSearchAdapter mAutoCompletSearchAdapter;
    private List<Fragment> mFragmentList;
    private List<String> mStringList;
    //提供方法给子界面获取最新数据
    private List<SearchAutoResult> mSearchAutoResults=new ArrayList<>();
    private int currenFragmentIndex=0;//当前显示的角标
    private SearchResultInfo.DataBean mSearchResultData=new SearchResultInfo.DataBean();//搜索结果
    private SearchResultAllFragment mSearchResultAllFragment;
    //刚才搜索的关键字
    private String searchKey;
    private int searchKeyType;
    private int mPage=0;
    private int mPageSize=10;
    private int searchCurrenFragmentIndex;
    private SearchResultUserFragment mSearchResultUserFragment;
    private boolean keyAutomatic=true;//是否是自动联想
    private ListPopupWindow mListPopupWindow;

    public SearchResultInfo.DataBean getSearchResultData() {
        return mSearchResultData;
    }

    /**
     * 将搜索的参数返回给需要的子Fragnment
     * @return
     */
    public SearchParams getSearchParams() {
        SearchParams searchParams=new SearchParams();
        searchParams.setPage(mPage);
        searchParams.setPageSize(mPageSize);
        searchParams.setSearchCurrenFragmentIndex(searchCurrenFragmentIndex);
        searchParams.setSearchKey(searchKey);
        searchParams.setSearchKeyType(searchKeyType);
        return searchParams;
    }

    public static void start(Activity context, String key, View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("key",key);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        searchKey = getIntent().getStringExtra("key");
        setContentView(R.layout.activity_search);
        showToolBar(false);
        int minHeight=0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            minHeight= SystemUtils.getStatusBarHeight(this);
            if(minHeight<=0){
                minHeight= ScreenUtils.dpToPxInt(25);
            }
        }
        View stateView = findViewById(R.id.view_state_bar);
        stateView.getLayoutParams().height=minHeight;
//        stateView.setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
        StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        mSearchPresenter = new SearchPresenter(this);
        mSearchPresenter.attachView(this);
        initFragments();
        initAdapter();
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        //如果过来的携带有关键字，回显至搜索栏
        if(!TextUtils.isEmpty(searchKey)){
            keyAutomatic=false;//非自动联想，确保不会重复搜索
            bindingView.searchEtInput.setText(searchKey);
            bindingView.searchEtInput.setSelection(searchKey.length());//回显到输入框
            keyAutomatic=true;
        }
    }


    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //搜索
                    case R.id.tv_search:
                        searchKey(bindingView.searchEtInput.getText().toString().trim(),2,currenFragmentIndex);
                        break;
                    //清空输入框
                    case R.id.search_iv_delete:
                        deleteInput();
                        break;
                    //返回
                    case R.id.iv_back:
                        onBackPressed();
                        break;
                }
            }
        };
        bindingView.tvSearch.setOnClickListener(onClickListener);
        bindingView.searchIvDelete.setOnClickListener(onClickListener);
        bindingView.ivBack.setOnClickListener(onClickListener);
        bindingView.searchEtInput.addTextChangedListener(this);
        bindingView.searchEtInput.setOnEditorActionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mSearchPresenter){
            mSearchPresenter.detachView();
        }
        if(null!=mAutoCompletSearchAdapter)mAutoCompletSearchAdapter.setNewData(null);
        if(null!=mSearchAutoResults) mSearchAutoResults.clear();
        mSearchAutoResults=null; mAutoCompletSearchAdapter=null;
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(null!=bindingView&&!TextUtils.isEmpty(searchKey)) {
            bindingView.searchEtInput.setText(searchKey);//专场动画返回时候原样返回效果
            bindingView.searchEtInput.setSelection(searchKey.length());
        }
    }

    /**
     * 提供给子界面的登录方法
     */
    public void login(){
        Intent intent=new Intent(SearchActivity.this,LoginGroupActivity.class);
        startActivityForResult(intent,Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录意图，需进一步确认
        if(Constant.INTENT_LOGIN_EQUESTCODE==requestCode&&resultCode==Constant.INTENT_LOGIN_RESULTCODE){
            if(null!=data){
                boolean booleanExtra = data.getBooleanExtra(Constant.INTENT_LOGIN_STATE, false);
                //登录成功,刷新子界面
                if(booleanExtra){
                    //刷新登录后的列表
                    searchKey(searchKey,searchKeyType,searchCurrenFragmentIndex);
                }
            }
        }
    }





    @Override
    public void initData() {

    }

    private void initAdapter() {
        MyFragmentPagerAdapter myFragmentPagerAdapter =new MyFragmentPagerAdapter(getSupportFragmentManager(),mFragmentList,mStringList);
        bindingView.viewPager.setAdapter(myFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currenFragmentIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 初始化
     */
    private void initFragments() {
        mFragmentList=new ArrayList<>();
        mStringList=new ArrayList<>();
        mSearchResultAllFragment = SearchResultAllFragment.newInstance();
        mSearchResultUserFragment = SearchResultUserFragment.newInstance();
        mFragmentList.add(mSearchResultAllFragment);
        mFragmentList.add(mSearchResultUserFragment);
        mStringList.add("全部");
        mStringList.add("用户");
    }


    /**
     * 这个界面由子界面触发
     * @param key 关键字
     * @param keyType 关键字类型
     * @param searchType 搜索词汇类型
     */
    public void onSearch(String key, int keyType, int searchType) {
        if(TextUtils.isEmpty(key)) return;
        //每次搜索还原参数
        if(!Utils.isCheckNetwork()) return;
        mPage=0;
        searchKeyType=keyType;
        keyAutomatic=false;
        bindingView.searchEtInput.setText(key);
        bindingView.searchEtInput.setSelection(key.length());
        seaveSearchHistoryList(new SearchAutoResult(key,keyType),searchType);
        keyAutomatic=true;
        searchResylt(key);
    }


    /**
     * 保存历史搜索记录，最多20条，且不重复,根据当前用户搜索的类型来保存
     * @param searchAutoResult searchAutoResult.getType()  0:用户 1：视频 2:未知
     * @param type 搜索的类型，0:用户 1：视频
     *
     */
    private void seaveSearchHistoryList(SearchAutoResult searchAutoResult, int type) {

        List<SearchAutoResult> list = SearchCacheManager.getInstance().getSearchHistoeyList(type);
        if (list == null) {
            list = new ArrayList<>();
            list.add(searchAutoResult);
        } else {
            Iterator<SearchAutoResult> iterator = list.iterator();
            while (iterator.hasNext()) {
                SearchAutoResult autoResult = iterator.next();
                if (TextUtils.equals(searchAutoResult.getKey(), autoResult.getKey())) {
                    iterator.remove();
                }
            }
            list.add(0, searchAutoResult);
        }
        int size = list.size();
        if (size > 50) { // 最多保存50条
            for (int i = size - 1; i >= 50; i--) {
                list.remove(i);
            }
        }
        SearchCacheManager.getInstance().saveHistoryList(list,currenFragmentIndex);
        //刷新搜索历史记录
        updataHistory();
    }





    /**
     * 搜索关键字
     * @param key
     * @param keyType 0:用户 1：视频 2:未知
      * @param currenFragmentIndex
     */
    private void searchKey(String key, int keyType, int currenFragmentIndex) {
        if(!Utils.isCheckNetwork()) return;
        if(TextUtils.isEmpty(key)){
            bindingView.searchEtInput.startAnimation(mInputAnimation);
            ToastUtils.showCenterToast("搜索词条不能为空");
            return;
        }
        this.searchKey=key;
        this.searchKeyType=keyType;
        this.searchCurrenFragmentIndex=currenFragmentIndex;
        //每次搜索还原参数
        mPage=0;
        seaveSearchHistoryList(new SearchAutoResult(key,keyType),currenFragmentIndex);
        searchKeyType=keyType;
        searchResylt(key);
    }

    /**
     * 开始搜索
     * @param key
     */
    private void searchResylt(String key) {
        if(null!=mSearchPresenter&&!mSearchPresenter.isSearch()){
            InputTools.closeKeybord(bindingView.searchEtInput);//关闭软键盘
            showProgressDialog("搜索中...",true);
            searchKey=key;
            mPage++;
            mSearchPresenter.getSearchReachResult(key,0==currenFragmentIndex?"0":"1",mPage+"",mPageSize+"");
        }
    }

    /**
     * 清空输入框
     */
    private void deleteInput() {
        bindingView.searchEtInput.setText("");
    }

    /**
     * 搜索热匹配
     * @param key
     */
    private void searchAuto(CharSequence key) {
        if(!Utils.isCheckNetwork()) return;
        if(null!=mSearchPresenter&&!mSearchPresenter.isOutIsSearch()) mSearchPresenter.getAutoSearchReachResult(key.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(null!=mSearchResultAllFragment&&mSearchResultAllFragment.resultIsShow()){
                switchChildView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    /**
     * 输入框的监听
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence key, int start, int before, int count) {
        if(!TextUtils.isEmpty(key)&&key.length()>0){
            bindingView.searchIvDelete.setVisibility(View.VISIBLE);
            if(keyAutomatic){
                searchAuto(key);
            }
        }else{
            bindingView.searchIvDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 搜索热匹配结果回调
     * @param data
     */
    @Override
    public void showAutoSearcRelsult(SearchResultInfo data) {
        if(null!=bindingView&&!TextUtils.isEmpty(bindingView.searchEtInput.getText().toString().trim())){
            if(null!=mSearchAutoResults){
                mSearchAutoResults.clear();
            }else{
                mSearchAutoResults=new ArrayList<>();
            }
            if(null!=data&&null!=data.getData()&&null!=data.getData().getUser_list()&&data.getData().getUser_list().size()>0){
                List<SearchResultInfo.DataBean.UserListBean> user_list = data.getData().getUser_list();//作者
                for (SearchResultInfo.DataBean.UserListBean userListBean : user_list) {
                    mSearchAutoResults.add(new SearchAutoResult(userListBean.getNickname(),0));
                }
            }
            if(null!=data&&null!=data.getData()&&null!=data.getData().getVideo_list()&&data.getData().getVideo_list().size()>0){
                List<SearchResultInfo.DataBean.VideoListBean> video_list = data.getData().getVideo_list();//视频
                for (SearchResultInfo.DataBean.VideoListBean videoListBean : video_list) {
                    mSearchAutoResults.add(new SearchAutoResult(videoListBean.getDesp(),1));
                }
            }
            updataAutoAdapter();
        }
    }

    /**
     * 搜索结果回调
     * @param data
     */
    @Override
    public void showSearcRelsult(SearchResultInfo data) {
        closeProgressDialog();
        mSearchResultData = data.getData();
        updataSearchResultAdapter();
    }


    /**
     * 刷新子界面刷新搜索的历史记录
     */
    private void updataHistory() {
        if(null!=mSearchResultAllFragment) mSearchResultAllFragment.updataHistoyList();
        if(null!=mSearchResultUserFragment) mSearchResultUserFragment.updataHistoyList();
    }


    /**
     * 刷新子界面显示图层
     */
    private void switchChildView() {
        if(null!=mSearchResultAllFragment) mSearchResultAllFragment.switchShowView();
        if(null!=mSearchResultUserFragment) mSearchResultUserFragment.switchShowView();
    }


    /**
     * 刷新子界面的搜索结果适配器
     */
    private void updataSearchResultAdapter() {
        if(null!=mSearchResultAllFragment)  mSearchResultAllFragment.updataSearchResultAdapter(mPage);
        if(null!=mSearchResultUserFragment)  mSearchResultUserFragment.updataSearchResultAdapter();
    }


    @Override
    public void showErrorView() {
        closeProgressDialog();
        ToastUtils.showCenterToast("换个词试试吧~!");
    }

    @Override
    public void complete() {
        closeProgressDialog();
    }

    /**
     * 刷新热更新适配器
     */
    private void updataAutoAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null==mAutoCompletSearchAdapter){
                    mAutoCompletSearchAdapter = new AutoCompletSearchAdapter(SearchActivity.this,mSearchAutoResults);
                }else{
                    mAutoCompletSearchAdapter.setNewData(mSearchAutoResults);
                }
                if(null==mListPopupWindow){
                    mListPopupWindow = new ListPopupWindow(SearchActivity.this);
                    mListPopupWindow.setAdapter(mAutoCompletSearchAdapter);
                    mListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mListPopupWindow.setAnchorView(bindingView.searchBar);
                    mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mListPopupWindow.dismiss();
                            if(null!=mAutoCompletSearchAdapter){
                                List<SearchAutoResult> data = mAutoCompletSearchAdapter.getData();
                                SearchAutoResult searchAutoResult = data.get(position);
                                if(null!=searchAutoResult){
                                    searchKey(searchAutoResult.getKey(), searchAutoResult.getType(), currenFragmentIndex);
                                }
                            }
                        }
                    });
                }
                mListPopupWindow.show();
            }
        });
    }

    /**
     * 对输入法的回车键监听
     * @param v
     * @param actionId
     * @param event
     * @return
     */

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
            String text = bindingView.searchEtInput.getText().toString().trim();
            searchKey(text, 2, currenFragmentIndex);
            return true;
        }
        return false;
    }

    /**
     * 分享
     * @param shareInfo
     * @param shareFinlishListener
     */
    public void shareIntent(ShareInfo shareInfo, ShareFinlishListener shareFinlishListener) {
        onShare(shareInfo,shareFinlishListener);
    }
}
