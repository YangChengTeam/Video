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
import android.widget.LinearLayout;
import android.widget.TextView;
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

    private final Context context;
    private VideoDetailsMenuAdapter mVideoDetailsMenuAdapter;

    public CommonMenuDialog(Activity context) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_video_details_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.context=context;
        initLayoutParams();
        initAdapter();
    }

    private void initAdapter() {
        RecyclerView recyerView = (RecyclerView) findViewById(R.id.recyer_view);
        recyerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recyerView.setHasFixedSize(true);
        mVideoDetailsMenuAdapter = new VideoDetailsMenuAdapter(null);
        recyerView.setAdapter(mVideoDetailsMenuAdapter);
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
        ((TextView) findViewById(R.id.tv_canel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMenuDialog.this.dismiss();
            }
        });
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
}
