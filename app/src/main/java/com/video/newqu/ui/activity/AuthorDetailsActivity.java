package com.video.newqu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.GroupVideoListAdapter;
import com.video.newqu.adapter.UserVideoListAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.bean.VideoGroupList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityAuthorDetailsBinding;
import com.video.newqu.databinding.VideoDetailsEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.model.RecyclerViewSpacesItem;
import com.video.newqu.ui.contract.AuthorDetailContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.presenter.AuthorDetailPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.TimeUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeMarginView;
import com.video.newqu.view.widget.GlideCircleTransform;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/25 15:16
 * 作者个人中心详情界面--新特性版本
 */

public class AuthorDetailsActivity extends BaseActivity<ActivityAuthorDetailsBinding> implements AuthorDetailContract.View{

    private String mAuthorID;//用户ID
    private AuthorDetailPresenter mAuthorDetailPresenter;
    private MineUserInfo.DataBean.InfoBean mInfoBean;
    private int mPage=0;
    private int  mPageSize=20;
    private GroupVideoListAdapter mExpandedVideoListAdapter;//展开的
    private UserVideoListAdapter mVideoListAdapter;//九宫格
    private int mHeaderViewHeight;
    private VideoDetailsEmptyLayoutBinding mEmptyViewbindView;
    //分组
    private int headHeight;//分组头部高度
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private int mCurrentPosition = 0;
    private List<FollowVideoList.DataBean.ListsBean> mGroupList=null;//包含所有二维数组中的所有元素
    private boolean isExpanded;//是否展开，默认是九宫格
    private RecyclerViewSpacesItem mRecyclerViewSpacesItem;

    /**
     * 入口
     * @param context
     * @param authorID 用户ID
     */
    public static void start(Context context, String authorID) {
        Intent intent=new Intent(context,AuthorDetailsActivity.class);
        intent.putExtra("author_id",authorID);
        intent.putExtra("is_follow","");
        context.startActivity(intent);
    }

    /**
     * 入口
     * @param context
     * @param authorID 用户ID
     */
    public static void start(Context context, String authorID,String isFollow) {
        Intent intent=new Intent(context,AuthorDetailsActivity.class);
        intent.putExtra("author_id",authorID);
        intent.putExtra("is_follow",isFollow);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);
        showToolBar(false);
        initIntent();
        initAdapter();
        mAuthorDetailPresenter = new AuthorDetailPresenter(AuthorDetailsActivity.this);
        mAuthorDetailPresenter.attachView(this);
        mInfoBean= (MineUserInfo.DataBean.InfoBean) ApplicationManager.getInstance().getCacheExample().getAsObject(mAuthorID);
        initUserData();
        isMine(mAuthorID);
        loadUserInfo();//加载用户数据
    }


    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //点击别人，弹出举报弹窗，点击自己无选项
                    case R.id.iv_user_icon:
                        if(null==mInfoBean) return;
                        MediaSingerImagePreviewActivity.start(AuthorDetailsActivity.this,mInfoBean.getLogo(),bindingView.ivUserIcon);
                        break;
                    //关注
                    case R.id.re_add:
                        onFollowUser();
                        break;
                    //等级
                    case R.id.tv_user_grade:

                        break;
                    //返回
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                    //菜单
                    case R.id.iv_menu:
//                        onMenu();
                        showUserDetailsDataDialog();
                        break;
                    //粉丝
//                    case R.id.tv_fans_count:
//                        lookFans();
//                        break;
                    //关注
//                    case R.id.tv_follow_count:
//                        lookFollows();
//                        break;
                    //分享
                    case R.id.btn_share:
                        shareMineHome();
                        break;
                    //背景图片
                    case R.id.iv_user_image_bg_view:
                        if(null!=mInfoBean){
                            MediaSingerImagePreviewActivity.start(AuthorDetailsActivity.this,mInfoBean.getImage_bg(),bindingView.ivUserImageBg);
                        }
                        break;
                    //展开
                    case R.id.btn_expanded:
                        if(null==mVideoListAdapter) return;
                        List<FollowVideoList.DataBean.ListsBean> adapterList = mVideoListAdapter.getData();
                        if(null!=adapterList&&adapterList.size()>0){
                            isExpanded=!isExpanded;
                            setContentViewExpanedState(isExpanded);
                        }
                        break;
                }
            }
        };
        //标题栏上的按钮在父类那里
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.ivMenu.setOnClickListener(onClickListener);
        bindingView.btnShare.setOnClickListener(onClickListener);
        bindingView.ivUserIcon.setOnClickListener(onClickListener);
        bindingView.reAdd.setOnClickListener(onClickListener);
//        bindingView.tvFansCount.setOnClickListener(onClickListener);
//        bindingView.tvFollowCount.setOnClickListener(onClickListener);
        bindingView.ivUserImageBgView.setOnClickListener(onClickListener);
        bindingView.btnExpanded.setOnClickListener(onClickListener);

        bindingView.ivMenu.setVisibility(null!=VideoApplication.getInstance().getUserData()&&!TextUtils.isEmpty(mAuthorID)&&TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())?View.GONE:View.VISIBLE);
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        bindingView.llTopTitle.measure(width,width);
        bindingView.collapseToolbar.measure(width,width);
        ViewGroup.LayoutParams layoutParams = bindingView.reTopBar.getLayoutParams();
        layoutParams.width=RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height=  bindingView.llTopTitle.getMeasuredHeight();
        bindingView.reTopBar.setLayoutParams(layoutParams);
        bindingView.reTopBar.setBackgroundResource(R.drawable.home_top_bar_bg_shape);
        //min offset
        bindingView.collapseToolbar.setMinimumHeight(bindingView.llTopTitle.getMeasuredHeight());
        //变化值
        mHeaderViewHeight =bindingView.collapseToolbar.getMeasuredHeight();
        //当获取到数据，用户的介绍文字可能超过两行
        ViewTreeObserver viewTreeObserver = bindingView.collapseToolbar.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight=bindingView.collapseToolbar.getHeight();
                bindingView.collapseToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int abs = Math.abs(verticalOffset);
                //用户信息图层网上滑动，所以是相反的透明度
                float scale = (float) abs / (mHeaderViewHeight / 2);
                float alpha =(scale * 255);
                float barViewAlpha = Utils.absVakue(255f, alpha)/255f;
                //用户头像，登录View,用户资料View
                bindingView.userDataHeader.setAlpha(barViewAlpha);//采坑，这里的透明度必须是1。0-0.0之间的float类型

                if(abs<=mHeaderViewHeight/2){
                    //标题栏用户昵称
                    float textScale = (float) abs / (mHeaderViewHeight/2);
                    float textAlpha = (255 * textScale);
                    bindingView.tvTitleUserName.setTextColor(Color.argb((int) textAlpha, 255, 255, 255));//标题栏用户昵称
                }
                //界面用户昵称
                if(abs<=(mHeaderViewHeight/3)){
                    float textSubScale = (float) abs / (mHeaderViewHeight/3);
                    float textSubAlpha = (255 * textSubScale);
                    bindingView.tvSubtitleUserName.setTextColor(Color.argb((int) Utils.absValue(textSubAlpha,0,255), 255, 255, 255));//界面下边用户昵称
                }
                //处理标题栏背景图片渐变透明度
                float drawableAlpha =abs* 1.0f / (mHeaderViewHeight-bindingView.llTopTitle.getMeasuredHeight());
                float bgAlpha = drawableAlpha * 255;
                float v = bgAlpha / 225f;
                bindingView.reTopBar.setAlpha(v);
            }
        });
    }

    @Override
    public void initData() {}

    /**
     * 分享用户的主页
     */
    private void shareMineHome() {
        if(!TextUtils.isEmpty(mAuthorID)){
            ShareInfo shareInfo=new ShareInfo();
            String nikeName="";
            if(null!=mInfoBean){
                if(!TextUtils.isEmpty(mInfoBean.getNickname())){
                    try {
                        nikeName=URLDecoder.decode(mInfoBean.getNickname(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            shareInfo.setTitle("邀请你欣赏精彩视频");
            shareInfo.setDesp("我正在["+nikeName+"]的主页观看视频，精彩不容错过！");
            shareInfo.setUserID(mAuthorID);
            shareInfo.setImageLogo(mInfoBean.getLogo());
            shareInfo.setUrl("http://app.nq6.com/home/user/index?user_id="+shareInfo.getUserID());
            shareMineHome(shareInfo);
        }
    }

    /**
     * 初始化适配器，这里有两个不同样式的和数据结构的适配器
     */
    private void initAdapter() {
        List<FollowVideoList.DataBean.ListsBean> cacheList=(List<FollowVideoList.DataBean.ListsBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(mAuthorID + "_video_list");
        //两个适配器共用的配置信息
        mLinearLayoutManager= new LinearLayoutManager(AuthorDetailsActivity.this,LinearLayoutManager.VERTICAL,false);
        mGridLayoutManager = new GridLayoutManager(AuthorDetailsActivity.this, 3, GridLayoutManager.VERTICAL, false);
        mEmptyViewbindView = DataBindingUtil.inflate(AuthorDetailsActivity.this.getLayoutInflater(), R.layout.video_details_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new DataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null==mInfoBean){
                    mEmptyViewbindView.emptyView.showLoadingView();
                    loadUserInfo();//加载用户数据
                }else{
                    mEmptyViewbindView.emptyView.showLoadingView();
                    mPage=0;
                    loadVideoList();//直接加载用户发布的视频
                }
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();

        //时间轴Adapter
        //将本地缓存封装进全局
        if(null!=cacheList&&cacheList.size()>0) {
            if(null==mGroupList) mGroupList=new ArrayList<>();
            for (FollowVideoList.DataBean.ListsBean listsBean : cacheList) {
                mGroupList.add(listsBean);
            }
        }
        List<VideoGroupList> videoGroupLists = listToGroup(mGroupList);
        mExpandedVideoListAdapter = new GroupVideoListAdapter(videoGroupLists);
        mExpandedVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                List<VideoGroupList> data = mExpandedVideoListAdapter.getData();
                if(null!=data&&data.size()>3){
                    loadVideoList();
                }else{
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!Utils.isCheckNetwork()){
                                mExpandedVideoListAdapter.loadMoreFail();//加载失败
                            }else{
                                mExpandedVideoListAdapter.loadMoreEnd();//加载为空
                            }
                        }
                    });
                }
            }
        }, bindingView.recyerView);
        mExpandedVideoListAdapter.setOnItemClickListener(new GroupVideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MobclickAgent.onEvent(AuthorDetailsActivity.this, "click_play");
                if(null!=mGroupList&&mGroupList.size()>position){
                    //全屏
                    if(ConfigSet.getInstance().isPlayerModel()){
                        //改成获取当前视频的
                        try{
                            FollowVideoList.DataBean dataBean=new FollowVideoList.DataBean();
                            dataBean.setLists(mGroupList);
                            FollowVideoList followVideoList=new FollowVideoList();
                            followVideoList.setData(dataBean);
                            String json = JSONArray.toJSON(followVideoList).toString();
                            if(!TextUtils.isEmpty(json)) {
                                Intent intent=new Intent(AuthorDetailsActivity.this,VerticalVideoPlayActivity.class);
                                intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_VERTICAL_AUTHOR);
                                intent.putExtra(Constant.KEY_POISTION,position);
                                intent.putExtra(Constant.KEY_PAGE,mPage);
                                intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                                intent.putExtra(Constant.KEY_JSON,json);
                                startActivity(intent);
                                return;
                            }
                        }catch (Exception e){

                        }
                        //单个
                    }else{
                        FollowVideoList.DataBean.ListsBean listsBean = mGroupList.get(position);
                        if(null!=listsBean&&!TextUtils.isEmpty(listsBean.getVideo_id())){
                            saveLocationHistoryList(listsBean);
                            VideoDetailsActivity.start(AuthorDetailsActivity.this,listsBean.getVideo_id(),listsBean.getUser_id(),false);
                        }
                    }
                }
            }
        });

        //九宫格Adapter
        mVideoListAdapter = new UserVideoListAdapter(cacheList,3,null);
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                List<FollowVideoList.DataBean.ListsBean> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>3){
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
        }, bindingView.recyerView);
        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(AuthorDetailsActivity.this, "click_play");
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
                                    Intent intent=new Intent(AuthorDetailsActivity.this,VerticalVideoPlayActivity.class);
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_USER_DETAILS);
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
                                VideoDetailsActivity.start(AuthorDetailsActivity.this,listsBean.getVideo_id(),listsBean.getUser_id(),false);
                            }
                        }
                    }
                }
            }
        });

        //设定播放器样式
        setContentViewExpanedState(isExpanded);

        //滚动点监听，实现分组悬浮，只在九宫格样式中生效
        bindingView.recyerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当recyclerView的滑动改变改变的时候 实时拿到它的高度
                headHeight = bindingView.tvHeaderView.getMeasuredHeight();
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isExpanded&&null!=mLinearLayoutManager){
                    View itemView = mLinearLayoutManager.findViewByPosition(mCurrentPosition + 1);
                    if (itemView != null) {
                        // 100      110  10
                        if (itemView.getTop() <= headHeight) {
                            bindingView.tvHeaderView.setY(-(headHeight - itemView.getTop()));
                        } else {
                            bindingView.tvHeaderView.setY(0);
                        }
                    }
                    //拿到当前显示的item的position
                    int currentPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (mCurrentPosition != currentPosition) {
                        mCurrentPosition = currentPosition;
                        bindingView.tvHeaderView.setY(0);
                        setHeaderData(mCurrentPosition);
                    }
                }
            }
        });
        //设置悬浮得HeaderView数据
        List<VideoGroupList> data = mExpandedVideoListAdapter.getData();
        if(null!=data&&data.size()>0){
            setHeaderData(mCurrentPosition);
        }
    }

    /**
     * 设置界面展示风格
     * @param expanedState true：日期轴展示 false：九宫格
     */
    private void setContentViewExpanedState(boolean expanedState) {
        //避免重复切换引起的间距拉开问题
        if(null!=mRecyclerViewSpacesItem){
            bindingView.recyerView.removeItemDecoration(mRecyclerViewSpacesItem);
        }
        bindingView.recyerView.setAdapter(null);
        //为时间轴样式下且界面有数据，显示悬浮头部
        if(expanedState){
            if(null!= mExpandedVideoListAdapter){
                List<VideoGroupList> data = mExpandedVideoListAdapter.getData();
                if(null!=data&&data.size()>0){
                    bindingView.tvHeaderView.setVisibility(View.VISIBLE);
                }
                bindingView.recyerView.setLayoutManager(mLinearLayoutManager);
                bindingView.recyerView.setAdapter(mExpandedVideoListAdapter);
            }
        }else{
            if(null==mRecyclerViewSpacesItem){
                mRecyclerViewSpacesItem = new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1));
            }
            bindingView.tvHeaderView.setVisibility(View.GONE);
            bindingView.recyerView.setLayoutManager(mGridLayoutManager);
            bindingView.recyerView.addItemDecoration(mRecyclerViewSpacesItem);
            bindingView.recyerView.setAdapter(mVideoListAdapter);
        }
        bindingView.btnExpanded.setImageResource(expanedState?R.drawable.ic_btn_pack:R.drawable.ic_btn_expanded);
    }

    //设置Header数据
    private void setHeaderData(int currentPosition) {
        if(null!= mExpandedVideoListAdapter){
            List<VideoGroupList> data = mExpandedVideoListAdapter.getData();
            if(null!=data&&data.size()>0){
                VideoGroupList videoGroupList = data.get(currentPosition);
                if(null!=videoGroupList){
                    bindingView.tvHeaderView.setText(videoGroupList.getDateTime()+"发布");
                }
            }
        }
    }

    /**
     * 为两个适配器设置新的数据
     * @param data
     */
    //时间轴是整个重新刷新（为了保证item的index唯一性），九宫格是分页加载
    private void setNewDataToAdapter(List<FollowVideoList.DataBean.ListsBean> data) {
        //九宫格
        if(null!=mVideoListAdapter){
            mVideoListAdapter.setNewData(data);
            mVideoListAdapter.loadMoreComplete();
        }
        //时间轴
        if(null!= mExpandedVideoListAdapter){
            if(null!=mGroupList) mGroupList.clear();
            if(null==mGroupList) mGroupList=new ArrayList<>();
            mGroupList.addAll(data);
            List<VideoGroupList> videoGroupLists = listToGroup(mGroupList);
            mExpandedVideoListAdapter.setNewData(videoGroupLists);
            mExpandedVideoListAdapter.loadMoreComplete();
        }
        setHeaderData(mCurrentPosition);//仅当为全新的数据才会设置Header数据
    }

    /**
     * 为适配器添加新的数据
     * @param data
     */
    private void addDataToAdapter(List<FollowVideoList.DataBean.ListsBean> data) {
        //九宫格
        if(null!=mVideoListAdapter){
            mVideoListAdapter.addData(data);
            mVideoListAdapter.loadMoreComplete();
        }
        //时间轴
        if(null!= mExpandedVideoListAdapter){
            if(null==mGroupList) mGroupList=new ArrayList<>();
            for (FollowVideoList.DataBean.ListsBean listsBean : data) {
                mGroupList.add(listsBean);
            }
            List<VideoGroupList> videoGroupLists = listToGroup(mGroupList);
            mExpandedVideoListAdapter.setNewData(videoGroupLists);
            mExpandedVideoListAdapter.loadMoreComplete();
        }
    }

    /**
     * 是自己，关注按钮置为灰色,
     * @param userID
     */
    private void isMine(String userID) {
        if(!TextUtils.isEmpty(userID)&&TextUtils.equals(userID,VideoApplication.getLoginUserID())){
            bindingView.reAdd.setBackgroundResource(R.drawable.bg_item_follow_gray_transpent_selector);
            bindingView.ivAdd.setImageResource(R.drawable.ic_min_add_gray);
            bindingView.tvAdd.setTextColor(CommonUtils.getColor(R.color.common_h2));
        }
    }

    /**
     * 获取意图对象
     */
    private void initIntent() {
        Intent intent = getIntent();
        if(intent != null) {
            mAuthorID =intent .getStringExtra("author_id");
            if(TextUtils.isEmpty(mAuthorID)){
                ToastUtils.showCenterToast("错误");
                finish();
            }
        }
    }

    /**
     * 加载用户所有视频
     */
    private void loadVideoList() {
        mPage++;
        if(null!=mAuthorDetailPresenter) mAuthorDetailPresenter.getUpLoadVideoList(mAuthorID, VideoApplication.getLoginUserID(),mPage+"",mPageSize+"");
    }


    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        if(null!=mAuthorDetailPresenter) mAuthorDetailPresenter.getUserInfo(mAuthorID);
    }

    /**
     * 提供给子界面的登录方法
     */
    public void login(){
        Intent intent=new Intent(AuthorDetailsActivity.this,LoginGroupActivity.class);
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
                //登录成功
                if(booleanExtra){
                    mPage=0;
                    loadUserInfo();
                }
            }
        }
    }

    //==========================================数据请求回调==========================================

    /**
     * 用户基本信息结果回调
     * @param data
     */
    @Override
    public void showUserInfo(MineUserInfo data,String userID) {
        mInfoBean = data.getData().getInfo();
        if(null!=mInfoBean)
            ApplicationManager.getInstance().getCacheExample().remove(mAuthorID);
            ApplicationManager.getInstance().getCacheExample().put(mAuthorID,mInfoBean);
            initUserData();
            loadVideoList();
            //H5跳转而来根据动作是否关注用户
            if(null!=getIntent()&&null!=getIntent().getStringExtra("is_follow")&&TextUtils.equals("1",getIntent().getStringExtra("is_follow"))){
                if(null!=VideoApplication.getInstance().getUserData()){
                    if(TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())){
                        showErrorToast(null,null,"自己时刻都在关注着自己！");
                        return;
                    }
                    //未关注
                    if(0==mInfoBean.getIs_follow()){
                        onFollow();
                    }
                }else{
                    login();
                }
            }
    }

    /**
     * 关注用户结果回调
     * @param isFollow
     * @param text
     */
    @Override
    public void showFollowUser(Boolean isFollow,String text) {
        closeProgressDialog();
        //关注成功
        if(null!=isFollow&&isFollow){
        mInfoBean.setIs_follow(1);
        //取消关注成功
        }else if(null!=isFollow&&!isFollow){
            mInfoBean.setIs_follow(0);
        }
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED);
        showFinlishToast(null,null,text);
        switchIsFollow();//切换关注状态
    }

    /**
     * 举报用户结果回调
     * @param data
     */
    @Override
    public void showReportUserResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(jsonObject.length()>0&&1==jsonObject.getInt("code")){
                ToastUtils.showCenterToast(jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户的所有视频成功回调
     * @param data
     */
    @Override
    public void showUpLoadVideoList(FollowVideoList data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView();
        //替换为全新数据
        if(1==mPage){
            ApplicationManager.getInstance().getCacheExample().remove(mAuthorID+"_video_list");
            ApplicationManager.getInstance().getCacheExample().put(mAuthorID+"_video_list", (Serializable) data.getData().getLists(), Constant.CACHE_TIME);
            setNewDataToAdapter(data.getData().getLists());
            //添加数据
        }else{
            addDataToAdapter(data.getData().getLists());
        }
    }


    /**
     * 获取用户视频为空
     * @param data
     */
    @Override
    public void showUpLoadVideoListEmpty(String data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView();
        //用户不存在作品，应当删除本地缓存，防止不同步
        if(1==mPage){
            ApplicationManager.getInstance().getCacheExample().remove(mAuthorID+"_video_list");
        }
        if(null!= mExpandedVideoListAdapter) mExpandedVideoListAdapter.loadMoreEnd();
        if(null!=mVideoListAdapter) mVideoListAdapter.loadMoreEnd();
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }



    /**
     * 获取用户视频错误
     * @param data
     */
    @Override
    public void showUpLoadVideoListError(String data) {
        if(null!= mExpandedVideoListAdapter){
            mExpandedVideoListAdapter.loadMoreFail();
            List<VideoGroupList> list = mExpandedVideoListAdapter.getData();
            if(1==mPage&&null==list||list.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(null!= mVideoListAdapter){
            mVideoListAdapter.loadMoreFail();
            List<FollowVideoList.DataBean.ListsBean> list2 = mVideoListAdapter.getData();
            if(1==mPage&&null==list2||list2.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 当加载错误的时候
     */
    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }


    /**
     * 将数据分组分类封装
     * @param lists
     * @return
     */
    private List<VideoGroupList> listToGroup(List<FollowVideoList.DataBean.ListsBean> lists) {
        if(null==lists) return null;
        List<VideoGroupList> videoGroupLists=new ArrayList<>();
        if(lists.size()<=0) return videoGroupLists;
        String dateTime="";
        int groupCount=-1;
        for (int i = 0; i < lists.size(); i++) {
            //第一条数据,一维数组中创建一个元素，元素中存放一个key和二维数组
            FollowVideoList.DataBean.ListsBean listsBean = lists.get(i);
            listsBean.setItemIndex(i);
            String time=listsBean.getAdd_time()+"000";
            String timeForString = TimeUtils.getTimeForString(Long.parseLong(time));
            if (0 == i) {
                List<FollowVideoList.DataBean.ListsBean> items = new ArrayList<>();
                VideoGroupList group = new VideoGroupList();
                group.setDateTime(timeForString);
                items.add(listsBean);
                group.setListsBeans(items);
                videoGroupLists.add(group);
                dateTime = timeForString;
                groupCount = 0;
                //同一个属性下面的，将数据插入已有的一维数组中和二维数组中
            } else if (TextUtils.equals(dateTime, timeForString)) {
                VideoGroupList videoGroupList = videoGroupLists.get(groupCount);
                if (null != videoGroupList) {
                    videoGroupList.getListsBeans().add(listsBean);
                    dateTime = timeForString;
                }
                //新建一维数组元素，元素中存放一个key和二维数组
            } else if (!TextUtils.equals(dateTime, timeForString)) {
                List<FollowVideoList.DataBean.ListsBean> items = new ArrayList<>();
                VideoGroupList group = new VideoGroupList();
                group.setDateTime(timeForString);
                items.add(listsBean);
                dateTime = timeForString;
                group.setListsBeans(items);
                videoGroupLists.add(group);
                groupCount++;
            }
        }
        return videoGroupLists;
    }

    /**
     * 关注处理
     */
    private void onFollowUser() {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }
        if(null==mInfoBean){
            return;
        }
        if(TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())){
            showErrorToast(null,null,"自己时刻都在关注着自己！");
            return;
        }

        if(null!= VideoApplication.getInstance().getUserData()){
            //已关注
            if(1==mInfoBean.getIs_follow()){
                showUnFollowMenu();
                //未关注
            }else{
                onFollow();
            }
        }else{
            login();
        }
    }

    //===========================================数据绑定===========================================
    /**
     * 初始化用户基本信息
     */
    private void initUserData() {
        if(null==mInfoBean){
            return;
        }else{
            try {
                bindingView.tvTitleUserName.setText(TextUtils.isEmpty(mInfoBean.getNickname()) ? "火星人" : mInfoBean.getNickname());
                bindingView.tvSubtitleUserName.setText(TextUtils.isEmpty(mInfoBean.getNickname()) ? "火星人" : mInfoBean.getNickname());
                String decode = URLDecoder.decode(TextUtils.isEmpty(mInfoBean.getSignature())?"宝宝暂时没有个性签名":mInfoBean.getSignature(), "UTF-8");
                bindingView.tvUserDesp.setText(decode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            bindingView.tvUserGrade.setText("Lv"+(TextUtils.isEmpty(mInfoBean.getRank())?"1":mInfoBean.getRank()));
            bindingView.tvFansCount.setText(TextUtils.isEmpty(mInfoBean.getFans())?"0粉丝":Utils.changeNumberFormString(mInfoBean.getFans())+"粉丝");
            bindingView.tvFollowCount.setText(TextUtils.isEmpty(mInfoBean.getFollows())?"0关注":mInfoBean.getFollows()+"关注");
            bindingView.ivUserSex.setImageResource(TextUtils.isEmpty(mInfoBean.getGender())?R.drawable.ic_sex_not_know:TextUtils.equals("女",mInfoBean.getGender())?R.drawable.iv_icon_sex_women:TextUtils.equals("男",mInfoBean.getGender())?R.drawable.iv_icon_sex_man:R.drawable.ic_sex_not_know);
            bindingView.userVideoCount.setText(TextUtils.isEmpty(mInfoBean.getVideo_count())?"0作品":mInfoBean.getVideo_count()+"作品");
            //是否对该作者已关注
            switchIsFollow();
            setHeaderImageBG();
        }
    }

    /**
     * 是否对该作者已关注
     */
    private void switchIsFollow() {
        if(null==mInfoBean){
            return;
        }
        bindingView.reAdd.setBackgroundResource(R.drawable.text_bg_round_app_style_pressed_true_selector);
        bindingView.ivAdd.setImageResource(1==mInfoBean.getIs_follow()?R.drawable.iv_follow_true_white:R.drawable.ic_min_add_white);
        bindingView.tvAdd.setText(TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())?"关 注":1==mInfoBean.getIs_follow()?"已关注":"关 注");
        bindingView.tvAdd.setTextColor(CommonUtils.getColor(R.color.white));
        isMine(mAuthorID);
    }

    /**
     * 关注用户
     */
    private void onFollow() {
        showProgressDialog("关注中，请稍后...",true);
        if(null!=mAuthorDetailPresenter) mAuthorDetailPresenter.onFollowUser(mInfoBean.getId(), VideoApplication.getLoginUserID());
    }

    /**
     * 举报用户
     * @param userId
     */
    private void onReportUser(String userId) {
        showProgressDialog("举报用户中...",true);
        if(null!=mAuthorDetailPresenter) mAuthorDetailPresenter.onReportUser(VideoApplication.getLoginUserID(),userId);
    }


    /**
     * 设置头部背景图片
     */
    private void setHeaderImageBG() {
        if(null==mInfoBean){
            return;
        }
        if(!AuthorDetailsActivity.this.isFinishing()){
            //设置背景封面
            Glide.with(this)
                    .load(TextUtils.isEmpty(mInfoBean.getImage_bg())?R.drawable.iv_mine_bg:Utils.imageUrlChange(mInfoBean.getImage_bg()))
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(bindingView.ivUserImageBg);
            //作者封面
            Glide.with(this)
                    .load(TextUtils.isEmpty(mInfoBean.getLogo())?R.drawable.iv_mine:Utils.imageUrlChange(mInfoBean.getLogo()))
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(this))
                    .into(bindingView.ivUserIcon);
        }
    }

    /**
     * 弹出取消关注窗口
     */
    private void showUnFollowMenu() {
        List<VideoDetailsMenu> list=new ArrayList<>();
        VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
        videoDetailsMenu1.setItemID(1);
        videoDetailsMenu1.setTextColor("#FF576A8D");
        videoDetailsMenu1.setItemName("取消关注");
        list.add(videoDetailsMenu1);

        CommonMenuDialog commonMenuDialog =new CommonMenuDialog(AuthorDetailsActivity.this);
        commonMenuDialog.setData(list);
        commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int itemID) {
                //取消关注
                switch (itemID) {
                    case 1:
                        onFollow();
                        break;
                }
            }
        });
        commonMenuDialog.show();
    }

    /**
     * 查看关注列表
     */
    private void lookFollows() {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }

        if(null==mInfoBean){
            return;
        }

        if(!TextUtils.equals(mAuthorID, VideoApplication.getLoginUserID())){
            ToastUtils.showCenterToast("只对用户自己可见");
            return;
        }
        String title;
        int type=0;
        //是自己
        if(null!= VideoApplication.getInstance().getUserData()&&TextUtils.equals(mAuthorID, VideoApplication.getLoginUserID())){
            title="我关注的人";
            type=1;
        }else{
            title=mInfoBean.getNickname()+"关注的人";
            type=0;
        }
        startTargetActivity(Constant.KEY_FRAGMENT_TYPE_FOLLOW_USER_LIST,title,mAuthorID,type);
    }


    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType) {
        Intent intent=new Intent(AuthorDetailsActivity.this, ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        startActivity(intent);
    }

    /**
     * 查看粉丝列表
     */
    private void lookFans() {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }
        if(null==mInfoBean){
            return;
        }
        String title;
        int type=0;
        //是自己
        if(null!= VideoApplication.getInstance().getUserData()&&TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())){
            title="我的粉丝";
            type=1;
        }else{
            title=mInfoBean.getNickname()+"的粉丝";
            type=0;
        }
        startTargetActivity(Constant.KEY_FRAGMENT_TYPE_FANS_LIST,title,mAuthorID,type);
    }


    /**
     * 显示详细信息
     */

    private void showUserDetailsDataDialog() {
        if(TextUtils.isEmpty(mAuthorID)) return;
        if(null==mInfoBean) return;
        if(!AuthorDetailsActivity.this.isFinishing()){
            List<VideoDetailsMenu> list=new ArrayList<>();
            //是发布此视频的用作者自己
            if(null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())){
                VideoDetailsMenu videoDetailsMenu6=new VideoDetailsMenu();
                videoDetailsMenu6.setItemID(6);
                videoDetailsMenu6.setTextColor("#FF576A8D");
                videoDetailsMenu6.setItemName("点击复制用户ID: "+mAuthorID);
                list.add(videoDetailsMenu6);
            }else{
                VideoDetailsMenu videoDetailsMenu6=new VideoDetailsMenu();
                videoDetailsMenu6.setItemID(6);
                videoDetailsMenu6.setTextColor("#FF576A8D");
                videoDetailsMenu6.setItemName("点击复制用户ID: "+mAuthorID);
                list.add(videoDetailsMenu6);

                VideoDetailsMenu videoDetailsMenu7=new VideoDetailsMenu();
                videoDetailsMenu7.setItemID(7);
                videoDetailsMenu7.setTextColor("#FFFF7044");
                videoDetailsMenu7.setItemName("举报此用户");
                list.add(videoDetailsMenu7);
            }
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog(AuthorDetailsActivity.this);
            commonMenuDialog.setData(list);
            commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                @Override
                public void onItemClick(int itemID) {
                    //复制ID
                    switch (itemID) {
                        case 6:
                            Utils.copyString(mAuthorID);
                            ToastUtils.showCenterToast("已复制");
                            break;
                        //举报用户
                        case 7:
                            //去登录
                            if(null== VideoApplication.getInstance().getUserData()){
                                ToastUtils.showCenterToast("该操作需要登录！");
                                login();
                            }else{
                                if(null!=mInfoBean){
                                    onReportUser(mInfoBean.getId());
                                }
                            }
                            break;
                    }
                }
            });
            commonMenuDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if(SharedPreferencesUtil.getInstance().getBoolean(Constant.KEY_MAIN_INSTANCE,false)){
            super.onBackPressed();
        }else{
            Intent intent=new Intent(AuthorDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.vars=null;
        if(null!=mAuthorDetailPresenter){
            mAuthorDetailPresenter.detachView();
        }
        if(null!=mGroupList) mGroupList.clear();
        if(null!=bindingView) bindingView.recyerView.setAdapter(null);
        mVideoListAdapter=null;mExpandedVideoListAdapter=null;
        mGroupList=null;
        Runtime.getRuntime().gc();
    }

    /**
     * 保存播放记录到本地
     * @param data
     */
    protected void saveLocationHistoryList(final FollowVideoList.DataBean.ListsBean data) {
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
                userLookVideoList.setVideoCommendCount(TextUtils.isEmpty(data.getComment_count())?"0":data.getComment_count());
                userLookVideoList.setVideoShareCount(TextUtils.isEmpty(data.getShare_times())?"0":data.getShare_times());
                userLookVideoList.setUserId(data.getUser_id());
                userLookVideoList.setItemIndex(0);
                userLookVideoList.setVideoId(data.getVideo_id());
                userLookVideoList.setVideoCover(data.getCover());
                userLookVideoList.setUploadTime(data.getAdd_time());
                userLookVideoList.setAddTime(System.currentTimeMillis());
                userLookVideoList.setIs_interest(data.getIs_interest());
                userLookVideoList.setIs_follow(data.getIs_follow());
                userLookVideoList.setVideoPath(data.getPath());
                userLookVideoList.setVideoPlayerCount(TextUtils.isEmpty(data.getPlay_times())?"0":data.getPlay_times());
                userLookVideoList.setVideoType(TextUtils.isEmpty(data.getType())?"2":data.getType());
                ApplicationManager.getInstance().getUserPlayerDB().insertNewPlayerHistoryOfObject(userLookVideoList);
            }
        }.start();
    }
}
