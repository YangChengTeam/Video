package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.VideoComentListAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.PlayCountInfo;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.bean.VideoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityVideoDetailsBinding;
import com.video.newqu.databinding.VideoDetailsEmptyLayoutBinding;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.listener.VideoComendClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.contract.VideoDetailsContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.dialog.FollowWeiXnDialog;
import com.video.newqu.ui.dialog.InputKeyBoardDialog;
import com.video.newqu.ui.fragment.KsyAuthorizeSettingFragment;
import com.video.newqu.ui.presenter.VideoDetailsPresenter;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ContentCheckKey;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.TimeUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.util.attach.VideoDownloadComposrTask;
import com.video.newqu.view.layout.DataChangeMarginView;
import com.video.newqu.view.layout.VideoGroupRelativeLayout;
import com.video.newqu.view.widget.GlideCircleTransform;
import com.xinqu.videoplayer.XinQuVideoPlayer;
import com.xinqu.videoplayer.XinQuVideoPlayerStandard;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
/**
 * TinyHung@outlook.com
 * 2017/5/25 9:26
 * 视频详情，留言评论列表
 */
public class VideoDetailsActivity extends BaseActivity<ActivityVideoDetailsBinding> implements VideoDetailsContract.View, TopicClickListener , VideoComendClickListener {

    private String mVideoId;
    private VideoDetailsPresenter mVideoDetailsPresenter;
    private int  mPage=0;//当前显示留言页数
    private int  mPageSize=20;//每页留言条数
    private VideoComentListAdapter mVideoComentListAdapter;
    private String mVideoAuthorID;//视频作者ID
    private VideoInfo.DataBean.InfoBean mVideoInfo;//视频信息
    private String toUserID="0";
    private VideoDetailsEmptyLayoutBinding mEmptyBindingView;
    private int mVideoViewHeight=0;
    private LinearLayoutManager mLinearLayoutManager;
    private ScaleAnimation mFollowScaleAnimation;
    private boolean mIsHistory;
    private int mHeaderViewHeight;
    private int oldScrollOffset=-1;//刚才滑动的
    /**
     * 入口
     * @param context
     * @param videoID 视频ID
     */
    public static void start(Context context, String videoID, String videoAuthorID,boolean isHistory) {
        Intent intent=new Intent(context,VideoDetailsActivity.class);
        intent.putExtra("video_id",videoID);
        intent.putExtra("video_author_id",videoAuthorID);
        intent.putExtra("isHistory",isHistory);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        showToolBar(false);
        initIntent();
        mVideoDetailsPresenter = new VideoDetailsPresenter(this);
        mVideoDetailsPresenter.attachView(this);
        mVideoInfo= (VideoInfo.DataBean.InfoBean) ApplicationManager.getInstance().getCacheExample().getAsObject(mVideoId);
        initVideoData();
        initAdapter();
        getVideoInfo();
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListenet=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //返回
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                    //菜单
                    case R.id.btn_menu:
                        showMenu();
                        break;
                    //分享
                    case R.id.btn_share:
                        onVideoShare();
                        break;

                    //表情 打开输入框，并打表情面板
                    case R.id.btn_iv_face_icon:
                        showInputKeyBoardDialog(false,true,"输入评论内容");
                        break;
                    //打开输入框，并打开键盘
                    case R.id.tv_input_content:
                        showInputKeyBoardDialog(true,false,"输入评论内容");
                        break;
                    //发送消息
                    case R.id.btn_tv_send:
                        sendWordsMessage();
                        break;
                    //关注
                    case R.id.btn_ll_follow:
                        onFollowUser();
                        break;
                    //点击了用户头像
                    case R.id.iv_video_author_icon:
                        if(null!=mVideoInfo){
                            AuthorDetailsActivity.start(VideoDetailsActivity.this,mVideoInfo.getUser_id());
                        }
                        break;
                    //下载视频
                    case R.id.btn_download:
                        downloadVideo();
                        break;
                    case R.id.video_item_list_user_name:
                        //仅当不是自己，才看继续查看用户中心界面
                        if(null!=mVideoInfo&&!TextUtils.isEmpty(bindingView.videoItemListUserName.getText().toString())){
                            AuthorDetailsActivity.start(VideoDetailsActivity.this,mVideoAuthorID);
                        }
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListenet);
        bindingView.btnMenu.setOnClickListener(onClickListenet);
        bindingView.btnShare.setOnClickListener(onClickListenet);
        bindingView.btnDownload.setOnClickListener(onClickListenet);
        bindingView.videoItemListUserName.setOnClickListener(onClickListenet);
        bindingView.btnPrice.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                priceVideo(true);
            }
        });
        bindingView.btnIvFaceIcon.setOnClickListener(onClickListenet);
        bindingView.btnTvSend.setOnClickListener(onClickListenet);
        bindingView.btnLlFollow.setOnClickListener(onClickListenet);
        bindingView.ivVideoAuthorIcon.setOnClickListener(onClickListenet);
        bindingView.tvInputContent.setOnClickListener(onClickListenet);
        mFollowScaleAnimation = AnimationUtil.followAnimation();
        //监听文字变化
        bindingView.tvInputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    if(null!=bindingView)  bindingView.btnTvSend.setTextColor(CommonUtils.getColor(R.color.text_orgin_selector));
                }else{
                    if(null!=bindingView)  {
                        bindingView.btnTvSend.setTextColor(CommonUtils.getColor(R.color.colorTabText));
                        bindingView.tvInputContent.setHint("说点什么...");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        bindingView.llTopBarBg.measure(width,width);
        bindingView.llUserHeaderView.measure(width,width);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bindingView.topBarBg.getLayoutParams();
        layoutParams.width=RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height=  bindingView.llTopBarBg.getMeasuredHeight();
        bindingView.topBarBg.setLayoutParams(layoutParams);
        bindingView.topBarBg.setBackgroundResource(R.drawable.home_top_bar_bg_shape);
        bindingView.collapseToolbar.measure(width,width);
        mHeaderViewHeight=bindingView.collapseToolbar.getMeasuredHeight();
        //滚动手势处理
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int abs = Math.abs(verticalOffset);
                if(oldScrollOffset==abs) return;
                //小窗口播放器的处理
                if(abs>=mHeaderViewHeight){
                    if(bindingView.videoPlayer.isPlaying()){
                        bindingView.videoPlayer.startWindowPlay();
                    }
                }else{
                    bindingView.videoPlayer.backPress();
                }
                //手势滑动的留言文本输入框处理
                if(oldScrollOffset<abs){
                    showMenuTabView();  //显示输入框
                }else{
                    hideMenuTabView(); //隐藏输入框
                }
                oldScrollOffset=abs;
                if(abs>(mHeaderViewHeight+1)) return;
                //标题栏的背景颜色处理
                float scale = (float) abs / mHeaderViewHeight;
                float alpha =(scale * 255);
                float v = alpha / 225f;
                bindingView.topBarBg.setAlpha(v);
            }
        });
    }

    @Override
    public void initData() {

    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mLinearLayoutManager = new LinearLayoutManager(VideoDetailsActivity.this);
        bindingView.recyerView.setLayoutManager(mLinearLayoutManager);
        bindingView.recyerView.setHasFixedSize(false);
        mVideoComentListAdapter = new VideoComentListAdapter(null,this,this);
        mVideoComentListAdapter.showEmptyView(true);
        //加载中占位布局
        mEmptyBindingView = DataBindingUtil.inflate(getLayoutInflater(), R.layout.video_details_empty_layout, (ViewGroup) bindingView.recyerView.getParent(), false);
        mEmptyBindingView.emptyView.setOnRefreshListener(new DataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyBindingView.emptyView.showLoadingView();
                if(null==mVideoInfo){
                    getVideoInfo();
                }else{
                    mPage=0;
                    loadComentList();
                }
            }
        });
        mEmptyBindingView.emptyView.showLoadingView();
        mVideoComentListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoComentListAdapter){
                    List<ComentList.DataBean.CommentListBean> data = mVideoComentListAdapter.getData();
                    if(null!=data&&data.size()>=10&&null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isLoading()){
                        mVideoComentListAdapter.setEnableLoadMore(true);
                        loadComentList();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoComentListAdapter.loadMoreEnd();//没有更多的数据了
                            }
                        });
                    }
                }
            }
        }, bindingView.recyerView);

        mVideoComentListAdapter.setEmptyView(mEmptyBindingView.getRoot());
        bindingView.recyerView.setAdapter(mVideoComentListAdapter);

        bindingView.recyerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //手势滑动的留言文本输入框处理
                if(dy>0){
                    showMenuTabView();  //显示输入框
                }else if(dy<0){
                    hideMenuTabView(); //隐藏输入框
                }
            }
        });
    }

    private boolean inputIsShow=true;
    /**
     * 隐藏菜单
     */
    public void hideMenuTabView() {
        if(!inputIsShow){
            return;
        }
        inputIsShow=false;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) bindingView.llBottomInput.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        bindingView.llBottomInput.animate().translationY(bindingView.llBottomInput.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    /**
     * 显示菜单
     */
    public void showMenuTabView() {
        if(inputIsShow){
            return;
        }
        inputIsShow=true;
        bindingView.llBottomInput.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }


    /**
     * 显示和隐藏小播放器
     * @param scollYDistance
     */
    private boolean max=false;
    private boolean min=false;

    private void switchChangeVideoPlayerLocation(int scollYDistance) {
//        if(null==mVideoInfo){
//            return;
//        }
//        if(0!=mVideoViewHeight){
//            //向上滑动的距离>=视频头部
//            if(scollYDistance>=mVideoViewHeight&&!min){
//                min=true;
//                max=false;
//                bindingView.videoPlayer.startWindowPlay();
//            }else if(scollYDistance<mVideoViewHeight&&!max) {
//                min=false;
//                max=true;
//                bindingView.videoPlayer.backPress();
//            }
//        }
//        Drawable drawable = bindingView.topBarBg.getDrawable();
//        if(null==drawable) return;
//        if (scollYDistance <= 0) {
//            drawable.mutate().setAlpha(0);
//        } else if (scollYDistance > 0 && scollYDistance <= mVideoViewHeight) {
//            float scale = (float) scollYDistance / mVideoViewHeight;
//            float alpha = (255 * scale);
//            drawable.mutate().setAlpha((int) alpha);
//        } else {
//            drawable.mutate().setAlpha(255);
//        }
//        bindingView.topBarBg.setImageDrawable(drawable);
    }


    private int getScrolledDistance() {
        int position = mLinearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = mLinearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }


    /**
     * 下载视频
     */
    private void downloadVideo() {
        if(TextUtils.isEmpty(mVideoAuthorID)) return;
        if(null==mVideoInfo||TextUtils.isEmpty(mVideoInfo.getPath()))  return;
        if(!SharedPreferencesUtil.getInstance().getBoolean(Constant.FOLLOW_WEIXIN,false)){
            FollowWeiXnDialog followWeiXnDialog=new FollowWeiXnDialog(VideoDetailsActivity.this);
            followWeiXnDialog.setOnItemClickListener(new FollowWeiXnDialog.OnItemClickListener() {
                @Override
                public void onFollow() {
                    MobclickAgent.onEvent(VideoDetailsActivity.this, "click_follow_wechat");
//                    SharedPreferencesUtil.getInstance().putBoolean(Constant.FOLLOW_WEIXIN,true);
//                    Intent intent= new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri contentUrl = Uri.parse("http://jump.hupeh.cn/xqsp1223.php");
//                    intent.setData(contentUrl);
//                    startActivity(intent);
                    Utils.copyString("新趣小视频助手");
                    ToastUtils.showCenterToast("已复制微信号");
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(VideoDetailsActivity.this)
                            .setTitle("新趣小视频助手")
                            .setMessage(getResources().getString(R.string.open_weixin_tips));
                    builder.setNegativeButton("算了", null);
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreferencesUtil.getInstance().putBoolean(Constant.FOLLOW_WEIXIN,true);
                            try {
                                Uri uri = Uri.parse("weixin://");
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
                }
            });
            followWeiXnDialog.setTipMsg("成功关注[新趣小视频]公众号后开放下载功能噢~");
            followWeiXnDialog.show();
            return;
        }

        //先检查金山云权限
        if(!AuthInfoManager.getInstance().getAuthState()){
            KsyAuthorizeSettingFragment fragment = KsyAuthorizeSettingFragment.newInstance();
            FragmentManager supportFragmentManager=getSupportFragmentManager();
            fragment.show(supportFragmentManager,"ksy_authorize");
            return;
        }

        //检查SD读写权限
        RxPermissions.getInstance(VideoDetailsActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    //用户已登录
                    if(null!=VideoApplication.getInstance().getUserData()){
                        //发布此时品的主人正式观看的用户自己
                        if(TextUtils.equals(mVideoAuthorID,VideoApplication.getLoginUserID())){
                            new VideoDownloadComposrTask(VideoDetailsActivity.this,mVideoInfo.getPath()).start();
                        }else{
                            //用户允许下载
                            if(null!=mVideoInfo.getDownload_permiss()&&TextUtils.equals("0",mVideoInfo.getDownload_permiss())){
                                new VideoDownloadComposrTask(VideoDetailsActivity.this,mVideoInfo.getPath()).start();
                                //用户不允许下载
                            }else{
                                ToastUtils.showCenterToast("发布此视频的用户未开放他人下载此视频权限！");
                            }
                        }
                        //用户未登录
                    }else{
                        if(!VideoDetailsActivity.this.isFinishing()){
                            login();
                        }
                    }
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(VideoDetailsActivity.this)
                            .setTitle("SD读取权限申请失败")
                            .setMessage("存储权限被拒绝，请务必授予我们存储权限！是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(VideoDetailsActivity.this,141);
                        }
                    });
                    builder.show();
                }
            }
        });
    }


    /**
     * 为适配器增加新数据，添加至第0个位置
     * @param data
     */
    private void updataAddDataToTopAdapter(ComentList.DataBean.CommentListBean data) {
        if(null!=mVideoComentListAdapter){
            if(null!=mEmptyBindingView) mEmptyBindingView.emptyView.showEmptyView("没有留言，说两句吧~",R.drawable.iv_com_message_empty);
            mVideoComentListAdapter.addData(0,data);
            //替换最新的缓存
            ApplicationManager.getInstance().getCacheExample().remove(mVideoId+"_comlist");
            ApplicationManager.getInstance().getCacheExample().put(mVideoId+"_comlist", (Serializable) mVideoComentListAdapter.getData());
            //每次发表评论成功，滚动至顶部
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    bindingView.recyerView.smoothScrollToPosition(1);
                }
            });
        }
    }

    /**
     * 获取传递数据
     */
    private void initIntent() {
        Intent intent = getIntent();
        mVideoId = intent.getStringExtra("video_id");
        mVideoAuthorID = intent.getStringExtra("video_author_id");
        mIsHistory = intent.getBooleanExtra("isHistory",false);
        if(TextUtils.isEmpty(mVideoId)){
            ToastUtils.showCenterToast("错误");
            finish();
            return;
        }
    }

    /**
     * 获取视频详细信息
     */
    private void getVideoInfo() {
        if(null!=mVideoDetailsPresenter){
            mVideoDetailsPresenter.getVideoInfo(VideoApplication.getLoginUserID(),mVideoAuthorID,mVideoId);
        }
    }

    /**
     * 初始化视频详情信息
     */
    private void initVideoData() {
        if(null!=mVideoInfo&&null!=bindingView&&!TextUtils.isEmpty(bindingView.videoItemListUserName.getText().toString())){
            bindingView.tvItemPlayCount.setText((TextUtils.isEmpty(mVideoInfo.getPlay_times())?"0":mVideoInfo.getPlay_times())+" 次播放");
            String add_time = mVideoInfo.getAdd_time()+"000";
            bindingView.tvUploadTime.setText(TimeUtils.getTilmNow(Long.parseLong(add_time))+" 发布");
            bindingView.tvCommendCount.setText((TextUtils.isEmpty(mVideoInfo.getComment_times())?"0":mVideoInfo.getComment_times())+" 评论");
            bindingView.tvLikeCount.setText((TextUtils.isEmpty(mVideoInfo.getCollect_times())?"0":mVideoInfo.getCollect_times())+" 喜欢");
            bindingView.btnPrice.setImageResource(1==mVideoInfo.getIs_interest()?R.drawable.btn_nav_like_selector_red:R.drawable.btn_nav_like_selector_white);
            return;//防止二次渲染播放器
        }
        initLayoutParams();
        initHeaderViewData();
        createPlayVideo();
    }

    /**
     * 打开输入法键盘
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     * @param hintText hint文字
     */
    private void showInputKeyBoardDialog(boolean showKeyboard,boolean showFaceBoard,String hintText) {
        InputKeyBoardDialog inputKeyBoardDialog = new InputKeyBoardDialog(VideoDetailsActivity.this);
        inputKeyBoardDialog.setInputText(bindingView.tvInputContent.getText().toString());
        inputKeyBoardDialog.setParams(showKeyboard,showFaceBoard);
        inputKeyBoardDialog.setHintText(hintText);
        inputKeyBoardDialog.setBackgroundWindown(0.1f);
        inputKeyBoardDialog.setIndexOutErrorText("评论内容超过字数限制");
        inputKeyBoardDialog.setOnKeyBoardChangeListener(new InputKeyBoardDialog.OnKeyBoardChangeListener() {
            //文字发生了变化
            @Override
            public void onChangeText(String inputText) {
                if(!TextUtils.isEmpty(inputText)){
                    SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style), bindingView.tvInputContent,null,null);
                    bindingView.tvInputContent.setText(topicStyleContent);
                }else{
                    toUserID="0";
                    bindingView.tvInputContent.setText(inputText);
                }
            }

            //提交
            @Override
            public void onSubmit() {
                sendWordsMessage();
            }
        });
         inputKeyBoardDialog.show();
    }

    /**
     * 根据视频的宽高缩放播放器的宽高
     */
    private void initLayoutParams() {
        if(null==mVideoInfo){
            return;
        }
        //设置视频的窗口大小，非常重要
        int videoType=0;
        if(TextUtils.isEmpty(mVideoInfo.getType())){
            if(!TextUtils.isEmpty(mVideoInfo.getVideo_width())&&!TextUtils.isEmpty(mVideoInfo.getVideo_height())){
                int videoWidth = Integer.parseInt(mVideoInfo.getVideo_width());
                int videoHeight = Integer.parseInt(mVideoInfo.getVideo_height());
                if(videoWidth==videoHeight){
                    videoType=3;
                }else if(videoWidth>videoHeight){
                    videoType=1;
                }else if(videoWidth<videoHeight){
                    videoType=2;
                }
            }
        }else{
            videoType=Integer.parseInt(mVideoInfo.getType());
        }
        setVideoRatio(videoType,TextUtils.isEmpty(mVideoInfo.getVideo_width())?0:Integer.parseInt(mVideoInfo.getVideo_width()),TextUtils.isEmpty(mVideoInfo.getVideo_height())?0:Integer.parseInt(mVideoInfo.getVideo_height()),bindingView.videoPlayer,bindingView.reItemVideo);
        //布局的全局宽高变化监听器
        ViewTreeObserver viewTreeObserver = bindingView.reItemVideo.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                mVideoViewHeight=bindingView.reItemVideo.getHeight();
                bindingView.reVideoGroup.getLayoutParams().height=mVideoViewHeight;
                bindingView.reInvalidView.getLayoutParams().height=mVideoViewHeight;
                bindingView.reItemVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeaderViewHeight=mVideoViewHeight;
            }
        });
        if(bindingView.reTopBar.getVisibility()!=View.VISIBLE){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindingView.reTopBar.setVisibility(View.VISIBLE);
                    bindingView.reTopBar.startAnimation(AnimationUtil.moveToViewTopLocation5());

                }
            },100);
        }
    }

    /**
     * 设置视频宽高
     * @param videoType 视频 宽高类型
     * @param videoWidth 视频分辨率-宽
     * @param videoHeight 视频分辨率-高
     * @param video_player 播放器
     */
    public static void setVideoRatio(int videoType, int videoWidth, int videoHeight, XinQuVideoPlayerStandard video_player, View view) {
        //只有Type,没有宽高
        if(0!=videoType&&0==videoWidth){
            switch (videoType) {
                //默认，正方形
                case 0:
                case 3:
                    video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
                    video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
                    break;
                //宽
                case 1:
                    video_player.widthRatio=16;
                    video_player.heightRatio=9;
                    break;
                //长
                case 2:
                    video_player.widthRatio=3;
                    video_player.heightRatio=4;
                    break;
                default:
                    video_player.widthRatio=9;
                    video_player.heightRatio=16;
            }
            //有Type并且有宽高
        }else if(0!=videoType&&videoWidth>0&&videoHeight>0){
            switch (videoType) {
                //默认，正方形
                case 0:
                case 3:
                    video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
                    video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
                    break;
                //宽
                case 1:
                    double videoHorizontalRatio = new BigDecimal((float)videoWidth/videoHeight).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //5:4
                    if(1.25==videoHorizontalRatio){
                        video_player.widthRatio=5;
                        video_player.heightRatio=4;
                        //16:9
                    }else if(videoHorizontalRatio>=1.78){
                        video_player.widthRatio=16;
                        video_player.heightRatio=9;
                        //4:3
                    }else if(videoHorizontalRatio>=1.33){
                        video_player.widthRatio=4;
                        video_player.heightRatio=3;
                        //5:4
                    }else{
                        video_player.widthRatio=16;
                        video_player.heightRatio=9;
                    }
                    break;
                //长
                case 2:
                    double videoVerticaRatio = new BigDecimal((float)videoHeight/videoWidth).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //5:4
                    if(1.25==videoVerticaRatio){
                        video_player.widthRatio=4;
                        video_player.heightRatio=5;
                        //16:9
                    }else if(videoVerticaRatio>=1.60){
                        video_player.widthRatio=9;
                        video_player.heightRatio=16;
                        //4:3
                    }else if(videoVerticaRatio>=1.33){
                        video_player.widthRatio=3;
                        video_player.heightRatio=4;
                        //5:4
                    }else{
                        video_player.widthRatio=3;
                        video_player.heightRatio=4;
                    }

                    break;
                default:
                    video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
                    video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
            }
            //没Type也没有宽高
        }else{
            video_player.widthRatio=1;
            video_player.heightRatio=1;
        }
        int heightRatio = video_player.getHeightRatio();
        int widthRatio = video_player.getWidthRatio();
        int specHeight = (int) ((ScreenUtils.getScreenWidth() * (float) heightRatio) / widthRatio);
        if(null!=view) view.getLayoutParams().height=specHeight;
    }

    /**
     * 初始化播放，一进来满足条件自动播放视频
     */
    private void createPlayVideo() {
        if(null==mVideoInfo){
            return;
        }
        //点赞
        bindingView.btnPrice.setImageResource(1==mVideoInfo.getIs_interest()?R.drawable.btn_nav_like_selector_red:R.drawable.btn_nav_like_selector_white);
        bindingView.reVideoGroup.setIsPrice(1==mVideoInfo.getIs_interest()?true:false);

        if(!VideoDetailsActivity.this.isFinishing()){
            int imageID = Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0, 5)];
            //设置大播放器封面
            Glide.with(this)
                    .load(mVideoInfo.getCover())
                    .crossFade()//渐变
                    .thumbnail(0.1f)
                    .error(imageID)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(bindingView.videoPlayer.thumbImageView);

            //设置播放器路径信息
            String proxyUrl=mVideoInfo.getPath();
            //设置播放器路径信息
            HttpProxyCacheServer proxy = VideoApplication.getProxy();
            if(null!=proxy){
                proxyUrl= proxy.getProxyUrl(mVideoInfo.getPath());
            }
            bindingView.videoPlayer.setUp(proxyUrl, XinQuVideoPlayer.SCREEN_WINDOW_LIST, ConfigSet.getInstance().isPalyerLoop(),mVideoInfo.getDesp());
            //注册播放视频的状态
            bindingView.videoPlayer.setOnVideoPlayerStateListener(new XinQuVideoPlayer.OnVideoPlayerStateListener() {
                //开始播放
                @Override
                public void onStartPlayer() {
                    if(null!=mVideoInfo) ApplicationManager.getInstance().postVideoPlayState(mVideoInfo.getVideo_id(),0,null);
                }
                //播放完成/重复播放，在循环模式下，该方法会被回调多次，未设置循环播放只回调一次
                @Override
                public void onPlayerCompletion() {
                    if(null!=mVideoInfo) ApplicationManager.getInstance().postVideoPlayState(mVideoInfo.getVideo_id(),0,null);
                    //播放完成后为该设备用户打上极光Service端喜好标签，如果重复，直接覆盖
                    if(null!=mVideoInfo){
                        Set<String> tags=new HashSet<>();
                        //这个标签是针对设备的
                        tags.add(TextUtils.isEmpty(mVideoInfo.getVideoTag())?"普通美女":mVideoInfo.getVideoTag());//视频类别标签
                        JPushInterface.setTags(VideoDetailsActivity.this, (int)System.currentTimeMillis(),tags);
                    }
                }
            });
            //如果视频是审核未通过
            if("2".equals(mVideoInfo.getStatus())){
                bindingView.reInvalidView.setVisibility(View.VISIBLE);
                bindingView.ivInvalidView.setImageResource(R.drawable.ic_look_error_big);
                bindingView.reInvalidView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showCenterToast("该视频审核未通过，无法播放！");
                    }
                });
                return;
            }
            //如果是历史记录过来的，直接播放
            if(!mIsHistory){
                //用户设置了允许WIFI网络下自动播放
                if(1==Utils.getNetworkType()&&ConfigSet.getInstance().isWifiAuthPlayer()){
                    bindingView.videoPlayer.startVideo();
                    //用户设置了允许移动网络下自动播放
                }else if(2==Utils.getNetworkType()&&ConfigSet.getInstance().isMobilePlayer()){
                    bindingView.videoPlayer.startVideo();
                }
            }else{
                bindingView.videoPlayer.startVideo();
            }
        }
        bindingView.reVideoGroup.setImageVisibility();
        //初始化双击点赞
        bindingView.reVideoGroup.setOnDoubleClickListener(new VideoGroupRelativeLayout.OnDoubleClickListener() {
            @Override
            public void onDoubleClick() {
                priceVideo(false);
            }

            @Override
            public void onClick() {

            }
        });
    }


    /**
     * 设置头部数据
     */
    private void initHeaderViewData() {

        if(null==mVideoInfo){
            return;
        }
        if(null==bindingView){
            return;
        }

        if(!VideoDetailsActivity.this.isFinishing()){
            //作者封面
            Glide.with(VideoDetailsActivity.this)
                    .load(Utils.imageUrlChange(mVideoInfo.getLogo()))
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(VideoDetailsActivity.this))
                    .into(bindingView.ivVideoAuthorIcon);
            //用户信息
            try {
                bindingView.videoItemListUserName.setText(TextUtils.isEmpty(mVideoInfo.getNickname())?"火星人":mVideoInfo.getNickname());
                //设置视频介绍，需要单独处理
                String decode = URLDecoder.decode(TextUtils.isEmpty(mVideoInfo.getDesp())?"":mVideoInfo.getDesp(), "UTF-8");
                SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.record_text_color), bindingView.videoItemListTitle, this,null);
                bindingView.videoItemListTitle.setText(topicStyleContent);
                bindingView.llHeaderVideoDespView.setVisibility(TextUtils.isEmpty(mVideoInfo.getDesp())?View.GONE:View.VISIBLE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            bindingView.tvItemPlayCount.setText((TextUtils.isEmpty(mVideoInfo.getPlay_times())?"0":mVideoInfo.getPlay_times())+" 次播放");
            String add_time = mVideoInfo.getAdd_time()+"000";
            bindingView.tvUploadTime.setText(TimeUtils.getTilmNow(Long.parseLong(add_time))+" 发布");
            bindingView.tvCommendCount.setText((TextUtils.isEmpty(mVideoInfo.getComment_times())?"0":mVideoInfo.getComment_times())+" 评论");
            bindingView.tvLikeCount.setText((TextUtils.isEmpty(mVideoInfo.getCollect_times())?"0":mVideoInfo.getCollect_times())+" 喜欢");
            //是自己
            if(!TextUtils.isEmpty(mVideoAuthorID)&&TextUtils.equals(mVideoAuthorID,VideoApplication.getLoginUserID())){
                isVisibilityView(bindingView.llFollowView,false);
            }else{
                isVisibilityView(bindingView.llFollowView,1==mVideoInfo.getIs_follow()?false:true);
            }
            bindingView.reItemVideo.setBackgroundColor(Color.parseColor("#FF212121"));
        }
    }




    /**
     * 加载评论列表
     */
    private void loadComentList() {
        if(null==mVideoInfo){
            return;
        }
        if(null!=mVideoDetailsPresenter){
            mPage++;
            mVideoDetailsPresenter.getComentList(mVideoInfo.getVideo_id(),mPage+"",mPageSize+"");
        }
    }

    /**
     * 分享
     */
    private void onVideoShare() {

        if(null==mVideoInfo){
            return;
        }
        if(!TextUtils.equals("0",mVideoInfo.getIs_private())){
            ToastUtils.showErrorToast(VideoDetailsActivity.this,null,null,"私密视频无法分享，请先更改隐私权限");
            return;
        }

        if("0".equals(mVideoInfo.getStatus())){
            ToastUtils.showErrorToast(VideoDetailsActivity.this,null,null,"视频审核中，无法分享");
            return;
        }

        if("2".equals(mVideoInfo.getStatus())){
            ToastUtils.showErrorToast(VideoDetailsActivity.this,null,null,"视频审核不通过，无法分享");
            return;
        }
        XinQuVideoPlayer.goOnPlayOnPause();
        ShareInfo shareInfo=new ShareInfo();
        shareInfo.setDesp("新趣小视频:"+mVideoInfo.getDesp());
        shareInfo.setTitle("新趣小视频分享");
        shareInfo.setShareTitle("分享视频至");
        shareInfo.setUrl(mVideoInfo.getPath());
        shareInfo.setVideoID(mVideoInfo.getVideo_id());
        shareInfo.setImageLogo(mVideoInfo.getCover());
        onShare(shareInfo);
    }


    @Override
    protected void onShareDialogDismiss() {
        super.onShareDialogDismiss();
        resume();
    }


    /**
     * 提供给子界面的登录方法
     */
    public void login(){
        Intent intent=new Intent(VideoDetailsActivity.this,LoginGroupActivity.class);
        startActivityForResult(intent, Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录意图，需进一步确认
        if(Constant.INTENT_LOGIN_EQUESTCODE==requestCode&&resultCode==Constant.INTENT_LOGIN_RESULTCODE){
            if(null!=data){
                boolean booleanExtra = data.getBooleanExtra(Constant.INTENT_LOGIN_STATE, false);
                if(booleanExtra){
                    getVideoInfo();//刷新
                    if (null!=VideoApplication.getInstance().getUserData()&&!VideoApplication.getInstance().userIsBinDingPhone()) {
                        binDingPhoneNumber("绑定手机号码",Constant.FRAGMENT_TYPE_PHONE_BINDING,"发布视频需要验证手机号");
                    }
                }
            }
        }
    }

    /**
     * 对视频点赞
     */
    private void priceVideo(boolean showDialog) {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }
        if(null==mVideoInfo) return;
        //已经登录
        if(null!=VideoApplication.getInstance().getUserData()){
            if(!TextUtils.equals("0",mVideoInfo.getIs_private())){
                ToastUtils.showErrorToast(VideoDetailsActivity.this,null,null,"私密视频无法收藏，请先更改隐私权限");
                return;
            }
            if(!TextUtils.equals("1",mVideoInfo.getStatus())){
                String status = mVideoInfo.getStatus();
                String message="点赞失败";
                if(TextUtils.equals("0",status)){
                    message="暂时无法点赞，此视频正在审核中!";
                }else if(TextUtils.equals("2",status)){
                    message="点赞失败，此视频审核未通过!";
                }
                ToastUtils.showCenterToast(message);
                return;
            }
            if(showDialog){
                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                    showProgressDialog(1==mVideoInfo.getIs_interest()?"取消点赞中..":"点赞中..",true);
                    mVideoDetailsPresenter.onPriseVideo(mVideoInfo.getVideo_id(),VideoApplication.getLoginUserID());
                }
            }else{
                if(null!=bindingView&&null!=bindingView.reVideoGroup){
                    bindingView.reVideoGroup.startPriceAnimation();
                }
                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                    mVideoDetailsPresenter.onPriseVideo(mVideoInfo.getVideo_id(),VideoApplication.getLoginUserID());
                }
            }
        //未登录
        }else{
            ToastUtils.showCenterToast("点赞需要登录账户");
            login();
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
        if(null==mVideoInfo){
            return;
        }
        //是都已登录
        if(null!=VideoApplication.getInstance().getUserData()){
            if(TextUtils.equals(VideoApplication.getLoginUserID(),mVideoInfo.getUser_id())){
                ToastUtils.showCenterToast("自己时刻都在关注着自己");
                return;
            }
            if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isFollowUser()){
                showProgressDialog("操作中..",true);
                mVideoDetailsPresenter.onFollowUser(mVideoInfo.getUser_id(),VideoApplication.getLoginUserID());
            }
        }else{
            login();
        }
    }

    /**
     * 发送留言消息
     */
    private void sendWordsMessage() {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }
        if(null==mVideoInfo){
            return;
        }
        String wordsmMessage = bindingView.tvInputContent.getText().toString();
        if(TextUtils.isEmpty(wordsmMessage)){
            ToastUtils.showCenterToast("评论内容不能为空！");
            return;
        }

        if(!TextUtils.equals("0",mVideoInfo.getIs_private())){
            ToastUtils.showCenterToast("私密视频无法评论，请先更改隐私权限");
            return;
        }


        if(TextUtils.equals("0",mVideoInfo.getStatus())){
            ToastUtils.showCenterToast("暂时无法评论，此视频正在审核中!");
            return;
        }
        if(TextUtils.equals("2",mVideoInfo.getStatus())){
            ToastUtils.showCenterToast("评论失败，此视频审核未通过!");
            return;
        }

        if(null!=VideoApplication.getInstance().getUserData()){
            if(null!=mVideoInfo){
                showProgressDialog("留言中...",true);
                try {
                    boolean isContrasts=ContentCheckKey.getInstance().contrastKey(wordsmMessage);
                    if(isContrasts){
                        wordsmMessage= new Cheeses().createALTERNATE_TEXT()[Utils.getRandomNum(0,4)];
                    }
                    String encode = URLEncoder.encode(wordsmMessage, "UTF-8");
                    if(null!=mVideoDetailsPresenter){
                        mVideoDetailsPresenter.addComentMessage(VideoApplication.getLoginUserID(),mVideoInfo.getVideo_id(),encode,toUserID);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }else{
            login();
        }
    }


    /**
     * 删除视频
     */
    private void deleteVideo() {
        showProgressDialog("删除视频中...",true);
        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isDeteleVideo()){
            mVideoDetailsPresenter.deleteVideo(VideoApplication.getLoginUserID(),mVideoId);
        }
    }


    /**
     * 删除视频提示
     */
    private void deleteVideoTips() {
        //删除视频提示
        new android.support.v7.app.AlertDialog.Builder(VideoDetailsActivity.this)
                .setTitle("删除视频提示")
                .setMessage(getResources().getString(R.string.detele_video_tips))
                .setNegativeButton("取消", null)
                .setPositiveButton("删除",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteVideo();
                            }
                        }).setCancelable(false).show();
    }

    /**
     * 显示菜单
     */
    private void showMenu() {

        if(TextUtils.isEmpty(mVideoId))return;
        if(null==mVideoInfo){
            return;
        }
        if(!this.isFinishing()){
            List<VideoDetailsMenu> list=new ArrayList<>();
            //是发布此视频的用作者自己
            if(null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(mVideoInfo.getUser_id(),VideoApplication.getLoginUserID())){
                //原本私密的视频
                if(TextUtils.equals("1",mVideoInfo.getIs_private())){
                    //私密和公开属性
                    VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
                    videoDetailsMenu1.setItemID(1);
                    videoDetailsMenu1.setTextColor("#FF576A8D");
                    videoDetailsMenu1.setItemName("将此视频设置为公开视频");
                    list.add(videoDetailsMenu1);
                    //原本公开的视频
                }else{
                    VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
                    videoDetailsMenu1.setItemID(1);
                    videoDetailsMenu1.setTextColor("#FF576A8D");
                    videoDetailsMenu1.setItemName("将此视频设置为私密视频");
                    list.add(videoDetailsMenu1);
                    //是否允许他人下载此视频
                    VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
                    videoDetailsMenu2.setItemID(2);
                    videoDetailsMenu2.setTextColor("#FF576A8D");
                    //原本允许别人下载此作品
                    if(null!=mVideoInfo.getDownload_permiss()&&TextUtils.equals("0",mVideoInfo.getDownload_permiss())){
                        videoDetailsMenu2.setItemName("不允许别人下载此视频");
                        //原本不允许别人下载此作品
                    }else{
                        videoDetailsMenu2.setItemName("允许别人下载此视频");
                    }
                    list.add(videoDetailsMenu2);
                }
                //删除视频
                VideoDetailsMenu videoDetailsMenu3=new VideoDetailsMenu();
                videoDetailsMenu3.setItemID(3);
                videoDetailsMenu3.setTextColor("#FFFF5000");
                videoDetailsMenu3.setItemName("删除此视频");
                list.add(videoDetailsMenu3);
            }else{

                VideoDetailsMenu videoDetailsMenu5=new VideoDetailsMenu();
                videoDetailsMenu5.setItemID(5);
                videoDetailsMenu5.setTextColor("#FFFF5000");
                videoDetailsMenu5.setItemName("举报此用户");
                list.add(videoDetailsMenu5);

                VideoDetailsMenu videoDetailsMenu4=new VideoDetailsMenu();
                videoDetailsMenu4.setItemID(4);
                videoDetailsMenu4.setTextColor("#FFFF5000");
                videoDetailsMenu4.setItemName("举报此视频");
                list.add(videoDetailsMenu4);
            }
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog(VideoDetailsActivity.this);
            commonMenuDialog.setData(list);
            commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                @Override
                public void onItemClick(int itemID) {
                    //公开、私密视频
                    switch (itemID) {
                        case 1:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                if(TextUtils.equals("0",mVideoInfo.getIs_private())){
                                    privateVideoTips();
                                }else{
                                    if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPrivateVideo()){
                                        showProgressDialog("操作中..",true);
                                        mVideoDetailsPresenter.setVideoPrivateState(mVideoId,VideoApplication.getLoginUserID());
                                    }
                                }
                            }
                            break;
                        //下载权限
                        case 2:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isDownloadPermiss()){
                                   showProgressDialog("操作中..",true);
                                    mVideoDetailsPresenter.changeVideoDownloadPermission(mVideoId,VideoApplication.getLoginUserID());
                                }
                            }
                            break;
                        //删除视频
                        case 3:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                deleteVideoTips();
                            }
                            break;
                        //举报视频
                        case 4:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                onReportVideo(mVideoId);
                            }else{
                                ToastUtils.showCenterToast("举报视频需要登录账户");
                                login();
                            }
                            break;
                        //举报用户
                        case 5:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                onReportUser(mVideoInfo.getUser_id());
                            }else{
                                ToastUtils.showCenterToast("举报用户需要登录账户");
                                login();
                            }
                            break;
                    }
                }
            });
            XinQuVideoPlayer.goOnPlayOnPause();
            commonMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    XinQuVideoPlayer.goOnPlayOnResume();
                }
            });
            commonMenuDialog.show();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 举报视频
     * @param video_id
     */
    private void onReportVideo(String video_id) {
        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isReportVideo()){
            showProgressDialog("举报视频中...",true);
            mVideoDetailsPresenter.onReportVideo(VideoApplication.getLoginUserID(),video_id);
        }
    }

    /**
     * 视频私密状态设置提示
     */
    private void privateVideoTips() {
        new android.support.v7.app.AlertDialog.Builder(VideoDetailsActivity.this)
                .setTitle("隐私视频设置")
                .setMessage(getResources().getString(R.string.set_peivate_video_tips))
                .setNegativeButton("取消", null)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPrivateVideo()){
                                    showProgressDialog("操作中..",true);
                                    mVideoDetailsPresenter.setVideoPrivateState(mVideoInfo.getVideo_id(),VideoApplication.getLoginUserID());
                                }
                            }
                        }).setCancelable(false).show();
    }


    /**
     * 举报用户
     * @param accuseUserId
     */
    private void onReportUser(String accuseUserId) {
        if(TextUtils.equals(VideoApplication.getLoginUserID(),mVideoAuthorID)){
            showErrorToast(null,null,"自己不能举报自己");
            return;
        }

        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isReportUser()){
            showProgressDialog("举报用户中...",true);
            mVideoDetailsPresenter.onReportUser(VideoApplication.getLoginUserID(),accuseUserId);
        }
    }

    /**
     * 是否显示关注按钮
     * @param view
     * @param isVisibility
     */
    private void isVisibilityView(final View view, boolean isVisibility) {
        if(null==view) return;
        if(isVisibility){
            if(view.getVisibility()==View.VISIBLE) return;
            view.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = AnimationUtil.moveLeftToViewLocation();
            view.startAnimation(translateAnimation);
        }else{
            if(view.getVisibility()==View.GONE) return;
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewRight();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(translateAnimation);
        }
    }




    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        if(!VideoDetailsActivity.this.isFinishing()){
            Intent intent=new Intent(VideoDetailsActivity.this, ContentFragmentActivity.class);
            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
            intent.putExtra(Constant.KEY_TITLE,title);
            intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
            intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
            intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
            startActivity(intent);
        }
    }



//-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=联网回调-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=

    @Override
    public void showVideoInfoResult(VideoInfo data) {
        mVideoInfo = data.getData().getInfo();
        if(null!=mVideoInfo&&TextUtils.isEmpty(mVideoInfo.getPath())){
            ApplicationManager.getInstance().getCacheExample().remove(mVideoId);
            showErrorToast(null,null,"视频不存在");
            finish();
            return;
        }
        ApplicationManager.getInstance().getCacheExample().remove(mVideoId);
        ApplicationManager.getInstance().getCacheExample().put(mVideoId,mVideoInfo);
        initVideoData();
        //获取视频留言
        mPage=0;
        loadComentList();
    }


    @Override
    public void showLoadVideoInfoError() {
        if(null==mVideoInfo&&null!=mEmptyBindingView){//在没有缓存的情况下也是显示加载失败的
            mEmptyBindingView.emptyView.showErrorView();
        }
    }


    @Override
    public void showComentList(ComentList data) {}

    @Override
    public void showComentList(String videoID, ComentList data) {
        if(null!=mEmptyBindingView) mEmptyBindingView.emptyView.showEmptyView("还没有留言，说两句吧~",R.drawable.iv_com_message_empty);
        if(null!=mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreComplete();
            if(1==mPage){
                mVideoComentListAdapter.setNewData(data.getData().getComment_list());
            }else{
                mVideoComentListAdapter.addData(data.getData().getComment_list());
            }
        }
    }

    /**
     * 留言列表为空
     * @param data
     */
    @Override
    public void showComentListEmpty(String data) {
        if(null!=mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreEnd();
            if(1==mPage){
                if(null!=mEmptyBindingView) mEmptyBindingView.emptyView.showEmptyView("还没有留言，说两句吧~",R.drawable.iv_com_message_empty);
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 加载留言列表失败
     */
    @Override
    public void showComentListError() {
        if(null!=mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreFail();
        }
        if(1==mPage){
            if(null!=mEmptyBindingView) mEmptyBindingView.emptyView.showErrorView();
        }
        if(mPage>0){
            mPage--;
        }
    }

    /**
     * 增加留言回调
     * @param data
     */
    @Override
    public void showAddComentRelult(SingComentInfo data) {
        closeProgressDialog();
        bindingView.tvInputContent.setText("");
        bindingView.tvInputContent.setHint("说点什么...");
        toUserID="0";
        ToastUtils.showCenterToast("评论成功");
        SingComentInfo.DataBean.InfoBean info = data.getData().getInfo();
        if(null!=info){
            ComentList.DataBean.CommentListBean commentListBeanInfo = new ComentList.DataBean.CommentListBean();
            commentListBeanInfo.setAdd_time(String.valueOf(info.getAdd_time()));
            commentListBeanInfo.setComment(info.getComment());
            commentListBeanInfo.setId(info.getId());
            commentListBeanInfo.setLogo(info.getLogo());
            commentListBeanInfo.setNickname(info.getNickname());
            commentListBeanInfo.setUser_id(info.getUser_id());
            commentListBeanInfo.setvideo_id(info.getVideo_id());
            commentListBeanInfo.setTo_nickname(info.getTo_nickname());
            commentListBeanInfo.setTo_user_id(info.getTo_user_id());
            commentListBeanInfo.setComment_id(info.getComment_id());
            updataAddDataToTopAdapter(commentListBeanInfo);
        }
    }

    /**
     * 删除视频结果
     * @param data
     */
    @Override
    public void showDeteleVideoResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&&TextUtils.equals(Constant.DELETE_VIDEO_CONTENT,jsonObject.getString("msg"))){
                //删除成功
                String  videoID= new JSONObject(jsonObject.getString("data")).getString("video_id");
                if(!TextUtils.isEmpty(videoID)){
                    ToastUtils.showCenterToast("删除成功");
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_VIDEO_CHANGED);
                    finish();
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
     * 公开或者私有视频
     * @param result
     */
    @Override
    public void showSetVideoPrivateStateResult(String result) {
        closeProgressDialog();
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject=new JSONObject(result);
                if(null!=jsonObject&&jsonObject.length()>0){
                    //修改权限成功
                    if(1==jsonObject.getInt("code")){
                        if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }
                        JSONObject dataObject=new JSONObject(jsonObject.getString("data"));
                        int isPrivate = dataObject.getInt("is_private");
                        if(null!=mVideoInfo){
                            mVideoInfo.setIs_private(isPrivate+"");
                            ApplicationManager.getInstance().getCacheExample().remove(mVideoId);
                            ApplicationManager.getInstance().getCacheExample().put(mVideoId,mVideoInfo);
                            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_VIDEO_CHANGED);
                        }
                    }else{
                        if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更改了视频下载权限
     * @param result
     */
    @Override
    public void showChangeVideoDownloadPermissionResult(String result) {
        closeProgressDialog();
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject=new JSONObject(result);
                if(null!=jsonObject&&jsonObject.length()>0){
                    //修改权限成功
                    if(1==jsonObject.getInt("code")){
                        if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }
                        JSONObject dataObject=new JSONObject(jsonObject.getString("data"));
                        int isPrivate = dataObject.getInt("download_permiss");
                        if(null!=mVideoInfo){
                            mVideoInfo.setDownload_permiss(isPrivate+"");
                            ApplicationManager.getInstance().getCacheExample().remove(mVideoId);
                            ApplicationManager.getInstance().getCacheExample().put(mVideoId,mVideoInfo);
                        }
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_VIDEO_CHANGED);
                    }else{
                        if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                            ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点赞视频成功
     * @param data
     */
    @Override
    public void showPriseResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")){
                JSONObject resultData = new JSONObject(jsonObject.getString("data"));
                //点赞成功
                if(TextUtils.equals(Constant.PRICE_SUCCESS,jsonObject.getString("msg"))&&null!=bindingView){
                    mVideoInfo.setIs_interest(1);
                    bindingView.reVideoGroup.startPriceAnimation();
                    bindingView.btnPrice.setImageResource(R.drawable.btn_nav_like_selector_red);
                    bindingView.tvLikeCount.setText(resultData.getString("collect_times")+" 喜欢");
                    bindingView.btnPrice.startAnimation(mFollowScaleAnimation);
                    //取消点赞成功
                }else if(TextUtils.equals(Constant.PRICE_UNSUCCESS,jsonObject.getString("msg"))&&null!=bindingView){
                    mVideoInfo.setIs_interest(0);
//                    bindingView.btnPrice.setImageResource(0==followIconState?R.drawable.btn_nav_like_selector_white:R.drawable.btn_nav_like_selector_gray);
                    bindingView.btnPrice.setImageResource(R.drawable.btn_nav_like_selector_white);
                    bindingView.tvLikeCount.setText(resultData.getString("collect_times")+" 喜欢");
                    bindingView.reVideoGroup.setIsPrice(false);
                }
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_VIDEO_CHANGED);
            }else{
                showErrorToast(null,null,"收藏失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    /**
     * 关注成功
     * @param data
     */
    @Override
    public void showFollowUserResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")){
                //关注成功
                if(TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                    mVideoInfo.setIs_follow(1);
                    showFinlishToast(null,null,"关注成功");
                    isVisibilityView(bindingView.llFollowView,false);
                }else if(TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                    mVideoInfo.setIs_follow(0);
                    showFinlishToast(null,null,"取消关注成功");
                }
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED);
            }else{
                showErrorToast(null,null,"关注失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPostPlayCountResult(String data) {
        if(TextUtils.isEmpty(data)){
            return;
        }
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")&& TextUtils.equals(Constant.PLAY_COUNT_SUCCESS,jsonObject.getString("msg"))&&null!=bindingView){
                PlayCountInfo playCountInfo = new Gson().fromJson(data, PlayCountInfo.class);
                PlayCountInfo.DataBean.InfoBean info = playCountInfo.getData().getInfo();
                bindingView.tvItemPlayCount.setText(info.getPlaty_times()+" 次播放");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 举报用户回调
     * @param data
     */
    @Override
    public void showReportUserResult(String data) {
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")){
                showFinlishToast(null,null,jsonObject.getString("msg"));
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
        closeProgressDialog();
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")){
                showFinlishToast(null,null,jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 联网错误
     */
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    /**
     * 点击了话题
     * @param topic
     */
    @Override
    public void onTopicClick(String topic) {
        if(!TextUtils.isEmpty(topic)){
            startTargetActivity(Constant.KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT,topic,VideoApplication.getLoginUserID(),0,topic);
        }
    }

    /**
     * 点击了连接
     * @param url
     */
    @Override
    public void onUrlClick(String url) {
        WebViewActivity.loadUrl(VideoDetailsActivity.this,url,"未知");
    }

    @Override
    public void onAuthoeClick(String author) {
        AuthorDetailsActivity.start(VideoDetailsActivity.this,author);
    }


    /**
     * 点击了用户头像
     * @param userID
     */
    @Override
    public void onAuthorIconClick(String userID) {
        AuthorDetailsActivity.start(VideoDetailsActivity.this,userID);
    }

    /**
     * 点击了留言列表
     * @param data
     */
    @Override
    public void onAuthorItemClick(ComentList.DataBean.CommentListBean data) {
        if(null!=data){
            toUserID=data.getUser_id();
            showMenuTabView();
            bindingView.tvInputContent.setHint("回复 "+data.getNickname());
            showInputKeyBoardDialog(true,false,"回复 "+data.getNickname());
        }
    }

    @Override
    public void onBackPressed() {
        if (XinQuVideoPlayer.backPress()) {
            return;
        }
        //处理是由外部如网页中打开APP，回退界面跳转
        if(SharedPreferencesUtil.getInstance().getBoolean(Constant.KEY_MAIN_INSTANCE,false)){
            super.onBackPressed();
        }else{
            Intent intent=new Intent(VideoDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    private void resume() {
        if(!mIsHistory){
            if(1==Utils.getNetworkType()&& ConfigSet.getInstance().isWifiAuthPlayer()){
                XinQuVideoPlayer.goOnPlayOnResume();
            }else if(2==Utils.getNetworkType()&& ConfigSet.getInstance().isMobilePlayer()){
                XinQuVideoPlayer.goOnPlayOnResume();
            }
        }else{
            XinQuVideoPlayer.goOnPlayOnResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        XinQuVideoPlayer.goOnPlayOnPause();
    }

    @Override
    public void onDestroy() {
        XinQuVideoPlayer.releaseAllVideos();
        if(null!=mVideoDetailsPresenter){
            mVideoDetailsPresenter.detachView();
        }
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
