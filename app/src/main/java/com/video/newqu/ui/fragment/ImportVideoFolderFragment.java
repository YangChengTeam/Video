package com.video.newqu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.video.newqu.R;
import com.video.newqu.adapter.ImportVideoFolderAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.VideoFolder;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentVideoFolderBinding;
import com.video.newqu.ui.activity.MediaLocationVideoListActivity;
import com.video.newqu.util.ImageCache;
import com.video.newqu.util.StringUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.VideoUtils;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-30 20:26
 * 本地视频文件选择
 */

public class ImportVideoFolderFragment extends BaseFragment<FragmentVideoFolderBinding> implements BaseQuickAdapter.RequestLoadMoreListener {

    private ImportVideoFolderAdapter mImportVideoFolderAdapter;
    private MediaLocationVideoListActivity mActivity;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MediaLocationVideoListActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ToastUtils.showCenterToast("SD存储卡准备中");
            closeProgressDialog();
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡授予本软件的使用权限！");
            return;
        }
        showLoadingView("加载相册列表中...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createFolderList();
            }
        }, Constant.POST_DELAYED_ADD_DATA_TIME);
    }

    @Override
    protected void initViews() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_folder;
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        bindingView.recyerView.setLayoutManager(mLinearLayoutManager);
        mImportVideoFolderAdapter = new ImportVideoFolderAdapter(null,getActivity());
        mImportVideoFolderAdapter.setOnLoadMoreListener(this);
        //设置空视图
        mImportVideoFolderAdapter.setEmptyView(R.layout.location_folder_file_empty_layout, (ViewGroup) bindingView.recyerView.getParent());
        bindingView.recyerView.setAdapter(mImportVideoFolderAdapter);
        //条目的点击事件
        bindingView.recyerView.addOnItemTouchListener(new com.video.newqu.comadapter.listener.OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<VideoFolder> folderlist= adapter.getData();
                if(null!=folderlist&&folderlist.size()>0){
                    final VideoFolder item = folderlist.get(position);
                    if (item != null && StringUtils.isNotEmpty(item.path)) {
                        if(null!=mActivity){
                            mActivity.addFolderFragment(item.path,item.name);
                        }
                    }
                }
            }
        });
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(7890==msg.what){
                showContentView();
                List<VideoFolder> videoFolders= (List<VideoFolder>) msg.obj;
                if(null!=mImportVideoFolderAdapter){
                    mImportVideoFolderAdapter.setNewData(videoFolders);
                }
            }else if(789==msg.what){
                showContentView();
            }
        }
    };

    /**
     * 先预加载所有的视频文件列表出来
     */
    private void createFolderList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<VideoFolder> videoFolders=VideoUtils.getVideoFolderList();
                    if(null!=mHandler){
                        Message message=Message.obtain();
                        message.obj=videoFolders;
                        message.what=7890;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(null!=mHandler){
                        mHandler.sendEmptyMessage(789);
                    }
                }
            }
        }).start();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //进入
        if(enter){
            return AnimationUtils.loadAnimation(getActivity(), R.anim.menu_enter);
        //销毁
        }else{
            return AnimationUtils.loadAnimation(getActivity(), R.anim.menu_exit);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }

    @Override
    public void onDestroyView() {
        ImageCache.getInstance().recyler();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoadMoreRequested() {
        if(null!=mImportVideoFolderAdapter){
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    mImportVideoFolderAdapter.loadMoreEnd();
                }
            });
        }
    }
}
