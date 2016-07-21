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
	//ʹ��ע��ķ�ʽ���ҵ��ؼ�
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
		
		//�����ı��ı仯
		et_address_queryphone.addTextChangedListener(new TextWatcher() {
			//����������ı��ı仯
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//1.��ȡ�ı�����������
				String phone = s.toString();
				//2.���ݺ�����й������ж�
				String location = AddressDao.queryAddress(getApplicationContext(), phone);
				//3.�жϹ������Ƿ�Ϊ��
				if (! TextUtils.isEmpty(location)) {
					//����ѯ�ĺ�����������ø�textView��ʾ
					tv_address_phoneaddress.setText(location);
				}
			}
			//���ı��仯֮ǰ����
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//���ı��仯֮�����
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	public void query(View v){
		//1.��ȡ����Ҫ��ѯ�ĺ���
		String queryPhone = et_address_queryphone.getText().toString().trim();
		//2.�жϺ����Ƿ�Ϊ��
		if (TextUtils.isEmpty(queryPhone)) {
			Toast.makeText(getApplicationContext(), "����ĺ���Ϊ�գ����������룡��", 0).show();
			//ʵ�ֶ�����Ч��
			 Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			 et_address_queryphone.startAnimation(shake);//��������
			 //ʵ���ֻ��𶯵�Ч��
			 Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			 vibrator.vibrate(500);
		}else {
			// 3.���ݺ����ѯ�������
			String location = AddressDao.queryAddress(getApplicationContext(),queryPhone);
			// 4.�жϺ���Ĺ������Ƿ�Ϊ��
			if (!TextUtils.isEmpty(location)) {
				tv_address_phoneaddress.setText(location);
			}
		}
	}
}
