package com.video.newqu.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;

import com.video.newqu.VideoApplication;
import com.video.newqu.util.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

/**
 * TinyHung@outlook.com
 * 2017/6/30 8:36
 * 全局的异常拦截
 */
public class CrashHanlder {

    private static CrashHanlder mCrashHanlder;
    private VideoApplication mContext;

    public static CrashHanlder getInstance() {
        if(null==mCrashHanlder){
            mCrashHanlder=new CrashHanlder();
        }
        return mCrashHanlder;
    }

    /**
     * 将所有Activity存入栈顶
     */
    public Stack<Activity> mActivityStacks = new Stack<>();

    //当Activity create的时候放入到Stack
    public void addToStack(Activity activity) {
        mActivityStacks.push(activity);
    }

    //当Activity 销毁的时候
    public void removeFromStack(Activity activity) {
        mActivityStacks.remove(activity);
    }

    /**
     * 初始化
     * @param context
     */
    public void init(VideoApplication context) {
        this.mContext=context;
        //检测Looper活动情况
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable ex) {
                new Thread() {
                    @Override
                    public void run() {
                        //上传处理程序错误
                        procesError(ex);
                        Looper.prepare();
                        //出现错误弹窗提示
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivityStacks.peek());//这里的上下文应该拿栈顶部的上下文
                        builder.setTitle("提示").setMessage("抱歉，程序出错，我们将尽快修复错误,确定退出程序")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //1、其中一个界面出现错误应该退出整个应用程序而不是退出当前的界面
                                        while (!mActivityStacks.empty()) {

                                            mActivityStacks.pop().finish();
                                        }
                                        //当程序出现了不可预料的错误把错误捕获了在杀掉程序进程
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                }).setCancelable(false).create().show();

                        Looper.loop();
                    }
                }.start();
            }
        });
    }
    private void procesError(Throwable ex) {

        File file = saveReport(ex);
//        uploadReport(file);
    }


    /**
     * 生成错误日志
     * @param ex
     * @return
     */
    private File saveReport(Throwable ex) {
        FileWriter writer = null;
        PrintWriter printWriter = null;
        try {
            File file = new File(mContext.getFilesDir(), "error_log" + System.currentTimeMillis());
            writer = new FileWriter(file);
            printWriter = new PrintWriter(writer);
            writer.append("========Build==========\n");
            writer.append(String.format("BOARD\t%s\n", Build.BOARD));
            writer.append(String.format("BOOTLOADER\t%s\n", Build.BOOTLOADER));
            writer.append(String.format("BRAND\t%s\n", Build.BRAND));
            writer.append(String.format("CPU_ABI\t%s\n", Build.CPU_ABI));
            writer.append(String.format("CPU_ABI2\t%s\n", Build.CPU_ABI2));
            writer.append(String.format("DEVICE\t%s\n", Build.DEVICE));
            writer.append(String.format("DISPLAY\t%s\n", Build.DISPLAY));
            writer.append(String.format("FINGERPRINT\t%s\n", Build.FINGERPRINT));
            writer.append(String.format("HARDWARE\t%s\n", Build.HARDWARE));
            writer.append(String.format("HOST\t%s\n", Build.HOST));
            writer.append(String.format("ID\t%s\n", Build.ID));
            writer.append(String.format("MANUFACTURER\t%s\n", Build.MANUFACTURER));
            writer.append(String.format("MODEL\t%s\n", Build.MODEL));
            writer.append(String.format("SERIAL\t%s\n", Build.SERIAL));
            writer.append(String.format("PRODUCT\t%s\n", Build.PRODUCT));

            writer.append("========APP==========\n");
            try {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                writer.append(String.format("versionCode\t%s\n", versionCode));
                writer.append(String.format("versionName\t%s\n", versionName));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            writer.append("========Exception==========\n");
            ex.printStackTrace(printWriter);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 上传错误日志
     * @param report
     */
    private void uploadReport(File report) {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            URL url = new URL("www.baidu.com" + "/ErrorReportServlet");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            os = urlConnection.getOutputStream();
            fis = new FileInputStream(report);
            byte[] buf = new byte[1024 * 8];
            int len = 0;
            while ((len = fis.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            int responseCode = urlConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(os);
            close(fis);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
