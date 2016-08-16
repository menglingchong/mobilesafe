package com.heima.mobilesafe;

import com.heima.mobilesafe.utils.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView gv_home_gridview;
	private AlertDialog dialog;
	private EditText et_setpassword_password;
	private EditText et_setpassword_confrim;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);
		gv_home_gridview.setAdapter(new myAdapter());
		gv_home_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				
				case 0://�ֻ�����
					//��ת���ֻ�����ģ��
					//�ж��û��Ƿ��ǵ�һ�ε�����������������룬���ǵ�һ�ε�������������룬����������ȷ���ܽ����ģ��
					//�ж��û��Ƿ����ù�����
					if (TextUtils.isEmpty(sp.getString("password", ""))) { 
						//����Ϊ�գ�������������ĶԻ���
						showSetPasswordDialog();
					} else {
						//���벻Ϊ�գ�������������ĶԻ���
						showEnterPasswordDialog();
					}
					
					break;
				case 1://ͨѶ��ʿ
					Intent intent1 = new Intent(getApplicationContext(), CallSmsSafeActivity.class);
					startActivity(intent1);
					break;
				case 2://�������
					Intent intent2 = new Intent(HomeActivity.this, SoftManagerActivity.class);
					startActivity(intent2);
					break;
				case 3://���̹���
					Intent intent3 = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent3);
					break;
				case 4://����ͳ��
					Intent intent4 = new Intent(HomeActivity.this, TrafficActivity.class);
					startActivity(intent4);
					break;
				case 5://�ֻ�ɱ��
					Intent intent5 = new Intent(HomeActivity.this, AntiVirusActivity.class);
					startActivity(intent5);
					break;
				case 6://��������
					Intent intent6 = new Intent(HomeActivity.this, ClearCacheActivity.class);
					startActivity(intent6);
					break;
				case 7://�߼�����
					Intent intent7 = new Intent(getApplicationContext(), AToolsActivity.class);
					startActivity(intent7);
					break;
				case 8://��������
					Intent intent8 = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent8);
					break;

				default:
					break;
				}
			}
		});
	}
	/**
	 * ��������Ի���
	 */
	int count =0;
	protected void showEnterPasswordDialog() {

		AlertDialog.Builder builder = new Builder(this);
		//���öԻ�����ȡ��
		builder.setCancelable(false);
		//�������ļ�ת����view����
		View view = View.inflate(getApplicationContext(), R.layout.dialog_enterpassword, null);
		//��ȡ�Ӳ����еĿؼ�
		final EditText et_enterpassword_password = (EditText) view.findViewById(R.id.et_enterpassword_password);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		ImageView iv_enterpassword_hide = (ImageView) view.findViewById(R.id.iv_enterpassword_hide);
		iv_enterpassword_hide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ������ʾ����
				if (count %2==0) {
					//��������
					et_enterpassword_password.setInputType(129);//ͨ��������������������
				} else {
					//��ʾ����
					et_enterpassword_password.setInputType(0);
				}
				count++;
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1.��ȡ��������Ի��������
				String password = et_enterpassword_password.getText().toString().trim();
				//2.�ж������Ƿ�Ϊ��
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "���������Ϊ�գ����������룡", 0).show();
					return;
				}
				//3.�������sp�б������������ж�
				String sp_password = sp.getString("password", "");
				if (MD5Util.passwordMD5(password).equals(sp_password)) {
					//ҳ����ת���ֻ�����ҳ��
					Intent intent =new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
					//���ضԻ���
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "����������ȷ", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "�������벻��ȷ������������!", 0).show();
				}
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���ضԻ���
				dialog.dismiss();
			}
		});
		builder.setView(view);
//		builder.show();
		
		dialog = builder.create();
		dialog.show();
	}
	/**
	 * ��������ĶԻ���
	 */
	protected void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		//���öԻ�����ȡ��
		builder.setCancelable(false);
		//�������ļ�ת����view����
		View view = View.inflate(getApplicationContext(), R.layout.dialog_setpassword, null);
		et_setpassword_password = (EditText) view.findViewById(R.id.et_setpassword_password);
		et_setpassword_confrim = (EditText) view.findViewById(R.id.et_setpassword_confrim);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		//����ȷ����ȡ����ť�ĵ���¼�
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��������
				//1.��ȡ������������������
				String password = et_setpassword_password.getText().toString().trim();
				//2.�ж������Ƿ�Ϊ��
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "����Ϊ�գ����������룡", 0).show();
					return;
				}
				//3.��ȡȷ������
				String confrim = et_setpassword_confrim.getText().toString().trim();
				//4.�ж����������Ƿ�һ��;����һ�¾ͱ��浽sharedPreference
				if (password.equals(confrim)) {
					Editor edit = sp.edit();
					edit.putString("password", MD5Util.passwordMD5(password));
					edit.commit();
					//ȡ���Ի���
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "�������óɹ�", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "�����������ò�һ��", 0).show();
				}
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���ضԻ���
				dialog.dismiss();
			}
		}); 
		builder.setView(view);
//		builder.show();
		
		dialog = builder.create();
		dialog.show();
	}

	public class myAdapter extends BaseAdapter{
		//����Դ
		int[] imageId = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
				R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
				R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };
		String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��", "�ֻ�ɱ��", "��������",
				"�߼�����", "��������" };
		private ImageView iv_itemhome_icon;
		private TextView tv_itemhome_text;
		//������Ŀ�ĸ���
		@Override
		public int getCount() {
			return 9;
		}
		//������Ŀ��ʾ����ʽ
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//�������ļ�ת��Ϊview����
			View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
			//ÿ����Ŀ�����ݶ���һ������ʼ���ؼ���ȥ���ÿؼ���ֵ
			//view.findViewByid()�Ǵ��Ӳ����ļ����ҿؼ���findviewByid()�Ǵ�activity_home���ҿؼ�
			iv_itemhome_icon = (ImageView) view.findViewById(R.id.iv_itemhome_icon);
			tv_itemhome_text = (TextView) view.findViewById(R.id.tv_itemhome_text);
			//���ÿؼ���ֵ
			iv_itemhome_icon.setImageResource(imageId[position]);
			tv_itemhome_text.setText(names[position]);
			return view;
		}
		//��ȡ��Ŀ��Ӧ������
		@Override
		public Object getItem(int position) {
			return null;
		}
		//��ȡ��Ŀ��Ӧ��id
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
	}
}
