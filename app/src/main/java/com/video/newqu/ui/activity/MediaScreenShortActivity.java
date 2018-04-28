package com.video.newqu.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.ksyun.media.player.misc.KSYProbeMediaInfo;
import com.ksyun.media.shortvideo.utils.ProbeMediaInfoTools;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityMediaScreenshortBinding;
import com.video.newqu.util.ToastUtils;
import java.util.ArrayList;

/**
 * 视频封面选择
 */

public class MediaScreenShortActivity extends BaseActivity<ActivityMediaScreenshortBinding> implements View.OnClickListener {

    public  final String COMPOSE_PATH = "compose_path";
    public  final String PREVIEW_LEN = "preview_length";
    private String mLocalPath;  //合成视频的本地存储地址
    private volatile Bitmap mBitmap;  //视频封面
    private long mSeekTime;
    private float mPreviewLength;  //视频预览时长
    private ProbeMediaInfoTools mImageSeekTools; //根据时间获取视频帧的工具类
    private HandlerThread mSeekThumbnailThread;
    private Handler mSeekThumbnailHandler;
    private Runnable mSeekThumbnailRunable;
    private volatile boolean mStopSeekThumbnail = true;
    private volatile boolean mH265File = false;
    public   Handler sHandler=new Handler();
    private long mAutoDurtion=1000;//默认秒递增
    private  boolean seetCursor=false;//默认减
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        initIntent();
        setContentView(R.layout.activity_media_screenshort);
        showToolBar(false);
        //must set
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startCoverSeek();
    }

    private void initIntent() {
        mLocalPath = getIntent().getExtras().getString(COMPOSE_PATH);
        mPreviewLength = getIntent().getExtras().getFloat(PREVIEW_LEN);
        if(TextUtils.isEmpty(mLocalPath)){
            if(!MediaScreenShortActivity.this.isFinishing()){
                ToastUtils.showCenterToast("错误!");
            }
            finish();
        }
    }


    @Override
    public void initViews() {
        bindingView.tvTitle.setText("选择封面");
        bindingView.ivBack.setOnClickListener(this);
        bindingView.ivSubmit.setOnClickListener(this);
        bindingView.ivSeekReset.setOnTouchListener(onTouchListener);
        bindingView.ivUnseekReset.setOnTouchListener(onTouchListener);
    }

    @Override
    public void initData() {

    }



    private void initSeekThread() {
        if (mSeekThumbnailThread == null) {
            mSeekThumbnailThread = new HandlerThread("screen_setup_thread", Thread.NORM_PRIORITY);
            mSeekThumbnailThread.start();
            mSeekThumbnailHandler = new Handler(mSeekThumbnailThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    return;
                }
            };

            mSeekThumbnailRunable = new Runnable() {
                @Override
                public void run() {

                    if (!mH265File) {
                        mBitmap = mImageSeekTools.getVideoThumbnailAtTime(mLocalPath, mSeekTime,
                                0, 0, true);
                    } else {
                        //h265的视频暂时不支持精准seek
                        mBitmap = mImageSeekTools.getVideoThumbnailAtTime(mLocalPath, mSeekTime,
                                0, 0, false);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mBitmap != null&&null!=bindingView) {
                                bindingView.coverImage.setImageBitmap(mBitmap);
                            }
                        }
                    });
                    if (!mStopSeekThumbnail&&null!=mSeekThumbnailHandler){
                        mSeekThumbnailHandler.postDelayed(mSeekThumbnailRunable, 100);
                    }
                }
            };
        }
    }


    @Override
    public void onDestroy() {
        mStopSeekThumbnail = true;
        if (mSeekThumbnailHandler != null) {
            mSeekThumbnailHandler.removeCallbacksAndMessages(null);
            mSeekThumbnailHandler = null;
        }
        if(null!=sHandler){
            sHandler.removeMessages(0);
        }
        if (mSeekThumbnailThread != null) {
            mSeekThumbnailThread.getLooper().quit();
            try {
                mSeekThumbnailThread.join();
            } catch (InterruptedException e) {
            } finally {
                mSeekThumbnailThread = null;
            }
        }
        super.onDestroy();
    }

    /**
     * 这里携带截图的毫秒数返回即可
     */
    private void close() {
        Intent intent=new Intent();
        intent.putExtra(Constant.KEY_INTENT_MEDIA_THUBM,mSeekTime);
        setResult(Constant.MEDIA_START_COVER_RESULT_CODE,intent);
        finish();
    }


    private void startCoverSeek() {
        //h265的视频暂时不支持精准获取缩略图
        mImageSeekTools = new ProbeMediaInfoTools();
        mImageSeekTools.probeMediaInfo(mLocalPath, new ProbeMediaInfoTools.ProbeMediaInfoListener() {
            @Override
            public void probeMediaInfoFinished(ProbeMediaInfoTools.MediaInfo info) {
                ArrayList<KSYProbeMediaInfo.KSYProbeMediaData> videoStreams = info.videoStreams;
                if (info.videoStreams != null && info.videoStreams.size() > 0) {
                    KSYProbeMediaInfo.KSYVideoCodecType videoCodecType = info.videoStreams.get(0).getVideoCodecType();
                    if (videoCodecType == KSYProbeMediaInfo.KSYVideoCodecType.KSY_VIDEO_H265) {
                        mH265File = true;
                    }
                }
            }
        });

        mBitmap = mImageSeekTools.getVideoThumbnailAtTime(mLocalPath, mSeekTime, 0, 0, true);
        bindingView.coverImage.setImageBitmap(mBitmap);
        bindingView.coverSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rate = progress / 100.f;
                mSeekTime = (long) (mPreviewLength * rate);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                initSeekThread();
                mStopSeekThumbnail = false;
                if(null!=mSeekThumbnailHandler) mSeekThumbnailHandler.postDelayed(mSeekThumbnailRunable, 100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mStopSeekThumbnail = true;
                if (mSeekThumbnailHandler != null) {
                    mSeekThumbnailHandler.removeCallbacksAndMessages(null);
                    mSeekThumbnailHandler.post(mSeekThumbnailRunable);
                }
            }
        });
    }

    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                //加
                case R.id.iv_seek_reset:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            seekLongToReset(false);
                            mStopSeekThumbnail = true;
                            if (mSeekThumbnailHandler != null) {
                                mSeekThumbnailHandler.removeCallbacksAndMessages(null);
                                mSeekThumbnailHandler.post(mSeekThumbnailRunable);
                            }
                             break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_DOWN:
                            seetCursor=true;

                            if(null!=sHandler) sHandler.postDelayed(NextSeekToRunnable,mAutoDurtion);
                            initSeekThread();
                            mStopSeekThumbnail = false;

                            seekShortToReset(true);
                            seekLongToReset(true);
                            break;
                    }
                    return true;
                //减
                case R.id.iv_unseek_reset:

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            seekLongToReset(false);

                            mStopSeekThumbnail = true;
                            if (mSeekThumbnailHandler != null) {
                                mSeekThumbnailHandler.removeCallbacksAndMessages(null);
                                mSeekThumbnailHandler.post(mSeekThumbnailRunable);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:

                            break;
                        case MotionEvent.ACTION_DOWN:
                            seetCursor=false;

                            if(null!=sHandler) sHandler.postDelayed(NextSeekToRunnable,mAutoDurtion);
                            initSeekThread();
                            mStopSeekThumbnail = false;

                            seekShortToReset(false);
                            seekLongToReset(true);
                            break;
                    }
                    return true;
            }
            return false;
        }
    };


    /**
     * 进度+和—长按事件
     * @param seek
     */
    private void seekLongToReset(boolean seek) {
        if(seek){
            if(null!=sHandler) sHandler.postDelayed(NextSeekToRunnable,0);
        }else{
            if(null!=sHandler) sHandler.removeCallbacks(NextSeekToRunnable);

        }
    }

    private Runnable NextSeekToRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = bindingView.coverSeekBar.getProgress();
            //加，每一秒进度条向上加5秒
            if(seetCursor){
                if(progress>=100){
                    progress=100;
                }else{
                    progress+=5;
                }
            //减，每一秒进度条向下减5秒
            }else{
                if(progress<=0){
                    progress=0;
                }else{
                    progress-=5;
                }
            }

            bindingView.coverSeekBar.setProgress(progress);
            if(null!=mSeekThumbnailHandler) mSeekThumbnailHandler.post(mSeekThumbnailRunable);
            if(null!=sHandler) sHandler.postDelayed(this, mAutoDurtion);
        }
    };


    /**
     * 微调进度
     * @param seek 真：加，假：减
     */
    private void seekShortToReset(boolean seek) {
        int progress = bindingView.coverSeekBar.getProgress();

        //加
        if(seek){
            if(progress>=100){
                progress=100;
            }else{
                progress+=1;
            }
        }else{
            if(progress<=0){
                progress=0;
            }else{
                progress-=1;
            }
        }
        bindingView.coverSeekBar.setProgress(progress);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_submit:
                close();
                break;
            default:
                break;
        }
    }
}
