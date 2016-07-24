package com.heima.mobilesafe.db.bean;
/**
 * bean类
 * @author lenovo
 *
 */
public class BlackNumInfo {

	private String blacknum;
	private int mode;
	//构造函数
	public BlackNumInfo() {
		super();
	}
	//两个参数的构造函数，方便添加数据
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
		//因为mode的值0-2，对mode进行判断,在这里判断,之后在调用这个方法的时候就不用再去对mode进行判断
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
