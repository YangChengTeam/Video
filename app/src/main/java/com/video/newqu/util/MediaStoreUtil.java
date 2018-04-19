package com.video.newqu.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.video.newqu.bean.WeiXinVideo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/5.
 */

public class MediaStoreUtil {

    /**
     * 查询音频文件名称
     *
     * @param context
     * @return
     */
    public static List<String> getAudioNames(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA }, null, new String[] {}, null);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            list.add(fileName);
        }
        return list;
    }

    /**
     * 查询图片文件名称
     *
     * @param context
     * @return
     */
    public static List<String> getImageNames(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA }, null,
                new String[] {}, null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            list.add(filePath + "/" + fileName);
        }
        return list;
    }

    /**
     * 查询图片文件
     *
     * @param context
     * @return
     */
    public static List<File> getImages(Context context) {
        List<File> list = new ArrayList<File>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA }, null,
                new String[] {}, null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //Logger.i(TAG, "fileName==" + fileName);
            File file = new File(filePath);
            list.add(file);
        }
        return list;
    }

    /**
     * 查询文件
     *
     * @param context
     * @return
     */
    public static List<File> getAllFiles(Context context) {
        List<File> list = new ArrayList<File>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA }, null,
                new String[] {}, null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            File file = new File(filePath);
            list.add(file);
        }
        return list;
    }

    /**
     * 获取所有的缩列图
     *
     * @param context
     * @return
     */
    public static Bitmap[] getBitmaps(Context context) {
        Bitmap[] bitmaps;
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.Media._ID);
        int count = cursor.getCount();
        int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        bitmaps = new Bitmap[count];
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(image_column_index);
            bitmaps[i] = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        return bitmaps;
    }

    /**
     * 查询图片缩列文件名称
     *
     * @param context
     * @return
     */
    public static List<String> getThumbNames(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.KIND,
                        MediaStore.Images.Thumbnails.IMAGE_ID }, null, new String[] {}, null);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            list.add(fileName);
        }
        return list;
    }

    /**
     * 获得所有视频文件
     * @param context
     */
    public static List<WeiXinVideo> getVideoInfo(Context context,String... fromat){

        String[] thumbColumns = new String[]{
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };

        String[] mediaColumns = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION
        };
        //首先检索SDcard上所有的video
        try {
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
            ArrayList<WeiXinVideo> videoList = new ArrayList<WeiXinVideo>();
            if(null!=cursor){
                if(cursor.moveToFirst()){
                    do{
                        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        //只需要受支持的视频格式的文件
                        if(isSupport(filePath,fromat)){
                            File file =new File(filePath);
                            if(file.exists()&&file.isFile()){
                                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                                String durtion=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                                //防止.mp4的临时文件
                                if(null!=durtion&&0!=Integer.parseInt(durtion)){
                                    WeiXinVideo info = new WeiXinVideo();
                                    //获取当前Video对应的Id，然后根据该ID获取其Thumb
                                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                                    String[] selectionArgs = new String[]{id+""
                                    };
                                    Cursor thumbCursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
                                    if(null!=thumbCursor){
                                        if(thumbCursor.moveToFirst()){
                                            String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                                            info.setVidepThbunPath(thumbPath);
                                        }
                                    }
                                    info.setFileName(title);
                                    info.setVideoPath(filePath);
                                    info.setVideoDortion(Integer.parseInt(TextUtils.isEmpty(durtion)?"0":durtion));
                                    info.setVideoCreazeTime(file.lastModified());
                                    videoList.add(info);
                                }
                            }
                        }
                    }while(cursor.moveToNext());
                }
            }

            if(null!=videoList&&videoList.size()>0){
                //对视频时间进行倒序排序
                Collections.sort(videoList, new Comparator<WeiXinVideo>() {
                    @Override
                    public int compare(WeiXinVideo o1, WeiXinVideo o2) {
                        return o2.getVideoCreazeTime().compareTo(o1.getVideoCreazeTime());
                    }
                });
            }
            return videoList;
        }catch (Exception e){

        }
        return null;
    }

    /**
     * 返回受支持的视频格式
     * @param filePath
     * @param fromat
     * @return
     */
    public static boolean isSupport(String filePath, String... fromat) {
        if(TextUtils.isEmpty(filePath)) return false;
        String substring = filePath.substring(filePath.lastIndexOf(".")+1);
        if(null!=fromat&&fromat.length>0){
            for (String s : fromat) {
                if(TextUtils.equals(substring,s)){
                    return true;
                }
            }
        }
        return false;
    }
}
