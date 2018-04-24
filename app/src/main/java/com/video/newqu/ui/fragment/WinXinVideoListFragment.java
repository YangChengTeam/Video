package com.video.newqu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.adapter.LocationVideoListAdapter;
import com.video.newqu.base.BaseDialogFragment;
import com.video.newqu.bean.VideoInfos;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentWeixinVideoBinding;
import com.video.newqu.listener.OnItemClickListener;
import com.video.newqu.manager.DBBatchVideoUploadManager;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.VideoUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/17 10:13
 * 微信文件夹下面的视频列表，多选，上传
 */

public class WinXinVideoListFragment extends BaseDialogFragment<FragmentWeixinVideoBinding,MainPresenter>{

    private static List<WeiXinVideo> mData;
    private LocationVideoListAdapter mListAdapter;

    public static WinXinVideoListFragment newInstance(List<WeiXinVideo> weiXinVideos) {
        WinXinVideoListFragment fragment=new WinXinVideoListFragment();
        mData=weiXinVideos;
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_weixin_video;
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
        mListAdapter = new LocationVideoListAdapter(mData, new OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //更新底部按钮
                List<WeiXinVideo> data = mListAdapter.getData();
                if(null!=data&&data.size()>0){
                    String submitText="关闭";
                    for (WeiXinVideo item : data) {
                        if(item.getIsSelector()){
                            submitText="一键分享";
                            break;
                        }
                    }
                    bindingView.btnSubmit.setText(submitText);
                }
            }
        });
        bindingView.recyerView.setAdapter(mListAdapter);
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_DAY,true);
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


    public interface  OnDialogUploadListener{
        void onUpload();
    }
    private OnDialogUploadListener mOnDialogUploadListener;

    public void setOnDialogUploadListener(OnDialogUploadListener onDialogUploadListener) {
        mOnDialogUploadListener = onDialogUploadListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mData) mData.clear();
        if(null!=mListAdapter) mListAdapter.setNewData(null);
        mListAdapter =null;mOnDialogUploadListener=null;mData=null;
    }
}
