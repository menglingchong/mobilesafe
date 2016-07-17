package com.heima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.style.TtsSpan.TelephoneBuilder;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		System.out.println("�ֻ������ˣ����յ��˹㲥");
		//���sim���Ƿ����仯
		SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		//1.��ȡ���浽sp��SIM����
		String sp_sim = sp.getString("sim", "");
		//2.��ȡSIM����
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = tel.getSimSerialNumber();
		// 3.�ж�����SIM�����Ƿ�Ϊ��
		if (!TextUtils.isEmpty(sp_sim) && !TextUtils.isEmpty(sim)) {
			// 4.�ж�����SIM�����Ƿ�һ��
			if (!sp_sim.equals(sp)) {
				// ����sim����һ�£���������

				// ���ͱ������ŵĲ���
				// ���ŵĹ�����
				SmsManager manager = SmsManager.getDefault();
				// destinationAddress:�ռ���
				// scAddress���������ĵ�ַ ;һ��Ϊnull
				// text����������
				// sentIntent���Ƿ��ͳɹ� һ��Ϊnull
				// deliveryIntent�����ŵ�Э��;һ��Ϊnull
				manager.sendTextMessage("18779167471", null, "���ͱ������ţ���", null,
						null);
			}  
		 }  
	}
}
