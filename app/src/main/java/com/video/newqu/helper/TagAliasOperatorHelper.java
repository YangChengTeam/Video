package com.video.newqu.helper;

import android.content.Context;
import com.video.newqu.util.Logger;
import cn.jpush.android.api.JPushMessage;

/**
 * 处理tag alias相关的逻辑
 *
 */
public class TagAliasOperatorHelper {

    private final String TAG = "TagAliasOperatorHelper";
    private static TagAliasOperatorHelper mInstance;

    public interface  OnAliasChangeListener{
        void onChange(JPushMessage jPushMessage);
    }

    private OnAliasChangeListener mOnAliasChangeListener;

    public void setOnAliasChangeListener(OnAliasChangeListener onAliasChangeListener) {
        mOnAliasChangeListener = onAliasChangeListener;
    }

    private TagAliasOperatorHelper(){

    }

    public static TagAliasOperatorHelper getInstance(){
        if(mInstance == null){
            synchronized (TagAliasOperatorHelper.class){
                if(mInstance == null){
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }


    /**
     * 对TAG操作的回调
     * @param context
     * @param jPushMessage
     */
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {

    }

    /**
     * 检查TAG回调
     * @param context
     * @param jPushMessage
     */
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage){

    }

    /**
     * 对别名操作的回调
     * @param context
     * @param jPushMessage
     */
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        if(null!=mOnAliasChangeListener){
            mOnAliasChangeListener.onChange(jPushMessage);
        }
    }
}
