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
		//子线程中执行耗时操作，从数据库中查找黑名单
		fillData();
	}
	/**
	 * 子线程中查找黑名单,使用异步加载框架
	 */
	private void fillData() {
		
		new MyAsynctask() {

			//子线程执行之前调用该方法
			@Override
			public void preTask() {
				pb_callsmssafe_loading.setVisibility(View.VISIBLE);//显示progressbar
			}
			//子线程执行之后调用该方法
			@Override
			public void postTask() {
				//数据加载完成之后，更新UI的操作
				lv_callsmssafe_contacts.setAdapter(new MyAdapter());
				pb_callsmssafe_loading.setVisibility(View.INVISIBLE);//数据加载完成之后隐藏progressbar
				
			}
			//子线程执行过程中调用该方法
			@Override
			public void doInBack() {
				list = blackNumDao.querryAllNum();
				System.out.println();
			}
		}.execute();
	}
	//创建适配器的类
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
		//获取条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//完整的listview复用缓存
			if (convertView ==null) {
				view = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
				viewHolder = new ViewHolder();
				//将控件放到容器中
				viewHolder.tv_itemcallsmssafe_blacknum =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
				viewHolder.tv_itemcallsmssafe_mode =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
				viewHolder.iv_itemcallsmssafe_delete =(ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
				//将容器和view对象绑定到一起
				view.setTag(viewHolder);
			}else {
				view =convertView;
				//从view对象中得到控件的容器
				viewHolder = (ViewHolder) view.getTag();
			}
			
//			TextView tv_itemcallsmssafe_blacknum = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
//			TextView tv_itemcallsmssafe_mode = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
//			ImageView iv_itemcallsmssafe_delete = (ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
			//设置显示数据
			BlackNumInfo blackNumInfo = list.get(position);//获取相应条目的bean对象
			viewHolder.tv_itemcallsmssafe_blacknum.setText(blackNumInfo.getBlacknum());
//			tv_itemcallsmssafe_mode.setText(blackNumInfo.getMode()+"");
			int mode = blackNumInfo.getMode();
			switch (mode) {
			case BlackNumDao.CALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("电话拦截");
				break;
			case BlackNumDao.SMS:
				viewHolder.tv_itemcallsmssafe_mode.setText("短信拦截");
				break;
			case BlackNumDao.ALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("全部拦截");
				break;
			}
			return view;
		}
	}
	/**
	 * 创建存放控件的类
	 * @author lenovo
	 *
	 */
	static class ViewHolder{
		TextView tv_itemcallsmssafe_blacknum,tv_itemcallsmssafe_mode;
		ImageView iv_itemcallsmssafe_delete;
	}
	
}
