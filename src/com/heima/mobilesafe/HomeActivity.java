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
		//数据源
		int[] imageId = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
				R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
				R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };
		String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理",
				"高级工具", "设置中心" };
		private ImageView iv_itemhome_icon;
		private TextView tv_itemhome_text;
		//设置条目的个数
		@Override
		public int getCount() {
			return 9;
		}
		//设置条目显示的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//将布局文件转化为view对象
			View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
			//每个条目的内容都不一样，初始化控件，去设置控件的值
			//view.findViewByid()是从子布局文件中找控件，findviewByid()是从activity_home中找控件
			iv_itemhome_icon = (ImageView) view.findViewById(R.id.iv_itemhome_icon);
			tv_itemhome_text = (TextView) view.findViewById(R.id.tv_itemhome_text);
			//设置控件的值
			iv_itemhome_icon.setImageResource(imageId[position]);
			tv_itemhome_text.setText(names[position]);
			return view;
		}
		//获取条目对应的数据
		@Override
		public Object getItem(int position) {
			return null;
		}
		//获取条目对应的id
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
	}
}
