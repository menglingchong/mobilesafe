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
 * �ֻ���������
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
		//��sp�л�ȡ�����������Ϣ
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//��Ϊ�����֣�1.��ʾ���ù����ֻ��������ܣ�2.�����ֻ���������
		//�ж��û��Ƿ��ǵ�һ�ν����ֻ�����ģʽ���ǣ�����ת�������򵼽��棬������ʾ���ù����ֻ���������
		if (sp.getBoolean("first", true)) {
			//��һ�ν����ֻ�����ģʽ����ת�������򵼽���
			Intent intent =new Intent(getApplicationContext(), SetUP1Activity.class);
			startActivity(intent);
			//�Ƴ�LostFindActivity����
			finish();
		} else {
			//��ʾ�ֻ���������
			setContentView(R.layout.activity_lostfind);
			ViewUtils.inject(this);
//			iv_lostfind_lock = (ImageView) findViewById(R.id.iv_lostfind_lock);
//			tv_lostfind_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
			//���ݱ��浽��ȫ����ͷ�������״̬��������
			//���ð�ȫ����
			tv_lostfind_safenumber.setText(sp.getString("safenumber",""));
			//���ݷ�������״̬���÷�������ͼƬ
			//��ȡ����ķ�������״̬
			boolean ischecked = sp.getBoolean("isChecked", false);
			if (ischecked) {
				//������������
				iv_lostfind_lock.setImageResource(R.drawable.lock);
			}else {
				//�رշ�������
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
