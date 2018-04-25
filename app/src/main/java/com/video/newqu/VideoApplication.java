package com.video.newqu;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import com.blankj.utilcode.util.Utils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.ksyun.media.player.KSYHardwareDecodeWhiteList;
import com.mob.MobSDK;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.video.newqu.bean.UserData;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.util.ACache;
import com.video.newqu.util.CommonDateParseUtil;
import com.video.newqu.util.ContentCheckKey;
import com.video.newqu.util.DateParseUtil;
import com.video.newqu.util.attach.FaceConversionUtil;
import com.video.newqu.util.KSYAuthorPermissionsUtil;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;

/**
 *  TinyHung@outlook.com
 *  2017/5/20 10:53
 */

public class VideoApplication extends Application {

    private static VideoApplication mInstance;
    public static boolean videoComposeFinlish;
    private UserData.DataBean.InfoBean mUserData=null;
    public static  String mUuid;
    public static int mToday=0;
    public static int mBuildChanleType=0;
    private String[] mLocations=new String[]{"39","116"};//默认是北京坐标

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static VideoApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = VideoApplication.this;
        Config.DEBUG=false;
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "xinquConfig", Context.MODE_MULTI_PROCESS);
        ACache cache = ACache.get(mInstance);
        ApplicationManager.getInstance().setCacheExample(cache);//初始化后需要设置给通用管理者
        //极光消息推送
        JPushInterface.setDebugMode(false);
        JPushInterface.init(mInstance);
        UserData.DataBean.InfoBean userData = (UserData.DataBean.InfoBean)  ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_USER_DATA);
        setUserData(userData,false);
        mToday = Integer.parseInt(CommonDateParseUtil.getNowDay());
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //友盟统计、分享
                PlatformConfig.setWeixin("wx2d62a0f011b43f32", "c43aeb050a3eab9f723c04cfc0525800");//设置微信SDK账号
                PlatformConfig.setSinaWeibo("994868311", "908f16503b8ebe004cdf9395cebe1b14","http://sns.whalecloud.com/sina2/callback");//设置微博分享/登录SDK//https://api.weibo.com/oauth2/default.html
                PlatformConfig.setQQZone("1106176094","Pkas3I3J2OpaZzsH");//设置QQ/空间SDK账号
                UMShareAPI.get(mInstance);
                Utils.init(mInstance);
                GoagalInfo.get().init(mInstance);
                mUuid = GoagalInfo.get().uuid;
                if(TextUtils.isEmpty(mUuid)){
                    mUuid= SystemUtils.getLocalIpAddress();
                }
                HttpConfig.setPublickey(Constant.URL_PRIVATE_KEY);
                ContentCheckKey.getInstance().init();
                ApplicationManager.getInstance().initSDPath();
                MobclickAgent.setScenarioType(mInstance, MobclickAgent.EScenarioType.E_UM_NORMAL);//普通统计模式
                ConfigSet.getInstance().init();
                //第一次打开程序,初始化设置项
                if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.IS_FIRST_START)){
                    ConfigSet.getInstance().initSetting();
                    SharedPreferencesUtil.getInstance().putInt(Constant.IS_FIRST_START,1);
                }
                if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.IS_FIRST_START_DB)){
                    if(ApplicationManager.getInstance().getVideoUploadDB().getUploadVideoList().size()>0){
                        try {
                            ApplicationManager.getInstance().getVideoUploadDB().deteleAllUploadList();
                        }catch (Exception e){
                        }
                    }
                    SharedPreferencesUtil.getInstance().putInt(Constant.IS_FIRST_START_DB,1);
                }
                //如果从来未保存星期几，就保存当天的星期日期，用来标记一个礼拜扫描一个本地视频
                if(0==SharedPreferencesUtil.getInstance().getInt(Constant.SETTING_TODAY_WEEK_SUNDY)){
                    int todayWeekSundy = DateParseUtil.getTodayWeekSundy();                    SharedPreferencesUtil.getInstance().putInt(Constant.SETTING_TODAY_WEEK_SUNDY,todayWeekSundy);//保存第一次安装时候的星期日期
                }
                Fresco.initialize(mInstance);//动态贴纸解析必须
                try {
                    MobSDK.init(mInstance, "1ecf369922dc5", "aaf891da7ce90d40d52de6bedf5bf89c");
                }catch (Exception e){

                }
                //初始化全局异常拦截
                //CrashHanlder.getInstance().init(VideoApplication.this);
                //LeakCanary.install(VideoApplication.this);//内存泄漏检测
                //初始化表情包
                FaceConversionUtil.getInstace().getFileText(getApplicationContext());
                //金山云
                KSYAuthorPermissionsUtil.init();
                KSYHardwareDecodeWhiteList.getInstance().init(mInstance);
            }
        }.start();
    }

    private HttpProxyCacheServer proxy;
    public static HttpProxyCacheServer getProxy() {
        VideoApplication app = (VideoApplication) mInstance.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    /**
     * 构造100M大小的缓存池
     * @return
     */
    private HttpProxyCacheServer newProxy() {
        //SD卡已挂载并且可读写
        int cacheSize = 200 * 1024 * 1024;
        //线使用内部缓存
        return new HttpProxyCacheServer.Builder(this).cacheDirectory(new File(ApplicationManager.getInstance().getVideoCacheDir())).maxCacheSize(cacheSize)//1BG缓存大小上限
                .build();
    }

    /**
     * 更新用户信息
     * @param userData
     */
    public synchronized void setUserData(UserData.DataBean.InfoBean userData,boolean isSerializable) {
        //注册极光服务
        if(null!=userData&&!TextUtils.isEmpty(userData.getId())){
            JPushInterface.setAlias(VideoApplication.this, (int) System.currentTimeMillis(),"xinqu_id_"+userData.getId());//极光推送别名,后台需要以此别名进行消息推送
            Set<String> tags=new HashSet<>();
            //这个标签是针对设备的
            tags.add("app_xinqu");//渠道标签
            tags.add(TextUtils.isEmpty(userData.getGender())?"男":userData.getGender());//性别标签
            JPushInterface.setTags(this, (int)System.currentTimeMillis(),tags);//极光推送标签
        //注销
        }else {
            JPushInterface.deleteAlias(VideoApplication.this, (int) System.currentTimeMillis());
        }
        if(isSerializable){
            //序列化一個對象到緩存
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_USER_DATA);
            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_USER_DATA,userData);
        }
        mUserData = userData;
    }

    public UserData.DataBean.InfoBean getUserData() {
        return mUserData;
    }

    /**
     * 返回当前登录用户ID，如果登录返回USERID,未登录直接返回设备号
     * @return
     */
    public static String getLoginUserID() {
        try {
            if(null==VideoApplication.getInstance().getUserData()){
                return "0";
            }
            if(TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getId())){
                return "0";
            }
            return VideoApplication.getInstance().getUserData().getId();
        }catch (Exception e){
            return "0";
        }
    }

    public boolean userIsBinDingPhone() {
        return null==VideoApplication.getInstance().getUserData()?false:!TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getPhone());
    }

    public void setLocation(String[] locationID) {
        this.mLocations=locationID;
    }

    public String[] getLocations() {
        return mLocations;
    }
}
