package com.video.newqu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ImageCache;
import com.video.newqu.util.attach.LoadLocalShortImgTask;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.view.widget.CircleProgressView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TinyHung@outlook.com
 * 2017/7/11 14:25
 * 视频合并和上传列表，共用 itemType: 0：上传 1：合并
 * uploadType 100:等待上传 103正在上传 104：上传失败
 */

public class HomeUploadVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final LayoutInflater mLayoutInflater;
    private TreeMap<Long, UploadVideoInfo> mUploadVideoList;
    private final List<Long> mMapsKey=new ArrayList<>();
    private List<String> taskList=null;
    private Map<Integer,Integer> mIntegerMap=new HashMap<>();

    public HomeUploadVideoListAdapter(Context context, TreeMap<Long, UploadVideoInfo> uploadVideoInfoMap) {
        this.mUploadVideoList=uploadVideoInfoMap;
        mLayoutInflater = LayoutInflater.from(context);
        if(null!=mUploadVideoList&&mUploadVideoList.size()>0){
            Iterator<Map.Entry<Long, UploadVideoInfo>> iterator = mUploadVideoList.entrySet().iterator();
            if(null!=mMapsKey) mMapsKey.clear();
            while (iterator.hasNext()){
                Map.Entry<Long, UploadVideoInfo> next = iterator.next();
                mMapsKey.add(next.getKey());
            }
        }else{
            if(null!=mMapsKey) mMapsKey.clear();
        }
        taskList=new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.home_upload_video_item, null);
        return new VideoHolder(inflate);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoHolder videoHolder= (VideoHolder) holder;
        if(null!=videoHolder){
            if(null!=mMapsKey&&mMapsKey.size()>0){
                Long key = mMapsKey.get(position);
                if(0!=key){
                    final UploadVideoInfo uploadVideoInfo = mUploadVideoList.get(key);
                    if(null!=uploadVideoInfo){
                        videoHolder.circleProgressbar.setProgress(uploadVideoInfo.getUploadProgress());
                        try {
                            String videoPath=uploadVideoInfo.getFilePath();
                            if(1==uploadVideoInfo.getItemType()){
                                //合并任务
                                videoHolder.tv_item_title.setTextColor(CommonUtils.getColor(R.color.white));
                                videoHolder.tv_item_title.setText("合并中");
                                videoHolder.iv_item_icon.setClickable(false);
                                videoPath=uploadVideoInfo.getResoucePath();
                            }else{
                                //上传任务
                                String state="等待中";
                                switch (uploadVideoInfo.getUploadType()) {
                                    case 100:
                                        state="等待中";
                                        videoHolder.tv_item_title.setTextColor(CommonUtils.getColor(R.color.white));
                                        videoHolder.iv_item_icon.setClickable(false);
                                        break;
                                    //暂停了上传
                                    case 101:
                                        state="暂停中";
                                        videoHolder.tv_item_title.setTextColor(CommonUtils.getColor(R.color.yellow));
                                        videoHolder.iv_item_icon.setClickable(true);
                                        break;
                                    case 103:
                                        state="上传中";
                                        videoHolder.tv_item_title.setTextColor(CommonUtils.getColor(R.color.white));
                                        videoHolder.iv_item_icon.setClickable(false);
                                        break;
                                    case 104:
                                        state="上传失败";
                                        videoHolder.tv_item_title.setTextColor(CommonUtils.getColor(R.color.app_red_style));
                                        videoHolder.iv_item_icon.setClickable(true);
                                        break;
                                }
                                videoHolder.tv_item_title.setText(state);
                            }
                            //封面设置
                            Bitmap bitmap = ImageCache.getInstance().get(uploadVideoInfo.getFilePath());
                            if(null!=bitmap){
                                videoHolder.iv_item_icon.setImageBitmap(bitmap);
                            }else{
                                //保证不会重复去创建对象加载视频封面
                                if(null!=taskList&&taskList.contains(videoPath))return;
                                if(null==taskList) taskList=new ArrayList<>();
                                taskList.add(videoPath);
                                new LoadLocalShortImgTask(videoHolder.iv_item_icon,R.drawable.iv_video_square_errror,videoPath).execute();
                            }
                        }catch (Exception e){

                        }
                        //保证不会重复的去创建监听器
                        if(null!=mIntegerMap&&mIntegerMap.containsKey(position))return;
                        videoHolder.iv_item_icon.setOnClickListener(new UploadClickListenr(uploadVideoInfo));
                        videoHolder.iv_item_icon.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                if(1==uploadVideoInfo.getItemType()){
                                    return true;
                                }
                                SystemUtils.startVibrator(200);
                                if(null!=mUploadItemClickListener){
                                    mUploadItemClickListener.onLongClickDetele(uploadVideoInfo);
                                }
                                return false;
                            }
                        });
                        if(null==mIntegerMap) mIntegerMap=new HashMap<>();
                        mIntegerMap.put(position,position);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return null==mUploadVideoList?0:mUploadVideoList.size();
    }




    public List<Long> getKeyList() {
        return mMapsKey;
    }

    public class VideoHolder extends RecyclerView.ViewHolder{

        public ImageView iv_item_icon;
        public TextView tv_item_title;
        public CircleProgressView circleProgressbar;
        public RelativeLayout re_upload_view;

        public VideoHolder(View itemView) {
            super(itemView);
            iv_item_icon= (ImageView) itemView.findViewById(R.id.iv_item_icon);
            tv_item_title= (TextView) itemView.findViewById(R.id.tv_item_title);
            circleProgressbar= (CircleProgressView) itemView.findViewById(R.id.circleProgressbar);
            re_upload_view= (RelativeLayout) itemView.findViewById(R.id.re_upload_view);
        }
    }

    /**
     * 设置最新的数据
     * @param uploadVideoInfoMap
     */
    public void setNewData(TreeMap<Long, UploadVideoInfo> uploadVideoInfoMap){
        this.mUploadVideoList=uploadVideoInfoMap;
        if(null!=mUploadVideoList&&mUploadVideoList.size()>0){
            Iterator<Map.Entry<Long, UploadVideoInfo>> iterator = mUploadVideoList.entrySet().iterator();
            if(null!=mMapsKey) mMapsKey.clear();
            while (iterator.hasNext()){
                Map.Entry<Long, UploadVideoInfo> next = iterator.next();
                mMapsKey.add(next.getKey());
            }
        }else{
            if(null!=mMapsKey) mMapsKey.clear();
            if(null!=taskList) taskList.clear();
            if(null!=mIntegerMap) mIntegerMap.clear();
        }
    }

    /**
     * 处理所有的点击事件
     */
    private class UploadClickListenr implements View.OnClickListener {

        private final UploadVideoInfo data;

        public UploadClickListenr(UploadVideoInfo item) {
            this.data=item;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //重新上传
                case R.id.iv_item_icon:
                    if(0==data.getItemType()){
                        if(null!=mUploadItemClickListener) mUploadItemClickListener.onUploadTask(data);
                    }
                    break;
            }
        }
    }

    /**
     * 对外提供的接口
     */

    public interface  OnUploadItemClickListener{
        void onUploadTask(UploadVideoInfo data);
        void onLongClickDetele(UploadVideoInfo data);
    }

    private OnUploadItemClickListener mUploadItemClickListener;

    public void setOnUploadItemClickListener(OnUploadItemClickListener onUploadItemClickListener){
        this.mUploadItemClickListener=onUploadItemClickListener;
    }
}
