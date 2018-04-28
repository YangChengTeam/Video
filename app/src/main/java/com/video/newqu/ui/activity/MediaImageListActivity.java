package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.video.newqu.R;
import com.video.newqu.adapter.ImageListAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.PhotoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.ActivityVideoListBinding;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.model.GridSpaceItemDecoration;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 */

public class MediaImageListActivity extends BaseActivity<ActivityVideoListBinding> implements View.OnClickListener{

    private ImageListAdapter mImageListAdapter;
    private  ArrayList<PhotoInfo> pitchPhotoInfos;//已经选中的相册列表
    public   int MAX_IMAGE_NUM=3;//默认最大可选择图片数量为3张

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        showToolBar(false);
        findViewById(R.id.view_state_bar).setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
        StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        initIntent();
        initAdapter();
    }

    private void initIntent() {
        Intent intent = getIntent();
        MAX_IMAGE_NUM=intent.getIntExtra("max_num",3);
        pitchPhotoInfos=intent.getParcelableArrayListExtra("photoinfos");
    }

    private void initAdapter() {
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(MediaImageListActivity.this, 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recyerView.addItemDecoration(new GridSpaceItemDecoration(MediaImageListActivity.this, 3,3,3,3));
        mImageListAdapter = new ImageListAdapter(null, ScreenUtils.getScreenWidth());
        mImageListAdapter.setMaxNum(MAX_IMAGE_NUM);//设置最大可选择的图片数量
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mImageListAdapter.setEmptyView(emptyViewbindView.getRoot());
        emptyViewbindView.ivItemIcon.setImageResource(R.drawable.iv_work_video_empty);
        emptyViewbindView.tvItemName.setText("未发现照片~");
        bindingView.recyerView.setAdapter(mImageListAdapter);
        mImageListAdapter.setOnItemChangeListener(new ImageListAdapter.OnItemChangeListener() {
            @Override
            public void onChange(int num) {
                bindingView.tvTitle.setText(num+"/"+MAX_IMAGE_NUM);
                bindingView.appBarLayout.setExpanded(true);//每次选中就让标题栏下来
            }
        });
        mImageListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mImageListAdapter.loadMoreEnd();//加载为空
            }
        },bindingView.recyerView);
    }



    @Override
    public void initViews() {
        bindingView.ivSubmit.setOnClickListener(this);
        bindingView.ivBack.setOnClickListener(this);
        bindingView.tvTitle.setText((null!=pitchPhotoInfos&&pitchPhotoInfos.size()>0)?pitchPhotoInfos.size()+"/"+MAX_IMAGE_NUM:0+"/"+MAX_IMAGE_NUM);//设置初始已选中和可选择图片数量
        bindingView.ivSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ToastUtils.showCenterToast("SD存储卡准备中");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡使用权限！");
            return;
        }
        showProgressDialog("获取本机照片中...",true);
       new LoadLocationImageTask().execute();
    }


    private class LoadLocationImageTask extends AsyncTask<Void,Void,List<PhotoInfo>>{
        @Override
        protected List<PhotoInfo> doInBackground(Void... params) {
            // 指定要查询的uri资源
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            // 获取ContentResolver
            ContentResolver contentResolver =getContentResolver();
            // 查询的字段
            String[] projection = { MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
            // 条件
            String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
            String[] selectionArgs = { "image/jpeg" };
            // 排序
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
            // 查询sd卡上的图片
            Cursor cursor = contentResolver.query(uri, projection, selection,
                    selectionArgs, sortOrder);

            List<PhotoInfo> photoInfoList=null;

            if (cursor != null) {
                photoInfoList=new ArrayList<PhotoInfo>();
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    // 获得图片的id
                    PhotoInfo photoInfo=new PhotoInfo();
                    photoInfo.setId(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media._ID)));
                    // 获得图片所在的路径(可以使用路径构建URI)
                    photoInfo.setImagePath(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA)));
                    photoInfoList.add(photoInfo);
                    if(null!=pitchPhotoInfos&&pitchPhotoInfos.size()>0){
                        for (PhotoInfo pitchPhotoInfo : pitchPhotoInfos) {
                            if(TextUtils.equals(pitchPhotoInfo.getId(),photoInfo.getId())){
                                photoInfo.setSelector(true); //回显选中的图片状态
                            }
                        }
                    }
                }
                // 关闭cursor
                cursor.close();
            }
            return photoInfoList;
        }

        @Override
        protected void onPostExecute(List<PhotoInfo> infoList) {
            super.onPostExecute(infoList);
            closeProgressDialog();
            if(null!=infoList&&infoList.size()>0){
                if(null!=mImageListAdapter) mImageListAdapter.setNewData(infoList);
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //提交
            case R.id.iv_submit:
                close();
                break;
            //返回
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    /**
     * 携带数据返回
     */
    private void close() {

        if(null!=mImageListAdapter){
            List<PhotoInfo> data = mImageListAdapter.getData();
            if(null!=data&&data.size()>0){
                List<PhotoInfo> newPhotoInfo=new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    PhotoInfo photoInfo = data.get(i);
                    if(photoInfo.isSelector()){
                        newPhotoInfo.add(photoInfo);
                    }
                }
                if(null!=newPhotoInfo&&newPhotoInfo.size()>0){
                    Intent intent=new Intent();
                    intent.putParcelableArrayListExtra("image_list",(ArrayList<? extends Parcelable>) newPhotoInfo);
                    setResult(0x1006,intent);
                    finish();
                }else{
                    showErrorToast(null,null,"请先选择至少一张照片");
                }
            }else{
                ToastUtils.showCenterToast("未发现照片");
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查SD读写权限
        RxPermissions.getInstance(MediaImageListActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){

                }else{
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaImageListActivity.this)
                                .setTitle("SD读取权限申请失败")
                                .setMessage("部分权限被拒绝，将无法使用本地视频相册功能，请先授予足够权限再使用视频扫描功能！授权成功后请重启开启本界面。是否现在去设置？");
                        builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SystemUtils.getInstance().startAppDetailsInfoActivity(MediaImageListActivity.this,141);
                            }
                        });
                        builder.show();
                        return;
                    }
                    ToastUtils.showCenterToast("请检查SD卡状态");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=pitchPhotoInfos) pitchPhotoInfos.clear(); pitchPhotoInfos=null;
        mImageListAdapter=null;
    }
}
