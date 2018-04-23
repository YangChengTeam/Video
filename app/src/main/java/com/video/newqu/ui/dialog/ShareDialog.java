package com.video.newqu.ui.dialog;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.adapter.ShareAdapter;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @time 2016/10/26 15:41
 * @des $分享选择界面
 */
public class ShareDialog extends BottomSheetDialog {

    public ShareDialog(AppCompatActivity context) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.dialog_share);
        initLayoutParams();
        List<ShareMenuItemInfo> homeItemInfos = new ArrayList<>();
        homeItemInfos.add(new ShareMenuItemInfo("微信",R.drawable.iv_share_weichat, SHARE_MEDIA.WEIXIN));
        homeItemInfos.add(new ShareMenuItemInfo("微博",R.drawable.iv_share_weibo,SHARE_MEDIA.SINA));
        homeItemInfos.add(new ShareMenuItemInfo("QQ",R.drawable.iv_share_qq,SHARE_MEDIA.QQ));
        homeItemInfos.add(new ShareMenuItemInfo("朋友圈",R.drawable.iv_share_weichatfriend,SHARE_MEDIA.WEIXIN_CIRCLE));
        homeItemInfos.add(new ShareMenuItemInfo("QQ空间",R.drawable.iv_share_qq_zone,SHARE_MEDIA.QZONE));
        homeItemInfos.add(new ShareMenuItemInfo("更多",R.drawable.iv_share_more,SHARE_MEDIA.MORE));
        homeItemInfos.add(new ShareMenuItemInfo("复制链接",R.drawable.iv_share_copy,null));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context,4,GridLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        final ShareAdapter shareAdapter = new ShareAdapter(homeItemInfos);
        recyclerView.setAdapter(shareAdapter);
        shareAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=mOnShareItemClickListener){
                    ShareDialog.this.dismiss();
                    List<ShareMenuItemInfo> data = shareAdapter.getData();
                    ShareMenuItemInfo shareMenuItemInfo = data.get(position);
                    mOnShareItemClickListener.onItemClick(shareMenuItemInfo);
                }
            }
        });
        findViewById(R.id.tv_canel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialog.this.dismiss();
            }
        });
    }

    /**
     * 设置Dialog显示在屏幕底部
     */
    private void initLayoutParams() {
        final LinearLayout llContent = (LinearLayout) findViewById(R.id.bottom_sheet);
        ViewTreeObserver viewTreeObserver = llContent.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                View view = findViewById(R.id.content_bg);
                view.getLayoutParams().height=llContent.getHeight();
                llContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }
    public interface OnShareItemClickListener{
        void onItemClick(ShareMenuItemInfo shareMenuItemInfo);
    }

    private OnShareItemClickListener mOnShareItemClickListener;

    public void setOnItemClickListener(OnShareItemClickListener onShareItemClickListener) {
        mOnShareItemClickListener = onShareItemClickListener;
    }
}
