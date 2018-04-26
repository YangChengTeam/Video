package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidkun.xtablayout.XTabLayout;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.misc.KSYProbeMediaInfo;
import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.ksyun.media.shortvideo.sticker.DrawTextParams;
import com.ksyun.media.shortvideo.sticker.KSYStickerInfo;
import com.ksyun.media.shortvideo.sticker.KSYStickerView;
import com.ksyun.media.shortvideo.sticker.StickerHelpBoxInfo;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.ksyun.media.shortvideo.utils.ProbeMediaInfoTools;
import com.ksyun.media.shortvideo.utils.ShortVideoConstants;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.filter.audio.AudioFilterBase;
import com.ksyun.media.streamer.filter.audio.AudioReverbFilter;
import com.ksyun.media.streamer.filter.audio.KSYAudioEffectFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySoftFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySpecialEffectsFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyStylizeFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterBase;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.XinQuFragmentPagerAdapter;
import com.video.newqu.base.TopBaseActivity;
import com.video.newqu.bean.CaptionsInfo;
import com.video.newqu.bean.MediaFilterInfo;
import com.video.newqu.bean.MediaSoundFilter;
import com.video.newqu.bean.StickerListInfo;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.bean.VideoInfos;
import com.video.newqu.camera.adapter.MediaEditBeautyAdapter;
import com.video.newqu.camera.adapter.MediaEditThumbnailAdapter;
import com.video.newqu.camera.adapter.MediaRecordFilterAdapter;
import com.video.newqu.camera.adapter.MediaRecordSoundFilter;
import com.video.newqu.camera.audiorange.AudioSeekLayout;
import com.video.newqu.camera.config.ShortVideoConfig;
import com.video.newqu.camera.constant.Constants;
import com.video.newqu.camera.util.DataFactory;
import com.video.newqu.camera.videorange.HorizontalListView;
import com.video.newqu.camera.videorange.VideoRangeSeekBar;
import com.video.newqu.camera.videorange.VideoThumbnailInfo;
import com.video.newqu.camera.view.SectionSeekLayout;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemClickListener;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.OnMediaStickerListener;
import com.video.newqu.manager.ActivityCollectorManager;
import com.video.newqu.model.HorzontalSpacesItemDecoration;
import com.video.newqu.ui.contract.MediaEditContract;
import com.video.newqu.ui.dialog.InputKeyBoardDialog;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.ui.fragment.KsyAuthorizeSettingFragment;
import com.video.newqu.ui.fragment.MediaStickerUseFragment;
import com.video.newqu.ui.fragment.MediaStickerFragment;
import com.video.newqu.ui.fragment.TopicListDialogFragment;
import com.video.newqu.ui.presenter.MediaEditPresenter;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.attach.FaceConversionUtil;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.util.VideoUtils;
import com.video.newqu.util.attach.VideoComposeTask;
import com.video.newqu.view.widget.ColorPickerView;
import com.video.newqu.view.widget.EffectsButton;
import com.video.newqu.view.widget.MultiDirectionSlidingDrawer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017-08-21 10:38
 * 集成金山云的短视频编辑界面
 */

public class MediaEditActivity extends TopBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, MediaEditContract.View,OnMediaStickerListener {

    //onOutputConfirmClick 视频输出
    private  final int FILTER_DISABLE = 0;//默认的无滤镜
    //底部的六个功能区
    private  final int FAIR_LAYOUT_INDEX = 0;//美颜
    private  final int FILTER_LAYOUT_INDEX = 1;//滤镜
    private  final int CAPTION_LAYOUT_INDEX = 2;//字幕
    private  final int VIDEO_RANGE_INDEX = 3;//时长裁剪
    private  final int SOUND_LAYOUT_INDEX = 4;//音效
    private  final int STICKER_LAYOUT_INDEX = 5;//贴纸
    private  final int MUSIC_LAYOUT_INDEX = 6;//音乐编辑
    private View[] mBottomViewList;
    private int mCureentFilterPoistion=0;
    private int mFilterTypeIndex = -1;
    private  final int BEAUTY_NATURE = 101;
    private  final int BEAUTY_PRO = 102;
    private  final int BEAUTY_FLOWER_LIKE = 103;
    private  final int BEAUTY_DELICATE = 104;
    //录制，贴纸相关
    private KSYEditKit mEditKit=null; //编辑合成kit类
    private ImgBeautyProFilter mImgBeautyProFilter=null;  //美颜filter
    private int mEffectFilterIndex = FILTER_DISABLE;  //滤镜filter type
    private RelativeLayout mPreviewLayout=null;
    private GLSurfaceView mEditPreviewView=null;
    private ImageView mPauseView=null;
    //贴纸
    private KSYStickerView mKSYStickerView=null;  //贴纸预览区域（图片贴纸和字幕贴纸公用）
    private Bitmap mStickerDeleteBitmap=null;  //贴纸辅助区域的删除按钮（图片贴纸和字幕贴纸公用）
    private Bitmap mStickerRotateBitmap=null;  //贴纸辅助区域的旋转按钮（图片贴纸和字幕贴纸公用）
    private StickerHelpBoxInfo mStickerHelpBoxInfo=null;  //贴纸辅助区域的画笔（图片贴纸和字幕贴纸公用）

    private SectionSeekLayout mSectionView=null;
    private Timer mPreviewRefreshTimer=null;
    private TimerTask mPreviewRefreshTask=null;  //跟随播放预览的缩略图自动滚动任务
    //背景音乐
    private AppCompatSeekBar mOriginAudioVolumeSeekBar=null;//原音音量
    private AppCompatSeekBar mBgmVolumeSeekBar=null;//音乐音量
    private boolean mFirstPlay = true;
    private AudioSeekLayout.OnAudioSeekChecked mAudioSeekListener=null;
    private float mAudioLength;  //背景音乐时长
    private float mPreviewLength; //视频裁剪后的时长
    private AudioSeekLayout mAudioSeekLayout=null;  //音频seek布局
    private ShortVideoConfig mComposeConfig=null; //输出视频参数配置
    private  final int AUDIO_FILTER_DISABLE = 0;  //不使用音频滤镜的类型标志
    private int mAudioEffectType = AUDIO_FILTER_DISABLE;  //变声类型缓存变量
    private int mAudioReverbType = AUDIO_FILTER_DISABLE;  //混响类型缓存变量

    //变声类型数组常量,0为无变声
    private static   int[] SOUND_CHANGE_TYPE = {0,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_PITCH,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_FEMALE,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_MALE,
            KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_HEROIC, KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_ROBOT};

    //混响类型数组常量 0为无混响
    private  static int[] REVERB_TYPE = {0,AudioReverbFilter.AUDIO_REVERB_LEVEL_1, AudioReverbFilter.AUDIO_REVERB_LEVEL_2, AudioReverbFilter.AUDIO_REVERB_LEVEL_3,
            AudioReverbFilter.AUDIO_REVERB_LEVEL_4,AudioReverbFilter.AUDIO_REVERB_LEVEL_5};

    private String mLogoPath = "assets://XinQuLogo/logo.png";
    private Handler mMainHandler;
    private int mBottomViewPreIndex=-1;//默认是不显示,初始化显示1

    //for video range
    private HorizontalListView mVideoThumbnailList=null;
    private VideoRangeSeekBar mVideoRangeSeekBar=null;
    private MediaEditThumbnailAdapter mMediaEditThumbnailAdapter=null;
    private  final int LONG_VIDEO_MAX_LEN = Constant.MEDIA_VIDEO_EDIT_MAX_DURTION;//最大5分钟
    private int mMaxClipSpanMs = LONG_VIDEO_MAX_LEN;  //默认的最大裁剪时长
    private float mHLVOffsetX = 0.0f;
    private long mEditPreviewDuration;
    private TextView mVideoRangeStart,mVideoRange,mVideoRangeEnd;
    private float mLastX = 0;
    //for scale
    private int mScaleMode = KSYEditKit.SCALING_MODE_BEST_FIT;
    private int mScaleType = KSYEditKit.SCALE_TYPE_9_16;
    private float mMinCrop;
    private float mMaxCrop;
    private float mPreviewTouchStartX;
    private float mPreviewTouchStartY;
    private float mLastRawX;
    private float mLastRawY;
    private float mTouchLastX;
    private float mTouchLastY;
    private boolean mIsPreviewMoved = false;  //是否移动过了，如果移动过了，ACTION_UP时不触发bottom区域隐藏
    private int PREVIEW_TOUCH_MOVE_MARGIN = 30;  //触发移动的最小距离
    private int mScreenWidth;
    private int mScreenHeight;
    private String mVideoPath;
    private VideoInfos mVideoInfo;
    private XinQuFragmentPagerAdapter mXinQuFragmentPagerAdapter=null;//贴纸适配器
    private MediaRecordSoundFilter mMediaEditSoundReverAdapter=null;//混响适配器
    private MediaRecordSoundFilter mMediaEditSoundFilter=null;//变声适配器
    private ViewPager mSticker_tab_viewpager=null;
    //贴纸的
    private ImageView btnLoading;
    private AnimationDrawable drawableAnimation;
    private List<StickerListInfo.DataBean> mStickerListInfoList=null;//贴纸的所有分类
    private TextView mTv_color_view=null;
    private ImageView mIv_thbum_icon=null;
    private ProbeMediaInfoTools mImageSeekTools=null;
    private boolean mH265File=false;
    private int mSourceType;
    private String mMusicID="0";
    private String mMusicPath="";
    private MediaRecordFilterAdapter mMediaRecordFilterAdapter=null;//滤镜
    private MediaEditBeautyAdapter mMediaEditBeautyAdapter=null;//美颜
    private int mCureentBuayePoistion=0;//选中的美颜效果
    private View mDefaultRecordBottomLayout=null;//默认的底部的菜单栏
    private View mPreRecordConfigLayout=null;//当前正在显示的
    private EffectsButton mBtn_fair=null;
    private ImageView mIv_video_cover=null;
    private long mThbumSseekTime=1;//封面的位置
    private TextView mVideoDespContent;
    private TextView mTv_num;
    private LoadingProgressView mLoadingProgressedView=null;//进度狂
    private float fps=0F;
    private boolean isPrivate=false;
    private boolean isDownloadPermiss=true;
    private boolean obiectIsRecyler=true;//结束Activity时候是否需要回收所有的对象，关乎后台合成视频
//    private RelativeLayout mBottom_bar;
    private  final int VIDEO_DESP_CHARCOUNT=100;
    private ImageView mIc_media_handle;
    private boolean firstAddTextSticker=true;
    private boolean isScreenThubm=false;//是否正在截图
    private WeakReference<MediaEditPresenter> mMediaEditPresenterWeakReference;
    private TextView mTipsTextView=null;//第一次使用功能提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollectorManager.addActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_media_edit);
        //默认设置为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initIntent();
        mScreenWidth =ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();
        mStickerListInfoList=new ArrayList<>();
        initPresenter();
        //初始化所有基础UI
        initViews();
        //onWaterMarkLogoClick(false);//默认显示右上角水印
        //开启预览
        startEditPreview();
        setVideoCover();
    }

    private void initPresenter() {
        MediaEditPresenter mediaEditPresenter = new MediaEditPresenter(MediaEditActivity.this);
        mMediaEditPresenterWeakReference = new WeakReference<MediaEditPresenter>(mediaEditPresenter);
        mMediaEditPresenterWeakReference.get().attachView(this);
    }


    /**
     * 初始化所有View
     */
    private void initViews() {
        mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        mEditPreviewView = (GLSurfaceView) findViewById(R.id.edit_preview);
        mKSYStickerView = (KSYStickerView) findViewById(R.id.sticker_panel);//贴纸
        mIv_thbum_icon = (ImageView) findViewById(R.id.iv_thbum_icon);//截图预览
        //接受全局正在显示的View,默认底部编辑器View
        mDefaultRecordBottomLayout = findViewById(R.id.media_edit_send_view);
        mPreRecordConfigLayout = mDefaultRecordBottomLayout;
        //测量抽屉高度
        MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.sliding_drawer);
        LinearLayout content = (LinearLayout) findViewById(R.id.content);
        RelativeLayout handle = (RelativeLayout) findViewById(R.id.handle);
        mIc_media_handle = (ImageView) findViewById(R.id.ic_media_handle);
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        content.measure(width,height);
        handle.measure(width,height);
        slidingDrawer.getLayoutParams().height=(content.getMeasuredHeight()+ handle.getMeasuredHeight());
        slidingDrawer.setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                if(null!=mIc_media_handle) mIc_media_handle.setImageResource(R.drawable.ic_media_down);
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                if(null!=mIc_media_handle) mIc_media_handle.setImageResource(R.drawable.ic_media_up);
            }
        });
        //TOP
        EffectsButton btn_close = (EffectsButton) findViewById(R.id.btn_close);
        mBtn_fair = (EffectsButton) findViewById(R.id.btn_fair);

        EffectsButton btn_captions = (EffectsButton) findViewById(R.id.btn_captions);
        EffectsButton btn_cat = (EffectsButton) findViewById(R.id.btn_cat);
        EffectsButton btn_sound = (EffectsButton) findViewById(R.id.btn_sound);
        //RIGHT
        EffectsButton btn_music = (EffectsButton) findViewById(R.id.btn_music);
        EffectsButton btn_sticker = (EffectsButton) findViewById(R.id.btn_sticker);
        EffectsButton btn_filter = (EffectsButton) findViewById(R.id.btn_filter);

        EffectsButton.OnClickEffectButtonListener onButtonClickListener=new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton(EffectsButton view) {
                int poistion=0;
                switch (view.getId()) {
                    //美颜
                    case R.id.btn_fair:
                        poistion=0;
                        break;
                    //滤镜
                    case R.id.btn_filter:
                        poistion=1;
                        break;
                    //字幕
                    case R.id.btn_captions:
                        poistion=2;
                        break;
                    //裁剪
                    case R.id.btn_cat:
                        poistion=3;
                        break;
                    //音效
                    case R.id.btn_sound:
                        poistion=4;
                        break;
                    //贴纸
                    case R.id.btn_sticker:
                        poistion=5;
                        break;
                    //音乐
                    case R.id.btn_music:
                        if(null!=mTipsTextView&&mTipsTextView.getVisibility()!=View.GONE){
                            mTipsTextView.setVisibility(View.GONE);
                            mTipsTextView=null;
                        }
                        poistion=6;
                        break;
                }
                showFunctionUI(poistion);
            }
        };

        ((TextView) findViewById(R.id.tv_fair_tips)).setText("滑动选择美颜类型");
        mBtn_fair.setOnClickEffectButtonListener(onButtonClickListener);
        btn_filter.setOnClickEffectButtonListener(onButtonClickListener);
        btn_captions.setOnClickEffectButtonListener(onButtonClickListener);
        btn_cat.setOnClickEffectButtonListener(onButtonClickListener);
        btn_sound.setOnClickEffectButtonListener(onButtonClickListener);
        btn_music.setOnClickEffectButtonListener(onButtonClickListener);
        btn_sticker.setOnClickEffectButtonListener(onButtonClickListener);
        //所有的功能View
        mBottomViewList=new View[7];
        //美颜
        View fairLayout = findViewById(R.id.record_fair_layout);
        mBottomViewList[FAIR_LAYOUT_INDEX] = fairLayout;
        //滤镜
        View filterLayout = findViewById(R.id.media_edit_filter_choose);
        mBottomViewList[FILTER_LAYOUT_INDEX] = filterLayout;
        //字幕
        View captionsLayout = findViewById(R.id.media_edit_captions_choose);
        mBottomViewList[CAPTION_LAYOUT_INDEX] = captionsLayout;
        //时长裁剪
        View videoRangeLayout = findViewById(R.id.media_edit_video_range_choose);
        mBottomViewList[VIDEO_RANGE_INDEX] = videoRangeLayout;
        //音效
        View musicEditLayout = findViewById(R.id.media_sound_layout);
        mBottomViewList[SOUND_LAYOUT_INDEX] = musicEditLayout;
        //贴纸
        View stickerLayout = findViewById(R.id.media_edit_sticker_choose);
        mBottomViewList[STICKER_LAYOUT_INDEX] = stickerLayout;
        //音乐编辑区域
        View media_audio_seek_layout = findViewById(R.id.media_audio_seek_layout);
        mBottomViewList[MUSIC_LAYOUT_INDEX] = media_audio_seek_layout;

        //音乐编辑监听
        mAudioSeekLayout = (AudioSeekLayout) findViewById(R.id.audioSeekLayout);
        mAudioSeekLayout.setOnClickViewListener(new AudioSeekLayout.OnClickViewListener() {
            //请除音乐
            @Override
            public void onCanelMusic() {
                if(null!=mEditKit){
                    if(null!=mEditKit) mEditKit.stopBgm();
                    cleanMusic();
                    setEnableBgmEdit(false);
                    cleafunctionUI();
                }
            }
            //关闭编辑器
            @Override
            public void onCloseAudioView() {
                cleafunctionUI();
            }
        });
        //点击了工区模块的关闭按钮
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_record_filter_close:
                    case R.id.btn_record_fair_close:
                    case R.id.btn_record_sound_close:
                    case R.id.btn_media_cat_close:
                    case R.id.btn_media_sticker_close:
                    case R.id.btn_media_captions_close:
                        cleafunctionUI();
                        break;
                }
            }
        };
        ((ImageView) findViewById(R.id.btn_record_fair_close)).setOnClickListener(onClickListener);
        ((ImageView) findViewById(R.id.btn_record_filter_close)).setOnClickListener(onClickListener);
        ((ImageView) findViewById(R.id.btn_record_sound_close)).setOnClickListener(onClickListener);
        ((ImageView) findViewById(R.id.btn_media_sticker_close)).setOnClickListener(onClickListener);
        ((ImageView) findViewById(R.id.btn_media_cat_close)).setOnClickListener(onClickListener);
        ((ImageView) findViewById(R.id.btn_media_captions_close)).setOnClickListener(onClickListener);
        View.OnClickListener onInputEditClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //暂停
                    case R.id.btn_pause:
                        onPauseClick();
                        break;
//                    //添加表情
//                    case R.id.btn_tv_addface:
//                        showInputKeyBoardDialog(false,true);
//                        break;
                    //打开文字输入面板
                    case R.id.tv_video_desp_content:
                        showInputKeyBoardDialog(true,false);
                        break;
                    //是否私有
                    case R.id.ll_private:
                        isPrivate();
                        break;
                    //是否允许他人下载
                    case R.id.ll_download_permiss:
                        isDownloadPermission();
                        break;
                    //保存至相册
                    case R.id.btn_save:
                        checkedLocationVideFile();
                        break;
                    //发布
                    case R.id.btn_send:
                        onNextClick();
                        break;
                    //选择封面
                    case R.id.iv_video_cover:
                        Intent intent=new Intent(MediaEditActivity.this,MediaScreenShortActivity.class);
                        intent.putExtra("compose_path",mVideoPath);
                        intent.putExtra("preview_length",mPreviewLength);
                        startActivityForResult(intent,Constant.MEDIA_START_COVER_REQUEST_CODE);
                        break;
                    //请空选中的贴纸
                    case R.id.btn_empty_sticker:
                        if(null!=mKSYStickerView)  mKSYStickerView.removeBitmapStickers();
                        break;
                }
            }
        };
        //关闭
        btn_close.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton(EffectsButton view) {
                onBackoffClick();
            }
        });
        TextView btnTvAddtopic = (TextView) findViewById(R.id.btn_tv_addtopic);
        btnTvAddtopic.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                addTopic();
            }
        });

        mVideoDespContent = (TextView) findViewById(R.id.tv_video_desp_content);
        ((LinearLayout) findViewById(R.id.ll_download_permiss)).setOnClickListener(onInputEditClickListener);
        mVideoDespContent.setOnClickListener(onInputEditClickListener);

        ((TextView) findViewById(R.id.btn_save)).setOnClickListener(onInputEditClickListener);
        ((TextView) findViewById(R.id.btn_send)).setOnClickListener(onInputEditClickListener);
        ((ImageView) findViewById(R.id.btn_empty_sticker)).setOnClickListener(onInputEditClickListener);

        LinearLayout ll_private = (LinearLayout) findViewById(R.id.ll_private);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ll_private.getLayoutParams();
        layoutParams.height= btnTvAddtopic.getLayoutParams().height;
        ll_private.setLayoutParams(layoutParams);
        ll_private.setOnClickListener(onInputEditClickListener);

        mTv_num = (TextView) findViewById(R.id.tv_num);
        mTv_num.setText(VIDEO_DESP_CHARCOUNT+"");
        mIv_video_cover = (ImageView) findViewById(R.id.iv_video_cover);
        mIv_video_cover.setOnClickListener(onInputEditClickListener);
        mPauseView = (ImageView) findViewById(R.id.btn_pause);
        mPauseView.setOnClickListener(onInputEditClickListener);
        mPauseView.getDrawable().setLevel(2);

        mVideoDespContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTv_num.setText(VIDEO_DESP_CHARCOUNT- s.length()+"");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //片段编辑
        mSectionView = (SectionSeekLayout) findViewById(R.id.session_layout);
        mSectionView.setThumbWidth(ScreenUtils.getScreenWidth()>=1280?105:60);
        mMainHandler = new Handler();
        mEditKit = new KSYEditKit(MediaEditActivity.this);
        mEditKit.setDisplayPreview(mEditPreviewView);
        mEditKit.setOnInfoListener(mOnInfoListener);
        //添加贴纸View到SDK
        mEditKit.addStickerView(mKSYStickerView);
        mEditKit.getAudioPlayerCapture().setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

                mAudioLength = iMediaPlayer.getDuration();
                mAudioSeekListener = new AudioSeekLayout.OnAudioSeekChecked() {
                    @Override
                    public void onActionUp(long start, long end) {
                        mEditKit.setBGMRanges(start, end, true);
                        //每次选中音乐的片段位置发生了变化，从头开始预览视频
                        changePlayButton(false);
                        mEditKit.seekTo(0);//seek到起始位置重新播放
                        changePlayButton(true);
                    }
                };
                mAudioSeekLayout.setOnAudioSeekCheckedListener(mAudioSeekListener);
                if (mFirstPlay) {
                    mFirstPlay = false;
                    mAudioSeekLayout.updateAudioSeekUI(mAudioLength, mPreviewLength);
                }
            }
        });

        mEditPreviewView.setOnTouchListener(mPreviewViewTouchListener);
        RelativeLayout.LayoutParams coverLayoutParams = (RelativeLayout.LayoutParams) mIv_thbum_icon.getLayoutParams();
        coverLayoutParams.height=mEditPreviewView.getLayoutParams().height;
        coverLayoutParams.width=mEditPreviewView.getLayoutParams().width;
        mIv_thbum_icon.setLayoutParams(coverLayoutParams);
        //片段信息变更回调
        mSectionView.setOnSectionSeekListener(new SectionSeekLayout.OnSectionSeekListener() {
            @Override
            public void onRangeChanged(int index, long start, long end) {
                // 更新贴纸显示时间区间
                mKSYStickerView.updateStickerInfo(index, start, end - start);
            }

            @Override
            public void removeSticker(int id) {
                // 删除贴纸
                mKSYStickerView.removeSticker(id);
            }

            @Override
            public void onPausePreview() {
                onPauseClick();
            }

            @Override
            public void onSeekTo(long time) {
                mEditKit.seekTo(time);
                mEditKit.updateStickerDraw();
                mEditKit.pausePlay(false);
                startPreviewTimerTask();
            }
        });
        //贴纸信息变更回调
        mKSYStickerView.setOnStickerSelected(new KSYStickerView.OnStickerStateChanged() {
            /**
             * 某一个贴纸被选择
             * @param index  被选择的贴纸的index
             * @param text  被选择的贴纸的text信息，若为非null说明为字幕贴纸
             */
            @Override
            public void selected(int index, String text) {
                //重新选择某一个区间
                //进入贴纸编辑状态，需要先暂停预览播放
                pausePreview();
                if(null!=mSectionView) mSectionView.startSeek(index);
                if(!TextUtils.isEmpty(text)){
                    if(TextUtils.equals("点击修改",text)){
                        showCaptionsTextInputView(true,false,"");
                    }else{
                        showCaptionsTextInputView(true,false,text);
                    }
                }
            }
            /**
             * 某一个贴纸被删除
             * @param list 被删除的贴纸的index集合
             * @param text 删除贴纸的text信息，若为非null说明为当前编辑贴纸并且是字幕贴纸
             */
            @Override
            public void deleted(List<Integer> list, String text) {
                //带字幕的当前贴纸
                if(null!=mSectionView) mSectionView.delete(list);
                mKSYStickerView.setCurrentText("");
            }
        });
        SeekBar.OnSeekBarChangeListener onSeekBarChangelistener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                float val = progress / 100.f;
                switch (seekBar.getId()) {
                    case R.id.record_mic_audio_volume:
                        if(null!=mEditKit) mEditKit.setOriginAudioVolume(val);
                        break;
                    case R.id.record_music_audio_volume:
                        if(null!=mEditKit) mEditKit.setBgmVolume(val);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        //配乐音量大小的进度控制
        mBgmVolumeSeekBar = (AppCompatSeekBar) findViewById(R.id.record_music_audio_volume);
        mBgmVolumeSeekBar.setOnSeekBarChangeListener(onSeekBarChangelistener);
        //音乐音量控制器
        mOriginAudioVolumeSeekBar = (AppCompatSeekBar) findViewById(R.id.record_mic_audio_volume);
        mOriginAudioVolumeSeekBar.setOnSeekBarChangeListener(onSeekBarChangelistener);
        mOriginAudioVolumeSeekBar.setProgress((int) mEditKit.getOriginAudioVolume() * 100);
        mBgmVolumeSeekBar.setProgress((int) mEditKit.getBgmVolume() * 100);
        slidingDrawer.animateOpen();//默认开启抽屉
        //第一次使用弹出使用提示
        if(1!= SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_MEDIA_EDIT_CODE)){
            mTipsTextView = (TextView) findViewById(R.id.tv_music_tips_message);
            mTipsTextView.setText(getResources().getString(R.string.tips_first_record_msg));
            mTipsTextView.setVisibility(View.VISIBLE);
            mTipsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null!=mTipsTextView){
                        mTipsTextView.setVisibility(View.GONE);
                    }
                }
            });
            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_MEDIA_EDIT_CODE,1);
        }
    }

    /**
     * 显示文字输入面板
     */
    private void showCaptionsTextInputView(boolean showKeyboard, boolean showFaceBoard, String text) {
        final InputKeyBoardDialog inputKeyBoardDialog = new InputKeyBoardDialog(MediaEditActivity.this);
        inputKeyBoardDialog.setInputText(text);
        inputKeyBoardDialog.setParams(showKeyboard,showFaceBoard);
        inputKeyBoardDialog.setSubmitText("确定");
        inputKeyBoardDialog.setHintText("请输入字幕内容");
        inputKeyBoardDialog.setMaxTextCount(19);
        inputKeyBoardDialog.setBackgroundWindown(0.1f);
        inputKeyBoardDialog.hideFaceBtn();
        inputKeyBoardDialog.setIndexOutErrorText("字幕文字长度超过限制");
        inputKeyBoardDialog.setOnKeyBoardChangeListener(new InputKeyBoardDialog.OnKeyBoardChangeListener() {
            //文字发生了变化
            @Override
            public void onChangeText(String inputText) {
                if(null!=mKSYStickerView) mKSYStickerView.setCurrentText(inputText);
            }
            @Override
            public void onSubmit() {

            }
        });
        inputKeyBoardDialog.show();
    }

    /**
     * 开始预览视频
     */
    private void startEditPreview() {
        if(TextUtils.isEmpty(mVideoPath)) return;
        mEditKit.setEditPreviewUrl(mVideoPath);
        mEditKit.setOriginAudioVolume(1.0f);
        //设置是否循环预览
        mEditKit.setLooping(true);
        //开启预览
        try {
            mEditKit.startEditPreview();
            //设置预览的原始音频的音量
            if(!TextUtils.isEmpty(mMusicPath)){
                mEditKit.startBgm(mMusicPath, true);
                setEnableBgmEdit(true);
            }else{
                setEnableBgmEdit(false);
            }
            changePlayButton(true);
        }catch (Exception e){
        }
        if(null!=mOriginAudioVolumeSeekBar){
            mOriginAudioVolumeSeekBar.setProgress((int) (mEditKit.getOriginAudioVolume() * 100));
        }
    }

    /**
     * 打开输入法键盘
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     */
    private void showInputKeyBoardDialog(boolean showKeyboard,boolean showFaceBoard) {
        final InputKeyBoardDialog inputKeyBoardDialog = new InputKeyBoardDialog(MediaEditActivity.this);
        inputKeyBoardDialog.setInputText(mVideoDespContent.getText().toString());
        inputKeyBoardDialog.setParams(showKeyboard,showFaceBoard);
        inputKeyBoardDialog.setSubmitText("确定");
        inputKeyBoardDialog.setHintText("请输入视频文字介绍");
        inputKeyBoardDialog.setMaxTextCount(VIDEO_DESP_CHARCOUNT);
        inputKeyBoardDialog.setBackgroundWindown(0.1f);
        inputKeyBoardDialog.setIndexOutErrorText("描述文字长度超过限制");
        inputKeyBoardDialog.setOnKeyBoardChangeListener(new InputKeyBoardDialog.OnKeyBoardChangeListener() {
            //文字发生了变化
            @Override
            public void onChangeText(String inputText) {
                if(null!=mVideoDespContent){
                    if(!TextUtils.isEmpty(inputText)){
                        SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style), mVideoDespContent,null,null);
                        mVideoDespContent.setText(topicStyleContent);
                    }else{
                        mVideoDespContent.setText(inputText);
                    }
                }
            }

            //提交
            @Override
            public void onSubmit() {

            }
        });
        inputKeyBoardDialog.show();
    }

    /**
     * 是否选中，播放动画
     */
    private void isPrivate() {
        isPrivate=!isPrivate;
        ImageView iv_is_private = (ImageView) findViewById(R.id.iv_is_private);
        if(isPrivate){
            iv_is_private.setImageResource(R.drawable.media_edit_isprivate_true);
        }else{
            iv_is_private.setImageResource(R.drawable.media_edit_isprivate_false);
        }
        AnimationDrawable drawable = (AnimationDrawable) iv_is_private.getDrawable();
        drawable.setOneShot(true);
        drawable.start();
    }


    /**
     * 是否允许别人下载，播放动画
     */
    private void isDownloadPermission() {
        isDownloadPermiss=!isDownloadPermiss;
        ImageView iv_download_permiss = (ImageView) findViewById(R.id.iv_download_permiss);
        if(isDownloadPermiss){
            iv_download_permiss.setImageResource(R.drawable.media_edit_isprivate_true);
        }else{
            iv_download_permiss.setImageResource(R.drawable.media_edit_isprivate_false);
        }
        AnimationDrawable drawable = (AnimationDrawable) iv_download_permiss.getDrawable();
        drawable.setOneShot(true);
        drawable.start();
    }

    private void cleanMusic() {
        mMusicID="";
        mMusicPath="";
    }

    /**
     * 添加话题
     */
    private void addTopic() {
        if(null== mVideoDespContent) return;
        String content = mVideoDespContent.getText().toString();
        if(content.length()>=VIDEO_DESP_CHARCOUNT){
            ToastUtils.showCenterToast("字数已超过限制");
            return;
        }
        int count = Utils.exceedTopicCount(content);
        if(count>=3){
            ToastUtils.showCenterToast("话题数量超过限制");
            return;
        }
        pause();
        TopicListDialogFragment.getInstance(3-count).setOnDismissListener(new TopicListDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(List<String> topics) {
                resume();
                setResultTopic(topics);
            }
        }).show(getSupportFragmentManager(),"topic");
    }



    /**
     * 直接复制到另一个目录下保存
     */
    private void checkedLocationVideFile() {

        ConfigSet.getInstance().setSaveVideo(true);
        showProgressDialog("保存至相册中，请稍后..",true);
        try{
            String desDir = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ?
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                            + "/XinQu/Camera/" : Environment.getExternalStorageDirectory().getAbsolutePath() + "/XinQu/Camera/";
            File file = new File(desDir);
            if(!file.exists()){
                file.mkdirs();
            }
            String name = mVideoPath.substring(mVideoPath.lastIndexOf('/'));
            String desPath = desDir + name;
            File desFile = new File(desPath);
            try {
                File srcFile = new File(mVideoPath);
                if (srcFile.exists() && !desFile.exists()) {
                    InputStream is = new FileInputStream(mVideoPath);
                    FileOutputStream fos = new FileOutputStream(desFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                    is.close();
                    fos.close();
                    closeProgressDialog();
                    ToastUtils.showCenterToast("保存到相册成功，位置:"+desPath);
                    // 发送系统广播通知刷新
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(desFile);
                    intent.setData(uri);
                    sendBroadcast(intent);

                }else{
                    closeProgressDialog();
                    ToastUtils.showCenterToast("已经保存至本地相册"+desPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                closeProgressDialog();
                ToastUtils.showCenterToast("保存至相册失败"+e.getMessage());
            }
        }catch (Exception e){
            closeProgressDialog();
            ToastUtils.showCenterToast("保存至相册失败"+e.getMessage());
        }
    }

    /**
     * 显示功能View UI
     * @param poistion
     */
    private void showFunctionUI(int poistion) {
        if(mBottomViewPreIndex==poistion) return;
        if(null!=mPreRecordConfigLayout&&mPreRecordConfigLayout.getVisibility()!=View.GONE){
            mPreRecordConfigLayout.setVisibility(View.GONE);
        }
        if(null!=mBottomViewList&&mBottomViewList.length>0){
            showViewFromBottomToTop(mBottomViewList[poistion]);
        }
        mPreRecordConfigLayout=mBottomViewList[poistion];
        mBottomViewPreIndex = poistion;

        //音乐
        if(poistion==MUSIC_LAYOUT_INDEX){
            //若还未去选择音乐，去选择音乐
            if(TextUtils.isEmpty(mMusicPath)){
                Intent intent=new Intent(MediaEditActivity.this,MediaMusicActivity.class);
                startActivityForResult(intent,Constant.MEDIA_START_MUSIC_REQUEST_CODE);
                overridePendingTransition(R.anim.menu_enter, 0);//进场动画
            }
            return;
        }

        //初始化美颜
        if(poistion==FAIR_LAYOUT_INDEX&&null==mMediaEditBeautyAdapter){
            initFairView();
            return;
        }

        //选中了滤镜
        if(poistion==FILTER_LAYOUT_INDEX&&null==mMediaRecordFilterAdapter){
            //初始化滤镜适配器
            initFilterUI();
            return;
        }
        //选中了贴纸
        if (poistion == STICKER_LAYOUT_INDEX ) {
            if (mPauseView.getDrawable().getLevel() == 2) {
                onPauseClick();
            }
            if(null==mXinQuFragmentPagerAdapter){
                initStickerView();
            }
            return;
        }

        //选中了字幕
        if(poistion==CAPTION_LAYOUT_INDEX){
            //暂停播放先
            if (mPauseView.getDrawable().getLevel() == 2) {
                onPauseClick();
            }
            //初始化字幕
            if(null==mTv_color_view){
                initCoptions();
            }
            return;
        }

        //初始化音效功能区
        if(poistion==SOUND_LAYOUT_INDEX&&null==mContentViews&&null==mMediaEditSoundFilter){
            initSoundInitUI();
            return;
        }

        //初始化裁剪
        if(poistion==VIDEO_RANGE_INDEX&&null==mVideoRangeSeekBar){
            initVideoRange();
            initSeekBar();
            initThumbnailAdapter();
            return;
        }
    }


    private void initIntent() {
        Bundle bundle = getIntent().getExtras();
        mSourceType = bundle.getInt(Constant.KEY_MEDIA_RECORD_PRAMER_SOURCETYPE, 0);
        mVideoPath = bundle.getString(Constant.KEY_MEDIA_RECORD_PRAMER_VIDEO_PATH);
        //类型：1：分享视频上传 2：选择本地视频上传  3：拍摄视频上传
        if(3==mSourceType){
            mMusicID = bundle.getString(Constant.KEY_MEDIA_KEY_MUSIC_ID);
            mMusicPath = bundle.getString(Constant.KEY_MEDIA_KEY_MUSIC_PATH);
            fps=bundle.getFloat(Constant.KEY_MEDIA_KEY_FPS,0F);
        }
        if(TextUtils.isEmpty(mVideoPath)){
            ToastUtils.showCenterToast("传入路径有误!");
            finish();
        }
    }


    private View.OnTouchListener mPreviewViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //获取相对屏幕的坐标，即以屏幕左上角为原点
            mLastRawX = event.getRawX();
            mLastRawY = event.getRawY();
            // 预览区域的大小
            RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) mPreviewLayout
                    .getLayoutParams();
            int previewHeight = previewParams.height;
            int previewWidth = previewParams.width;

            //预览的crop信息
            int left = (int) (previewParams.leftMargin - mEditKit.getPreviewCropRect().left * previewWidth);
            int right = previewWidth;
            int top = (int) (previewParams.topMargin -
                    mEditKit.getPreviewCropRect().top * previewHeight);
            int bottom = previewHeight;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isPreviewScreenArea(event.getX(), event.getY(), left, right, top, bottom,
                            false, true)) {
                        //获取相对preview区域的坐标，即以preview左上角为原点
                        mPreviewTouchStartX = event.getX() - left;
                        mPreviewTouchStartY = event.getY() - top;
                        mTouchLastX = event.getX();
                        mTouchLastY = event.getY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveX = (int) Math.abs(event.getX() - mTouchLastX);
                    int moveY = (int) Math.abs(event.getY() - mTouchLastY);
                    if (mPreviewTouchStartX > 0 && mPreviewTouchStartY > 0 && ((moveX >
                            PREVIEW_TOUCH_MOVE_MARGIN) ||
                            (moveY > PREVIEW_TOUCH_MOVE_MARGIN))) {
                        //触发移动
                        mIsPreviewMoved = true;
                        updatePreviewView();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //未移动
                    if (!mIsPreviewMoved) {
                        updateBottomVideible((int) mLastRawX, (int) mLastRawY);
                        //对编辑状态的贴纸生效
                        if(null!=mSectionView) mSectionView.calculateRange();
                    }

                    mIsPreviewMoved = false;
                    mPreviewTouchStartX = 0f;
                    mPreviewTouchStartY = 0f;
                    mTouchLastX = 0f;
                    mTouchLastY = 0f;
                    break;
                default:
                    break;
            }
            return true;
        }
    };


    /**
     * 是否在小窗区域移动
     *
     * @param x      当前点击的相对屏幕左上角的x坐标
     * @param y      当前点击的相对屏幕左上角的y坐标
     * @param left   预览左上角距离屏幕区域左上角的x轴距离
     * @param right  预览右上角距离屏幕区域左上角的x轴距离
     * @param top    预览左上角距离屏幕区域左上角的y轴距离
     * @param bottom 预览右上角距离屏幕区域左上角的y轴距离
     * @return
     */
    private boolean isPreviewScreenArea(float x, float y, int left, int right, int top, int
            bottom, boolean enableX, boolean enableY) {
        if (enableX && enableY) {
            if (x > left && x < right &&
                    y > top && y < bottom) {
                return true;
            }
        } else if (enableX) {
            if (x > left && x < right) {
                return true;
            }
        } else if (enableY) {
            if (y > top && y < bottom) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据手指滑动的距离对预览区域进行裁剪显示
     */
    public void updatePreviewView() {
        //裁剪模式下，并且有多余的区域需要裁剪才进行裁剪，否则不尽兴
        if (mEditKit.getCropScale() <= 0 || mScaleMode != KSYEditKit.SCALING_MODE_CROP) {
            return;
        }
        RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) mPreviewLayout
                .getLayoutParams();
        int previewHeight = previewParams.height;
        int previewWidth = previewParams.width;
        //只有裁剪模式才需要更新预览的显示区域

        if (!mEditKit.getIsLandscape()) {
            //竖屏模式，上下裁剪，
            //更新窗口位置参数
            float newY = (mLastRawY - mPreviewTouchStartY);
            mMaxCrop = previewParams.topMargin;
            mMinCrop = 0.f - mEditKit.getCropScale() * (float) previewHeight + previewParams
                    .topMargin;
            //不能超出可裁剪范围
            if (newY > mMaxCrop) {
                newY = mMaxCrop;
            }

            if (newY < mMinCrop) {
                newY = mMinCrop;
            }

            float top = newY / previewHeight;
            mEditKit.setPreviewCrop(0.f, 0.f - top, 1.f, 0.f);
        } else {
            //横屏模式左右裁剪
            float newX = (mLastRawX - mPreviewTouchStartX);
            mMaxCrop = 0.f;
            mMinCrop = 0.f - mEditKit.getCropScale() * (float) previewWidth + previewParams
                    .leftMargin;
            //不能超出可裁剪范围
            if (newX > mMaxCrop) {
                newX = mMaxCrop;
            }

            if (newX < mMinCrop) {
                newX = mMinCrop;
            }

            float left = newX / previewWidth;
            mEditKit.setPreviewCrop(0.f - left, 0.f, 0.f, 1.f);
        }
    }

    public void resizePreview(int type, int mode) {
        mScaleType = type;
        mScaleMode = mode;

        mEditKit.setScaleType(type);
        mEditKit.setScalingMode(mScaleMode);

        //默认全屏显示预览
        int previewWidth = mScreenWidth;
        int previewHeight = mScreenHeight;

        //根据不同比例来更新预览区域大小
        //to 3:4
        if (mScaleType == KSYEditKit.SCALE_TYPE_3_4) {
            previewHeight = previewWidth * 4 / 3;
        }
        //to 1:1
        if (mScaleType == KSYEditKit.SCALE_TYPE_1_1) {
            previewHeight = previewWidth;
        }

        RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) mPreviewLayout
                .getLayoutParams();
        previewParams.height = previewHeight;
        previewParams.width = previewWidth;
        mPreviewLayout.setLayoutParams(previewParams);
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if(null!=mEditKit) mEditKit.onResume();
        if(null!=mEditKit&&mPauseView.getDrawable().getLevel() == 1){
            mEditKit.pausePlay(false);
            changePlayButton(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if(null!=mEditKit) mEditKit.onPause();
        if(null!=mEditKit&&mPauseView.getDrawable().getLevel() == 2){
            mEditKit.pausePlay(true);
            changePlayButton(false);
        }
    }

    private void resume(){
        if(null!=mEditKit) mEditKit.onResume();
        if(null!=mEditKit&&mPauseView.getDrawable().getLevel() == 1){
            mEditKit.pausePlay(false);
            changePlayButton(true);
        }
    }


    private void pause(){
        MobclickAgent.onPause(this);
        if(null!=mEditKit) mEditKit.onPause();
        if(null!=mEditKit&&mPauseView.getDrawable().getLevel() == 2){
            mEditKit.pausePlay(true);
            changePlayButton(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mMainHandler) mMainHandler.removeMessages(0);
        if(null!=mMediaEditPresenterWeakReference&&null!=mMediaEditPresenterWeakReference.get()){
            mMediaEditPresenterWeakReference.get().detachView();
        }
        changePlayButton(false);
        cleanMusic();
        stopPreviewTimerTask();
        Utils.deleteAllFiles(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/XinQu/Camera"));
        if(null!=mKSYStickerView) mKSYStickerView.setOnStickerSelected(null);
        mVideoInfo=null;
        clearImgFilter();
        if(obiectIsRecyler){
            if(null!=mEditKit){
                mEditKit.stopEditPreview();
                mEditKit.release();
            }
        }
        if(null!=drawableAnimation) drawableAnimation.start();
        drawableAnimation=null;
        ActivityCollectorManager.removeActivity(this);
        Runtime.getRuntime().gc();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onBackoffClick();  //覆盖系统返回键进行个性化处理
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                updateBottomVideible(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateBottomVideible(int x, int y) {
        cleafunctionUI();
    }

    private void cleafunctionUI() {
        if(null!=mPreRecordConfigLayout&&mPreRecordConfigLayout.getVisibility()==View.VISIBLE&&mPreRecordConfigLayout!=mDefaultRecordBottomLayout&&-1!=mBottomViewPreIndex){
            mPreRecordConfigLayout.setVisibility(View.GONE);
            mPreRecordConfigLayout=mDefaultRecordBottomLayout;
            showViewFromBottomToTop(mDefaultRecordBottomLayout);
            mBottomViewPreIndex=-1;
        }
    }


    /**
     * 登录
     */
    public void login() {
        Intent intent = new Intent(MediaEditActivity.this, LoginGroupActivity.class);
        startActivityForResult(intent, Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }
    /**
     * 选中本地背景音乐后返回结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //选择音乐结果，不管有没有选中音乐回来
        if(requestCode==Constant.MEDIA_START_MUSIC_REQUEST_CODE){
            //选中了音乐
            if(Constant.MEDIA_START_MUSIC_RESULT_CODE==resultCode){
                if(null!=data){
                    mMusicID = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID);
                    mMusicPath = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH);
                    if(!TextUtils.isEmpty(mMusicPath)){
                        if(Utils.isFileToMp3(mMusicPath)){
                            try {
                                if(null!=mEditKit){
                                    //每次选中音乐的片段位置发生了变化，从头开始预览视频
                                    mEditKit.startBgm(mMusicPath, true);
                                    mEditKit.seekTo(0);//seek到起始位置重新播放
                                    setEnableBgmEdit(true);
                                }
                            }catch (Exception e){

                            }
                        }else{
                            ToastUtils.showCenterToast("音乐地址错误或音乐格式不受支持！");
                            if(-1!=mBottomViewPreIndex){
                                cleafunctionUI();
                                return;
                            }
                        }
                    }
                }
                //未选中音乐
            }else{
                if(-1!=mBottomViewPreIndex){
                    cleafunctionUI();
                }
                return;
            }
        }else if(requestCode==Constant.INTENT_LOGIN_EQUESTCODE&& resultCode == Constant.INTENT_LOGIN_RESULTCODE){
            if (null != data) {
                boolean booleanExtra = data.getBooleanExtra(Constant.INTENT_LOGIN_STATE, false);
                //登录成功,判断用户有没有绑定手机号码
                if (booleanExtra&&null!=VideoApplication.getInstance().getUserData()) {
                    if(!VideoApplication.getInstance().userIsBinDingPhone()){
                        binDingPhoneNumber("绑定手机号码",Constant.FRAGMENT_TYPE_PHONE_BINDING,getResources().getString(R.string.binding_phone_tips));
                        return;
                    }
                }else{
                    ToastUtils.showCenterToast("检测到账户异常，需要重新验证身份");
                    UMShareConfig config = new UMShareConfig();
                    config.isNeedAuthOnGetUserInfo(true);
                    UMShareAPI.get(MediaEditActivity.this).setShareConfig(config);
                    login();
                }
            }
         //封面
        }else if(Constant.MEDIA_START_COVER_REQUEST_CODE==requestCode&&Constant.MEDIA_START_COVER_RESULT_CODE==resultCode){
            if(null!=data){
                mThbumSseekTime = data.getLongExtra(Constant.KEY_INTENT_MEDIA_THUBM, 0);
                setVideoCover();
            }
        }else if(requestCode==Constant.MEDIA_BINDING_PHONE_REQUEST && resultCode == Constant.MEDIA_BINDING_PHONE_RESULT){
            if(null!=VideoApplication.getInstance().getUserData()&&VideoApplication.getInstance().userIsBinDingPhone()){
                onNextClick();
                return;
            }else{
                ToastUtils.showCenterToast("需要绑定手机号码才能发布作品噢~");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Handler sHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==123){
                Bitmap bitmap= (Bitmap) msg.obj;
                if(null!=bitmap){
                    if(null!=mIv_video_cover) mIv_video_cover.setImageBitmap(bitmap);
                }else{
                    if(null!=mIv_video_cover) mIv_video_cover.setImageResource(R.drawable.iv_video_square_errror);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     *设置封面
     */
    private void setVideoCover() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(null==mVideoInfo){
                    try {
                        //这里的代码只被执行一次
                        mVideoInfo = VideoUtils.getVideoInfo(mVideoPath);
                        //设置封面
                        if(ConfigSet.getInstance().isSaveVideo()){
                            try{
                                String desDir = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ?
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                                                + "/XinQu/Camera/" : Environment.getExternalStorageDirectory().getAbsolutePath() + "/XinQu/Camera/";
                                File file = new File(desDir);
                                if(!file.exists()){
                                    file.mkdirs();
                                }
                                String name = mVideoPath.substring(mVideoPath.lastIndexOf('/'));
                                String desPath = desDir + name;
                                File desFile = new File(desPath);
                                try {
                                    File srcFile = new File(mVideoPath);
                                    if (srcFile.exists() && !desFile.exists()) {
                                        InputStream is = new FileInputStream(mVideoPath);
                                        FileOutputStream fos = new FileOutputStream(desFile);
                                        byte[] buffer = new byte[1024];
                                        int length;
                                        while ((length = is.read(buffer)) != -1) {
                                            fos.write(buffer, 0, length);
                                        }
                                        is.close();
                                        fos.close();

                                        // 发送系统广播通知刷新
                                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        Uri uri = Uri.fromFile(desFile);
                                        intent.setData(uri);
                                        sendBroadcast(intent);
                                    }else{

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            }catch (Exception e){

                            }
                        }
                    }catch (Exception e){

                    }
                }
                try{
                    //获取截图
                    Bitmap bitmap=null;
                    ProbeMediaInfoTools probeMediaInfoTools = new ProbeMediaInfoTools();
                    if (!mH265File) {
                        bitmap = probeMediaInfoTools.getVideoThumbnailAtTime(mVideoPath, mThbumSseekTime, 0, 0, true);
                    } else {
                        //h265的视频暂时不支持精准seek
                        bitmap = probeMediaInfoTools.getVideoThumbnailAtTime(mVideoPath, mThbumSseekTime, 0, 0, false);
                    }
                    Message message=Message.obtain();
                    message.what=123;
                    message.obj=bitmap;
                    sHandler.sendMessage(message);

                }catch (Exception e){
                    Message message=Message.obtain();
                    message.what=123;
                    message.obj=null;
                    sHandler.sendMessage(message);
                }
            }
        }.start();
    }



    /**
     * 设置话题
     * @param
     */
    private void setResultTopic(List<String> topic_list) {
        if(null!=topic_list&&topic_list.size()>0){
            String topicContent=null;
            StringBuilder stringBuilder=new StringBuilder();
            for (String s : topic_list) {
                stringBuilder.append(s);
            }
            topicContent=stringBuilder.toString();

            String despContent= mVideoDespContent.getText().toString();
            //输入框剩余可输入长度 已不够设置新的话题
            if((VIDEO_DESP_CHARCOUNT-despContent.length())<topicContent.length()){
                ToastUtils.showCenterToast("剩余编辑字数长度不足！");
                return;
            }
            //输入框中有包含旧的一样的话题关键字
//			if(Utils.topicCountEquals(despContent,topicContent)){
//				return;
//			}
            mVideoDespContent.setText("");
            String newContent = (despContent+topicContent);
            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(MediaEditActivity.this,newContent,(int) mVideoDespContent.getTextSize());
            mVideoDespContent.setText(spannableString);
        }
    }





    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }


    /**
     * 切换播放按钮
     * @param flag
     */
    private void changePlayButton(boolean flag) {
        if(flag){
            if(null!=mPauseView) mPauseView.getDrawable().setLevel(2);
            if(null!=mSectionView) mSectionView.calculateRange();
            startPreviewTimerTask();
            //恢复播放的时候，需要调用setDrawHelpTool隐藏当前编辑态的贴纸的辅助绘制区域
            if(null!=mKSYStickerView) mKSYStickerView.setDrawHelpTool(false);
        }else{
            if(null!=mPauseView) mPauseView.getDrawable().setLevel(1);
            stopPreviewTimerTask();
        }
    }


    /**
     * 从Assert文件夹中读取位图数据
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    /**
     * 从sd中读取位图数据
     * @param fileName
     * @return
     */
    private Bitmap getImageFromSDFile(String fileName) {
        Bitmap bitmap=null;
        try {
            bitmap= BitmapFactory.decodeFile(fileName);
        }catch (Exception e){

        }
        return bitmap;
    }
    private void onWaterMarkLogoClick(boolean isCheck) {
        if (isCheck) {
            mEditKit.showWaterMarkLogo(mLogoPath, 0.77f, 0.02f, 0.20f, 0, 0.8f);
        } else {
            mEditKit.hideWaterMarkLogo();
        }
    }

    /**
     * 暂停和开始播放
     */
    private void onPauseClick() {
        if (null!=mEditKit&&mPauseView.getDrawable().getLevel() == 2) {
            mEditKit.pausePlay(true);
            changePlayButton(false);
        } else if(null!=mEditKit){
            mEditKit.pausePlay(false);
            changePlayButton(true);
        }
    }

    private void onBackoffClick() {
        if(-1!=mBottomViewPreIndex){
            cleafunctionUI();
        }else{
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if(-1!=mBottomViewPreIndex){
            cleafunctionUI();
            return;
        }
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("退出提示")
                .setMessage("确定要放弃所有的操作吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 合成视频,只弹出分辨率选择框
     */
    private void onNextClick() {
        //最大限制12分钟短片
        if(null!=mVideoRangeSeekBar&&mVideoRangeSeekBar.getRangeEnd()-mVideoRangeSeekBar.getRangeStart()>Constants.MAX_CAP_DURTION){
            ToastUtils.showCenterToast("视频时长必须小于12分钟");
            return;
        }
        if(null==VideoApplication.getInstance().getUserData()){
            login();
            return;
        }else{
            if(null!=VideoApplication.getInstance().getUserData()&&VideoApplication.getInstance().userIsBinDingPhone()){
                onOutputConfirmClick();
                return;
            }else{
                binDingPhoneNumber("绑定手机号码",Constant.FRAGMENT_TYPE_PHONE_BINDING,getResources().getString(R.string.binding_phone_tips));
            }
        }
    }
    /**
     *  视频输出
     */
    private void onOutputConfirmClick() {
        if(null==mEditKit||null==mVideoInfo) return;
        if(null!=mVideoRangeSeekBar&&mVideoRangeSeekBar.getRangeEnd()-mVideoRangeSeekBar.getRangeStart()<5){
            ToastUtils.showCenterToast("视频最小长度不能小于5秒！");
            return;
        }
        //检查SD读写权限
        RxPermissions.getInstance(MediaEditActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    //已经取得授权
                    if(AuthInfoManager.getInstance().getAuthState()){
                        if(null==mComposeConfig) mComposeConfig=new ShortVideoConfig();
                        //配置合成参数
                        //用户是否勾选了视频输出添加视频水印
                        if(ConfigSet.getInstance().isAddWatermark()){
                            onWaterMarkLogoClick(true);
                        }
                        mComposeConfig.videoBitrate=2000;
                        mEditKit.setVideoFps(0);//fps
                        mEditKit.setTargetResolution(StreamerConstants.VIDEO_RESOLUTION_480P);
                        mEditKit.setEncodeMethod(StreamerConstants.ENCODE_METHOD_HARDWARE);//编码方式，软编，ENE_METHOD_HARDWARE硬编
                        mEditKit.setVideoDecodeMethod(StreamerConstants.DECODE_METHOD_HARDWARE);//硬解
                        mEditKit.setVideoCodecId(AVConst.CODEC_ID_AVC);//编码方法 AVConst.CODEC_ID_AVC  264
                        mEditKit.setVideoEncodeProfile(VideoEncodeFormat.ENCODE_PROFILE_BALANCE);// 默认平衡质量
                        mEditKit.setAudioKBitrate(mComposeConfig.audioBitrate);
                        mEditKit.setVideoKBitrate(mComposeConfig.videoBitrate);
                        //设置合成路径
                        String fileFolder =ApplicationManager.getInstance().getOutPutPath(1);
                        File file = new File(fileFolder);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        StringBuilder composeUrl = new StringBuilder(fileFolder).append("/").append(System.currentTimeMillis());
                        composeUrl.append(".mp4");
                        mEditKit.pauseEditPreview();
                        changePlayButton(false);
                        //构建合并\上传对象
                        UploadVideoInfo uploadVideoInfo=new UploadVideoInfo();
                        uploadVideoInfo.setId(System.currentTimeMillis());
                        uploadVideoInfo.setVideoFps(fps);
                        uploadVideoInfo.setVideoBitrate(mComposeConfig.videoBitrate);
                        uploadVideoInfo.setCompostOutFilePath(composeUrl.toString());
                        uploadVideoInfo.setFilePath(composeUrl.toString());
                        uploadVideoInfo.setIsPrivate(isPrivate);
                        uploadVideoInfo.setItemType(1);
                        uploadVideoInfo.setMusicID(mMusicID);
                        uploadVideoInfo.setMusicPath(mMusicPath);
                        uploadVideoInfo.setUploadType(99);
                        uploadVideoInfo.setDownloadPermiss(isDownloadPermiss?"0":"1");
                        uploadVideoInfo.setVideoCoverFps(mThbumSseekTime);
                        try {
                            uploadVideoInfo.setVideoDesp(URLEncoder.encode(mVideoDespContent.getText().toString(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        uploadVideoInfo.setVideoDurtion((int) mEditPreviewDuration);
                        uploadVideoInfo.setVideoWidth(Integer.parseInt(mVideoInfo.getVideo_width()));
                        uploadVideoInfo.setVideoHeight(Integer.parseInt(mVideoInfo.getVideo_height()));
                        uploadVideoInfo.setSourceType(mSourceType);
                        uploadVideoInfo.setResoucePath(mVideoPath);
                        obiectIsRecyler=false;
                        VideoComposeTask videoComposeTask = new VideoComposeTask(uploadVideoInfo, mEditKit);
                        videoComposeTask.execute();//直接单个任务进行，不支持暂停、取消等操作
                        ActivityCollectorManager.finlishAllActivity();
                        //ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_ADD_VIDEO_TASK);//通知切换到HomeFragment界面
                        //VideoComposeProcessor.getInstance().addVideoComposeTask(null,null);//支持暂停、取消任务的Task
                    //金山云权限不够,去检查和重新获取权限
                    }else{
                        //暂停预览
                        if(null!=mEditKit) mEditKit.onPause();
                        if(null!=mEditKit&&mPauseView.getDrawable().getLevel() == 2){
                            mEditKit.pausePlay(true);
                            changePlayButton(false);
                        }
                        KsyAuthorizeSettingFragment fragment = KsyAuthorizeSettingFragment.newInstance();
                        FragmentManager supportFragmentManager = getSupportFragmentManager();
                        fragment.show(supportFragmentManager,"ksy_authorize");
                    }
                }else{
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaEditActivity.this)
                                .setTitle("SD读取权限申请失败")
                                .setMessage("部分权限被拒绝，将无法使用视频合并和上传功能，请先授予足够权限再使用视频扫描功能！授权成功后请重启开启本界面。是否现在去设置？");
                        builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SystemUtils.getInstance().startAppDetailsInfoActivity(MediaEditActivity.this,141);
                            }
                        });
                        builder.show();
                        return;
                    }
                }
            }
        });
    }


    private KSYEditKit.OnInfoListener mOnInfoListener = new KSYEditKit.OnInfoListener() {
        @Override
        public Object onInfo(int type, String... msgs) {
            switch (type) {
                case ShortVideoConstants.SHORTVIDEO_EDIT_PREPARED:
                    mEditPreviewDuration = mEditKit.getEditDuration();
                    mPreviewLength = mEditPreviewDuration;
                    // 启动预览后，开始片段编辑UI初始化
                    if(null!=mSectionView) mSectionView.init(mEditPreviewDuration, mEditKit);
                    startPreviewTimerTask();
                    break;
                //开始合并文件
                case ShortVideoConstants.SHORTVIDEO_COMPOSE_START: {

                    return null;
                }
                case ShortVideoConstants.SHORTVIDEO_COMPOSE_FINISHED: {

                    //合成结束需要置为null，再次预览时重新创建
//                    clearImgFilter();
//                    cleanMusic();
//                    composeFinished(msgs[0]);
                    return null;
                }
                case ShortVideoConstants.SHORTVIDEO_COMPOSE_ABORTED:

                    break;
                //重新播放
                case ShortVideoConstants.SHORTVIDEO_EDIT_PREVIEW_PLAYER_INFO:
                    repeatPlayerMusic();//重新播放视频了
                    break;
                case ShortVideoConstants.SHORTVIDEO_COMPOSE_TAIL_STARTED:

                    break;
                case ShortVideoConstants.SHORTVIDEO_COMPOSE_TITLE_STARTED:

                default:
                    return null;
            }
            return null;
        }
    };




    private void repeatPlayerMusic() {
        if(null!=mPauseView) mPauseView.getDrawable().setLevel(2);
        if(null!=mSectionView) mSectionView.calculateRange();
    }



    /**
     * 显示View,从下而上
     * @param view
     */
    private void showViewFromBottomToTop(View view) {
        if(null!=view&&view.getVisibility()!=View.VISIBLE){
            view.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewLocation();
            view.startAnimation(translateAnimation);
        }
    }


    //============================================美颜===============================================

    /**
     * 初始化美颜列表
     */
    private void initFairView() {
        RecyclerView record_recycler_fair = (RecyclerView) findViewById(R.id.record_recycler_bueay);
        List<MediaFilterInfo> mediaFilterData = DataFactory.getMediaBeautyData(this);
        if(null!=mediaFilterData&&mediaFilterData.size()>0){
            mediaFilterData.add(0,new MediaFilterInfo("无",R.drawable.beauty_origin,true,100));//添加一个默认没有美颜效果的项
        }
        record_recycler_fair.setLayoutManager(new LinearLayoutManager(MediaEditActivity.this,LinearLayoutManager.HORIZONTAL,false));
        record_recycler_fair.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        mMediaEditBeautyAdapter = new MediaEditBeautyAdapter(mediaFilterData);
        record_recycler_fair.setAdapter(mMediaEditBeautyAdapter);
        record_recycler_fair.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switchBeautyFilterType(position);
            }
        });
    }

    /**
     * 切换选中的美颜类型
     * @param poistion
     */
    private void switchBeautyFilterType(int poistion) {
        if(poistion==mCureentBuayePoistion){
            return;
        }
        //切换UI
        if(null!= mMediaEditBeautyAdapter){
            List<MediaFilterInfo> data = mMediaEditBeautyAdapter.getData();
            if(null!=data&&data.size()>0){
                //先还原刚才选中的
                for (int i = 0; i < data.size(); i++) {
                    if(mCureentBuayePoistion==i){
                        data.get(i).setSelector(false);
                        break;
                    }
                }
                mMediaEditBeautyAdapter.notifyItemChanged(mCureentBuayePoistion);//刷新原来选中的为未选中
                //再设置新的选中的
                for (int i = 0; i < data.size(); i++) {
                    if(poistion==i){
                        data.get(poistion).setSelector(true);
                        break;
                    }
                }
                mMediaEditBeautyAdapter.notifyItemChanged(poistion);
                mCureentBuayePoistion = poistion;
                mBtn_fair.setBackgroundResource(mCureentBuayePoistion<=0?R.drawable.ic_edit_fair_nomail:R.drawable.ic_ic_record_fair_noi_fair_pre);
                MediaFilterInfo mediaFilterInfo = data.get(mCureentBuayePoistion);
                if(null!=mediaFilterInfo){
                    addImgFilter(mediaFilterInfo.getId());
                }
            }
        }
    }

    /**
     * 添加美颜
     * @param type
     */
    private void addImgFilter(int type) {
        ImgBeautyProFilter proFilter;
        ImgBeautySpecialEffectsFilter specialEffectsFilter;
        ImgTexFilter texFilter;
        List<ImgFilterBase> filters = new LinkedList<>();
        switch (type) {
            case BEAUTY_NATURE:
                ImgBeautySoftFilter softFilter = new ImgBeautySoftFilter(mEditKit.getGLRender());
                softFilter.setGrindRatio(0.5f);
                filters.add(softFilter);
                break;
            case BEAUTY_PRO:
                proFilter = new ImgBeautyProFilter(mEditKit.getGLRender(), getApplicationContext());
                proFilter.setGrindRatio(0.5f);
                proFilter.setWhitenRatio(0.5f);
                proFilter.setRuddyRatio(0);
                filters.add(proFilter);
                break;
            case BEAUTY_FLOWER_LIKE:
                proFilter = new ImgBeautyProFilter(mEditKit.getGLRender(), getApplicationContext(), 3);
                proFilter.setGrindRatio(0.5f);
                proFilter.setWhitenRatio(0.5f);
                proFilter.setRuddyRatio(0.15f);
                filters.add(proFilter);
                break;
            case BEAUTY_DELICATE:
                proFilter = new ImgBeautyProFilter(mEditKit.getGLRender(), getApplicationContext(), 3);
                proFilter.setGrindRatio(0.5f);
                proFilter.setWhitenRatio(0.5f);
                proFilter.setRuddyRatio(0.3f);
                filters.add(proFilter);
                break;
            case FILTER_DISABLE:
                break;
            default:
                break;
        }
        if (mFilterTypeIndex != -1 && mEffectFilterIndex != FILTER_DISABLE) {
            if (mFilterTypeIndex < 13) {
                specialEffectsFilter = new ImgBeautySpecialEffectsFilter(mEditKit.getGLRender(),
                        getApplicationContext(), mEffectFilterIndex);
                filters.add(specialEffectsFilter);
            } else {
                texFilter = new ImgBeautyStylizeFilter(mEditKit.getGLRender(), getApplicationContext(),
                        mEffectFilterIndex);
                filters.add(texFilter);
            }
        }
        if (filters.size() > 0) {
            mEditKit.getImgTexFilterMgt().setFilter(filters);
        } else {
            mEditKit.getImgTexFilterMgt().setFilter((ImgTexFilterBase) null);

        }
    }


    //============================================滤镜==============================================


    /**
     * 视频滤镜
     * https://github.com/ksvc/KSYStreamer_Android/wiki/style_filter
     */
    private void initFilterUI(){
        List<MediaFilterInfo> mediaFilterData = DataFactory.getMediaFilterData(this);
        if(null!=mediaFilterData){
            MediaFilterInfo Info=new MediaFilterInfo();
            Info.setTitle("无滤镜");
            Info.setIcon(R.drawable.filter_original);
            Info.setSelector(true);
            mediaFilterData.add(0,Info);
        }
        RecyclerView filterRecyclerVie= (RecyclerView) findViewById(R.id.filter_recyclerView);
        filterRecyclerVie.setLayoutManager(new LinearLayoutManager(MediaEditActivity.this,LinearLayoutManager.HORIZONTAL,false));
        filterRecyclerVie.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        filterRecyclerVie.setHasFixedSize(true);
        mMediaRecordFilterAdapter = new MediaRecordFilterAdapter(mediaFilterData);
        filterRecyclerVie.setAdapter(mMediaRecordFilterAdapter);
        filterRecyclerVie.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switchFilterType(position);
            }
        });
        setEffectFilter(FILTER_DISABLE);//默认的无滤镜
    }

    /**
     * 切换选中的滤镜类型
     * @param poistion
     */
    private void switchFilterType(int poistion) {
        if(poistion==mCureentFilterPoistion){
            return;
        }
        //切换UI
        if(null!= mMediaRecordFilterAdapter){
            List<MediaFilterInfo> data = mMediaRecordFilterAdapter.getData();
            if(null!=data&&data.size()>0){
                //先还原刚才选中的
                for (int i = 0; i < data.size(); i++) {
                    if(mCureentFilterPoistion==i){
                        data.get(i).setSelector(false);
                        break;
                    }
                }
                mMediaRecordFilterAdapter.notifyItemChanged(mCureentFilterPoistion);//刷新原来选中的为未选中
                //再设置新的选中的
                for (int i = 0; i < data.size(); i++) {
                    if(poistion==i){
                        data.get(poistion).setSelector(true);
                        break;
                    }
                }
                mMediaRecordFilterAdapter.notifyItemChanged(poistion);
                mCureentFilterPoistion = poistion;
            }
        }
        //切换滤镜--空
        if(poistion<=0){
            setEffectFilter(FILTER_DISABLE);//默认的无滤镜
            //其他
        }else{
            setEffectFilter(Constants.FILTER_TYPE[poistion-1]);
        }
    }

    /**
     * 取消所有的滤镜设置
     */
    private void clearImgFilter() {
        mImgBeautyProFilter = null;
        mEffectFilterIndex = FILTER_DISABLE;
    }


    /**
     * 添加滤镜
     */
    private void addImgFilter() {

        ImgBeautyProFilter proFilter;
        ImgBeautySpecialEffectsFilter specialEffectsFilter;
        List<ImgFilterBase> filters = new LinkedList<>();

        if (mImgBeautyProFilter != null) {
            proFilter = new ImgBeautyProFilter(mEditKit.getGLRender(), getApplicationContext());
            proFilter.setGrindRatio(mImgBeautyProFilter.getGrindRatio());
            proFilter.setRuddyRatio(mImgBeautyProFilter.getRuddyRatio());
            proFilter.setWhitenRatio(mImgBeautyProFilter.getWhitenRatio());
            mImgBeautyProFilter = proFilter;
            filters.add(proFilter);
        }

        if (mEffectFilterIndex != FILTER_DISABLE) {
            specialEffectsFilter = new ImgBeautySpecialEffectsFilter(mEditKit.getGLRender(),
                    getApplicationContext(), mEffectFilterIndex);
            filters.add(specialEffectsFilter);
        }

        if (filters.size() > 0) {

            mEditKit.getImgTexFilterMgt().setFilter(filters);

        } else {
            mEditKit.getImgTexFilterMgt().setFilter((ImgTexFilterBase) null);
        }
    }

    /**
     * 修改字体颜色
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        //必须选中字幕贴纸该设置才会生效
        mKSYStickerView.setCurrentTextColor(newColor);
        if(null!=mTv_color_view) mTv_color_view.setTextColor(newColor);
    }

    /**
     * 暂停预览，方便片段编辑
     */
    private void pausePreview() {
        if (mPauseView.getDrawable().getLevel() == 2) {
            mEditKit.pausePlay(true);
            changePlayButton(false);
        }
    }


    private void startPreviewTimerTask() {
        if(null!=mSectionView) mSectionView.startPreview();
        mPreviewRefreshTimer = new Timer();
        mPreviewRefreshTask = new TimerTask() {
            @Override
            public void run() {
                refreshUiOnUiThread();
            }
        };
        // 定义顶部滚动view的刷新频率为20fps
        mPreviewRefreshTimer.schedule(mPreviewRefreshTask, 50, 50);
    }

    private void stopPreviewTimerTask() {
        if (mPreviewRefreshTimer != null) {
            mPreviewRefreshTimer.cancel();
            mPreviewRefreshTimer = null;
        }
        if (mPreviewRefreshTask != null) {
            mPreviewRefreshTask.cancel();
            mPreviewRefreshTask = null;
        }
        if(null!=mSectionView)  mSectionView.stopPreview();
    }

    private void refreshUiOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=mEditKit){
                    long curTime = mEditKit.getEditPreviewCurrentPosition();
                    if(null!=mSectionView) mSectionView.scrollAuto(curTime);
                }
            }
        });
    }


    /**
     * 初始化贴纸UI素材界面
     */

    private void initStickerView() {
        //第一次初始化，根据缓存数据初始界面
        if(null==mXinQuFragmentPagerAdapter){
            btnLoading = (ImageView) findViewById(R.id.btn_loading);
            btnLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initStickerList();
                }
            });
            //贴纸列表分类缓存
            mStickerListInfoList= (List<StickerListInfo.DataBean>)  ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_MEDIA_EDIT_STICKER_LIST);
            List<String> titles=new ArrayList<>();
            titles.add("用过的");
            List<Fragment> fragments=new ArrayList<>();
            fragments.add(MediaStickerUseFragment.newInstance(this));//先初始化一个用过的界面，再根据缓存确定其他所有的分类界面

            if(null!=mStickerListInfoList&&mStickerListInfoList.size()>0){
                for (StickerListInfo.DataBean dataBean : mStickerListInfoList) {
                    if(null!=dataBean){
                        titles.add(dataBean.getName());
                        fragments.add(MediaStickerFragment.newInstance(dataBean.getId(),this));
                    }
                }
            }
            mXinQuFragmentPagerAdapter = new XinQuFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
            XTabLayout sticker_tab_layout = (XTabLayout) findViewById(R.id.sticker_tab_layout);
            mSticker_tab_viewpager = (ViewPager) findViewById(R.id.sticker_tab_viewpager);
            mSticker_tab_viewpager.setAdapter(mXinQuFragmentPagerAdapter);
            mSticker_tab_viewpager.setOffscreenPageLimit(1);
            sticker_tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
            sticker_tab_layout.setupWithViewPager(mSticker_tab_viewpager);
            if(null!=mStickerListInfoList&&mStickerListInfoList.size()>0){
                mSticker_tab_viewpager.setCurrentItem(1);
            }else {
                mSticker_tab_viewpager.setCurrentItem(0);
                //只当在缓存不存在的情况下才请求网络加载素材分类列表
                initStickerList();
            }
        //刷新界面，根据新的数据刷新界面
        }else{
            if(null!=mStickerListInfoList&&mStickerListInfoList.size()>0){
                List<String> titles=new ArrayList<>();
                titles.add("用过的");
                List<Fragment> fragments=new ArrayList<>();
                fragments.add(MediaStickerUseFragment.newInstance(this));
                for (StickerListInfo.DataBean dataBean : mStickerListInfoList) {
                    if(null!=dataBean){
                        titles.add(dataBean.getName());
                        fragments.add(MediaStickerFragment.newInstance(dataBean.getId(),this));
                    }
                }
                mXinQuFragmentPagerAdapter.setNewFragments(fragments,titles);
                mSticker_tab_viewpager.setCurrentItem(1);
            }
        }
    }

    /**
     * 贴纸的点击事件
     * @param stickerPath
     */
    @Override
    public void onStickerAddItemClick(String stickerPath) {
        if(TextUtils.isEmpty(stickerPath)) return;
        //进入贴纸编辑状态，需要先暂停预览播放
        initStickerHelpBox();
        pausePreview();
        KSYStickerInfo info = new KSYStickerInfo();
        int index=0;
        info.startTime = Long.MIN_VALUE;
        info.duration = mEditKit.getEditDuration();
        //静态的
        if(stickerPath.endsWith(".png")){
            Bitmap imageFromSDFile = getImageFromSDFile(stickerPath);
            if(null==imageFromSDFile){
                ToastUtils.showCenterToast("添加贴纸错误！");
                return;
            }
            info.bitmap = imageFromSDFile;
            info.stickerType = KSYStickerInfo.STICKER_TYPE_IMAGE; //是否是字幕贴纸
        }else{
            info.animateUrl = stickerPath;//stickerPath.toString();
            info.stickerType = KSYStickerInfo.STICKER_TYPE_IMAGE_ANIMATE; //是否是字幕贴纸
        }
        //添加一个贴纸
        index = mKSYStickerView.addSticker(info, mStickerHelpBoxInfo);
        //选择下一个贴纸时让上一个贴纸生效（如果之前已选择贴纸）
        if (null!=mSectionView&&mSectionView.isSeeking()) {
            mSectionView.calculateRange();
        }
        //开始当前贴纸的片段编辑
        if(null!=mSectionView) mSectionView.startSeek(index);
    }

    /**
     * 加载列表贴纸失败的时候刷新调用
     */
    private void initStickerList() {
        if(null==mMediaEditPresenterWeakReference||null==mMediaEditPresenterWeakReference.get()){
            initPresenter();
        }
        findViewById(R.id.tv_error_sticker_tips).setVisibility(View.GONE);//隐藏提示文字
        if(null!=btnLoading){
            btnLoading.setImageResource(R.drawable.loading_anim);
            btnLoading.setVisibility(View.VISIBLE);
        }
        if(null==drawableAnimation){
            drawableAnimation = (AnimationDrawable) btnLoading.getDrawable();
            drawableAnimation.start();
        }
        if(null!=mMediaEditPresenterWeakReference&&null!=mMediaEditPresenterWeakReference.get()&&!mMediaEditPresenterWeakReference.get().isStickerLoading()){
            mMediaEditPresenterWeakReference.get().getStickerList();
        }
    }

    //==========================================字幕相关============================================

    /**
     * 初始化字幕
     */
    private void initCoptions() {

        View.OnClickListener onCaptionClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ll_captions_canel:
                        if(null!=mKSYStickerView){
                            mKSYStickerView.removeTextStickers();
                        }
                        break;
                    case R.id.ll_captions_add:
                        addCaptionEmptySticker();
                        break;
                }
            }
        };

        ((LinearLayout) findViewById(R.id.ll_captions_canel)).setOnClickListener(onCaptionClickListener);
        ((LinearLayout) findViewById(R.id.ll_captions_add)).setOnClickListener(onCaptionClickListener);
        mTv_color_view = (TextView) findViewById(R.id.tv_color_view);//调色板当前选中的字体颜色
        mTv_color_view.setTextColor(CommonUtils.getColor(R.color.white));
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
        colorPickerView.setOrientation(ColorPickerView.Orientation.HORIZONTAL);
        colorPickerView.setOnColorPickerChangeListener(new ColorPickerView.OnColorPickerChangeListener() {
            @Override
            public void onColorChanged(ColorPickerView picker, int color) {
                changeTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(ColorPickerView picker) {

            }

            @Override
            public void onStopTrackingTouch(ColorPickerView picker) {

            }
        });

        if(null!=mKSYStickerView){
            mKSYStickerView.setCurrentTextColor(Color.WHITE);
        }
        if(firstAddTextSticker){
            addCaptionEmptySticker();
            showCaptionsTextInputView(true,false,"");
            firstAddTextSticker=false;
        }
    }

    /**
     * 添加一个空背景的字幕
     */
    private void addCaptionEmptySticker() {
        initStickerHelpBox();
        pausePreview();
        //进入字幕编辑状态，需要先暂停预览播放
        KSYStickerInfo params = new KSYStickerInfo();
        //字幕贴纸的文字相关信息
        DrawTextParams textParams = new DrawTextParams();
        textParams.textPaint = new TextPaint();
        textParams.textPaint.setTextSize(DrawTextParams.DEFAULT_TEXT_SIZE);
        textParams.textPaint.setColor(mKSYStickerView.getCurrentTextColor());
        textParams.textPaint.setTextAlign(Paint.Align.LEFT);
        textParams.textPaint.setStyle(Paint.Style.FILL);
        textParams.textPaint.setAntiAlias(true);
        textParams.autoNewLine = false;
        params.bitmap = null;
        if (TextUtils.isEmpty(textParams.text)) {
            textParams.text = "点击修改";
        }
        params.textParams = textParams;
        params.startTime = Long.MIN_VALUE;
        params.duration = mEditKit.getEditDuration();
        params.stickerType = KSYStickerInfo.STICKER_TYPE_TEXT;  //是否是字幕贴纸
        int index = mKSYStickerView.addSticker(params, mStickerHelpBoxInfo);
        // 选择下一个字幕时让前一个字幕生效（如果之前已选择一个字幕）
        if (null!=mSectionView&&mSectionView.isSeeking()) {
            mSectionView.calculateRange();
        }
        //开始当前字幕的片段编辑
        if(null!=mSectionView) mSectionView.startSeek(index);
    }


    /**
     * 贴纸的辅助区域
     */
    private void initStickerHelpBox() {
        if (mStickerDeleteBitmap == null) mStickerDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_delete);
        if (mStickerRotateBitmap == null)  mStickerRotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_rotate);
        if (mStickerHelpBoxInfo == null) {
            mStickerHelpBoxInfo = new StickerHelpBoxInfo();
            mStickerHelpBoxInfo.deleteBit = mStickerDeleteBitmap;
            mStickerHelpBoxInfo.rotateBit = mStickerRotateBitmap;
            Paint helpBoxPaint = new Paint();
            helpBoxPaint.setColor(Color.BLACK);
            helpBoxPaint.setStyle(Paint.Style.STROKE);
            helpBoxPaint.setAntiAlias(true);
            helpBoxPaint.setStrokeWidth(4);
            mStickerHelpBoxInfo.helpBoxPaint = helpBoxPaint;
        }
    }



    /**
     * 根据是否有背景音乐选中来设置相应的编辑控件是否可用
     */
    private void setEnableBgmEdit(boolean enable) {
        if (mBgmVolumeSeekBar != null) {
            mBgmVolumeSeekBar.setEnabled(enable);
        }
    }

    //=========================================视频时长裁剪==========================================
    /**
     * 初始化时长裁剪UI
     */

    private void initVideoRange() {
        //h265的视频暂时不支持精准获取缩略图
        mImageSeekTools = new ProbeMediaInfoTools();
        mImageSeekTools.probeMediaInfo(mVideoPath, new ProbeMediaInfoTools.ProbeMediaInfoListener() {
            @Override
            public void probeMediaInfoFinished(ProbeMediaInfoTools.MediaInfo info) {
                if (info.videoStreams != null && info.videoStreams.size() > 0) {
                    KSYProbeMediaInfo.KSYVideoCodecType videoCodecType = info.videoStreams.get(0).getVideoCodecType();
                    if (videoCodecType == KSYProbeMediaInfo.KSYVideoCodecType.KSY_VIDEO_H265) {
                        mH265File = true;
                    }
                }
            }
        });

        mVideoRangeStart = (TextView) findViewById(R.id.range_start);  //裁剪开始位置
        mVideoRange = (TextView) findViewById(R.id.range);    //裁剪时长
        mVideoRangeEnd = (TextView) findViewById(R.id.range_end);  //裁剪结束位置
        //裁剪bar
        mVideoRangeSeekBar = (VideoRangeSeekBar) findViewById(R.id.videodurtion_change_seekbar);
        mVideoRangeSeekBar.setOnVideoMaskScrollListener(mVideoMaskScrollListener);
        mVideoRangeSeekBar.setOnRangeBarChangeListener(onRangeBarChangeListener);
        //缩略图显示
        mVideoThumbnailList = (HorizontalListView) findViewById(R.id.hlistview);
        mVideoThumbnailList.setOnScrollListener(mVideoThumbnailScrollListener);
    }

    /**
     * 调整条进度
     */
    private void initSeekBar() {
        long durationMS = mEditKit.getEditDuration();
        float durationInSec = durationMS * 1.0f / 1000;
        if (durationMS > mMaxClipSpanMs) {
            mVideoRangeSeekBar.setMaxRange(mMaxClipSpanMs * 1.0f / 1000);
        } else {
            mVideoRangeSeekBar.setMaxRange(durationInSec);
        }
        mVideoRangeSeekBar.setMinRange(Constants.MIN_CAP_DURTION);
        if (durationInSec > Constants.MAX_CAP_DURTION) {
            mVideoRangeSeekBar.setRange(0.0f, Constants.MAX_CAP_DURTION);
        } else {
            mVideoRangeSeekBar.setRange(0.0f, durationInSec);
        }
    }

    /**
     * 截取视频的封面
     */
    private void initThumbnailAdapter() {
        float picWidth;  //每个thumbnail显示的宽度
        if (mVideoRangeSeekBar == null) {
            picWidth = 60;
        } else {
            picWidth = mVideoRangeSeekBar.getFrameWidth();
        }
        long durationMS = mEditKit.getEditDuration();

        //list区域需要显示的item个数
        int totalFrame;
        //比最大裁剪时长大的视频,每长mMaxClipSpanMs长度,则增加8个thumbnail
        //比最大裁剪时长小的视频,最多显示8个thumbnail
        if (durationMS > mMaxClipSpanMs) {
            totalFrame = (int) (durationMS * 8) / mMaxClipSpanMs;
        } else {
            totalFrame = 10;
        }

        int mm = totalFrame;

        VideoThumbnailInfo[] listData = new VideoThumbnailInfo[totalFrame];
        for (int i = 0; i < totalFrame; i++) {
            listData[i] = new VideoThumbnailInfo();
            if (durationMS > mMaxClipSpanMs) {
                listData[i].mCurrentTime = i * ((float) durationMS / 1000) * (1.0f / mm);
            } else {
                if (i > 0 && i < 9) {
                    listData[i].mCurrentTime = (i - 1) * ((float) durationMS / 1000) * (1.0f / 8);
                }
            }

            if (i == 0 && mVideoRangeSeekBar != null) {
                listData[i].mType = VideoThumbnailInfo.TYPE_START;
                listData[i].mWidth = (int) mVideoRangeSeekBar.getMaskWidth();
            } else if (i == totalFrame - 1 && mVideoRangeSeekBar != null) {
                listData[i].mType = VideoThumbnailInfo.TYPE_END;
                listData[i].mWidth = (int) mVideoRangeSeekBar.getMaskWidth();
            } else {
                listData[i].mType = VideoThumbnailInfo.TYPE_NORMAL;
                listData[i].mWidth = (int) picWidth;
            }
        }

        mMediaEditThumbnailAdapter = new MediaEditThumbnailAdapter(this, listData, mEditKit);
        mVideoThumbnailList.setAdapter(mMediaEditThumbnailAdapter);
    }


    VideoRangeSeekBar.OnRangeBarChangeListener onRangeBarChangeListener = new VideoRangeSeekBar.OnRangeBarChangeListener() {

        @Override
        public void onIndexChangeListener(VideoRangeSeekBar rangeBar, float rangeStart, float rangeEnd, final int change, boolean toEnd) {

            float toLen = (mVideoRangeSeekBar.getRangeEnd() + mHLVOffsetX) * 1000;
            if (toEnd && toLen >= mMaxClipSpanMs && mMaxClipSpanMs > 0 && toLen <= mEditPreviewDuration) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCenterToast("视频总长不能超过" + mMaxClipSpanMs / 1000 + "秒 ");
                    }
                });
            }
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mHLVOffsetX >= 7.5f && mHLVOffsetX <= 8.5f && !mVideoRangeSeekBar.isTouching()) {
                        mHLVOffsetX = 8.0f;
                        mVideoRangeSeekBar.setRange(mVideoRangeSeekBar.getRangeStart(), mVideoRangeSeekBar.getRangeStart() + mHLVOffsetX);
                    }
                    setRangeTextView(mHLVOffsetX);
                }
            });
            //开启异步线程去截图，只保证程序的顺畅运行，不保证视频封面是否截图成功
            if(!isScreenThubm){
                long screenThubmTime = (long) (mVideoRangeSeekBar.getRangeEnd() * 1000);
                new ScreenThubmAsyncTask().execute(screenThubmTime);
            }
        }

        @Override
        public void onActionUp() {
            rangeLoopPreview();
        }

        @Override
        public void onEventDown(int mode) {
            if(2==mode){
                mIv_thbum_icon.setVisibility(View.VISIBLE);
                if(null!=mEditKit){
                    mEditKit.pausePlay(true);
                    changePlayButton(false);
                }
            }
        }

        @Override
        public void onEventUp(int mode) {
            if(2==mode){
                mIv_thbum_icon.setVisibility(View.GONE);
                if(null!=mEditKit){
                    mEditKit.pausePlay(false);
                    changePlayButton(true);
                }
            }
        }
    };


    /**
     * 异步截图任务
     */
    private class ScreenThubmAsyncTask extends AsyncTask<Long,Void,Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isScreenThubm=true;
        }
        @Override
        protected Bitmap doInBackground(Long... params) {
            if(null!=mImageSeekTools){
                Bitmap  bitmap=null;
                if (!mH265File) {
                    bitmap = mImageSeekTools.getVideoThumbnailAtTime(mVideoPath, params[0], 0, 0, true);
                } else {
                    //h265的视频暂时不支持精准seek
                    bitmap = mImageSeekTools.getVideoThumbnailAtTime(mVideoPath, params[0], 0, 0, false);
                }
                return bitmap;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            isScreenThubm=false;
            if(null!=bitmap&&null!=mIv_thbum_icon){
                mIv_thbum_icon.setImageBitmap(bitmap);
            }
        }
    }


    /**
     * loop preview duraing range
     */
    private void rangeLoopPreview() {
        long startTime = (long) ((mVideoRangeSeekBar.getRangeStart() + mHLVOffsetX) * 1000);
        long endTime = (long) ((mVideoRangeSeekBar.getRangeEnd() + mHLVOffsetX) * 1000);

        //是否对播放区间的设置立即生效，true为立即生效
        mEditKit.setEditPreviewRanges(startTime, endTime, true);
    }


    /**
     * seek to preview
     *
     * @param second
     */
    private void seekToPreview(float second) {
        if (mVideoRangeSeekBar != null) {
            mVideoRangeSeekBar.setIndicatorVisible(false);
        }
        long seekTo = (long) (second * 1000);
        if (seekTo > mEditPreviewDuration) {
            seekTo = mEditPreviewDuration;
        }
        if (seekTo < 0) {
            seekTo = 0;
        }
        mEditKit.seekEditPreview(seekTo);
        if (mVideoRangeSeekBar != null) {
            mVideoRangeSeekBar.setIndicatorOffsetSec((mEditKit.getEditPreviewCurrentPosition() * 1.0f - mHLVOffsetX * 1000) /
                    1000);
        }
    }

    /**
     * 处理时长裁剪的封面显示
     * @param offset
     */
    private void setRangeTextView(float offset) {
        mVideoRangeStart.setText(formatTimeStr(mVideoRangeSeekBar.getRangeStart() + offset));
        mVideoRangeEnd.setText(formatTimeStr(mVideoRangeSeekBar.getRangeEnd() + offset));
        mVideoRange.setText(formatTimeStr2(((int) (10 * mVideoRangeSeekBar.getRangeEnd())) - (int) (10 * mVideoRangeSeekBar.getRangeStart())));
        mPreviewLength = (long) (mVideoRangeSeekBar.getRangeEnd() - mVideoRangeSeekBar.getRangeStart()) * 1000;
        if (mAudioSeekLayout != null && mAudioLength != 0 && mPreviewLength < mAudioLength) {
            mAudioSeekLayout.updateAudioSeekUI(mAudioLength, mPreviewLength);
        }
    }


    private String formatTimeStr2(int s) {
        int second = s / 10;
        int left = s % 10;

        return String.format("%d.%d", second, left);
    }

    private String formatTimeStr(float s) {
        int minute = ((int) s) / 60;
        int second = ((int) s) % 60;
        int left = ((int) (s * 10)) % 10;
        return String.format("%02d:%02d.%d", minute, second, left);
    }

    VideoRangeSeekBar.OnVideoMaskScrollListener mVideoMaskScrollListener = new VideoRangeSeekBar.OnVideoMaskScrollListener() {

        @Override
        public void onVideoMaskScrollListener(VideoRangeSeekBar rangeBar, MotionEvent event) {
            mVideoThumbnailList.dispatchTouchEvent(event);
        }
    };

    /**
     * 滑动缩略图的Bar
     */
    HorizontalListView.OnScrollListener mVideoThumbnailScrollListener = new HorizontalListView.OnScrollListener() {

        @Override
        public void onScroll(final int currentX) {
            final String msg = String.format("currentXX: %d", currentX);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mHLVOffsetX = mVideoRangeSeekBar.getRange(currentX);

                    if (mEditPreviewDuration > mMaxClipSpanMs) {
                        if ((mVideoRangeSeekBar.getRangeEnd() + mHLVOffsetX) * 1000 >= mEditPreviewDuration) {
                            mHLVOffsetX = (mEditPreviewDuration / 1000 - mVideoRangeSeekBar.getRangeEnd());
                        }
                    }
                    setRangeTextView(mHLVOffsetX);
                    if (mLastX != mVideoRangeSeekBar.getRangeStart() + mHLVOffsetX) {
                        //do not need to effect
//                        rangeLoopPreview();
                        mLastX = mVideoRangeSeekBar.getRangeStart() + mHLVOffsetX;
                    }
                }
            });
        }
    };


    //===========================================音效===============================================

    private int mSoundCureenIndex=0;//当前显示的界面
    private View[]  mTabLines=null;
    private View[]  mContentViews=null;
    private TextView[]  mTabTitles=null;

    /**
     * 初始化音效界面UI
     */
    private void initSoundInitUI() {
        mContentViews=new View[2];//2个界面
        mTabLines=new View[2];//底部指示器
        mTabTitles=new TextView[2];//标题
        //四个界面
        View media_edit_sound_change = findViewById(R.id.media_edit_sound_change);
        View media_edit_sound_reverb = findViewById(R.id.media_edit_sound_reverb);
        mContentViews[0]=media_edit_sound_change;
        mContentViews[1]=media_edit_sound_reverb;
        //菜单
        LinearLayout ll_change_sound = (LinearLayout) findViewById(R.id.ll_change_sound);
        LinearLayout ll_change_rever = (LinearLayout) findViewById(R.id.ll_change_rever);

        //菜单文字
        TextView tv_change_sound = (TextView) findViewById(R.id.tv_change_sound);
        TextView tv_change_rever = (TextView) findViewById(R.id.tv_change_rever);

        mTabTitles[0]=tv_change_sound;
        mTabTitles[1]=tv_change_rever;

        //菜单指示器
        View line_change_sound = findViewById(R.id.line_change_sound);
        View line_change_rever = findViewById(R.id.line_change_rever);

        mTabLines[0]=line_change_sound;
        mTabLines[1]=line_change_rever;

        View.OnClickListener onSeniorClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int poistion=0;
                switch (view.getId()) {
                    //变声
                    case R.id.ll_change_sound:
                        poistion=0;
                        break;
                    //混响
                    case R.id.ll_change_rever:
                        poistion=1;
                        break;
                }
                changeSeniorView(poistion);
            }
        };

        ll_change_sound.setOnClickListener(onSeniorClickListener);
        ll_change_rever.setOnClickListener(onSeniorClickListener);

        mContentViews[mSoundCureenIndex].setVisibility(View.VISIBLE);
        mTabLines[mSoundCureenIndex].setVisibility(View.VISIBLE);
        mTabTitles[mSoundCureenIndex].setSelected(true);
        //初始化变声
        initSoundEffectView();
    }



    /**
     * 切换显示的界面
     * @param poistion
     */
    private void changeSeniorView(int poistion) {
        mTabLines[mSoundCureenIndex].setVisibility(View.GONE);
        mContentViews[mSoundCureenIndex].setVisibility(View.GONE);
        mTabTitles[mSoundCureenIndex].setSelected(false);
        mContentViews[poistion].setVisibility(View.VISIBLE);
        mTabLines[poistion].setVisibility(View.VISIBLE);
        mTabTitles[poistion].setSelected(true);

        mSoundCureenIndex=poistion;
        //混响
        if(1==poistion){
            //只初始化一次
            if(null== mMediaEditSoundReverAdapter){
                initSoundReverberationView();
            }
        }
    }



    //===========================================变声===============================================

    private int cureenSoundEffectIndex=0;
    /**
     * 初始化变声
     */
    private void initSoundEffectView() {
        RecyclerView soundRecylerView = (RecyclerView) findViewById(R.id.sound_change_recycler);
        soundRecylerView.setLayoutManager(new LinearLayoutManager(MediaEditActivity.this,LinearLayoutManager.HORIZONTAL,false));
        soundRecylerView.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        List<MediaSoundFilter> soundFilterData = DataFactory.getSoundFilterData(0);//变声
        if(null!=soundFilterData&&soundFilterData.size()>0){
            MediaSoundFilter mediaSoundFilter=new MediaSoundFilter();
            mediaSoundFilter.setIcon(R.drawable.ic_filter_square_empty);
            mediaSoundFilter.setName("无变声");
            mediaSoundFilter.setSelector(true);
            soundFilterData.add(0,mediaSoundFilter);
        }
        mMediaEditSoundFilter = new MediaRecordSoundFilter(soundFilterData);
        soundRecylerView.setAdapter(mMediaEditSoundFilter);
        soundRecylerView.setHasFixedSize(true);
        //对变声的监听
        soundRecylerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switchSoundEffectType(position);
            }
        });
    }

    private void switchSoundEffectType(int position) {
        if(position==cureenSoundEffectIndex){
            return;
        }
        //切换UI
        if(null!= mMediaEditSoundFilter){
            List<MediaSoundFilter> data = mMediaEditSoundFilter.getData();
            if(null!=data&&data.size()>0){
                //先还原刚才选中的
                for (int i = 0; i < data.size(); i++) {
                    if(cureenSoundEffectIndex==i){
                        data.get(i).setSelector(false);
                        break;
                    }
                }
                mMediaEditSoundFilter.notifyItemChanged(cureenSoundEffectIndex);//刷新原来选中的为未选中
                //再设置新的选中的
                for (int i = 0; i < data.size(); i++) {
                    if(position==i){
                        data.get(position).setSelector(true);
                        break;
                    }
                }
                mMediaEditSoundFilter.notifyItemChanged(position);
                cureenSoundEffectIndex = position;
            }
        }
        //0 为无变声
        mAudioEffectType = SOUND_CHANGE_TYPE[position];
        addAudioFilter();
    }

    //===========================================混响===============================================

    private int cureenReverberationIndex=0;

    /**
     * 初始化混响
     */
    private void initSoundReverberationView() {

        RecyclerView soundRecylerView = (RecyclerView) findViewById(R.id.media_edit_reverb_recycler);
        soundRecylerView.setLayoutManager(new LinearLayoutManager(MediaEditActivity.this,LinearLayoutManager.HORIZONTAL,false));
        soundRecylerView.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        soundRecylerView.setHasFixedSize(true);
        List<MediaSoundFilter> soundFilterData = DataFactory.getSoundFilterData(1);//混响
        if(null!=soundFilterData&&soundFilterData.size()>0){
            MediaSoundFilter mediaSoundFilter=new MediaSoundFilter();
            mediaSoundFilter.setIcon(R.drawable.ic_filter_square_empty);
            mediaSoundFilter.setName("无混响");
            mediaSoundFilter.setSelector(true);
            soundFilterData.add(0,mediaSoundFilter);
        }
        mMediaEditSoundReverAdapter = new MediaRecordSoundFilter(soundFilterData);
        soundRecylerView.setAdapter(mMediaEditSoundReverAdapter);

        //对混响的监听
        soundRecylerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switchSoundReverberationType(position);
            }
        });
    }

    /**
     * 混响的选中项处理
     * @param position
     */
    private void switchSoundReverberationType(int position) {
        if(position==cureenReverberationIndex){
            return;
        }
        //切换UI
        if(null!= mMediaEditSoundReverAdapter){
            List<MediaSoundFilter> data = mMediaEditSoundReverAdapter.getData();
            if(null!=data&&data.size()>0){
                //先还原刚才选中的
                for (int i = 0; i < data.size(); i++) {
                    if(cureenReverberationIndex==i){
                        data.get(i).setSelector(false);
                        break;
                    }
                }
                mMediaEditSoundReverAdapter.notifyItemChanged(cureenReverberationIndex);//刷新原来选中的为未选中
                //再设置新的选中的
                for (int i = 0; i < data.size(); i++) {
                    if(position==i){
                        data.get(position).setSelector(true);
                        break;
                    }
                }
                mMediaEditSoundReverAdapter.notifyItemChanged(position);
                cureenReverberationIndex = position;
            }
        }
        //0 为无混响
        mAudioReverbType = REVERB_TYPE[position];
        addAudioFilter();
    }


    /**
     * 添加音频滤镜，支持变声和混响同时生效
     */
    private void addAudioFilter() {
        KSYAudioEffectFilter effectFilter;
        AudioReverbFilter reverbFilter;
        List<AudioFilterBase> filters = new LinkedList<>();
        if (mAudioEffectType != AUDIO_FILTER_DISABLE) {
            effectFilter = new KSYAudioEffectFilter
                    (mAudioEffectType);
            filters.add(effectFilter);
        }
        if (mAudioReverbType != AUDIO_FILTER_DISABLE) {
            reverbFilter = new AudioReverbFilter();
            reverbFilter.setReverbLevel(mAudioReverbType);
            filters.add(reverbFilter);
        }
        if (filters.size() > 0) {
            mEditKit.getAudioFilterMgt().setFilter(filters);
        } else {
            mEditKit.getAudioFilterMgt().setFilter((AudioFilterBase) null);
        }
    }



    private void setBeautyFilter() {
        if (mImgBeautyProFilter == null) {
            mImgBeautyProFilter = new ImgBeautyProFilter(mEditKit.getGLRender(), MediaEditActivity.this);
            addImgFilter();
        }
    }

    private void setEffectFilter(int type) {
        mEffectFilterIndex = type;
        addImgFilter();
    }

    private void resumeEditPreview() {
        if(null!=mEditKit) mEditKit.resumeEditPreview();
        changePlayButton(true);
        mImgBeautyProFilter=null;
        setBeautyFilter();
    }

    /**
     * 还原加载素材状态图标
     */
    private void hideStickerLoading() {
        if(null!=drawableAnimation){
            if(drawableAnimation.isRunning()) drawableAnimation.stop();
            drawableAnimation=null;
        }
        if(null!=btnLoading){
            btnLoading.setImageResource(0);
            btnLoading.setVisibility(View.GONE);
        }
        findViewById(R.id.tv_error_sticker_tips).setVisibility(View.GONE);
    }


    //=========================================获取贴纸素材回调=======================================
    /**
     * 获取的列表
     * @param data
     */
    @Override
    public void showStickerList(StickerListInfo data) {
        hideStickerLoading();
        if(null!=mStickerListInfoList) mStickerListInfoList.clear();
        mStickerListInfoList=data.getData();
        if(null!=mStickerListInfoList&&mStickerListInfoList.size()>0){
            initStickerView();
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MEDIA_EDIT_STICKER_LIST);
            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_MEDIA_EDIT_STICKER_LIST, (Serializable) mStickerListInfoList,Constant.CACHE_STICKER_TIME);
        }
    }



    @Override
    public void showStickerEmpty(String data) {
        hideStickerLoading();
    }

    @Override
    public void showStickerError(String data) {
        if(null!=drawableAnimation){
            if(drawableAnimation.isRunning()) drawableAnimation.stop();
            drawableAnimation=null;
        }
        if(null==mStickerListInfoList||mStickerListInfoList.size()<=0){
            if(null!=btnLoading){
                btnLoading.setImageResource(R.drawable.ic_refresh);
            }
            findViewById(R.id.tv_error_sticker_tips).setVisibility(View.VISIBLE);
        }else{
            if(null!=btnLoading){
                btnLoading.setImageResource(0);
            }
        }
    }


    //========================================获取字幕素材回调========================================
    @Override
    public void showCaptionsList(CaptionsInfo data) {}

    @Override
    public void showCaptionsEmpty(String data) {}

    @Override
    public void showCaptionsError(String data) {}

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    protected void showProgressDialog(String message,boolean isProgress){
        if(!MediaEditActivity.this.isFinishing()){
            if(null==mLoadingProgressedView){
                mLoadingProgressedView = new LoadingProgressView(this,isProgress);
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }
    /**
     * 关闭进度框
     */
    public void closeProgressDialog(){
        try {
            if(!MediaEditActivity.this.isFinishing()){
                if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()){
                    mLoadingProgressedView.dismiss();
                    mLoadingProgressedView=null;
                }
            }
        }catch (Exception e){}
    }
}
