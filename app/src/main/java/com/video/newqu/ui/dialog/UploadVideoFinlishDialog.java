package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.bumptech.glide.Glide;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.adapter.UploadFinlishShareAdapter;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.DialogUploadShareBinding;
import com.video.newqu.util.CommonUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/4/23
 * 视频上传完成的分享弹窗
 */

public class UploadVideoFinlishDialog extends BaseDialog<DialogUploadShareBinding> {

    private UploadVideoInfo mUploadVideoInfo;

    public UploadVideoFinlishDialog(Activity context, UploadVideoInfo uploadVideoInfo) {
        super(context,R.style.CommendDialogStyle);
        this.mUploadVideoInfo=uploadVideoInfo;
        setContentView(R.layout.dialog_upload_share);
        initLayoutParams();
    }

    @Override
    public void initViews() {
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),6,LinearLayoutManager.VERTICAL,false);
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
                        mOnItemClickListener.onItemClick(shareMenuItemInfo,mUploadVideoInfo);
                    }
                }
            }
        });
        bindingView.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideoFinlishDialog.this.dismiss();
            }
        });
        if(null!=mUploadVideoInfo){
            File file=new File(mUploadVideoInfo.getFilePath());
            if(file.exists()){
                Glide
                    .with(getContext())
                    .load(Uri.fromFile(file))
                    .error(R.drawable.iv_video_errror)
                    .animate(R.anim.item_alpha_in)
                    .skipMemoryCache(true)
                    .into(bindingView.ivCover);
            }
        }
    }


    private void initLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= FrameLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
        ViewTreeObserver viewTreeObserver = bindingView.bottomSheet.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                bindingView.contentBg.getLayoutParams().height=bindingView.bottomSheet.getHeight();
                bindingView.bottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Glide.with(getContext()).pauseRequests();
        mUploadVideoInfo=null;mOnItemClickListener=null;
    }

    @Override
    public void show() {
        super.show();
        bindingView.finlishView.setmResultType(1);//成功状态
        bindingView.finlishView.setColor(CommonUtils.getColor(R.color.app_style));
        bindingView.finlishView.initPath();
    }

    public interface  OnItemClickListener{
        void onItemClick(ShareMenuItemInfo shareMenuItemInfo, UploadVideoInfo uploadVideoInfo);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
