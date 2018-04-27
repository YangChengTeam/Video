package com.video.newqu.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sina.helper.MD5;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.HomeUploadVideoListAdapter;
import com.video.newqu.adapter.XinQuFragmentPagerAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.bean.TopicList;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentHomeBinding;
import com.video.newqu.model.HomeHorzontalSpacesItemDecoration;
import com.video.newqu.ui.activity.SearchActivity;
import com.video.newqu.ui.dialog.UploadVideoFinlishDialog;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.upload.bean.UploadDeteleTaskInfo;
import com.video.newqu.upload.listener.VideoUploadListener;
import com.video.newqu.upload.manager.VideoUploadTaskManager;
import com.video.newqu.util.ImageCache;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ShareUtils;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.util.attach.VideoComposeProcessor;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * TinyHung@outlook.com
 * 2017/5/20 10:22
 * 首页
 */

public class HomeFragment extends BaseFragment<FragmentHomeBinding,MainPresenter> implements VideoUploadListener, Observer {

    private List<Fragment> mFragmentList;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int minHeight=0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            minHeight= SystemUtils.getStatusBarHeight(getActivity());
            if(minHeight<=0){
                minHeight= ScreenUtils.dpToPxInt(25);
            }
        }
        bindingView.topEmptyView.getLayoutParams().height=minHeight;
        ApplicationManager.getInstance().addObserver(this);
        initFragments();
        List<TopicList.DataBean> topList = (List<TopicList.DataBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_TOPIC_LIST);
        if(null!=topList&&topList.size()>0){
            String[] strings=new String[topList.size()];
            for (int i = 0; i < topList.size(); i++) {
                strings[i]=topList.get(i).getTopic();
            }
            bindingView.tvAutoText.setData(strings);
        }else{
            bindingView.tvAutoText.setData(new Cheeses().createAutKey());
        }
        //搜索热词
        bindingView.tvAutoText.setAutoDurtion(5*1000);//5秒钟轮播一次
        bindingView.tvAutoText.startAuto();
        initUploadAdapter();
        checkedUploadTake(true);
    }

    /**
     * 初始化界面
     */
    private void initFragments() {
        if(null==mFragmentList) mFragmentList=new ArrayList<>();
        mFragmentList.clear();
        mFragmentList.add(new HomeFollowVideoFragment());
        mFragmentList.add(new HomeHotVideoFragment());
        mFragmentList.add(new HomeTopicFragment());
        List<String> titles=new ArrayList<>();
        titles.add(getResources().getString(R.string.home_fragment_follow_title));
        titles.add(getResources().getString(R.string.home_fragment_hot_title));
        titles.add(getResources().getString(R.string.home_fragment_topic_title));
        XinQuFragmentPagerAdapter myXinQuFragmentPagerAdapter =new XinQuFragmentPagerAdapter(getChildFragmentManager(),mFragmentList,titles);
        bindingView.homeViewPager.setAdapter(myXinQuFragmentPagerAdapter);
        bindingView.homeViewPager.setOffscreenPageLimit(3);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.homeViewPager);
        bindingView.homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                bindingView.homeViewPager.setScroll(position==2?false:true);
                if(0==position){
                    hideNewMessageDot();
                }
                if(2==position){
                    MobclickAgent.onEvent(getActivity(), "click_topic");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bindingView.homeViewPager.setCurrentItem(1);
    }


    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.re_search_bar:
                        SearchActivity.start(getActivity(),bindingView.tvAutoText.getKey(),v);
                        break;
                    case R.id.btn_histroy:
                         startTargetActivity(Constant.KEY_FRAGMENT_TYPE_PLSYER_HISTORY,"观看视频记录",null,0);
                        break;

                }
            }
        };
        bindingView.reSearchBar.setOnClickListener(onClickListener);
        bindingView.btnHistroy.setOnClickListener(onClickListener);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(null!=bindingView&&null!=bindingView.tvAutoText){
            bindingView.tvAutoText.startAuto();
        }
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
        if(null!=bindingView&&null!=bindingView.tvAutoText){
            bindingView.tvAutoText.stopAuto();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(null!=bindingView&&null!=bindingView.tvAutoText){
            bindingView.tvAutoText.startAuto();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(null!=bindingView&&null!=bindingView.tvAutoText){
            bindingView.tvAutoText.stopAuto();
        }
    }

    /**
     * 显示新消息圆点
     */
    public void showNewMessageDot(int count) {
        if(null==bindingView) return;
        if(0!=bindingView.homeViewPager.getCurrentItem()){
            bindingView.tvNewMessage.setText(count+"");
            bindingView.tvNewMessage.setVisibility(View.VISIBLE);
        }else{
            bindingView.tvNewMessage.setText("");
            bindingView.tvNewMessage.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏新消息圆点
     */
    private void hideNewMessageDot() {
        if(null==bindingView) return;
        if(View.GONE!=bindingView.tvNewMessage.getVisibility()){
            bindingView.tvNewMessage.setVisibility(View.GONE);
        }
    }

    public void showUploadList(boolean isTips) {
        checkedUploadTake(isTips);
    }


    public void changeUploadVideoState() {
        checkedUploadTake(false);
    }

    //===========================================视频上传============================================

    private TreeMap<Long,UploadVideoInfo> mUploadVideoInfoMap=new TreeMap<>();//用来存放上传的任务列表，为了解决上传进度条错乱问题
    private static HomeUploadVideoListAdapter mUploadVideoListAdapter;

    /**
     * 初始化上传,适配器中包含正在合并的视频
     */
    private void initUploadAdapter() {
        bindingView.uploadRecylerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mUploadVideoListAdapter = new HomeUploadVideoListAdapter(getActivity(),mUploadVideoInfoMap);
        bindingView.uploadRecylerView.addItemDecoration(new HomeHorzontalSpacesItemDecoration(Utils.dip2px(5)));
        bindingView.uploadRecylerView.setAdapter(mUploadVideoListAdapter);
        mUploadVideoListAdapter.setOnUploadItemClickListener(new HomeUploadVideoListAdapter.OnUploadItemClickListener() {

            //暂停与开始
            @Override
            public void onUploadTask(UploadVideoInfo data) {
                if(null!=data){
                    //失败了，重新上传
                    if(104==data.getUploadType()||101==data.getUploadType()){
                        updataUploadChangeState(data,100);
                        VideoUploadTaskManager.getInstance().setUploadListener(HomeFragment.this).addUploadTaskAndExcute(data);
                    }
                }
            }

            //长按删除上传任务
            @Override
            public void onLongClickDetele(final UploadVideoInfo data) {
                if(null==data) return;
                if(1==data.getItemType()){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle("取消视频合并提示")
                            .setMessage(getResources().getString(R.string.video_compose_canel_tips));
                    builder.setPositiveButton("放弃合并",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("继续合并", null);
                    builder.setCancelable(false);
                    builder.show();
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle("取消上传任务提示")
                            .setMessage(getResources().getString(R.string.video_upload_canel_tips));
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    canelUploadTask(data);
                                }
                            });
                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });
    }

    /**
     * 取消上传任务
     * @param data
     */
    private void canelUploadTask(UploadVideoInfo data) {
        showProgressDialog("取消上传任务中...",true);
        UploadDeteleTaskInfo uploadDeteleTaskInfo = VideoUploadTaskManager.getInstance().canelSingleTask(data);
        closeProgressDialog();
        if(null!=uploadDeteleTaskInfo){
            if(uploadDeteleTaskInfo.isCancel()){
                removeUploadListAdapterItem(data,0);
                return;
            }else{
                ToastUtils.showCenterToast(uploadDeteleTaskInfo.getMessage());
            }
        }
    }

    /**
     * 检查上传任务，并自动添加至上传任务中
     */
    public void checkedUploadTake(boolean isTips) {

        if(null==VideoApplication.getInstance().getUserData()){
            return;
        }
        if(null==bindingView) return;
        if(null==bindingView.uploadRecylerView) return;
        List<UploadVideoInfo> uploadVideoList = ApplicationManager.getInstance().getVideoUploadDB().getUploadVideoList();
        if(null!=uploadVideoList&&uploadVideoList.size()>0){
            if(null!=mUploadVideoInfoMap) mUploadVideoInfoMap.clear();
            for (UploadVideoInfo uploadVideoInfo : uploadVideoList) {
                uploadVideoInfo.setItemType(0);
                mUploadVideoInfoMap.put(uploadVideoInfo.getId(),uploadVideoInfo);
            }
            if(null!=mUploadVideoListAdapter){
                mUploadVideoListAdapter.setNewData(mUploadVideoInfoMap);
                mUploadVideoListAdapter.notifyDataSetChanged();
                autoUpload(isTips,uploadVideoList);
            }
        }
    }


    /**
     * 自动上传任务前的准备
     * @param isTips
     * @param uploadVideoList
     */
    private void autoUpload(final boolean isTips, final List<UploadVideoInfo> uploadVideoList) {
        //接入的是WIFI
        if(1==Utils.getNetworkType()){
            upload(uploadVideoList);
        }else {
            //不是WIFI网络
            if(isTips){
                //接入的是移动网络
                if(Utils.isCheckNetwork()){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle("视频上传提示")
                            .setMessage("您的设备未接入WIFI网络，继续使用移动网络上传可能会产生额外的流量费用，是否继续上传?");
                    builder.setPositiveButton("继续上传",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    upload(uploadVideoList);
                                }
                            });
                    builder.setNegativeButton("WIFI下自动上传", null);
                    builder.setCancelable(false);
                    builder.show();
                    //不是WIFI也未接入网络
                }else{
                    //没有网络
                    showErrorToast(null,null,"没有可用的网络连接");
                }
                //非第一次提示
            }else{
                //在移动网络下和用户允许的流量上传
                if(2==Utils.getNetworkType()&&ConfigSet.getInstance().isMobileUpload()){
                    if(!getActivity().isFinishing()){
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle("3G/4G流量上传提醒")
                                .setMessage("您开启了流量上传功能，正在尝试使用流量上传。请注意您设备的流量消耗！若不允许流量上传，请在关闭此功能。");
                        builder.setPositiveButton("知道了", null);
                        builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startTargetActivity(Constant.KEY_FRAGMENT_TYPE_SETTINGS,"设置中心",null,0);
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                        upload(uploadVideoList);
                    }
                }
            }
        }
    }

    /**
     * 开始上传任务
     * @param uploadVideoList
     */
    private void upload(List<UploadVideoInfo> uploadVideoList) {
        VideoUploadTaskManager.getInstance().setUploadListener(this).addUploadTaskAndExcute(uploadVideoList);
    }


    //=====================================上传状态回调-子线程========================================
    /**
     * 上传开始
     * @param data
     */
    @Override
    public void uploadStart(UploadVideoInfo data) {
        updataUploadChangeState(data,100);
    }

    /**
     * 上传完成
     * @param data
     */
    @Override
    public void uploadSuccess(UploadVideoInfo data,String serviceExtras) {
        if(!TextUtils.isEmpty(serviceExtras)){
            try {
                JSONObject jsonObject=new JSONObject(serviceExtras);
                if(null!=jsonObject&&jsonObject.length()>0){
                    if(TextUtils.equals("1",jsonObject.getString("Status"))){
                        JSONObject resultData=new JSONObject(jsonObject.getString("data"));
                        if(null!=resultData&&resultData.length()>0){
                            data.setServiceCallBackBody(serviceExtras);
                            data.setServiceVideoId(resultData.getString("video_id"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        removeUploadListAdapterItem(data,1);
    }

    /**
     * 上传进度
     * @param data
     */
    @Override
    public void uploadProgress(UploadVideoInfo data) {
        updataUploadProgress(data);
    }

    /**
     * 上传失败回调
     * @param data 目标
     * @param errorCode 失败Code
     * @param errorMsg 消息
     */
    @Override
    public void uploadFail(UploadVideoInfo data, int stateCode,int errorCode, final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showCenterToast(errorMsg);
            }
        });
        switch (errorCode) {
            //文件被移除或权限被拒绝
            case Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND:
                removeUploadListAdapterItem(data,0);
                break;
            //客户端网络原因
            case Constant.UPLOAD_ERROR_CODE_CLIENTEXCEPTION:
                updataUploadChangeState(data,104);
                break;
            //服务端接收失败
            case Constant.UPLOAD_ERROR_CODE_SERVICEEXCEPTION:
                if(404==stateCode){
                    //上传的文件片段找不到
                    removeUploadListAdapterItem(data,0);
                }else{
                    updataUploadChangeState(data,104);
                }
                break;
        }
    }

    /**
     * 刷新上传的状态
     * @param data
     */
    private void updataUploadChangeState(UploadVideoInfo data,int stateType) {
        if(null==mUploadVideoInfoMap) mUploadVideoInfoMap=new TreeMap<>();
        data.setUploadType(stateType);
        data.setItemType(0);
        mUploadVideoInfoMap.put(data.getId(),data);
        ApplicationManager.getInstance().getVideoUploadDB().updateUploadVideoInfo(data);
        Message message= Message.obtain();
        message.what=0x100;
        mHandler.sendMessage(message);
    }


    /**
     * 删除列表元素
     * @param data
     */
    private void removeUploadListAdapterItem(UploadVideoInfo data,int state){
        try {
            ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(data);
        }catch (Exception e){

        }
        if(null!=data&&null!=mUploadVideoInfoMap&&mUploadVideoInfoMap.size()>0){
            mUploadVideoInfoMap.remove(data.getId());
            Message message=Message.obtain();
            message.what=0x100;
            message.obj=data;
            message.arg1=state;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 刷新上传进度条
     * @param data
     */
    private void updataUploadProgress(UploadVideoInfo data) {
        data.setUploadType(103);
        ApplicationManager.getInstance().getVideoUploadDB().updateUploadVideoInfo(data);
        Message message=Message.obtain();
        message.what=0x103;
        message.obj=data;
        mHandler.sendMessage(message);
    }

    /**
     * 刷新适配器,包含上传任何和合并任务,
     * @param data
     */
    private synchronized void updataProgress(UploadVideoInfo data) {
        try {
            if(null!=mUploadVideoListAdapter&&!getActivity().isFinishing()){
                if(null==mUploadVideoInfoMap){
                    mUploadVideoInfoMap=new TreeMap<>();
                }
                mUploadVideoInfoMap.put(data.getId(),data);
                mUploadVideoListAdapter.setNewData(mUploadVideoInfoMap);
                mUploadVideoListAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
        }
    }

    /**
     * 刷新视频上传UI界面
     */
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //开始上传、上传失败、上传完成界面全部刷新
            if(0x100==msg.what){
                if(null!=mUploadVideoListAdapter){
                    mUploadVideoListAdapter.setNewData(mUploadVideoInfoMap);
                    mUploadVideoListAdapter.notifyDataSetChanged();
                }
                //上传完成，展示分享面板
                if(1==msg.arg1){
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_VIDEO_CHANGED);//通知观察者准备刷新
                    UploadVideoInfo data = (UploadVideoInfo) msg.obj;
                    if(null!=data&&!TextUtils.isEmpty(data.getServiceVideoId())){
                        showShareViewState(data);
                    }
                }
            //上传/合并 进度刷新
            } else if(0x103==msg.what){
                UploadVideoInfo videoInfo = (UploadVideoInfo) msg.obj;
                if(null!=videoInfo){
                    updataProgress(videoInfo);
                }
            }
            super.handleMessage(msg);
        }
    };

    //==========================================上传成功=============================================

    /**
     * 切换分享面板的显示状态
     * @param data
     */
    private void showShareViewState(UploadVideoInfo data) {
        if(null!=data){
            final Activity runActivity = VideoApplication.getInstance().getRunActivity();
            if(null!=runActivity&&!runActivity.isFinishing()){
                UploadVideoFinlishDialog sharePopupWindow=new UploadVideoFinlishDialog(runActivity,data);
                sharePopupWindow.setOnItemClickListener(new UploadVideoFinlishDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(ShareMenuItemInfo shareMenuItemInfo, UploadVideoInfo uploadVideoInfo) {
                        if(null!=shareMenuItemInfo&&null!=uploadVideoInfo){
                            if(uploadVideoInfo.getIsPrivate()){
                                ToastUtils.showCenterToast(getResources().getString(R.string.home_share_error_tips));
                                return;
                            }
                            ShareInfo shareInfo=new ShareInfo();
                            String desp="";
                            if(!TextUtils.isEmpty(uploadVideoInfo.getVideoDesp())){
                                try {
                                    desp=URLDecoder.decode(uploadVideoInfo.getVideoDesp(),"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            shareInfo.setDesp(getResources().getString(R.string.share_home_tips)+(TextUtils.isEmpty(desp)?"":"["+desp+"]"));
                            shareInfo.setTitle(VideoApplication.getInstance().getUserData().getNickname()+"@你，我的新趣视频更新啦");
                            String url = "http://app.nq6.com/home/show/index?id=" + uploadVideoInfo.getServiceVideoId();
                            String token = MD5.hexdigest(url + "xinqu_123456");
                            shareInfo.setUrl(url+"&token=" + token);//+"&share_type="+"1"
                            shareInfo.setVideoID(uploadVideoInfo.getServiceVideoId());
                            shareInfo.setVideoPath(uploadVideoInfo.getFilePath());
                            if(SHARE_MEDIA.MORE==shareMenuItemInfo.getPlatform()){
                                Intent intent=new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT,shareInfo.getTitle());
                                intent.putExtra(Intent.EXTRA_TEXT, shareInfo.getUrl());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                runActivity.startActivity(Intent.createChooser(intent, getResources().getString(R.string.shared_to)));
                            }else{
                                ShareUtils.share(runActivity,shareInfo,shareMenuItemInfo.getPlatform(),null);
                            }
                        }
                    }
                });
                sharePopupWindow.show();
            }
        }
    }

    /**
     * 来自首页的刷新
     */
    public void fromMainUpdata(){
        if(null!=bindingView&&null!=bindingView.homeViewPager){
            if(null!=mFragmentList&&mFragmentList.size()>0){
                Fragment fragment = mFragmentList.get(bindingView.homeViewPager.getCurrentItem());
                if(fragment instanceof HomeFollowVideoFragment){
                    ((HomeFollowVideoFragment)fragment).fromMainUpdata();
                }else if(fragment instanceof HomeHotVideoFragment){
                    ((HomeHotVideoFragment)fragment).fromMainUpdata();
                }else if(fragment instanceof HomeTopicFragment){
                    ((HomeTopicFragment)fragment).fromMainUpdata();
                }
            }
        }
    }

    /**
     * 切换界面
     * @param childIndex
     */
    public void currentChildView(int childIndex) {
        if(null==mFragmentList||mFragmentList.size()<=0) return;
        if(childIndex<0||childIndex>=mFragmentList.size()) return;
        if(null!=bindingView&&null!=bindingView.homeViewPager){
            bindingView.homeViewPager.setCurrentItem(childIndex,true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        ImageCache.getInstance().recyler();
        VideoComposeProcessor.getInstance().onDestory();
    }

    //=======================================登录登出、视频合并=======================================
    /**
     * 观察者更新界面
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    //登出
                    case Constant.OBSERVABLE_ACTION_UNLOGIN:
                        hideNewMessageDot();
                        break;
                }
            }else if(arg instanceof UploadVideoInfo){
                final UploadVideoInfo data= (UploadVideoInfo) arg;
                if(null!=data){
                    switch (data.getComposeState()) {
                        //合并开始
                        case Constant.VIDEO_COMPOSE_STARTED:
                            updataProgress(data);
                            break;
                        //合并进度
                        case Constant.VIDEO_COMPOSE_PROGRESS:
                            updataProgress(data);
                            break;
                        //合并完成
                        case Constant.VIDEO_COMPOSE_FINLISHED:
                            data.setItemType(1);
                            updataProgress(data);
                            break;
                        //合并失败
                        case Constant.VIDEO_UPLOAD_ERROR:
                            showErrorToast(null,null,"合并失败！源文件不受支持");
                            if(null!=mUploadVideoInfoMap){
                                mUploadVideoInfoMap.remove(data.getId());
                                if(null!=mUploadVideoListAdapter){
                                    mUploadVideoListAdapter.setNewData(mUploadVideoInfoMap);
                                    mUploadVideoListAdapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        //开始上传,加入上传队列中
                        case Constant.VIDEO_UPLOAD_STARTED:
                            data.setItemType(0);
                            data.setUploadType(100);
                            updataProgress(data);
                            //接入的是WIFI
                            if(1==Utils.getNetworkType()){
                                VideoUploadTaskManager.getInstance().setUploadListener(this).addUploadTaskAndExcute(data);
                                //不是WIFI网络
                            }else {
                                //用户刚刚最新添加了任务
                                if(Utils.isCheckNetwork()){
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                                            .setTitle("视频上传提示")
                                            .setMessage("您的设备未接入WIFI网络，继续使用移动网络上传可能会产生额外的流量费用，是否继续上传?");
                                    builder.setPositiveButton("继续上传",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    VideoUploadTaskManager.getInstance().setUploadListener(HomeFragment.this).addUploadTaskAndExcute(data);
                                                }
                                            });
                                    builder.setNegativeButton("WIFI下自动上传", null);
                                    builder.setCancelable(false);
                                    builder.show();
                                    //不是WIFI也未接入网络
                                }else{
                                    //没有网络
                                    showErrorToast(null,null,"没有可用的网络连接");
                                }
                            }
                            break;
                    }
                }
            }
        }
    }
}
