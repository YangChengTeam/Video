package com.video.newqu.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import com.video.newqu.R;
import com.video.newqu.adapter.CountryCodeAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.NumberCountryInfo;
import com.video.newqu.bean.PinyinComparator;
import com.video.newqu.bean.SerMap;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.CharacterParser;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityCountrySelectorBinding;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.manager.ThreadManager;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.view.widget.SeekBar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import cn.smssdk.SMSSDK;

/**
 * TinyHung@outlook.com
 * 2017/6/21 11:25
 * 国家代号选择
 */


public class CountryCodeSelectorActivity extends BaseActivity<ActivityCountrySelectorBinding>{

    private CountryCodeAdapter mCountryCodeAdapter;
    private List<NumberCountryInfo> mCountryList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selector);
        showProgressDialog("获取国际区号中..",true);
        getNumberList();
    }


    /**
     * 获取国际区号
     */
    private void getNumberList() {
        final SerMap serMap= (SerMap) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_COUNTRY_NUMBER_LIST);
        //取缓存
        if(null==serMap||serMap.getMap().size()<=0){
            ToastUtils.showCenterToast("区号列表不存在");
            finish();
        }
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    packageData(serMap);
                    mHandler.sendEmptyMessage(100);
                }catch (Exception e){
                    mHandler.sendEmptyMessage(99);
                }
            }
        });
    }

    @Override
    public void initViews() {
        setTitle("选择国家或地区");
        characterParser = CharacterParser.getInstance();
        //实例化汉字转拼音类
        pinyinComparator = new PinyinComparator();
        bindingView.seekBar.setTextView(bindingView.dialogText);
    }


    @Override
    public void initData() {

    }



    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(100==msg.what){
                closeProgressDialog();
                initAdapter();
                initSeekBar();
            }else if(99==msg.what){
                closeProgressDialog();
                ToastUtils.showCenterToast("获取国际区号失败！");
            }
        }
    };
    /**
     * 数据的封装
     * @param serMap
     */
    private void packageData(SerMap serMap) {
        HashMap<String, String> map = serMap.getMap();
        //遍历封装进List
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            String zone = next.getKey();
            String rule = next.getValue();
            //去除所有可用的国家代号代码
            String[] countryByMCC = SMSSDK.getCountryByMCC(zone);
            if(null!=countryByMCC&&countryByMCC.length>0){
                NumberCountryInfo numberCountryInfo=new NumberCountryInfo();
                numberCountryInfo.setRule(rule);
                numberCountryInfo.setCountryName(countryByMCC[0]);
                numberCountryInfo.setZone(countryByMCC[1]);
                //汉字转换成拼音
                String pinyin = characterParser.getSelling(countryByMCC[0]);
                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if(sortString.matches("[A-Z]")){
                    numberCountryInfo.setSortLetters(sortString.toUpperCase());
                }else{
                    numberCountryInfo.setSortLetters("#");
                }
                if(null==mCountryList){
                    mCountryList=new ArrayList<>();
                }
                mCountryList.add(numberCountryInfo);
            }
        }
    }



    /**
     * 初始化导航栏
     */
    private void initSeekBar() {
        //导航栏
        bindingView.seekBar.setOnTouchingLetterChangedListener(new SeekBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mCountryCodeAdapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    bindingView.listView.setSelection(position);
                }
            }
        });
    }



    /**
     * 初始化适配器
     */
    private void initAdapter() {

        if(null!=mCountryList&&mCountryList.size()>0){
            mCountryList.add(new NumberCountryInfo("86","Z","^((13[0-9])|(15[0-9])|(18[0-9])|(16[0-9])|(17[0-9]))\\d{8}$","中国"));
            Collections.sort(mCountryList, pinyinComparator);
        }
        mCountryCodeAdapter = new CountryCodeAdapter(CountryCodeSelectorActivity.this,mCountryList);
        bindingView.listView.setAdapter(mCountryCodeAdapter);
        //条目点击监听
        bindingView.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<NumberCountryInfo> listData = mCountryCodeAdapter.getListData();
                NumberCountryInfo numberCountryInfo = listData.get(position);
                backLast(numberCountryInfo);
            }
        });

        //根据输入框输入值的改变来过滤搜索
        bindingView.filterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }









    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<NumberCountryInfo> filterDateList = new ArrayList<NumberCountryInfo>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = mCountryList;
        }else{
            filterDateList.clear();
            for(NumberCountryInfo countryInfo : mCountryList){
                String name = countryInfo.getCountryName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(countryInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mCountryCodeAdapter.updateListView(filterDateList);
    }

    /**
     * 点击条目后跳转
     * @param numberCountryInfo
     */
    private void backLast(NumberCountryInfo numberCountryInfo) {
        Intent intent=new Intent();
        intent.putExtra("numberCountryInfo",numberCountryInfo);
        setResult(89,intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
