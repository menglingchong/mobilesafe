package com.heima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.heima.mobilesafe.db.dao.AntiVirusDao;
import com.heima.mobilesafe.utils.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	private ImageView iv_antivirus_scanner;
	private TextView iv_antivirus_text;
	private ProgressBar pb_antivirus_progress;
	private LinearLayout ll_antivirus_appname;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		iv_antivirus_scanner = (ImageView) findViewById(R.id.iv_antivirus_scanner);
		iv_antivirus_text = (TextView) findViewById(R.id.iv_antivirus_text);
		pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
		ll_antivirus_appname = (LinearLayout) findViewById(R.id.ll_antivirus_appname);
		
		
		//设置旋转动画
		//fromDegrees:旋转开始的角度；toDegrees：旋转结束的角度； 
		//后面四个参数：是一自身变化，还是以父控件变化
		//Animation.RELATIVE_TO_SELF, 以自身旋转；or Animation.RELATIVE_TO_PARENT：以父控件旋转
		RotateAnimation rotateAnimation = new  RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);//持续时间
		rotateAnimation.setRepeatCount(Animation.INFINITE);//INFINITE:一直旋转
		
		//设置动画插值器，解决扫描时动画卡顿的效果
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		rotateAnimation.setInterpolator(linearInterpolator);
		
		iv_antivirus_scanner.setAnimation(rotateAnimation);
		
		//扫描程序
		scanner();
	}
	/**
	 * 扫描程序
	 */
	private void scanner() {
		
		list = new ArrayList<String>();
		//1.获取包的管理者
		final PackageManager pm = getPackageManager();
		iv_antivirus_text.setText("正在初始化杀毒引擎。。。");
		
		new Thread(new Runnable() {
		 
			@Override
			public void run() {
				SystemClock.sleep(100);
				//2.获取所有的安装应用程序信息
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES );
				//3.设置最大进度
				pb_antivirus_progress.setMax(installedPackages.size());
				//设置当前进度
				int count =0;
				for (final PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					count ++;
					pb_antivirus_progress.setProgress(count);
					
					//4.设置扫描显示软件名称
					final String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
					//在子线程中更新UI
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							iv_antivirus_text.setText("正在扫描："+appname);
							
							//7.获取应用程序的签名，并进行MD5加密
							//获取应用程序的签名信息，获取签名数组
							Signature[] singature= packageInfo.signatures;
							String charsString = singature[0].toCharsString();
							//对签名信息进行md5加密
							String passwordMD5 = MD5Util.passwordMD5(charsString);
							System.out.println(appname+" : "+passwordMD5);
							//8.查询MD5值是否在数据库中
							boolean b = AntiVirusDao.queryAntiVirus(getApplicationContext(), passwordMD5);
							//6.展示扫描过的软件名称
							TextView textView = new TextView(getApplicationContext());
							//9.根据返回的值将病毒标识出来
							if (b) {
								//有病毒
								textView.setTextColor(Color.RED);
								//将病毒的包名添加到集合
								list.add(packageInfo.packageName);
							} else {
								//没有病毒
								textView.setTextColor(Color.BLACK);
							}
							textView.setText(appname);
							textView.setTextSize(15);
							//将textView添加到线程布局中
//							ll_antivirus_appname.addView(textView);
							ll_antivirus_appname.addView(textView,0);
						}
					});
				}
				//5.扫描完成，显示扫描完成的信息，同时动画停止 
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (list.size()>0) {
							//有病毒
							iv_antivirus_text.setText("扫描完成，没有"+list.size()+"个发现病毒！");
							//卸载病毒应用
							AlertDialog.Builder builder = new Builder(AntiVirusActivity.this);
							builder.setTitle("提醒！发现"+list.size()+"个病毒");
							builder.setIcon(R.drawable.ic_launcher);
							builder.setMessage("是否卸载该应用？");
							builder.setPositiveButton("确定", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//卸载操作
									for (String packagename : list) {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.DELETE");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setData(Uri.parse("package:"+packagename));
										startActivity(intent);
									}
									iv_antivirus_text.setText("病毒卸载完成，现在无病毒！");
								}
							});
							builder.setNegativeButton("取消", null);
							builder.show();
						}else {
							//没有病毒
							iv_antivirus_text.setText("扫描完成，没有发现病毒！");
						}
						
						iv_antivirus_scanner.clearAnimation();//移出动画
					}
				});
			}
		}).start();
	}
}
