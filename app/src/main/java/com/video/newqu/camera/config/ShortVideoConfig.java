package com.video.newqu.camera.config;

import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;

/**
 * 录制和输出参数类
 */

public class ShortVideoConfig {
    public float fps = StreamerConstants.DEFAULT_TARGET_FPS;
    public int resolution = StreamerConstants.VIDEO_RESOLUTION_480P;
    public int videoBitrate = StreamerConstants.DEFAILT_MIN_VIDEO_BITRATE;
    public int audioBitrate = StreamerConstants.DEFAULT_AUDIO_BITRATE;
    public int encodeType = AVConst.CODEC_ID_AVC;
    public int encodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE;
    public int encodeProfile = VideoEncodeFormat.ENCODE_PROFILE_LOW_POWER;
}
