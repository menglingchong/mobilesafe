package com.heima.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private String name;//����
	private Drawable icon;//ͼ��
	private String packageName;//����
	private String versionName;//�汾��
	private boolean isSD;//�Ƿ�װ��sd��
	private boolean isUser;//�Ƿ����û�����
	//�������в����Ĺ��캯��,������Ϣ��ӵ�bean������
	public AppInfo(String name, Drawable icon, String packageName,
			String versionName, boolean isSD, boolean isUser) {
		super();
		this.name = name;
		this.icon = icon;
		this.packageName = packageName;
		this.versionName = versionName;
		this.isSD = isSD;
		this.isUser = isUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	//������ʾbean�����е���Ϣ
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", icon=" + icon + ", packageName="
				+ packageName + ", versionName=" + versionName + ", isSD="
				+ isSD + ", isUser=" + isUser + "]";
	}
}
