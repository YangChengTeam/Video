package com.video.newqu.ui.pager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danikula.videocache.HttpProxyCacheServer;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BasePager;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.bean.VideoEventMessage;
import com.video.newqu.bean.VideoInfo;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.PagerVideoPlayerLayoutBinding;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.activity.VerticalHistoryVideoPlayActivity;
import com.video.newqu.ui.contract.VideoDetailsContract;
import com.video.newqu.ui.fragment.VerticalVideoCommentFragment;
import com.video.newqu.ui.presenter.VideoDetailsPresenter;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.CommonUtils;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/10/9
 * 专为历史播放记录特殊性创建的ViewPager
 */

public class VerticalHistoryVidepPlayViewPager extends BasePager<PagerVideoPlayerLayoutBinding> implements TopicClickListener, VideoDetailsContract.View{

    private final UserPlayerVideoHistoryList mVideoBean;
    private final int mPoistion;//当前正在显示第几个Item
    private VideoDetailsPresenter mVideoDetailsPresenter;
    private ScaleAnimation mFollowScaleAnimation;

    /**
     * @param context
     * @param listsBean
     * @param position 用来标记数据的处理，当listsBean发生变化新并保存最新数据
     */
    public VerticalHistoryVidepPlayViewPager(VerticalHistoryVideoPlayActivity context, UserPlayerVideoHistoryList listsBean, int position) {
        super(context);
        this.mPoistion=position;
        this.mVideoBean=listsBean;
        setContentView(R.layout.pager_video_player_layout);
    }

    @Override
    public void initViews() {
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
                            mContext.onBackPressed();
                        }
                        break;
                    //关注用户
                    case R.id.tv_add_follow:
                        if(null!=VideoApplication.getInstance().getUserData()){
                            if(null!=mVideoBean&&!TextUtils.isEmpty(mVideoBean.getUserId())){
                                if(TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUserId())){
                                    ToastUtils.showCenterToast("自己时刻都在关注自己");
                                    return;
                                }
                                if(null!=mVideoDetailsPresenter){
                                    mVideoDetailsPresenter.onFollowUser(mVideoBean.getUserId(),VideoApplication.getLoginUserID());
                                }
                            }
                        }else{
                            if(null!=mContext&&!mContext.isFinishing()){
                                if(mContext instanceof VerticalHistoryVideoPlayActivity){
                                    ((VerticalHistoryVideoPlayActivity) mContext).login();
                                }
                            }
                        }

                        break;
                    //评论，直接打开输入面板，直接打开输入法，不显示表情
                    case R.id.tv_input_edit_text:
                        if(null==mVideoBean) return;
                        showCommentView(true);
                        break;
                    //不打开留言列表
                    case R.id.ll_comment:
                        if(null==mVideoBean) return;
                        showCommentView(false);
                        break;
                    //分享
                    case R.id.ll_share:
                        onVideoShare();
                        break;

                    //点击了用户头像
                    case R.id.iv_video_author_icon:
                        if(null!=mContext&&!mContext.isFinishing()&&!TextUtils.isEmpty(mVideoBean.getUserId())){
                            AuthorDetailsActivity.start(mContext,mVideoBean.getUserId());
                        }
                        break;
                    //下载视频
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
        mVideoDetailsPresenter = new VideoDetailsPresenter(mContext);
        mVideoDetailsPresenter.attachView(this);
        mFollowScaleAnimation = AnimationUtil.followAnimation();
    }

    @Override
    public void initData() {
        if(null==mVideoBean)return;
        if(null!=mContext&&!mContext.isFinishing()){
            //视频封面
            int imageID = Cheeses.IMAGE_EMPTY_COLOR[Utils.getRandomNum(0, 5)];
            Glide.with(mContext)
                    .load(mVideoBean.getVideoCover())
                    .thumbnail(0.1f)
                    .error(imageID)
                    .crossFade()//渐变
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(bindingView.videoPlayer.thumbImageView);
            //作者封面
            Glide.with(mContext)
                    .load(Utils.imageUrlChange(mVideoBean.getUserCover()))
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
                String decode = URLDecoder.decode(TextUtils.isEmpty(mVideoBean.getVideoDesp())?"":mVideoBean.getVideoDesp(), "UTF-8");
                //设置视频介绍，需要单独处理
                SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.app_text_style), bindingView.tvVideoDesp,this,null);
                bindingView.tvVideoDesp.setText(topicStyleContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //用户信息
            bindingView.tvVideoAuthorName.setText(TextUtils.isEmpty(mVideoBean.getUserName())?"外星人":mVideoBean.getUserName());
            bindingView.tvAddFollow.setVisibility((null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUserId()))?View.GONE:1==mVideoBean.getIs_follow()?View.GONE:View.VISIBLE);
            bindingView.tvComment.setText(TextUtils.isEmpty(mVideoBean.getVideoCommendCount())?"0":mVideoBean.getVideoCommendCount());
            bindingView.tvPrice.setText(TextUtils.isEmpty(mVideoBean.getVideoLikeCount())?"0":mVideoBean.getVideoLikeCount());
            bindingView.tvShareCount.setText(TextUtils.isEmpty(mVideoBean.getVideoShareCount())?"0":mVideoBean.getVideoShareCount());
            bindingView.tvUserFans.setText(TextUtils.isEmpty(mVideoBean.getVideoPlayerCount())?"0次播放":Utils.formatW(Integer.parseInt(mVideoBean.getVideoPlayerCount()))+"次播放");
            bindingView.ivPrice.setImageResource(1==mVideoBean.getIs_interest()?R.drawable.iv_icon_follow_true:R.drawable.iv_follow_selector);
            setVideoRatio(Integer.parseInt(mVideoBean.getVideoType()),bindingView.videoPlayer);
            HttpProxyCacheServer proxy = VideoApplication.getProxy();
            String proxyUrl = proxy.getProxyUrl(mVideoBean.getVideoPath());
            bindingView.videoPlayer.setUp(proxyUrl, WindowVideoPlayer.SCREEN_WINDOW_LIST, ConfigSet.getInstance().isPalyerLoop(),mVideoBean.getVideoDesp());
            //监听播放状态
            bindingView.videoPlayer.setOnVideoPlayerStateListener(new WindowVideoPlayer.OnVideoPlayerStateListener() {
                //开始播放
                @Override
                public void onStartPlayer() {
                    if(null!=mVideoBean) ApplicationManager.getInstance().postVideoPlayState(mVideoBean.getVideoId(),0,null);
                }
                //播放完成/重复播放，在循环模式下，该方法会被回调多次，未设置循环播放只回调一次
                @Override
                public void onPlayerCompletion() {
                    if(null!=mVideoBean) ApplicationManager.getInstance().postVideoPlayState(mVideoBean.getVideoId(),1,null);
                }
            });

            bindingView.reVideoGroup.setOnDoubleClickListener(new VideoGroupRelativeLayout.OnDoubleClickListener() {

                //双击
                @Override
                public void onDoubleClick() {
                    //已经登录
                    if(null!=VideoApplication.getInstance().getUserData()){
                        if(null!=mVideoBean){
                            bindingView.reVideoGroup.startPriceAnimation();
                            if(1!=mVideoBean.getIs_interest()){
                                priceVideo(false);
                            }
                        }else{
                            bindingView.reVideoGroup.startPriceAnimation();
                        }
                        //未登录
                    }else{
                        if(null!=mContext&&!mContext.isFinishing()){
                            ToastUtils.showCenterToast("点赞需要登录账户");
                            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                                ((VerticalHistoryVideoPlayActivity) mContext).login();
                            }
                        }
                    }
                }

                //单击
                @Override
                public void onSingleClick() {
                    if(null!=bindingView) bindingView.videoPlayer.onClickTouch();
                }

                //向左滑
                @Override
                public void onLeftSwipe() {
                    if(bindingView.reControllerView.getVisibility()!=View.VISIBLE){
                        showControllerView(true);
                        return;
                    }
                    //拉出用户中心界面
                    if(null!=mContext&&!mContext.isFinishing()){
                        AuthorDetailsActivity.start(mContext,mVideoBean.getUserId());
                    }
                }

                //向右滑
                @Override
                public void onRightSwipe() {
                    showControllerView(false);
                }
            });
        }
        bindingView.reVideoGroup.setImageVisibility();
    }

    /**
     * 显示留言面板
     * @param flag
     */
    private void showCommentView(boolean flag) {
        if(null!=mContext&&mContext instanceof VerticalHistoryVideoPlayActivity){
            if(null!=mContext&&!mContext.isFinishing()){
                VerticalVideoCommentFragment fragment = VerticalVideoCommentFragment.newInstance(mVideoBean.getVideoId(), TextUtils.isEmpty(mVideoBean.getVideoCommendCount()) ? "0" : mVideoBean.getVideoCommendCount(),mVideoBean.getAddTime()+"",flag);
                fragment.setOnDismissListener(new VerticalVideoCommentFragment.OnFragmentDataChangeListener() {
                    @Override
                    public void onDismiss(int commentCount) {
                        WindowVideoPlayer.goOnPlayOnResume();
                        bindingView.tvComment.setText(commentCount+"");
                    }

                    @Override
                    public void onAddComment(ComentList.DataBean.CommentListBean newCommentData) {

                    }

                });
                FragmentManager supportFragmentManager = ((VerticalHistoryVideoPlayActivity) mContext).getSupportFragmentManager();
                WindowVideoPlayer.goOnPlayOnPause();
                fragment.show(supportFragmentManager,"comment");
            }
        }
    }

    /**
     * 显示和隐藏控制器
     */
    private boolean isHideing=false;
    private void showControllerView(boolean isShow) {
        if(isShow){
            if(bindingView.reControllerView.getVisibility()==View.VISIBLE) return;
            bindingView.reControllerView.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = AnimationUtil.moveLeftToViewLocation();
            bindingView.reControllerView.startAnimation(translateAnimation);
        }else{
            if(isHideing) return;
            if(bindingView.reControllerView.getVisibility()==View.GONE) return;
            isHideing=true;
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewRight();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isHideing=false;
                    bindingView.reControllerView.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            bindingView.reControllerView.startAnimation(translateAnimation);
        }
    }

    /**
     * 设置视频宽高
     * @param videoRatio
     * @param video_player
     */
    public static void setVideoRatio(int videoRatio, WindowVideoPlayerStandard video_player) {
        video_player.widthRatio=Constant.VIDEO_RATIO_LONF_WIDTH;
        video_player.heightRatio=Constant.VIDEO_RATIO_WIDE_WIDTH;
//        switch (videoRatio) {
//            //默认，正方形
//            case 0:
//            case 3:
//                video_player.widthRatio= Constant.VIDEO_RATIO_MOON;
//                video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
//                break;
//            //宽
//            case 1:
//                video_player.widthRatio= Constant.VIDEO_RATIO_WIDE_WIDTH;
//                video_player.heightRatio=Constant.VIDEO_RATIO_WIDE_HEIGHT;
//                break;
//            //长
//            case 2:
//                video_player.widthRatio=Constant.VIDEO_RATIO_LONF_WIDTH;
//                video_player.heightRatio=Constant.VIDEO_RATIO_WIDE_WIDTH;
//                break;
//            default:
//                video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
//                video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
//        }
    }

    /**
     * 提供给外界播放事件
     */
    public void onPlaye() {
        if(null!=mVideoBean&&null!=bindingView){
            if("2".equals(mVideoBean.getStatus())){
                bindingView.reInvalidView.setVisibility(View.VISIBLE);
                bindingView.ivInvalidView.setImageResource(R.drawable.ic_look_error_big);
                bindingView.reInvalidView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showCenterToast("您的视频审核不通过，无法播放和响应其他操作！");
                    }
                });
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
                    bindingView.videoPlayer.startVideo();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mVideoBean.setAddTime(System.currentTimeMillis());
                                ApplicationManager.getInstance().getUserPlayerDB().updatePlayerHistoryInfo(mVideoBean);
                            }catch (Exception e){
                            }
                        }
                    }.start();
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
        //还原播放器进度条和隐藏业务逻辑控制器
        if(null!=bindingView){
            bindingView.ivInvalidView.setImageResource(0);
            bindingView.reInvalidView.setOnClickListener(null);
            bindingView.reInvalidView.setVisibility(View.GONE);//处理未通过审核的布局显示
            bindingView.bottomProgress.setProgress(0);
            bindingView.bottomProgress.setSecondaryProgress(0);
            bindingView.viewTopBar.setVisibility(View.GONE);
            bindingView.viewBottomBar.setVisibility(View.GONE);
        }
    }

    /**
     * 下载视频
     */
    private void downloadVideo() {
        if(null==mContext) return;
        if(null==mVideoBean||TextUtils.isEmpty(mVideoBean.getVideoPath()))  return;
        if(!mContext.isFinishing()){
            //检查SD读写权限
            RxPermissions.getInstance(mContext).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if(null!=aBoolean&&aBoolean){
                        //用户已登录
                        if(null!=VideoApplication.getInstance().getUserData()){
                            //发布此时品的主人正式观看的用户自己
                            if(TextUtils.equals(mVideoBean.getUserId(),VideoApplication.getLoginUserID())){

                                new VideoDownloadComposrTask(mContext,mVideoBean.getVideoPath()).start();
                            }else{
                                //用户允许下载
                                if(null!=mVideoBean.getDownloadPermiss()&&TextUtils.equals("0",mVideoBean.getDownloadPermiss())){

                                    new VideoDownloadComposrTask(mContext,mVideoBean.getVideoPath()).start();
                                    //用户不允许下载
                                }else{
                                    ToastUtils.showCenterToast("发布此视频的用户未开放他人下载此视频权限！");
                                }
                            }
                            //用户未登录
                        }else{
                            if(null!=mContext&&!mContext.isFinishing()){
                                if(mContext instanceof VerticalHistoryVideoPlayActivity){
                                    ((VerticalHistoryVideoPlayActivity) mContext).login();
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
    }

    /**
     * 分享
     */
    private void onVideoShare() {
        if(null==mVideoBean){
            return;
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
        shareInfo.setDesp("新趣小视频:"+mVideoBean.getVideoDesp());
        shareInfo.setTitle("新趣小视频分享");
        shareInfo.setUrl(mVideoBean.getVideoPath());
        shareInfo.setVideoID(mVideoBean.getVideoId());
        shareInfo.setShareTitle("分享视频至");
        shareInfo.setImageLogo(mVideoBean.getVideoCover());
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                ((VerticalHistoryVideoPlayActivity) mContext).onShare(shareInfo);
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
                //点击的按钮
                if(showDialog){
                    if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                        if(null!=mContext&&!mContext.isFinishing()){
                            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                                ((VerticalHistoryVideoPlayActivity) mContext).showProgressDialog(1==mVideoBean.getIs_interest()?"取消点赞中..":"点赞中..",true);
                            }
                        }
                        mVideoDetailsPresenter.onPriseVideo(mVideoBean.getVideoId(),VideoApplication.getLoginUserID());
                    }
                    //双击的屏幕
                }else{
                    bindingView.reVideoGroup.startPriceAnimation();
                    if(mVideoBean.getIs_interest()!=1){
                        if(null!=mVideoDetailsPresenter&&!mVideoDetailsPresenter.isPriseVideo()){
                            mVideoDetailsPresenter.onPriseVideo(mVideoBean.getVideoId(),VideoApplication.getLoginUserID());
                        }
                    }
                }
            }
            //未登录
        }else{
            if(null!=mContext&&!mContext.isFinishing()){
                ToastUtils.showCenterToast("点赞需要登录账户");
                if(mContext instanceof VerticalHistoryVideoPlayActivity){
                    ((VerticalHistoryVideoPlayActivity) mContext).login();
                }
            }
        }
    }

    /**
     * 提供给外界暂停事件
     */
    public void onPause() {
        hideWorkControllerView();
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
            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                ((VerticalHistoryVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
    }

    @Override
    public void complete() {

    }

    /**
     * 加载视频留言成功回调
     */
    @Override
    public void showComentList(ComentList data) {

    }

    @Override
    public void showComentList(String videoID, ComentList data) {

    }

    /**
     * 加载视频留言为空回调
     */
    @Override
    public void showComentListEmpty(String data) {

    }

    /**
     * 加载视频留言失败回调
     */
    @Override
    public void showComentListError() {

    }

    /**
     * 评论回调
     * @param data
     */
    @Override
    public void showAddComentRelult(SingComentInfo data) {

    }

    /**
     * 点赞回调
     * @param data
     */
    @Override
    public void showPriseResult(String data) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                ((VerticalHistoryVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(1==jsonObject.getInt("code")){
                //点赞成功
                if(TextUtils.equals(Constant.PRICE_SUCCESS,jsonObject.getString("msg"))){
                    bindingView.ivPrice.setImageResource(R.drawable.iv_icon_follow_true);

                    String collectCount = mVideoBean.getVideoCommendCount();
                    int intCollectCount = Integer.parseInt(collectCount);
                    intCollectCount++;
                    bindingView.tvPrice.setText(intCollectCount+"");
                    if(null!=mVideoBean) {
                        mVideoBean.setIs_interest(1);
                        mVideoBean.setVideoCommendCount(intCollectCount+"");
                    }

                    bindingView.reVideoGroup.startPriceAnimation();
                    if(null!=mFollowScaleAnimation){
                        bindingView.ivPrice.startAnimation(mFollowScaleAnimation);
                    }
                    //取消点赞成功
                }else if(TextUtils.equals(Constant.PRICE_UNSUCCESS,jsonObject.getString("msg"))){
                    if(null!=mVideoBean) mVideoBean.setIs_interest(0);
                    bindingView.ivPrice.setImageResource(R.drawable.iv_follow_selector);

                    String collectCount = mVideoBean.getVideoCommendCount();
                    int intCollectCount = Integer.parseInt(collectCount);
                    if(intCollectCount>0){
                        intCollectCount--;
                    }
                    bindingView.tvPrice.setText(intCollectCount+"");
                }
                //点赞完成后通知持有者刷新最新的集合
                if(null!=mVideoBean){
                    VideoEventMessage videoEventMessage=new VideoEventMessage();
                    videoEventMessage.setMessage(Constant.EVENT_HISTORY_VIDEO_PLAY_PAGE_UPDATA);
                    videoEventMessage.setData(mVideoBean);
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

    /**
     * 关注用户回调
     */
    @Override
    public void showFollowUserResult(String data) {
        if(null!=mContext&&!mContext.isFinishing()){
            if(mContext instanceof VerticalHistoryVideoPlayActivity){
                ((VerticalHistoryVideoPlayActivity) mContext).closeProgressDialog();
            }
        }
        try {
            JSONObject jsonObject=new JSONObject(data);
            if(jsonObject.length()>0){
                if(1==jsonObject.getInt("code")){
                    //关注成功
                    if(TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                        if(null!=mVideoBean) mVideoBean.setIs_follow(1);
                        bindingView.tvAddFollow.setVisibility(View.GONE);
                    }else if(TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                        if(null!=mVideoBean) mVideoBean.setIs_follow(0);
                        bindingView.tvAddFollow.setVisibility((null!=VideoApplication.getInstance().getUserData()&&TextUtils.equals(VideoApplication.getLoginUserID(),mVideoBean.getUserId()))?View.GONE:View.VISIBLE);
                    }
                    //点赞完成后通知持有者刷新最新的集合
                    if(null!=mVideoBean){
                        VideoEventMessage videoEventMessage=new VideoEventMessage();
                        videoEventMessage.setMessage(Constant.EVENT_HISTORY_VIDEO_PLAY_PAGE_UPDATA);
                        videoEventMessage.setData(mVideoBean);
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

    @Override
    public void showVideoInfoResult(VideoInfo data) {

    }

    @Override
    public void showReportUserResult(String data) {

    }

    @Override
    public void showReportVideoResult(String data) {

    }

    @Override
    public void showLoadVideoInfoError() {

    }

    @Override
    public void showDeteleVideoResult(String data) {

    }

    @Override
    public void showSetVideoPrivateStateResult(String result) {

    }

    @Override
    public void showChangeVideoDownloadPermissionResult(String data) {

    }

    @Override
    public void showPostPlayCountResult(String data) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mVideoDetailsPresenter){
            mVideoDetailsPresenter.detachView();
        }
    }
}
