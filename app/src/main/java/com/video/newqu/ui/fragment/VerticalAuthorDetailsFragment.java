package com.video.newqu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.bean.VideoGroupList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityAuthorDetailsBinding;
import com.video.newqu.databinding.VideoDetailsEmptyLayoutBinding;
import com.video.newqu.event.VerticalPlayMessageEvent;
import com.video.newqu.listener.OnUserVideoListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.model.RecyclerViewSpacesItem;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.activity.MediaSingerImagePreviewActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.AuthorDetailContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.presenter.AuthorDetailPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.TimeUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeMarginView;
import com.video.newqu.view.widget.GlideCircleTransform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/12/5.
 * 垂直的滑动列表用户中心界面
 */

public class VerticalAuthorDetailsFragment extends BaseFragment<ActivityAuthorDetailsBinding,AuthorDetailPresenter> implements AuthorDetailContract.View,OnUserVideoListener {

    private GroupVideoListAdapter mExpandedVideoListAdapter;
    private UserVideoListAdapter mVideoListAdapter;//九宫格
    public String mAuthorID;//用户ID
    private MineUserInfo.DataBean.InfoBean mInfoBean;//用户基本信息
    private int mPage=0;
    private int mPageSize=10;
    private int mHeaderViewHeight;
    private WeakReference<VerticalVideoPlayActivity> mActivityWeakReference;
    private VideoDetailsEmptyLayoutBinding mEmptyViewbindView;
    //分组
    private int headHeight;//分组头部高度
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private int mCurrentPosition = 0;
    private List<FollowVideoList.DataBean.ListsBean> mGroupList=null;//包含所有二维数组中的所有元素
    private boolean isExpanded;//是否展开，默认是九宫格
    private RecyclerViewSpacesItem mRecyclerViewSpacesItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        VerticalVideoPlayActivity activity= (VerticalVideoPlayActivity) context;
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //点击别人，弹出举报弹窗，点击自己无选项
                    case R.id.iv_user_icon:
                        if(null==mInfoBean) return;
                        MediaSingerImagePreviewActivity.start(getActivity(),mInfoBean.getLogo(),bindingView.ivUserIcon);
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
                            MediaSingerImagePreviewActivity.start(getActivity(),mInfoBean.getImage_bg(),bindingView.ivUserImageBg);
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
        bindingView.ivMenu.setVisibility(null!=mAuthorID&&null!=VideoApplication.getInstance().getUserData()&&!TextUtils.isEmpty(mAuthorID)&&TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())?View.GONE:View.VISIBLE);
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        bindingView.llTopTitle.measure(width,height);
        bindingView.collapseToolbar.measure(width,height);
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
                float scale = (float) abs /(mHeaderViewHeight / 2);
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
    public int getLayoutId() {
        return R.layout.activity_author_details;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        mPresenter = new AuthorDetailPresenter(getActivity());
        mPresenter.attachView(this);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(null!=bindingView&&null!= mVideoListAdapter &&!TextUtils.isEmpty(mAuthorID)&&null!=mExpandedVideoListAdapter){
            if(null==mInfoBean&&null==mGroupList){
                mInfoBean= (MineUserInfo.DataBean.InfoBean)   ApplicationManager.getInstance().getCacheExample().getAsObject(mAuthorID);
                if(null!=mInfoBean){
                    initUserData();
                }
                List<FollowVideoList.DataBean.ListsBean> cacheList=(List<FollowVideoList.DataBean.ListsBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(mAuthorID + "_video_list");
                if(null== mVideoListAdapter.getData()||mVideoListAdapter.getData().size()<=0){
                    if(null!=cacheList&&cacheList.size()>0){
                        //先设置九宫格的适配器
                        mVideoListAdapter.setNewData(cacheList);
                        //再处理时间轴的适配器数据
                        if(null==mGroupList) mGroupList=new ArrayList<>();
                        for (FollowVideoList.DataBean.ListsBean listsBean : cacheList) {
                            mGroupList.add(listsBean);
                        }
                        List<VideoGroupList> videoGroupLists = listToGroup(mGroupList);
                        mExpandedVideoListAdapter.setNewData(videoGroupLists);
                        setHeaderData(mCurrentPosition);
                    }else{
                        if (null != mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
                    }
                }
                loadUserInfo();
            }
        }
    }


    /**
     * 接收播放器界面的通知,还原所有数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VerticalPlayMessageEvent event) {
        if(null!=event){
            this.mAuthorID=event.getAuthorID();
            mInfoBean=null;
            if(null!=mGroupList) mGroupList.clear();
            mGroupList=null;
            mPage=0;//还原页数
            initUserData();//还原所有View数据为初始状态
            if(null!=mVideoListAdapter) mVideoListAdapter.setNewData(null);
            if(null!= mExpandedVideoListAdapter) mExpandedVideoListAdapter.setNewData(null);//还原视频列表为空
            if (null!= mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView();
            //悬浮的
            mCurrentPosition=0;//还原悬浮的头部
            bindingView.tvHeaderView.setText("--");
            bindingView.tvHeaderView.setVisibility(View.GONE);
            //强制还原显示模式为九宫格样式
            isExpanded=false;
            setContentViewExpanedState(isExpanded);
            //最后设置用户昵称、头像基本信息
            bindingView.tvTitleUserName.setText(TextUtils.isEmpty(event.getUserName()) ? "火星人" : event.getUserName());
            bindingView.tvSubtitleUserName.setText(TextUtils.isEmpty(event.getUserName()) ? "火星人" : event.getUserName());
            //展开AppBarlayout
            bindingView.appBarLayout.setExpanded(true);
            //作者头像
            Glide.with(this)
                    .load(TextUtils.isEmpty(event.getUserCover())?R.drawable.iv_mine:Utils.imageUrlChange(event.getUserCover()))
                    .error(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(bindingView.ivUserIcon);
        }
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mLinearLayoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);

        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.video_details_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
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
        //九宫格Adapter
        mVideoListAdapter = new UserVideoListAdapter(null,3,null);
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
                                    getActivity().finish();
                                    Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_AUTHOE_CORE);
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
                                getActivity().finish();
                                VideoDetailsActivity.start(getActivity(),listsBean.getVideo_id(),listsBean.getUser_id(),false);
                            }
                        }
                    }
                }
            }
        });

        //时间轴
        mExpandedVideoListAdapter = new GroupVideoListAdapter(null);
        mExpandedVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!= mExpandedVideoListAdapter){
                    List<VideoGroupList> data = mExpandedVideoListAdapter.getData();
                    if(null!=data&&data.size()>1){
                        mExpandedVideoListAdapter.setEnableLoadMore(true);
                        loadVideoList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    if(null!= mExpandedVideoListAdapter) mExpandedVideoListAdapter.loadMoreFail();//加载失败
                                }else{
                                    if(null!= mExpandedVideoListAdapter) mExpandedVideoListAdapter.loadMoreEnd();//加载为空
                                }
                            }
                        });
                    }
                }
            }
        }, bindingView.recyerView);
        mExpandedVideoListAdapter.setOnItemClickListener(new GroupVideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MobclickAgent.onEvent(getActivity(), "click_play");
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
                                Intent intent=new Intent(getActivity(),VerticalVideoPlayActivity.class);
                                intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_VERTICAL_AUTHOR);
                                intent.putExtra(Constant.KEY_POISTION,position);
                                intent.putExtra(Constant.KEY_PAGE,mPage);
                                intent.putExtra(Constant.KEY_AUTHOE_ID,VideoApplication.getLoginUserID());
                                intent.putExtra(Constant.KEY_JSON,json);
                                getActivity().finish();
                                startActivity(intent);
                            }
                        }catch (Exception e){

                        }
                        //单个
                    }else{
                        FollowVideoList.DataBean.ListsBean listsBean = mGroupList.get(position);
                        if(null!=listsBean&&!TextUtils.isEmpty(listsBean.getVideo_id())){
                            saveLocationHistoryList(listsBean);
                            getActivity().finish();
                            VideoDetailsActivity.start(getActivity(),listsBean.getVideo_id(),listsBean.getUser_id(),false);
                        }
                    }
                }
            }
        });

        setContentViewExpanedState(isExpanded);

        //滚动点监听，实现分组悬浮
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
        //决定是否要显示悬浮的分组
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
        mCurrentPosition=0;
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
     * 初始化用户信息
     */
    private void initUserData() {
        if(null==mInfoBean){
            bindingView.tvTitleUserName.setText("火星人");
            bindingView.tvSubtitleUserName.setText("火星人");
            bindingView.tvUserDesp.setText("宝宝暂时没有个性签名");
            bindingView.tvUserGrade.setText("Lv1");
            bindingView.tvFansCount.setText("0粉丝");
            bindingView.tvFollowCount.setText("0关注");
            bindingView.userVideoCount.setText("0作品");
            bindingView.ivUserSex.setImageResource(R.drawable.iv_icon_sex_women);
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
            bindingView.tvFansCount.setText((TextUtils.isEmpty(mInfoBean.getFans())?"0粉丝": Utils.changeNumberFormString(mInfoBean.getFans()))+"粉丝");
            bindingView.tvFollowCount.setText((TextUtils.isEmpty(mInfoBean.getFollows())?"0关注":mInfoBean.getFollows())+"关注");
            bindingView.userVideoCount.setText((TextUtils.isEmpty(mInfoBean.getVideo_count())?"0作品":mInfoBean.getVideo_count())+"作品");
            bindingView.ivUserSex.setImageResource(TextUtils.isEmpty(mInfoBean.getGender())?R.drawable.ic_sex_not_know:TextUtils.equals("女",mInfoBean.getGender())?R.drawable.iv_icon_sex_women:TextUtils.equals("男",mInfoBean.getGender())?R.drawable.iv_icon_sex_man:R.drawable.ic_sex_not_know);
        }
        switchIsFollow();
        //设置背景封面和用户头像
        if(!getActivity().isFinishing()){
            //设置背景封面
            Glide.with(this)
                    .load(null==mInfoBean?R.drawable.iv_mine_bg:TextUtils.isEmpty(mInfoBean.getImage_bg())?R.drawable.iv_mine_bg:Utils.imageUrlChange(mInfoBean.getImage_bg()))
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(bindingView.ivUserImageBg);
            //作者头像
            Glide.with(this)
                    .load(null==mInfoBean?R.drawable.iv_mine:TextUtils.isEmpty(mInfoBean.getLogo())?R.drawable.iv_mine:Utils.imageUrlChange(mInfoBean.getLogo()))
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(bindingView.ivUserIcon);
        }
    }

    /**
     * 是否对该作者已关注
     */
    private void switchIsFollow() {
        if(null==mInfoBean){
            bindingView.reAdd.setBackgroundResource(R.drawable.text_bg_round_app_style_pressed_true_selector);
            bindingView.ivAdd.setImageResource(R.drawable.iv_follow_true_white);
            bindingView.tvAdd.setText("关 注");
            bindingView.tvAdd.setTextColor(CommonUtils.getColor(R.color.white));
        }else{
            bindingView.reAdd.setBackgroundResource(R.drawable.text_bg_round_app_style_pressed_true_selector);
            bindingView.ivAdd.setImageResource(1==mInfoBean.getIs_follow()?R.drawable.iv_follow_true_white:R.drawable.ic_min_add_white);
            bindingView.tvAdd.setText(TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())?"关 注":1==mInfoBean.getIs_follow()?"已关注":"关 注");
            bindingView.tvAdd.setTextColor(CommonUtils.getColor(R.color.white));
        }
        if(!TextUtils.isEmpty(mAuthorID)&&TextUtils.equals(mAuthorID,VideoApplication.getLoginUserID())){
            bindingView.reAdd.setBackgroundResource(R.drawable.bg_item_follow_gray_transpent_selector);
            bindingView.ivAdd.setImageResource(R.drawable.ic_min_add_gray);
            bindingView.tvAdd.setTextColor(CommonUtils.getColor(R.color.common_h2));
        }
    }


    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        if(null!= mPresenter &&!mPresenter.isLoadUserInfo()){
            mPresenter.getUserInfo(mAuthorID);
        }
    }

    /**
     * 加载用户所有视频
     */
    private void loadVideoList() {
        mPage++;
        if(null!= mPresenter) mPresenter.getUpLoadVideoList(mAuthorID, VideoApplication.getLoginUserID(),mPage+"",mPageSize+"");
    }

    /**
     * 分享用户的主页
     */
    private void shareMineHome() {
        if(!TextUtils.isEmpty(mAuthorID)&&null!=mActivityWeakReference){
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
            shareInfo.setShareTitle("分享用户主页至");
            shareInfo.setUrl("http://app.nq6.com/home/user/index?user_id="+shareInfo.getUserID());
            if(null!=mActivityWeakReference&&null!=mActivityWeakReference.get()){
                mActivityWeakReference.get().shareMineHome(shareInfo);
            }
        }
    }


    /**
     * 显示详细信息
     */
    private void showUserDetailsDataDialog() {

        if(TextUtils.isEmpty(mAuthorID)) return;
        if(null==mInfoBean) return;

        if(null!=getActivity()&&!getActivity().isFinishing()){
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
                videoDetailsMenu7.setTextColor("#FFFF5000");
                videoDetailsMenu7.setItemName("举报此用户");
                list.add(videoDetailsMenu7);
            }
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog((AppCompatActivity) getActivity());
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
                                if(null!=mActivityWeakReference&&null!=mActivityWeakReference.get()){
                                    ToastUtils.showCenterToast("该操作需要登录！");
                                    mActivityWeakReference.get().login();
                                }
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
            if(null!=mActivityWeakReference&&null!=mActivityWeakReference.get()){
                mActivityWeakReference.get().login();
            }
        }
    }


    private void onBackPressed(){
        if(null!=mActivityWeakReference&&null!=mActivityWeakReference.get()&&!mActivityWeakReference.get().isFinishing()){
            mActivityWeakReference.get().setCureenItem(0);
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
                        onFollow();
                        break;
                }
            }
        });
        commonMenuDialog.show();
    }



    /**
     * 关注用户
     */
    private void onFollow() {
        if(null!= mPresenter &&!mPresenter.isFollow()){
            showProgressDialog("关注中，请稍后...",true);
            mPresenter.onFollowUser(mInfoBean.getId(), VideoApplication.getLoginUserID());
        }
    }


    /**
     * 举报用户
     * @param userId
     */
    private void onReportUser(String userId) {
        showProgressDialog("举报用户中...",true);
        if(null!= mPresenter) mPresenter.onReportUser(VideoApplication.getLoginUserID(),userId);
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
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if(null!=mGroupList) mGroupList.clear();
        mGroupList=null;
        if(null!=mActivityWeakReference){
            mActivityWeakReference.clear();
        }
        if(null!=bindingView) bindingView.recyerView.setAdapter(null);
        mVideoListAdapter=null;mExpandedVideoListAdapter=null;
        mCurrentPosition=0;isExpanded=false;mAuthorID=null;mInfoBean=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //======================================点击事件的监听============================================

    @Override
    public void onItemClick(int poistion) {

    }

    @Override
    public void onLongClick(String videoID) {

    }

    @Override
    public void onDeleteVideo(String videoID) {

    }

    @Override
    public void onPublicVideo(String videoID) {

    }

    @Override
    public void onUnFollowVideo(String videoID) {

    }

    @Override
    public void onHeaderIcon(String userID) {

    }


    //========================================网络请求回调===========================================

    @Override
    public void showErrorView() {
        if(null==mInfoBean){
            if (null != mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
        }
    }

    @Override
    public void complete() {

    }

    /**
     * 显示用户基本信息
     * @param data
     */
    @Override
    public void showUserInfo(MineUserInfo data,String userID) {
        //防止因为网络问题加载延缓导致数据与用户比匹配问题
        if(null!=mAuthorID&&!TextUtils.equals(mAuthorID,userID)){
            return;
        }
        mInfoBean = data.getData().getInfo();
        if(null!=mInfoBean)   ApplicationManager.getInstance().getCacheExample().remove(mAuthorID);
        ApplicationManager.getInstance().getCacheExample().put(mAuthorID,mInfoBean);
        String videoCount = TextUtils.isEmpty(mInfoBean.getVideo_count()) ? "0" : mInfoBean.getVideo_count();
        bindingView.userVideoCount.setText(videoCount+"作品");
        initUserData();
        mPage=0;
        loadVideoList();
    }

    /**
     * 关注用户结果
     * @param isFollow
     * @param text
     */
    @Override
    public void showFollowUser(Boolean isFollow, String text) {
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
     * 举报用户结果
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
     * 加载用户视频列表成功
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
     * 加载用户视频列表为空
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
     * 加载用户视频列表失败
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
     * 将数据分组分类封装
     * @param lists
     * @return
     */
    private List<VideoGroupList> listToGroup(List<FollowVideoList.DataBean.ListsBean> lists) {
        List<VideoGroupList> videoGroupLists=new ArrayList<>();
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
}
