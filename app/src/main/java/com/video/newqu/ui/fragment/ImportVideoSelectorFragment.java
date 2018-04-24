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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.adapter.MoivesListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.listener.OnItemLongClickListener;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentImportVideoSelectorBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.event.ScanMessageEvent;
import com.video.newqu.manager.ThreadManager;
import com.video.newqu.model.GridSpaceItemDecorationComent;
import com.video.newqu.ui.activity.MediaEditActivity;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.MediaStoreUtil;
import com.video.newqu.util.ScanWeixin;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-30 29:41
 * 本地视频列表选择
 */

public class ImportVideoSelectorFragment extends BaseFragment<FragmentImportVideoSelectorBinding,MainPresenter> {

    private String mVideo_folder_path;//读取置顶文件夹下的视频封面
    private MoivesListAdapter mVideoListAdapter;
    private ScanWeixin mScanWeiXin;
    private List<WeiXinVideo> mWeiXinVideos;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    @Override
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_video_selector;
    }

    /**
     * 传参
     * @param path
     * @param name
     * @return
     */
    public static ImportVideoSelectorFragment newInstance(String path, String name) {
        ImportVideoSelectorFragment importVideoSelectorFragment=new ImportVideoSelectorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("video_folder_path",path);
        importVideoSelectorFragment.setArguments(bundle);
        return importVideoSelectorFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 取参
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mVideo_folder_path = arguments.getString("video_folder_path");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        if(TextUtils.isEmpty(mVideo_folder_path)){
            showErrorToast(null,null,"目录不正确，请返回重试");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!TextUtils.isEmpty(mVideo_folder_path)&&new File(mVideo_folder_path).exists()&&null==mWeiXinVideos){
            if(null!= mVideoListAdapter){
                mVideoListAdapter.setNewData(null);
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showLoadingView("加载视频中...",R.drawable.loading_anim);
                loadVideo();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mScanWeiXin){
            mScanWeiXin.setScanEvent(false);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recyerView.addItemDecoration(new GridSpaceItemDecorationComent(ScreenUtils.dpToPxInt(1.5f)));
        mVideoListAdapter = new MoivesListAdapter(null);
        //设置空视图
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.showLoadingView("加载视频中...",R.drawable.loading_anim);
        mVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        }, bindingView.recyerView);
        bindingView.recyerView.setAdapter(mVideoListAdapter);

        //长按
        bindingView.recyerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showActionMenu(view,position);
            }
        });
        //点击
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
                            showErrorToast(null,null,"该视频格式不受支持，请换个视频重试！");
                            return;
                        }

                    }else{
                        showErrorToast(null,null,"视频不存在，请重新扫描重试！");
                        return;
                    }
                }
            }
        });
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
                                    ToastUtils.showCenterToast("删除失败!"+e.getMessage());
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
        if(null!=mVideo_folder_path){
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    mScanWeiXin = new ScanWeixin();
                    mScanWeiXin.setExts("mp4", "3gp", "mov");
                    mScanWeiXin.setEvent(true);
                    mWeiXinVideos = mScanWeiXin.scanFiles(mVideo_folder_path);
                    if(null!=mHandler){
                        mHandler.sendEmptyMessage(10011);
                    }
                }
            });
        }
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //分段刷新
            if(10010==msg.what){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("该文件夹下未找到视频文件~",R.drawable.ic_list_empty_icon,false);
                List<WeiXinVideo> weiXinVideos= (List<WeiXinVideo>) msg.obj;
                if(null!= mVideoListAdapter){
                    mVideoListAdapter.addData(weiXinVideos);
                }
            //加载完毕了
            }else if(10011==msg.what){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("该文件夹下未找到视频文件~",R.drawable.ic_list_empty_icon,false);
                if(null!= mVideoListAdapter){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
                        }
                    });
                }
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 开启新线程异步接收消息,接收子线程实时扫描的视频列表
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(ScanMessageEvent event) {
        if (null != event&&TextUtils.equals("updata_video_list",event.getMessage())) {
            List<WeiXinVideo> weiXinVideos = event.getWeiXinVideos();
            if(null!=weiXinVideos&&weiXinVideos.size()>0){
                if(null!=mHandler){
                    Message message=Message.obtain();
                    message.what=10010;
                    message.obj=weiXinVideos;
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        closeProgressDialog();
        if(null!=mScanWeiXin){
            mScanWeiXin.setScanEvent(false);
        }
        mScanWeiXin=null;
        if(null!=mWeiXinVideos){
            mWeiXinVideos.clear();
            mWeiXinVideos=null;
        }
        mEmptyViewbindView=null;
        super.onDestroy();
    }
}
