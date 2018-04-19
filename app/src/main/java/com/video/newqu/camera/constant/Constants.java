package com.video.newqu.camera.constant;

import com.ksyun.media.streamer.filter.imgtex.ImgBeautySpecialEffectsFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyStylizeFilter;

/**
 * TinyHung@Outlook.com
 * 2017/9/11.
 */

public class Constants {

    public static final int MAX_DURATION=30 * 1000;  //最长拍摄时长 30s
    public static final int MIN_DURATION = 5 * 1000;  //最短拍摄时长 5s
    public static final float MAX_CAP_DURTION = 720.0f;  //最长裁剪时长 12分钟
    public static final float MIN_CAP_DURTION = 1.0f;  //最短裁剪时长 12分钟
    //滤镜类型
    public static final int[] FILTER_TYPE = {ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_FRESHY,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_BEAUTY,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SWEETY,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SEPIA,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_BLUE,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_NOSTALGIA,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SAKURA,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SAKURA_NIGHT,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_RUDDY_NIGHT,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SUNSHINE_NIGHT,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_RUDDY,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_SUSHINE,
            ImgBeautySpecialEffectsFilter.KSY_SPECIAL_EFFECT_NATURE,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_AMARO,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_BRANNAN,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_EARLY_BIRD,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_HUDSON,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_LOMO,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_NASHVILLE,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_RISE,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_TOASTER,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_VALENCIA,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_WALDEN,
            ImgBeautyStylizeFilter.KSY_FILTER_STYLE_XPROLL};
}
