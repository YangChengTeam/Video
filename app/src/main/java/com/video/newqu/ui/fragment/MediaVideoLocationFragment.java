package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.adapter.MoivesListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentLocationVideoListBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ThreadManager;
import com.video.newqu.model.RecyclerViewSpacesItem;
import com.video.newqu.ui.activity.MediaEditActivity;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.MediaStoreUtil;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;
import java.io.File;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/7.
 * 本机视频列表
 */

public class MediaVideoLocationFragment extends BaseFragment<FragmentLocationVideoListBinding,MainPresenter> {

    private MoivesListAdapter mVideoListAdapter;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    @Override
    protected void initViews() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_location_video_list;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        loadVideo();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        if(null==bindingView) return;
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recyerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1.5f)));
        mVideoListAdapter = new MoivesListAdapter(null);
        //设置空视图
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.showLoadingView("扫描视频中...",R.drawable.loading_anim);
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mVideoListAdapter.setEnableLoadMore(true);
            }
        },bindingView.recyerView);

        bindingView.recyerView.setAdapter(mVideoListAdapter);
        //长按
        mVideoListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showActionMenu(view,position);
                return false;
            }
        });
        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<WeiXinVideo> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>0){
                    WeiXinVideo item = data.get(position);
                    if(null!=item&&null!=item.getVideoPath()&&new File(item.getVideoPath()).isFile()){
                        if(MediaStoreUtil.isSupport(item.getVideoPath(),"mp4","mov","3gp")){
                            if(item.getVideoDortion()<Constant.MEDIA_VIDEO_EDIT_MIN_DURTION){
                                showErrorToast(null,null,"视频长度小于5秒！");
                                return;
                            }
                            Intent intent=new Intent(getActivity(),MediaEditActivity.class);
                            intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_VIDEO_PATH,item.getVideoPath());
                            intent.putExtra(Constant.KEY_MEDIA_RECORD_PRAMER_SOURCETYPE,2);//选择视频上传
                            startActivity(intent);
                            return;
                        }else{
                            showErrorToast(null,null,"抱歉，该视频格式不受支持，请换个视频重试");
                            return;
                        }
                    }else{
                        showErrorToast(null,null,"视频不存在，请重新扫描重试！");
                        return;
                    }
                }
            }
        });
//        bindingView.recyerView.addOnItemTouchListener(new OnItemLongClickListener() {
//            @Override
//            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//
//            }
//        });
    }

    /**
     * 显示删除菜单
     */
    private void showActionMenu(View view, final int position) {
        if(null!= mVideoListAdapter){
            List<WeiXinVideo> data = mVideoListAdapter.getData();
            if(null!=data&&data.size()>0){
                final WeiXinVideo weiXinVideo = data.get(position);
                if(null!=weiXinVideo){
                    PopupMenu actionMenu = new PopupMenu(getActivity(), view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
                    actionMenu.inflate(R.menu.detele_video_action);
                    actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.menu_detele){
                                try {
                                    boolean flag = FileUtils.deleteFile(weiXinVideo.getVideoPath());
                                    if(flag&&null!= mVideoListAdapter){
                                        mVideoListAdapter.remove(position);
                                    }else{
                                        ToastUtils.showCenterToast("删除失败!");
                                    }
                                }catch (Exception e){
                                    ToastUtils.showCenterToast("删除失败!");
                                }
                            }
                            return false;
                        }
                    });
                    actionMenu.show();
                }
            }
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if(10011==msg.what){
               if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("在相册中未找到视频！试试右上角的相册列表吧~",R.drawable.ic_list_empty_icon,false);
                List<WeiXinVideo> videoInfo = (List<WeiXinVideo>) msg.obj;
                if(null!= mVideoListAdapter){
                    mVideoListAdapter.setNewData(videoInfo);
                    mVideoListAdapter.loadMoreEnd();
                }
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 扫描本机相册的所有视频
     */
    private void loadVideo() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("SD存储卡准备中");
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

        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                List<WeiXinVideo> videoInfos = MediaStoreUtil.getVideoInfo(getActivity(),"mp4","mov","3gp");
                if(null!=mHandler){
                    Message message=Message.obtain();
                    message.what=10011;
                    message.obj=videoInfos;
                    mHandler.sendMessage(message);
                }

            }
        });
    }


    public void updataAdapter(List<WeiXinVideo> weiXinVideos) {
        if(null!= mVideoListAdapter){
            mVideoListAdapter.setNewData(weiXinVideos);
            bindingView.recyerView.post(new Runnable() {
                @Override
                public void run() {
                    mVideoListAdapter.loadMoreEnd();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEmptyViewbindView=null;
    }
}
