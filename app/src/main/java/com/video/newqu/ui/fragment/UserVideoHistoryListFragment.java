package com.video.newqu.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.adapter.GroupHistroyVideoListAdapter;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.HistoryVideoGroupList;
import com.video.newqu.bean.SubmitEvent;
import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentHistoryListBinding;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.activity.VerticalHistoryVideoPlayActivity;
import com.video.newqu.ui.activity.VideoDetailsActivity;
import com.video.newqu.ui.contract.UserHistoryContract;
import com.video.newqu.ui.presenter.UserHistoryPresenter;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.TimeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
/**
 * TinyHung@Outlook.com
 * 2017/10/12
 * 用户观看过的视频记录
 * 为了排序，直接一次性加载全部，取消分页
 */

public class UserVideoHistoryListFragment extends BaseFragment<FragmentHistoryListBinding,UserHistoryPresenter> implements UserHistoryContract.View {

    private static final String TAG = "UserVideoHistoryListFragment";
    //    private int page=0;
//    private int pageSize=10;
    private GroupHistroyVideoListAdapter mUserHistoryVideoListAdapter;
    private ContentFragmentActivity mContext;
    private ReEmptyLayoutBinding mEmptyViewbindView;
    private List<UserPlayerVideoHistoryList> mGroupList=null;
    //分组
    private int mCurrentPosition = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private int headHeight;//分组头部高度

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (ContentFragmentActivity) context;
    }
    @Override
    protected void initViews() {
        initAdapter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_history_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new UserHistoryPresenter();
        mPresenter.attachView(this);
//        page=0;
        loadListData();
        //第一次使用弹出使用提示
        if(1!= SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_USER_HISTORY_CODE)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //删除视频提示
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.hint)
                            .setTitle("历史观看记保存录规则")
                            .setMessage("播放视频记录最多只保存最近播放的200条视频记录，超过200条以新替旧。播放完成的视频在不删除缓存文件的情况下再次播放无需消耗流量")
                            .setPositiveButton("知道了",
                                    null).setCancelable(false).show();
                    SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_USER_HISTORY_CODE,1);
                }
            },800);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mLinearLayoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        bindingView.recyerView.setLayoutManager(mLinearLayoutManager);
        mUserHistoryVideoListAdapter = new GroupHistroyVideoListAdapter(null);
        mUserHistoryVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                bindingView.recyerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserHistoryVideoListAdapter.loadMoreEnd();
                    }
                });
            }
        },bindingView.recyerView);
        mUserHistoryVideoListAdapter.setOnItemClickListener(new GroupHistroyVideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (null != mGroupList && mGroupList.size() > position) {
                    //全屏
                    if (ConfigSet.getInstance().isPlayerModel()) {
                        VerticalHistoryVideoPlayActivity.start(getActivity(), Constant.FRAGMENT_TYPE_HOSTORY,position,mGroupList);
                        //单个
                    } else {
                        UserPlayerVideoHistoryList userPlayerVideoHistoryList = mGroupList.get(position);
                        if (null != userPlayerVideoHistoryList && !TextUtils.isEmpty(userPlayerVideoHistoryList.getVideoId())) {
                            VideoDetailsActivity.start(getActivity(), userPlayerVideoHistoryList.getVideoId(), userPlayerVideoHistoryList.getUserId(), true);
                        }
                    }
                }
            }
        });
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.showLoadingView();
        mUserHistoryVideoListAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        bindingView.recyerView.setAdapter(mUserHistoryVideoListAdapter);
        //滚动点监听，实现分组悬浮
        bindingView.recyerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当recyclerView的滑动改变改变的时候 实时拿到它的高度
                headHeight = bindingView.tvHeaderView.getMeasuredHeight();
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(null!=mLinearLayoutManager){
                    View itemView = mLinearLayoutManager.findViewByPosition(mCurrentPosition + 1);
                    if (itemView != null) {
                        // 100      110  10
                        if (itemView.getTop() <= headHeight) {
                            bindingView.tvHeaderView.setY(-(headHeight - itemView.getTop()));
                        } else {
                            bindingView.tvHeaderView.setY(0);
                        }
                    }
                    //拿到当前显示的item的position
                    int currentPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (mCurrentPosition != currentPosition) {
                        mCurrentPosition = currentPosition;
                        bindingView.tvHeaderView.setY(0);
                        setHeaderData(mCurrentPosition);
                    }
                }
            }
        });
    }

    //设置Header数据
    private void setHeaderData(int currentPosition) {
        if(null!= mUserHistoryVideoListAdapter){
            List<HistoryVideoGroupList> data = mUserHistoryVideoListAdapter.getData();
            if(null!=data&&data.size()>0){
                HistoryVideoGroupList videoGroupList = data.get(currentPosition);
                if(null!=videoGroupList){
                    bindingView.tvHeaderView.setText(videoGroupList.getDateTime());
                    if(bindingView.tvHeaderView.getVisibility()!=View.VISIBLE) bindingView.tvHeaderView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 加载历史记录
     */
    private void loadListData() {
        if(null!=mPresenter&&!mPresenter.isVideoLoading()){
//            page++;
//            mUserHistoryPresenter.getVideoHistoryList(page,pageSize);
            mPresenter.getAllVideoHistoryList();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(null!=bindingView) bindingView.tvHeaderView.setVisibility(View.GONE);
    }

    /**
     * 提交事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SubmitEvent event){
        if(null!=event){
            if(TextUtils.equals("caneal_history",event.getMessage())){
                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("删除提示")
                        .setMessage("清空历史播放记录后无法恢复，确定继续吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(null!=mUserHistoryVideoListAdapter){
                                    ApplicationManager.getInstance().getUserPlayerDB().deteleAllPlayerHistoryList();
                                    mUserHistoryVideoListAdapter.setNewData(null);

                                    if(null!=mContext&&!mContext.isFinishing()){
//                                        page=0;
                                        loadListData();
                                    }
                                }
                            }
                        }).setCancelable(false).show();
            }
        }
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void showVideoHistoryList(List<UserPlayerVideoHistoryList> data) {
        if(null!=mContext&&!mContext.isFinishing()){
            mContext.showCanealHistoryMenu(true);
        }
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有播放记录~",R.drawable.iv_work_video_empty,false);
        if(null!=mUserHistoryVideoListAdapter){
            mGroupList=data;
            List<HistoryVideoGroupList> videoGroupLists = listToGroup(data);
            mUserHistoryVideoListAdapter.setNewData(videoGroupLists);
            if(null!=mLinearLayoutManager) mLinearLayoutManager.scrollToPositionWithOffset(0,0);
            mCurrentPosition=0;
            setHeaderData(mCurrentPosition);
//            if(1==page){
//                mUserHistoryVideoListAdapter.setNewData(data);
//            }else{
//                mUserHistoryVideoListAdapter.addData(data);
//            }
        }
    }
    @Override
    public void showVideoHistoryListEmpty(String data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有播放记录~",R.drawable.iv_work_video_empty,false);
        if(null!=mUserHistoryVideoListAdapter){
            List<HistoryVideoGroupList> dataList = mUserHistoryVideoListAdapter.getData();
            if(null!=mContext&&!mContext.isFinishing()&&null==dataList||dataList.size()<=0){
                mContext.showCanealHistoryMenu(false);
            }
        }
        if(null!=bindingView) bindingView.tvHeaderView.setVisibility(View.GONE);
//        if(page>0){
//            page--;
//        }
    }

    /**
     * 将数据分组封装
     * @param lists
     * @return
     */
    private List<HistoryVideoGroupList> listToGroup(List<UserPlayerVideoHistoryList> lists) {
        if(null!=lists&&lists.size()>0){
            List<HistoryVideoGroupList> videoGroupLists=new ArrayList<>();
            String dateTime="";
            int groupCount=-1;
            for (int i = 0; i < lists.size(); i++) {
                //第一条数据,一维数组中创建一个元素，元素中存放一个key和二维数组
                UserPlayerVideoHistoryList listsBean = lists.get(i);
                listsBean.setItemIndex(i);
                String timeForString = TimeUtils.getTimeForString(listsBean.getAddTime());
                if (0 == i) {
                    List<UserPlayerVideoHistoryList> items=new ArrayList<>();
                    HistoryVideoGroupList group = new HistoryVideoGroupList();
                    group.setDateTime(timeForString);
                    items.add(listsBean);
                    group.setListsBeans(items);
                    videoGroupLists.add(group);
                    dateTime = timeForString;
                    groupCount = 0;
                    //同一个属性下面的，将数据插入已有的一维数组中和二维数组中
                } else if (TextUtils.equals(dateTime, timeForString)) {
                    HistoryVideoGroupList videoGroupList = videoGroupLists.get(groupCount);
                    if (null != videoGroupList) {
                        videoGroupList.getListsBeans().add(listsBean);
                        dateTime = timeForString;
                    }
                    //新建一维数组元素，元素中存放一个key和二维数组
                } else if (!TextUtils.equals(dateTime, timeForString)) {
                    List<UserPlayerVideoHistoryList> items=new ArrayList<>();
                    HistoryVideoGroupList group = new HistoryVideoGroupList();
                    group.setDateTime(timeForString);
                    items.add(listsBean);
                    dateTime = timeForString;
                    group.setListsBeans(items);
                    videoGroupLists.add(group);
                    groupCount++;
                }
            }
            return videoGroupLists;
        }
        return new ArrayList<>();
    }
}
