package com.heima.mobilesafe;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

public abstract class SetUPBaseActivity extends Activity {

	//��ť����¼��Ĳ���
	//��ÿ�������е���һ������һ����ť��������ȡ��������
	public void next(View v){
		next_activity();
	}
	public void pre(View v){
		pre_activity();
	}
	//��Ϊ�����в�֪��������һ������һ�������ִ�в������룬����Ҫ����һ�����󷽷���������ʵ��������󷽷���
	//�����Լ�������ȥʵ�ֳ��󷽷�
	//��һ���Ĳ���
	public abstract void next_activity();
	//��һ���Ĳ���
	public abstract void pre_activity();
	
	//�ڸ�����ֱ�Ӷ������еķ��ؼ�����ͳһ�Ĵ���
	//�����ֻ�����ť�ĵ���¼�
	//keyCode:����ť�ı�ʶ
	//event�������Ĵ����¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//�ж�keycode�Ƿ��Ƿ��ؼ��ı�ʶ
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//����true������Ļ�������¼� 
//			return true;
			pre_activity();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
