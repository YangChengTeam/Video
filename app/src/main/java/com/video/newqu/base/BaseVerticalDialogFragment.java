package com.video.newqu.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.video.newqu.R;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.SnackBarListener;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;

/**
 * TinyHung@outlook.com
 * 2017/3/17 15:46
 * 新特性下拉弹窗Fragment
 */

public abstract class BaseVerticalDialogFragment<VS extends ViewDataBinding> extends DialogFragment {

    // 子布局view
    protected VS bindingView;
    protected LoadingProgressView mLoadingProgressedView;
    private int fragmentMarginHeight=0;//距离顶部的距离，子类可定制决定

    protected void setFragmentMarginHeight(int fragmentMarginHeight) {
        this.fragmentMarginHeight = fragmentMarginHeight;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);//((ViewGroup) window.findViewById(android.R.id.content))
        View ll = inflater.inflate(R.layout.fragment_light_weight_base, (ViewGroup) window.findViewById(R.id.content),false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight()-ScreenUtils.dpToPxInt(fragmentMarginHeight));//这2行,和上面的一样,注意顺序就行;
        window.setWindowAnimations(R.style.HomeItemPopupAnimation);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount=0.0f;
        window.setAttributes(attributes);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        FrameLayout contentView = (FrameLayout) ll.findViewById(R.id.content_view);
        contentView.addView(bindingView.getRoot());
        return ll;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }


    protected abstract void initViews();
    public abstract int getLayoutId();

    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        getActivity().startActivity(intent);
    }


    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
        getActivity().startActivity(intent);
    }

    protected void saveLocationHistoryList(final FollowVideoList.DataBean.ListsBean data) {
        if(null==data) return;
        new Thread(){
            @Override
            public void run() {
                super.run();
                UserPlayerVideoHistoryList userLookVideoList=new UserPlayerVideoHistoryList();
                userLookVideoList.setUserName(TextUtils.isEmpty(data.getNickname())?"火星人":data.getNickname());
                userLookVideoList.setUserSinger("该宝宝没有个性签名");
                userLookVideoList.setUserCover(data.getLogo());
                userLookVideoList.setVideoDesp(data.getDesp());
                userLookVideoList.setVideoLikeCount(TextUtils.isEmpty(data.getCollect_times())?"0":data.getCollect_times());
                userLookVideoList.setVideoCommendCount(TextUtils.isEmpty(data.getComment_count())?"0":data.getComment_count());
                userLookVideoList.setVideoShareCount(TextUtils.isEmpty(data.getShare_times())?"0":data.getShare_times());
                userLookVideoList.setUserId(data.getUser_id());
                userLookVideoList.setVideoId(data.getVideo_id());
                userLookVideoList.setVideoCover(data.getCover());
                userLookVideoList.setUploadTime(data.getAdd_time());
                userLookVideoList.setAddTime(System.currentTimeMillis());
                userLookVideoList.setIs_interest(data.getIs_interest());
                userLookVideoList.setIs_follow(data.getIs_follow());
                userLookVideoList.setVideoPath(data.getPath());
                userLookVideoList.setVideoPlayerCount(TextUtils.isEmpty(data.getPlay_times())?"0":data.getPlay_times());
                userLookVideoList.setVideoType(TextUtils.isEmpty(data.getType())?"2":data.getType());
                ApplicationManager.getInstance().getUserPlayerDB().insertNewPlayerHistoryOfObject(userLookVideoList);
            }
        }.start();
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onInvisible() {}
    protected void onVisible() {}

    protected <T extends View> T getView(int id) {
        if(null==getView()) return null;
        return (T) getView().findViewById(id);
    }

    protected void showProgressDialog(String message,boolean isProgress,boolean isCancle){
        if(null==mLoadingProgressedView){
            mLoadingProgressedView = new LoadingProgressView(getActivity(),isProgress);
        }
        if(!getActivity().isFinishing()){
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.onSetCanceledOnTouchOutside(isCancle);
            mLoadingProgressedView.show();
        }
    }

    protected void showProgressDialog(String message,boolean isProgress){
        if(null==mLoadingProgressedView){
            mLoadingProgressedView = new LoadingProgressView(getActivity(),isProgress);
        }
        if(!getActivity().isFinishing()){
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    protected void closeProgressDialog(){
        if(null!=mLoadingProgressedView&&!getActivity().isFinishing()&&mLoadingProgressedView.isShowing()){
            mLoadingProgressedView.dismiss();
            mLoadingProgressedView=null;
        }
    }

    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
        }
    }

    protected void showNetWorkTips(){

        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        }, "没有可用的网络链接");
    }

    protected void showFinlishToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
