package com.video.newqu.ui.fragment;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.VideoComentListAdapter;
import com.video.newqu.base.BaseDialogFragment;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentVerticalPlayCommendBinding;
import com.video.newqu.databinding.VideoDetailsEmptyLayoutBinding;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.listener.VideoComendClickListener;
import com.video.newqu.ui.activity.AuthorDetailsActivity;
import com.video.newqu.ui.activity.VerticalHistoryVideoPlayActivity;
import com.video.newqu.ui.activity.VerticalVideoPlayActivity;
import com.video.newqu.ui.contract.VerticalVideoCommentContract;
import com.video.newqu.ui.dialog.InputKeyBoardDialog;
import com.video.newqu.ui.presenter.VerticalVideoCommentPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.TextViewTopicSpan;
import com.video.newqu.util.TimeUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.DataChangeMarginView;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/12/6.
 * 视频的留言列表
 */

public class VerticalVideoCommentFragment extends BaseDialogFragment<FragmentVerticalPlayCommendBinding,VerticalVideoCommentPresenter> implements  VerticalVideoCommentContract.View,VideoComendClickListener ,TopicClickListener {

    private VideoComentListAdapter mVideoComentListAdapter;
    private String mVideoID;
    private int mPage=0;
    private int mPageSize=10;
    private String toUserID="0";//toUserID很重要
    private VideoDetailsEmptyLayoutBinding mEmptyBindingView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vertical_play_commend;
    }

    /**
     * 必须调用此方法初始化传入参数
     * @param videoID 目标ID
     * @param commentCount 当前评论数量
     * @param uoploadTime 视频的发布时间
     * @param isShowInput 是否自动显示数据框
     * @return
     */
    public static VerticalVideoCommentFragment newInstance(String videoID, String commentCount, String uoploadTime, boolean isShowInput){
        VerticalVideoCommentFragment fragment=new VerticalVideoCommentFragment();
        Bundle bundle=new Bundle();
        bundle.putString("video_id",videoID);
        bundle.putString("comment_count",commentCount);
        bundle.putString("upload_time",uoploadTime);
        bundle.putBoolean("is_showInput",isShowInput);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setFragmentMarginHeight(200);//顶部设置200DP的TopMargin
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Translucent_NoTitleBar);//全屏铺满
        Bundle arguments = getArguments();
        if(null!=arguments){
            mVideoID = arguments.getString("video_id");
        }else{
            throw new IllegalArgumentException("XinQu:your must prest 'Bundle' params!Please refer newInstance() method!");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(TextUtils.isEmpty(mVideoID)) return;
        Bundle arguments = getArguments();
        if(null!=arguments){
            String commentCount = arguments.getString("comment_count");
            String uploadTime = arguments.getString("upload_time");
            bindingView.tvCommentCount.setText(commentCount);
            long add_time =System.currentTimeMillis();
            if(!TextUtils.isEmpty(uploadTime)){
                String add_timeString =uploadTime+"000";
                add_time=Long.parseLong(add_timeString);
            }
            bindingView.tvUploadTime.setText(TimeUtils.getTilmNow(add_time)+" 发布");
        }
        mPresenter = new VerticalVideoCommentPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
        loadComentList();
        if(null!=arguments){
            final boolean isShowInput = arguments.getBoolean("is_showInput",false);
            //根据外部状态确定是否自动打开评论输入框
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isShowInput){
                        showInputKeyBoardDialog(true,false,"输入评论内容");
                    }
                }
            },600);
        }
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        bindingView.recylerView.setLayoutManager(linearLayoutManager);
        bindingView.recylerView.setHasFixedSize(false);
        mVideoComentListAdapter = new VideoComentListAdapter(null,this,this);
        mVideoComentListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mVideoComentListAdapter){
                    List<ComentList.DataBean.CommentListBean> data = mVideoComentListAdapter.getData();
                    if(null!=data&&data.size()>=9&&null!= mPresenter &&!mPresenter.isLoading()){
                        bindingView.swiperLayout.setRefreshing(false);
                         loadComentList();
                    }else{
                        bindingView.recylerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mVideoComentListAdapter.loadMoreFail();//加载失败
                                }else{
                                    mVideoComentListAdapter.loadMoreEnd();//加载为空
                                }
                            }
                        });
                    }
                }
            }
        }, bindingView.recylerView);

        mEmptyBindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.video_details_empty_layout, (ViewGroup) bindingView.recylerView.getParent(),false);
        mEmptyBindingView.emptyView.setOnRefreshListener(new DataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyBindingView.emptyView.showLoadingView();
                loadComentList();
            }
        });
        mEmptyBindingView.emptyView.showLoadingView();

        mVideoComentListAdapter.setEmptyView(mEmptyBindingView.getRoot());
        bindingView.recylerView.setAdapter(mVideoComentListAdapter);
    }


    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //关闭
                    case R.id.btn_close_commend:
                        VerticalVideoCommentFragment.this.dismiss();
                        break;
                    //留言列表打开输入键盘，直接打开输入法，不显示表情
                    case R.id.tv_input_content:
                        showInputKeyBoardDialog(true,false,"输入评论内容");
                        break;
                    //留言列表打开输入键盘，直接显示表情面板，不打开输入法
                    case R.id.btn_iv_face_icon:
                        showInputKeyBoardDialog(false,true,"输入评论内容");
                        break;
                    //发送留言
                    case R.id.btn_tv_send:
                        sendMessage();
                        break;
                }
            }
        };
        bindingView.btnCloseCommend.setOnClickListener(onClickListener);
        bindingView.tvInputContent.setOnClickListener(onClickListener);
        bindingView.btnIvFaceIcon.setOnClickListener(onClickListener);
        bindingView.btnTvSend.setOnClickListener(onClickListener);
        //添加假的输入框文字变化监听
        bindingView.tvInputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    if(null!=bindingView)  bindingView.btnTvSend.setTextColor(CommonUtils.getColor(R.color.text_orgin_selector));
                }else{
                    if(null!=bindingView)  {
                        bindingView.btnTvSend.setTextColor(CommonUtils.getColor(R.color.colorTabText));
                        bindingView.tvInputContent.setHint("写评论...");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        bindingView.swiperLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                loadComentList();
            }
        });
    }

    /**
     * 加载评论列表
     */
    private void loadComentList() {
        if(!TextUtils.isEmpty(mVideoID)&&null!= mPresenter &&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getComentList(mVideoID,mPage+"",mPageSize+"");
        }
    }


    /**
     * 打开输入法键盘
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     */
    private void showInputKeyBoardDialog(boolean showKeyboard,boolean showFaceBoard,String hintText) {
        if(null!=getActivity()&&!getActivity().isFinishing()){
            InputKeyBoardDialog inputKeyBoardDialog = new InputKeyBoardDialog(getActivity());
            inputKeyBoardDialog.setInputText(bindingView.tvInputContent.getText().toString());
            inputKeyBoardDialog.setParams(showKeyboard,showFaceBoard);
            inputKeyBoardDialog.setBackgroundWindown(0.1f);
            inputKeyBoardDialog.setHintText(hintText);
            inputKeyBoardDialog.showTips(true);
            inputKeyBoardDialog.setIndexOutErrorText("评论内容超过字数限制");
            inputKeyBoardDialog.setOnKeyBoardChangeListener(new InputKeyBoardDialog.OnKeyBoardChangeListener() {
                //文字发生了变化
                @Override
                public void onChangeText(String inputText) {
                    if(!TextUtils.isEmpty(inputText)){
                        SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style), bindingView.tvInputContent,null,null);
                        bindingView.tvInputContent.setText(topicStyleContent);
                    }else{
                        bindingView.tvInputContent.setText(inputText);
                        toUserID="0";
                    }
                }

                //提交
                @Override
                public void onSubmit() {
                    sendMessage();
                }
            });
            inputKeyBoardDialog.show();
        }
    }


    /**
     * 发送留言
     */
    private void sendMessage() {
        if(TextUtils.isEmpty(mVideoID)){
            return;
        }

        if(!Utils.isCheckNetwork()){
            ToastUtils.showCenterToast("没有可用的网络连接");
            return;
        }

        String wordsmMessage = bindingView.tvInputContent.getText().toString();
        if(TextUtils.isEmpty(wordsmMessage)){
            ToastUtils.showCenterToast("评论内容不能为空！");
            return;
        }

        if(null!= mPresenter &&!mPresenter.isAddComment()){
            if(null!=VideoApplication.getInstance().getUserData()){
                try {
                    showProgressDialog("发表评论中..",true);
                    String encode = URLEncoder.encode(wordsmMessage, "UTF-8");
                    mPresenter.addComentMessage(VideoApplication.getLoginUserID(),mVideoID,encode,toUserID);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if(null!=activity&&!activity.isFinishing()){
                    if(activity instanceof VerticalVideoPlayActivity){
                        ((VerticalVideoPlayActivity) activity).login();
                    }else if(activity instanceof VerticalHistoryVideoPlayActivity){
                        ((VerticalHistoryVideoPlayActivity) activity).login();
                    }
                }
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(null!=bindingView&&null!=mOnFragmentDataChangeListener){
            mOnFragmentDataChangeListener.onDismiss(Integer.parseInt(bindingView.tvCommentCount.getText().toString().trim()));
        }
        super.onDismiss(dialog);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=mEmptyBindingView) mEmptyBindingView.emptyView.onDestroy();
        if(null!=bindingView) bindingView.recylerView.setAdapter(null);
        if(null!=mVideoComentListAdapter) mVideoComentListAdapter.setNewData(null);
    }

    /**
     * 为适配器增加新数据，添加至第0个位置
     * @param info
     */
    private void updataAddDataToTopAdapter(SingComentInfo.DataBean.InfoBean info) {
        if(null!=mVideoComentListAdapter){
            ComentList.DataBean.CommentListBean commentListBean = new ComentList.DataBean.CommentListBean();
            commentListBean.setAdd_time(String.valueOf(info.getAdd_time()));
            commentListBean.setComment(info.getComment());
            commentListBean.setId(info.getId());
            commentListBean.setLogo(info.getLogo());
            commentListBean.setNickname(info.getNickname());
            commentListBean.setUser_id(info.getUser_id());
            commentListBean.setvideo_id(info.getVideo_id());
            commentListBean.setTo_nickname(info.getTo_nickname());
            commentListBean.setTo_user_id(info.getTo_user_id());
            commentListBean.setComment_id(info.getComment_id());
            mVideoComentListAdapter.addData(0, commentListBean);
            String count = bindingView.tvCommentCount.getText().toString().trim();
            int i = Integer.parseInt(count);
            bindingView.tvCommentCount.setText((i+1)+"");
            bindingView.recylerView.post(new Runnable() {
                @Override
                public void run() {
                    bindingView.recylerView.scrollToPosition(0);
                }
            });
            if(null!=mOnFragmentDataChangeListener){
                mOnFragmentDataChangeListener.onAddComment(commentListBean);
            }
        }
    }


    //===========================================点击事件回调========================================

    @Override
    public void onAuthorIconClick(String userID) {
        if(null!=getActivity()&&!getActivity().isFinishing()&&!TextUtils.isEmpty(userID)){
            AuthorDetailsActivity.start(getActivity(),userID);
        }
    }

    @Override
    public void onAuthorItemClick(ComentList.DataBean.CommentListBean data) {
        if(null!=data){
            toUserID=data.getUser_id();
            // TODO: 2017/10/19 对留言评论
            bindingView.tvInputContent.setText("");
            bindingView.tvInputContent.setHint("回复 "+data.getNickname());
            showInputKeyBoardDialog(true,false,"回复 "+data.getNickname());
        }
    }

    @Override
    public void onTopicClick(String topic) {
        if(!TextUtils.isEmpty(topic)){
            startTargetActivity(Constant.KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT,topic,VideoApplication.getLoginUserID(),0,topic);
        }
    }

    @Override
    public void onUrlClick(String url) {

    }

    @Override
    public void onAuthoeClick(String authorID) {

    }



    //===========================================加载数据回调========================================

    @Override
    public void showComentList(ComentList data) {
        if(null!=bindingView)bindingView.swiperLayout.setRefreshing(false);
        if(null!= mEmptyBindingView) mEmptyBindingView.emptyView.showEmptyView("还没有留言，说两句吧~",R.drawable.iv_com_message_empty);
        if(null!= mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mVideoComentListAdapter.setNewData(data.getData().getComment_list());
            //添加数据
            }else{
                mVideoComentListAdapter.addData(data.getData().getComment_list());
            }
        }
    }

    @Override
    public void showComentListEmpty(String data) {
        if(null!=bindingView)bindingView.swiperLayout.setRefreshing(false);
        if(null!= mEmptyBindingView) mEmptyBindingView.emptyView.showEmptyView("还没有留言，说两句吧~",R.drawable.iv_com_message_empty);
        if(null!= mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreEnd();//加载完成
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void showComentListError() {
        if(null!=bindingView)bindingView.swiperLayout.setRefreshing(false);
        if(1==mPage){
            if(null!= mEmptyBindingView) mEmptyBindingView.emptyView.showErrorView();
        }
        if(null!= mVideoComentListAdapter){
            mVideoComentListAdapter.loadMoreFail();//加载完成
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void showAddComentRelult(SingComentInfo data) {
        closeProgressDialog();
        toUserID="0";
        bindingView.tvInputContent.setText("");
        bindingView.tvInputContent.setHint("写评论...");
        ToastUtils.showCenterToast("评论成功");
        SingComentInfo.DataBean.InfoBean info = data.getData().getInfo();
        if(null!=info){
            updataAddDataToTopAdapter(info);
        }
    }
    @Override
    public void showErrorView() {
        closeProgressDialog();
    }
    @Override
    public void complete() {}

    //回调,将评论数量回调给调用者
    public interface OnFragmentDataChangeListener{
        void onDismiss(int commentCount);
        void onAddComment(ComentList.DataBean.CommentListBean newCommentData);
    }
    private OnFragmentDataChangeListener mOnFragmentDataChangeListener;

    public void setOnDismissListener(OnFragmentDataChangeListener onFragmentDataChangeListener) {
        mOnFragmentDataChangeListener = onFragmentDataChangeListener;
    }
}
