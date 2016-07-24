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
	//注解初始化控件，类似spring,注解的形式生成javabean，内部：通过反射的方式执行了findviewById
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
		
		//子线程中执行耗时操作,获取联系人
		//异步加载框架
		new MyAsynctask() {
			
			@Override
			public void preTask() {
				//加载数据前显示progressbar，在子线程执行之前执行的操作
				pb_contact_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void doInBack() {
				//在子线程中执行的操作
				list = ContactEngine.getAllContactInfo(getApplicationContext());
			}
			@Override
			public void postTask() {
				//在子线程执行之后执行的操作
				lv_contact_contacts.setAdapter(new ContactAdapter());//给listview控件设置适配器
				//加载数据完成后隐藏进度条
				pb_contact_loading.setVisibility(View.INVISIBLE);
			}
			
		}.execute();
//		lv_contact_contacts = (ListView) findViewById(R.id.lv_contact_contacts);
		
		//listView的条目的点击事件
		lv_contact_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.makeText(getApplicationContext(), "按钮被点击了！！", 0).show();
				//将点击的联系人的电话设置给安全号码界面
				Intent intent = new Intent();
				intent.putExtra("phone", list.get(position).get("phone"));
				//将数据传递给设置安全号码界面，会将结果传递给上一个activity
				setResult(RESULT_OK, intent);
				finish();//当前界面消失的时候，会回调上一个activity的onActivityResult()方法
			}
		});
	}
	
	public class ContactAdapter extends BaseAdapter{
		//获取条目的个数
		@Override
		public int getCount() {
			return list.size();
		}
		//获取条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_contact, null);
			TextView tv_itemcontact_name = (TextView) view.findViewById(R.id.tv_itemcontact_name);
			TextView tv_itemcontact_phone = (TextView) view.findViewById(R.id.tv_itemcontact_phone);
			//设置控件的值
			tv_itemcontact_name.setText(list.get(position).get("name"));
			tv_itemcontact_phone.setText(list.get(position).get("phone"));
			return view;
		}
		//获取条目对应的数据
		@Override
		public Object getItem(int position) {
			return null;
		}
		//获取条目对应id
		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
	
}
