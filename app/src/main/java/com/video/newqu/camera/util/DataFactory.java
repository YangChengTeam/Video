package com.video.newqu.camera.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.AnimatedStickerInfo;
import com.video.newqu.bean.MediaFilterInfo;
import com.video.newqu.bean.MediaSoundFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.video.newqu.R.string.set;


/**
 * Adapter数据构造的工厂类.
 */

public class DataFactory {

    public static final String TAG="DataFactory";
    private static final int MODE_SOUND_CHANGE = 0;
    private static final int MODE_REVERB = 1;

    /**
     * 美颜
     */

    private static final String[] BEAUTY_TYPE_NAME = {"自然", "唯美", "花颜", "粉嫩"};
    private static final int[] BEAUTY_ID = {101,102,103,104};

    private static final int[] BEAUTY_TYPE_ID = {R.drawable.beauty_nature, R.drawable.beauty_pro,
            R.drawable.beauty_flower_like, R.drawable.beauty_delicate};



    /**
     * 滤镜
     */
    private static final String[] IMG_FILTER_NAME = {"小清新", "靓丽", "甜美可人", "怀旧", "蓝调", "老照片",
            "樱花", "樱花(夜)", "红润(夜)", "阳光(夜)", "红润", "阳光", "自然", "优格", "流年", "柔光", "初夏",
            "纽约", "碧波", "日系", "梦幻", "恬淡", "候鸟", "淡雅"};
    private static final int[] FILTER_IMAGE_ID = {R.drawable.filter_fresh, R.drawable.filter_beautiful,
            R.drawable.filter_sweet, R.drawable.filter_sepia, R.drawable.filter_blue, R.drawable.filter_nostalgia,
            R.drawable.filter_sakura, R.drawable.filter_sakura_night, R.drawable.filter_ruddy_night, R.drawable.filter_sunshine_night,
            R.drawable.filter_ruddy, R.drawable.filter_sunshine, R.drawable.filter_nature, R.drawable.yogurt, R.drawable.fleeting_time,
            R.drawable.soft_ligth, R.drawable.early_summer, R.drawable.newyork, R.drawable.greenwaves,
            R.drawable.japanese, R.drawable.illusion, R.drawable.tranquil, R.drawable.migrant_bird, R.drawable.elegant};

    /**
     * 混响
     */
    public static final String[] SOUND_CHANGE_NAME = { "浑厚","萝莉","大叔", "庄重", "机器人"};
    public static final int[] SOUND_CHANGE_IMG_ID = { R.drawable.iv_media_sound_hunhou, R.drawable.lolita,R.drawable.uncle,  R.drawable.solemn, R.drawable.robot};

    /**
     * 变声
     */
    public static final String[] REVERB_NAME = { "录音棚", "KTV", "小舞台", "演唱会","回声"};
    public static final int[] REVERB_IMG_ID = { R.drawable.record_studio,R.drawable.ktv, R.drawable.woodwing, R.drawable.concert,R.drawable.iv_media_edit_sound_hunxiang};


    public static List<MediaFilterInfo> getMediaFilterData(Context context) {

        List<MediaFilterInfo> filterData = new ArrayList<>();
        for (int i = 0; i < IMG_FILTER_NAME.length; i++) {
            MediaFilterInfo mediaFilterInfo=new MediaFilterInfo();
            mediaFilterInfo.setTitle(IMG_FILTER_NAME[i]);
            mediaFilterInfo.setIcon(FILTER_IMAGE_ID[i]);
            mediaFilterInfo.setSelector(false);
            filterData.add(mediaFilterInfo);
        }
        return filterData;
    }


    public static List<MediaFilterInfo> getMediaBeautyData(Context context) {
        List<MediaFilterInfo> filterData = new ArrayList<>();
        for (int i = 0; i < BEAUTY_TYPE_NAME.length; i++) {
            MediaFilterInfo mediaFilterInfo=new MediaFilterInfo();
            mediaFilterInfo.setTitle(BEAUTY_TYPE_NAME[i]);
            mediaFilterInfo.setIcon(BEAUTY_TYPE_ID[i]);
            mediaFilterInfo.setSelector(false);
            mediaFilterInfo.setId(BEAUTY_ID[i]);
            filterData.add(mediaFilterInfo);
        }
        return filterData;
    }





    /**
     *
     * @param mode 0：变声 1：混响
     * @return
     */
    public static List<MediaSoundFilter> getSoundFilterData(int mode) {

        List<MediaSoundFilter> dataList = new ArrayList<>();
        if (mode == MODE_SOUND_CHANGE) {
            for (int i = 0; i < SOUND_CHANGE_NAME.length; i++) {
                MediaSoundFilter mediaSoundFilter=new MediaSoundFilter();
                mediaSoundFilter.setName(SOUND_CHANGE_NAME[i]);
                mediaSoundFilter.setIcon(SOUND_CHANGE_IMG_ID[i]);
                dataList.add(mediaSoundFilter);
            }
         //混响
        } else {
            for (int i = 0; i < REVERB_NAME.length; i++) {
                MediaSoundFilter mediaSoundFilter=new MediaSoundFilter();
                mediaSoundFilter.setName(REVERB_NAME[i]);
                mediaSoundFilter.setIcon(REVERB_IMG_ID[i]);
                dataList.add(mediaSoundFilter);
            }
        }
        return dataList;
    }


    public static List<AnimatedStickerInfo> getAnimatedSticker(String animateStickerPath) {
        List<AnimatedStickerInfo> animatedStickerInfos=null;
        try {
            String[] files = VideoApplication.getInstance().getAssets().list(animateStickerPath);
            if(null!=files&&files.length>0){
                animatedStickerInfos=new ArrayList<>();
                for (String name : files) {
                    AnimatedStickerInfo animatedStickerInfo=new AnimatedStickerInfo();
                    animatedStickerInfo.setLogoPath(animateStickerPath+File.separator + name);
                    animatedStickerInfos.add(animatedStickerInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animatedStickerInfos;
    }
}
