
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.bean.VideoInfo;

/**
 * @time 2017/5/23 10:50
 * @des 视频详情接口
 */
public interface VideoDetailsContract {

    interface View extends BaseContract.BaseView {
        void showComentList(ComentList data);
        void showComentList(String videoID, ComentList data);
        void showComentListEmpty(String data);
        void showComentListError();
        void showAddComentRelult(SingComentInfo data);
        void showPriseResult(String text);
        void showFollowUserResult(String text);
        void showPostPlayCountResult(String data);
        void showVideoInfoResult(VideoInfo data);
        void showLoadVideoInfoError();
        void showReportUserResult(String data);
        void showReportVideoResult(String data);
        void showDeteleVideoResult(String data);
        void showSetVideoPrivateStateResult(String result);
        void showChangeVideoDownloadPermissionResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getComentList(String videoID, String page, String pageSize);//获取视频留言列表
        void addComentMessage(String id, String video_id, String wordsmMessage, String toUserID);//对视频添加留言
        void onPriseVideo(String videoID, String userID);//对视频点赞
        void onFollowUser(String userID, String fansUserID);//关注某个用户
        void getVideoInfo(String userID, String videoAuthorID, String videoID);//获取视频详细信息
        void onReportUser(String userID, String accuseUserId);//举报用户
        void onReportVideo(String userID, String videoID);//举报视频
        void postPlayCount(String userID, String videoID);//增加播放记录
        void deleteVideo(String userID, String videoID);//删除视频
        void setVideoPrivateState(String videoID, String userID);//设置视频隐私状态
        void changeVideoDownloadPermission(String videoId, String userID);//设置视频下载权限
    }
}
