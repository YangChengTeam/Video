package com.video.newqu.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import com.video.newqu.R;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.StickerDataBean;
import com.video.newqu.camera.adapter.MediaEditNetStickerListAdapter;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentRecyclerviewLayoutBinding;
import com.video.newqu.databinding.ReEmptyLayoutBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.listener.OnMediaStickerListener;
import com.video.newqu.ui.presenter.MediaStickerPresenter;
import com.video.newqu.util.Utils;
import com.video.newqu.model.GridSpaceItemDecorationComent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
/**
 * TinyHung@Outlook.com
 * 2017/9/12.
 * 用户用过的贴纸列表片段
 */
public class MediaStickerUseFragment extends BaseFragment<FragmentRecyclerviewLayoutBinding,MediaStickerPresenter> implements Observer {

    private static OnMediaStickerListener mOnMediaStickerListener;
    private MediaEditNetStickerListAdapter mMediaEditStickerAdapter;
    private ReEmptyLayoutBinding mEmptyViewbindView;
    public static MediaStickerUseFragment newInstance(OnMediaStickerListener onMediaStickerListener){
        MediaStickerUseFragment mediaStickerFragment=new MediaStickerUseFragment();
        mOnMediaStickerListener=onMediaStickerListener;
        return mediaStickerFragment;
    }

    @Override
    protected void initViews() {}

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyclerview_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        ApplicationManager.getInstance().addObserverToMusic(this);
    }

    /**
     * 初始化贴纸适配器
     */
    private void initAdapter() {
        List<StickerDataBean> stickerList = ApplicationManager.getInstance().getStickerDB().getStickerList();
        Collections.sort(stickerList, new Comparator<StickerDataBean>() {
            @Override
            public int compare(StickerDataBean o1, StickerDataBean o2) {
                return String.valueOf(o2.getUpdataTime()).compareTo(String.valueOf(o1.getUpdataTime()));
            }
        });
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(),5, LinearLayoutManager.VERTICAL,false));
        bindingView.recyerView.addItemDecoration(new GridSpaceItemDecorationComent(Utils.dip2px(6)));
        mMediaEditStickerAdapter = new MediaEditNetStickerListAdapter(stickerList,mOnMediaStickerListener);
        //占位布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.showLoadingView();
        mEmptyViewbindView.emptyView.showEmptyView("还没有使用记录~",R.drawable.ic_list_empty_icon,false);
        mMediaEditStickerAdapter.setEmptyView(mEmptyViewbindView.getRoot());
        bindingView.recyerView.setAdapter(mMediaEditStickerAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserverToMusic(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Integer){
            Integer action= (Integer) arg;
            switch (action) {
                case Constant.OBSERVABLE_ACTION_STICKER_CHANGED:
                    if(null!=mMediaEditStickerAdapter){
                        List<StickerDataBean> stickerList = ApplicationManager.getInstance().getStickerDB().getStickerList();
                        Collections.sort(stickerList, new Comparator<StickerDataBean>() {
                            @Override
                            public int compare(StickerDataBean o1, StickerDataBean o2) {
                                return String.valueOf(o2.getUpdataTime()).compareTo(String.valueOf(o1.getUpdataTime()));
                            }
                        });
                        mMediaEditStickerAdapter.setNewData(stickerList);
                    }
                    break;
            }
        }
    }
}
