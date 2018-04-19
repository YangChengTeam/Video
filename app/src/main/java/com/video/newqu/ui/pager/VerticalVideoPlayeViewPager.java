package com.video.newqu.ui.pager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BasePager;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.bean.VideoEventMessage;
import com.video.newqu.bean.VideoInfo;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.VerticalPagerVideoPlayLayoutBinding;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.listener.ShareFinlishListener;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.model.AcFunDanmakuParser;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.contract.VideoDetailsContract;
import com.video.newqu.ui.dialog.CommonMenuDialog;
import com.video.newqu.ui.dialog.FollowWeiXnDialog;
import com.video.newqu.ui.fragment.VerticalVideoPlayCommendFragment;
import com.video.newqu.ui.presenter.VideoDetailsPresenter;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.util.VideoDownloadComposrTask;
import com.video.newqu.view.widget.GlideCircleTransform;
import com.video.newqu.view.widget.VideoGroupRelativeLayout;
import com.xinqu.videoplayer.full.WindowVideoPlayer;
import com.xinqu.videoplayer.full.WindowVideoPlayerStandard;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import cn.jpush.android.api.JPushInterface;
import jp.wasabeef.glide.transformations.BlurTransformation;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/10/9
 * 视频播放的片段View
 */

public class VerticalVideoPlayeViewPager extends BasePager<VerticalPagerVideoPlayLayoutBinding>implements TopicClickListener, VideoDetailsContract.View, ShareFinlishListener {

    private FollowVideoList.DataBean.ListsBean mVideoBean;
    private final int mPoistion;//当前正在显示第几个Item
    private final int mRootViewType;//跳转过来的界面类型
    private VideoDetailsPresenter mVideoDetailsPresenter;
    private ScaleAnimation mFollowScaleAnimation;
    //弹幕相关
    private DanmakuContext mDanmakuContext;
    private AcFunDanmakuParser mParser;
    private ConcurrentLinkedQueue<ComentList.DataBean.CommentListBean> mQueue;
    private final int WHAT_DISPLAY_SINGLE_DANMAKU = 0xffa02;
    private final int TIME_ADD = 1000;
    private List<ComentList.DataBean.CommentListBean> mCommentList;

    /**
     * @param context
     * @param listsBean
     * @param position 用来标记数据的处理，当listsBean发生变化，通知外面刷新并保存最新数据
     */
    public VerticalVideoPlayeViewPager(VerticalVideoPlayActivity context, FollowVideoList.DataBean.ListsBean listsBean, int position, int rootViewType) {
        super(context);
        this.mPoistion=position;
        this.mRootViewType =rootViewType;
        this.mVideoBean=listsBean;
        setContentView(R.layout.vertical_pager_video_play_layout);
    }


    @Override
    public void initViews() {
        bindingView.tvInputEditText.setText("点我发送弹幕~");
        //设置播放进度回调
        bindingView.videoPlayer.setOnVideoPlayerProgressListener(new WindowVideoPlayerStandard.OnVideoPlayerProgressListener() {
            @Override
            public void onStateAutoComplete(int progress) {
                bindingView.bottomProgress.setProgress(progress);
            }

            @Override
            public void onProgressAndText(int progress) {
                bindingView.bottomProgress.setProgress(progress);
            }

            @Override
            public void onBufferProgress(int progress) {
                bindingView.bottomProgress.setSecondaryProgress(progress);
            }

            @Override
            public void onProgressAndTime(int progress) {
                bindingView.bottomProgress.setProgress(progress);
                bindingView.bottomProgress.setSecondaryProgress(0);
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //关闭
                    case R.id.btn_close:
                        if(null!=mContext&&!mContext.isFinishing()){
                            if(mContext instanceof VerticalVideoPlayActivity){
                                mContext.onBackPressed();
                            }
                        }
                        break;
                    //菜单
                    case R.id.btn_menu:
                        showMenu();
                        break;

                    //关注用户
                    case R.id.tv_add_follow:
                        if(null!=VideoApplication.getInstance().getUserData()){
                            if(null!=mVideoBean&&!TextUtils.isEmpty(mVideoBean.getUser_id())){
                                if(TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUser_id())){
                                    ToastUtils.showCenterToast("自己时刻都在关注自己");
                                    return;
                                }
                                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isFollowUser()){
                                    mVideoDetailsPresenter.onFollowUser(mVideoBean.getUser_id(),VideoApplication.getLoginUserID());
                                }
                            }
                        }else{
                            if(null!=mContext&&!mContext.isFinishing()){
                                if(mContext instanceof VerticalVideoPlayActivity){
                                    ((VerticalVideoPlayActivity) mContext).login();
                                }
                            }
                        }

                        break;
                    //分享
                    case R.id.ll_share:
                        onVideoShare();
                        break;

                    //点击了用户头像
                    case R.id.iv_video_author_icon:
                        if(null!=mContext&&!mContext.isFinishing()){
                            if(mContext instanceof VerticalVideoPlayActivity){
                                ((VerticalVideoPlayActivity) mContext).setCureenItem(1);
                            }
                        }
                        break;
                    //打开评论面板，弹出输入框
                    case R.id.tv_input_edit_text:
                        if(null==mVideoBean) return;
                        showCommentView(true);
                        break;

                    //打开评论面板，不弹出输入框
                    case R.id.ll_comment:
                        if(null==mVideoBean) return;
                        showCommentView(false);
                        break;
                    //下载
                    case R.id.btn_download:
                        downloadVideo();
                        break;
                }
            }
        };

        bindingView.tvAddFollow.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.tvInputEditText.setOnClickListener(onClickListener);
        bindingView.ivVideoAuthorIcon.setOnClickListener(onClickListener);
        bindingView.llComment.setOnClickListener(onClickListener);
        bindingView.btnDownload.setOnClickListener(onClickListener);

        bindingView.llPrice.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                priceVideo(true);
            }
        });
        bindingView.llShare.setOnClickListener(onClickListener);
        bindingView.btnMenu.setOnClickListener(onClickListener);
        if(null!=mContext&&!mContext.isFinishing()){
            mVideoDetailsPresenter = new VideoDetailsPresenter(mContext);
            mVideoDetailsPresenter.attachView(this);
        }
        mFollowScaleAnimation = AnimationUtil.followAnimation();
    }


    @Override
    public void initData() {
        if(null==mVideoBean) return;
        if(null!=mContext&&!mContext.isFinishing()&&null!=bindingView){
            //视频封面
            int imageID = Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0, 5)];
            Glide.with(mContext)
                    .load(mVideoBean.getCover())
                    .thumbnail(0.1f)
                    .error(imageID)
                    .crossFade()//渐变
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(bindingView.videoPlayer.thumbImageView);
            //作者封面
            Glide.with(mContext)
                    .load(Utils.imageUrlChange(mVideoBean.getLogo()))
                    .error(R.drawable.iv_mine)
                    .placeholder(R.drawable.iv_mine)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into(bindingView.ivVideoAuthorIcon);

            try {
                String decode = URLDecoder.decode(TextUtils.isEmpty(mVideoBean.getDesp())?"":mVideoBean.getDesp(), "UTF-8");
                //设置视频介绍，需要单独处理
                SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.app_text_style), bindingView.tvVideoDesp,this,null);
                bindingView.tvVideoDesp.setText(topicStyleContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //播放监听
            bindingView.videoPlayer.setOnVideoPlayerStateListener(new WindowVideoPlayer.OnVideoPlayerStateListener() {
                //开始播放,这个方法只回调一次
                @Override
                public void onStartPlayer() {
                    //考虑到内存消耗，弹幕功能应该在这里初始化,界面切换了即销毁
                    if(ConfigSet.getInstance().isShowAutoComment()){//如果用户开启了弹幕
                        if(null!=mVideoBean&&null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isLoading()){
                            mVideoDetailsPresenter.getComentList(mVideoBean.getVideo_id(),"1000","1000");
                        }
                    }
                    if(null!=mVideoBean) ApplicationManager.getInstance().postVideoPlayState(mVideoBean.getVideo_id(),0,null);
                }
                //播放完成/重复播放，在循环模式下，该方法会被回调多次，未设置循环播放只回调一次
                @Override
                public void onPlayerCompletion() {
                    if(null!=mVideoBean) ApplicationManager.getInstance().postVideoPlayState(mVideoBean.getVideo_id(),1,null);
                    //播放完成后为该设备用户打上极光Service端喜好标签，如果重复，直接覆盖
                    if(null!=mContext&&null!=mVideoBean){
                        Set<String> tags=new HashSet<>();
                        //这个标签是针对设备的
                        tags.add(TextUtils.isEmpty(mVideoBean.getCate())?"普通美女":mVideoBean.getCate());//视频类别标签
                        JPushInterface.setTags(mContext, (int)System.currentTimeMillis(),tags);
                    }
                }
            });
            //用户信息
            mVideoBean.getAdd_time().toUpperCase();
            bindingView.tvVideoAuthorName.setText(TextUtils.isEmpty(mVideoBean.getNickname())?"外星人":mVideoBean.getNickname());
            bindingView.tvAddFollow.setVisibility((null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUser_id()))?View.GONE:1==mVideoBean.getIs_follow()?View.GONE:View.VISIBLE);
            bindingView.tvComment.setText(TextUtils.isEmpty(mVideoBean.getComment_times())?"0":mVideoBean.getComment_times());
            bindingView.tvPrice.setText(TextUtils.isEmpty(mVideoBean.getCollect_times())?"0":mVideoBean.getCollect_times());
            bindingView.tvShareCount.setText(TextUtils.isEmpty(mVideoBean.getShare_times())?"0":mVideoBean.getShare_times());
            bindingView.tvUserFans.setText(TextUtils.isEmpty(mVideoBean.getPlay_times())?"0次播放":Utils.formatW(Integer.parseInt(mVideoBean.getPlay_times()))+"次播放");
            bindingView.ivPrice.setImageResource(1==mVideoBean.getIs_interest()?R.drawable.iv_icon_follow_true:R.drawable.iv_follow_selector);
            setVideoRatio(Integer.parseInt(TextUtils.isEmpty(mVideoBean.getType())?"2":mVideoBean.getType()),bindingView.videoPlayer);
            //布局的全局宽高变化监听器
//            ViewTreeObserver viewTreeObserver = bindingView.reItemVideo.getViewTreeObserver();
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//                @Override
//                public void onGlobalLayout() {
//                    int mVideoViewHeight=bindingView.reItemVideo.getHeight();
//                    bindingView.reVideoGroup.getLayoutParams().height=mVideoViewHeight;
//                    bindingView.reItemVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });
            HttpProxyCacheServer proxy = VideoApplication.getProxy();
            String proxyUrl = proxy.getProxyUrl(mVideoBean.getPath());
            bindingView.videoPlayer.setUp(proxyUrl, WindowVideoPlayer.SCREEN_WINDOW_LIST, ConfigSet.getInstance().isPalyerLoop(),mVideoBean.getDesp());
            //设置双击和单击的触摸事件监听
            bindingView.reVideoGroup.setOnDoubleClickListener(new VideoGroupRelativeLayout.OnDoubleClickListener() {
                //双击
                @Override
                public void onDoubleClick() {
                    priceVideo(false);
                }
                //这里将单击事件交给播放器处理
                @Override
                public void onSingleClick() {
                    if(null!=bindingView) bindingView.videoPlayer.onClickTouch();
                }
                //向左滑
                @Override
                public void onLeftSwipe() {}
                //向右滑
                @Override
                public void onRightSwipe() {}
            });
        }
        bindingView.reVideoGroup.setImageVisibility();
        if("2".equals(mVideoBean.getStatus())){
            // 高斯模糊背景 原来 参数：12,5  23,4
            Glide.with(mContext).load(mVideoBean.getCover())
                    .error(R.drawable.bg_live_transit)
                    .bitmapTransform(new BlurTransformation(mContext, 21, 9)).listener(new RequestListener<Object, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }

            }).into(bindingView.icInvalid);
        }
    }

    /**
     * 下载视频
     */
    private void downloadVideo() {
        if(null==mContext||mContext.isFinishing()) return;
        if(null==mVideoBean||TextUtils.isEmpty(mVideoBean.getPath()))  return;
        if(!SharedPreferencesUtil.getInstance().getBoolean(Constant.FOLLOW_WEIXIN,false)){
            FollowWeiXnDialog followWeiXnDialog=new FollowWeiXnDialog(mContext);
            followWeiXnDialog.setOnItemClickListener(new FollowWeiXnDialog.OnItemClickListener() {
                @Override
                public void onFollow() {
                    MobclickAgent.onEvent(mContext, "click_follow_wechat");
//                    SharedPreferencesUtil.getInstance().putBoolean(Constant.FOLLOW_WEIXIN,true);
//                    Intent intent= new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri contentUrl = Uri.parse("http://jump.hupeh.cn/xqsp1223.php");
//                    intent.setData(contentUrl);
//                    mContext.startActivity(intent);
                    Utils.copyString("新趣小视频助手");
                    ToastUtils.showCenterToast("已复制微信号");
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext)
                            .setTitle("新趣小视频助手")
                            .setMessage(mContext.getResources().getString(R.string.open_weixin_tips));
                    builder.setNegativeButton("算了", null);
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                Uri uri = Uri.parse("weixin://");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);
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
        //检查SD读写权限
        RxPermissions.getInstance(mContext).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    //用户已登录
                    if(null!=VideoApplication.getInstance().getUserData()){
                        //发布此时品的主人正式观看的用户自己
                        if(TextUtils.equals(mVideoBean.getUser_id(),VideoApplication.getLoginUserID())){
                            new VideoDownloadComposrTask(mContext,mVideoBean.getPath()).start();
                        }else{
                            //用户允许下载
                            if(null!=mVideoBean.getDownload_permiss()&&TextUtils.equals("0",mVideoBean.getDownload_permiss())){
                                new VideoDownloadComposrTask(mContext,mVideoBean.getPath()).start();
                                //用户不允许下载
                            }else{
                                ToastUtils.showCenterToast("发布此视频的用户未开放他人下载此视频权限！");
                            }
                        }
                        //用户未登录
                    }else{
                        if(null!=mContext&&!mContext.isFinishing()){
                            if(mContext instanceof VerticalVideoPlayActivity){
                                ((VerticalVideoPlayActivity) mContext).login();
                            }
                        }
                    }
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext)
                            .setTitle("SD读取权限申请失败")
                            .setMessage("存储权限被拒绝，请务必授予我们存储权限！是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(mContext,141);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    /**
     * 显示留言面板
     * @param flag
     */
    private void showCommentView(boolean flag) {
        if(null!=mContext&&mContext instanceof VerticalVideoPlayActivity){
            VerticalVideoPlayCommendFragment fragment = VerticalVideoPlayCommendFragment.newInstance(mVideoBean.getVideo_id(), TextUtils.isEmpty(mVideoBean.getComment_times()) ? "0" : mVideoBean.getComment_times(), TextUtils.isEmpty(mVideoBean.getAdd_time()) ? System.currentTimeMillis() + "" : mVideoBean.getAdd_time(),flag);
            fragment.setOnDismissListener(new VerticalVideoPlayCommendFragment.OnFragmentDataChangeListener() {
                @Override
                public void onDismiss(int commentCount) {
                    WindowVideoPlayer.goOnPlayOnResume();
                    bindingView.tvComment.setText(commentCount+"");
                    //通知保存界面数据
                    if(null!=mVideoBean){
                        mVideoBean.setComment_times(commentCount+"");
                        VideoEventMessage videoEventMessage=new VideoEventMessage();
                        videoEventMessage.setMessage(Constant.EVENT_VIDEO_PLAY_PAGE_UPDATA);
                        videoEventMessage.setListsBean(mVideoBean);
                        videoEventMessage.setPoistion(mPoistion);
                        EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
                    }
                }

                @Override
                public void onAddComment(ComentList.DataBean.CommentListBean newCommentData) {
                    if(null!=newCommentData&&ConfigSet.getInstance().isShowAutoComment()){
                        if(null!=mQueue&&null!=mDanmakuContext){
//                            mQueue.add(newCommentData);//添加至任务栈
                            //立即生成一个弹幕
                            addTempDanmaku(newCommentData.getComment());
                        }else if(null!=newCommentData){
                            if(null!=mVideoBean&&null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isLoading()){
                                mVideoDetailsPresenter.getComentList(mVideoBean.getVideo_id(),"1000","1000");
                            }
                        }
                    }
                }
            });
            FragmentManager supportFragmentManager = ((VerticalVideoPlayActivity) mContext).getSupportFragmentManager();
            WindowVideoPlayer.goOnPlayOnPause();
            fragment.show(supportFragmentManager,"comment");
        }
    }

    /**
     * 初始化弹幕和设置并自动开始滚动显示
     */
    private void initDanmaku() {
        if(null!=bindingView&&null!=bindingView.svDanmaku){
            bindingView.svDanmaku.show();
            if(null==mQueue){
                mQueue = new ConcurrentLinkedQueue<>();
            }
            mQueue.clear();
            mQueue.addAll(mCommentList);
            if(null==mDanmakuContext){
                mDanmakuContext = DanmakuContext.create();
            }
            // 设置最大显示行数
            HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
            maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2); // 滚动弹幕最大显2行
            // 设置是否禁止重叠
            HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
            overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, false);//允许X坐标重叠
            overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
            //普通文本弹幕描边设置样式
            mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) //描边的厚度
                    .setDuplicateMergingEnabled(false) //如果是图文混合编排编排，最后不要描边
                    .setScrollSpeedFactor(2.0f) //弹幕的速度。注意！此值越小，速度越快！值越大，速度越慢。// by phil
                    .setScaleTextSize(1.0f)  //缩放的值
//                    .setCacheStuffer(new BackgroundCacheStuffer(),)  // 绘制背景使用BackgroundCacheStuffer
                    .setMaximumLines(maxLinesPair)
                    .preventOverlapping(overlappingEnablePair);

            if(null==mParser){
                mParser = new AcFunDanmakuParser();
            }
            bindingView.svDanmaku.prepare(mParser, mDanmakuContext);
//            bindingView.svDanmaku.showFPS(true);//显示实时帧率，调试模式下开启
            bindingView.svDanmaku.enableDanmakuDrawingCache(true);//保存绘制的缓存
            bindingView.svDanmaku.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void prepared() {//准备完成了

                    bindingView.svDanmaku.start();
                }
            });
            displayDanmaku();//自动开始弹幕的滚动
        }
    }

    /**
     * 弹幕的消息处理
     */
    private Handler mDanmakuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //开始循环的执行弹幕滚动
                case WHAT_DISPLAY_SINGLE_DANMAKU:
                    mDanmakuHandler.removeMessages(WHAT_DISPLAY_SINGLE_DANMAKU);
                    displayDanmaku();
                    break;
            }
        }
    };


    /**
     * 自动任务
     */
    private void displayDanmaku() {
        boolean p = bindingView.svDanmaku.isPaused();
        //如果当前的弹幕由于Android生命周期的原因进入暂停状态，那么不应该不停的消耗弹幕数据
        //要知道，在这里发出一个handler消息，那么将会消费（删掉）ConcurrentLinkedQueue头部的数据
        if (null!=mQueue&&!p) {
            ComentList.DataBean.CommentListBean commentListBean = mQueue.poll();//从集合元素中弹出一条
            if(null!=commentListBean){
                if (!TextUtils.isEmpty(commentListBean.getComment())) {
                    try {
                        String decode = URLDecoder.decode(commentListBean.getComment(), "utf-8");
                        addDanmaku(decode, true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        mDanmakuHandler.sendEmptyMessageDelayed(WHAT_DISPLAY_SINGLE_DANMAKU, TIME_ADD);//1秒钟添加一条留言至字幕中
    }


    /**
     * 添加一条自己发布的留言到字幕上
     * @param comment
     */
    private void addTempDanmaku(String comment) {
        if(null!=bindingView&&null!=mContext&&!mContext.isFinishing()){

            if(null==mParser) {
                mParser = new AcFunDanmakuParser();
            }
            if(null==mDanmakuContext) {
                mDanmakuContext=DanmakuContext.create();
            }
            BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            if (danmaku == null || bindingView.svDanmaku == null) {
                return;
            }
            try {
                String decode = URLDecoder.decode(comment, "utf-8");
                danmaku.text = decode;
                danmaku.padding = 5;
                danmaku.priority = 2;  // 一定会显示, 一般用于本机发送的弹幕
                danmaku.isLive = true;
                danmaku.setTime(bindingView.svDanmaku.getCurrentTime());//立即显示
                danmaku.textSize = 22f * (mParser.getDisplayer().getDensity() - 0.6f); //文本弹幕字体大小
                danmaku.textColor = CommonUtils.getColor(R.color.record_text_color); //文本的颜色
                bindingView.svDanmaku.addDanmaku(danmaku);//调用这个方法，添加字幕到控件，开始滚动
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加一条字幕到控件
     * @param cs
     * @param islive
     */
    private void addDanmaku(CharSequence cs, boolean islive) {
        if(null==mParser) {
            mParser = new AcFunDanmakuParser();
        }
        if(null==mDanmakuContext) {
            mDanmakuContext=DanmakuContext.create();
        }
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || bindingView.svDanmaku == null) {
            return;
        }

        danmaku.text = cs;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.setTime(bindingView.svDanmaku.getCurrentTime() + 1200);//所长时间后加入弹幕组合
        danmaku.textSize = 20f * (mParser.getDisplayer().getDensity() - 0.6f); //文本弹幕字体大小
        danmaku.textColor = Color.WHITE; //文本的颜色
        danmaku.textShadowColor = CommonUtils.getColor(R.color.translucent_22); //文本弹幕描边的颜色
//        danmaku.underlineColor = Color.DKGRAY; //文本弹幕下划线的颜色
//        danmaku.borderColor = CommonUtils.getColor(R.color.translucent_65); //边框的颜色
        bindingView.svDanmaku.addDanmaku(danmaku);//调用这个方法，添加字幕到控件，开始滚动
    }

    /**
     * 供ParentView生命周期调用
     * mQueue一这个对象为准
     */
    public void onPause() {
        if(null!=bindingView&&null!=mQueue&&bindingView.svDanmaku.isPrepared()){
            bindingView.svDanmaku.pause();
        }
    }

    /**
     * 供ParentView生命周期调用
     */

    public void onResume() {
        if(null!=bindingView&&null!=mQueue&&bindingView.svDanmaku.isPrepared()&&bindingView.svDanmaku.isPaused()){
            bindingView.svDanmaku.resume();
            //重新启动handler消息机制，触发弹幕显示
            //如果没有这一个方法，那么显示弹幕的机制将失灵（失去驱动）
            //这个方法就是重新激发弹幕显示的handler机制。
            resumeDanmaku();
        }
    }

    /**
     * 销毁和释放弹幕相关所有资源资源
     */
    public void releaseDanmaku() {
        if(null!=bindingView){
            if(null!=mDanmakuHandler){
                mDanmakuHandler.removeMessages(WHAT_DISPLAY_SINGLE_DANMAKU);
                mDanmakuHandler.removeMessages(0);
            }
            if(bindingView.svDanmaku.isPrepared()){
                bindingView.svDanmaku.stop();//停止弹幕
            }
            bindingView.svDanmaku.stop();
            bindingView.svDanmaku.release();//释放弹幕资源
            if(null!=mQueue){
                mQueue.clear();
            }
            if(null!=mParser) mParser.release();
            if(null!=mCommentList) mCommentList.clear();
            bindingView.svDanmaku.clearDanmakusOnScreen();
            bindingView.svDanmaku.clear();
            mQueue=null;mParser=null;mCommentList=null;
            hideWorkControllerView();
        }
    }

    /**
     * 销毁调用
     */
    public void onDestroy() {
        if(null!=mVideoDetailsPresenter){
            mVideoDetailsPresenter.detachView();
        }
        if(null!=mFollowScaleAnimation&&mFollowScaleAnimation.hasStarted()){
            mFollowScaleAnimation.cancel();
        }
        releaseDanmaku();
        mVideoBean=null;mFollowScaleAnimation=null;bindingView=null;
    }

    /**
     * 驱动弹幕显示机制重新运作起来
     */
    private void resumeDanmaku() {
        if (!mQueue.isEmpty()&&null!=mDanmakuHandler)
            mDanmakuHandler.sendEmptyMessageDelayed(WHAT_DISPLAY_SINGLE_DANMAKU,TIME_ADD);//
    }


    /**
     * 设置视频宽高
     * @param videoRatio
     * @param video_player
     */
    public static void setVideoRatio(int videoRatio, WindowVideoPlayerStandard video_player) {
        video_player.widthRatio=Constant.VIDEO_RATIO_WIDE_HEIGHT;
        video_player.heightRatio=Constant.VIDEO_RATIO_WIDE_WIDTH;
    }

    // TODO: 2018/1/16 处理审核不通过的视频
    /**
     * 提供给外界播放事件
     */
    public void onPlaye() {
        if(null!=mVideoBean&&null!=bindingView){
            if("2".equals(mVideoBean.getStatus())){
                bindingView.reInvalidView.setVisibility(View.VISIBLE);
                bindingView.reInvalidView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showCenterToast("您的视频审核不通过，无法播放和响应其他操作！");
                    }
                });
                bindingView.icInvalid.setAlpha(0.96f);
                return;
            }
            showWorkControllerView();
        }
    }

    /**
     * 显示业务层ControllerView
     */
    private void showWorkControllerView() {
        if(null!=bindingView){
            if(bindingView.viewTopBar.getVisibility()==View.VISIBLE) return;
            TranslateAnimation topToBottom = AnimationUtil.moveToViewTopLocation5();
            topToBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    //审核未通过的视频，直接禁止所有功能，显示不通过按钮,ViewStub这个布局只在需要用到的时候加载和渲染
                    if(null!=bindingView) bindingView.videoPlayer.startVideo();
                    saveLocationHistoryList();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            TranslateAnimation bottomToTop = AnimationUtil.moveToViewLocation5();
            bindingView.viewTopBar.setVisibility(View.VISIBLE);
            bindingView.viewBottomBar.setVisibility(View.VISIBLE);
            bindingView.viewTopBar.startAnimation(topToBottom);
            bindingView.viewBottomBar.startAnimation(bottomToTop);
        }
    }

    /**
     * 隐藏业务层ControllerView
     */
    private void  hideWorkControllerView(){
        if(null!=bindingView){
            //还原播放器进度条和隐藏业务逻辑控制器
            bindingView.bottomProgress.setProgress(0);
            bindingView.bottomProgress.setSecondaryProgress(0);
            bindingView.viewTopBar.setVisibility(View.GONE);
            bindingView.viewBottomBar.setVisibility(View.GONE);
        }
    }



    /**
     * 保存播放的历史记录
     */
    private void saveLocationHistoryList() {
        try {
            if(null==mVideoBean|| TextUtils.isEmpty(mVideoBean.getVideo_id())) return;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    UserPlayerVideoHistoryList userLookVideoList=new UserPlayerVideoHistoryList();
                    userLookVideoList.setUserName(TextUtils.isEmpty(mVideoBean.getNickname())?"火星人":mVideoBean.getNickname());
                    userLookVideoList.setUserSinger("该宝宝没有个性签名");
                    userLookVideoList.setUserCover(mVideoBean.getLogo());
                    userLookVideoList.setVideoDesp(mVideoBean.getDesp());
                    userLookVideoList.setVideoLikeCount(TextUtils.isEmpty(mVideoBean.getCollect_times())?"0":mVideoBean.getCollect_times());
                    userLookVideoList.setVideoCommendCount(TextUtils.isEmpty(mVideoBean.getComment_times())?"0":mVideoBean.getComment_times());
                    userLookVideoList.setVideoShareCount(TextUtils.isEmpty(mVideoBean.getShare_times())?"0":mVideoBean.getShare_times());
                    userLookVideoList.setUserId(mVideoBean.getUser_id());
                    userLookVideoList.setVideoId(mVideoBean.getVideo_id());
                    userLookVideoList.setVideoCover(mVideoBean.getCover());
                    userLookVideoList.setUploadTime(mVideoBean.getAdd_time());
                    userLookVideoList.setItemIndex(0);
                    userLookVideoList.setAddTime(System.currentTimeMillis());
                    userLookVideoList.setIs_interest(mVideoBean.getIs_interest());
                    userLookVideoList.setIs_follow(mVideoBean.getIs_follow());
                    userLookVideoList.setVideoPath(mVideoBean.getPath());
                    userLookVideoList.setVideoPlayerCount(TextUtils.isEmpty(mVideoBean.getPlay_times())?"0":mVideoBean.getPlay_times());
                    userLookVideoList.setVideoType(TextUtils.isEmpty(mVideoBean.getType())?"2":mVideoBean.getType());
                    userLookVideoList.setStatus(mVideoBean.getStatus());
                    ApplicationManager.getInstance().getUserPlayerDB().insertNewPlayerHistoryOfObject(userLookVideoList);
                }
            }.start();
        }catch (Exception e){

        }
    }


    /**
     * 分享
     */
    private void onVideoShare() {

        if(null==mVideoBean){
            return;
        }
        //用户自己的作品过来的时候
        if(mRootViewType ==Constant.FRAGMENT_TYPE_WORKS){
            if(!TextUtils.equals("0",mVideoBean.getIs_private())){
                showErrorToast(null,null,"私密视频无法分享，请先更改隐私权限");
                return;
            }
        }

        if("0".equals(mVideoBean.getStatus())){
            ToastUtils.showErrorToast(mContext,null,null,"视频审核中，无法分享");
            return;
        }

        if("2".equals(mVideoBean.getStatus())){
            ToastUtils.showErrorToast(mContext,null,null,"视频审核不通过，无法分享");
            return;
        }

        ShareInfo shareInfo=new ShareInfo();
        shareInfo.setUrl(mVideoBean.getPath());
        shareInfo.setNetUrl(mVideoBean.getPath());
        shareInfo.setDesp("新趣小视频:"+mVideoBean.getDesp());
        shareInfo.setTitle("新趣小视频分享");
        shareInfo.setVideoID(mVideoBean.getVideo_id());
        shareInfo.setImageLogo(mVideoBean.getCover());
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).onShare(shareInfo,this);
            }
        }
    }

    /**
     * 对视频点赞
     * @param showDialog
     */
    private void priceVideo(boolean showDialog) {
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }
        if(null==mVideoBean){
            return;
        }
        //已经登录
        if(null!=VideoApplication.getInstance().getUserData()){
            if(null!=mVideoBean){
                if(mRootViewType ==Constant.FRAGMENT_TYPE_WORKS){
                    if(!TextUtils.equals("0",mVideoBean.getIs_private())){
                        showErrorToast(null,null,"私密视频无法收藏，请先更改隐私权限");
                        return;
                    }
                }
                //点击的按钮
                if(showDialog){
                    if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                        if(null!=mContext&&!mContext.isFinishing()){
                            if(mContext instanceof VerticalVideoPlayActivity){
                                ((VerticalVideoPlayActivity) mContext).showProgressDialog(1==mVideoBean.getIs_interest()?"取消点赞中..":"点赞中..",true);
                            }
                        }
                        mVideoDetailsPresenter.onPriseVideo(mVideoBean.getVideo_id(),VideoApplication.getLoginUserID());
                    }
                //双击的屏幕
                }else{
                    bindingView.reVideoGroup.startPriceAnimation();
                    if(mVideoBean.getIs_interest()!=1){
                        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                            mVideoDetailsPresenter.onPriseVideo(mVideoBean.getVideo_id(),VideoApplication.getLoginUserID());
                        }
                    }
                }
            }
            //未登录
        }else{
            ToastUtils.showCenterToast("点赞需要登录账户");
            if(null!=mContext&&!mContext.isFinishing()){
                if(mContext instanceof VerticalVideoPlayActivity){
                    ((VerticalVideoPlayActivity) mContext).login();
                }
            }
        }
    }

    /**
     * 菜单
     */
    private void showMenu() {
        if(null==mVideoBean){
            return;
        }
        if(null!=mContext&&!mContext.isFinishing()){
            List<VideoDetailsMenu> list=new ArrayList<>();
            VideoDetailsMenu videoDetailsMenu6=new VideoDetailsMenu();
            videoDetailsMenu6.setItemID(6);
            videoDetailsMenu6.setTextColor("#FF576A8D");
            videoDetailsMenu6.setItemName(ConfigSet.getInstance().isShowAutoComment()?"不显示留言字幕":"显示留言字幕");
            list.add(videoDetailsMenu6);
            //是发布此视频的用作者自己
            if(null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(mVideoBean.getUser_id(),VideoApplication.getLoginUserID())&&mRootViewType==Constant.FRAGMENT_TYPE_WORKS){
                //原本私密的视频
                if(TextUtils.equals("1",mVideoBean.getIs_private())){
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
                    if(null!=mVideoBean.getDownload_permiss()&&TextUtils.equals("0",mVideoBean.getDownload_permiss())){
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
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog(mContext);
            commonMenuDialog.setData(list);
            commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                @Override
                public void onItemClick(int itemID) {
                    //公开、私密视频
                    switch (itemID) {
                        case 1:
                            if(null!=mContext&&!mContext.isFinishing()&&null!=VideoApplication.getInstance().getUserData()){
                                //原本是公开的，设置为私密前提示一下
                                if(TextUtils.equals("0",mVideoBean.getIs_private())){
                                    privateVideoTips();
                                }else{
                                    if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPrivateVideo()){
                                        if(mContext instanceof VerticalVideoPlayActivity){
                                            ((VerticalVideoPlayActivity) mContext).showProgressDialog("操作中..",true);
                                        }
                                        mVideoDetailsPresenter.setVideoPrivateState(mVideoBean.getVideo_id(),VideoApplication.getLoginUserID());
                                    }
                                }
                            }
                            break;
                        //下载权限
                        case 2:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                if(null!=mContext&&null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isDownloadPermiss()){
                                    if(mContext instanceof VerticalVideoPlayActivity){
                                        ((VerticalVideoPlayActivity) mContext).showProgressDialog("操作中..",true);
                                    }
                                    mVideoDetailsPresenter.changeVideoDownloadPermission(mVideoBean.getVideo_id(),VideoApplication.getLoginUserID());
                                }
                            }
                            break;
                        //删除视频
                        case 3:
                            if(null!=mContext&&!mContext.isFinishing()&&null!=VideoApplication.getInstance().getUserData()){
                                deleteVideoTips();
                            }
                            break;
                        //举报视频
                        case 4:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                onReportVideo(mVideoBean.getVideo_id());
                            }else{
                                ToastUtils.showCenterToast("举报视频需要登录账户");
                                if(mContext instanceof VerticalVideoPlayActivity){
                                    ((VerticalVideoPlayActivity) mContext).login();
                                }
                            }
                            break;
                        //举报用户
                        case 5:
                            if(null!=VideoApplication.getInstance().getUserData()){
                                onReportUser(mVideoBean.getUser_id());
                            }else{
                                ToastUtils.showCenterToast("举报用户需要登录账户");
                                if(mContext instanceof VerticalVideoPlayActivity){
                                    ((VerticalVideoPlayActivity) mContext).login();
                                }
                            }
                            break;
                        //字幕显示与否
                        case 6:
                            ConfigSet.getInstance().setShowAutoComment();
                            boolean showAutoComment = ConfigSet.getInstance().isShowAutoComment();
                            //用户开启了弹幕
                            if(!showAutoComment){
                                bindingView.svDanmaku.hide();
                                releaseDanmaku();
                            }else{
                                if(null!=bindingView&&bindingView.videoPlayer.isPlaying()&&null!=mVideoBean&&null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isLoading()){
                                    mVideoDetailsPresenter.getComentList(mVideoBean.getVideo_id(),"1","20");
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
     * 删除视频提示
     */
    private void deleteVideoTips() {
        //删除视频提示
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setTitle("删除视频提示")
                .setMessage(mContext.getResources().getString(R.string.detele_video_tips))
                .setNegativeButton("取消", null)
                .setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mContext instanceof VerticalVideoPlayActivity){
                            ((VerticalVideoPlayActivity) mContext).showProgressDialog("删除视频中..",true);
                        }
                        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isDeteleVideo()){
                            mVideoDetailsPresenter.deleteVideo(VideoApplication.getLoginUserID(),mVideoBean.getVideo_id());
                        }
                    }
                }).setCancelable(false).show();

    }


    /**
     * 视频私密状态设置提示
     */
    private void privateVideoTips() {

        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setTitle("隐私视频设置")
                .setMessage(mContext.getResources().getString(R.string.set_peivate_video_tips))
                .setNegativeButton("取消", null)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPrivateVideo()){
                                    if(mContext instanceof VerticalVideoPlayActivity){
                                        ((VerticalVideoPlayActivity) mContext).showProgressDialog("操作中..",true);
                                    }
                                    mVideoDetailsPresenter.setVideoPrivateState(mVideoBean.getVideo_id(),VideoApplication.getLoginUserID());
                                }
                            }
                        }).setCancelable(false).show();
    }



    /**
     * 举报视频
     * @param video_id
     */
    private void onReportVideo(String video_id) {
        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isReportVideo()){
            if(null!=mContext&&!mContext.isFinishing()){
                if(mContext instanceof VerticalVideoPlayActivity){
                    ((VerticalVideoPlayActivity) mContext).showProgressDialog("举报视频中...",true);
                }
            }
            mVideoDetailsPresenter.onReportVideo(VideoApplication.getLoginUserID(),video_id);
        }
    }

    /**
     * 举报用户
     * @param accuseUserId
     */
    private void onReportUser(String accuseUserId) {
        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isReportUser()){
            if(null!=mContext&&!mContext.isFinishing()){
                if(mContext instanceof VerticalVideoPlayActivity){
                    ((VerticalVideoPlayActivity) mContext).showProgressDialog("举报用户中...",true);
                }
            }
            mVideoDetailsPresenter.onReportUser(VideoApplication.getLoginUserID(),accuseUserId);
        }
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

    @Override
    public void onUrlClick(String url) {

    }

    @Override
    public void onAuthoeClick(String authorID) {

    }


    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        if(null!=mContext&&!mContext.isFinishing()){
            Intent intent=new Intent(mContext, ContentFragmentActivity.class);
            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
            intent.putExtra(Constant.KEY_TITLE,title);
            intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
            intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
            intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
            mContext.startActivity(intent);
        }
    }


    //==========================================数据回调=============================================

    @Override
    public void showErrorView() {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
    }

    @Override
    public void complete() {}

    /**
     * 获取留言列表成功,这个回调在这里是获取到了所有的留言，有新增留言的话，直接add到弹幕中去显示
     */
    @Override
    public void showComentList(String videoID,ComentList data) {
        //是请求者提交的获取留言任务
        if(null!=mVideoBean&&null!=videoID&&TextUtils.equals(videoID,mVideoBean.getVideo_id())){//校验回调回来的数据是否是刚才的调用者
            mCommentList = data.getData().getComment_list();
            //初始化并自动开始弹幕
            initDanmaku();
        }
    }


    /**
     * 获取留言列表成功
     */
    @Override
    public void showComentList(ComentList data) {}

    /**
     * 获取留言列表为空
     */
    @Override
    public void showComentListEmpty(String data) {}

    /**
     * 获取留言列表失败
     */
    @Override
    public void showComentListError() {}

    /**
     * 添加留言记录回调
     * @param data
     */
    @Override
    public void showAddComentRelult(SingComentInfo data) {}

    /**
     * 点赞回调
     * @param data
     */
    @Override
    public void showPriseResult(String data) {

        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        if(TextUtils.isEmpty(data)){

            ToastUtils.showCenterToast("点赞失败");
        }else{
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(1==jsonObject.getInt("code")&&null!=bindingView){
                    //点赞成功
                    if(TextUtils.equals(Constant.PRICE_SUCCESS,jsonObject.getString("msg"))){
                        bindingView.ivPrice.setImageResource(R.drawable.iv_icon_follow_true);

                        String collectCount = mVideoBean.getCollect_times();
                        int intCollectCount = Integer.parseInt(collectCount);
                        intCollectCount++;
                        bindingView.tvPrice.setText(intCollectCount+"");
                        if(null!=mVideoBean) {
                            mVideoBean.setIs_interest(1);
                            mVideoBean.setCollect_times(intCollectCount+"");
                        }
                        bindingView.reVideoGroup.startPriceAnimation();
                        if(null!=mFollowScaleAnimation){
                            bindingView.ivPrice.startAnimation(mFollowScaleAnimation);
                        }
                        //取消点赞成功
                    }else if(TextUtils.equals(Constant.PRICE_UNSUCCESS,jsonObject.getString("msg"))){
                        if(null!=mVideoBean) mVideoBean.setIs_interest(0);
                        bindingView.ivPrice.setImageResource(R.drawable.iv_follow_selector);

                        String collectCount = mVideoBean.getCollect_times();
                        int intCollectCount = Integer.parseInt(collectCount);
                        if(intCollectCount>0){
                            intCollectCount--;
                        }
                        bindingView.tvPrice.setText(intCollectCount+"");

                        if(null!=mVideoBean) {
                            mVideoBean.setIs_interest(1);
                            mVideoBean.setCollect_times(intCollectCount+"");
                        }
                    }
                    //点赞完成后通知持有者刷新最新的集合
                    if(null!=mVideoBean){
                        VideoEventMessage videoEventMessage=new VideoEventMessage();
                        videoEventMessage.setMessage(Constant.EVENT_VIDEO_PLAY_PAGE_UPDATA);
                        videoEventMessage.setListsBean(mVideoBean);
                        videoEventMessage.setPoistion(mPoistion);
                        EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
                    }
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_VIDEO_CHANGED);
                }else{
                    showErrorToast(null,null,"收藏失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 关注用户回调
     */
    @Override
    public void showFollowUserResult(String data) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(jsonObject.length()>0){
                if(1==jsonObject.getInt("code")&&null!=bindingView){
                    //关注成功
                    if(TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                        if(null!=mVideoBean) mVideoBean.setIs_follow(1);
                        bindingView.tvAddFollow.setVisibility(View.GONE);
                    }else if(TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                        if(null!=mVideoBean) mVideoBean.setIs_follow(0);
                        bindingView.tvAddFollow.setVisibility((null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUser_id()))?View.GONE:View.VISIBLE);
                    }
                    //点赞完成后通知持有者刷新最新的集合
                    if(null!=mVideoBean){
                        VideoEventMessage videoEventMessage=new VideoEventMessage();
                        videoEventMessage.setMessage(Constant.EVENT_VIDEO_PLAY_PAGE_UPDATA);
                        videoEventMessage.setListsBean(mVideoBean);
                        videoEventMessage.setPoistion(mPoistion);
                        EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
                    }
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_FOLLOW_USER_CHANGED);
                }else{
                    ToastUtils.showCenterToast(jsonObject.getString("msg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示视频详细信息
     * @param data
     */
    @Override
    public void showVideoInfoResult(VideoInfo data) {}

    /**
     * 举报用户结果
     * @param data
     */
    @Override
    public void showReportUserResult(String data) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(null!=jsonObject&&jsonObject.length()>0){
                    if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                        ToastUtils.showCenterToast(jsonObject.getString("msg"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 举报视频结果
     * @param data
     */
    @Override
    public void showReportVideoResult(String data) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(null!=jsonObject&&jsonObject.length()>0){
                    if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                        ToastUtils.showCenterToast(jsonObject.getString("msg"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载视频信息失败
     */
    @Override
    public void showLoadVideoInfoError() {}

    /**
     * 删除视频的结果
     * @param result
     */
    @Override
    public void showDeteleVideoResult(String result) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(null!=jsonObject&&jsonObject.length()>0){
                        //删除视频成功
                        if(1==jsonObject.getInt("code")){
                            WindowVideoPlayer.releaseAllVideos();
                            if(!TextUtils.isEmpty(jsonObject.getString("msg"))){
                                ToastUtils.showCenterToast(jsonObject.getString("msg"));
                            }
                            //删除成功
                            String  videoID= new JSONObject(jsonObject.getString("data")).getString("video_id");
                            if(!TextUtils.isEmpty(videoID)){
                                VideoEventMessage videoEventMessage=new VideoEventMessage();
                                videoEventMessage.setMessage(Constant.EVENT_TOPIC_VIDEO_PLAY_PAGE_DETELE);
                                videoEventMessage.setListsBean(mVideoBean);
                                videoEventMessage.setPoistion(mPoistion);
                                EventBus.getDefault().post(videoEventMessage);
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
    }

    /**
     * 改变了视频的隐私权限,需要刷新缓存数据
     * @param result
     */
    @Override
    public void showSetVideoPrivateStateResult(String result) {
        if(null!=mContext&&!mContext.isFinishing()) {
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
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
                            if(null!=mVideoBean){
                                mVideoBean.setIs_private(isPrivate+"");
                                VideoEventMessage videoEventMessage=new VideoEventMessage();
                                videoEventMessage.setMessage(Constant.EVENT_TOPIC_VIDEO_PLAY_PAGE_UPDATA);
                                videoEventMessage.setListsBean(mVideoBean);
                                videoEventMessage.setPoistion(mPoistion);
                                EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
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
    }

    /**
     * 改变了视频的下载权限，需要刷新缓存数据
     * @param result
     */
    @Override
    public void showChangeVideoDownloadPermissionResult(String result) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalVideoPlayActivity){
                ((VerticalVideoPlayActivity) mContext).closeProgressDialog();
            }
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
                            if(null!=mVideoBean){
                                mVideoBean.setDownload_permiss(isPrivate+"");
                                VideoEventMessage videoEventMessage=new VideoEventMessage();
                                videoEventMessage.setMessage(Constant.EVENT_TOPIC_VIDEO_PLAY_PAGE_UPDATA);
                                videoEventMessage.setListsBean(mVideoBean);
                                videoEventMessage.setPoistion(mPoistion);
                                EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
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
    }

    /**
     * 增加了视频播放记录
     * @param data
     */
    @Override
    public void showPostPlayCountResult(String data) {}

    /**
     * 分享完成了
     * @param videoID
     * @param newShareCount
     */
    @Override
    public void shareFinlish(String videoID, String newShareCount) {
        if(null!=bindingView){
            //点赞完成后通知持有者刷新最新的集合
            if(null!=mVideoBean){
                String share_times = mVideoBean.getShare_times();
                int shareCount = Integer.parseInt(share_times);
                shareCount++;
                bindingView.tvShareCount.setText(shareCount+"");
                mVideoBean.setShare_times(shareCount+"");
                VideoEventMessage videoEventMessage=new VideoEventMessage();
                videoEventMessage.setMessage(Constant.EVENT_TOPIC_VIDEO_PLAY_PAGE_UPDATA);
                videoEventMessage.setListsBean(mVideoBean);
                videoEventMessage.setPoistion(mPoistion);
                EventBus.getDefault().post(videoEventMessage);//通知持有者刷新界面
            }
        }
    }
}
