package com.heima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.mobilesafe.bean.AppInfo;
import com.heima.mobilesafe.db.dao.WatchDogDao;
import com.heima.mobilesafe.engine.AppEngine;
import com.heima.mobilesafe.utils.AppUtil;
import com.heima.mobilesafe.utils.DensityUtil;
import com.heima.mobilesafe.utils.MyAsynctask;

public class SoftManagerActivity extends Activity implements OnClickListener{

	private ListView lv_softmanager_appinfo;
	private ProgressBar pb_softmanager_loading;
	private List<AppInfo> list;
	//用户程序集合
	private ArrayList<AppInfo> userappInfo;
	//系统程序集合
	private ArrayList<AppInfo> systemappInfo;
	private View view;
	private TextView tv_softmanager_userorsystem;
	private AppInfo appInfo;
	private PopupWindow popupWindow;
	private MyAdapter myAdapter;
	private TextView tv_softmanager_rom;
	private TextView tv_softmanager_sd;
	private WatchDogDao watchDogDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_softmanager);
		
		watchDogDao = new WatchDogDao(getApplicationContext());
		lv_softmanager_appinfo = (ListView) findViewById(R.id.lv_softmanager_appinfo);
		pb_softmanager_loading = (ProgressBar) findViewById(R.id.pb_softmanager_loading);
		tv_softmanager_userorsystem = (TextView) findViewById(R.id.tv_softmanager_userorsystem);
		tv_softmanager_rom = (TextView) findViewById(R.id.tv_softmanager_rom);
		tv_softmanager_sd = (TextView) findViewById(R.id.tv_softmanager_sd);
		//获取可用内存，获取的都是kb
		long availableRom = AppUtil.getAvailableRom();
		long availableSD = AppUtil.getAvailableSD();
		//数据转化
		String sdsize = Formatter.formatFileSize(getApplicationContext(), availableSD);
		String romsize = Formatter.formatFileSize(getApplicationContext(), availableRom);
		//设置显示
		tv_softmanager_rom.setText("内存可用："+romsize);
		tv_softmanager_sd.setText("SD卡可用："+sdsize);
		//异步加载AppInfo信息数据
		fillData();
		//listView的滑动监听
		listViewOnScroll();
		listenItemOnClick();
		listenItemLognClick();
	}
	/**
	 * 条目的长按点击事件
	 */
	private void listenItemLognClick() {
		lv_softmanager_appinfo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//返回值是true:表示执行操作，false：拦截操作
				//加锁和解锁的操作
				//1.屏蔽用于显示用户程序个数和系统程序个数的view条目
				if (position ==0 || position == userappInfo.size()+1) {
					return true;
				}
				//获取数据
				if (position <= userappInfo.size()) {
					//用户程序
					appInfo =userappInfo.get(position-1);
					
				}else {
					//系统程序
					appInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//加锁解锁
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				//判断应用程序有没有加锁，有解锁，没有加锁
				if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
					//解锁
					watchDogDao.delLockApp(appInfo.getPackageName());//从数据库中删除应用程序的包名
					viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
				}else {
					//加锁操作
					//判断如果是当前应用程序，就不加锁
					if (!appInfo.getPackageName().equals(getPackageName())) {
						
						watchDogDao.addLockApp(appInfo.getPackageName());
						viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
					}else {
						Toast.makeText(getApplicationContext(), "当前应用程序不能加锁！", 0).show();
					}
				}
				//更新适配器
//				myAdapter.notifyDataSetChanged();
				//返回true：执行操作，将事件消费掉，false：拦截操作
				return true;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//隐藏旧的popupWindow
		hidepopupWindow();
	}
	/**
	 * listView条目的点击监听事件
	 */
	private void listenItemOnClick() {
		lv_softmanager_appinfo.setOnItemClickListener(new OnItemClickListener() {
			
			//view:条目的view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//弹出气泡
				//1.屏蔽用于显示用户程序个数和系统程序个数的view对象
				if (position ==0 || position == userappInfo.size()+1) {
					return;
				}
				//2.获取条目对应的应用程序的信息
				//数据要从systemappinfo和userappinfo中获取
				if (position <= userappInfo.size()) {
					//从userappInfo中获取bean对象信息
					appInfo = userappInfo.get(position-1);
				}else {
					//从systemAPPInfo中获取bena对象信息
					appInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//5.弹出气泡前，先要删除旧的气泡
				hidepopupWindow();
				//3.弹出气泡
				//将布局转换成view对象
				View contentView = View.inflate(getApplicationContext(), R.layout.item_popuwindow, null);
				
				//初始化控件
				LinearLayout ll_popupwindow_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_uninstall);
				LinearLayout ll_popupwindow_start = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_start);
				LinearLayout ll_popupwindow_share = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_share);
				LinearLayout ll_popupwindow_detail = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_detail);
				
				//设置控件的点击事件
				ll_popupwindow_uninstall.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_start.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_share.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_detail.setOnClickListener(SoftManagerActivity.this);
				
				//contentView:显示view对象；width,height:view对象的宽高
				popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//动画要想执行，执行的控件必须有背景，动画是基于背景来执行一些计算，没有背景动画无法执行，popupWindow默认没有设置背景
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				//4.获取条目的位置，让气泡显示在相应在条目上
				int location[] =new int[2];//保存x和y的坐标的数组
				view.getLocationInWindow(location);//获取条目x和y的坐标，同时保存到location
				//获取x和y的坐标
				int x = location[0];
				int y = location[1];
				//parent:要挂载到哪个控件上；gravity，x,y：控制popuWindow显示的位置
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP , x+DensityUtil.dip2qx(getApplicationContext(), 50),y);
				
				//6.设置动画
				//缩放动画
				//前四个参数：控制控件由没有到有，动画 0：没有；1：整个控件
				//后四个参数：控制控件按照只剩还是父控件进行变化
				//RELATIVE_TO_SELF:按照自身变化
				//RELATIVE_TO_PARENT:按照父控件变化
				ScaleAnimation scaleAnimation = new ScaleAnimation(0, 0, 1, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(500);
				
				//渐变动画
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);//由半透明变成不透明
				alphaAnimation.setDuration(500);
				
				//组合动画
				//shareInterpolator:是否使用相同的动画插值器		true:共享	false：不共享，各自使用各自的
				AnimationSet animationSet = new AnimationSet(true);
				animationSet.addAnimation(scaleAnimation);
				animationSet.addAnimation(alphaAnimation);
				//执行动画
				contentView.startAnimation(animationSet);
			}

		});
	}
	/**
	 * 隐藏popupWindow
	 */
	private void hidepopupWindow() {
		if (popupWindow != null) {
			popupWindow.dismiss();//隐藏popuWindow
			popupWindow = null;
		}
	}
	/**
	 * listView控件的滑动监听
	 */
	private void listViewOnScroll() {
		lv_softmanager_appinfo.setOnScrollListener(new OnScrollListener() {
			//滑动状态改变的时候调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			//滑动的时候调用
			//view:listView; firstVisibleItem:界面第一个显示的条目；	visibleItemCount:显示条目的总个数；totalItemCount:总共的条目个数
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//滑动的时候隐藏popupWindow
				hidepopupWindow();
				//listView在初始化的时候就会调用onScroll()方法,userappInfo和systemappInfo还未加载。所以先要进行不为空的判断
				if (userappInfo!= null && systemappInfo != null) {
					if (firstVisibleItem >= userappInfo.size()+1) {
						tv_softmanager_userorsystem.setText("系统程序("+systemappInfo.size()+")");
					}else {
						tv_softmanager_userorsystem.setText("手机程序("+userappInfo.size()+")");
					}
				}
			}
		});
	}
	/**
	 * 异步加载数据
	 */
	private void fillData() {
		new MyAsynctask() {
			@Override
			public void preTask() {
				//显示Progressbar
				pb_softmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//将数据显示到界面
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_softmanager_appinfo.setAdapter(myAdapter);
				}else {
					//更新适配器数据
					myAdapter.notifyDataSetChanged();
				}
				
				//隐藏progressbar
				pb_softmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//加载数据
				list = AppEngine.getAppInfos(getApplicationContext());
				//将用户程序和系统程序区分
				userappInfo = new ArrayList<AppInfo>();
				systemappInfo = new ArrayList<AppInfo>();
				for (AppInfo appinfo : list) {
					//将数据分别存放到用户程序集合和系统程序集合中
					if (appinfo.isUser()) {
						userappInfo.add(appinfo);
					}else {
						systemappInfo.add(appinfo);
					}
				}
			}
		}.execute();
	}

	//创建Adapter类
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
			//增加条目
			if (position ==0) {
				//添加用户程序(...个)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("用户程序("+userappInfo.size()+")");
				return textView;
			}else if (position == userappInfo.size()+1) {
				//添加系统程序(...个)textView
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("系统程序("+systemappInfo.size()+")");
				return textView;
			}
			//完整的listview复用缓存
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				viewHolder.iv_itemsoftmanager_islock = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_islock);
				//将viewholer和view对象绑定
				view.setTag(viewHolder);
			}
			//这种方式当滑动的时候，会出现条目错乱，这是因为我们添加了另外的两个textView条目造成view条目的混乱!
			/*if (convertView == null) {
				//将布局文件转化为View对象
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				//将控件放到控件容器中
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				//将viewHolder和view对象绑定
				view.setTag(viewHolder);
			}else{//复用的时候判断view的类型
				if (convertView instanceof RelativeLayout ) {
					view =convertView;
					//从view对象中获取viewHolder，即控件容器
					viewHolder = (ViewHolder) view.getTag();
				}
			
			}*/
			//获取相应条目的bean对象
//			AppInfo appInfo = list.get(position);
			
			//因为数据分别存在系统程序集合和用户程序集合中，所以相应条目bena对象要到不同的集合中去获取 
			if (position <= userappInfo.size()) {
				//用户程序
				appInfo = userappInfo.get(position -1);
			}else {
				//系统程序
				appInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
			//设置显示数据
			viewHolder.iv_itemsoftmanager_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_itemsoftmanager_name.setText(appInfo.getName());
			viewHolder.tv_itemsoftmanager_version.setText(appInfo.getVersionName());
			boolean sd = appInfo.isSD();
			if (sd) {
				//安装在sd卡
				viewHolder.tv_itemsoftmanager_issd.setText("SD卡存储");
			}else {
				//安装在手机
				viewHolder.tv_itemsoftmanager_issd.setText("手机存储");
			}
			
			//判断应用程序是加锁还是解锁
			if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
				//加锁
				viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
			}else {
				//解锁
				viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
			}
			
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemsoftmanager_icon,iv_itemsoftmanager_islock;
		TextView tv_itemsoftmanager_name,tv_itemsoftmanager_issd,tv_itemsoftmanager_version;
	}
	
	//popupWindow控件的点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popupwindow_uninstall:
			System.out.println("卸载");
			uninstall();
			break;
		case R.id.ll_popupwindow_start:
			System.out.println("开启");
			startSoft();
			break;
		case R.id.ll_popupwindow_share:
			System.out.println("分享");
			shareSoft();
			break;
		case R.id.ll_popupwindow_detail:
			System.out.println("详情");
			detail();
			break;

		}
	}
	/**
	 * 软件分享
	 */
	private void shareSoft() {
		/**
		 * 通过log日志查看跳转信息
		 *  Intent 
			{ 
				act=android.intent.action.SEND  :action
				typ=text/plain 					:type		
				flg=0x3000000 					:flag
				cmp=com.android.mms/.ui.ComposeMessageActivity (has extras)   intent中包含信息
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "发现一个很牛逼的软件，"+appInfo.getName()+",请自行下载！！");
		startActivity(intent);
		/*Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "发现一个很牛x软件"+appInfo.getName()+",下载地址:www.baidu.com,自己去搜");
		startActivity(intent);*/
	}

	/**
	 * 软件详情
	 */
	private void detail() {
		/**
		 * 通过系统的log日志查看跳转的信息
		 *  Intent 
			{ 
			act=android.settings.APPLICATION_DETAILS_SETTINGS    action
			dat=package:com.example.android.apis   data
			cmp=com.android.settings/.applications.InstalledAppDetails  开启的activity
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
		startActivity(intent);
	}

	/**
	 * 开启软件
	 */
	private void startSoft() {
		PackageManager pm = getPackageManager();
		//获取应用程序的启动意图
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "系统核心程序，无法启动",0).show();
		}
	}

	/**
	 * 卸载软件
	 */
	private void uninstall() {
		//通过隐式意图去实现卸载软件的操作
		/**
		 *  <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
		 */
		//判断是否是系统核心程序，若是则不能卸载
		if (appInfo.isUser()) {
			//判断是否是自己的程序，若是就不卸载
			if (!appInfo.getPackageName().equals(getPackageName())) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
				startActivityForResult(intent, 0);
			}else {
				Toast.makeText(getApplicationContext(), "自己的程序，不能随便卸载", 0).show();
			}
		}else {
			Toast.makeText(getApplicationContext(), "系统核心程序，不能卸载！", 0).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			fillData();//加载数据
			break;
		}
	}
}
