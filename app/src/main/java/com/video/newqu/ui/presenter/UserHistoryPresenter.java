
package com.video.newqu.ui.presenter;

import android.os.AsyncTask;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.contract.UserHistoryContract;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 用户历史播放记录
 */

public class UserHistoryPresenter extends RxPresenter<UserHistoryContract.View> implements UserHistoryContract.Presenter<UserHistoryContract.View> {


    private boolean isVideoLoading;


    public boolean isVideoLoading() {
        return isVideoLoading;
    }



    @Override
    public void getVideoHistoryList(int page, int pageSize) {
        if(isVideoLoading) return;
        new LoadingVideoAsyncTask(page,pageSize).execute();

    }

    @Override
    public void getAllVideoHistoryList() {
        if(isVideoLoading) return;
        new LoadingVideoAsyncTask().execute();
    }


    /**
     * 获取用户观看视频的历史记录
     * 要实现分页加载功能，须传入mPage，mPageSize，且参数必须大于0
     */
    private class LoadingVideoAsyncTask extends AsyncTask<Void,Void,List<UserPlayerVideoHistoryList>> {

        private int mPage=0;
        private int mPageSize=0;

        public LoadingVideoAsyncTask(int page, int pageSize) {
            this.mPage=page;
            this.mPageSize=pageSize;
        }

        public LoadingVideoAsyncTask(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isVideoLoading =true;
        }

        @Override
        protected List<UserPlayerVideoHistoryList> doInBackground(Void... voids) {
            List<UserPlayerVideoHistoryList> uploadVideoList=null;
            try {
                if(mPage>0&&mPageSize>0){
                    uploadVideoList = ApplicationManager.getInstance().getUserPlayerDB().queryPlayerHistoryListOfPage(mPage,mPageSize);
                }else{
                    uploadVideoList=ApplicationManager.getInstance().getUserPlayerDB().getAllHistoryPlayerVideoList();
                }
                if(null!=uploadVideoList&&uploadVideoList.size()>0){
                    Collections.sort(uploadVideoList, new Comparator<UserPlayerVideoHistoryList>() {
                        @Override
                        public int compare(UserPlayerVideoHistoryList o1, UserPlayerVideoHistoryList o2) {
                            return String.valueOf(o2.getAddTime()).compareTo(String.valueOf(o1.getAddTime()));
                        }
                    });
                }
            }catch (Exception e){
                return null;
            }
            return uploadVideoList;
        }

        @Override
        protected void onPostExecute(List<UserPlayerVideoHistoryList> userLookVideoLists) {
            super.onPostExecute(userLookVideoLists);
            isVideoLoading =false;
            if(null!=userLookVideoLists&&userLookVideoLists.size()>0){
                if(null!=mView) mView.showVideoHistoryList(userLookVideoLists);
            }else{
                if(null!=mView) mView.showVideoHistoryListEmpty("没有更多视频了");
            }
        }
    }

}
