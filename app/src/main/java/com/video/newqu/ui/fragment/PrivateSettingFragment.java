package com.video.newqu.ui.fragment;

import android.view.View;
import android.widget.CompoundButton;
import com.video.newqu.R;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.databinding.FragmentPrivateSettingBinding;
import com.video.newqu.ui.presenter.MainPresenter;

/**
 * TinyHung@Outlook.com
 * 2018/4/17
 * 隐私设置
 */

public class PrivateSettingFragment extends BaseLightWeightFragment<FragmentPrivateSettingBinding,MainPresenter>{

    private boolean isChecked=false;

    @Override
    protected void initViews() {
        bindingView.reFollowSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked=!isChecked;
                bindingView.swFollowSe.setChecked(isChecked);
            }
        });
        bindingView.swFollowSe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrivateSettingFragment.this.isChecked=isChecked;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_private_setting;
    }
}
