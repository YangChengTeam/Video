package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.adapter.EmojiListAdapter;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.bean.ChatEmoji;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.DialogInputKeyboardLayoutBinding;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.InputTools;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.ToastUtils;
import java.util.List;
import static com.video.newqu.util.attach.FaceConversionUtil.getInstace;


/**
 * TinyHung@Outlook.com
 * 2017/10/19
 * 对输入框进行包装，避免了键盘弹起布局顶起的问题
 */

public class InputKeyBoardDialog extends BaseDialog<DialogInputKeyboardLayoutBinding>{

    private EmojiListAdapter mEmojiListAdapter;
    private int content_charMaxNum=99;//留言字数上限
    private String mHintTtext="写评论...";
    private CharSequence content_temp;//监听前的文本
    private String indexOutErrortex="评论内容超过字数限制";
    private boolean isShowTips=false;//是否显示首次播放的评论提示
    private AutoDismissRunnable mDismissRunnable;
    private Animation mClickViewVisibleAnimation;
    private boolean faceIsForbidden;//表情面板是否禁用

    public InputKeyBoardDialog(@NonNull Activity context) {
        super(context, R.style.CommendDialogStyle);
        setContentView(R.layout.dialog_input_keyboard_layout);
        initLayoutParams();
        initEmotionData();
    }

    @Override
    public void initViews() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bindingView.llFacechoose.getLayoutParams();
        layoutParams.height = ScreenUtils.dpToPxInt(230);
        bindingView.llFacechoose.setLayoutParams(layoutParams);
        bindingView.inputEditText.setHint("写评论..");
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_btn_face:
                        if(faceIsForbidden){
                            ToastUtils.showCenterToast("表情不可用!");
                            return;
                        }
                        showFaceBoard();
                        if(null!= bindingView) InputTools.closeKeybord( bindingView.inputEditText);
                        break;
                    case R.id.btn_submit:
                        if(null!=mOnKeyBoardChangeListener){
                            InputKeyBoardDialog.this.dismiss();
                            mOnKeyBoardChangeListener.onSubmit();
                        }
                        break;

                    case R.id.input_edit_text:
                        if(bindingView.llFacechoose.getVisibility()!=View.GONE){
                            bindingView.llFacechoose.setVisibility(View.GONE);
                            bindingView.ivBtnFace.setImageResource(R.drawable.ic_face_boart);
                        }
                        break;
                }
            }
        };
        bindingView.ivBtnFace.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        //获取EditText的点击事件，关闭表情面板
        bindingView.inputEditText.setOnClickListener(onClickListener);
        //监听输入框文字
        bindingView.inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                content_temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.btnSubmit.setTextColor(CommonUtils.getColor(R.color.text_orgin_selector));
                }else{
                    bindingView.btnSubmit.setTextColor(CommonUtils.getColor(R.color.colorTabText));
                }
                if(null!=mOnKeyBoardChangeListener){
                    mOnKeyBoardChangeListener.onChangeText(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(null!=editable&&editable.length()>0){
                    if(null!= bindingView){
                        if (null!=content_temp&&content_temp.length() > content_charMaxNum) {
                            //只保留最大长度范围内文字
                            String substring = content_temp.toString().substring(0, content_charMaxNum - 1);
                            bindingView.inputEditText.setText(substring);
                            bindingView.inputEditText.setSelection(substring.length());
                            ToastUtils.showCenterToast(indexOutErrortex);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void dismiss() {
        if(null!= bindingView){
            InputTools.closeKeybord( bindingView.inputEditText);
        }
        if(null!=mClickViewVisibleAnimation){
            mClickViewVisibleAnimation.cancel();
        }
        if(null!=bindingView&&null!=mDismissRunnable){
            bindingView.tvTipsMineMessage.removeCallbacks(mDismissRunnable);
            bindingView.tvTipsMineMessage.setVisibility(View.GONE);
        }
        super.dismiss();
        faceIsForbidden=false;
        mClickViewVisibleAnimation=null;mDismissRunnable=null;mEmojiListAdapter=null;
    }

    @Override
    public void show() {
        super.show();
        //是否需要显示首次评论弹幕提示
        if(isShowTips&&!SharedPreferencesUtil.getInstance().getBoolean(Constant.COMMENT_FIRST_TIPS,false)){
            bindingView.tvTipsMineMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bindingView.tvTipsMineMessage.setVisibility(View.GONE);
                }
            });
            bindingView.tvTipsMineMessage.setVisibility(View.VISIBLE);
            mDismissRunnable = new AutoDismissRunnable();
            bindingView.tvTipsMineMessage.postDelayed(mDismissRunnable,2000);
            SharedPreferencesUtil.getInstance().putBoolean(Constant.COMMENT_FIRST_TIPS,true);
        }
    }

    private class AutoDismissRunnable implements Runnable {
        @Override
        public void run() {
            if(null!=bindingView&&bindingView.tvTipsMineMessage.getVisibility()!=View.GONE){
                mClickViewVisibleAnimation = new AlphaAnimation(1.0f, 0.0f);
                mClickViewVisibleAnimation.setDuration(500);
                mClickViewVisibleAnimation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        bindingView.tvTipsMineMessage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                });
                bindingView.tvTipsMineMessage.startAnimation(mClickViewVisibleAnimation);
            }
        }
    }
    /**
     * 初始化表情
     */
    private void initEmotionData() {
        //表情集合
        List<ChatEmoji> chatEmojis = getInstace().emojis;
        mEmojiListAdapter = new EmojiListAdapter(getContext(),chatEmojis);
        if(null!=bindingView){
            bindingView.gridViewFace.setAdapter(mEmojiListAdapter);
            bindingView.gridViewFace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(null!=mEmojiListAdapter){
                        List<ChatEmoji> emojiLists =mEmojiListAdapter.getData();
                        if(null!=emojiLists&&emojiLists.size()>0){
                            ChatEmoji chatEmoji = emojiLists.get(position);
                            if(null!=chatEmoji){
                                if (!TextUtils.isEmpty(chatEmoji.getCharacter())) {
                                    SpannableString spannableString = getInstace().addFace(getContext(), chatEmoji.getId(), chatEmoji.getCharacter(),(int) bindingView.inputEditText.getTextSize());
                                    bindingView.inputEditText.append(spannableString);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 显示表情面板
     */
    private void showFaceBoard() {
        if(null==bindingView) return;
        //手动开启或关闭键盘
        if(bindingView.llFacechoose.getVisibility()!=View.GONE){
            bindingView.llFacechoose.setVisibility(View.GONE);
            bindingView.ivBtnFace.setImageResource(R.drawable.ic_face_boart);
        }else{
            if (bindingView.llFacechoose.getVisibility() != View.VISIBLE) {
                bindingView.llFacechoose.setVisibility(View.VISIBLE);
                bindingView.ivBtnFace.setImageResource(R.drawable.ic_face_keybaord);
            }
        }
    }

    /**
     * 设置Dialog显示在屏幕底部
     */
    private void initLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }

    /**
     * 设置确认按钮文字
     * @param submitText
     */
    public void setSubmitText(String submitText) {
        if(null!= bindingView){
            bindingView.btnSubmit.setText(submitText);
        }
    }


    /**
     * 隐藏表情面板
     */
    public void hideFaceBtn() {
        faceIsForbidden=true;
    }

    /**
     * 回显输入框文字
     * @param inputText
     */
    public void setInputText(String inputText) {
        if(null!=bindingView&&!TextUtils.isEmpty(inputText)){
            SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style),  bindingView.inputEditText,null,null);
            bindingView.inputEditText.setText(topicStyleContent);
            bindingView.inputEditText.setSelection(topicStyleContent.length());
        }
    }

    /**
     * 根据参数，是否显示表平面板或者输入法
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     */
    public void setParams(boolean showKeyboard, boolean showFaceBoard) {
        if(showKeyboard){
            InputTools.openKeybord( bindingView.inputEditText);
            bindingView.inputEditText.requestFocus();
        }
        if(showFaceBoard){
            showFaceBoard();
            bindingView.inputEditText.requestFocus();
        }
    }


    /**
     * 设置输入字数限制
     * @param charMaxNum
     */
    public void setMaxTextCount(int charMaxNum) {
        this.content_charMaxNum=charMaxNum;
    }


    /**
     * 设置HintText
     * @param hintTtext
     */
    public void setHintText(String hintTtext) {
        this.mHintTtext=hintTtext;
        if(null!= bindingView)  bindingView.inputEditText.setHint(mHintTtext);
    }

    /**
     * 设置背景透明度 完全透明 0.0 - 1.0 完全不透明
     * @param windownAmount
     */
    public void setBackgroundWindown(float windownAmount) {
        getWindow().setDimAmount(windownAmount);
    }

    /**
     * 设置文字超出提示语
     * @param errorText
     */
    public void setIndexOutErrorText(String errorText){
        this.indexOutErrortex=errorText;
    }


    public void showTips(boolean flag) {
        this.isShowTips=flag;
    }

    public  interface OnKeyBoardChangeListener{
        void onChangeText(String inputText);
        void onSubmit();
    }
    public void setOnKeyBoardChangeListener(OnKeyBoardChangeListener onKeyBoardChangeListener) {
        mOnKeyBoardChangeListener = onKeyBoardChangeListener;
    }
    private OnKeyBoardChangeListener mOnKeyBoardChangeListener;
}
