package com.heima.mobilesafe;

import java.util.HashMap;
import java.util.List;

import com.heima.mobilesafe.engine.ContactEngine;
import com.heima.mobilesafe.utils.MyAsynctask;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactActivity extends Activity {
	//ע���ʼ���ؼ�������spring,ע�����ʽ����javabean���ڲ���ͨ������ķ�ʽִ����findviewById
	@ViewInject(R.id.lv_contact_contacts)
	private ListView lv_contact_contacts;
	private List<HashMap<String, String>> list;
	@ViewInject(R.id.pb_contact_loading)
	private ProgressBar pb_contact_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		ViewUtils.inject(this);
//		pb_contact_loading = (ProgressBar) findViewById(R.id.pb_contact_loading);
		
		//���߳���ִ�к�ʱ����,��ȡ��ϵ��
		//�첽���ؿ��
		new MyAsynctask() {
			
			@Override
			public void preTask() {
				//��������ǰ��ʾprogressbar�������߳�ִ��֮ǰִ�еĲ���
				pb_contact_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void doInBack() {
				//�����߳���ִ�еĲ���
				list = ContactEngine.getAllContactInfo(getApplicationContext());
			}
			@Override
			public void postTask() {
				//�����߳�ִ��֮��ִ�еĲ���
				lv_contact_contacts.setAdapter(new ContactAdapter());//��listview�ؼ�����������
				//����������ɺ����ؽ�����
				pb_contact_loading.setVisibility(View.INVISIBLE);
			}
			
		}.execute();
//		lv_contact_contacts = (ListView) findViewById(R.id.lv_contact_contacts);
		
		//listView����Ŀ�ĵ���¼�
		lv_contact_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.makeText(getApplicationContext(), "��ť������ˣ���", 0).show();
				//���������ϵ�˵ĵ绰���ø���ȫ�������
				Intent intent = new Intent();
				intent.putExtra("phone", list.get(position).get("phone"));
				//�����ݴ��ݸ����ð�ȫ������棬�Ὣ������ݸ���һ��activity
				setResult(RESULT_OK, intent);
				finish();//��ǰ������ʧ��ʱ�򣬻�ص���һ��activity��onActivityResult()����
			}
		});
	}
	
	public class ContactAdapter extends BaseAdapter{
		//��ȡ��Ŀ�ĸ���
		@Override
		public int getCount() {
			return list.size();
		}
		//��ȡ��Ŀ����ʽ
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_contact, null);
			TextView tv_itemcontact_name = (TextView) view.findViewById(R.id.tv_itemcontact_name);
			TextView tv_itemcontact_phone = (TextView) view.findViewById(R.id.tv_itemcontact_phone);
			//���ÿؼ���ֵ
			tv_itemcontact_name.setText(list.get(position).get("name"));
			tv_itemcontact_phone.setText(list.get(position).get("phone"));
			return view;
		}
		//��ȡ��Ŀ��Ӧ������
		@Override
		public Object getItem(int position) {
			return null;
		}
		//��ȡ��Ŀ��Ӧid
		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
	
}
