package com.heima.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String name;//����
	private String packageName;//����
	private Drawable icon;//ͼ��
	private long romSize;//ռ�õ��ڴ�ռ�
	private boolean isUser;//�Ƿ����û�����
	private boolean isChecked = false;//checkbox�ĳ�ʼֵ����Ϊfalse
	public TaskInfo() {
		super();
	}
	public TaskInfo(String name, String packageName, Drawable icon,
			long romSize, boolean isUser) {
		super();
		this.name = name;
		this.packageName = packageName;
		this.icon = icon;
		this.romSize = romSize;
		this.isUser = isUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getRomSize() {
		return romSize;
	}
	public void setRomSize(long romSize) {
		this.romSize = romSize;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	@Override
	public String toString() {
		return "TaskInfo [name=" + name + ", packageName=" + packageName
				+ ", icon=" + icon + ", romSize=" + romSize + ", isUser="
				+ isUser + "]";
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
}
