package com.video.newqu.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.ksyun.media.shortvideo.utils.ShortVideoConstants;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.camera.adapter.MediaEditThumbnailAdapter;
import com.video.newqu.camera.videorange.HorizontalListView;
import com.video.newqu.camera.videorange.VideoRangeSeekBar;
import com.video.newqu.camera.videorange.VideoThumbnailInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityVideoCatBinding;
import com.video.newqu.ui.dialog.RecordProgressDialog;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/11/26
 * 视频裁剪
 */

public class MediaVideoCatActivity extends BaseActivity<ActivityVideoCatBinding>{

    private int mSourceType;
    private String mVideoPath;
    private KSYEditKit mEditKit;
    private long mEditPreviewDuration;
    private Handler mMainHandler;
    private static final int LONG_VIDEO_MAX_LEN = Constant.MEDIA_VIDEO_EDIT_MAX_DURTION;//最大5分钟
    private int mMaxClipSpanMs = LONG_VIDEO_MAX_LEN;  //默认的最大裁剪时长
    private float mHLVOffsetX = 0.0f;
    private float mLastX = 0;
    private MediaEditThumbnailAdapter mMediaEditThumbnailAdapter;
    private Timer mTimer;
    private RecordProgressDialog mRecordProgressDialog;
    private boolean mComposeFinished=false;//是否正在合并


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cat);
        showToolBar(false);
    }



    @Override
    public void initViews() {
        bindingView.tvTitle.setText("视频裁剪");
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(id==R.id.iv_back){
                    onBackPressed();
                }else if(id==R.id.btn_submit){
                    next();
                }
            }
        };
        bindingView.ivBack.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
    }

    private void next() {

        //最大限制10分钟短片
        if(null!=bindingView.videodurtionChangeSeekbar&&bindingView.videodurtionChangeSeekbar.getRangeEnd()-bindingView.videodurtionChangeSeekbar.getRangeStart()>300.0){
            ToastUtils.showCenterToast("视频时长必须小于5分钟");
            return;
        }
        if(AuthInfoManager.getInstance().getAuthState()){
            if(null!=mEditKit){
                mEditKit.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);//编码方式，软编，ENE_METHOD_HARDWARE硬编
                mEditKit.setVideoDecodeMethod(StreamerConstants.DECODE_METHOD_HARDWARE);//硬解
//                mEditKit.setVideoEncodeProfile(VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE);// 默认高等质量
                mEditKit.setVideoEncodeProfile(VideoEncodeFormat.ENCODE_PROFILE_BALANCE);// 默认平衡质量
                //设置合成路径
                String fileFolder = ApplicationManager.getInstance().getOutPutPath(1);
                File file = new File(fileFolder);
                if (!file.exists()) {
                    file.mkdir();
                }
                StringBuilder composeUrl = new StringBuilder(fileFolder).append("/").append(System.currentTimeMillis());
                composeUrl.append(".mp4");
                mEditKit.startCompose(composeUrl.toString());
            }
        }else{
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("视频合成提示")
                    .setMessage(getResources().getString(R.string.ksy_tips_compose))
                    .setNegativeButton(
                            "网络设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                                    startActivity(intent);
                                }
                            })
                    .setNeutralButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("打开WLAN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            SystemUtils.openWLAN();
                        }
                    }).setCancelable(false).show();
        }
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        mSourceType = bundle.getInt(Constant.KEY_MEDIA_RECORD_PRAMER_SOURCETYPE, 0);
        mVideoPath = bundle.getString(Constant.KEY_MEDIA_RECORD_PRAMER_VIDEO_PATH);
        if(TextUtils.isEmpty(mVideoPath)){
            ToastUtils.showCenterToast("传入路径有误!");
            finish();
        }

        mMainHandler = new Handler();
        mEditKit = new KSYEditKit(MediaVideoCatActivity.this);
        mEditKit.setDisplayPreview(bindingView.editPreview);
        mEditKit.setOnErrorListener(new KSYEditKit.OnErrorListener() {
            @Override
            public void onError(int type, long msg) {

                switch (type) {
                    case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FAILED_UNKNOWN:
                    case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_CLOSE_FAILED:
                    case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_FORMAT_NOT_SUPPORTED:
                    case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_OPEN_FAILED:
                    case ShortVideoConstants.SHORTVIDEO_ERROR_COMPOSE_FILE_WRITE_FAILED:
                        break;
                    case ShortVideoConstants.SHORTVIDEO_ERROR_SDK_AUTHFAILED:

                        break;

                    case ShortVideoConstants.SHORTVIDEO_EDIT_PREVIEW_PLAYER_ERROR:

                    default:
                        break;
                }
            }
        });
        mEditKit.setOnInfoListener(new KSYEditKit.OnInfoListener() {
            @Override
            public Object onInfo(int type, String... strings) {

                switch (type) {
                    case ShortVideoConstants.SHORTVIDEO_EDIT_PREPARED:
                        mEditPreviewDuration = mEditKit.getEditDuration();
                        initVideoRange();
                        initSeekBar();
                        initThumbnailAdapter();
                        break;
                    //开始合并文件
                    case ShortVideoConstants.SHORTVIDEO_COMPOSE_START: {
                        mComposeFinished=true;
                        mEditKit.pauseEditPreview();
                        showCompileWindow();
                        composeStarted();
                        return null;
                    }
                    case ShortVideoConstants.SHORTVIDEO_COMPOSE_FINISHED: {
                        composeFinished(strings[0]);
                        mComposeFinished=false;
                        return null;
                    }
                    //放弃合并
                    case ShortVideoConstants.SHORTVIDEO_COMPOSE_ABORTED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(null!= mRecordProgressDialog && mRecordProgressDialog.isShowing()){
                                    mRecordProgressDialog.setProgress(0);
                                    mRecordProgressDialog.dismiss();
                                }
                                if(null!=mEditKit) mEditKit.resumeEditPreview();
                            }
                        });
                        break;

                    //重新播放
                    case ShortVideoConstants.SHORTVIDEO_EDIT_PREVIEW_PLAYER_INFO:

                        break;
                    case ShortVideoConstants.SHORTVIDEO_COMPOSE_TAIL_STARTED:

                        break;
                    case ShortVideoConstants.SHORTVIDEO_COMPOSE_TITLE_STARTED:

                    default:
                        return null;
                }
                return null;
            }
        });
        //添加贴纸View到SDK
        mEditKit.addStickerView(bindingView.stickerPanel);
        mEditKit.setEditPreviewUrl(mVideoPath);
        mEditKit.setOriginAudioVolume(1.0f);
        //设置是否循环预览
        mEditKit.setLooping(true);
        mEditKit.startEditPreview();
    }



    public void composeStarted() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(null!=mEditKit){
                    final int progress = mEditKit.getProgress();
                    updateProgress(progress);
                }
            }
        }, 500, 500);
    }

    /**
     * 刷新合成进度
     * @param progress
     */
    private void updateProgress( int progress) {
        if(null!=mHandler){
            Message message=Message.obtain();
            message.what=100;
            message.arg1=progress;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 合并完成
     * @param path
     */
    public void composeFinished(String path) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(null!=mHandler){
            Message message=Message.obtain();
            message.what=110;
            message.obj=path;
            mHandler.sendMessage(message);
        }
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(100==msg.what){
                int progress=msg.arg1;
//                int rate = (int) SystemStateObtainUtil.getInstance().sampleCPU();
                if(!MediaVideoCatActivity.this.isFinishing()&&null!= mRecordProgressDialog && mRecordProgressDialog.isShowing()){
                    mRecordProgressDialog.setProgress(progress);
                }
            }else if(110==msg.what){
                final String videoPath = (String) msg.obj;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startEditActivity(videoPath);
                    }
                });
            }
        }
    };

    /**
     * 显示合成视频中的进度窗体
     */
    private void showCompileWindow(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(null== mRecordProgressDialog){
                        mRecordProgressDialog = new RecordProgressDialog(MediaVideoCatActivity.this);
                        mRecordProgressDialog.setMode(RecordProgressDialog.SHOW_MODE1);
                        mRecordProgressDialog.setOnDialogBackListener(new RecordProgressDialog.OnDialogBackListener() {
                            @Override
                            public void onBack() {
                                if (mComposeFinished&&!MediaVideoCatActivity.this.isFinishing()&&null!= mRecordProgressDialog && mRecordProgressDialog.isShowing()) {
                                    new android.support.v7.app.AlertDialog.Builder(MediaVideoCatActivity.this)
                                            .setCancelable(true)
                                            .setTitle("视频即将合成完毕,是否终止合成?")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            })
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    if(null!=mEditKit) mEditKit.stopCompose();
                                                }
                                            }).show();
                                    //当前有界面正在显示
                                }
                            }
                        });
                    }
                    mRecordProgressDialog.setTipsMessage("处理中，稍等一会儿...");
                    mRecordProgressDialog.setProgress(0);
                    if(!mRecordProgressDialog.isShowing()) mRecordProgressDialog.show();
                }catch (Exception e){

                }
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mEditKit&&!mComposeFinished){
            mEditKit.pausePlay(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=mEditKit&&!mComposeFinished){
            mEditKit.pausePlay(true);
        }
    }

    /**
     * 去到编辑界面
     * @param videoPath
     */
    private void startEditActivity(String videoPath) {
        if(null!=mRecordProgressDialog&&mRecordProgressDialog.isShowing()){
            mRecordProgressDialog.dismiss();
            mRecordProgressDialog=null;
        }
        if(!TextUtils.isEmpty(videoPath)){
            Intent intent=new Intent(MediaVideoCatActivity.this,MediaEditActivity.class);
            intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_VIDEO_PATH,videoPath);
            intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_SOURCETYPE,mSourceType);//选择视频上传
            startActivity(intent);
            finish();
        }
    }


    /**
     * 初始化时长裁剪UI
     */

    private void initVideoRange() {
        bindingView.videodurtionChangeSeekbar.setOnVideoMaskScrollListener(mVideoMaskScrollListener);
        bindingView.videodurtionChangeSeekbar.setOnRangeBarChangeListener(onRangeBarChangeListener);
        //缩略图显示
        bindingView.hlistview.setOnScrollListener(mVideoThumbnailScrollListener);
    }


    VideoRangeSeekBar.OnVideoMaskScrollListener mVideoMaskScrollListener = new VideoRangeSeekBar.OnVideoMaskScrollListener() {

        @Override
        public void onVideoMaskScrollListener(VideoRangeSeekBar rangeBar, MotionEvent event) {
            bindingView.hlistview.dispatchTouchEvent(event);
        }
    };



    /**
     * 滑动缩略图的Bar
     */
    HorizontalListView.OnScrollListener mVideoThumbnailScrollListener = new HorizontalListView.OnScrollListener() {

        @Override
        public void onScroll(final int currentX) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mHLVOffsetX = bindingView.videodurtionChangeSeekbar.getRange(currentX);

                    if (mEditPreviewDuration > mMaxClipSpanMs) {
                        if ((bindingView.videodurtionChangeSeekbar.getRangeEnd() + mHLVOffsetX) * 1000 >= mEditPreviewDuration) {
                            mHLVOffsetX = (mEditPreviewDuration / 1000 - bindingView.videodurtionChangeSeekbar.getRangeEnd());
                        }
                    }
                    setRangeTextView(mHLVOffsetX);
                    if (mLastX != bindingView.videodurtionChangeSeekbar.getRangeStart() + mHLVOffsetX) {
                        //do not need to effect
//                        rangeLoopPreview();
                        mLastX = bindingView.videodurtionChangeSeekbar.getRangeStart() + mHLVOffsetX;
                    }
                }
            });
        }
    };



    /**
     * 处理时长裁剪的封面显示
     * @param offset
     */
    private void setRangeTextView(float offset) {
        bindingView.rangeStart.setText(formatTimeStr(bindingView.videodurtionChangeSeekbar.getRangeStart() + offset));
        bindingView.rangeEnd.setText(formatTimeStr(bindingView.videodurtionChangeSeekbar.getRangeEnd() + offset));
        bindingView.range.setText("已选择"+formatTimeStr((int)(bindingView.videodurtionChangeSeekbar.getRangeEnd()-bindingView.videodurtionChangeSeekbar.getRangeStart()))+"秒");
    }

    private String formatTimeStr(float s) {
        int minute = ((int) s) / 60;
        int second = ((int) s) % 60;
        int left = ((int) (s * 10)) % 10;
        return String.format("%02d:%02d.%d", minute, second, left);
    }

    private String formatTimeStr2(int s) {
        int second = s / 10;
        int left = s % 10;

        return String.format("%d.%d", second, left);
    }

    /**
     * 调整条进度
     */
    private void initSeekBar() {

        long durationMS = mEditKit.getEditDuration();
        float durationInSec = durationMS * 1.0f / 1000;
        if (durationMS > mMaxClipSpanMs) {
            bindingView.videodurtionChangeSeekbar.setMaxRange(mMaxClipSpanMs * 1.0f / 1000);
        } else {
            bindingView.videodurtionChangeSeekbar.setMaxRange(durationInSec);
        }
        bindingView.videodurtionChangeSeekbar.setMinRange(1.0f);
        if (durationInSec > 300.0f) {
            bindingView.videodurtionChangeSeekbar.setRange(0.0f, 300.0f);
        } else {
            bindingView.videodurtionChangeSeekbar.setRange(0.0f, durationInSec);
        }
    }

    /**
     * 截取视频的封面
     */
    private void initThumbnailAdapter() {
        float picWidth;  //每个thumbnail显示的宽度
        if (bindingView.videodurtionChangeSeekbar == null) {
            picWidth = 60;
        } else {
            picWidth = bindingView.videodurtionChangeSeekbar.getFrameWidth();
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

            if (i == 0 && bindingView.videodurtionChangeSeekbar != null) {
                listData[i].mType = VideoThumbnailInfo.TYPE_START;
                listData[i].mWidth = (int) bindingView.videodurtionChangeSeekbar.getMaskWidth();
            } else if (i == totalFrame - 1 && bindingView.videodurtionChangeSeekbar != null) {
                listData[i].mType = VideoThumbnailInfo.TYPE_END;
                listData[i].mWidth = (int) bindingView.videodurtionChangeSeekbar.getMaskWidth();
            } else {
                listData[i].mType = VideoThumbnailInfo.TYPE_NORMAL;
                listData[i].mWidth = (int) picWidth;
            }
        }
        mMediaEditThumbnailAdapter = new MediaEditThumbnailAdapter(this, listData, mEditKit);
        bindingView.hlistview.setAdapter(mMediaEditThumbnailAdapter);
    }

    VideoRangeSeekBar.OnRangeBarChangeListener onRangeBarChangeListener = new VideoRangeSeekBar.OnRangeBarChangeListener() {

        @Override
        public void onIndexChangeListener(VideoRangeSeekBar rangeBar, float rangeStart, float rangeEnd, final int change, boolean toEnd) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mHLVOffsetX >= 7.5f && mHLVOffsetX <= 8.5f && !bindingView.videodurtionChangeSeekbar.isTouching()) {
                        mHLVOffsetX = 8.0f;
                        bindingView.videodurtionChangeSeekbar.setRange(bindingView.videodurtionChangeSeekbar.getRangeStart(), bindingView.videodurtionChangeSeekbar.getRangeStart() + mHLVOffsetX);
                    }
                    setRangeTextView(mHLVOffsetX);
                }
            });
        }

        @Override
        public void onActionUp() {
            rangeLoopPreview();
        }

        @Override
        public void onEventDown(int mode) {

        }

        @Override
        public void onEventUp(int mode) {

        }
    };

    /**
     * loop preview duraing range
     */
    private void rangeLoopPreview() {
        long startTime = (long) ((bindingView.videodurtionChangeSeekbar.getRangeStart() + mHLVOffsetX) * 1000);
        long endTime = (long) ((bindingView.videodurtionChangeSeekbar.getRangeEnd() + mHLVOffsetX) * 1000);

        //是否对播放区间的设置立即生效，true为立即生效
        mEditKit.setEditPreviewRanges(startTime, endTime, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
    }
}
