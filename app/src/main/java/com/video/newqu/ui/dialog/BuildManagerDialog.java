package com.video.newqu.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.UpdataApkInfo;

/**
 * TinyHung@outlook.com
 * 2017/6/19 15:27
 * 软件更新
 */

public class BuildManagerDialog extends Dialog {


    public interface  OnUpdataListener{
        void onUpdata();
    }

    private  OnUpdataListener mOnUpdataListener;

    public void setOnUpdataListener(OnUpdataListener onUpdataListener) {
        mOnUpdataListener = onUpdataListener;
    }


    public BuildManagerDialog(Context context, int menuDialog) {
        super(context, menuDialog);
        setContentView(R.layout.dialog_updata);
        setCancelable(false);
        initViews();
    }

    private void initViews() {

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close:
                        BuildManagerDialog.this.dismiss();
                        break;

                    case R.id.btn_upload:
                        BuildManagerDialog.this.dismiss();
                        if(null!=mOnUpdataListener){
                            mOnUpdataListener.onUpdata();
                        }
                        break;
                }
            }
        };
        ((ImageView) findViewById(R.id.btn_close)).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.btn_upload)).setOnClickListener(onClickListener);
    }


    /**
     * 设置更新内容
     * @param data
     */
    public void setUpdataData(UpdataApkInfo.DataBean data) {
        if(null==data) return;
        TextView tvVerstion = (TextView) findViewById(R.id.tv_verstion);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        TextView tvSize = (TextView) findViewById(R.id.tv_size);
        SpannableString spannableString = new SpannableString("最新版本："+data.getVersion());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFF84F39")), 5,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvVerstion.setText(spannableString);
        if(TextUtils.isEmpty(data.getSize())){
            data.setSize("15.8");
        }
        SpannableString spannableSizeString = new SpannableString("版本大小："+data.getSize()+"M");
        spannableSizeString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFF84F39")), 5,spannableSizeString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSize.setText(spannableSizeString);
        String desp = data.getUpdate_log();
        if(TextUtils.isEmpty(desp)){
            desp="优化性能，提升稳定性 程序员修复了一些已知的BUG";
        }
        String[] strings = desp.split(" ");
        if(null!=strings&&strings.length>0){
            StringBuilder sb=new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                if(0==i){
                }else if(1==i){
                    sb.append(i+"：").append(strings[i]);//第一条不需要下划线
                }else{
                    sb.append("\n"+i+"：").append(strings[i]);//第一条不需要下划线
                }
            }
            tvContent.setText(sb.toString());
        }else{
            tvContent.setText(desp);
        }
    }
}
