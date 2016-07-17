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
		
		System.out.println("手机重启了，接收到了广播");
		//检查sim卡是否发生变化
		SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		//1.获取保存到sp的SIM卡号
		String sp_sim = sp.getString("sim", "");
		//2.获取SIM卡号
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = tel.getSimSerialNumber();
		// 3.判断两个SIM卡号是否为空
		if (!TextUtils.isEmpty(sp_sim) && !TextUtils.isEmpty(sim)) {
			// 4.判断两个SIM卡号是否一致
			if (!sp_sim.equals(sp)) {
				// 两个sim卡不一致，发动短信

				// 发送报警短信的操作
				// 短信的管理者
				SmsManager manager = SmsManager.getDefault();
				// destinationAddress:收件人
				// scAddress：短信中心地址 ;一般为null
				// text：短信内容
				// sentIntent：是否发送成功 一般为null
				// deliveryIntent：短信的协议;一般为null
				manager.sendTextMessage("18779167471", null, "发送报警短信！！", null,
						null);
			}  
		 }  
	}
}
