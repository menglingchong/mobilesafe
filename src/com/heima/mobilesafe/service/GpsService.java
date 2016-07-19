package com.heima.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class GpsService extends Service {

	private LocationManager locationManager;
	private String bestProvider;
	private MyLocationListener myLocationListener;
	private SharedPreferences sp;
	
	//创建服务
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//1.获取位置管理者
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2.获取定位方式
		List<String> providers = locationManager.getProviders(true);
		for (String string : providers) {
			System.out.println(string);
		}
		if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		}else if (providers.contains(LocationManager.GPS_PROVIDER)) {
			bestProvider = LocationManager.GPS_PROVIDER;
		}else {
			Toast.makeText(getApplicationContext(), "没有最佳的定位方式", 0).show();
			return;
		}
		//3.定位
		myLocationListener = new MyLocationListener();
		//provider:定位方式；minTime：定位最小时间间隔；minDistance：定位最小距离间隔；listener：回调监听
		locationManager.requestLocationUpdates(bestProvider, 5000, 1, myLocationListener);
	}
	
	private class MyLocationListener implements LocationListener{
		//定位位置发生变化时调用
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();//获取纬度
			double longitude = location.getLongitude();//获取经度
			//将获取到的经纬度信息，发送给安全号码
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(sp.getString("safenumber", "5556"), null, "经度："+longitude+"	  纬度："+latitude, null, null);
			//停止服务.（避免服务一直运行）
			stopSelf();
		}
		//定位状态发生变化时调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		//定位可用时调用
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		//定位不可用时调用
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myLocationListener);//关闭gps
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	

}
