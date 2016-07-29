package com.heima.mobilesafe;

import java.util.List;

import com.heima.mobilesafe.bean.AppInfo;
import com.heima.mobilesafe.engine.AppEngine;
import com.heima.mobilesafe.utils.MyAsynctask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SoftManagerActivity extends Activity {

	private ListView lv_softmanager_appinfo;
	private ProgressBar pb_softmanager_loading;
	private List<AppInfo> list;
	private View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_softmanager);
		
		lv_softmanager_appinfo = (ListView) findViewById(R.id.lv_softmanager_appinfo);
		pb_softmanager_loading = (ProgressBar) findViewById(R.id.pb_softmanager_loading);
		
		//异步加载AppInfo信息数据
		fillData();
	}
	/**
	 * 异步加载数据
	 */
	private void fillData() {
		new MyAsynctask() {
			@Override
			public void preTask() {
				//显示Progressbar
				pb_softmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//将数据显示到界面
				lv_softmanager_appinfo.setAdapter(new MyAdapter());
				//隐藏progressbar
				pb_softmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//加载数据
				list = AppEngine.getAppInfos(getApplicationContext());
			}
		}.execute();
	}

	//创建Adapter类
	private class MyAdapter extends BaseAdapter{

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//完整的listview复用缓存
			if (convertView == null) {
				//将布局文件转化为View对象
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				//将控件放到控件容器中
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				//将viewHolder和view对象绑定
				view.setTag(viewHolder);
			}else {
				view =convertView;
				//从view对象中获取viewHolder，即控件容器
				viewHolder = (ViewHolder) view.getTag();
			}
			//获取相应条目的bean对象
			AppInfo appInfo = list.get(position);
			//设置显示数据
			viewHolder.iv_itemsoftmanager_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_itemsoftmanager_name.setText(appInfo.getName());
			viewHolder.tv_itemsoftmanager_version.setText(appInfo.getVersionName());
			boolean sd = appInfo.isSD();
			if (sd) {
				//安装在sd卡
				viewHolder.tv_itemsoftmanager_issd.setText("SD卡存储");
			}else {
				//安装在手机
				viewHolder.tv_itemsoftmanager_issd.setText("手机存储");
			}
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemsoftmanager_icon;
		TextView tv_itemsoftmanager_name,tv_itemsoftmanager_issd,tv_itemsoftmanager_version;
	}
}
