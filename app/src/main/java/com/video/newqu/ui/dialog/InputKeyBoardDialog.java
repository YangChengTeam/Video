package com.video.newqu.ui.dialog;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.adapter.EmojiListAdapter;
import com.video.newqu.bean.ChatEmoji;
import com.video.newqu.contants.Constant;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.InputTools;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.ToastUtils;
import java.util.List;
import static com.video.newqu.util.FaceConversionUtil.getInstace;


/**
 * TinyHung@Outlook.com
 * 2017/10/19
 * 对输入框进行包装，避免了键盘弹起布局顶起的问题
 */

public class InputKeyBoardDialog extends Dialog {

    private final Context mContext;
    private LinearLayout mLl_facechoose;
    private ImageView mIv_btn_face;
    private EmojiListAdapter mEmojiListAdapter;
    private GridView mGrid_view_face;
    private EditText mInput_edit_text;
    private int content_charMaxNum=99;//留言字数上限
    private TextView btnsubmit;
    private String mHintTtext="写评论...";
    private CharSequence content_temp;//监听前的文本
    private String indexOutErrortex="评论内容超过字数限制";
    private boolean isShowTips=false;//是否显示首次播放的评论提示
    private TextView mTvTips;
    private AutoDismissRunnable mDismissRunnable;
    private Animation mClickViewVisibleAnimation;


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



    public InputKeyBoardDialog(@NonNull Context context) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_input_keyboard_layout);

        this.mContext=context;
        initLayoutParams();
        initViews();
        initEmotionData();
    }

    @Override
    public void dismiss() {
        if(null!=mContext&&null!=mInput_edit_text){
            InputTools.closeKeybord(mInput_edit_text);
        }
        if(null!=mClickViewVisibleAnimation){
            mClickViewVisibleAnimation.cancel();
        }
        if(null!=mTvTips&&null!=mDismissRunnable){
            mTvTips.removeCallbacks(mDismissRunnable);
            mTvTips.setVisibility(View.GONE);
        }
        super.dismiss();
        mClickViewVisibleAnimation=null;mInput_edit_text=null;mDismissRunnable=null;mLl_facechoose=null;mEmojiListAdapter=null;mGrid_view_face=null;
    }

    @Override
    public void show() {
        super.show();
        //是否需要显示首次评论弹幕提示
        if(isShowTips&&!SharedPreferencesUtil.getInstance().getBoolean(Constant.COMMENT_FIRST_TIPS,false)){
            mTvTips = (TextView)findViewById(R.id.tv_tips_mine_message);
            mTvTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mTvTips){
                        mTvTips.setVisibility(View.GONE);
                    }
                }
            });
            mTvTips.setVisibility(View.VISIBLE);
            mDismissRunnable = new AutoDismissRunnable();
            mTvTips.postDelayed(mDismissRunnable,2000);
            SharedPreferencesUtil.getInstance().putBoolean(Constant.COMMENT_FIRST_TIPS,true);
        }
    }

    private class AutoDismissRunnable implements Runnable {
        @Override
        public void run() {
            if(null!=mTvTips&&mTvTips.getVisibility()!=View.GONE){
                mClickViewVisibleAnimation = new AlphaAnimation(1.0f, 0.0f);
                mClickViewVisibleAnimation.setDuration(500);
                mClickViewVisibleAnimation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(null!=mTvTips) mTvTips.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                });
                mTvTips.startAnimation(mClickViewVisibleAnimation);
            }
        }
    }
    /**
     * 初始化
     */
    private void initViews() {
        mInput_edit_text = (EditText)findViewById(R.id.input_edit_text);
        mIv_btn_face = (ImageView) findViewById(R.id.iv_btn_face);
        btnsubmit = (TextView) findViewById(R.id.btn_submit);
        mLl_facechoose = (LinearLayout) findViewById(R.id.ll_facechoose);
        mGrid_view_face = (GridView) findViewById(R.id.grid_view_face);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLl_facechoose.getLayoutParams();
        layoutParams.height = ScreenUtils.dpToPxInt(230);
        mLl_facechoose.setLayoutParams(layoutParams);
        mInput_edit_text.setHint("写评论..");
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_btn_face:
                        showFaceBoard();
                        if(null!=mInput_edit_text) InputTools.closeKeybord(mInput_edit_text);
                        break;
                    case R.id.btn_submit:
                        if(null!=mOnKeyBoardChangeListener){
                            InputKeyBoardDialog.this.dismiss();
                            mOnKeyBoardChangeListener.onSubmit();
                        }
                        break;

                    case R.id.input_edit_text:
                        if(null!=mLl_facechoose&&mLl_facechoose.getVisibility()!=View.GONE){
                            mLl_facechoose.setVisibility(View.GONE);
                            mIv_btn_face.setImageResource(R.drawable.ic_face_boart);
                        }
                        break;
                }
            }
        };
        mIv_btn_face.setOnClickListener(onClickListener);
        btnsubmit.setOnClickListener(onClickListener);
        //获取EditText的点击事件，关闭表情面板
        mInput_edit_text.setOnClickListener(onClickListener);
        //监听输入框文字
        mInput_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                content_temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    if(null!= btnsubmit) btnsubmit.setTextColor(CommonUtils.getColor(R.color.text_orgin_selector));
                }else{
                    if(null!= btnsubmit)  btnsubmit.setTextColor(CommonUtils.getColor(R.color.colorTabText));
                }
                if(null!=mOnKeyBoardChangeListener){
                    mOnKeyBoardChangeListener.onChangeText(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(null!=editable&&editable.length()>0){
                    if(null!=mInput_edit_text){
                        if (null!=content_temp&&content_temp.length() > content_charMaxNum) {
                            //只保留最大长度范围内文字
                            String substring = content_temp.toString().substring(0, content_charMaxNum - 1);
                            mInput_edit_text.setText(substring);
                            mInput_edit_text.setSelection(substring.length());
                            ToastUtils.showCenterToast(indexOutErrortex);
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化表情
     */
    private void initEmotionData() {
        //表情集合
        List<ChatEmoji> chatEmojis = getInstace().emojis;
        mEmojiListAdapter = new EmojiListAdapter(mContext,chatEmojis);
        if(null!=mGrid_view_face){
            mGrid_view_face.setAdapter(mEmojiListAdapter);
            mGrid_view_face.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(null!=mEmojiListAdapter){
                        List<ChatEmoji> emojiLists =mEmojiListAdapter.getData();
                        if(null!=emojiLists&&emojiLists.size()>0){
                            ChatEmoji chatEmoji = emojiLists.get(position);
                            if(null!=chatEmoji){
                                if (!TextUtils.isEmpty(chatEmoji.getCharacter())) {
                                    SpannableString spannableString = getInstace().addFace(mContext, chatEmoji.getId(), chatEmoji.getCharacter(),(int)mInput_edit_text.getTextSize());
                                    mInput_edit_text.append(spannableString);
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
        if(null==mLl_facechoose||null==mIv_btn_face) return;
        //手动开启或关闭键盘
        if(mLl_facechoose.getVisibility()!=View.GONE){
            mLl_facechoose.setVisibility(View.GONE);
            mIv_btn_face.setImageResource(R.drawable.ic_face_boart);
        }else{
            if (mLl_facechoose.getVisibility() != View.VISIBLE) {
                mLl_facechoose.setVisibility(View.VISIBLE);
                mIv_btn_face.setImageResource(R.drawable.ic_face_keybaord);
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
        if(null!= btnsubmit){
            btnsubmit.setText(submitText);
        }
    }


    /**
     * 隐藏表平面板
     */
    public void hideFaceBtn() {
        if(null!=mIv_btn_face){
            mIv_btn_face.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_empty)).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 回显输入框文字
     * @param inputText
     */
    public void setInputText(String inputText) {
        if(null!=mInput_edit_text&&!TextUtils.isEmpty(inputText)){
            SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style), mInput_edit_text,null,null);
            mInput_edit_text.setText(topicStyleContent);
            mInput_edit_text.setSelection(topicStyleContent.length());
        }
    }

    /**
     * 根据参数，是否显示表平面板或者输入法
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     */
    public void setParams(boolean showKeyboard, boolean showFaceBoard) {
        if(showKeyboard){
            InputTools.openKeybord(mInput_edit_text);
            mInput_edit_text.requestFocus();
        }
        if(showFaceBoard){
            showFaceBoard();
            mInput_edit_text.requestFocus();
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
        if(null!=mInput_edit_text) mInput_edit_text.setHint(mHintTtext);
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
}
