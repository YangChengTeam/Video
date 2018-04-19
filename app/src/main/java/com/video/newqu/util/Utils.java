package com.video.newqu.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tencent.open.utils.HttpUtils;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.MediaMusicHomeMenu;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.bean.SubscribeInfo;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.xinqu.videoplayer.XinQuVideoPlayerStandard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TinyHung@outlook.com
 * 2017/3/17 23:09
 */

public class Utils {

    public static int setDialogWidth(Dialog context) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) context.getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics = new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight = LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height = hight;
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        Log.d("Utils", "setDialogWidth: screenWidth=");
        if (screenWidth <= 720) {
            attributes.width = screenWidth - 100;
        } else if (screenWidth > 720 && screenWidth < 1100) {
            attributes.width = screenWidth - 200;
        } else if (screenWidth > 1100 && screenWidth < 1500) {
            attributes.width = screenWidth - 280;
        } else {
            attributes.width = screenWidth - 200;
        }
        attributes.gravity = Gravity.CENTER;
        return attributes.width;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat();

    public static String formatW(int vaule) {
        if (vaule >= 10000) {
            float l = vaule / 10000.0f;

            return format(l, "#.#'W'");
        }
        return String.valueOf(vaule);
    }

    public static String format(float vaule, String pattern) {
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(vaule);
    }


    public static void setDialogWidth(Dialog context, int unWidth) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) context.getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics = new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);
        int hight = LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height = hight;
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        attributes.width = screenWidth - unWidth;
        Log.d("Utils", "setDialogWidth: screenWidth-unWidth=" + attributes.width);
        attributes.gravity = Gravity.CENTER;
    }


    /**
     * 切割字符串，去除开头不需要的
     * @param url
     * @param param
     * @return
     */
    public static String cutImageUrl(String url, String param) {
        if (url.startsWith(param)) {
            String substring = url.substring(param.length(), url.length());
            return url.substring(param.length(), url.length());
        }
        return null;
    }

    /**
     * 生成 min 到 max之间的随机数,包含 min max
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNum(int min, int max) {
        return min + (int) (Math.random() * max);
    }

    public static void setActivityDialogWidth(Activity context) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics = new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight = LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height = hight;

        int screenWidth = systemService.getDefaultDisplay().getWidth();
        if (screenWidth <= 720) {
            attributes.width = screenWidth - 80;
        } else if (screenWidth > 720 && screenWidth < 1100) {
            attributes.width = screenWidth - 120;
        } else if (screenWidth > 1100 && screenWidth < 1500) {
            attributes.width = screenWidth - 150;
        } else {
            attributes.width = screenWidth - 200;
        }
        attributes.gravity = Gravity.CENTER;
    }

    /**
     * 获取内部版本号
     * @return
     */
    public static int getVersionCode()//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = VideoApplication.getInstance().getPackageManager().getPackageInfo(VideoApplication.getInstance().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设备识别码
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return mTm.getDeviceId();
    }

    /**
     * 获取当前设备是否有网
     *
     * @return
     */
    public static boolean isCheckNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前设备是否有网
     *
     * @return
     */
    public static boolean isCheckNetwork() {
        ConnectivityManager cm = (ConnectivityManager) VideoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络类型
     * -1:错误
     * 1：WIFI
     * 2：3G
     *
     * @return
     */

    public static int getNetworkType() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) VideoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            return NetContants.NETWORK_STATE_WIFI;
                        } else {
                            return NetContants.NETWORK_STATE_3G;
                        }
                    } else {
                        return NetContants.NETWORK_STATE_NO_CONNECTION;
                    }
                } else {
                    return NetContants.NETWORK_STATE_NO_CONNECTION;
                }
            }
        } catch (Exception e) {

            return NetContants.NETWORK_STATE_ERROR;
        }
        return NetContants.NETWORK_STATE_ERROR;
    }


    /**
     * 复制字符串
     *
     * @param content 复制的文本
     */
    public static void copyString(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) VideoApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 使用浏览器打开链接
     */
    public static void openLink(Context context, String content) {
        Uri issuesUrl = Uri.parse(content);
        Intent intent = new Intent(Intent.ACTION_VIEW, issuesUrl);
        context.startActivity(intent);
    }


    /**
     * 将数据转换为万为单位
     * @param no
     * @return
     */

    public static String formatWan(int no) {
        double n = (double) no / 10000;
        return changeDouble(n) + "万";
    }

    public static double changeDouble(Double dou) {
        NumberFormat nf = new DecimalFormat("0.0 ");
        dou = Double.parseDouble(nf.format(dou));
        return dou;
    }

    public static double changeDouble(float num) {
        double parseDouble = 0.0;
        try {
            NumberFormat nf = new DecimalFormat("0.00");
            parseDouble = Double.parseDouble(nf.format(num));
            return parseDouble;
        } catch (Exception e) {
            return parseDouble;
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取屏幕的大小
     */

    public static DisplayMetrics getScreenSize(Activity act) {
        DisplayMetrics metric = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;  // 屏幕高度（像素
        return metric;
    }

    /**
     * 获取屏幕的高
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = getScreenSize(activity);
        int height = displayMetrics.heightPixels;  // 屏幕高度（像素
        return height;
    }

    /**
     * 获取屏幕的宽
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = getScreenSize(activity);
        int width = displayMetrics.widthPixels;  // 屏幕高度（像素
        return width;
    }


    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        String imei = telephonyManager.getDeviceId();
        return imei;
    }



    /**
     * 保留两位小数
     * @param percent
     * @return
     */
    public static float float2(float percent) {
        BigDecimal b  =   new BigDecimal(percent);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 获取应用的包名
     *
     * @param context
     * @return
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 通过Url切割文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * 获取.APK文件的包信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static int getAPKPathVerstion(Context context, File apkPath) {
        int versionCode = 0;
        if (apkPath.exists() && ZipUtil.isArchiveFile(apkPath)) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                versionCode = packageInfo.versionCode;
            } else {
                versionCode = 0;
            }

            return versionCode;
        } else {
            return versionCode;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath
     * @return
     */
    public static boolean deleteFiledeleteFile(File sPath) {

        boolean flag = false;
        // 路径为文件且不为空则进行删除
        if (null!=sPath&& sPath.exists()&&sPath.isFile() ) {
            sPath.delete();
            flag = true;
        }
        return flag;
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null&&files.length>0){
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 返回可以正常连接的url地址
     * @param logo
     * @return
     */
    public static String imageUrlChange(String logo) {
        if(TextUtils.isEmpty(logo)) return null;
        if(logo.startsWith("http")){
            return logo;
        }
        return NetContants.BASE_IP+logo;
    }



    /**
     * 本机SD卡是否可用
     * @return
     */
    public static boolean hasSdCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        }
        return false;
    }
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = VideoApplication.getInstance().getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(VideoApplication.getInstance().getApplicationContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 判断是否是常用11位数手机号
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
       return m.matches();
    }

    /**
     * 是否是6位数字验证码
     * @param phoneNumber
     * @return
     */
    public static boolean isNumberCode(String phoneNumber) {
        Pattern p = Pattern.compile("^\\d{4}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 提取短信中的验证码4位
     * @param smsBody
     * @return
     */
    public static  String getAuthCodeFromSms(String smsBody) {
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(smsBody);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 6-16位密码正则判断
     * @param phoneNumber
     * @return
     */
    public static boolean isPassword(String phoneNumber) {
        Pattern p = Pattern.compile("^([0-9]|[a-zA-Z]){6,16}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }


    /**
     * 将String写入本地
     *
     * @param response
     * @param filePath
     */
    public static void writeString(String response, File filePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            byte[] bytes = response.getBytes();
            try {
                fileOutputStream.write(bytes);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测输入文本框内容中的话题是否超过限制个数,正则表达式"#[^#]+#"
     * @return
     */
    public static int exceedTopicCount(String content) {

        List<String> hashtags = getHashtags(content);

        if(null!=hashtags&&hashtags.size()>0){
            for (String hashtag : hashtags) {
                Log.d("Utils", "exceedTopicCount: hashtag="+hashtag);
            }
            return hashtags.size();
        }
        return 0;
    }


    private static final Pattern hashtagPattern =
            Pattern.compile("#[^#]+#");

    private static String removeHashtags(String text){
        Matcher matcher;
        String newTweet = text.trim();
        String cleanedText="";
        while(!newTweet.equals(cleanedText)){
            cleanedText=newTweet;
            matcher = hashtagPattern.matcher(cleanedText);
            newTweet = matcher.replaceAll("");
            newTweet =newTweet.trim();
        }
        return cleanedText;
    }


    /**
     *  // 定义正则表达式
     private static final String AT = "@[\u4e00-\u9fa5\\w]+";// @人
     private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
     private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
     private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
     */
    /**
     * 将文本内容中的带有话题关键字内容提取出来封装进集合
     * @param content
     * @return
     */
    public static Map<String, Map<Integer, Integer>> hashtags(String content){

        //话题关键字Key 开始角标 结束角标
        Map<String,Map<Integer,Integer>> topicMaps=new HashMap<>();
        List<String> hashtagSet=new ArrayList<String>();
        Matcher matcher = hashtagPattern.matcher(content);
        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            String tmpHashtag=content.substring(matchStart,matchEnd);

            hashtagSet.add(tmpHashtag);


            Map<Integer,Integer> map=new HashMap<>();//记录话题的开始位置和结束位置
            map.put(matchStart,matchEnd);
            topicMaps.put(tmpHashtag,map);

            content=content.replace(tmpHashtag,"");

            matcher = hashtagPattern.matcher(content);
        }
        if(null!=hashtagSet&&hashtagSet.size()>0){

        }
        return topicMaps;
    }



    public static List<String> getHashtags(String content){
        List<String> hashtagSet=new ArrayList<String>();
        Matcher matcher = hashtagPattern.matcher(content);
        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            String tmpHashtag=content.substring(matchStart,matchEnd);
            hashtagSet.add(tmpHashtag);
            content=content.replace(tmpHashtag,"");
            matcher = hashtagPattern.matcher(content);
        }
        return hashtagSet;
    }

    /**
     *判断输入框内字符串是否包含新的话题字符
     * @param content
     * @param topic
     * @return
     */
    public static boolean topicCountEquals(String content, String topic) {
        List<String> hashtags = getHashtags(content);
        if(null!=hashtags&&hashtags.size()>0){
            for (String hashtag : hashtags) {
                if(TextUtils.equals(hashtag,topic)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将字符串中有话题的字段加上标签所需的颜色
     * @param content
     */
    public static String changeTopic(String content) {
        if(TextUtils.isEmpty(content)){
            return null;
        }
        List<String> hashtags = getHashtags(content);
        String newDesp=content;
        if(null!=hashtags&&hashtags.size()>0){
            String topic1 = hashtags.get(0);
            String newContent = content.replace(topic1, "<font color='#FD7004'>" + topic1 + "</font>");
            newDesp=newContent;
            if(hashtags.size()>1){
                String topic2 = hashtags.get(1);
                String newConten2 = newContent.replace(topic2, "<font color='#FD7004'>" + topic2 + "</font>");
                newDesp=newConten2;
            }
        }
        return newDesp;
    }

    /**
     * 提取话题中的正文本
     * @param stringExtra
     * @return
     */
    public static String slipTopic(String stringExtra) {
        if(TextUtils.isEmpty(stringExtra)){
            return null;
        }
        if(stringExtra.startsWith("#")){
            return stringExtra.substring(1, stringExtra.length() - 1);
        }
        return stringExtra;
    }

    /**
     * 初始化轮播图
     * @return
     */
    public static List<String> curenADImage() {
        List<String> list=new ArrayList<>();
        for (int i = 0; i < Cheeses.AUTO_IMAGE.length; i++) {
            list.add(Cheeses.AUTO_IMAGE[i]);
        }
        return list;
    }

    /**
     * 切割字符串
     * @param content  要切割的内容
     * @param count 最大保留长度
     * @return
     */
    public static String subString(String content, int count) {
        if(TextUtils.isEmpty(content)){
            return "";
        }
        if(content.length()<=count){
            return content;
        }
        return content.substring(0,count)+"..";
    }

    public static int getNotificationID() {
        long num = System.currentTimeMillis();
        String s = String.valueOf(num);
        if(s.length()>=8){
            String substring = s.substring(7, s.length() - 1);
            return Integer.parseInt(substring);
        }
        return 0;
    }

    /**
     * 比较两个集合中是否有不一样的新的数据
     * @param oldList  本地缓存的
     * @param newList  新的，这里的新数据是不会为空或者长度不会为0的
     *  返回结果是否相等
     */
    public static int compareToDataHasNewData(List<FollowVideoList.DataBean.ListsBean> oldList, List<FollowVideoList.DataBean.ListsBean> newList) {

        if(null==oldList||oldList.size()<=0){
            return newList==null?0:newList.size();
        }
        if(null==newList||newList.size()<=0){
            return 0;
        }
        try {
            for (int i = 0; i < oldList.size(); i++) {
                for (int i1 = 0; i1 < newList.size(); i1++) {
                    if(TextUtils.equals(oldList.get(i).getVideo_id(),newList.get(i1).getVideo_id())){
                        newList.remove(i1);
                    }
                }
            }
        }catch (Exception e){

        }
        return newList.size();
    }


    public boolean equalList(List list1, List list2) {
        if (list1.size() != list2.size())
            return false;
        for (Object object : list1) {
            if (!list2.contains(object))
                return false;
        }
        return true;

    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[] {
                    null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (Throwable t) {
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String mdStr = bigInt.toString(16);
        int slen = 32 - mdStr.length();
        for (int i = 0; i < slen; i++) {
            mdStr = 0 + mdStr;
        }
        return mdStr;
    }


    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    /**
     *
     * @param videoType 视频类型
     * @param video_player 播放器
     * @param groupView 父布局
     */
    public static void setVideoRatio(int videoType, XinQuVideoPlayerStandard video_player, View groupView) {
        switch (videoType) {
            //默认，正方形
            case 0:
            case 3:
                video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
                video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
                break;
            //宽
            case 1:
                video_player.widthRatio=16;
                video_player.heightRatio=9;
                break;
            //长
            case 2:
                video_player.widthRatio=3;
                video_player.heightRatio=4;
                break;
            default:
                video_player.widthRatio=Constant.VIDEO_RATIO_MOON;
                video_player.heightRatio=Constant.VIDEO_RATIO_MOON;
        }
        int heightRatio = video_player.getHeightRatio();
        int widthRatio = video_player.getWidthRatio();
        int specHeight = (int) ((ScreenUtils.getScreenWidth() * (float) heightRatio) / widthRatio);
        if(null!=groupView) groupView.getLayoutParams().height=specHeight;
    }


    //改变bitmap尺寸的方法
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppSatus(Context context, String pageName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;//栈里找不到，返回3
        }
    }

    /**
     * 检查两个数组是否都为空或者长度都为0
     * @param mvideo_list
     * @param user_list
     * @return
     */
    public static boolean changeListVolume(List<SearchResultInfo.DataBean.VideoListBean> mvideo_list, List<SearchResultInfo.DataBean.UserListBean> user_list) {

        if(null!=mvideo_list&&mvideo_list.size()>0){
            return true;
        }

        if(null!=user_list&&user_list.size()>0){
            return true;
        }
        return false;
    }
    public static boolean checkPermission(Context context, String permName, String pkgName){
        PackageManager pm = context.getPackageManager();
        if(PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName)){
            System.out.println(pkgName + "has permission : " + permName);
            return true;
        }else{
            //PackageManager.PERMISSION_DENIED == pm.checkPermission(permName, pkgName)
            System.out.println(pkgName + "not has permission : " + permName);
            return false;
        }
    }

    /**
     * 只截取保留字符串最后5位数
     * @param content
     * @return
     */
    public static int substring(String content) {
        if(TextUtils.isEmpty(content)) return 0;
        if(content.length()<5){
            return Integer.parseInt(content.substring(0,content.length()));
        }
        return Integer.parseInt(content.substring(content.length()-5,content.length()));
    }

    /**
     * 安装apk
     */
    public static  void installApk(File filePath) {
        if(null!=filePath&&filePath.exists()&&filePath.isFile()&&0!=Utils.getAPKPathVerstion(VideoApplication.getInstance(), filePath)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.parse("file://" + filePath.toString()),
                    "application/vnd.android.package-archive");
            VideoApplication.getInstance().getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 将数字转变为以万为单位
     * @param number
     * @return
     */
    public static String changeNumberFormString(String number) {
        if(TextUtils.isEmpty(number)) return "";
        int intNumber = Integer.parseInt(number);
        if(intNumber<10000){
            return intNumber+"";
        }else if(intNumber==10000){
            return "1.0万";
        }else{
            return save2number(intNumber/10000)+"万";
        }
    }

    /**
     * 四舍五入保留两位小数点
     * @param number
     * @return
     */
    public static  double  save2number(double number){
        // 方式一：
//        double f = 3.1516;
//        BigDecimal b = new BigDecimal(f);
//        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        // 方法二 #.00 表示两位小数 #.0000四位小数 以此类推…
        return Double.parseDouble(new java.text.DecimalFormat("#.00").format(number));
    }

    public static boolean isContainKey(String topicID) {
        if(TextUtils.isEmpty(topicID)) return false;
        for (int i = 0; i < Cheeses.TOPIC_KEY.length; i++) {
            if(TextUtils.equals(topicID,Cheeses.TOPIC_KEY[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * 去除最后一个“/”
     * @param filterFolderPath
     * @return
     */
    public static String subFolderEnd(String filterFolderPath) {
        if(TextUtils.isEmpty(filterFolderPath)) return filterFolderPath;
        if(filterFolderPath.endsWith("/")){
           return filterFolderPath.substring(0, filterFolderPath.length() - 1);
        }
        return filterFolderPath;
    }


    public static List<MediaMusicHomeMenu.DataBean> catMenuItemList(List<MediaMusicHomeMenu.DataBean> data) {
        if(null==data|data.size()<=0) return data;
        List<MediaMusicHomeMenu.DataBean> newList=new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            newList.add(data.get(i));
            if(newList.size()>=9){
                break;
            }
        }
        //添加一个更多至末尾
        MediaMusicHomeMenu.DataBean tmpeMenuData=new MediaMusicHomeMenu.DataBean();
        tmpeMenuData.setIconID(R.drawable.ic_music_more);
        tmpeMenuData.setItemType(1);
        tmpeMenuData.setPid("0");
        tmpeMenuData.setSort("0");
        tmpeMenuData.setName("更多"+(data.size()-9));
        newList.add(tmpeMenuData);
        return newList;
    }

    /**
     * 用于格式化duration为HH:MM:SS格式
     * @param duration
     * @return
     */
    public static String formatDurationForHMS(long duration) {
        long second = duration / 1000;
        long minute = second / 60;
        long hour = minute / 60;
        second = second % 60;
        minute = minute % 60;
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

    public static boolean isFileToMp3(String absolutePath) {
        if(!TextUtils.isEmpty(absolutePath)){
            return absolutePath.endsWith(".mp3")||absolutePath.endsWith(".MP3");
        }
        return false;
    }

    public static boolean isFileToMp4(String absolutePath) {
        if(!TextUtils.isEmpty(absolutePath)){
            return absolutePath.endsWith(".mp4")||absolutePath.endsWith(".MP4");
        }
        return false;
    }

    /**
     * 对数值的取反操作
     * @param alpha 取反前数值
     * @param minVar 最小取值
     * @param maxVar 最大取值
     * 返回值为最终取值数值
     */
    public static float[] vars=null;
    public static float absValue(float alpha, int minVar, int maxVar) {
        if(null==vars){
            vars=new float[maxVar];
            for (int i = minVar; i < maxVar; i++) {
                vars[i]=i;
            }
        }
        if(null!=vars&&vars.length>0){
            return vars.length-alpha;
        }
        return 0;
    }

    /**
     * 将没有后缀的地址
     * @param fileName
     * @return
     */
    public static String rexVideoPath(String fileName) {
        if(null!=fileName&&fileName.length()>0){
            if(fileName.endsWith(".mp4")||fileName.endsWith(".MP4")){
                return fileName;
            }
            return fileName+".mp4";
        }
        return fileName;
    }

    public static String getReservedSession() {
        Random rand=new Random();//生成随机数
        String cardNnumer="";
        for(int a=0;a<6;a++){
            cardNnumer+=rand.nextInt(10);//生成6位数字
        }
        return cardNnumer;
    }

    public static float absVakue(float maxVar, float var ) {
        return maxVar-var;
    }

    public static String getBindPhoneNumber(String phone) {
        if(TextUtils.isEmpty(phone)) return "";
        if(phone.length()>=11){
            String substring = phone.substring(3, phone.length() - 4);
            return phone.replace(substring,"****");
        }
        return "";
    }
}
