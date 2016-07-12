package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private GridView gv_home_gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);
		gv_home_gridview.setAdapter(new myAdapter());
		gv_home_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 8:
					Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
	}
	
	public class myAdapter extends BaseAdapter{
		//����Դ
		int[] imageId = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
				R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
				R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };
		String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��", "�ֻ�ɱ��", "��������",
				"�߼�����", "��������" };
		private ImageView iv_itemhome_icon;
		private TextView tv_itemhome_text;
		//������Ŀ�ĸ���
		@Override
		public int getCount() {
			return 9;
		}
		//������Ŀ��ʾ����ʽ
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//�������ļ�ת��Ϊview����
			View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
			//ÿ����Ŀ�����ݶ���һ������ʼ���ؼ���ȥ���ÿؼ���ֵ
			//view.findViewByid()�Ǵ��Ӳ����ļ����ҿؼ���findviewByid()�Ǵ�activity_home���ҿؼ�
			iv_itemhome_icon = (ImageView) view.findViewById(R.id.iv_itemhome_icon);
			tv_itemhome_text = (TextView) view.findViewById(R.id.tv_itemhome_text);
			//���ÿؼ���ֵ
			iv_itemhome_icon.setImageResource(imageId[position]);
			tv_itemhome_text.setText(names[position]);
			return view;
		}
		//��ȡ��Ŀ��Ӧ������
		@Override
		public Object getItem(int position) {
			return null;
		}
		//��ȡ��Ŀ��Ӧ��id
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
	}
}
