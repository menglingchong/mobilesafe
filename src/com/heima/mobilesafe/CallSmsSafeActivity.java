package com.heima.mobilesafe;

import java.util.List;

import com.heima.mobilesafe.bean.BlackNumInfo;
import com.heima.mobilesafe.db.dao.BlackNumDao;
import com.heima.mobilesafe.utils.MyAsynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {

	private ListView lv_callsmssafe_contacts;
	private ProgressBar pb_callsmssafe_loading;
	private List<BlackNumInfo> list;
	private BlackNumDao blackNumDao;
	private MyAdapter myAdapter;
	private AlertDialog dialog;
	private final int MaxNum = 20;//��ѯ���ܸ���
	private int startindex =0;//��ѯ����ʼλ��
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);
		blackNumDao = new BlackNumDao(getApplicationContext());
		lv_callsmssafe_contacts = (ListView) findViewById(R.id.lv_callsmssafe_contacts);
		pb_callsmssafe_loading = (ProgressBar) findViewById(R.id.pb_callsmssafe_loading);
		//���߳���ִ�к�ʱ�����������ݿ��в��Һ�����
		fillData();
		//listView�Ļ��������¼�
		
		lv_callsmssafe_contacts.setOnScrollListener(new OnScrollListener() {
			//����״̬�ı�ʱ���ø÷���
			//scrollState:����״̬
			//SCROLL_STATE_IDLE:�������ڿ���״̬
			//SCROLL_STATE_FLING:���ڿ��ٻ���
			//SCROLL_STATE_TOUCH_SCROLL:��������
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//��listView��ֹ��ʱ���ж�listview�Ƿ��ǲ�ѯ���ݵ����һ����Ŀ���Ǿͼ�����һ�����ݣ����ǵĻ����û����������Ĳ���
				if (scrollState==OnScrollListener.SCROLL_STATE_IDLE) {
					//��ȡ������ʾ���һ����Ŀ
					int position = lv_callsmssafe_contacts.getLastVisiblePosition();//��ȡ������ʾ���һ����Ŀ��������Ŀ��λ�� 
					//�жϽ�����ʾ�����һ����Ŀ�Ƿ������һ������
					if (position == list.size()-1) {
						//������һ������
						startindex+= MaxNum;//����ʼ�Ĳ�ѯλ�ý��и���
						fillData();
					}
				}
			}
			//����ʱ���ø÷���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}
	/**
	 * ���߳��в��Һ�����,ʹ���첽���ؿ��
	 */
	private void fillData() {
		
		new MyAsynctask() {

		
			//���߳�ִ��֮ǰ���ø÷���
			@Override
			public void preTask() {
				pb_callsmssafe_loading.setVisibility(View.VISIBLE);//��ʾprogressbar
			}
			//���߳�ִ��֮����ø÷���
			@Override
			public void postTask() {
				//���ݼ������֮�󣬸���UI�Ĳ���
				
				if (myAdapter==null) { //��һ�μ��ص�ʱ���listview����adapter
					myAdapter = new MyAdapter();
					lv_callsmssafe_contacts.setAdapter(myAdapter);
				} else {
					myAdapter.notifyDataSetChanged();//���ǵ�һ�μ��أ�ֱ�Ӹ���������
				}
				
				pb_callsmssafe_loading.setVisibility(View.INVISIBLE);//���ݼ������֮������progressbar
				
			}
			//���߳�ִ�й����е��ø÷���
			@Override
			public void doInBack() {
				if (list ==null) {
					list = blackNumDao.querryPartNum(MaxNum, startindex);
				}else {
					list.addAll(blackNumDao.querryPartNum(MaxNum, startindex));//��һ��������ӵ���һ��������
				}
			}
		}.execute();
	}
	//��������������
	private class MyAdapter extends BaseAdapter{
		private View view;
		private ViewHolder viewHolder;
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
		//��ȡ��Ŀ����ʽ
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//������listview���û���
			if (convertView ==null) {
				view = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
				viewHolder = new ViewHolder();
				//���ؼ��ŵ�������
				viewHolder.tv_itemcallsmssafe_blacknum =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
				viewHolder.tv_itemcallsmssafe_mode =(TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
				viewHolder.iv_itemcallsmssafe_delete =(ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
				//��������view����󶨵�һ��
				view.setTag(viewHolder);
			}else {
				view =convertView;
				//��view�����еõ��ؼ�������
				viewHolder = (ViewHolder) view.getTag();
			}
			
//			TextView tv_itemcallsmssafe_blacknum = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_blacknum);
//			TextView tv_itemcallsmssafe_mode = (TextView) view.findViewById(R.id.tv_itemcallsmssafe_mode);
//			ImageView iv_itemcallsmssafe_delete = (ImageView) view.findViewById(R.id.iv_itemcallsmssafe_delete);
			//������ʾ����
			final BlackNumInfo blackNumInfo = list.get(position);//��ȡ��Ӧ��Ŀ��bean����
			viewHolder.tv_itemcallsmssafe_blacknum.setText(blackNumInfo.getBlacknum());
//			tv_itemcallsmssafe_mode.setText(blackNumInfo.getMode()+"");
			int mode = blackNumInfo.getMode();
			switch (mode) {
			case BlackNumDao.CALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("�绰����");
				break;
			case BlackNumDao.SMS:
				viewHolder.tv_itemcallsmssafe_mode.setText("��������");
				break;
			case BlackNumDao.ALL:
				viewHolder.tv_itemcallsmssafe_mode.setText("ȫ������");
				break;
			}
			/**
			 * ɾ��������
			 */
			viewHolder.iv_itemcallsmssafe_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//ɾ������������
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("ɾ��");
					builder.setMessage("��ȷ��Ҫɾ��:"+blackNumInfo.getBlacknum()+"��?");
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//ɾ�����������ȴ����ݿ���ɾ����Ȼ����ɾ��list�����еĽ����ϵ���Ŀ
							//1.ɾ�����ݿ��еĺ�����
							blackNumDao.delBlackNum(blackNumInfo.getBlacknum());
							//2.ɾ��list�����еĺ�������ɾ����Ŀ��Ӧλ�õĺ�����
							list.remove(position);
							//3.���½��棬���������ĸ���
							myAdapter.notifyDataSetChanged();
							//4.���ضԻ���
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});
			return view;
		}
	}
	/**
	 * ������ſؼ�����
	 * @author lenovo
	 *
	 */
	static class ViewHolder{
		TextView tv_itemcallsmssafe_blacknum,tv_itemcallsmssafe_mode;
		ImageView iv_itemcallsmssafe_delete;
	}
	/**
	 * ��Ӻ�����
	 */
	public void addBlackNum(View v){
		AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
		View view = View.inflate(getApplicationContext(), R.layout.dialog_addblacknum, null);
		//��ʼ���ؼ�
		Button btn_addblacknum_cancle = (Button) view.findViewById(R.id.btn_addblacknum_cancle);
		Button btn_addblacknum_ok = (Button) view.findViewById(R.id.btn_addblacknum_ok);
		final TextView et_addblacknum_num = (TextView) view.findViewById(R.id.et_addblacknum_num);
		final RadioGroup rg_addblacknum_check = (RadioGroup) view.findViewById(R.id.rg_addblacknum_check);
		btn_addblacknum_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��Ӻ�����
				//1.��ȡ����ĺ���������
				String number = et_addblacknum_num.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "����������Ϊ�գ����������룡", 0).show();
					return;
				}
				//2.��ȡ������������ģʽ
				int mode =-1;
				int radioButtonId = rg_addblacknum_check.getCheckedRadioButtonId();//��ȡRadioGroup��ѡ���buttonId
				switch (radioButtonId) {
				case R.id.rb_addblacknum_call://��������������ģʽ����Ϊ�绰����
					mode = BlackNumDao.CALL;
					break;
				case  R.id.rb_addblacknum_sms://��������������ģʽ����Ϊ��������
					mode = BlackNumDao.SMS;
					break;
				case R.id.rb_addblacknum_all://��������������ģʽ����Ϊȫ������
					mode = BlackNumDao.ALL;
					break;
				}
				//3.�����������������ģʽ����
				//3.1.���浽���ݿ�
				blackNumDao.addBlackNum(number, mode);
				//3.2���浽list�����в���ʾ�ڽ���
//				list.add(new BlackNumInfo(number, mode));//��ӵ���������¶ˣ����鲻��
				list.add(0, new BlackNumInfo(number, mode));//��ӵ������������
				//4.���½���
				myAdapter.notifyDataSetChanged();
				//���ضԻ���
				dialog.dismiss();
			}
		});
		btn_addblacknum_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���ضԻ���
				dialog.dismiss();
			}
		});
		
		builder.setView(view);
		
        dialog = builder.create();
		dialog.show();
	}
	
}
