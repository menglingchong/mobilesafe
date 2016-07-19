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
	
	//��������
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//1.��ȡλ�ù�����
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2.��ȡ��λ��ʽ
		List<String> providers = locationManager.getProviders(true);
		for (String string : providers) {
			System.out.println(string);
		}
		if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		}else if (providers.contains(LocationManager.GPS_PROVIDER)) {
			bestProvider = LocationManager.GPS_PROVIDER;
		}else {
			Toast.makeText(getApplicationContext(), "û����ѵĶ�λ��ʽ", 0).show();
			return;
		}
		//3.��λ
		myLocationListener = new MyLocationListener();
		//provider:��λ��ʽ��minTime����λ��Сʱ������minDistance����λ��С��������listener���ص�����
		locationManager.requestLocationUpdates(bestProvider, 5000, 1, myLocationListener);
	}
	
	private class MyLocationListener implements LocationListener{
		//��λλ�÷����仯ʱ����
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();//��ȡγ��
			double longitude = location.getLongitude();//��ȡ����
			//����ȡ���ľ�γ����Ϣ�����͸���ȫ����
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(sp.getString("safenumber", "5556"), null, "���ȣ�"+longitude+"	  γ�ȣ�"+latitude, null, null);
			//ֹͣ����.���������һֱ���У�
			stopSelf();
		}
		//��λ״̬�����仯ʱ����
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		//��λ����ʱ����
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		//��λ������ʱ����
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myLocationListener);//�ر�gps
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	

}
