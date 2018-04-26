package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.video.newqu.R;
import com.video.newqu.adapter.LocationVideoListAdapter;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.bean.VideoInfos;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.DialogWeixinVideoBinding;
import com.video.newqu.listener.OnItemClickListener;
import com.video.newqu.manager.DBBatchVideoUploadManager;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.VideoUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/17 10:13
 * 微信文件夹下面的视频列表，多选，上传
 */

public class WinXinVideoListDialog extends BaseDialog<DialogWeixinVideoBinding> {

    private LocationVideoListAdapter mListAdapter;

    public WinXinVideoListDialog(@NonNull Activity context) {
        super(context,R.style.CommendDialogStyle);
        setContentView(R.layout.dialog_weixin_video);
        initLayoutParams();
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
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancle:
                        dismiss();
                        break;
                    case R.id.btn_submit:
                        onSubmit();
                        break;
                }
            }
        };
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        bindingView.btnCancle.setOnClickListener(onClickListener);
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false));
        mListAdapter = new LocationVideoListAdapter(null, new OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //更新底部按钮
                List<WeiXinVideo> data = mListAdapter.getData();
                if(null!=data&&data.size()>0){
                    String submitText="关闭";
                    int count=0;
                    for (WeiXinVideo item : data) {
                        if(item.getIsSelector()){
                            count++;
                        }
                    }
                    if(count>0){
                        submitText="分享 "+count+"/"+data.size();
                    }
                    bindingView.btnSubmit.setText(submitText);
                }
            }
        });
        bindingView.recyerView.setAdapter(mListAdapter);
    }

    public void setData(List<WeiXinVideo> data){
        if(null!=mListAdapter&&isShowing()){
            mListAdapter.setNewData(data);
            if(null!=data&&data.size()>0){
                bindingView.btnSubmit.setText("分享 "+data.size()+"/"+data.size());
            }
            SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_DAY,true);//标记为已扫描状态
        }
    }

    /**
     * 点击了上传按钮
     */
    private void onSubmit() {
        if(TextUtils.equals("关闭",bindingView.btnSubmit.getText().toString())){
            dismiss();
        }else{
            List<String> selectedList = getSelectedList();
            if(null!=selectedList&&selectedList.size()>0){
                DBBatchVideoUploadManager DBBatchVideoUploadManager = new DBBatchVideoUploadManager(getContext());
                //批量添加任务至上传队列
                for (String file : selectedList) {
                    final WeiChactVideoInfo weiChactVideoInfo = new WeiChactVideoInfo();
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
                    DBBatchVideoUploadManager.insertNewUploadVideoInfo(weiChactVideoInfo);
                }
                if(null!=mOnDialogUploadListener){
                    dismiss();
                    mOnDialogUploadListener.onUpload();
                }
            }else{
                dismiss();
            }
        }
    }

    /**
     * 返回选中的列表
     * @return
     */
    public List<String> getSelectedList(){
        List<String> selectedList;
        if(null!=mListAdapter){
            List<WeiXinVideo> data = mListAdapter.getData();
            if(null!=data&&data.size()>0){
                selectedList=new ArrayList<>();
                for (WeiXinVideo item : data) {
                    if(item.getIsSelector()){
                        selectedList.add(item.getVideoPath());
                    }
                }
                return selectedList;
            }
        }
        return null;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mOnDialogUploadListener){
            mOnDialogUploadListener.onDissmiss();
        }
    }

    public interface  OnDialogUploadListener{
        void onUpload();
        void onDissmiss();
    }
    private OnDialogUploadListener mOnDialogUploadListener;

    public void setOnDialogUploadListener(OnDialogUploadListener onDialogUploadListener) {
        mOnDialogUploadListener = onDialogUploadListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null!=mListAdapter) mListAdapter.setNewData(null);mListAdapter =null;
    }
}
