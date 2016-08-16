package com.heima.mobilesafe;

import com.heima.mobilesafe.utils.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView gv_home_gridview;
	private AlertDialog dialog;
	private EditText et_setpassword_password;
	private EditText et_setpassword_confrim;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);
		gv_home_gridview.setAdapter(new myAdapter());
		gv_home_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				
				case 0://手机防盗
					//跳转到手机防盗模块
					//判断用户是否是第一次点击，若是则设置密码，不是第一次点击，则输入密码，密码输入正确才能进入该模块
					//判断用户是否设置过密码
					if (TextUtils.isEmpty(sp.getString("password", ""))) { 
						//密码为空，弹出设置密码的对话框
						showSetPasswordDialog();
					} else {
						//密码不为空，弹出输入密码的对话框
						showEnterPasswordDialog();
					}
					
					break;
				case 1://通讯卫士
					Intent intent1 = new Intent(getApplicationContext(), CallSmsSafeActivity.class);
					startActivity(intent1);
					break;
				case 2://软件管理
					Intent intent2 = new Intent(HomeActivity.this, SoftManagerActivity.class);
					startActivity(intent2);
					break;
				case 3://进程管理
					Intent intent3 = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent3);
					break;
				case 4://流量统计
					Intent intent4 = new Intent(HomeActivity.this, TrafficActivity.class);
					startActivity(intent4);
					break;
				case 5://手机杀毒
					Intent intent5 = new Intent(HomeActivity.this, AntiVirusActivity.class);
					startActivity(intent5);
					break;
				case 6://缓存清理
					Intent intent6 = new Intent(HomeActivity.this, ClearCacheActivity.class);
					startActivity(intent6);
					break;
				case 7://高级工具
					Intent intent7 = new Intent(getApplicationContext(), AToolsActivity.class);
					startActivity(intent7);
					break;
				case 8://设置中心
					Intent intent8 = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent8);
					break;

				default:
					break;
				}
			}
		});
	}
	/**
	 * 输入密码对话框
	 */
	int count =0;
	protected void showEnterPasswordDialog() {

		AlertDialog.Builder builder = new Builder(this);
		//设置对话框不能取消
		builder.setCancelable(false);
		//将布局文件转化成view对象
		View view = View.inflate(getApplicationContext(), R.layout.dialog_enterpassword, null);
		//获取子布局中的控件
		final EditText et_enterpassword_password = (EditText) view.findViewById(R.id.et_enterpassword_password);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		ImageView iv_enterpassword_hide = (ImageView) view.findViewById(R.id.iv_enterpassword_hide);
		iv_enterpassword_hide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 隐藏显示密码
				if (count %2==0) {
					//隐藏密码
					et_enterpassword_password.setInputType(129);//通过代码设置输入框的类型
				} else {
					//显示密码
					et_enterpassword_password.setInputType(0);
				}
				count++;
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1.获取输入密码对话框的密码
				String password = et_enterpassword_password.getText().toString().trim();
				//2.判断密码是否为空
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "输入的密码为空，请重新输入！", 0).show();
					return;
				}
				//3.将密码和sp中保存的密码进行判断
				String sp_password = sp.getString("password", "");
				if (MD5Util.passwordMD5(password).equals(sp_password)) {
					//页面跳转到手机防盗页面
					Intent intent =new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					//隐藏对话框
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "密码输入正确", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "密码输入不正确，请重新输入!", 0).show();
				}
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//隐藏对话框
				dialog.dismiss();
			}
		});
		builder.setView(view);
//		builder.show();
		
		dialog = builder.create();
		dialog.show();
	}
	/**
	 * 设置密码的对话框
	 */
	protected void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//设置对话框不能取消
		builder.setCancelable(false);
		//将布局文件转化成view对象
		View view = View.inflate(getApplicationContext(), R.layout.dialog_setpassword, null);
		et_setpassword_password = (EditText) view.findViewById(R.id.et_setpassword_password);
		et_setpassword_confrim = (EditText) view.findViewById(R.id.et_setpassword_confrim);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		//设置确定和取消按钮的点击事件
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//设置密码
				//1.获取密码输入框输入的内容
				String password = et_setpassword_password.getText().toString().trim();
				//2.判断密码是否为空
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "密码为空，请重新输入！", 0).show();
					return;
				}
				//3.获取确认密码
				String confrim = et_setpassword_confrim.getText().toString().trim();
				//4.判断两次密码是否一致;密码一致就保存到sharedPreference
				if (password.equals(confrim)) {
					Editor edit = sp.edit();
					edit.putString("password", MD5Util.passwordMD5(password));
					edit.commit();
					//取消对话框
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "密码设置成功", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "两次密码设置不一致", 0).show();
				}
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//隐藏对话框
				dialog.dismiss();
			}
		}); 
		builder.setView(view);
//		builder.show();
		
		dialog = builder.create();
		dialog.show();
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
