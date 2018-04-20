package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.adapter.LocationVideoListAdapter;
import com.video.newqu.bean.VideoInfos;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.OnItemClickListener;
import com.video.newqu.manager.DBBatchVideoUploadManager;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.VideoUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/17 10:13
 * 扫描本地视频文件上传至服务器弹窗
 */

public class LocationVideoUploadDialog extends BottomSheetDialog implements OnItemClickListener, View.OnClickListener {

    private final Activity context;
    private RecyclerView mRecyer_view;
    private Button mBt_submit;
    private LocationVideoListAdapter mLoactionVideoListAdapter;
    private List<String> filePath=new ArrayList<>();
    private List<WeiXinVideo> mVideo_list;


    public LocationVideoUploadDialog(Activity context ) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.location_video_upload);
        initLayoutParams();
        this.context=context;
        initViews();
        initAdapter();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_DAY,true);//标记为今天已扫描
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



    private void initViews() {
        mRecyer_view = (RecyclerView) findViewById(R.id.recyer_view);
        mBt_submit = (Button) findViewById(R.id.bt_submit);
        mBt_submit.setOnClickListener(this);
        ((Button) findViewById(R.id.bt_canel)).setOnClickListener(this);
    }

    private void initAdapter() {
        mRecyer_view.setLayoutManager(new GridLayoutManager(context,3));
        mLoactionVideoListAdapter = new LocationVideoListAdapter(mVideo_list,this);
        mRecyer_view.setAdapter(mLoactionVideoListAdapter);
    }


    /**
     * 刷新适配器
     */
    private void updataAdapter() {
        mLoactionVideoListAdapter.setNewData(mVideo_list);
        switchButtonState();
    }


    @Override
    public void OnItemClick(int position) {
        switchButtonState();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mVideo_list) mVideo_list.clear();mVideo_list=null;
        if(null!=filePath) filePath.clear(); filePath=null;
        mLoactionVideoListAdapter=null;
        mRecyer_view=null;mBt_submit=null;
    }
    /**
     * 切换按钮状态
     */
    private void switchButtonState() {

        if(null!=filePath){
            filePath.clear();
        }
        List<WeiXinVideo> videoInfos = mLoactionVideoListAdapter.getData();
        if(null!=videoInfos&&videoInfos.size()>0){
            for (int i = 0; i < videoInfos.size(); i++) {
                WeiXinVideo locationVideoInfo = videoInfos.get(i);
                if(null!=locationVideoInfo){
                    if(locationVideoInfo.getIsSelector()){
                        filePath.add(locationVideoInfo.getVideoPath());
                    }
                }
            }
        }
        if(null==filePath||filePath.size()==0){
            mBt_submit.setText("关闭");
        }else{
            mBt_submit.setText("一键分享");
        }
    }

    // TODO: 2017/8/3 关闭
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.bt_submit){
            if(null!=filePath&&filePath.size()>0){
                submitUpLoad();
            }else{
                dismiss();
            }

        }else if(id==R.id.bt_canel){
            dismiss();
        }
    }



    private void submitUpLoad() {

        if(null==filePath||filePath.size()==0){
            return;
        }
        DBBatchVideoUploadManager DBBatchVideoUploadManager = new DBBatchVideoUploadManager(context);
        //批量添加任务至上传队列
        for (String file : filePath) {
            final WeiChactVideoInfo weiChactVideoInfo = new WeiChactVideoInfo();
            File file1 = new File(file);
            weiChactVideoInfo.setFilePath(file);
            VideoInfos videoInfo = VideoUtils.getVideoInfo(file);
            if(null!=videoInfo){
                weiChactVideoInfo.setVideo_width(videoInfo.getVideo_width());
                weiChactVideoInfo.setVideo_height(videoInfo.getVideo_height());
                weiChactVideoInfo.setVideo_durtion(videoInfo.getVideo_durtion());
                weiChactVideoInfo.setCode_rate("2000");
                weiChactVideoInfo.setFrame_num(videoInfo.getVideo_fram());
            }
            weiChactVideoInfo.setFrame_num("20.01");
            weiChactVideoInfo.setID(System.currentTimeMillis());
            weiChactVideoInfo.setIsUploadFinlish(false);
            weiChactVideoInfo.setFileName(FileUtils.getFileName(file));
            weiChactVideoInfo.setSourceType(1);
            boolean b = DBBatchVideoUploadManager.insertNewUploadVideoInfo(weiChactVideoInfo);
        }

        if(null!=mOnDialogUploadListener){
            this.dismiss();
            mOnDialogUploadListener.onUploadVideo();
        }
    }

    public void setData(List<WeiXinVideo> weiXinVideos) {
        if(null==weiXinVideos) return;
        this.mVideo_list=weiXinVideos;
        updataAdapter();
    }

    public interface  OnDialogUploadListener{
        void onUploadVideo();
    }

    private OnDialogUploadListener mOnDialogUploadListener;

    public OnDialogUploadListener getOnDialogUploadListener() {
        return mOnDialogUploadListener;
    }

    public void setOnDialogUploadListener(OnDialogUploadListener onDialogUploadListener) {
        mOnDialogUploadListener = onDialogUploadListener;
    }
}
