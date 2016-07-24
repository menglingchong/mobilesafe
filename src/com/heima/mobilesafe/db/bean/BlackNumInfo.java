package com.heima.mobilesafe.db.bean;
/**
 * bean��
 * @author lenovo
 *
 */
public class BlackNumInfo {

	private String blacknum;
	private int mode;
	//���캯��
	public BlackNumInfo() {
		super();
	}
	//���������Ĺ��캯���������������
	public BlackNumInfo(String blacknum, int mode) {
		super();
		this.blacknum = blacknum;
		if (mode >=0 && mode<= 2) {
			this.mode =mode;
		}else {
			this.mode = 0;
		}
	}
	public String getBlacknum() {
		return blacknum;
	}
	public void setBlacknum(String blacknum) {
		this.blacknum = blacknum;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		//��Ϊmode��ֵ0-2����mode�����ж�,�������ж�,֮���ڵ������������ʱ��Ͳ�����ȥ��mode�����ж�
		if (0 <= mode && mode <=2) {
			this.mode = mode;
		}else {
			this.mode =0;
		}
	}
	@Override
	public String toString() {
		return "BlackNumInfo [blacknum=" + blacknum + ", mode=" + mode + "]";
	}
	
	
}
