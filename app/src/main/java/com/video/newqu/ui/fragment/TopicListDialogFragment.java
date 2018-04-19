package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.adapter.TopicListAdapter;
import com.video.newqu.base.BaseDialogFragment;
import com.video.newqu.bean.TopicList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentTopicListBinding;
import com.video.newqu.databinding.RecylerViewEmptyLayoutBinding;
import com.video.newqu.model.StaggerSpacesItemDecoration;
import com.video.newqu.ui.contract.TopicContract;
import com.video.newqu.ui.presenter.TopicPresenter;
import com.video.newqu.util.Utils;
import com.video.newqu.view.refresh.SwipePullRefreshLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/28.
 * 话题选择列表
 */

public class TopicListDialogFragment extends BaseDialogFragment<FragmentTopicListBinding> implements TopicContract.View, BaseQuickAdapter.RequestLoadMoreListener {


    private List<TopicList.DataBean> topList;
    private TopicListAdapter mTopicListAdapter;
    private TopicPresenter mTopicPresenter;
    private int mTopicMax;
    private static TopicListDialogFragment mInstance;
    private ArrayList<String> mTopicList;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_topic_list;
    }

    public synchronized static TopicListDialogFragment getInstance(int topicMax){
        synchronized(TopicListDialogFragment.class){
            if(null== mInstance){
                mInstance = new TopicListDialogFragment();
                Bundle bundle=new Bundle();
                bundle.putInt("topic_max",topicMax);
                mInstance.setArguments(bundle);
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mTopicMax = arguments.getInt("topic_max",3);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopicPresenter = new TopicPresenter(getActivity());
        initAdapter();
        mTopicPresenter.attachView(this);
        topList= (List<TopicList.DataBean>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_TOPIC_LIST);
        if(null!=topList&&topList.size()>0){
            mTopicListAdapter.setNewData(topList);
        }else{
            showGroupLoadingView();
        }
        mTopicPresenter.getTopicList();
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_back:
                        TopicListDialogFragment.this.dismiss();
                        break;
                    //确定
                    case R.id.iv_submit:
                        startResult();
                        break;
                }
            }
        };
        bindingView.ivBack.setOnClickListener(onClickListener);
        bindingView.tvTitle.setText("选择话题");
        bindingView.ivSubmit.setVisibility(View.VISIBLE);
        bindingView.ivSubmit.setOnClickListener(onClickListener);

        bindingView.swiperefreshLayout.setOnRefreshListener(new SwipePullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindingView.swiperefreshLayout.setRefreshing(true);
                loadMoreTopicList();
            }
        });

    }


    /**
     * 有结果的返回
     */
    private void startResult() {
        List<TopicList.DataBean> mTopicListAdapterData = mTopicListAdapter.getData();
        mTopicList = new ArrayList<>();
        if(null!=mTopicListAdapterData&&mTopicListAdapterData.size()>0){
            for (TopicList.DataBean dataBean : mTopicListAdapterData) {
                if(dataBean.isSelector()){
                    mTopicList.add("#"+dataBean.getTopic()+"#");
                }
            }
            if(null!= mTopicList && mTopicList.size()>0){
                Intent intent=new Intent();
                intent.putStringArrayListExtra("topic_list", mTopicList);
                TopicListDialogFragment.this.dismiss();
                return;
            }else{
                showErrorToast(null,null,"请至少选择一个话题");
            }
        }else{
            TopicListDialogFragment.this.dismiss();
        }
    }


    private void loadMoreTopicList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindingView.swiperefreshLayout.setRefreshing(false);
            }
        },800);
    }




    @Override
    protected void onRefresh() {
        super.onRefresh();
        showGroupLoadingView();
        if(null!=mTopicPresenter){
            mTopicPresenter.getTopicList();
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(),2, LinearLayoutManager.VERTICAL,false));//new FlowLayoutManager()
        bindingView.recyerView.addItemDecoration(new StaggerSpacesItemDecoration(Utils.dip2px(getActivity(),3)));
        mTopicListAdapter = new TopicListAdapter(topList);
        RecylerViewEmptyLayoutBinding emptyViewbindView= DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.recyler_view_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mTopicListAdapter.setEmptyView(emptyViewbindView.getRoot());
        emptyViewbindView.ivItemIcon.setImageResource(R.drawable.ic_list_empty_icon);
        emptyViewbindView.tvItemName.setText("没有发现话题关键词~");
        mTopicListAdapter.setMaxTopicNum(mTopicMax);
        mTopicListAdapter.setOnLoadMoreListener(this);
        bindingView.recyerView.setAdapter(mTopicListAdapter);

        mTopicListAdapter.setOnItemClickListener(new TopicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                List<TopicList.DataBean> mTopicListAdapterData = mTopicListAdapter.getData();
                if(null!=mTopicListAdapterData&&mTopicListAdapterData.size()>0){
                    int poistion=0;
                    for (TopicList.DataBean dataBean : mTopicListAdapterData) {
                        if(dataBean.isSelector()){
                            poistion++;
                        }
                    }
                }
            }
        });
    }



    /**
     * 获取话题列表成功
     * @param data
     */
    @Override
    public void showTopicListFinlish(TopicList data) {
        showGroupContentView();
        if(null!=topList){
            topList.clear();
        }
        topList=data.getData();
        ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_TOPIC_LIST);
        ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_TOPIC_LIST, (Serializable) topList);
        if(null!=mTopicListAdapter) mTopicListAdapter.setNewData(topList);
    }

    @Override
    public void showTopicListEmpty(String data) {
        showGroupContentView();
        showErrorToast(null,null,data);
    }

    @Override
    public void showTopicListError(String data) {
        showGroupLoadingErrorView();
        showErrorToast(null,null,data);
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onDestroy() {
        if(null!=mTopicPresenter){
            mTopicPresenter.detachView();
        }
        super.onDestroy();
    }

    @Override
    public void onLoadMoreRequested() {

        bindingView.swiperefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(null!=mTopicListAdapter){
                    mTopicListAdapter.loadMoreEnd();//加载为空
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mOnDismissListener){
            mOnDismissListener.onDismiss(mTopicList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * 设置消失的监听事件
     * @param onDismissListener
     * @return
     */
    public TopicListDialogFragment setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return mInstance;
    }


    public interface OnDismissListener{
        void onDismiss(List<String> topics);
    }
    private OnDismissListener mOnDismissListener;
}
