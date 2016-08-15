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
		
		
		//������ת����
		//fromDegrees:��ת��ʼ�ĽǶȣ�toDegrees����ת�����ĽǶȣ� 
		//�����ĸ���������һ����仯�������Ը��ؼ��仯
		//Animation.RELATIVE_TO_SELF, ��������ת��or Animation.RELATIVE_TO_PARENT���Ը��ؼ���ת
		RotateAnimation rotateAnimation = new  RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);//����ʱ��
		rotateAnimation.setRepeatCount(Animation.INFINITE);//INFINITE:һֱ��ת
		
		//���ö�����ֵ�������ɨ��ʱ�������ٵ�Ч��
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		rotateAnimation.setInterpolator(linearInterpolator);
		
		iv_antivirus_scanner.setAnimation(rotateAnimation);
		
		//ɨ�����
		scanner();
	}
	/**
	 * ɨ�����
	 */
	private void scanner() {
		
		list = new ArrayList<String>();
		//1.��ȡ���Ĺ�����
		final PackageManager pm = getPackageManager();
		iv_antivirus_text.setText("���ڳ�ʼ��ɱ�����档����");
		
		new Thread(new Runnable() {
		 
			@Override
			public void run() {
				SystemClock.sleep(100);
				//2.��ȡ���еİ�װӦ�ó�����Ϣ
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES );
				//3.����������
				pb_antivirus_progress.setMax(installedPackages.size());
				//���õ�ǰ����
				int count =0;
				for (final PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					count ++;
					pb_antivirus_progress.setProgress(count);
					
					//4.����ɨ����ʾ�������
					final String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
					//�����߳��и���UI
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							iv_antivirus_text.setText("����ɨ�裺"+appname);
							
							//7.��ȡӦ�ó����ǩ����������MD5����
							//��ȡӦ�ó����ǩ����Ϣ����ȡǩ������
							Signature[] singature= packageInfo.signatures;
							String charsString = singature[0].toCharsString();
							//��ǩ����Ϣ����md5����
							String passwordMD5 = MD5Util.passwordMD5(charsString);
							System.out.println(appname+" : "+passwordMD5);
							//8.��ѯMD5ֵ�Ƿ������ݿ���
							boolean b = AntiVirusDao.queryAntiVirus(getApplicationContext(), passwordMD5);
							//6.չʾɨ������������
							TextView textView = new TextView(getApplicationContext());
							//9.���ݷ��ص�ֵ��������ʶ����
							if (b) {
								//�в���
								textView.setTextColor(Color.RED);
								//�������İ�����ӵ�����
								list.add(packageInfo.packageName);
							} else {
								//û�в���
								textView.setTextColor(Color.BLACK);
							}
							textView.setText(appname);
							textView.setTextSize(15);
							//��textView��ӵ��̲߳�����
//							ll_antivirus_appname.addView(textView);
							ll_antivirus_appname.addView(textView,0);
						}
					});
				}
				//5.ɨ����ɣ���ʾɨ����ɵ���Ϣ��ͬʱ����ֹͣ 
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (list.size()>0) {
							//�в���
							iv_antivirus_text.setText("ɨ����ɣ�û��"+list.size()+"�����ֲ�����");
							//ж�ز���Ӧ��
							AlertDialog.Builder builder = new Builder(AntiVirusActivity.this);
							builder.setTitle("���ѣ�����"+list.size()+"������");
							builder.setIcon(R.drawable.ic_launcher);
							builder.setMessage("�Ƿ�ж�ظ�Ӧ�ã�");
							builder.setPositiveButton("ȷ��", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//ж�ز���
									for (String packagename : list) {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.DELETE");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setData(Uri.parse("package:"+packagename));
										startActivity(intent);
									}
									iv_antivirus_text.setText("����ж����ɣ������޲�����");
								}
							});
							builder.setNegativeButton("ȡ��", null);
							builder.show();
						}else {
							//û�в���
							iv_antivirus_text.setText("ɨ����ɣ�û�з��ֲ�����");
						}
						
						iv_antivirus_scanner.clearAnimation();//�Ƴ�����
					}
				});
			}
		}).start();
	}
}
