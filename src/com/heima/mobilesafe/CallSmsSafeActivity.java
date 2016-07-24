package com.heima.mobilesafe;

import java.util.List;

import com.heima.mobilesafe.db.bean.BlackNumInfo;
import com.heima.mobilesafe.db.dao.BlackNumDao;
import com.heima.mobilesafe.utils.MyAsynctask;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CallSmsSafeActivity extends Activity {

	private ListView lv_callsmssafe_contacts;
	private ProgressBar pb_callsmssafe_loading;
	private List<BlackNumInfo> list;
	private BlackNumDao blackNumDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);
		blackNumDao = new BlackNumDao(getApplicationContext());
		lv_callsmssafe_contacts = (ListView) findViewById(R.id.lv_callsmssafe_contacts);
		pb_callsmssafe_loading = (ProgressBar) findViewById(R.id.pb_callsmssafe_loading);
		//���߳���ִ�к�ʱ�����������ݿ��в��Һ�����
		fillData();
	}
	/**
	 * ���߳��в��Һ�����,ʹ���첽���ؿ��
	 */
	private void fillData() {
		
		new MyAsynctask() {

			//���߳�ִ��֮ǰ���ø÷���
			@Override
			public void preTask() {
				pb_callsmssafe_loading.setVisibility(View.VISIBLE);//��ʾprogressbar
			}
			//���߳�ִ��֮����ø÷���
			@Override
			public void postTask() {
				//���ݼ������֮�󣬸���UI�Ĳ���
				lv_callsmssafe_contacts.setAdapter(new MyAdapter());
				pb_callsmssafe_loading.setVisibility(View.INVISIBLE);//���ݼ������֮������progressbar
				
			}
			//���߳�ִ�й����е��ø÷���
			@Override
			public void doInBack() {
				list = blackNumDao.querryAllNum();
				System.out.println();
			}
		}.execute();
	}
	//��������������
	private class MyAdapter extends BaseAdapter{
		private View view;
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
		//��ȡ��Ŀ����ʽ
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//������listview���û���
			if (convertView ==null) {
				view = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
				viewHolder = new ViewHolder();
				//���ؼ��ŵ�������
				viewHolder.tv_itemcallsmssafe_blacknum =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
				viewHolder.tv_itemcallsmssafe_mode =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
				viewHolder.iv_itemcallsmssafe_delete =(ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
				//��������view����󶨵�һ��
				view.setTag(viewHolder);
			}else {
				view =convertView;
				//��view�����еõ��ؼ�������
				viewHolder = (ViewHolder) view.getTag();
			}
			
//			TextView tv_itemcallsmssafe_blacknum = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
//			TextView tv_itemcallsmssafe_mode = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
//			ImageView iv_itemcallsmssafe_delete = (ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
			//������ʾ����
			BlackNumInfo blackNumInfo = list.get(position);//��ȡ��Ӧ��Ŀ��bean����
			viewHolder.tv_itemcallsmssafe_blacknum.setText(blackNumInfo.getBlacknum());
//			tv_itemcallsmssafe_mode.setText(blackNumInfo.getMode()+"");
			int mode = blackNumInfo.getMode();
			switch (mode) {
			case BlackNumDao.CALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("�绰����");
				break;
			case BlackNumDao.SMS:
				viewHolder.tv_itemcallsmssafe_mode.setText("��������");
				break;
			case BlackNumDao.ALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("ȫ������");
				break;
			}
			return view;
		}
	}
	/**
	 * ������ſؼ�����
	 * @author lenovo
	 *
	 */
	static class ViewHolder{
		TextView tv_itemcallsmssafe_blacknum,tv_itemcallsmssafe_mode;
		ImageView iv_itemcallsmssafe_delete;
	}
	
}
