package com.heima.mobilesafe;

import com.heima.mobilesafe.fragment.CacheFragment;
import com.heima.mobilesafe.fragment.SdFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class ClearCacheActivity extends FragmentActivity {

	private CacheFragment cacheFragment;
	private SdFragment sdFragment;
	private FragmentManager supportFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearcache);
		
		//1.��ȡfragment
		cacheFragment = new CacheFragment();
		sdFragment = new SdFragment();
		//2.��ȡfragment�Ĺ�����
		supportFragmentManager = getSupportFragmentManager();
		//��������
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		//���fragment���ؼ���
		//����1��Ҫ�滻�Ŀؼ���id������2��Ҫ��ӵ�fragment
		beginTransaction.add(R.id.fl_clearcache_fragment, cacheFragment);//���ݲ���ˢ��
		beginTransaction.add(R.id.fl_clearcache_fragment, sdFragment); 
		//����fragment����
		beginTransaction.hide(sdFragment);
		//�ύ����
		beginTransaction.commit();
	}
	//��ť�ĵ���¼�
	public void cache(View v){
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		beginTransaction.hide(sdFragment);
	    beginTransaction.show(cacheFragment);
		beginTransaction.commit();
	}
	
	public void sd(View v){
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		beginTransaction.hide(cacheFragment);
		beginTransaction.show(sdFragment);
		beginTransaction.commit();
	}
}
