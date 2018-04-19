package com.video.newqu.ui.dialog;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.adapter.ShareAdapter;
import com.video.newqu.bean.ShareMenuItemInfo;
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
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        List<ShareMenuItemInfo> homeItemInfos = new ArrayList<>();
        homeItemInfos.add(new ShareMenuItemInfo("微信",R.drawable.iv_share_weichat));
        homeItemInfos.add(new ShareMenuItemInfo("微博",R.drawable.iv_share_weibo));
        homeItemInfos.add(new ShareMenuItemInfo("QQ",R.drawable.iv_share_qq));
        homeItemInfos.add(new ShareMenuItemInfo("朋友圈",R.drawable.iv_share_weichatfriend));
        homeItemInfos.add(new ShareMenuItemInfo("QQ空间",R.drawable.iv_share_qq_zone));
        homeItemInfos.add(new ShareMenuItemInfo("更多",R.drawable.iv_share_more));
        homeItemInfos.add(new ShareMenuItemInfo("复制链接",R.drawable.iv_share_copy));
        ShareAdapter adapter = new ShareAdapter(context,homeItemInfos);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null!=mOnShareItemClickListener){
                    ShareDialog.this.dismiss();
                    mOnShareItemClickListener.onItemClick(position);
                }
            }
        });
        ((TextView) findViewById(R.id.tv_canel)).setOnClickListener(new View.OnClickListener() {
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
        void onItemClick(int pistion);
    }

    private OnShareItemClickListener mOnShareItemClickListener;

    public void setOnItemClickListener(OnShareItemClickListener onShareItemClickListener) {
        mOnShareItemClickListener = onShareItemClickListener;
    }
}
