package com.heima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heima.mobilesafe.bean.TaskInfo;
import com.heima.mobilesafe.engine.TaskEngine;
import com.heima.mobilesafe.utils.MyAsynctask;

public class TaskManagerActivity extends Activity {

	private ListView lv_taskmanager_process;
	private ProgressBar pb_taskmanager_loading;
	private View view;
	private MyAdapter myAdapter;
	private TaskInfo taskInfo;
	private List<TaskInfo> list;
	//用户程序集合
	private ArrayList<TaskInfo> userappInfo;
	//系统程序集合
	private ArrayList<TaskInfo> systemappInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		lv_taskmanager_process = (ListView) findViewById(R.id.lv_taskmanager_process);
		pb_taskmanager_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loading);
		//加载数据
		fillData();
		//条目的点击事件,更改条目的选择状态
		listViewItemClick();
	}
	//条目的点击事件
	private void listViewItemClick() {
		lv_taskmanager_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//修改checkbox的状态
				//屏蔽显示进程条目的view对象
				if (position ==0 || position == userappInfo.size()+1) {
					return;
				}
				//获取条目对应的信息
				if (position <= userappInfo.size()) {
					taskInfo = userappInfo.get(position-1);
				}else {
					taskInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//根据之前的checkbox的状态，来设置现在的checkbox的状态
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);//设置为取消状态
				}else {
					taskInfo.setChecked(true);//设置为选中状态
				}
				//更新adapter
//				myAdapter.notifyDataSetChanged();
				//只更新点击的条目
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(taskInfo.isChecked());
			}
		});
	}
	//加载数据
	private void fillData() {
		new MyAsynctask() {
			@Override
			public void preTask() {
				//显示Progressbar
				pb_taskmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//将数据显示到界面
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_taskmanager_process.setAdapter(myAdapter);
				}else {
					//更新适配器数据
					myAdapter.notifyDataSetChanged();
				}
				
				//隐藏progressbar
				pb_taskmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//加载数据
				list = TaskEngine.getAllTaskInfo(getApplicationContext());
				//将用户程序和系统程序区分
				userappInfo = new ArrayList<TaskInfo>();
				systemappInfo = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : list) {
					//将数据分别存放到用户程序集合和系统程序集合中
					if (taskInfo.isUser()) {
						userappInfo.add(taskInfo);
					}else {
						systemappInfo.add(taskInfo);
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
				textView.setText("用户进程("+userappInfo.size()+")");
				return textView;
			}else if (position == userappInfo.size()+1) {
				//添加系统程序(...个)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("系统进程("+systemappInfo.size()+")");
				return textView;
			}
			//完整的listview复用缓存
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag(); 
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_taskmanager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemtaskmanager_icon = (ImageView) view.findViewById(R.id.iv_itemtaskmanager_icon);
				viewHolder.tv_itemtaskmanager_name = (TextView) view.findViewById(R.id.tv_itemtaskmanager_name);
				viewHolder.tv_itemtaskmanager_ram = (TextView) view.findViewById(R.id.tv_itemtaskmanager_ram);
				viewHolder.cb_itemtaskmanager_ischecked = (CheckBox) view.findViewById(R.id.cb_itemtaskmanager_ischecked);
				//将viewholer和view对象绑定
				view.setTag(viewHolder);
			}
			//因为数据分别存在系统程序集合和用户程序集合中，所以相应条目bena对象要到不同的集合中去获取 
			if (position <= userappInfo.size()) {
				//用户程序
				taskInfo = userappInfo.get(position -1);
			}else {
				//系统程序
				taskInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
			//设置显示数据
			//Icon为空的应用，将Icon设置为默认的图片
			if (taskInfo.getIcon()==null) {
				viewHolder.iv_itemtaskmanager_icon.setImageResource(R.drawable.ic_default);
			}else {
				viewHolder.iv_itemtaskmanager_icon.setImageDrawable(taskInfo.getIcon());
			}
			//名称为空的应用，则将名称设置为包名
			if (TextUtils.isEmpty(taskInfo.getPackageName())) {
				viewHolder.tv_itemtaskmanager_name.setText(taskInfo.getPackageName());

			}else{
				viewHolder.tv_itemtaskmanager_name.setText(taskInfo.getName());
			}
			//数据转化
			long romSize = taskInfo.getRomSize();
			String formatFileSize = Formatter.formatFileSize(getApplicationContext(), romSize);
			viewHolder.tv_itemtaskmanager_ram.setText("内存占用："+formatFileSize);
			//因为checkbox的状态会跟着控件一起复用，则要动态修改控件的状态，不会跟着控件去复用，将checkbox状态保存到bean对象中、
			//在每次复用控件的时候，根据每个条目对应的bean对象保存的状态，来设置控件显示的相应状态
			if (taskInfo.isChecked()) {
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(true);
			}else {
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(false);
			}
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemtaskmanager_icon;
		TextView tv_itemtaskmanager_name,tv_itemtaskmanager_ram;
		CheckBox cb_itemtaskmanager_ischecked;
	}
		
}
