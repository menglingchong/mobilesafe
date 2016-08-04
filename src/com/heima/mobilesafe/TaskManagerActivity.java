package com.heima.mobilesafe;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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
import android.widget.Toast;

import com.heima.mobilesafe.bean.TaskInfo;
import com.heima.mobilesafe.engine.TaskEngine;
import com.heima.mobilesafe.utils.MyAsynctask;
import com.heima.mobilesafe.utils.TaskUtil;

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
	private TextView tv_taskmanager_count;
	private TextView tv_taskmanager_rom;
	private int processCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		lv_taskmanager_process = (ListView) findViewById(R.id.lv_taskmanager_process);
		pb_taskmanager_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loading);
		tv_taskmanager_count = (TextView) findViewById(R.id.tv_taskmanager_count);
		tv_taskmanager_rom = (TextView) findViewById(R.id.tv_taskmanager_rom);
		
		//��ȡϵͳ�������еĽ��̸������ڴ��С
		processCount = TaskUtil.getProcessCount(getApplicationContext());
		long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
		//��ȡ���ڴ�
		//���ݲ�ͬ��sdk�汾ȥ���ò�ͬ�ķ���
		//��ȡ��ǰ��sdk�汾
		int sdk =android.os.Build.VERSION.SDK_INT;
		long totalRam=0;
		if (sdk >=16) {
			totalRam = TaskUtil.getTotalRam(getApplicationContext());
		}else {
			totalRam = TaskUtil.getTotalRam();
		}
		//����ת��
		String avRam = Formatter.formatFileSize(getApplicationContext(), availableRam);
		String toRam = Formatter.formatFileSize(getApplicationContext(), totalRam);
		//�����������еĽ��̵ĸ������ڴ�Ĵ�С
		tv_taskmanager_count.setText("�����еĽ��̣�\n"+processCount+"��");
		tv_taskmanager_rom.setText("ʣ���ڴ�/���ڴ棺\n"+avRam+"/"+toRam);
		
		
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
					//�ж�������ǵ�ǰӦ��������Ϊѡ��״̬ 
					if (!taskInfo.getPackageName().equals(getPackageName())) {
						taskInfo.setChecked(true);//����Ϊѡ��״̬
					}
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
			//�ж�����Ǹ�Ӧ�ã���checkbox���ء����ǵĻ�����ʾcheckbox����getView����if����Ҫ��else
			if (taskInfo.getPackageName().equals(getPackageName())) {
				viewHolder.cb_itemtaskmanager_ischecked.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.cb_itemtaskmanager_ischecked.setVisibility(View.VISIBLE);
			}
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemtaskmanager_icon;
		TextView tv_itemtaskmanager_name,tv_itemtaskmanager_ram;
		CheckBox cb_itemtaskmanager_ischecked;
	}
	
	//ȫѡ
	public void all(View v){
		//�û�����
		for (int i = 0; i < userappInfo.size(); i++) {
			//���ǵ�ǰ���̣���checkbox����Ϊtrue
			if (!userappInfo.get(i).getPackageName().equals(getPackageName())) {
				userappInfo.get(i).setChecked(true);
			}
		}
		//ϵͳ����
		for (int i = 0; i < systemappInfo.size(); i++) {
			systemappInfo.get(i).setChecked(true);
		}
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	//ȡ��ȫѡ
	public void cancle(View v){
		//�û�����
		for (int i = 0; i < userappInfo.size(); i++) {
			//���ǵ�ǰ���̣���checkbox����Ϊtrue
			if (!userappInfo.get(i).getPackageName().equals(getPackageName())) {
				userappInfo.get(i).setChecked(false);
			}
		}
		//ϵͳ����
		for (int i = 0; i < systemappInfo.size(); i++) {
			systemappInfo.get(i).setChecked(false);
		}
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	//����
	public void clear(View v){
		//����ѡ�еĽ���
		//��ȡ���̵Ĺ�����
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//��ɱ���Ľ��̱��浽�ü�����
		List<TaskInfo> deleteList = new ArrayList<TaskInfo>();
		
		for (int i = 0; i < userappInfo.size(); i++) {
			if (userappInfo.get(i).isChecked()) {
				//����ѡ�еĽ���
				//packageName:Ҫ����Ľ��̵İ���
				activityManager.killBackgroundProcesses(userappInfo.get(i).getPackageName());
				//��ɱ���Ľ��̵���Ϣ���浽������
				deleteList.add(userappInfo.get(i));
			}
		}
		
		for (int i = 0; i < systemappInfo.size(); i++) {
			if (systemappInfo.get(i).isChecked()) {
				activityManager.killBackgroundProcesses(systemappInfo.get(i).getPackageName());
				deleteList.add(systemappInfo.get(i));
			}
		}
		long memory =0;
		//����deleteList���ֱ��userappinfo��systemappinfo��ɾ��deleteList�е�����
		for (TaskInfo taskInfo : deleteList) {
			if (taskInfo.isUser()) {
				//ɾ���û�����
				userappInfo.remove(taskInfo);
			}else {
				//ɾ��ϵͳ����
				systemappInfo.remove(taskInfo);
			}
			memory+=taskInfo.getRomSize();
		}
		
		//����ת��
		String memorySize = Formatter.formatFileSize(getApplicationContext(), memory);
		Toast.makeText(getApplicationContext(), "������"+deleteList.size()+"�����̣��ͷţ�"+memorySize+"�ڴ�ռ�", 0).show();
		
		//�ı��������еĽ������Լ���ʣ���ڴ�
		processCount= processCount-deleteList.size();
		tv_taskmanager_count.setText("�����н���:\n" + processCount + "��");
		//����ʣ�����ڴ棬�����»�ȡʣ�����ڴ�
		// ����ʣ�����ڴ�,���»�ȡʣ�����ڴ�
				// ��ȡʣ��,���ڴ�'
				long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
				// ����ת��
				String availaRam = Formatter.formatFileSize(getApplicationContext(),availableRam);
				// ��ȡ���ڴ�
				// ���ݲ�ͬ��sdk��ȥ���ò�ͬ�ķ���
				// 1.��ȡ��ǰ��sdk�汾
				int sdk = android.os.Build.VERSION.SDK_INT;
				long totalRam;
				if (sdk >= 16) {
					totalRam = TaskUtil.getTotalRam(getApplicationContext());
				} else {
					totalRam = TaskUtil.getTotalRam();
				}
				// ����ת��
				String totRam = Formatter.formatFileSize(getApplicationContext(),totalRam);
				tv_taskmanager_rom.setText("ʣ��/���ڴ�:\n" + availaRam + "/"+ totRam);

	
		//Ϊ�´����������׼��
		deleteList.clear();
		deleteList=null;
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	//����
	public void set(View v){
		
	}
		
}
