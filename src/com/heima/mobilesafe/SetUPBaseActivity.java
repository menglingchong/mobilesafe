package com.heima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class SetUPBaseActivity extends Activity {

	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//1.��ȡ����ʶ����
		//Ҫ��������ʶ������Ч�����뽫����ʶ����ע�ᵽ��Ļ�Ĵ����¼���
		gestureDetector = new GestureDetector(getApplicationContext(), new MyOnGestureListener());
	}
	//����Ĵ����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	//���Ƽ�������
	private class MyOnGestureListener extends SimpleOnGestureListener{
		//e1:���µ��¼��������а��µ�����
		//e2��̧���ʱ�䣬������̧�������
		//velocityX��velocityY �ٶȣ�   ��x���Ϻ���y���ϵ�����
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			//�õ����µ�X����
			float startX = e1.getRawX();
			//�õ�̧���X����
			float endX = e2.getRawX();
			//�õ����µ�y����
			float startY = e1.getRawY();
			//�õ�̧���y����
			float endY = e2.getRawY();
			//�ж��Ƿ���б��
			if ((Math.abs(startY-endY)) >50) {
				Toast.makeText(getApplicationContext(), "�ֿ�ʼ�һ��ˣ����ú�ѧϰ����", 0).show();
				return false;
			}
			//��һ��
			if ((startX-endX)>100) {
				next_activity();
			} 
			//��һ��
			if ((endX-startX)>100) {
				pre_activity();
			}
			//true:�¼�ִ�� false�������¼����¼���ִ��
			return true;
		}
		
	}
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
