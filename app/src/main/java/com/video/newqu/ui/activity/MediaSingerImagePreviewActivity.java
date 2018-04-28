package com.video.newqu.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivitySingerPreviewImageBinding;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @author TinyHung@Outlook.com
 * @des $预览单张图片
 */

public class MediaSingerImagePreviewActivity extends BaseActivity<ActivitySingerPreviewImageBinding> {

    private String imageUrl=null;//图片下载地址
    private boolean isDownload=false;
    private boolean download=true;

    public static void start(Activity context, String cover, ImageView imageView) {
        Intent intent = new Intent(context, MediaSingerImagePreviewActivity.class);
        intent.putExtra("imape_url",cover);
        if(null!=imageView){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, imageView, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }


    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView:
                    case R.id.root_view:
                        onBackPressed();
                        break;

                }
            }
        };
        bindingView.rootView.setOnClickListener(onClickListener);
        bindingView.imageView.setOnClickListener(onClickListener);

        bindingView.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SystemUtils.startVibrator(200);
                showPopupWindown();
                return false;
            }
        });
    }



    @Override
    public void initData() {
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imape_url");
        //设置背景封面
        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.iv_mine_bg)
                .crossFade()//渐变
                .thumbnail(0.1f)
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .skipMemoryCache(true)//跳过内存缓存
                .into(bindingView.imageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_preview_image);
        showToolBar(false);
        download=true;
    }

    /**
     * 显示保存图片弹窗
     */
    private void showPopupWindown() {
        View conentView= View.inflate(this,R.layout.popupwindown_copy_image_layout,null);
        conentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow popupWindow= new PopupWindow(conentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.LoadingProgressDialogStyle);
        popupWindow.setFocusable(true);
        conentView.findViewById(R.id.tv_save_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if(TextUtils.isEmpty(imageUrl)){
                    ToastUtils.showCenterToast("保存失败!");
                    return;
                }
                if(isDownload){
                    ToastUtils.showCenterToast("图片正在下载!");
                    return;
                }
                //检查SD卡权限
                RxPermissions.getInstance(MediaSingerImagePreviewActivity.this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(null!=aBoolean&&aBoolean){
                            new DownloadFileTask().execute(imageUrl);
                        }else{
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaSingerImagePreviewActivity.this)
                                    .setTitle("SD写入权限被拒绝!")
                                    .setMessage(getResources().getString(R.string.permissions_image_tips));
                            builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SystemUtils.getInstance().startAppDetailsInfoActivity(MediaSingerImagePreviewActivity.this,141);
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
        });
        conentView.findViewById(R.id.tv_canale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(conentView, Gravity.CENTER, 0, 0);
    }

    /**
     * 图片下载
     */
    private class DownloadFileTask extends AsyncTask<String,Integer,File> {

        private int laterate = 0;//当前已读字节

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDownload=true;
            bindingView.circleProgressbar.setVisibility(View.VISIBLE);
            bindingView.circleProgressbar.setProgress(0);
        }

        @Override
        protected File doInBackground(String... params) {
            File file=new File(Constant.IMAGE_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
            String name=FileUtils.getFileName(params[0]);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(900000);
                conn.setConnectTimeout(900000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                if(conn.getResponseCode()==200){
                    int length = conn.getContentLength();
                    int count = 0;
                    File outPutPath = new File(file.getAbsolutePath());
                    if (!outPutPath.exists()) {
                        outPutPath.mkdirs();
                    }
                    File apkDownloadPath = new File(outPutPath, name);
                    InputStream in = conn.getInputStream();
                    FileOutputStream os = new FileOutputStream(apkDownloadPath);
                    byte[] buffer = new byte[1024];
                    do {
                        int numread = in.read(buffer);
                        count += numread;
                        int progress = (int) (((float) count / length) * 100);// 得到当前进度
                        if (progress >= laterate + 1) {// 只有当前进度比上一次进度大于等于1，才可以更新进度
                            laterate = progress;
                            this.publishProgress(progress);
                        }
                        if (numread <= 0) {//下载完毕
                            break;
                        }
                        os.write(buffer, 0, numread);
                    } while (download);
                    in.close();
                    os.close();
                    return apkDownloadPath;
                }else{
                    Log.d("下载更新", "doInBackground: conn.getResponseCode()="+conn.getResponseCode());
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.toString();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(null!=bindingView) bindingView.circleProgressbar.setProgressNotInUiThread(values[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            isDownload=false;
            if(null!=bindingView) bindingView.circleProgressbar.setVisibility(View.GONE);
            if(null!=file&&file.exists()&&file.isFile()){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                MediaStore.Images.Media.insertImage(MediaSingerImagePreviewActivity.this.getApplicationContext().getContentResolver(), bitmap, file.getName(), "新趣用户头像");
                showFinlishToast(null,null,"已保存至相册"+file.getAbsolutePath());
            }else{
                showErrorToast(null,null,"下载失败！");
            }
        }
    }

    @Override
    public void onDestroy() {
        download=false;
        super.onDestroy();
        imageUrl=null;
        Runtime.getRuntime().gc();
    }
}
