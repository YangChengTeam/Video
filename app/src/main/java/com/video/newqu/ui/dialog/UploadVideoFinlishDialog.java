package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.adapter.UploadFinlishShareAdapter;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.view.widget.FinlishView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/4/23
 * 视频上传完成的分享弹窗
 */

public class UploadVideoFinlishDialog extends BottomSheetDialog {

    private UploadVideoInfo mUploadVideoInfo;
    private UploadFinlishShareAdapter mShareAdapter;

    public UploadVideoFinlishDialog(Activity context, UploadVideoInfo uploadVideoInfo) {
        super(context,R.style.CommendDialogStyle);
        this.mUploadVideoInfo=uploadVideoInfo;
        setContentView(R.layout.dialog_upload_share);
        initLayoutPrams();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        final List<ShareMenuItemInfo> shareMenuItemInfos=new ArrayList<>();
        shareMenuItemInfos.add(new ShareMenuItemInfo("微信好友",R.drawable.ic_share_wechat_normal, SHARE_MEDIA.WEIXIN));
        shareMenuItemInfos.add(new ShareMenuItemInfo("朋友圈",R.drawable.ic_share_wxcircle_normal,SHARE_MEDIA.WEIXIN_CIRCLE));
        shareMenuItemInfos.add(new ShareMenuItemInfo("QQ好友",R.drawable.ic_share_qq_normal,SHARE_MEDIA.QQ));
        shareMenuItemInfos.add(new ShareMenuItemInfo("QQ空间",R.drawable.ic_share_qzone_normal,SHARE_MEDIA.QZONE));
        shareMenuItemInfos.add(new ShareMenuItemInfo("微博",R.drawable.ic_share_sina_normal,SHARE_MEDIA.SINA));
        shareMenuItemInfos.add(new ShareMenuItemInfo("更多",R.drawable.ic_share_more,SHARE_MEDIA.MORE));
        mShareAdapter = new UploadFinlishShareAdapter(shareMenuItemInfos);
        recyclerView.setAdapter(mShareAdapter);
        mShareAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=mOnItemClickListener){
                    List<ShareMenuItemInfo> data = mShareAdapter.getData();
                    ShareMenuItemInfo shareMenuItemInfo = data.get(position);
                    if(null!=shareMenuItemInfo){
                        mOnItemClickListener.onItemClick(shareMenuItemInfo,mUploadVideoInfo);
                    }
                }
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
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
                    .into((ImageView) findViewById(R.id.iv_cover));
            }
        }
    }

    protected void initLayoutPrams(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        attributes.height= FrameLayout.LayoutParams.WRAP_CONTENT;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mShareAdapter) mShareAdapter.setNewData(null);
        mShareAdapter=null;mUploadVideoInfo=null;mOnItemClickListener=null;
    }

    @Override
    public void show() {
        super.show();
        FinlishView finlishView = (FinlishView) findViewById(R.id.finlish_view);
        finlishView.setmResultType(1);//成功状态
        finlishView.setColor(CommonUtils.getColor(R.color.app_style));
        finlishView.initPath();
    }

    public interface  OnItemClickListener{
        void onItemClick(ShareMenuItemInfo shareMenuItemInfo, UploadVideoInfo uploadVideoInfo);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
