package com.video.newqu.util.attach;

import android.text.TextUtils;
import com.ksyun.media.shortvideo.kit.KSYEditKit;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.util.ToastUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2017/11/20.
 * 视频合并处理工具类，合成完毕开始上传,支持批量合成、单个取消
 */

public class VideoComposeProcessor {

    public static VideoComposeProcessor cVideoComposeProcessor;
    private static Map<Long,VideoComposeTask> cVideoComposeTaskMap;


    public static synchronized VideoComposeProcessor getInstance(){
        synchronized(VideoComposeProcessor.class){
            if(null==cVideoComposeProcessor){
                cVideoComposeProcessor=new VideoComposeProcessor();
            }
            if(null==cVideoComposeTaskMap){
                cVideoComposeTaskMap=new HashMap<>();
            }
        }
        return cVideoComposeProcessor;
    }

    /**
     * 添加视频合成任务
     */
    public void addVideoComposeTask(UploadVideoInfo composeTaskInfo, KSYEditKit editKit){
        if(null==editKit) return;
        if(null==composeTaskInfo) return;
        if(TextUtils.isEmpty(composeTaskInfo.getCompostOutFilePath())){
            ToastUtils.showCenterToast("必须传入输出路径！");
            return;
        }
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_ADD_VIDEO_TASK);//通知切换到HomeFragment界面
        VideoComposeTask videoComposeTask = new VideoComposeTask(composeTaskInfo, editKit);
        if(null!=cVideoComposeTaskMap){
            cVideoComposeTaskMap.put(composeTaskInfo.getId(),videoComposeTask);
        }
        videoComposeTask.execute();
    }


    /**
     * 取消所有合并
     * @return
     */
    public  void stopAllCompose() {
        if(null!=cVideoComposeTaskMap&&cVideoComposeTaskMap.size()>0){
            Iterator<Map.Entry<Long, VideoComposeTask>> iterator = cVideoComposeTaskMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Long, VideoComposeTask> next = iterator.next();
                VideoComposeTask composeTaskInfo = next.getValue();
                if(null!=composeTaskInfo){
                    KSYEditKit editKti = composeTaskInfo.getEditKti();
                    if(null!=editKti){
                        editKti.stopCompose();
                        editKti.release();
                    }
                }
            }
            cVideoComposeTaskMap.clear();
        }
    }

    /**
     * 取消单个合并任务
     * @param taskId
     */
    public boolean stopCompose(Long taskId) {
        if(taskId==0) return false;
        if(null!=cVideoComposeTaskMap&&cVideoComposeTaskMap.size()>0){
            Iterator<Map.Entry<Long, VideoComposeTask>> iterator =cVideoComposeTaskMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Long, VideoComposeTask> next = iterator.next();
                if(taskId==next.getKey()){
                    VideoComposeTask composeTaskInfo = next.getValue();
                    if(null!=composeTaskInfo){
                        KSYEditKit editKti = composeTaskInfo.getEditKti();
                        if(null!=editKti){
                            editKti.stopCompose();
                            editKti.release();
                            cVideoComposeTaskMap.remove(taskId);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从任务列表中移除某个任务记录
     * @param data
     * @param data
     */
    public void removeComposeTaskList(UploadVideoInfo data) {
        if(null!=data&&null!=cVideoComposeTaskMap&&cVideoComposeTaskMap.size()>0){
            cVideoComposeTaskMap.remove(data.getId());
        }
        if(null!=cVideoComposeTaskMap&&cVideoComposeTaskMap.size()<=0){
            cVideoComposeTaskMap=null;
        }
    }

    public void onDestory(){
        if(null!=cVideoComposeTaskMap&&cVideoComposeTaskMap.size()>0){
            Iterator<Map.Entry<Long, VideoComposeTask>> iterator = cVideoComposeTaskMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Long, VideoComposeTask> next = iterator.next();
                if(null!=next){
                    VideoComposeTask value = next.getValue();
                    if(null!=value){
                        value.onDestory();
                    }
                }
            }
            cVideoComposeTaskMap.clear();
            cVideoComposeTaskMap=null;
        }
    }
}
