package com.heima.mobilesafe;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 手机防盗界面
 * @author lenovo
 *
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	@ViewInject(R.id.iv_lostfind_lock)
	private ImageView iv_lostfind_lock;
	@ViewInject(R.id.tv_lostfind_safenumber)
	private TextView tv_lostfind_safenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//从sp中获取保存的设置信息
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//分为两部分：1.显示设置过的手机防盗功能，2.设置手机防盗功能
		//判断用户是否是第一次进入手机防盗模式，是，则跳转到设置向导界面，否则显示设置过的手机防盗功能
		if (sp.getBoolean("first", true)) {
			//第一次进入手机防盗模式，跳转到设置向导界面
			Intent intent =new Intent(getApplicationContext(), SetUP1Activity.class);
			startActivity(intent);
			//移除LostFindActivity界面
			finish();
		} else {
			//显示手机防盗界面
			setContentView(R.layout.activity_lostfind);
			ViewUtils.inject(this);
//			iv_lostfind_lock = (ImageView) findViewById(R.id.iv_lostfind_lock);
//			tv_lostfind_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
			//根据保存到安全号码和防盗保护状态进行设置
			//设置安全号码
			tv_lostfind_safenumber.setText(sp.getString("safenumber",""));
			//根据防盗保护状态设置防盗保护图片
			//获取保存的防盗保护状态
			boolean ischecked = sp.getBoolean("isChecked", false);
			if (ischecked) {
				//开启防盗保护
				iv_lostfind_lock.setImageResource(R.drawable.lock);
			}else {
				//关闭防盗保护
				iv_lostfind_lock.setImageResource(R.drawable.unlock);
			}
			
		}
	}
	
	public void resetup(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP1Activity.class);
		startActivity(intent);
		finish();
	}
}
