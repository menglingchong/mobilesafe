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
	//�û����򼯺�
	private ArrayList<TaskInfo> userappInfo;
	//ϵͳ���򼯺�
	private ArrayList<TaskInfo> systemappInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		lv_taskmanager_process = (ListView) findViewById(R.id.lv_taskmanager_process);
		pb_taskmanager_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loading);
		//��������
		fillData();
		//��Ŀ�ĵ���¼�,������Ŀ��ѡ��״̬
		listViewItemClick();
	}
	//��Ŀ�ĵ���¼�
	private void listViewItemClick() {
		lv_taskmanager_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//�޸�checkbox��״̬
				//������ʾ������Ŀ��view����
				if (position ==0 || position == userappInfo.size()+1) {
					return;
				}
				//��ȡ��Ŀ��Ӧ����Ϣ
				if (position <= userappInfo.size()) {
					taskInfo = userappInfo.get(position-1);
				}else {
					taskInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//����֮ǰ��checkbox��״̬�����������ڵ�checkbox��״̬
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);//����Ϊȡ��״̬
				}else {
					taskInfo.setChecked(true);//����Ϊѡ��״̬
				}
				//����adapter
//				myAdapter.notifyDataSetChanged();
				//ֻ���µ������Ŀ
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(taskInfo.isChecked());
			}
		});
	}
	//��������
	private void fillData() {
		new MyAsynctask() {
			@Override
			public void preTask() {
				//��ʾProgressbar
				pb_taskmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//��������ʾ������
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_taskmanager_process.setAdapter(myAdapter);
				}else {
					//��������������
					myAdapter.notifyDataSetChanged();
				}
				
				//����progressbar
				pb_taskmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//��������
				list = TaskEngine.getAllTaskInfo(getApplicationContext());
				//���û������ϵͳ��������
				userappInfo = new ArrayList<TaskInfo>();
				systemappInfo = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : list) {
					//�����ݷֱ��ŵ��û����򼯺Ϻ�ϵͳ���򼯺���
					if (taskInfo.isUser()) {
						userappInfo.add(taskInfo);
					}else {
						systemappInfo.add(taskInfo);
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
				view = View.inflate(getApplicationContext(), R.layout.item_taskmanager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemtaskmanager_icon = (ImageView) view.findViewById(R.id.iv_itemtaskmanager_icon);
				viewHolder.tv_itemtaskmanager_name = (TextView) view.findViewById(R.id.tv_itemtaskmanager_name);
				viewHolder.tv_itemtaskmanager_ram = (TextView) view.findViewById(R.id.tv_itemtaskmanager_ram);
				viewHolder.cb_itemtaskmanager_ischecked = (CheckBox) view.findViewById(R.id.cb_itemtaskmanager_ischecked);
				//��viewholer��view�����
				view.setTag(viewHolder);
			}
			//��Ϊ���ݷֱ����ϵͳ���򼯺Ϻ��û����򼯺��У�������Ӧ��Ŀbena����Ҫ����ͬ�ļ�����ȥ��ȡ 
			if (position <= userappInfo.size()) {
				//�û�����
				taskInfo = userappInfo.get(position -1);
			}else {
				//ϵͳ����
				taskInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
			//������ʾ����
			//IconΪ�յ�Ӧ�ã���Icon����ΪĬ�ϵ�ͼƬ
			if (taskInfo.getIcon()==null) {
				viewHolder.iv_itemtaskmanager_icon.setImageResource(R.drawable.ic_default);
			}else {
				viewHolder.iv_itemtaskmanager_icon.setImageDrawable(taskInfo.getIcon());
			}
			//����Ϊ�յ�Ӧ�ã�����������Ϊ����
			if (TextUtils.isEmpty(taskInfo.getPackageName())) {
				viewHolder.tv_itemtaskmanager_name.setText(taskInfo.getPackageName());

			}else{
				viewHolder.tv_itemtaskmanager_name.setText(taskInfo.getName());
			}
			//����ת��
			long romSize = taskInfo.getRomSize();
			String formatFileSize = Formatter.formatFileSize(getApplicationContext(), romSize);
			viewHolder.tv_itemtaskmanager_ram.setText("�ڴ�ռ�ã�"+formatFileSize);
			//��Ϊcheckbox��״̬����ſؼ�һ���ã���Ҫ��̬�޸Ŀؼ���״̬��������ſؼ�ȥ���ã���checkbox״̬���浽bean�����С�
			//��ÿ�θ��ÿؼ���ʱ�򣬸���ÿ����Ŀ��Ӧ��bean���󱣴��״̬�������ÿؼ���ʾ����Ӧ״̬
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
