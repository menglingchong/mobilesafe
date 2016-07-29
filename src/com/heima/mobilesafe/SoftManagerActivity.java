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
		
		//�첽����AppInfo��Ϣ����
		fillData();
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
			}
		}.execute();
	}

	//����Adapter��
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
			//������listview���û���
			if (convertView == null) {
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
			}else {
				view =convertView;
				//��view�����л�ȡviewHolder�����ؼ�����
				viewHolder = (ViewHolder) view.getTag();
			}
			//��ȡ��Ӧ��Ŀ��bean����
			AppInfo appInfo = list.get(position);
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
