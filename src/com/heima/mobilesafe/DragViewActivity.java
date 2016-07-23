package com.heima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragViewActivity extends Activity {

	private LinearLayout ll_dragview_location;
	private SharedPreferences sp;
	private int screenWidth;
	private int screenHeight;
	private TextView tv_dragview_bottom;
	private TextView tv_dragview_top;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		sp =getSharedPreferences("config", MODE_PRIVATE);
		ll_dragview_location = (LinearLayout) findViewById(R.id.ll_dragview_location);
		tv_dragview_bottom = (TextView) findViewById(R.id.tv_dragview_bottom);
		tv_dragview_top = (TextView) findViewById(R.id.tv_dragview_top);
		//��ȡ��Ļ�Ĵ�С
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		int width = windowManager.getDefaultDisplay().getWidth();//���ڵķ���
		DisplayMetrics displayMetrics = new DisplayMetrics();//����һ�Ű�ֽ
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);//����ֽ���ÿ��
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		
		//���ÿؼ��Ļ��Բ���
		//1.��ȡ���������
		int x = sp.getInt("x", 0);
		int y = sp.getInt("y", 0);
		System.out.println("x:"+x+"	y:"+y);
		 
		//��ʼ���ؼ�֮ǰ�������ÿؼ�������
		//��ȡ���ؼ������Թ��򣬸��ؼ���layoutparams
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) ll_dragview_location.getLayoutParams();
		//������Ӧ������
		layoutParams.leftMargin=x;
		layoutParams.topMargin=y;
		//���ؼ���������
		ll_dragview_location.setLayoutParams(layoutParams);
		
		if (y >= screenHeight/2) {
			//�����·�����ʾ�Ϸ�
			tv_dragview_bottom.setVisibility(View.INVISIBLE);
			tv_dragview_top.setVisibility(View.VISIBLE);
		} else if (y <screenHeight/2) {
			//�����Ϸ�����ʾ�·�
			tv_dragview_bottom.setVisibility(View.VISIBLE);
			tv_dragview_top.setVisibility(View.INVISIBLE);
		}
		setTouch();
		setDoubleClick();
	}
	
	long [] mHits = new long[2];

	/**
	 * ˫������
	 */
	private void setDoubleClick() {
		ll_dragview_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				System.out.println("��˫���ˣ���");
				/**
				 *  src the source array to copy the content.   ������ԭ����
					srcPos the starting index of the content in src.  �Ǵ�Դ�����Ǹ�λ�ÿ�ʼ����
					dst the destination array to copy the data into.  ������Ŀ������
					dstPos the starting index for the copied content in dst.  �Ǵ�Ŀ�������Ǹ�λ�ÿ�ʼȥд
					length the number of elements to be copied.   �����ĳ���
				 */
				//��������Ĳ���
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				//�ж��뿪����ʱ�䣬���ø�����ĵڶ���Ԫ�أ�
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				//�ж��Ƿ��Ƕ������
				if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
					System.out.println("˫����");
					//˫������
					int l =(screenWidth-ll_dragview_location.getWidth())/2;
					int t =(screenHeight-ll_dragview_location.getHeight())/2;
					ll_dragview_location.layout(l, t, l+ll_dragview_location.getWidth(), t+ll_dragview_location.getHeight());
					
					//����ؼ�������
					Editor edit = sp.edit();
					edit.putInt("x", l);
					edit.putInt("y", t);
					edit.commit();
				}
			}
		});
		
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
					//�ڿؼ��Ļ���֮ǰҪ�ж�ltrb�Ƿ񳬳���Ļ�����������Ļ�Ͳ����л���
					if (l<0 || r>screenWidth ||t<0 || b>screenHeight-30) {
						break;
					}
					ll_dragview_location.layout(l, t, r, b);//���»��ƿؼ�
					//�ж�TextView����ʾ������
					int topScreen = ll_dragview_location.getTop();
					if (topScreen > screenHeight/2) {
						//�����·�����ʾ�Ϸ�
						tv_dragview_bottom.setVisibility(View.INVISIBLE);
						tv_dragview_top.setVisibility(View.VISIBLE);
					} else if (topScreen <screenHeight/2) {
						//�����Ϸ�����ʾ�·�
						tv_dragview_bottom.setVisibility(View.VISIBLE);
						tv_dragview_top.setVisibility(View.INVISIBLE);
					}
					//����������ʼ����
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://��ָ̧����¼�
					System.out.println("��ָ̧���ˣ�����");
					//����ؼ������꣬������ǿؼ������ꡣ��������ָ������
					int x = ll_dragview_location.getLeft();
					int y = ll_dragview_location.getTop();
					
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				
				//����false:�¼��¼��������أ��������´��ݣ�����true:�ǽ��¼����Ѽ��¼�����ִ��
				return false;
			}
		});
	}
}
