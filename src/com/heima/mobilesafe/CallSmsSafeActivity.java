package com.heima.mobilesafe;

import java.util.List;

import com.heima.mobilesafe.bean.BlackNumInfo;
import com.heima.mobilesafe.db.dao.BlackNumDao;
import com.heima.mobilesafe.utils.MyAsynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {

	private ListView lv_callsmssafe_contacts;
	private ProgressBar pb_callsmssafe_loading;
	private List<BlackNumInfo> list;
	private BlackNumDao blackNumDao;
	private MyAdapter myAdapter;
	private AlertDialog dialog;
	private final int MaxNum = 20;//查询的总个数
	private int startindex =0;//查询的起始位置
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);
		blackNumDao = new BlackNumDao(getApplicationContext());
		lv_callsmssafe_contacts = (ListView) findViewById(R.id.lv_callsmssafe_contacts);
		pb_callsmssafe_loading = (ProgressBar) findViewById(R.id.pb_callsmssafe_loading);
		//子线程中执行耗时操作，从数据库中查找黑名单
		fillData();
		//listView的滑动监听事件
		
		lv_callsmssafe_contacts.setOnScrollListener(new OnScrollListener() {
			//滑动状态改变时调用该方法
			//scrollState:滑动状态
			//SCROLL_STATE_IDLE:滑动处于空闲状态
			//SCROLL_STATE_FLING:处于快速滑动
			//SCROLL_STATE_TOUCH_SCROLL:缓慢滑动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//当listView静止的时候判断listview是否是查询数据的最后一个条目，是就加载下一波数据，不是的话，用户进行其他的操作
				if (scrollState==OnScrollListener.SCROLL_STATE_IDLE) {
					//获取界面显示最后一个条目
					int position = lv_callsmssafe_contacts.getLastVisiblePosition();//获取界面显示最后一个条目，返回条目的位置 
					//判断界面显示的最后一个条目是否是最后一个数据
					if (position == list.size()-1) {
						//加载下一波数据
						startindex+= MaxNum;//将起始的查询位置进行更新
						fillData();
					}
				}
			}
			//滑动时调用该方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
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
				
				if (myAdapter==null) { //第一次加载的时候给listview设置adapter
					myAdapter = new MyAdapter();
					lv_callsmssafe_contacts.setAdapter(myAdapter);
				} else {
					myAdapter.notifyDataSetChanged();//不是第一次加载，直接更新适配器
				}
				
				pb_callsmssafe_loading.setVisibility(View.INVISIBLE);//数据加载完成之后隐藏progressbar
				
			}
			//子线程执行过程中调用该方法
			@Override
			public void doInBack() {
				if (list ==null) {
					list = blackNumDao.querryPartNum(MaxNum, startindex);
				}else {
					list.addAll(blackNumDao.querryPartNum(MaxNum, startindex));//将一个集合添加到另一个集合中
				}
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
		public View getView(final int position, View convertView, ViewGroup parent) {
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
			final BlackNumInfo blackNumInfo = list.get(position);//获取相应条目的bean对象
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
			/**
			 * 删除黑名单
			 */
			viewHolder.iv_itemcallsmssafe_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//删除黑名单操作
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("删除");
					builder.setMessage("你确定要删除:"+blackNumInfo.getBlacknum()+"吗?");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除黑名单，先从数据库中删除，然后再删除list集合中的界面上的条目
							//1.删除数据库中的黑名单
							blackNumDao.delBlackNum(blackNumInfo.getBlacknum());
							//2.删除list集合中的黑名单，删除条目对应位置的黑名单
							list.remove(position);
							//3.更新界面，即适配器的更新
							myAdapter.notifyDataSetChanged();
							//4.隐藏对话框
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
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
	/**
	 * 添加黑名单
	 */
	public void addBlackNum(View v){
		AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
		View view = View.inflate(getApplicationContext(), R.layout.dialog_addblacknum, null);
		//初始化控件
		Button btn_addblacknum_cancle = (Button) view.findViewById(R.id.btn_addblacknum_cancle);
		Button btn_addblacknum_ok = (Button) view.findViewById(R.id.btn_addblacknum_ok);
		final TextView et_addblacknum_num = (TextView) view.findViewById(R.id.et_addblacknum_num);
		final RadioGroup rg_addblacknum_check = (RadioGroup) view.findViewById(R.id.rg_addblacknum_check);
		btn_addblacknum_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加黑名单
				//1.获取输入的黑名单号码
				String number = et_addblacknum_num.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "黑名单号码为空，请重新输入！", 0).show();
					return;
				}
				//2.获取黑名单的拦截模式
				int mode =-1;
				int radioButtonId = rg_addblacknum_check.getCheckedRadioButtonId();//获取RadioGroup的选择的buttonId
				switch (radioButtonId) {
				case R.id.rb_addblacknum_call://将黑名单的拦截模式设置为电话拦截
					mode = BlackNumDao.CALL;
					break;
				case  R.id.rb_addblacknum_sms://将黑名单的拦截模式设置为短信拦截
					mode = BlackNumDao.SMS;
					break;
				case R.id.rb_addblacknum_all://将黑名单的拦截模式设置为全部拦截
					mode = BlackNumDao.ALL;
					break;
				}
				//3.将黑名单号码和拦截模式保存
				//3.1.保存到数据库
				blackNumDao.addBlackNum(number, mode);
				//3.2保存到list集合中并显示在界面
//				list.add(new BlackNumInfo(number, mode));//添加到界面的最下端，体验不好
				list.add(0, new BlackNumInfo(number, mode));//添加到界面的最上面
				//4.更新界面
				myAdapter.notifyDataSetChanged();
				//隐藏对话框
				dialog.dismiss();
			}
		});
		btn_addblacknum_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//隐藏对话框
				dialog.dismiss();
			}
		});
		
		builder.setView(view);
		
        dialog = builder.create();
		dialog.show();
	}
	
}
