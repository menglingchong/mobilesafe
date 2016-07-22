package com.heima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class DragViewActivity extends Activity {

	private LinearLayout ll_dragview_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		ll_dragview_location = (LinearLayout) findViewById(R.id.ll_dragview_location);
		setTouch();
	}
	/**
	 * �������Ƶļ���
	 */
	private void setTouch() {
		ll_dragview_location.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction(): ��ȡ����ִ�е��¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://��ָ���µ��¼�
					System.out.println("��ָ�����ˣ�����");
					//��ָ���µ�����
					
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://��ָ�ƶ����¼�
					System.out.println("��ָ�ƶ��ˣ�����");
					//��ָ�ƶ���������
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//�����ƫ����
					int dX= newX -startX;
					int dY= newY -startY;
					//�ƶ���Ӧ��ƫ���������»�������
					//��ȡԭ�ؼ�������ߺͶ����ľ���
					int left = ll_dragview_location.getLeft();
					int top = ll_dragview_location.getTop();
					int l = left+dX;
					int t = top+dY;
					int r = l+ll_dragview_location.getWidth();
					int b = t+ll_dragview_location.getHeight();
					ll_dragview_location.layout(l, t, r, b);//���»��ƿؼ�
					//����������ʼ����
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://��ָ̧����¼�
					System.out.println("��ָ̧���ˣ�����");
					break;
				}
				
				//����false:�¼��¼��������أ��������´��ݣ�����true:�ǽ��¼����Ѽ��¼�����ִ��
				return true;
			}
		});
	}
}
