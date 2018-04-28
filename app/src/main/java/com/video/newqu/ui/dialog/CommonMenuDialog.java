package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.video.newqu.R;
import com.video.newqu.adapter.VideoDetailsMenuAdapter;
import com.video.newqu.bean.VideoDetailsMenu;
import com.video.newqu.comadapter.BaseQuickAdapter;
import java.util.List;

/**
 * @time 2016/10/26 15:41
 * @des 通用的底部弹窗界面，调用者传入ListItem
 */
public class CommonMenuDialog extends BottomSheetDialog {

    private VideoDetailsMenuAdapter mVideoDetailsMenuAdapter;

    public CommonMenuDialog(Activity context) {
        super(context, R.style.CommendDialogStyle);
        setContentView(R.layout.dialog_commend_menu);
        initLayoutPrams();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mVideoDetailsMenuAdapter = new VideoDetailsMenuAdapter(null);
        recyclerView.setAdapter(mVideoDetailsMenuAdapter);
        mVideoDetailsMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<VideoDetailsMenu> data = mVideoDetailsMenuAdapter.getData();
                if(null!=data&&data.size()>0){
                    VideoDetailsMenu videoDetailsMenu = data.get(position);
                    if(null!=videoDetailsMenu&&null!=mOnItemClickListener){
                        CommonMenuDialog.this.dismiss();
                        mOnItemClickListener.onItemClick(videoDetailsMenu.getItemID());
                    }
                }
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMenuDialog.this.dismiss();
            }
        });
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


    public void  setData(List<VideoDetailsMenu> list){
        if(null!=mVideoDetailsMenuAdapter){
            mVideoDetailsMenuAdapter.setNewData(list);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int itemID);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
