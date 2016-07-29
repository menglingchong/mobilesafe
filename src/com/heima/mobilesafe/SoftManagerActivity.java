package com.heima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.heima.mobilesafe.bean.AppInfo;
import com.heima.mobilesafe.engine.AppEngine;
import com.heima.mobilesafe.utils.MyAsynctask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SoftManagerActivity extends Activity {

	private ListView lv_softmanager_appinfo;
	private ProgressBar pb_softmanager_loading;
	private List<AppInfo> list;
	//用户程序集合
	private ArrayList<AppInfo> userappInfo;
	//系统程序集合
	private ArrayList<AppInfo> systemappInfo;
	private View view;
	private TextView tv_softmanager_userorsystem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_softmanager);
		
		lv_softmanager_appinfo = (ListView) findViewById(R.id.lv_softmanager_appinfo);
		pb_softmanager_loading = (ProgressBar) findViewById(R.id.pb_softmanager_loading);
		tv_softmanager_userorsystem = (TextView) findViewById(R.id.tv_softmanager_userorsystem);
		//异步加载AppInfo信息数据
		fillData();
		//listView的滑动监听
		listViewOnScroll();
	}
	/**
	 * listView控件的滑动监听
	 */
	private void listViewOnScroll() {
		lv_softmanager_appinfo.setOnScrollListener(new OnScrollListener() {
			//滑动状态改变的时候调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			//滑动的时候调用
			//view:listView; firstVisibleItem:界面第一个显示的条目；	visibleItemCount:显示条目的总个数；totalItemCount:总共的条目个数
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//listView在初始化的时候就会调用onScroll()方法,userappInfo和systemappInfo还未加载。所以先要进行不为空的判断
				if (userappInfo!= null && systemappInfo != null) {
					if (firstVisibleItem >= userappInfo.size()+1) {
						tv_softmanager_userorsystem.setText("系统程序("+systemappInfo.size()+")");
					}else {
						tv_softmanager_userorsystem.setText("手机程序("+userappInfo.size()+")");
					}
				}
			}
		});
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
				//将用户程序和系统程序区分
				userappInfo = new ArrayList<AppInfo>();
				systemappInfo = new ArrayList<AppInfo>();
				for (AppInfo appinfo : list) {
					//将数据分别存放到用户程序集合和系统程序集合中
					if (appinfo.isUser()) {
						userappInfo.add(appinfo);
					}else {
						systemappInfo.add(appinfo);
					}
				}
			}
		}.execute();
	}

	//创建Adapter类
	private class MyAdapter extends BaseAdapter{

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
//			list.size() = userappInfo.size()+systemappInfo.size();
			return userappInfo.size()+systemappInfo.size() +2;
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
			//增加条目
			if (position ==0) {
				//添加用户程序(...个)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("用户程序("+userappInfo.size()+")");
				return textView;
			}else if (position == userappInfo.size()+1) {
				//添加系统程序(...个)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("系统程序("+systemappInfo.size()+")");
				return textView;
			}
			//完整的listview复用缓存
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				//将viewholer和view对象绑定
				view.setTag(viewHolder);
			}
			//这种方式当滑动的时候，会出现条目错乱，这是因为我们添加了另外的两个textView条目造成view条目的混乱!
			/*if (convertView == null) {
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
			}else{//复用的时候判断view的类型
				if (convertView instanceof RelativeLayout ) {
					view =convertView;
					//从view对象中获取viewHolder，即控件容器
					viewHolder = (ViewHolder) view.getTag();
				}
			
			}*/
			//获取相应条目的bean对象
//			AppInfo appInfo = list.get(position);
			AppInfo appInfo ;
			//因为数据分别存在系统程序集合和用户程序集合中，所以相应条目bena对象要到不同的集合中去获取 
			if (position <= userappInfo.size()) {
				//用户程序
				appInfo = userappInfo.get(position -1);
			}else {
				//系统程序
				appInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
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
