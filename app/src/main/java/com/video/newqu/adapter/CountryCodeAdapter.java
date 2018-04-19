package com.video.newqu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.video.newqu.R;
import com.video.newqu.bean.NumberCountryInfo;

import java.util.List;


/**
 * TinyHung@outlook.com
 * 2017/6/16 12:55
 * 国家列表
 */
public class CountryCodeAdapter extends BaseAdapter implements SectionIndexer{

	private List<NumberCountryInfo> countryInfos;
	private final LayoutInflater mLayoutInflater;

	public CountryCodeAdapter(Context context, List<NumberCountryInfo> countryInfos) {
		this.countryInfos = countryInfos;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void updateListView(List<NumberCountryInfo> list){
		this.countryInfos = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return null==countryInfos?0:countryInfos.size();
	}

	public Object getItem(int position) {
		return null==countryInfos?null:countryInfos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View contentView, ViewGroup arg2) {
		ViewHolder viewHolder ;
		if ( null==contentView) {
			viewHolder = new ViewHolder();
			contentView =mLayoutInflater.inflate(R.layout.list_country_code_item, null);
			viewHolder.tv_item_pinyin = (TextView) contentView.findViewById(R.id.tv_item_pinyin);
			viewHolder.tv_item_title = (TextView) contentView.findViewById(R.id.tv_item_title);
			contentView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) contentView.getTag();
		}
		try {
			NumberCountryInfo numberCountryInfo = countryInfos.get(position);
			if(null!=numberCountryInfo){
				//根据poistion设置分类的首字母
				int section = getSectionForPosition(position);

				//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
				if(position == getPositionForSection(section)){
					viewHolder.tv_item_pinyin.setVisibility(View.VISIBLE);
					viewHolder.tv_item_pinyin.setText(numberCountryInfo.getSortLetters());
				}else{
					viewHolder.tv_item_pinyin.setVisibility(View.GONE);
				}
				viewHolder.tv_item_title.setText(numberCountryInfo.getCountryName()+" +"+numberCountryInfo.getZone());
			}
		}catch (Exception e){

		}

		return contentView;
	}

	public List<NumberCountryInfo> getListData() {
		return countryInfos;
	}

	private class ViewHolder{
		private TextView tv_item_title;
		private TextView tv_item_pinyin;

	}



	//根据ListView的当前位置获取分类的首字母的Char ascii值
	public int getSectionForPosition(int position) {
		return countryInfos.get(position).getSortLetters().charAt(0);
	}

	//根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = countryInfos.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	

	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();

		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}