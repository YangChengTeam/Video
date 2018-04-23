package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.adapter.UploadFinlishShareAdapter;
import com.video.newqu.base.BasePopupWindow;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.PopupwindownHomeShareBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/4/23
 * 视频上传完成的分享弹窗
 */

public class HomeSharePopupWindow extends BasePopupWindow<PopupwindownHomeShareBinding> {

    private final UploadVideoInfo uploadVideoInfo;
    public HomeSharePopupWindow(Activity context, UploadVideoInfo uploadVideoInfo) {
        super(context);
        this.uploadVideoInfo=uploadVideoInfo;
    }

    @Override
    public int setAnimationStyle() {
        return R.style.PopupToolBarAnimation;
    }

    @Override
    public int setLayoutID() {
        return R.layout.popupwindown_home_share;
    }

    @Override
    public void initViews() {
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,6,LinearLayoutManager.VERTICAL,false);
        bindingView.recylerView.setHasFixedSize(true);
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        final List<ShareMenuItemInfo> shareMenuItemInfos=new ArrayList<>();
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_wechat_normal, SHARE_MEDIA.WEIXIN));
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_qq_normal,SHARE_MEDIA.QQ));
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_sina_normal,SHARE_MEDIA.SINA));
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_wxcircle_normal,SHARE_MEDIA.WEIXIN_CIRCLE));
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_qzone_normal,SHARE_MEDIA.QZONE));
        shareMenuItemInfos.add(new ShareMenuItemInfo(null,R.drawable.ic_share_more,SHARE_MEDIA.MORE));
        final UploadFinlishShareAdapter shareAdapter=new UploadFinlishShareAdapter(shareMenuItemInfos);
        bindingView.recylerView.setAdapter(shareAdapter);
        shareAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=mOnItemClickListener){
                    List<ShareMenuItemInfo> data = shareAdapter.getData();
                    ShareMenuItemInfo shareMenuItemInfo = data.get(position);
                    if(null!=shareMenuItemInfo){
                        HomeSharePopupWindow.this.dismiss();
                        mOnItemClickListener.onItemClick(shareMenuItemInfo,uploadVideoInfo);
                    }
                }
            }
        });
        bindingView.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeSharePopupWindow.this.dismiss();
            }
        });
        ViewTreeObserver viewTreeObserver = bindingView.llContent.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                bindingView.contentBg.getLayoutParams().height=bindingView.llContent.getHeight();
            }
        });
        bindingView.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                HomeSharePopupWindow.this.dismiss();
                return false;
            }
        });
    }

    @Override
    public void initData() {
        bindingView.ivCover.setImageResource(R.drawable.iv_video_errror);
    }

    public interface  OnItemClickListener{
        void onItemClick(ShareMenuItemInfo shareMenuItemInfo, UploadVideoInfo uploadVideoInfo);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
