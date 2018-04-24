package com.video.newqu.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.StickerDataBean;
import com.video.newqu.bean.StickerNetInfo;
import com.video.newqu.camera.adapter.MediaEditNetStickerListAdapter;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.databinding.FragmentRecyclerviewLayoutBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.OnMediaStickerListener;
import com.video.newqu.ui.contract.MediaStickerContract;
import com.video.newqu.ui.presenter.MediaStickerPresenter;
import com.video.newqu.util.Utils;
import com.video.newqu.model.GridSpaceItemDecorationComent;
import com.video.newqu.view.layout.DataChangeView;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/12.
 * 贴纸列表片段
 */

public class MediaStickerFragment extends BaseFragment<FragmentRecyclerviewLayoutBinding,MediaStickerPresenter> implements MediaStickerContract.View {

    private static OnMediaStickerListener mOnMediaStickerListener;
    private String mStickerID;
    private MediaEditNetStickerListAdapter mMediaEditStickerAdapter=null;
    private int mPage=0;
    private int pageSize=10;
    private ReEmptyLayoutBinding mEmptyViewbindView;

    public static MediaStickerFragment newInstance(String typeID, OnMediaStickerListener onMediaStickerListener){
        MediaStickerFragment mediaStickerFragment=new MediaStickerFragment();
        Bundle bundle=new Bundle();
        bundle.putString("type_id",typeID);
        mediaStickerFragment.setArguments(bundle);
        mOnMediaStickerListener=onMediaStickerListener;
        return mediaStickerFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取出参数
        Bundle arguments = getArguments();
        if(null!=arguments) {
            mStickerID = arguments.getString("type_id");
        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyclerview_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new MediaStickerPresenter(getActivity());
        mPresenter.attachView(this);
        initAdapter();
    }

    @Override
    protected void initViews() {}

    @Override
    protected void onVisible() {
        super.onVisible();
        if(null!=bindingView&&null!=mMediaEditStickerAdapter&&(null==mMediaEditStickerAdapter||mMediaEditStickerAdapter.getData().size()<=0)&&null!=mPresenter&&!mPresenter.isLoading()){
            mPage=0;
            loadStickerList();
        }
    }
    private void loadStickerList() {
        if(null!= mPresenter &&!TextUtils.isEmpty(mStickerID)){
            mPage++;
            mPresenter.getStickerTypeList(mStickerID,mPage,pageSize);
        }
    }
    /**
     * 初始化贴纸适配器
     */
    private void initAdapter() {
        List<StickerDataBean> cacheList= (List<StickerDataBean>)  ApplicationManager.getInstance().getCacheExample().getAsObject(mStickerID+Constant.STICKER_STICKERID_LIST);
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(),5, LinearLayoutManager.VERTICAL,false));
        bindingView.recyerView.addItemDecoration(new GridSpaceItemDecorationComent(Utils.dip2px(6)));
        bindingView.recyerView.setHasFixedSize(true);
        mMediaEditStickerAdapter = new MediaEditNetStickerListAdapter(cacheList,mOnMediaStickerListener);
        //占位布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyViewbindView.emptyView.showLoadingView();
                loadStickerList();
            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
        mMediaEditStickerAdapter.setEmptyView(mEmptyViewbindView.getRoot());

        mMediaEditStickerAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadStickerList();
            }
        }, bindingView.recyerView);
        bindingView.recyerView.setAdapter(mMediaEditStickerAdapter);
    }

    /**
     * 网络失败
     */
    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}
    /**
     * 获取分类下列表成功
     * @param data
     */
    @Override
    public void showStickerList(StickerNetInfo data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有贴纸素材~",R.drawable.ic_list_empty_icon,true);
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                mMediaEditStickerAdapter.setNewData(data.getData());
                ApplicationManager.getInstance().getCacheExample().remove(mStickerID+Constant.STICKER_STICKERID_LIST);
                ApplicationManager.getInstance().getCacheExample().put(mStickerID+Constant.STICKER_STICKERID_LIST, (Serializable) data.getData(), Constant.CACHE_STICKER_TIME);
                //添加数据
            }else{
                mMediaEditStickerAdapter.addData(data.getData());
            }
        }
    }

    /**
     * 加载贴纸列表为空
     */
    @Override
    public void showStickerEmpty(String data) {
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("没有贴纸素材~",R.drawable.ic_list_empty_icon,true);
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mMediaEditStickerAdapter.setNewData(null);
                ApplicationManager.getInstance().getCacheExample().remove(mStickerID+Constant.STICKER_STICKERID_LIST);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 加载贴纸列表错误
     */
    @Override
    public void showStickerError(String data) {
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.loadMoreFail();
            List<StickerDataBean> dataList = mMediaEditStickerAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showErrorView();
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.pause(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.pause(true);
        }
    }

    @Override
    public void onDestroy() {
        if(null!=mMediaEditStickerAdapter){
            mMediaEditStickerAdapter.stopDownload();
        }
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.onDestroy();
        super.onDestroy();
    }
}
