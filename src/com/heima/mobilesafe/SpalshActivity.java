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
				// 2.版本有更新，弹出对话框
				showdialog();
				break;
			case MSG_ENTER_HOME:
				enterHome();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "连接服务器失败", 0).show();
				enterHome();
				break;
			case MSG_URL_ERROR:
				Toast.makeText(getApplicationContext(), "错误号："+MSG_URL_ERROR, 0).show();
				enterHome();
				break;
			case MSG_JSON_ERROR:
				Toast.makeText(getApplicationContext(), "错误号："+MSG_JSON_ERROR, 0).show();
				enterHome();
				break;
			case MSG_IO_ERROR:
				Toast.makeText(getApplicationContext(), "亲，网络连接出现错误。。。。", 0).show();
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
        tv_spalsh_versionname.setText("版本号："+getVersionName());
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //屏蔽自动更新版本
        if (sp.getBoolean("update", true)) {
			update();
		} else {
			//跳转到主界面
			//不能让主线程去睡眠2s,因为主线程有渲染界面的操作，主线程睡眠就没有办法进行渲染操作
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					SystemClock.sleep(2000);//睡眠2s
					enterHome();
				}
			}).start();
		}
        copydb("address.db");
        copydb("antivirus.db");
        //开启电话监听的服务
//        Intent intent = new Intent(getApplicationContext(), AddressService.class);
//        startService(intent);//开启服务
        
        //创建快捷方式
        shortcut();
    }
	/**
	 * 创建桌面快捷方式
	 */
	private void shortcut() {
		if (sp.getBoolean("firstshortcut", true)) {
			//给桌面发送一个广播
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			//设置快捷方式的名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
			//设置快捷方式的图标
			Bitmap value = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, value);
			//设置快捷方式执行的操作
			Intent intent2 = new Intent();
			intent2.setAction("com.heima.mobilesafe.home");
			intent2.addCategory("android.intent.category.DEFAULT");
			
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
			sendBroadcast(intent);
			
			//保存已创建桌面快捷方式的状态
			Editor edit = sp.edit();
			edit.putBoolean("firstshortcut", false);
			edit.commit();
		}
	}
	/**
	 * 拷贝数据库本地手机，将数据库存放在assert目录下，不会自动生成id
	 */
	private void copydb(String dbname) {
		//从assert目录中将数据库读取出来
		File file = new File(getFilesDir(), dbname);
		if (!file.exists()) {
			//1.获取assets的管理者
			AssetManager am = getAssets();
			InputStream in= null;
			FileOutputStream out =null;
			try {
				//2.读取数据库资源
				in = am.open(dbname);
				//getCacheDir():获取缓存的路径；getFilesDir():获取文件的存储路径
				//写入流
				out = new FileOutputStream(file);
				//3.读写操作
				//设置缓冲区
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
				//xUtils实现，第三方jar包
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
	}
	/**
	 * 弹出对话框
	 */
    protected void showdialog() {
    	AlertDialog.Builder builder =new Builder(this);
    	//设置对话框不消失
    	builder.setCancelable(false);
    	//设置对话框的图标
    	builder.setIcon(R.drawable.ic_launcher);
    	//设置对话框的标题
    	builder.setTitle("新版本："+code);
    	//设置对话框的信息
    	builder.setMessage("版本更新信息："+des);
    	builder.setPositiveButton("升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//3.下载最新版本
				download();
			}
		});
    	builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消升级,隐藏对话框，并跳转主页面
				dialog.dismiss();
				enterHome();
			}
		});
    	//显示对话框。2种不同的方式，实现的效果一样
//    	builder.create().show();
    	builder.show();
	}
    /**
     * 3.下载最新版本,使用第三方开源库xUtils进行端点续传
     */
    protected void download() {
		HttpUtils httpUtils = new HttpUtils();
		//判断sdcard是否挂载
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// url: 新版本的下载路径；target：保存新版本的目录；callback：RequestCallBack，回调接口
			httpUtils.download(apkUrl, "/storage/sdcard0/mobilesafe_2.apk",new RequestCallBack<File>() {
				// 下载成功时，调用该方法
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					//4.安装最新版本
					installApk();
				}
	
				// 下载失败时，调用该方法
				@Override
				public void onFailure(HttpException arg0, String arg1) {
	
				}
	
				// 显示下载进度的操作
				// total：下载的总进度；current：当前下载的进度；isUploading：是否支持端点续传
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					// 设置tv_spalsh_load控件可见,设置相应的下载进度
					tv_spalsh_load.setVisibility(View.VISIBLE);
					tv_spalsh_load.setText(current + "/" + total);
				}
	
			});
		}

	}
    /**
     *  4.安装最新版本,使用系统内置的PackageInstaller应用安装第三方应用.
     *  使用隐私意图调用PackageInstallerActivity.class类
     */
	protected void installApk() {
		
		/*<intent-filter>
	        <action android:name="android.intent.action.VIEW" />
	        <category android:name="android.intent.category.DEFAULT" />
	        <data android:scheme="content" /> //content:从内容提供者获取数据
	        <data android:scheme="file" /> //从文件中获取数据
	        <data android:mimeType="application/vnd.android.package-archive" />
	     </intent-filter>*/
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		//单独设置会相互覆盖，因此要统一设置
	/*	intent.setData(Uri.fromFile(new File("/storage/sdcard0/mobilesafe_2.apk")));
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(new File("/storage/sdcard0/mobilesafe_2.apk")), "application/vnd.android.package-archive");
		//在当前的Activity退出的时候，会调用之前的Activity的onActivityResult方法
		//requestCode:请求码，用来标识是从哪个activity跳转过来的
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}
	/**
     * 跳转到主界面
     */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		//移除Spalsh界面
		finish();
	}
	/**
     * 提醒用户更新版本
     */
   private void update() {
		//1.连接到服务器检查版本是否有更新，需要连接网络，耗时操作,4.0以后不允许在主线程中执行，则需要在子线程中进行
		new Thread(new Runnable() {
			
			Message message = new Message();
			private int startTime;
			@Override
			public void run() {
				//连接之前获取一个开始时间
				startTime = (int) System.currentTimeMillis();
				//1.1连接到服务器
				try {
					URL url = new URL("http://192.168.1.37:8080/updateinfo.html");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");//设置请求方式
					conn.setConnectTimeout(5000);//设置连接超时时间
					conn.setReadTimeout(5000);//设置读取超时时间
					int resonceCode = conn.getResponseCode();//获取服务器返回的转态码
					if (resonceCode ==200) {
						//连接成功，获取服务器返回的数据，code：最新的版本号，apkUrl:新版本的下载路径，des:描述信息，告诉用户增加了哪些信息，修复了哪些bug
						//获取数据之前，服务器是怎么封装数据的，xml，json...
						System.out.println("连接成功");
						//获取服务器返回的流信息
						InputStream inputStream = conn.getInputStream();
						//将流信息转化成json字符串
						 String json = StreamUtil.parserStreamUtil(inputStream);
						//对获取的json数据进行解析，创建JsonObject对象
						JSONObject jsonObject = new JSONObject(json);
						code = jsonObject.getString("code");
						apkUrl = jsonObject.getString("apkUrl");
						des = jsonObject.getString("des");
						System.out.println("code:"+code+"	apkUrl:"+apkUrl+"	des:"+des);
						//1.2检查是否有新版本
						//判断服务器的版本号和当前的版本号是否一致，一致则没有新版本，否则有新版本，弹出对话框，提醒用户更新版本
						if (code.equals(getVersionName())) {
							//没有新版本,跳转到开始界面
							message.what = MSG_ENTER_HOME;
						} else {
							//2.发现新版本,弹出对话框
							//更新主线程UI，发送消息到主线程，用handle处理消息
							message.what= MSG_UPDATE_DIALOG;
						}
					} else {
						//连接失败
						System.out.println("连接失败");
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
				} finally{ //不管有没有异常都可以执行下面的操作
					//处理外网连接时间的问题
					//连接成功之后，获取一个结束时间
					int endTime = (int) System.currentTimeMillis();
					int dTime = endTime-startTime;
					if (dTime <2000) {
						//睡眠2s钟
						SystemClock.sleep(2000-dTime);//始终睡眠2s的时间
					}
					//发送消息到主线程
					handler.sendMessage(message);
				}
			}
		}).start();
	}

   /**
    * 获取应用程序的版本名称
    * @return
    */
    private String getVersionName(){
    	//包的管理者，获取清单文件中的所有信息
    	PackageManager pm = getPackageManager();
    	try {
    		//根据包名获取清单文件中的信息，其实就是返回一个保存有清单文件信息的javabean
    		//packageName:应用程序的包名
    		//flags:指定信息的标签，0：获取基础的信息，比如包名，版本号，要想获取权限等信息，必须通过标签来指定才能获取
			//GET_PERMISSIONS：标签的含义：除了获取基础的信息之外，还会获取权限的信息。
    		//getPackageName():获取当前应用程序的包名
    		PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
    		//获取版本号名称
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }

}
