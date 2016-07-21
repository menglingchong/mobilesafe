package com.heima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.mobilesafe.db.dao.AddressDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AddressActivity extends Activity {
	//使用注解的方式来找到控件
	@ViewInject(R.id.et_address_queryphone)
	private EditText et_address_queryphone;
	@ViewInject(R.id.tv_address_phoneaddress)
	private TextView tv_address_phoneaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		ViewUtils.inject(this);
//		et_address_queryphone = (EditText) findViewById(R.id.et_address_queryphone);
//		tv_address_phoneaddress = (TextView) findViewById(R.id.tv_address_phoneaddress);
		
		//监听文本的变化
		et_address_queryphone.addTextChangedListener(new TextWatcher() {
			//监听输入框文本的变化
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//1.获取文本输入框的内容
				String phone = s.toString();
				//2.根据号码进行归属地判断
				String location = AddressDao.queryAddress(getApplicationContext(), phone);
				//3.判断归属地是否为空
				if (! TextUtils.isEmpty(location)) {
					//将查询的号码归属地设置给textView显示
					tv_address_phoneaddress.setText(location);
				}
			}
			//当文本变化之前调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//当文本变化之后调用
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	public void query(View v){
		//1.获取输入要查询的号码
		String queryPhone = et_address_queryphone.getText().toString().trim();
		//2.判断号码是否为空
		if (TextUtils.isEmpty(queryPhone)) {
			Toast.makeText(getApplicationContext(), "输入的号码为空，请重新输入！！", 0).show();
			//实现抖动的效果
			 Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			 et_address_queryphone.startAnimation(shake);//开启动画
			 //实现手机震动的效果
			 Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			 vibrator.vibrate(500);
		}else {
			// 3.根据号码查询其归属地
			String location = AddressDao.queryAddress(getApplicationContext(),queryPhone);
			// 4.判断号码的归属地是否为空
			if (!TextUtils.isEmpty(location)) {
				tv_address_phoneaddress.setText(location);
			}
		}
	}
}
