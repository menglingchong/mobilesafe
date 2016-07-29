package com.heima.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private String name;//名称
	private Drawable icon;//图标
	private String packageName;//包名
	private String versionName;//版本号
	private boolean isSD;//是否安装到sd卡
	private boolean isUser;//是否是用户程序
	//含有所有参数的构造函数,方便信息添加到bean对象中
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
	//方便显示bean对象中的信息
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", icon=" + icon + ", packageName="
				+ packageName + ", versionName=" + versionName + ", isSD="
				+ isSD + ", isUser=" + isUser + "]";
	}
}
