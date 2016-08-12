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
	//�û����򼯺�
	private ArrayList<AppInfo> userappInfo;
	//ϵͳ���򼯺�
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
		//��ȡ�����ڴ棬��ȡ�Ķ���kb
		long availableRom = AppUtil.getAvailableRom();
		long availableSD = AppUtil.getAvailableSD();
		//����ת��
		String sdsize = Formatter.formatFileSize(getApplicationContext(), availableSD);
		String romsize = Formatter.formatFileSize(getApplicationContext(), availableRom);
		//������ʾ
		tv_softmanager_rom.setText("�ڴ���ã�"+romsize);
		tv_softmanager_sd.setText("SD�����ã�"+sdsize);
		//�첽����AppInfo��Ϣ����
		fillData();
		//listView�Ļ�������
		listViewOnScroll();
		listenItemOnClick();
		listenItemLognClick();
	}
	/**
	 * ��Ŀ�ĳ�������¼�
	 */
	private void listenItemLognClick() {
		lv_softmanager_appinfo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//����ֵ��true:��ʾִ�в�����false�����ز���
				//�����ͽ����Ĳ���
				//1.����������ʾ�û����������ϵͳ���������view��Ŀ
				if (position ==0 || position == userappInfo.size()+1) {
					return true;
				}
				//��ȡ����
				if (position <= userappInfo.size()) {
					//�û�����
					appInfo =userappInfo.get(position-1);
					
				}else {
					//ϵͳ����
					appInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//��������
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				//�ж�Ӧ�ó�����û�м������н�����û�м���
				if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
					//����
					watchDogDao.delLockApp(appInfo.getPackageName());//�����ݿ���ɾ��Ӧ�ó���İ���
					viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
				}else {
					//��������
					//�ж�����ǵ�ǰӦ�ó��򣬾Ͳ�����
					if (!appInfo.getPackageName().equals(getPackageName())) {
						
						watchDogDao.addLockApp(appInfo.getPackageName());
						viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
					}else {
						Toast.makeText(getApplicationContext(), "��ǰӦ�ó����ܼ�����", 0).show();
					}
				}
				//����������
//				myAdapter.notifyDataSetChanged();
				//����true��ִ�в��������¼����ѵ���false�����ز���
				return true;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//���ؾɵ�popupWindow
		hidepopupWindow();
	}
	/**
	 * listView��Ŀ�ĵ�������¼�
	 */
	private void listenItemOnClick() {
		lv_softmanager_appinfo.setOnItemClickListener(new OnItemClickListener() {
			
			//view:��Ŀ��view����
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��������
				//1.����������ʾ�û����������ϵͳ���������view����
				if (position ==0 || position == userappInfo.size()+1) {
					return;
				}
				//2.��ȡ��Ŀ��Ӧ��Ӧ�ó������Ϣ
				//����Ҫ��systemappinfo��userappinfo�л�ȡ
				if (position <= userappInfo.size()) {
					//��userappInfo�л�ȡbean������Ϣ
					appInfo = userappInfo.get(position-1);
				}else {
					//��systemAPPInfo�л�ȡbena������Ϣ
					appInfo = systemappInfo.get(position-userappInfo.size()-2);
				}
				//5.��������ǰ����Ҫɾ���ɵ�����
				hidepopupWindow();
				//3.��������
				//������ת����view����
				View contentView = View.inflate(getApplicationContext(), R.layout.item_popuwindow, null);
				
				//��ʼ���ؼ�
				LinearLayout ll_popupwindow_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_uninstall);
				LinearLayout ll_popupwindow_start = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_start);
				LinearLayout ll_popupwindow_share = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_share);
				LinearLayout ll_popupwindow_detail = (LinearLayout) contentView.findViewById(R.id.ll_popupwindow_detail);
				
				//���ÿؼ��ĵ���¼�
				ll_popupwindow_uninstall.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_start.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_share.setOnClickListener(SoftManagerActivity.this);
				ll_popupwindow_detail.setOnClickListener(SoftManagerActivity.this);
				
				//contentView:��ʾview����width,height:view����Ŀ��
				popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//����Ҫ��ִ�У�ִ�еĿؼ������б����������ǻ��ڱ�����ִ��һЩ���㣬û�б��������޷�ִ�У�popupWindowĬ��û�����ñ���
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				//4.��ȡ��Ŀ��λ�ã���������ʾ����Ӧ����Ŀ��
				int location[] =new int[2];//����x��y�����������
				view.getLocationInWindow(location);//��ȡ��Ŀx��y�����꣬ͬʱ���浽location
				//��ȡx��y������
				int x = location[0];
				int y = location[1];
				//parent:Ҫ���ص��ĸ��ؼ��ϣ�gravity��x,y������popuWindow��ʾ��λ��
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP , x+DensityUtil.dip2qx(getApplicationContext(), 50),y);
				
				//6.���ö���
				//���Ŷ���
				//ǰ�ĸ����������ƿؼ���û�е��У����� 0��û�У�1�������ؼ�
				//���ĸ����������ƿؼ�����ֻʣ���Ǹ��ؼ����б仯
				//RELATIVE_TO_SELF:��������仯
				//RELATIVE_TO_PARENT:���ո��ؼ��仯
				ScaleAnimation scaleAnimation = new ScaleAnimation(0, 0, 1, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(500);
				
				//���䶯��
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);//�ɰ�͸����ɲ�͸��
				alphaAnimation.setDuration(500);
				
				//��϶���
				//shareInterpolator:�Ƿ�ʹ����ͬ�Ķ�����ֵ��		true:����	false������������ʹ�ø��Ե�
				AnimationSet animationSet = new AnimationSet(true);
				animationSet.addAnimation(scaleAnimation);
				animationSet.addAnimation(alphaAnimation);
				//ִ�ж���
				contentView.startAnimation(animationSet);
			}

		});
	}
	/**
	 * ����popupWindow
	 */
	private void hidepopupWindow() {
		if (popupWindow != null) {
			popupWindow.dismiss();//����popuWindow
			popupWindow = null;
		}
	}
	/**
	 * listView�ؼ��Ļ�������
	 */
	private void listViewOnScroll() {
		lv_softmanager_appinfo.setOnScrollListener(new OnScrollListener() {
			//����״̬�ı��ʱ�����
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			//������ʱ�����
			//view:listView; firstVisibleItem:�����һ����ʾ����Ŀ��	visibleItemCount:��ʾ��Ŀ���ܸ�����totalItemCount:�ܹ�����Ŀ����
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//������ʱ������popupWindow
				hidepopupWindow();
				//listView�ڳ�ʼ����ʱ��ͻ����onScroll()����,userappInfo��systemappInfo��δ���ء�������Ҫ���в�Ϊ�յ��ж�
				if (userappInfo!= null && systemappInfo != null) {
					if (firstVisibleItem >= userappInfo.size()+1) {
						tv_softmanager_userorsystem.setText("ϵͳ����("+systemappInfo.size()+")");
					}else {
						tv_softmanager_userorsystem.setText("�ֻ�����("+userappInfo.size()+")");
					}
				}
			}
		});
	}
	/**
	 * �첽��������
	 */
	private void fillData() {
		new MyAsynctask() {
			@Override
			public void preTask() {
				//��ʾProgressbar
				pb_softmanager_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				//��������ʾ������
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_softmanager_appinfo.setAdapter(myAdapter);
				}else {
					//��������������
					myAdapter.notifyDataSetChanged();
				}
				
				//����progressbar
				pb_softmanager_loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doInBack() {
				//��������
				list = AppEngine.getAppInfos(getApplicationContext());
				//���û������ϵͳ��������
				userappInfo = new ArrayList<AppInfo>();
				systemappInfo = new ArrayList<AppInfo>();
				for (AppInfo appinfo : list) {
					//�����ݷֱ��ŵ��û����򼯺Ϻ�ϵͳ���򼯺���
					if (appinfo.isUser()) {
						userappInfo.add(appinfo);
					}else {
						systemappInfo.add(appinfo);
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
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				viewHolder.iv_itemsoftmanager_islock = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_islock);
				//��viewholer��view�����
				view.setTag(viewHolder);
			}
			//���ַ�ʽ��������ʱ�򣬻������Ŀ���ң�������Ϊ������������������textView��Ŀ���view��Ŀ�Ļ���!
			/*if (convertView == null) {
				//�������ļ�ת��ΪView����
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				viewHolder = new ViewHolder();
				//���ؼ��ŵ��ؼ�������
				viewHolder.iv_itemsoftmanager_icon = (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				viewHolder.tv_itemsoftmanager_name = (TextView) view.findViewById(R.id.tv_itemsoftmanager_name);
				viewHolder.tv_itemsoftmanager_issd = (TextView) view.findViewById(R.id.tv_itemsoftmanager_issd);
				viewHolder.tv_itemsoftmanager_version = (TextView) view.findViewById(R.id.tv_itemsoftmanager_version);
				//��viewHolder��view�����
				view.setTag(viewHolder);
			}else{//���õ�ʱ���ж�view������
				if (convertView instanceof RelativeLayout ) {
					view =convertView;
					//��view�����л�ȡviewHolder�����ؼ�����
					viewHolder = (ViewHolder) view.getTag();
				}
			
			}*/
			//��ȡ��Ӧ��Ŀ��bean����
//			AppInfo appInfo = list.get(position);
			
			//��Ϊ���ݷֱ����ϵͳ���򼯺Ϻ��û����򼯺��У�������Ӧ��Ŀbena����Ҫ����ͬ�ļ�����ȥ��ȡ 
			if (position <= userappInfo.size()) {
				//�û�����
				appInfo = userappInfo.get(position -1);
			}else {
				//ϵͳ����
				appInfo = systemappInfo.get(position-userappInfo.size()-2);
			}
			//������ʾ����
			viewHolder.iv_itemsoftmanager_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_itemsoftmanager_name.setText(appInfo.getName());
			viewHolder.tv_itemsoftmanager_version.setText(appInfo.getVersionName());
			boolean sd = appInfo.isSD();
			if (sd) {
				//��װ��sd��
				viewHolder.tv_itemsoftmanager_issd.setText("SD���洢");
			}else {
				//��װ���ֻ�
				viewHolder.tv_itemsoftmanager_issd.setText("�ֻ��洢");
			}
			
			//�ж�Ӧ�ó����Ǽ������ǽ���
			if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
				//����
				viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
			}else {
				//����
				viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
			}
			
			return view;
		}
	}
	
	static class ViewHolder{
		ImageView iv_itemsoftmanager_icon,iv_itemsoftmanager_islock;
		TextView tv_itemsoftmanager_name,tv_itemsoftmanager_issd,tv_itemsoftmanager_version;
	}
	
	//popupWindow�ؼ��ĵ���¼�
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popupwindow_uninstall:
			System.out.println("ж��");
			uninstall();
			break;
		case R.id.ll_popupwindow_start:
			System.out.println("����");
			startSoft();
			break;
		case R.id.ll_popupwindow_share:
			System.out.println("����");
			shareSoft();
			break;
		case R.id.ll_popupwindow_detail:
			System.out.println("����");
			detail();
			break;

		}
	}
	/**
	 * �������
	 */
	private void shareSoft() {
		/**
		 * ͨ��log��־�鿴��ת��Ϣ
		 *  Intent 
			{ 
				act=android.intent.action.SEND  :action
				typ=text/plain 					:type		
				flg=0x3000000 					:flag
				cmp=com.android.mms/.ui.ComposeMessageActivity (has extras)   intent�а�����Ϣ
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "����һ����ţ�Ƶ������"+appInfo.getName()+",���������أ���");
		startActivity(intent);
		/*Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "����һ����ţx���"+appInfo.getName()+",���ص�ַ:www.baidu.com,�Լ�ȥ��");
		startActivity(intent);*/
	}

	/**
	 * �������
	 */
	private void detail() {
		/**
		 * ͨ��ϵͳ��log��־�鿴��ת����Ϣ
		 *  Intent 
			{ 
			act=android.settings.APPLICATION_DETAILS_SETTINGS    action
			dat=package:com.example.android.apis   data
			cmp=com.android.settings/.applications.InstalledAppDetails  ������activity
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
		startActivity(intent);
	}

	/**
	 * �������
	 */
	private void startSoft() {
		PackageManager pm = getPackageManager();
		//��ȡӦ�ó����������ͼ
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "ϵͳ���ĳ����޷�����",0).show();
		}
	}

	/**
	 * ж�����
	 */
	private void uninstall() {
		//ͨ����ʽ��ͼȥʵ��ж������Ĳ���
		/**
		 *  <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
		 */
		//�ж��Ƿ���ϵͳ���ĳ�����������ж��
		if (appInfo.isUser()) {
			//�ж��Ƿ����Լ��ĳ������ǾͲ�ж��
			if (!appInfo.getPackageName().equals(getPackageName())) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
				startActivityForResult(intent, 0);
			}else {
				Toast.makeText(getApplicationContext(), "�Լ��ĳ��򣬲������ж��", 0).show();
			}
		}else {
			Toast.makeText(getApplicationContext(), "ϵͳ���ĳ��򣬲���ж�أ�", 0).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			fillData();//��������
			break;
		}
	}
}
