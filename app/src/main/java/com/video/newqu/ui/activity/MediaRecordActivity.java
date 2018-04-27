package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckedTextView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.shortvideo.kit.KSYRecordKit;
import com.ksyun.media.shortvideo.mv.KSYMVInfo;
import com.ksyun.media.streamer.capture.CameraCapture;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.filter.audio.AudioFilterBase;
import com.ksyun.media.streamer.filter.audio.AudioReverbFilter;
import com.ksyun.media.streamer.filter.audio.KSYAudioEffectFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySoftFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautySpecialEffectsFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyStylizeFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterBase;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.logstats.StatsLogReport;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.TopBaseActivity;
import com.video.newqu.bean.MediaFilterInfo;
import com.video.newqu.bean.MediaMVInfo;
import com.video.newqu.bean.MediaSoundFilter;
import com.video.newqu.camera.adapter.MediaEditBeautyAdapter;
import com.video.newqu.camera.adapter.MediaMVAdapter;
import com.video.newqu.camera.adapter.MediaRecordFilterAdapter;
import com.video.newqu.camera.adapter.MediaRecordSoundFilter;
import com.video.newqu.camera.config.ShortVideoConfig;
import com.video.newqu.camera.constant.Constants;
import com.video.newqu.camera.recordclip.RecordProgressController;
import com.video.newqu.camera.recordclip.RecordProgressView;
import com.video.newqu.camera.util.DataFactory;
import com.video.newqu.camera.util.FileUtils;
import com.video.newqu.camera.view.CameraHintView;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemClickListener;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.PerfectClickViewListener;
import com.video.newqu.model.HorzontalSpacesItemDecoration;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.manager.ActivityCollectorManager;
import com.video.newqu.util.AnimationUtil;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.UnZipTask;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.FiltrateRelativeLayout;
import com.video.newqu.view.widget.EffectsButton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TinyHung@outlook.com
 * 2017-08-19 19:38
 * 集成金山云的短视频录制界面
 */

public class MediaRecordActivity extends TopBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private  final int MESSAGE_COMPLETED = 100;//视频合并完成
    private final  int PERMISSION_REQUEST_CAMERA_AUDIOREC = 1;   //摄像头、麦克风请求授权的请求码
    private  final int REQUEST_CODE = 10010;
    private  final int AUDIO_FILTER_DISABLE = 0;  //不使用音频滤镜的类型标志
    private int mAudioEffectType = AUDIO_FILTER_DISABLE;  //变声类型
    private int mAudioReverbType = AUDIO_FILTER_DISABLE;  //混响类型
    //变声类型数组常量,0为无变声
    private static  int[] SOUND_CHANGE_TYPE = {0,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_PITCH,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_FEMALE,KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_MALE, KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_HEROIC, KSYAudioEffectFilter.AUDIO_EFFECT_TYPE_ROBOT};
    //混响类型数组常量 0为无混响
    private static  int[] REVERB_TYPE = {0,AudioReverbFilter.AUDIO_REVERB_LEVEL_1, AudioReverbFilter.AUDIO_REVERB_LEVEL_2, AudioReverbFilter.AUDIO_REVERB_LEVEL_3, AudioReverbFilter.AUDIO_REVERB_LEVEL_4,AudioReverbFilter.AUDIO_REVERB_LEVEL_5};
    private  final int FILTER_DISABLE = 0;
    private GLSurfaceView mCameraPreviewView;
    private CameraHintView mCameraHintView;
    private Chronometer mChronometer;
    private ImageView mRecordView;
    private CheckedTextView mBackView;
    private ImageView mNextView;
    private View mDefaultRecordBottomLayout;
    //音效
    private View mViewSoundEffectLayout;
    //美颜
    private View mRecord_fair_layout;
    //滤镜
    private View mRecord_filter_layout;
    //MV
    private View mRecord_mv_layout;
    private MediaMVAdapter mMediaMVAdapter=null;
    //右侧菜单
    private View mRecord_right_menu_layout;
    //断点拍摄进度控制
    private RecordProgressController mRecordProgressCtl;
    //录制kit
    private KSYRecordKit mKSYRecordKit=null;
    private int mEffectFilterIndex = FILTER_DISABLE;  //滤镜filter type
    private int mLastEffectFilterIndex = FILTER_DISABLE;  //滤镜filter type
    private Map<Integer, ImgFilterBase> mEffectFilters=null;
    private Map<Integer, ImgFilterBase> mBeautyFilters=null;
    private int mFilterTypeIndex = 0;
    //美颜
    private  final int BEAUTY_DISABLE = 100;
    private  final int BEAUTY_NATURE = 101;
    private  final int BEAUTY_PRO = 102;
    private  final int BEAUTY_FLOWER_LIKE = 103;
    private  final int BEAUTY_DELICATE = 104;
    private int mLastImgBeautyTypeIndex = BEAUTY_DISABLE;  //美颜type
    private ImgBeautyProFilter mImgBeautyProFilter=null;  //美颜filter
    private MediaEditBeautyAdapter mMediaEditBeautyAdapter=null;
    private Handler mMainHandler;
    private boolean mIsFileRecording = false;
    private boolean mIsFlashOpened = false;
    private String mRecordUrl;
    private boolean mHWEncoderUnsupported;  //硬编支持标志位
    private boolean mSWEncoderUnsupported;  //软编支持标志位
    private View mPreRecordConfigLayout;
    private LoadingProgressView mLoadingProgressedView;
    private TextView mTimeCountDownText;
    private EffectsButton mBtn_record_delayed;
    private EffectsButton mBtn_flash;
    private EffectsButton mBtn_camera_fail;
    //数据模型
    private MediaRecordSoundFilter mMediaEditSoundFilter;//变声适配器
    private MediaRecordSoundFilter mMediaEditSoundReverAdapter;//混响适配器
    private int mCureentFilterPoistion=0;//选中的滤镜下标
    private int mCureentBuayePoistion=0;//选中的美颜下标
    //滤镜
    private MediaRecordFilterAdapter mMediaRecordFilterAdapter;
    private RadioGroup mBottomMenu;
    private String mMusicID="";
    private String mMusicPath="";
    private KSYMediaPlayer mMediaPlayer;
    private LinearLayout mLl_top_tab_controlle;
    private EffectsButton mBtn_record_music;
    private final static int PERMISSION_REQUEST_STORAGE = 1;
    private float fps=24F;
    private TextView mTipsTextView=null;//第一次使用功能引导提示


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollectorManager.addActivity(this);
        setContentView(R.layout.activity_media_record);
        //must set
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();
        mMainHandler = new Handler();

        //根据手机性能调整视频录制参数
        ShortVideoConfig config = new ShortVideoConfig();
        //高性能手机
        if(ScreenUtils.getScreenWidth()>=1080&&SystemUtils.getNumCores()>=4){
            config.resolution=StreamerConstants.VIDEO_RESOLUTION_1080P;
            config.fps = 30.0F;//采集帧率
        }else{
            config.resolution=StreamerConstants.VIDEO_RESOLUTION_720P;
            config.fps = 24.0F;//采集帧率
        }
        fps=config.fps;
        config.encodeType=AVConst.CODEC_ID_AVC;
        config.encodeMethod= StreamerConstants.ENCODE_METHOD_HARDWARE;//硬编  StreamerConstants.ENCODE_METHOD_SOFTWARE：软编
        config.encodeProfile= VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE;//编码性能，高； VideoEncodeFormat.ENCODE_PROFILE_BALANCE 平衡
        config.videoBitrate = StreamerConstants.DEFAULT_INIT_VIDEO_BITRATE;//视频码率
        config.audioBitrate = StreamerConstants.DEFAULT_AUDIO_BITRATE;//音频码率
        mKSYRecordKit.setPreviewFps(config.fps);//视频采集帧率
        mKSYRecordKit.setTargetFps(config.fps);//视频预览帧率
        mKSYRecordKit.setVideoKBitrate(config.videoBitrate);//视频采样率
        mKSYRecordKit.setAudioKBitrate(config.audioBitrate);//音频质量
        mKSYRecordKit.setPreviewResolution(config.resolution);//分辨率
        mKSYRecordKit.setTargetResolution(config.resolution);//预览分辨率
        mKSYRecordKit.setVideoCodecId(config.encodeType);//编码方式
        mKSYRecordKit.setEncodeMethod(config.encodeMethod);//编码方法
        mKSYRecordKit.setVideoEncodeProfile(config.encodeProfile);//视频录制的质量
        //默认只使用竖屏
        mKSYRecordKit.setRotateDegrees(0);
        mKSYRecordKit.setDisplayPreview(mCameraPreviewView);
        mKSYRecordKit.setEnableRepeatLastFrame(false);
        mKSYRecordKit.setCameraFacing(CameraCapture.FACING_FRONT);
        mKSYRecordKit.setFrontCameraMirror(true);
        mKSYRecordKit.setOnInfoListener(mOnInfoListener);
        mKSYRecordKit.setOnErrorListener(mOnErrorListener);
        mKSYRecordKit.setOnLogEventListener(mOnLogEventListener);
        mKSYRecordKit.setAudioOnly(false);
        mKSYRecordKit.setVoiceVolume(1.0f);
        // touch focus and zoom support
        CameraTouchHelper cameraTouchHelper = new CameraTouchHelper();
        cameraTouchHelper.setCameraCapture(mKSYRecordKit.getCameraCapture());
        mCameraPreviewView.setOnTouchListener(cameraTouchHelper);
        // set CameraHintView to show focus rect and zoom ratio
        cameraTouchHelper.setCameraHintView(mCameraHintView);
        startCameraPreviewWithPermCheck();
    }


    /**
     * 初始化
     */
    private void initViews() {
        mKSYRecordKit = new KSYRecordKit(this);
        //自定义控件初始化
        mCameraHintView = (CameraHintView) findViewById(R.id.camera_hint);
        mCameraPreviewView = (GLSurfaceView) findViewById(R.id.camera_preview);
        //实时记录展开的View,默认底部控制器是展开的
        mDefaultRecordBottomLayout = findViewById(R.id.default_bottom_layout);
        mPreRecordConfigLayout = mDefaultRecordBottomLayout;
        //音效
        mViewSoundEffectLayout = findViewById(R.id.media_sound_layout);
        //美颜效果列表
        mRecord_fair_layout = findViewById(R.id.record_fair_layout);
        //滤镜列表
        mRecord_filter_layout = findViewById(R.id.media_edit_filter_choose);
        //MV
        mRecord_mv_layout = findViewById(R.id.media_mv_choose);

        //拍摄
        mRecordView = (ImageView) findViewById(R.id.click_to_record);
        mRecordView.setImageResource(R.drawable.record_controller_seletor);
        //回删
        mBackView = (CheckedTextView) findViewById(R.id.click_to_back);
        //完成
        mNextView = (ImageView) findViewById(R.id.click_to_next);
        mTimeCountDownText = (TextView) findViewById(R.id.tv_camera_time_number);//倒计时按钮

        //所有的动画菜单点击项
        EffectsButton.OnClickEffectButtonListener onEffectButtonClickListener=new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton(EffectsButton view) {
                switch (view.getId()) {
                    //返回
                    case R.id.btn_close:
                        onBackPressed();
                        break;
                    //延时
                    case R.id.btn_record_delayed:
                        mBtn_record_delayed.setActivated(!mBtn_record_delayed.isActivated());
                        break;
                    //闪光灯
                    case R.id.btn_flash:
                        onFlashClick();
                        break;
                    //相机切换
                    case R.id.btn_switch_cam:
                        onSwitchCamera();
                        break;
                    //音效
                    case R.id.btn_record_sound:
                        invisibleRightMenuView();
                        if(mViewSoundEffectLayout.getVisibility()!=View.VISIBLE){
                            showView(mViewSoundEffectLayout);
                        }
                        if(null==mContentViews&&null==mMediaEditSoundFilter){
                            initSeniorInitUI();//初始化音效，只初始化一次
                        }
                        break;
                    //滤镜
                    case R.id.btn_record_filter:
                        invisibleRightMenuView();
                        showView(mRecord_filter_layout);
                        if(null==mMediaRecordFilterAdapter){
                            initFilterUI();//初始化滤镜列表
                        }
                        break;
                    //音乐
                    case R.id.btn_record_music:
                        //需要在回删掉所有录制的片段才允许更换音乐
                        if(null!=mTipsTextView&&mTipsTextView.getVisibility()!=View.GONE){
                            mTipsTextView.setVisibility(View.GONE);
                            mTipsTextView=null;
                        }
                        if(null!=mKSYRecordKit&&mKSYRecordKit.getRecordedFilesCount()<=0){
                            Intent intent=new Intent(MediaRecordActivity.this,MediaMusicActivity.class);
                            startActivityForResult(intent,Constant.MEDIA_START_MUSIC_REQUEST_CODE);
                            overridePendingTransition(R.anim.menu_enter, 0);//进场动画
                        }else{
                            ToastUtils.showCenterToast("录制途中不能更换音乐，要更换音乐请先回删所有录制片段");
                        }
                        break;
                    //美颜
                    case R.id.btn_camera_fail:
                        invisibleRightMenuView();
                        showView(mRecord_fair_layout);
                        break;
                    //MV特效
                    case R.id.btn_record_mv:
                        invisibleRightMenuView();
                        showView(mRecord_mv_layout);
                        if(null==mMediaMVAdapter){
                            initMVView();//初始化MV
                        }
                        break;
                }
            }
        };

        ((EffectsButton) findViewById(R.id.btn_close)).setOnClickEffectButtonListener(onEffectButtonClickListener);
        mBtn_record_music = (EffectsButton) findViewById(R.id.btn_record_music);
        mBtn_record_music.setOnClickEffectButtonListener(onEffectButtonClickListener);
        ((EffectsButton) findViewById(R.id.btn_record_filter)).setOnClickEffectButtonListener(onEffectButtonClickListener);
        ((EffectsButton) findViewById(R.id.btn_record_mv)).setOnClickEffectButtonListener(onEffectButtonClickListener);
        ((EffectsButton) findViewById(R.id.btn_switch_cam)).setOnClickEffectButtonListener(onEffectButtonClickListener);
        ((EffectsButton) findViewById(R.id.btn_record_sound)).setOnClickEffectButtonListener(onEffectButtonClickListener);
        mBtn_camera_fail = (EffectsButton) findViewById(R.id.btn_camera_fail);
        mBtn_flash = (EffectsButton) findViewById(R.id.btn_flash);
        mBtn_record_delayed = (EffectsButton) findViewById(R.id.btn_record_delayed);
        mBtn_record_delayed.setActivated(false);//延时默认不开启
        mBtn_camera_fail.setOnClickEffectButtonListener(onEffectButtonClickListener);
        mBtn_flash.setOnClickEffectButtonListener(onEffectButtonClickListener);
        mBtn_record_delayed.setOnClickEffectButtonListener(onEffectButtonClickListener);
        initFairView();//初始化美颜列表
        //默认为用户开启自然美颜效果
        switchBeautyFilterType(1);
        //避免在1秒内多次点击
        PerfectClickViewListener perfectClickListener=new PerfectClickViewListener() {
            @Override
            protected void onClickView(View view) {
                switch (view.getId()) {
                    case R.id.btn_record_mv_close:
                    case R.id.btn_record_filter_close:
                    case R.id.btn_record_sound_close:
                    case R.id.btn_record_fair_close:
                        onBackPressed();
                        break;
                    //录制
                    case R.id.click_to_record:
                        onRecordClick();
                        break;
                    //回删
                    case R.id.click_to_back:
                        onBackoffClick();
                        break;
                    //下一步
                    case R.id.click_to_next:
                        onNextClick();
                        break;
                    default:
                        break;
                }
            }
        };

        mRecordView.setOnClickListener(perfectClickListener);
        mBackView.setOnClickListener(perfectClickListener);
        mNextView.setOnClickListener(perfectClickListener);
        ((ImageView) findViewById(R.id.btn_record_mv_close)).setOnClickListener(perfectClickListener);
        ((ImageView) findViewById(R.id.btn_record_filter_close)).setOnClickListener(perfectClickListener);
        ((ImageView) findViewById(R.id.btn_record_sound_close)).setOnClickListener(perfectClickListener);
        ((ImageView) findViewById(R.id.btn_record_fair_close)).setOnClickListener(perfectClickListener);

        //断点拍摄UI初始化
        //mBarBottomLayout为拍摄进度显示的父控件
        RecordProgressView progressView = (RecordProgressView) findViewById(R.id.record_progress);
        mRecordProgressCtl = new RecordProgressController(progressView);
        //拍摄时长变更回调
        mRecordProgressCtl.setRecordingLengthChangedListener(mRecordLengthChangedListener);
        mRecordProgressCtl.start();
        mBackView.setChecked(false);


        //录制变速
        mBottomMenu = (RadioGroup) findViewById(R.id.bottomMenu);
        mBottomMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.rb_item1:
                        onSpeedClick(0.5f);
                        break;
                    case R.id.rb_item2:
                        onSpeedClick(0.5f);
                        break;
                    case R.id.rb_item3:
                        onSpeedClick(1.0f);
                        break;
                    case R.id.rb_item4:
                        onSpeedClick(1.5f);
                        break;
                    case R.id.rb_item5:
                        onSpeedClick(2.0f);
                        break;
                }
            }
        });
        ((RadioButton) findViewById(R.id.rb_item3)).setChecked(true);
        onSpeedClick(1.0f);//默认是选中标准速度的
        //顶部控制器
        mLl_top_tab_controlle = (LinearLayout) findViewById(R.id.ll_top_tab_controlle);
        //右侧菜单
        mRecord_right_menu_layout= findViewById(R.id.record_right_menu_layout);
        //计时器
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        //过滤点击事件，将触摸事件交给 CameraHintView 处理
        ((FiltrateRelativeLayout ) findViewById(R.id.filtra_layout)).setOnClickListener(new FiltrateRelativeLayout.OnClickListener() {
            @Override
            public void onClickView(View view) {
                if (null!=mDefaultRecordBottomLayout&&mDefaultRecordBottomLayout.getVisibility() != View.VISIBLE) {
                    if (mPreRecordConfigLayout.getVisibility() == View.VISIBLE) {
                        mPreRecordConfigLayout.setVisibility(View.INVISIBLE);
                    }
                    showRightViewFromBottomToTop(mRecord_right_menu_layout);
                    showViewFromBottomToTop(mDefaultRecordBottomLayout);
                    mPreRecordConfigLayout = mDefaultRecordBottomLayout;
                }
            }
        });
        //第一次使用弹出使用提示
        if(1!= SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_RECORD_CODE)){
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
            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_RECORD_CODE,1);
        }
    }

    private void invisibleRightMenuView() {
        if(null!=mRecord_right_menu_layout){
            mRecord_right_menu_layout.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 显示View
     * @param view
     */
    private void showView(View view) {
        if(null!=view){
            mPreRecordConfigLayout.setVisibility(View.GONE);
            showViewFromBottomToTop(view);
        }
        mPreRecordConfigLayout=view;
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

    /**
     * 显示右侧菜单View,由上而下
     * @param view
     */
    private void showRightViewFromBottomToTop(View view) {
        if(null!=view&&view.getVisibility()!=View.VISIBLE){
            view.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTopLocation();
            view.startAnimation(translateAnimation);
        }
    }

    /**
     * 隐藏View,从上而下
     * @param view
     */
    private void goneViewFromTopToBottom(final View view) {
        if(null!=view&&view.getVisibility()!=View.GONE){
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTop();
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
     * 设置录制视频速度
     * @param speed
     */
    private void onSpeedClick(float speed) {
        if(null!=mKSYRecordKit) mKSYRecordKit.setRecordSpeed(speed);
//        updateSpeedVolume();
    }


    /**
     * 变速录制时建议将原声静音，否则在添加背景音乐后效果不太好
     */
    private void updateSpeedVolume() {
        if (mKSYRecordKit.getRecordSpeed() != 1.0f ) {
            mKSYRecordKit.setVoiceVolume(0.0f);
            ToastUtils.showCenterToast("选择变速录制自动禁用了声音录制!");
        } else {
            //当用户没有选中背景音乐的时候才打开声音录制
            if(TextUtils.isEmpty(mMusicPath)&&null==mMediaPlayer){
                mKSYRecordKit.setVoiceVolume(1.0f);
            }
        }
    }


    //========================================初始化MV数据和列表======================================

    public static final String MV_ASSETS_SUB_PATH = "MVResource";
    private static final String MV_ICON_NAME = "icon.png";  //zip包中mv的缩略图文件名
    public static final String ZIP_INFO = ".zip";
    //mv
    private ArrayList<String> mMVPaths;  //各个mv资源的路径
    private String[] mMVFileNames;  //各mv资源zip包名存储数组
    private LinkedHashMap<String, KSYMVInfo> mMVs = new LinkedHashMap<>();
    // mv解析后的配置信息，避免重复解析mv的配置文件
    private int mCureentMVPoistion=0;


    /**
     * 初始化MV列表
     */
    private void initMVView() {
        List<MediaMVInfo> mvInfoList=new ArrayList<>();
        try {
            InputStream is = VideoApplication.getInstance().getAssets().open("content/mv_default.png");
            if(null!=is){
                Drawable icon = Drawable.createFromStream(is, "mv_default");
                mvInfoList.add(new MediaMVInfo("无场景",icon,true,0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecyclerView filter_mv_recyclerView = (RecyclerView) findViewById(R.id.filter_mv_recyclerView);
        filter_mv_recyclerView.setLayoutManager(new LinearLayoutManager(MediaRecordActivity.this,LinearLayoutManager.HORIZONTAL,false));
        filter_mv_recyclerView.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        filter_mv_recyclerView.setHasFixedSize(true);
        mMediaMVAdapter = new MediaMVAdapter(mvInfoList);
        filter_mv_recyclerView.setAdapter(mMediaMVAdapter);
        filter_mv_recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switchMVFilterType(position);
            }
        });
        //读取Assets下面MVResource所有的资源文件，并把zip包名除.zip外其它作为mv的名称及解压后的路径名
        //因此需要资源包名称和zip包名相同
        mMVFileNames = new String[0];
        mMVPaths = new ArrayList<>();
        try {
            mMVFileNames = this.getAssets().list(MV_ASSETS_SUB_PATH);
            for (String name : mMVFileNames) {
                mMVPaths.add(FileUtils.getAvailableMVPath(this) + File.separator +
                        name.substring(0, name.length() - ZIP_INFO.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < mMVPaths.size(); i++) {
            final String mvPath = mMVPaths.get(i);
            final String mvZipName = mMVFileNames[i];

            File file = new File(mvPath);
            if (file.exists()) {
                boolean isSame = true;
                //zip包是否需要重新copy解压
                try {
                    InputStream in = getAssets().open(MV_ASSETS_SUB_PATH + File.separator +
                            mMVFileNames[i]);
                    String newFileString = FileUtils.getFileMD5(in);

                    String localPath = mvPath + ZIP_INFO;
                    File localFile = new File(localPath);
                    String localFileMD5 = FileUtils.getFileMD5(localFile);
                    isSame = newFileString.equals(localFileMD5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isSame) {
                    addMVData(mvPath, mvZipName.substring(0, mvZipName.length() - ZIP_INFO.length()));
                } else {
                    FileUtils.reloadMVResourceFromAssets(getApplicationContext(), mvZipName,
                            new UnZipTask.OnProcessListener() {
                                @Override
                                public void onFinish(String filePath, String fileName) {
                                    addMVData(filePath, fileName);
                                }
                            });
                }
            } else {
                FileUtils.reloadMVResourceFromAssets(getApplicationContext(), mvZipName,
                        new UnZipTask.OnProcessListener() {
                            @Override
                            public void onFinish(String filePath, String fileName) {
                                addMVData(filePath, fileName);
                            }
                        });
            }
        }
    }

    private void addMVData(String filePath, String fileName) {
        if(null!=mMediaMVAdapter){
            Drawable image = FileUtils.getMVImage(filePath + File.separator + MV_ICON_NAME);
            MediaMVInfo mediaMVInfo=new MediaMVInfo(fileName,image,false,0);
            mMediaMVAdapter.addSingerData(mediaMVInfo);
        }
    }


    /**
     * 切换选中的美颜类型
     * @param poistion
     */
    private void switchMVFilterType(int poistion) {
        if(poistion==mCureentMVPoistion||null==mKSYRecordKit){
            return;
        }
        //切换UI
        if(null!= mMediaMVAdapter){
            List<MediaMVInfo> data = mMediaMVAdapter.getData();
            if(null!=data&&data.size()>0){
                //先还原刚才选中的
                for (int i = 0; i < data.size(); i++) {
                    if(mCureentMVPoistion==i){
                        data.get(i).setSelector(false);
                        break;
                    }
                }
                mMediaMVAdapter.notifyItemChanged(mCureentMVPoistion);//刷新原来选中的为未选中
                //再设置新的选中的
                for (int i = 0; i < data.size(); i++) {
                    if(poistion==i){
                        data.get(poistion).setSelector(true);
                        break;
                    }
                }
                mMediaMVAdapter.notifyItemChanged(poistion);
                mCureentMVPoistion = poistion;
                if(0==mCureentMVPoistion){
                    if(null!=mKSYRecordKit) mKSYRecordKit.applyMV(null);
                }else{
                    MediaMVInfo mediaMVInfo = data.get(mCureentMVPoistion);
                    if(null!=mediaMVInfo){
                        int index = 0;
                        for (int i = 0; i < mMVFileNames.length; i++) {
                            if (mMVFileNames[i].equals(mediaMVInfo.getTitle() + ZIP_INFO)) {
                                index = i;
                            }
                        }
                        final String mvPath = mMVPaths.get(index);
                        File file = new File(mvPath);
                        if (file.exists()) {
                            if (mMVs.containsKey(mediaMVInfo.getTitle())){
                                KSYMVInfo ksymvInfo = mMVs.get(mediaMVInfo.getTitle());
                                if(null!=mKSYRecordKit) mKSYRecordKit.applyMV(ksymvInfo);
                            } else {
                                KSYMVInfo ksymvInfo = new KSYMVInfo(mvPath);
                                mMVs.put(mediaMVInfo.getTitle(),ksymvInfo);
                                if(null!=mKSYRecordKit) mKSYRecordKit.applyMV(ksymvInfo);
                            }
                        }
                    }
                }
            }
        }
    }


    //========================================处理美颜的选中事件======================================

    /**
     * 初始化美颜列表
     */
    private void initFairView() {

        RecyclerView record_recycler_fair = (RecyclerView) findViewById(R.id.record_recycler_bueay);
        List<MediaFilterInfo> mediaFilterData = DataFactory.getMediaBeautyData(this);
        if(null!=mediaFilterData&&mediaFilterData.size()>0){
            mediaFilterData.add(0,new MediaFilterInfo("无",R.drawable.beauty_origin,true,100));//添加一个默认没有美颜效果的项
        }
        record_recycler_fair.setLayoutManager(new LinearLayoutManager(MediaRecordActivity.this,LinearLayoutManager.HORIZONTAL,false));
        record_recycler_fair.addItemDecoration(new HorzontalSpacesItemDecoration(Utils.dip2px(10)));
        record_recycler_fair.setHasFixedSize(true);
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
                mBtn_camera_fail.setBackgroundResource(mCureentBuayePoistion<=0?R.drawable.ic_record_fair_noi:R.drawable.ic_edit_fair_pre);
                MediaFilterInfo mediaFilterInfo = data.get(mCureentBuayePoistion);
                if(null!=mediaFilterInfo){
                    addBeautyFilter(mediaFilterInfo.getId());
                }
            }
        }
    }

    /**
     * 添加美颜
     * @param beautyFilterId
     */
    private void addBeautyFilter(int beautyFilterId) {

        if (mBeautyFilters == null) {
            mBeautyFilters = new LinkedHashMap<>();
        }

        //默认的没有美颜效果
        if (beautyFilterId == BEAUTY_DISABLE) {
            if (mBeautyFilters.containsKey(mLastImgBeautyTypeIndex)) {
                ImgFilterBase lastFilter = mBeautyFilters.get(mLastImgBeautyTypeIndex);
                if (mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(lastFilter)) {
                    mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastFilter, null);
                }
            }
            mLastImgBeautyTypeIndex = beautyFilterId;
            return;
        }
        //enable filter
        if (mBeautyFilters.containsKey(beautyFilterId)) {
            ImgFilterBase filterBase = mBeautyFilters.get(beautyFilterId);
            if (mBeautyFilters.containsKey(mLastImgBeautyTypeIndex)) {
                ImgFilterBase lastFilter = mBeautyFilters.get(mLastImgBeautyTypeIndex);
                if (mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(lastFilter)) {
                    mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastFilter, filterBase);
                }
            } else {
                if (!mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(filterBase)) {
                    mKSYRecordKit.getImgTexFilterMgt().addFilter(filterBase);
                }
            }
            mLastImgBeautyTypeIndex = beautyFilterId;
            return;
        }

        ImgFilterBase filterBase = null;
        switch (beautyFilterId) {
            case BEAUTY_NATURE:
                ImgBeautySoftFilter softFilter = new ImgBeautySoftFilter(mKSYRecordKit.getGLRender());
                softFilter.setGrindRatio(0.5f);
                filterBase = softFilter;
                break;
            case BEAUTY_PRO:
                ImgBeautyProFilter proFilter = new ImgBeautyProFilter(mKSYRecordKit.getGLRender()
                        , getApplicationContext());
                proFilter.setGrindRatio(0.5f);
                proFilter.setWhitenRatio(0.5f);
                proFilter.setRuddyRatio(0);
                filterBase = proFilter;
                break;
            case BEAUTY_FLOWER_LIKE:
                ImgBeautyProFilter pro1Filter = new ImgBeautyProFilter(mKSYRecordKit.getGLRender()
                        , getApplicationContext(), 3);
                pro1Filter.setGrindRatio(0.5f);
                pro1Filter.setWhitenRatio(0.5f);
                pro1Filter.setRuddyRatio(0.15f);
                mBeautyFilters.put(BEAUTY_FLOWER_LIKE, pro1Filter);
                filterBase = pro1Filter;
                break;
            case BEAUTY_DELICATE:
                ImgBeautyProFilter pro2Filter = new ImgBeautyProFilter(mKSYRecordKit.getGLRender()
                        , getApplicationContext(), 3);
                pro2Filter.setGrindRatio(0.5f);
                pro2Filter.setWhitenRatio(0.5f);
                pro2Filter.setRuddyRatio(0.3f);
                filterBase = pro2Filter;
                break;
            case BEAUTY_DISABLE:
                break;
            default:
                break;
        }

        if (filterBase != null) {
            ImgFilterBase lastFilter = null;
            if (mBeautyFilters.containsKey(mLastImgBeautyTypeIndex)) {
                lastFilter = mBeautyFilters.get(mLastImgBeautyTypeIndex);
            }
            mBeautyFilters.put(beautyFilterId, filterBase);
            if (lastFilter != null && mKSYRecordKit.getImgTexFilterMgt().getFilter().contains
                    (lastFilter)) {
                mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastFilter, filterBase);
            } else {
                mKSYRecordKit.getImgTexFilterMgt().addFilter(filterBase);
            }
        }
        mLastImgBeautyTypeIndex = beautyFilterId;
    }




    //===========================================滤镜的处理==========================================

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
        filterRecyclerVie.setLayoutManager(new LinearLayoutManager(MediaRecordActivity.this,LinearLayoutManager.HORIZONTAL,false));
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


    private void setEffectFilter(int type) {
        mEffectFilterIndex = type;
        addEffectFilter();
    }


    private void addEffectFilter() {
        if(null==mKSYRecordKit) return;

        if (mLastEffectFilterIndex == mEffectFilterIndex) {
            return;
        }
        if (mEffectFilters == null) {
            mEffectFilters = new LinkedHashMap<>();
        }

        if (mEffectFilterIndex == FILTER_DISABLE) {
            if (mEffectFilters.containsKey(mLastEffectFilterIndex)) {
                ImgFilterBase lastFilter = mEffectFilters.get(mLastEffectFilterIndex);
                if (mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(lastFilter)) {
                    mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastFilter, null);
                }
            }
            mLastEffectFilterIndex = mEffectFilterIndex;
            return;
        }
        if (mEffectFilters.containsKey(mEffectFilterIndex)) {
            ImgFilterBase filter = mEffectFilters.get(mEffectFilterIndex);
            if (mEffectFilters.containsKey(mLastEffectFilterIndex)) {
                ImgFilterBase lastfilter = mEffectFilters.get(mLastEffectFilterIndex);
                if (mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(lastfilter)) {
                    mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastfilter, filter);
                }
            } else {
                if (!mKSYRecordKit.getImgTexFilterMgt().getFilter().contains(filter)) {
                    mKSYRecordKit.getImgTexFilterMgt().addFilter(filter);
                }
            }
            mLastEffectFilterIndex = mEffectFilterIndex;
        } else {
            ImgFilterBase filter;
            if (mFilterTypeIndex < 13) {
                filter = new ImgBeautySpecialEffectsFilter(mKSYRecordKit.getGLRender(),
                        getApplicationContext(), mEffectFilterIndex);
            } else {
                filter = new ImgBeautyStylizeFilter(mKSYRecordKit
                        .getGLRender(), getApplicationContext(), mEffectFilterIndex);
            }
            mEffectFilters.put(mEffectFilterIndex, filter);
            ImgFilterBase lastFilter = null;
            if (mEffectFilters.containsKey(mLastEffectFilterIndex)) {
                lastFilter = mEffectFilters.get(mLastEffectFilterIndex);
            }
            if (lastFilter != null && mKSYRecordKit.getImgTexFilterMgt().getFilter().contains
                    (lastFilter)) {
                mKSYRecordKit.getImgTexFilterMgt().replaceFilter(lastFilter, filter);
            } else {
                mKSYRecordKit.getImgTexFilterMgt().addFilter(filter);
            }
            mLastEffectFilterIndex = mEffectFilterIndex;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        checkPermisson();
        mKSYRecordKit.setDisplayPreview(mCameraPreviewView);
        mKSYRecordKit.onResume();
        mCameraHintView.hideAll();
        startCameraPreviewWithPermCheck();
    }

    /**
     * 读取磁盘权限检查
     */
    private void checkPermisson() {

        int storagePer = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePer = ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);

        if (storagePer != PackageManager.PERMISSION_GRANTED || writePer != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions,
                        PERMISSION_REQUEST_STORAGE);
            }
        } else {

        }
    }




    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        mKSYRecordKit.onPause();
        if (!mKSYRecordKit.isRecording() && !mKSYRecordKit.isFileRecording()) {
            mKSYRecordKit.stopCameraPreview();
        }
        if(mIsFileRecording){
            stopRecord(false);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mMainHandler) mMainHandler.removeMessages(0);
        stopRecord(false);
        cleanMusic();
        mKSYRecordKit.stopBgm();
        mRecordProgressCtl.stop();
        mRecordProgressCtl.setRecordingLengthChangedListener(null);
        mRecordProgressCtl.release();
        mRecordProgressCtl=null;
        mKSYRecordKit.setOnLogEventListener(null);
        mKSYRecordKit.release();
        mKSYRecordKit.deleteAllFiles();//删除断点录制所有内容
        mKSYRecordKit=null;
        ActivityCollectorManager.removeActivity(this);
        Runtime.getRuntime().gc();
    }


    //开始录制
    private void startRecord() {
        if (mIsFileRecording) {
            // 上一次录制未停止完，不能开启下一次录制
            return;
        }
        //用户是否开启了延时拍摄
        if (mBtn_record_delayed.isActivated()&&null!=timeCountDownHandler) {
            timeCountDown = 4;
            Message message = timeCountDownHandler.obtainMessage(1);
            timeCountDownHandler.sendMessage(message);
        //直接拍摄
        } else {
            String fileFolder = getRecordFileFolder();
            mRecordUrl = fileFolder + "/" + System.currentTimeMillis() + ".mp4";
            //设置录制文件的本地存储路径，并开始录制
            boolean b = mKSYRecordKit.startRecord(mRecordUrl);
            if(!b){
                ToastUtils.showCenterToast("录制失败,请检查软件照相机使用权限或存储器!");
            }
        }
    }



    /**
     * 停止拍摄
     *
     * @param finished  finished 是否是已经结束录制
     */
    private void stopRecord(boolean finished) {
        //若录制文件大于1则需要在录制结束后触发文件合成
        if (finished) {
            changeRecordIngView(false);
            String fileFolder = getRecordFileFolder();
            //合成文件路径
            String outFile = fileFolder + "/" + "merger_" + System.currentTimeMillis() + ".mp4";
            //合成过程为异步，需要block下一步处理
            showProgressDialog("视频处理中，请稍后...",true);
            mKSYRecordKit.stopRecord(outFile, new KSYRecordKit.MergeFilesFinishedListener() {
                @Override
                public void onFinished(String filePath) {
                    Message message = timeCountDownHandler.obtainMessage(MESSAGE_COMPLETED);
                    message.obj=filePath;
                    timeCountDownHandler.sendMessage(message);
                }
            });
        } else {
            //普通录制暂停了
            if(null!=mKSYRecordKit) mKSYRecordKit.stopRecord();
        }
    }
    /**
     * 开始录制计时
     */
    private void startChronometer() {
        if(null!=mChronometer){
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.stop();
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
        }
    }

    /**
     * 停止录制计时
     */
    private void stopChronometer() {
        if (mIsFileRecording) {
            return;
        }
        if(null!=mChronometer&&mChronometer.getVisibility()!=View.GONE){
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.stop();
            mChronometer.setVisibility(View.GONE);
        }
    }


    /**
     * 是否正在录制视频
     * @param flag
     */
    private void changeRecordIngView(boolean flag) {
        if(null!=mLl_top_tab_controlle) mLl_top_tab_controlle.setVisibility(flag?View.INVISIBLE:View.VISIBLE);
        if(null!=mRecord_right_menu_layout) mRecord_right_menu_layout.setVisibility(flag?View.INVISIBLE:View.VISIBLE);
        //回删按钮
        if(null!=mBackView) mBackView.setVisibility(flag?View.INVISIBLE:null!=mKSYRecordKit&&mKSYRecordKit.getRecordedFilesCount() >= 1?View.VISIBLE:View.INVISIBLE);
        //速度调制器
        if(null!=mBottomMenu) mBottomMenu.setVisibility(flag?View.INVISIBLE:View.VISIBLE);

        if(flag){
            mIsFileRecording = true;
            mRecordView.animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start();
            mRecordView.setImageResource(R.drawable.record_pause);
            //开始拍摄后，禁止音乐按钮的点击事件
            if(null!=mBtn_record_music){
                mBtn_record_music.setBackgroundResource(R.drawable.ic_record_music_pre);
                mBtn_record_music.setClickable(false);
            }
            //开始倒计时
            startChronometer();
            if(null!=mRecordProgressCtl) mRecordProgressCtl.startRecording();//进度条开始
            //跟随录制一起播放音乐
            if(null!=mMediaPlayer&&!mMediaPlayer.isPlaying()){
                mMediaPlayer.start();
            }
        }else{
            if(null!=mMediaPlayer&&mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
            //保证这两发方法在停止录制视频时候不会重复触发
            if(null!=mChronometer&&mChronometer.getVisibility()!=View.GONE){
                if(null!=mRecordProgressCtl) mRecordProgressCtl.stopRecording();
                if(null!=mRecordView) mRecordView.animate().scaleX(1).scaleY(1).setDuration(500).start();
                mRecordView.setImageResource(R.drawable.record_controller_seletor);
                stopChronometer();
            }
        }
    }

    // Example to handle camera related operation
    private void setCameraAntiBanding50Hz() {
        Camera.Parameters parameters = mKSYRecordKit.getCameraCapture().getCameraParameters();
        if (parameters != null) {
            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            mKSYRecordKit.getCameraCapture().setCameraParameters(parameters);
        }
    }

    /**
     * 视频录制的监听
     */
    private KSYStreamer.OnInfoListener mOnInfoListener = new KSYStreamer.OnInfoListener() {
        @Override
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                //预览成功
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    setCameraAntiBanding50Hz();
                    break;

                case StreamerConstants.KSY_STREAMER_CAMERA_FACEING_CHANGED:

                    break;
                //开始录制成功
                case StreamerConstants.KSY_STREAMER_OPEN_FILE_SUCCESS:
                    mIsFileRecording = true;
                    changeRecordIngView(true);
                    break;
                //结束录制
                case StreamerConstants.KSY_STREAMER_FILE_RECORD_STOPPED:
                    //未停止结束，最好不要操作开始录制，否则在某些机型上容易造成录制开始失败的case
                    mIsFileRecording = false;
                    updateRecordUI();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 停止拍摄，还原界面显示
     */
    private void updateRecordUI() {
        changeRecordIngView(false);
    }



    /**
     * 不支持硬编的设备，fallback到软编
     */
    private void handleEncodeError() {
        int encodeMethod = mKSYRecordKit.getVideoEncodeMethod();
        if (encodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            mHWEncoderUnsupported = true;
            if (mSWEncoderUnsupported) {
                mKSYRecordKit.setEncodeMethod(
                        StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
            } else {
                mKSYRecordKit.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
            }
        } else if (encodeMethod == StreamerConstants.ENCODE_METHOD_SOFTWARE) {
            mSWEncoderUnsupported = true;
            if (mHWEncoderUnsupported) {
                mKSYRecordKit.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
            } else {
                mKSYRecordKit.setEncodeMethod(StreamerConstants.ENCODE_METHOD_HARDWARE);
            }
        }
    }

    //录制错误
    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    break;
                //Camera was disconnected due to use by higher priority user.
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                    break;
                default:
                    break;
            }
            switch (what) {
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    mKSYRecordKit.stopCameraPreview();
                    break;
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_CLOSE_FAILED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_OPEN_FAILED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_FORMAT_NOT_SUPPORTED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_WRITE_FAILED:
                    ToastUtils.showCenterToast("录制失败，请检查USB存储器");
                    stopRecord(false);
                    rollBackClipForError();
                    startRecord();
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN: {
                    handleEncodeError();
                    stopRecord(false);
                    rollBackClipForError();
                    startRecord();
                }
                break;
                default:
                    break;
            }
        }
    };

    private StatsLogReport.OnLogEventListener mOnLogEventListener =
            new StatsLogReport.OnLogEventListener() {
                @Override
                public void onLogEvent(StringBuilder singleLogContent) {
                }
            };

    /**
     * 前后置摄像头切换
     */
    private void onSwitchCamera() {
        mKSYRecordKit.switchCamera();
        mBtn_flash.setSelected(false);
        mIsFlashOpened = false;
    }

    /**
     * 闪光灯开关处理
     */
    private void onFlashClick() {
        if (mIsFlashOpened) {
            mKSYRecordKit.toggleTorch(false);
            mIsFlashOpened = false;
            mBtn_flash.setSelected(false);
        } else {
            mKSYRecordKit.toggleTorch(true);
            mIsFlashOpened = true;
            mBtn_flash.setSelected(true);
        }
    }



    /**
     * back按钮作为返回上一级和删除按钮
     * 当录制文件>=1时 作为删除按钮，否则作为返回上一级按钮
     * 作为删除按钮时，初次点击时先设置为待删除状态，在带删除状态下再执行文件回删
     */
    private void onBackoffClick() {
        if (null!=mDefaultRecordBottomLayout&&mDefaultRecordBottomLayout.getVisibility() != View.VISIBLE) {
            if (mPreRecordConfigLayout.getVisibility() == View.VISIBLE) {
                mPreRecordConfigLayout.setVisibility(View.INVISIBLE);
            }
            if(null!=mRecord_right_menu_layout&&mRecord_right_menu_layout.getVisibility()!=View.VISIBLE) mRecord_right_menu_layout.setVisibility(View.VISIBLE);
            if(null!=mBottomMenu&&mBottomMenu.getVisibility()!=View.VISIBLE) mBottomMenu.setVisibility(View.VISIBLE);
            showViewFromBottomToTop(mDefaultRecordBottomLayout);
            mPreRecordConfigLayout = mDefaultRecordBottomLayout;
            return;
        }
        //如果已经录制的分段大于等于1
        if (null!=mKSYRecordKit&&mKSYRecordKit.getRecordedFilesCount() >= 1) {
            if (!mBackView.isChecked()) {
                mBackView.setChecked(true);
                //设置最后一个文件为待删除文件
                mRecordProgressCtl.setLastClipPending();
            } else {
                //删除文件时，若文件正在录制，则需要停止录制
                if (mIsFileRecording) {
                    stopRecord(false);
                }
                mBackView.setChecked(false);
                //删除录制文件
                mKSYRecordKit.deleteRecordFile(mKSYRecordKit.getLastRecordedFiles());
                mRecordProgressCtl.rollback();

                if(mKSYRecordKit.getRecordedFilesCount()<=0){
                    mBackView.setVisibility(View.INVISIBLE);
                    if(null!=mBtn_record_music){
                        mBtn_record_music.setBackgroundResource(R.drawable.ic_record_music);
                        mBtn_record_music.setClickable(true);
                    }
                    cleanMusic();
                }
            }
            return;
        }
        cleanMusic();
        if(null!=mBackView) mBackView.setVisibility(View.GONE);
        if(null!=mBtn_record_music){
            mBtn_record_music.setBackgroundResource(R.drawable.ic_record_music);
            mBtn_record_music.setClickable(true);
        }
    }





    /**
     * 开始/停止录制
     */
    private void onRecordClick() {
        if (mIsFileRecording) {
            stopRecord(false);
        } else {
            startRecord();
        }
        //清除back按钮的状态
        clearBackoff();
    }


    private int timeCountDown;

    final Handler timeCountDownHandler = new Handler(){
        public void handleMessage(Message msg){
            if(1==msg.what){
                timeCountDown--;
                if(timeCountDown > 0){
                    mTimeCountDownText.setText(""+timeCountDown);
                    AnimationUtil.displayAnim(mTimeCountDownText,MediaRecordActivity.this,R.anim.anim_text_scale,View.VISIBLE);
                    Message message = timeCountDownHandler.obtainMessage(1);
                    timeCountDownHandler.sendMessageDelayed(message, 1000);      // send message
                }else{
                    mTimeCountDownText.setVisibility(View.GONE);
                    mBtn_record_delayed.setActivated(false);
                    if (mIsFileRecording) {
                        // 上一次录制未停止完，不能开启下一次录制
                        return;
                    }
                    String fileFolder = getRecordFileFolder();
                    mRecordUrl = fileFolder + "/" + System.currentTimeMillis() + ".mp4";
                    //设置录制文件的本地存储路径，并开始录制
                    boolean b = mKSYRecordKit.startRecord(mRecordUrl);
                    if(!b){
                        ToastUtils.showCenterToast("录制失败,请检查软件照相机使用权限或存储器!");
                    }
                }
                return;
            }else if(MESSAGE_COMPLETED==msg.what){
                //合成完毕，去编辑
                closeProgressDialog();
                String filePath = (String) msg.obj;
                mRecordUrl = filePath;  //合成文件本地路径
                //可以调用此接口在合成结束后，删除断点录制的所有视频
                //mKSYRecordKit.deleteAllFiles();
                if(!TextUtils.isEmpty(mRecordUrl)){
                    Intent intent=new Intent(MediaRecordActivity.this,MediaEditActivity.class);
                    intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_VIDEO_PATH,mRecordUrl);
                    intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH,mMusicPath);
                    intent.putExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID,mMusicID);
                    intent.putExtra(Constant.KEY_MEDIA_KEY_FPS,fps);
                    intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_SOURCETYPE,3);//视频的来源类型  0:未知， 1：微信文件夹 2：用户选择相册视频上传 3:录制拍摄
                    startActivity(intent);
                }
                return;
            }
        }
    };



    /**
     * 进入编辑页面
     */
    private void onNextClick() {
        clearBackoff();
        clearRecordState();
        mRecordView.animate().scaleX(1).scaleY(1).setDuration(500).start();
        mRecordView.setImageResource(R.drawable.record_controller_seletor);
        //进行编辑前需要停止录制，并且结束断点拍摄
        stopRecord(true);
    }


    //===========================================音效===============================================

    private int mSeniorCureenIndex=0;//当前显示的界面
    private View[]  mTabLines=null;
    private View[]  mContentViews=null;
    private TextView[]  mTabTitles=null;

    /**
     * 初始化高级功能界面UI
     */
    private void initSeniorInitUI() {
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

        mContentViews[mSeniorCureenIndex].setVisibility(View.VISIBLE);
        mTabLines[mSeniorCureenIndex].setVisibility(View.VISIBLE);
        mTabTitles[mSeniorCureenIndex].setSelected(true);
        //初始化变声
        initSoundEffectView();
    }



    /**
     * 切换显示的界面
     * @param poistion
     */
    private void changeSeniorView(int poistion) {
        mTabLines[mSeniorCureenIndex].setVisibility(View.GONE);
        mContentViews[mSeniorCureenIndex].setVisibility(View.GONE);
        mTabTitles[mSeniorCureenIndex].setSelected(false);

        mContentViews[poistion].setVisibility(View.VISIBLE);
        mTabLines[poistion].setVisibility(View.VISIBLE);
        mTabTitles[poistion].setSelected(true);

        mSeniorCureenIndex=poistion;
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
        soundRecylerView.setLayoutManager(new LinearLayoutManager(MediaRecordActivity.this,LinearLayoutManager.HORIZONTAL,false));
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
        soundRecylerView.setLayoutManager(new LinearLayoutManager(MediaRecordActivity.this,LinearLayoutManager.HORIZONTAL,false));
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
     * 添加变声/混响的Filter
     */
    private void addImgFilter() {

        ImgBeautyProFilter proFilter;
        ImgBeautySpecialEffectsFilter specialEffectsFilter;
        List<ImgFilterBase> filters = new LinkedList<>();

        if (mImgBeautyProFilter != null) {
            proFilter = new ImgBeautyProFilter(mKSYRecordKit.getGLRender(), getApplicationContext());
            proFilter.setGrindRatio(1.0f);//mImgBeautyProFilter.getGrindRatio() 磨皮
            proFilter.setRuddyRatio(0.2f);//mImgBeautyProFilter.getRuddyRatio() 红润
            proFilter.setWhitenRatio(0.3f);//mImgBeautyProFilter.getWhitenRatio() 美白
            mImgBeautyProFilter = proFilter;
            filters.add(proFilter);
        }

        if (mEffectFilterIndex != FILTER_DISABLE) {
            specialEffectsFilter = new ImgBeautySpecialEffectsFilter(mKSYRecordKit.getGLRender(), getApplicationContext(), mEffectFilterIndex);
            filters.add(specialEffectsFilter);
        }

        if (filters.size() > 0) {
            mKSYRecordKit.getImgTexFilterMgt().setFilter(filters);
        } else {
            mKSYRecordKit.getImgTexFilterMgt().setFilter((ImgTexFilterBase) null);
        }
    }

    private void setBeautyFilter() {
        mImgBeautyProFilter = new ImgBeautyProFilter(mKSYRecordKit.getGLRender(), MediaRecordActivity.this);
        addImgFilter();
    }




    private boolean isFilter=false;
    private void setFilter() {
        if(isFilter) return;
        setBeautyFilter();
    }

    /**
     * 添加音频滤镜，支持变声和混响同时生效
     * https://github.com/ksvc/KSYStreamer_Android/wiki/Audio_Filter
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
            mKSYRecordKit.getAudioFilterMgt().setFilter(filters);
        } else {
            mKSYRecordKit.getAudioFilterMgt().setFilter((AudioFilterBase) null);
        }
    }

    /**
     * 重置录制状态
     */
    private void clearRecordState() {
        mKSYRecordKit.stopBgm();
//        mKSYRecordKit.getImgTexFilterMgt().setFilter((ImgFilterBase) null);
//        mImgBeautyProFilter = null;
//        mKSYRecordKit.getImgTexFilterMgt().setExtraFilter((ImgFilterBase) null);
//        if(null!=mBtn_camera_fail) mBtn_camera_fail.setSelected(false);
//       switchBeautyFilterType(1);//还原美颜选中项
        switchFilterType(0);//还原滤镜选中项
        switchMVFilterType(0);//还原MV选中项
        addImgFilter();
        switchSoundEffectType(0);//还原变声选中项
        switchSoundReverberationType(0);//还原混响选选中项
        mAudioEffectType = AUDIO_FILTER_DISABLE;
        mAudioReverbType = AUDIO_FILTER_DISABLE;
        addAudioFilter();
        clearPitchState();
    }



    private void cleanMusic() {
        if(null!=mMediaPlayer){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMusicID="";
        mMusicPath="";
        mMediaPlayer=null;
        if(null!=mKSYRecordKit) mKSYRecordKit.setVoiceVolume(1.0f);
    }



//    AnimationUtil.displayAnim(mRl_record_setting_content,MediaRecordActivity.this,R.anim.anim_setting_content_show,View.VISIBLE);




    @Override
    public void onBackPressed() {
        //当前有功能面板正在显示
        if(null!=mDefaultRecordBottomLayout&&mDefaultRecordBottomLayout.getVisibility() != View.VISIBLE){
            if (mPreRecordConfigLayout.getVisibility() == View.VISIBLE) {
                mPreRecordConfigLayout.setVisibility(View.INVISIBLE);
            }
            showRightViewFromBottomToTop(mRecord_right_menu_layout);
            showViewFromBottomToTop(mDefaultRecordBottomLayout);
            mPreRecordConfigLayout = mDefaultRecordBottomLayout;
            return;
        }

        if(mKSYRecordKit.getRecordedFilesCount() >= 1){
            stopRecord(false);//停止录制
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("退出提示")
                    .setMessage("确定要放弃录制并删除已录制视频文件吗？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setCancelable(false).show();
            return;
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.menu_exit);//出场动画
    }



    /**
     * 清除音调状态，重置为'0'
     */
    private void clearPitchState() {
//        mPitchText.setText("0");
        mKSYRecordKit.getBGMAudioFilterMgt().setFilter((AudioFilterBase) null);
    }






    /**
     * 打开系统文件夹，导入音频文件作为背景音乐
     */
    private void importMusicFile() {
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, "ksy_import_music_file");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 选中本地背景音乐后返回结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //音乐界面有携带音乐回来
        if(Constant.MEDIA_START_MUSIC_REQUEST_CODE==requestCode&& Constant.MEDIA_START_MUSIC_RESULT_CODE==resultCode){
            if(null!=data){
                mMusicID = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_ID);
                mMusicPath = data.getStringExtra(Constant.KEY_MEDIA_KEY_MUSIC_PATH);
                if(!TextUtils.isEmpty(mMusicPath)){
                    if(Utils.isFileToMp3(mMusicPath)){
                        if(null!=mKSYRecordKit) mKSYRecordKit.setVoiceVolume(0.0f);
                        //初始化一个音频播放器
                        if(null==mMediaPlayer){
                            mMediaPlayer =  new KSYMediaPlayer.Builder(MediaRecordActivity.this).build();
                        }
                        try {
                            mMediaPlayer.setDataSource(mMusicPath);
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepareAsync();
                            mMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(IMediaPlayer iMediaPlayer) {
                                    if(null!=mMediaPlayer){
                                        mMediaPlayer.pause();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        ToastUtils.showCenterToast("音乐地址错误或音乐格式不受支持！");
                    }
                }
            }
        }
    }


    private void startCameraPreviewWithPermCheck() {
        int cameraPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED || audioPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            } else {
                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_AUDIOREC);
            }
        } else {
            mKSYRecordKit.startCameraPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AUDIOREC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mKSYRecordKit.startCameraPreview();
                } else {
                    ToastUtils.showCenterToast("要正常使用拍摄功能，请务必授予拍摄、录音权限！");
                }
                break;
            }
        }
    }
    /**
     * 如果回删按钮是选中待删除，恢复未正常
     *
     * @return
     */
    private boolean clearBackoff() {
        if (mBackView.isChecked()) {
            mBackView.setChecked(false);
            //设置最后一个文件为普通文件
            mRecordProgressCtl.setLastClipNormal();
            return true;
        }
        return false;
    }

    /**
     * 拍摄错误停止后，删除多余文件的进度
     */
    private void rollBackClipForError() {
        //当拍摄异常停止时，SDk内部会删除异常文件，如果ctl比SDK返回的文件小，则需要更新ctl中的进度信息
        if(null!=mRecordProgressCtl&&null!=mKSYRecordKit){
            int clipCount = mRecordProgressCtl.getClipListSize();
            int fileCount = mKSYRecordKit.getRecordedFilesCount();
            if (clipCount > fileCount) {
                int diff = clipCount - fileCount;
                for (int i = 0; i < diff; i++) {
                    mRecordProgressCtl.rollback();
                }
            }
        }
    }

    /**
     * 监听录制总大小的进度信息
     */
    private RecordProgressController.RecordingLengthChangedListener mRecordLengthChangedListener =
            new RecordProgressController.RecordingLengthChangedListener() {
                @Override
                public void passMinPoint(boolean pass) {
                    if (pass) {
                        //超过最短时长显示下一步按钮，否则不能进入编辑，最短时长可自行设定，Demo中当前设定为5s
                        mNextView.setVisibility(View.VISIBLE);
                    } else {
                        mNextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void passMaxPoint() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //到达最大拍摄时长时，需要主动停止录制
                            stopRecord(true);
                        }
                    });
                }
            };

    private String getRecordFileFolder() {

        String recordOutPath = ApplicationManager.getInstance().getOutPutPath(0);
        if(new File(recordOutPath).exists()){
            return recordOutPath;
        }
        String fileFolder = "/sdcard/XinQu";
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return fileFolder;
    }


    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    protected void showProgressDialog(String message,boolean isProgress){
        if(!MediaRecordActivity.this.isFinishing()){
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
            if(!MediaRecordActivity.this.isFinishing()){
                if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()){
                    mLoadingProgressedView.dismiss();
                    mLoadingProgressedView=null;
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 显示加载中
     * @param loadingView
     * @param loadingError
     * @param animationDrawable
     */
    protected void showLoadingView(View loadingView,View loadingError,AnimationDrawable animationDrawable) {
        if(null!= loadingError && loadingError.getVisibility()!=View.GONE){
            loadingError.setVisibility(View.GONE);
        }
        if(null!= loadingView && loadingView.getVisibility()!=View.VISIBLE){
            loadingView.setVisibility(View.VISIBLE);
        }
        if(null!= animationDrawable &&!animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    /**
     * 显示内容
     * @param loadingView
     * @param loadingError
     * @param animationDrawable
     */
    protected void showContentView(View loadingView,View loadingError,AnimationDrawable animationDrawable) {
        if(null!= animationDrawable && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
        if(null!= loadingError && loadingError.getVisibility()!=View.GONE){
            loadingError.setVisibility(View.GONE);
        }
        if(null!= loadingView && loadingView.getVisibility()!=View.GONE){
            loadingView.setVisibility(View.GONE);
        }
    }
}