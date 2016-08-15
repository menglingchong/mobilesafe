package com.heima.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.heima.mobilesafe.service.AddressService;
import com.heima.mobilesafe.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.IOUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SpalshActivity extends Activity {

    protected static final int MSG_UPDATE_DIALOG = 1;
	protected static final int MSG_ENTER_HOME = 2;
	protected static final int MSG_SERVER_ERROR = 3;
	protected static final int MSG_URL_ERROR = 4;
	protected static final int MSG_IO_ERROR = 5;
	protected static final int MSG_JSON_ERROR = 6;
	private TextView tv_spalsh_versionname;
	private TextView tv_spalsh_load;
    private String code;
	private String apkUrl;
	private String des;
	private SharedPreferences sp;
	
	private Handler handler = new Handler(){

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_UPDATE_DIALOG:
				// 2.�汾�и��£������Ի���
				showdialog();
				break;
			case MSG_ENTER_HOME:
				enterHome();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "���ӷ�����ʧ��", 0).show();
				enterHome();
				break;
			case MSG_URL_ERROR:
				Toast.makeText(getApplicationContext(), "����ţ�"+MSG_URL_ERROR, 0).show();
				enterHome();
				break;
			case MSG_JSON_ERROR:
				Toast.makeText(getApplicationContext(), "����ţ�"+MSG_JSON_ERROR, 0).show();
				enterHome();
				break;
			case MSG_IO_ERROR:
				Toast.makeText(getApplicationContext(), "�ף��������ӳ��ִ��󡣡�����", 0).show();
				enterHome();
				break;
			default:
				break;
			}
		};
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        tv_spalsh_versionname = (TextView) findViewById(R.id.tv_spalsh_versionname);
        tv_spalsh_load = (TextView) findViewById(R.id.tv_spalsh_load);
        tv_spalsh_versionname.setText("�汾�ţ�"+getVersionName());
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //�����Զ����°汾
        if (sp.getBoolean("update", true)) {
			update();
		} else {
			//��ת��������
			//���������߳�ȥ˯��2s,��Ϊ���߳�����Ⱦ����Ĳ��������߳�˯�߾�û�а취������Ⱦ����
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					SystemClock.sleep(2000);//˯��2s
					enterHome();
				}
			}).start();
		}
        copydb("address.db");
        copydb("antivirus.db");
        //�����绰�����ķ���
//        Intent intent = new Intent(getApplicationContext(), AddressService.class);
//        startService(intent);//��������
        
        //������ݷ�ʽ
        shortcut();
    }
	/**
	 * ���������ݷ�ʽ
	 */
	private void shortcut() {
		if (sp.getBoolean("firstshortcut", true)) {
			//�����淢��һ���㲥
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			//���ÿ�ݷ�ʽ������
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ʿ");
			//���ÿ�ݷ�ʽ��ͼ��
			Bitmap value = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, value);
			//���ÿ�ݷ�ʽִ�еĲ���
			Intent intent2 = new Intent();
			intent2.setAction("com.heima.mobilesafe.home");
			intent2.addCategory("android.intent.category.DEFAULT");
			
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
			sendBroadcast(intent);
			
			//�����Ѵ��������ݷ�ʽ��״̬
			Editor edit = sp.edit();
			edit.putBoolean("firstshortcut", false);
			edit.commit();
		}
	}
	/**
	 * �������ݿⱾ���ֻ��������ݿ�����assertĿ¼�£������Զ�����id
	 */
	private void copydb(String dbname) {
		//��assertĿ¼�н����ݿ��ȡ����
		File file = new File(getFilesDir(), dbname);
		if (!file.exists()) {
			//1.��ȡassets�Ĺ�����
			AssetManager am = getAssets();
			InputStream in= null;
			FileOutputStream out =null;
			try {
				//2.��ȡ���ݿ���Դ
				in = am.open(dbname);
				//getCacheDir():��ȡ�����·����getFilesDir():��ȡ�ļ��Ĵ洢·��
				//д����
				out = new FileOutputStream(file);
				//3.��д����
				//���û�����
				byte[] b =new byte[1024];
				int len =-1;
				while ((len=in.read(b)) != -1) {
					out.write(b, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
			/*	try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				//xUtilsʵ�֣�������jar��
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
	}
	/**
	 * �����Ի���
	 */
    protected void showdialog() {
    	AlertDialog.Builder builder =new Builder(this);
    	//���öԻ�����ʧ
    	builder.setCancelable(false);
    	//���öԻ����ͼ��
    	builder.setIcon(R.drawable.ic_launcher);
    	//���öԻ���ı���
    	builder.setTitle("�°汾��"+code);
    	//���öԻ������Ϣ
    	builder.setMessage("�汾������Ϣ��"+des);
    	builder.setPositiveButton("����", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//3.�������°汾
				download();
			}
		});
    	builder.setNegativeButton("ȡ��", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//ȡ������,���ضԻ��򣬲���ת��ҳ��
				dialog.dismiss();
				enterHome();
			}
		});
    	//��ʾ�Ի���2�ֲ�ͬ�ķ�ʽ��ʵ�ֵ�Ч��һ��
//    	builder.create().show();
    	builder.show();
	}
    /**
     * 3.�������°汾,ʹ�õ�������Դ��xUtils���ж˵�����
     */
    protected void download() {
		HttpUtils httpUtils = new HttpUtils();
		//�ж�sdcard�Ƿ����
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// url: �°汾������·����target�������°汾��Ŀ¼��callback��RequestCallBack���ص��ӿ�
			httpUtils.download(apkUrl, "/storage/sdcard0/mobilesafe_2.apk",new RequestCallBack<File>() {
				// ���سɹ�ʱ�����ø÷���
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					//4.��װ���°汾
					installApk();
				}
	
				// ����ʧ��ʱ�����ø÷���
				@Override
				public void onFailure(HttpException arg0, String arg1) {
	
				}
	
				// ��ʾ���ؽ��ȵĲ���
				// total�����ص��ܽ��ȣ�current����ǰ���صĽ��ȣ�isUploading���Ƿ�֧�ֶ˵�����
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					// ����tv_spalsh_load�ؼ��ɼ�,������Ӧ�����ؽ���
					tv_spalsh_load.setVisibility(View.VISIBLE);
					tv_spalsh_load.setText(current + "/" + total);
				}
	
			});
		}

	}
    /**
     *  4.��װ���°汾,ʹ��ϵͳ���õ�PackageInstallerӦ�ð�װ������Ӧ��.
     *  ʹ����˽��ͼ����PackageInstallerActivity.class��
     */
	protected void installApk() {
		
		/*<intent-filter>
	        <action android:name="android.intent.action.VIEW" />
	        <category android:name="android.intent.category.DEFAULT" />
	        <data android:scheme="content" /> //content:�������ṩ�߻�ȡ����
	        <data android:scheme="file" /> //���ļ��л�ȡ����
	        <data android:mimeType="application/vnd.android.package-archive" />
	     </intent-filter>*/
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		//�������û��໥���ǣ����Ҫͳһ����
	/*	intent.setData(Uri.fromFile(new File("/storage/sdcard0/mobilesafe_2.apk")));
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(new File("/storage/sdcard0/mobilesafe_2.apk")), "application/vnd.android.package-archive");
		//�ڵ�ǰ��Activity�˳���ʱ�򣬻����֮ǰ��Activity��onActivityResult����
		//requestCode:�����룬������ʶ�Ǵ��ĸ�activity��ת������
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}
	/**
     * ��ת��������
     */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		//�Ƴ�Spalsh����
		finish();
	}
	/**
     * �����û����°汾
     */
   private void update() {
		//1.���ӵ����������汾�Ƿ��и��£���Ҫ�������磬��ʱ����,4.0�Ժ����������߳���ִ�У�����Ҫ�����߳��н���
		new Thread(new Runnable() {
			
			Message message = new Message();
			private int startTime;
			@Override
			public void run() {
				//����֮ǰ��ȡһ����ʼʱ��
				startTime = (int) System.currentTimeMillis();
				//1.1���ӵ�������
				try {
					URL url = new URL("http://192.168.1.37:8080/updateinfo.html");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");//��������ʽ
					conn.setConnectTimeout(5000);//�������ӳ�ʱʱ��
					conn.setReadTimeout(5000);//���ö�ȡ��ʱʱ��
					int resonceCode = conn.getResponseCode();//��ȡ���������ص�ת̬��
					if (resonceCode ==200) {
						//���ӳɹ�����ȡ���������ص����ݣ�code�����µİ汾�ţ�apkUrl:�°汾������·����des:������Ϣ�������û���������Щ��Ϣ���޸�����Щbug
						//��ȡ����֮ǰ������������ô��װ���ݵģ�xml��json...
						System.out.println("���ӳɹ�");
						//��ȡ���������ص�����Ϣ
						InputStream inputStream = conn.getInputStream();
						//������Ϣת����json�ַ���
						 String json = StreamUtil.parserStreamUtil(inputStream);
						//�Ի�ȡ��json���ݽ��н���������JsonObject����
						JSONObject jsonObject = new JSONObject(json);
						code = jsonObject.getString("code");
						apkUrl = jsonObject.getString("apkUrl");
						des = jsonObject.getString("des");
						System.out.println("code:"+code+"	apkUrl:"+apkUrl+"	des:"+des);
						//1.2����Ƿ����°汾
						//�жϷ������İ汾�ź͵�ǰ�İ汾���Ƿ�һ�£�һ����û���°汾���������°汾�������Ի��������û����°汾
						if (code.equals(getVersionName())) {
							//û���°汾,��ת����ʼ����
							message.what = MSG_ENTER_HOME;
						} else {
							//2.�����°汾,�����Ի���
							//�������߳�UI��������Ϣ�����̣߳���handle������Ϣ
							message.what= MSG_UPDATE_DIALOG;
						}
					} else {
						//����ʧ��
						System.out.println("����ʧ��");
						message.what= MSG_SERVER_ERROR;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					message.what = MSG_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					message.what = MSG_IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					message.what = MSG_JSON_ERROR;
				} finally{ //������û���쳣������ִ������Ĳ���
					//������������ʱ�������
					//���ӳɹ�֮�󣬻�ȡһ������ʱ��
					int endTime = (int) System.currentTimeMillis();
					int dTime = endTime-startTime;
					if (dTime <2000) {
						//˯��2s��
						SystemClock.sleep(2000-dTime);//ʼ��˯��2s��ʱ��
					}
					//������Ϣ�����߳�
					handler.sendMessage(message);
				}
			}
		}).start();
	}

   /**
    * ��ȡӦ�ó���İ汾����
    * @return
    */
    private String getVersionName(){
    	//���Ĺ����ߣ���ȡ�嵥�ļ��е�������Ϣ
    	PackageManager pm = getPackageManager();
    	try {
    		//���ݰ�����ȡ�嵥�ļ��е���Ϣ����ʵ���Ƿ���һ���������嵥�ļ���Ϣ��javabean
    		//packageName:Ӧ�ó���İ���
    		//flags:ָ����Ϣ�ı�ǩ��0����ȡ��������Ϣ������������汾�ţ�Ҫ���ȡȨ�޵���Ϣ������ͨ����ǩ��ָ�����ܻ�ȡ
			//GET_PERMISSIONS����ǩ�ĺ��壺���˻�ȡ��������Ϣ֮�⣬�����ȡȨ�޵���Ϣ��
    		//getPackageName():��ȡ��ǰӦ�ó���İ���
    		PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
    		//��ȡ�汾������
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }

}
