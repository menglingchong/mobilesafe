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
	//�û����򼯺�
	private ArrayList<AppInfo> userappInfo;
	//ϵͳ���򼯺�
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
		//�첽����AppInfo��Ϣ����
		fillData();
		//listView�Ļ�������
		listViewOnScroll();
	}
	/**
	 * listView�ؼ��Ļ�������
	 */
	private void listViewOnScroll() {
		lv_softmanager_appinfo.setOnScrollListener(new OnScrollListener() {
			//����״̬�ı��ʱ�����
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			//������ʱ�����
			//view:listView; firstVisibleItem:�����һ����ʾ����Ŀ��	visibleItemCount:��ʾ��Ŀ���ܸ�����totalItemCount:�ܹ�����Ŀ����
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//listView�ڳ�ʼ����ʱ��ͻ����onScroll()����,userappInfo��systemappInfo��δ���ء�������Ҫ���в�Ϊ�յ��ж�
				if (userappInfo!= null && systemappInfo != null) {
					if (firstVisibleItem >= userappInfo.size()+1) {
						tv_softmanager_userorsystem.setText("ϵͳ����("+systemappInfo.size()+")");
					}else {
						tv_softmanager_userorsystem.setText("�ֻ�����("+userappInfo.size()+")");
					}
				}
			}
		});
	}
	/**
	 * �첽��������
	 */
	private void fillData() {
		new MyAsynctask() {
		

			@Override
			public void preTask() {
				//��ʾProgressbar
				pb_softmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//��������ʾ������
				lv_softmanager_appinfo.setAdapter(new MyAdapter());
				//����progressbar
				pb_softmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//��������
				list = AppEngine.getAppInfos(getApplicationContext());
				//���û������ϵͳ��������
				userappInfo = new ArrayList<AppInfo>();
				systemappInfo = new ArrayList<AppInfo>();
				for (AppInfo appinfo : list) {
					//�����ݷֱ��ŵ��û����򼯺Ϻ�ϵͳ���򼯺���
					if (appinfo.isUser()) {
						userappInfo.add(appinfo);
					}else {
						systemappInfo.add(appinfo);
					}
				}
			}
		}.execute();
	}

	//����Adapter��
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
			//������Ŀ
			if (position ==0) {
				//����û�����(...��)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("�û�����("+userappInfo.size()+")");
				return textView;
			}else if (position == userappInfo.size()+1) {
				//���ϵͳ����(...��)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("ϵͳ����("+systemappInfo.size()+")");
				return textView;
			}
			//������listview���û���
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
				//��viewholer��view�����
				view.setTag(viewHolder);
			}
			//���ַ�ʽ��������ʱ�򣬻������Ŀ���ң�������Ϊ������������������textView��Ŀ���view��Ŀ�Ļ���!
			/*if (convertView == null) {
				//�������ļ�ת��ΪView����
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				//���ؼ��ŵ��ؼ�������
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				//��viewHolder��view�����
				view.setTag(viewHolder);
			}else{//���õ�ʱ���ж�view������
				if (convertView instanceof RelativeLayout ) {
					view =convertView;
					//��view�����л�ȡviewHolder�����ؼ�����
					viewHolder = (ViewHolder) view.getTag();
				}
			
			}*/
			//��ȡ��Ӧ��Ŀ��bean����
//			AppInfo appInfo = list.get(position);
			AppInfo appInfo ;
			//��Ϊ���ݷֱ����ϵͳ���򼯺Ϻ��û����򼯺��У�������Ӧ��Ŀbena����Ҫ����ͬ�ļ�����ȥ��ȡ 
			if (position <= userappInfo.size()) {
				//�û�����
				appInfo = userappInfo.get(position -1);
			}else {
				//ϵͳ����
				appInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
			//������ʾ����
			viewHolder.iv_itemsoftmanager_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_itemsoftmanager_name.setText(appInfo.getName());
			viewHolder.tv_itemsoftmanager_version.setText(appInfo.getVersionName());
			boolean sd = appInfo.isSD();
			if (sd) {
				//��װ��sd��
				viewHolder.tv_itemsoftmanager_issd.setText("SD���洢");
			}else {
				//��װ���ֻ�
				viewHolder.tv_itemsoftmanager_issd.setText("�ֻ��洢");
			}
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemsoftmanager_icon;
		TextView tv_itemsoftmanager_name,tv_itemsoftmanager_issd,tv_itemsoftmanager_version;
	}
}
