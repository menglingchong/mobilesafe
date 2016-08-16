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
	//��ʼ������
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	//����fragment�Ĳ���
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
				//��ת���������
				Intent intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.setData(Uri.parse("package:"+list.get(position).getPackagename()));
				startActivity(intent);
			}
		});
		
		return view;
	}
	//������activity����ʱ���øú���
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		scanner();
	}
	//ɨ��
	private void scanner() {
		pm = getActivity().getPackageManager();
		tv_cachefragment_text.setText("���ڳ�ʼ��ɨ�����档����");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(100);
				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				//���ý����������ֵ
				pb_cachefragment_progressbar.setMax(installedPackages.size());
				//���ý������ĵ�ǰֵ
				int count =0;
				for (PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					count++;
					pb_cachefragment_progressbar.setProgress(count);
					
					//����ɨ����ʾ��Ӧ�õ�����
					final String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
					//����UI����
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tv_cachefragment_text.setText("����ɨ�裺"+appname);
							}
						});
					}
					//��ȡ����Ĵ�С
			        //�����ȡ����
			        try {
			        	//��ȡ�������
						Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
						Method method = loadClass.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
						//receiver�����ʵ�������ز��������������Ǿ�̬�ģ������ָ��
						method.invoke(pm, packageInfo.packageName,mStatsObserver);
						
			        } catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				//ɨ�����
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_cachefragment_text.setVisibility(View.GONE);
							pb_cachefragment_progressbar.setVisibility(View.GONE);
							
							myAdapter = new MyAdapter();
							lv_cacheframent_caches.setAdapter(myAdapter);
							//��������
							if (list.size() >0) {
								btn_cachefragment_clear.setVisibility(View.VISIBLE);
								btn_cachefragment_clear.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										//�����棬android�е�ǰ��Ӧ�ò�����������Ӧ�õĻ��棬���ǿ���ʹ��freeStorageAndNotify()���������ֻ�����
										try {
											//ʹ�÷���ķ�ʽ�������棬
											Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
											Method method = loadClass.getDeclaredMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);
											//Long.MAX_VALUE:��ʾ��������ڴ�
											method.invoke(pm, Long.MAX_VALUE,new MyIPackageDataObserver());
										} catch (Exception e) {
											e.printStackTrace();
										}
										//������
										list.clear();
										//���½���
										myAdapter.notifyDataSetChanged();
										//����button��ť
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
	//AIDL:Զ�̷��� ������̼�ͨ�ŵ�����.
	private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
		//�������������֮�����
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			
		}
		
	}
	//AIDL:Զ�̷��� ������̼�ͨ�ŵ�����.
	//��ȡ�����С
    IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
        	long cachesize=stats.cacheSize;//�����С
        	/*long codesize=stats.codeSize;//Ӧ�ó����С
        	long datasize=stats.dataSize;//���ݴ�С*/ 
        	if (cachesize >0) {
				String cache = Formatter.formatFileSize(getActivity(), cachesize);
				list.add(new CacheInfo(stats.packageName, cache));
			}
        	
        	/*String code = Formatter.formatFileSize(getActivity(), codesize);
        	String data = Formatter.formatFileSize(getActivity(), datasize);*/
        	//System.out.println(stats.packageName+" cachesize:"+cache+"	codesize:"+code+" datasize:"+data);
        }
    };
    
    //����bean����
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
			//������ʾ����
			CacheInfo cacheInfo = list.get(position);
			//���ݰ�����ȡapplication��Ϣ
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(cacheInfo.getPackagename(), 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				String appname = applicationInfo.loadLabel(pm).toString();
				//������ʾ
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
