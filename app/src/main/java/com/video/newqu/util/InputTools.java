package com.video.newqu.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.video.newqu.VideoApplication;


/**
 * @author TinyHung@Outlook.com
 * @version 1.0
 * @des ${TODO}
 */
public class InputTools {


    /**
     * 强制隐藏软件盘
     * @param context
     */
    public static void hideKeyBoard(Activity context) {
        InputMethodManager systemService = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null!=systemService){
            systemService.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(),0);
        }
    }

    /**
     * 强制打开软件盘
     * @param context
     */
    public static void showKeyBoard(Activity context) {
        InputMethodManager systemService = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null!=systemService){
            systemService.showSoftInputFromInputMethod(context.getWindow().getDecorView().getWindowToken(),0);
        }
    }



    /**
     * 打卡软键盘
     *
     * @param mEditText
     */
    public static void openKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    /**
     * 关闭软键盘
     *
     * @param mEditText
     */
    public static void closeKeybord(EditText mEditText)
    {
        InputMethodManager imm = (InputMethodManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
