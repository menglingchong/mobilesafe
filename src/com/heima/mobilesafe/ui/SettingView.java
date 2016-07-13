package com.heima.mobilesafe.ui;

import com.heima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �Զ�����Ͽؼ���Ϊ���Ż������ļ��еĴ��룬ʹ���Զ�����Ͽؼ�
 * @author lenovo
 *
 */
public class SettingView extends RelativeLayout {

	private TextView tv_setting_title;
	private TextView tv_setting_des;
	private CheckBox cb_setting_chose;
	//�ڴ�����ʹ�õ�ʱ�����
	public SettingView(Context context) {
		super(context);
		init();//�ڳ�ʼ���Զ���ؼ���ʱ����ӿؼ�
	}
	
	//�ڲ����ļ���ʹ�õ�ʱ����ã�����һ�����Ͳ���
	public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	//�ڲ����ļ���ʹ�õ�ʱ����ã�AttributeSet���ؼ�������ֵ
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	//��ӿؼ�
	public void init(){
		//��Ӳ����ļ�
//		TextView textView = new TextView(getContext());
//		textView.setText("�����Զ�����Ͽؼ�");
		//��һ�ַ�ʽ���������ļ�ת����view������ӵ��Զ�����Ͽؼ���
//		View view = View.inflate(getContext(), R.layout.settingview, null);//���е���ȥ�Һ���
//		this.addView(view);//this:�����Զ���ؼ�����,���Զ�����Ͽؼ�����ӿؼ�
		//�ڶ��ַ�ʽ����ȡview����ͬʱ��view�������ø��ؼ����൱���ȴ���һ��view����Ȼ���ٰѿؼ��ŵ��Զ���ؼ���
		View view = View.inflate(getContext(), R.layout.settingview, this);//�������ˣ�ȥ�ҵ���
		//view.findViewById():��ȡsettingView�����ļ��е���Ӧ�Ŀؼ�
		tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) view.findViewById(R.id.tv_setting_des);
		cb_setting_chose = (CheckBox) view.findViewById(R.id.cb_setting_chose);
	}
	
	//Ϊ�˸ı��Զ���ؼ���ֵ�����һЩ�����������޸��Զ���ؼ���ֵ
	/**
	 *  ���ñ���ķ���
	 */
	public void setTitle(String title){
		tv_setting_title.setText(title);
	}
	/**
	 * ����������ϸ��Ϣ�ķ���
	 */
	public void setDes(String des){
		tv_setting_des.setText(des);
	}
	
	/**
	 * ����checkbox���Ƿ�ѡ�з���
	 */
	public void setChecked(boolean isChecked){
		cb_setting_chose.setChecked(isChecked);//����checkbox��״̬
	}
	/**
	 * ��ȡcheckbox��״̬
	 * @return
	 */
	public boolean isChecked(){
		return cb_setting_chose.isChecked();
	}
}
