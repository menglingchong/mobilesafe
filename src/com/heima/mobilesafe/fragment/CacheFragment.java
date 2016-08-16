package com.heima.mobilesafe.fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heima.mobilesafe.R;

public class CacheFragment extends Fragment {
	private TextView tv_cachefragment_text;
	private ProgressBar pb_cachefragment_progressbar;
	private ListView lv_cacheframent_caches;
	private List<CacheInfo> list;
	private PackageManager pm;
	private MyAdapter myAdapter;
	private Button btn_cachefragment_clear;
	//初始化操作
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	//设置fragment的布局
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		list = new ArrayList<CacheInfo>();
		list.clear();
		
		View view = inflater.inflate(R.layout.fragment_cache, container,false);
		tv_cachefragment_text = (TextView) view.findViewById(R.id.tv_cachefragment_text);
		pb_cachefragment_progressbar = (ProgressBar) view.findViewById(R.id.pb_cachefragment_progressbar);
		lv_cacheframent_caches = (ListView) view.findViewById(R.id.lv_cacheframent_caches);
		btn_cachefragment_clear = (Button) view.findViewById(R.id.btn_cachefragment_clear);
		lv_cacheframent_caches.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//跳转到详情界面
				Intent intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.setData(Uri.parse("package:"+list.get(position).getPackagename()));
				startActivity(intent);
			}
		});
		
		return view;
	}
	//当宿主activity创建时调用该函数
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		scanner();
	}
	//扫描
	private void scanner() {
		pm = getActivity().getPackageManager();
		tv_cachefragment_text.setText("正在初始化扫描引擎。。。");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(100);
				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				//设置进度条的最大值
				pb_cachefragment_progressbar.setMax(installedPackages.size());
				//设置进度条的当前值
				int count =0;
				for (PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					count++;
					pb_cachefragment_progressbar.setProgress(count);
					
					//设置扫描显示的应用的名称
					final String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
					//更新UI界面
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tv_cachefragment_text.setText("正在扫描："+appname);
							}
						});
					}
					//获取缓存的大小
			        //反射获取缓存
			        try {
			        	//获取类加载器
						Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
						Method method = loadClass.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
						//receiver：类的实例，隐藏参数，若方法不是静态的，则必须指定
						method.invoke(pm, packageInfo.packageName,mStatsObserver);
						
			        } catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				//扫描完成
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_cachefragment_text.setVisibility(View.GONE);
							pb_cachefragment_progressbar.setVisibility(View.GONE);
							
							myAdapter = new MyAdapter();
							lv_cacheframent_caches.setAdapter(myAdapter);
							//缓存清理
							if (list.size() >0) {
								btn_cachefragment_clear.setVisibility(View.VISIBLE);
								btn_cachefragment_clear.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										//清理缓存，android中当前的应用不能清理其他应用的缓存，但是可以使用freeStorageAndNotify()方法清理手机缓存
										try {
											//使用反射的方式来清理缓存，
											Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
											Method method = loadClass.getDeclaredMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);
											//Long.MAX_VALUE:表示申请最大内存
											method.invoke(pm, Long.MAX_VALUE,new MyIPackageDataObserver());
										} catch (Exception e) {
											e.printStackTrace();
										}
										//清理缓存
										list.clear();
										//更新界面
										myAdapter.notifyDataSetChanged();
										//隐藏button按钮
										btn_cachefragment_clear.setVisibility(View.GONE);
									}
								});
							}
						}
					});
				}
			}
		}).start();
	}
	//AIDL:远程服务 解决进程间通信的问题.
	private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
		//当缓存清理完成之后调用
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			
		}
		
	}
	//AIDL:远程服务 解决进程间通信的问题.
	//获取缓存大小
    IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
        	long cachesize=stats.cacheSize;//缓存大小
        	/*long codesize=stats.codeSize;//应用程序大小
        	long datasize=stats.dataSize;//数据大小*/ 
        	if (cachesize >0) {
				String cache = Formatter.formatFileSize(getActivity(), cachesize);
				list.add(new CacheInfo(stats.packageName, cache));
			}
        	
        	/*String code = Formatter.formatFileSize(getActivity(), codesize);
        	String data = Formatter.formatFileSize(getActivity(), datasize);*/
        	//System.out.println(stats.packageName+" cachesize:"+cache+"	codesize:"+code+" datasize:"+data);
        }
    };
    
    //创建bean对象
    class CacheInfo{
    	private String packagename;
    	private String cachesize;
		public CacheInfo(String packagename, String cachesize) {
			super();
			this.packagename = packagename;
			this.cachesize = cachesize;
		}
		public String getPackagename() {
			return packagename;
		}
		public void setPackagename(String packagename) {
			this.packagename = packagename;
		}
		public String getCachesize() {
			return cachesize;
		}
		public void setCachesize(String cachesize) {
			this.cachesize = cachesize;
		}
    	
    }
    private class MyAdapter extends BaseAdapter{

		private View cacheView;
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder;
			if (convertView != null) {
				cacheView = convertView;
				viewHolder = (ViewHolder) cacheView.getTag();
			}else {
				cacheView = View.inflate(getActivity(), R.layout.item_cachefragment, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_itemfragment_icon = (ImageView) cacheView.findViewById(R.id.iv_itemfragment_icon);
				viewHolder.tv_itemfragment_appname = (TextView) cacheView.findViewById(R.id.tv_itemfragment_appname);
				viewHolder.tv_itemfragment_cachesize = (TextView) cacheView.findViewById(R.id.tv_itemfragment_cachesize);
				cacheView.setTag(viewHolder);
			}
			//设置显示数据
			CacheInfo cacheInfo = list.get(position);
			//根据包名获取application信息
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(cacheInfo.getPackagename(), 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				String appname = applicationInfo.loadLabel(pm).toString();
				//设置显示
				viewHolder.iv_itemfragment_icon.setImageDrawable(icon);
				viewHolder.tv_itemfragment_appname.setText(appname);
				viewHolder.tv_itemfragment_cachesize.setText(cacheInfo.getCachesize());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cacheView;
		}
    }
    
    static class ViewHolder{
    	ImageView iv_itemfragment_icon;
    	TextView tv_itemfragment_appname,tv_itemfragment_cachesize;
    }
	
}
