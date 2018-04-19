package com.video.newqu.util;

import android.os.AsyncTask;
import com.video.newqu.ui.activity.MediaRecordActivity;

import java.io.File;

/**
 *
 */

public class UnZipTask extends AsyncTask<File, Void, Void> {
    private OnProcessListener mListener;
    private String mFilePath;
    private String mFileName;

    public UnZipTask(String targetZip) {
        mFileName = targetZip.substring(0, targetZip.length() - MediaRecordActivity.ZIP_INFO.length());
    }

    @Override
    protected Void doInBackground(File... params) {
        try {
            File file = params[0];
            int len = file.getAbsolutePath().length();
            mFilePath = file.getAbsolutePath().substring(0, len - MediaRecordActivity.ZIP_INFO.length());

            if (!new File(mFilePath).exists()) {
                com.video.newqu.camera.util.FileUtils.unZipFolder(file.getAbsolutePath(), file.getParent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mListener != null) {
            mListener.onFinish(mFilePath, mFileName);
        }
        super.onPostExecute(aVoid);
    }

    public interface OnProcessListener {
        void onFinish(String filePath, String fileName);
    }

    public void setOnProcessListener(OnProcessListener listener) {
        mListener = listener;
    }
}
