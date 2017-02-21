package com.qianhuan.yxgsd.holagames.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holagame.tool.Logd;
import com.qianhuan.yxgsd.holagames.pay.LyUrlConstant;
import com.qianhuan.yxgsd.holagames.tools.LogUtils;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;
import com.qianhuan.yxgsd.holagames.tools.http.HttpUtil;
import com.qianhuan.yxgsd.holagames.tools.http.NetException;
import com.qianhuan.yxgsd.holagames.tools.http.SdkJsonReqHandler;

public class LyPayPassworldDialog extends Dialog{
	public Context context;
	private View mainView;
	private GridView gridView;
	private ArrayList<Integer> keyValue;
	private PassworldAdapter  adapter;
	private boolean isScreen = true;
	private ArrayList<String> passWorld;
	private String uid;
	private String access_token;
	private CheckPassworldResult result;
	private ProgressDialog orderDialog;
	
	private TextView passwrold_tv1,passwrold_tv2,passwrold_tv3,passwrold_tv4,passwrold_tv5,passwrold_tv6;
	public LyPayPassworldDialog(Context context, int theme,String uid,String access_token,CheckPassworldResult result) {
		super(context, theme);
		this.context = context;
		this.uid = uid;
		this.access_token = access_token;
		this.result = result;
		mainView = LayoutInflater.from(context).inflate(ResUtil.getLayoutId(context, "ilong_activity_pay_passworld"), null);
		InitView();
		InitData();
		setContentView(mainView);
	}
	private void InitView() {
		// TODO Auto-generated method stub
		gridView = (GridView) mainView.findViewById(ResUtil.getId(context, "passworld_input"));
		
	}
	
	private void InitData() {
		passwrold_tv1 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv1"));
		passwrold_tv2 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv2"));
		passwrold_tv3 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv3"));
		passwrold_tv4 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv4"));
		passwrold_tv5 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv5"));
		passwrold_tv6 = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_activity_passworld_tv6"));
		gridView.setNumColumns(3);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		setKeyValue(3);
		adapter = new PassworldAdapter();
		gridView.setAdapter(adapter);
		passWorld = new ArrayList<String>();
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int keyCode = Integer.parseInt(arg1.getTag().toString());
				if(keyCode == -1){
					if(passWorld.size() == 0) return;
					passWorld.remove(passWorld.size()-1);
				}else if(passWorld.size()<6 && keyCode<10){
					passWorld.add(keyCode+"");
				}
				 if(passWorld.size() == 6){
					checkPassworld(uid, getPassworld(), access_token);
				}
				changePassworld();
			}
		});
		
		mainView.findViewById(ResUtil.getId(context, "ilong_dialog_passworld_close")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	/**
	 * 设置横竖屏
	 * @param isLand 是否是横屏
	 */
	public void changeCol(boolean isLand) {
		gridView.setNumColumns(isLand?4:3);
		setKeyValue(isLand?4:3);
	}

	private void setKeyValue(int col) {
		keyValue = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			keyValue.add(i);
		}
		keyValue = randomList(keyValue);
		keyValue.add(((12/col)-1)*col, 10);
		keyValue.add(-1);
	}
	/**
	 * 打乱排序
	 * @param sourceList
	 * @return 打乱后的数组
	 */
	public ArrayList<Integer> randomList(ArrayList<Integer> sourceList){
		ArrayList<Integer> newlist = new ArrayList<Integer>(sourceList.size());
		do {
			int randomIndex = Math.abs(new Random().nextInt(sourceList.size()));
			newlist.add(sourceList.remove(randomIndex));
		} while (sourceList.size()>0);
			return newlist;
	}
	public class PassworldAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return keyValue.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int value = keyValue.get(position);
			TextView textView = new TextView(context);
			textView.setBackgroundResource(ResUtil.getDrawableId(context, "ilong_passworld_dialog_item_onclick"));
			textView.setTextSize(25f);
			textView.setText(value+"");
			textView.setTextColor(new Color().parseColor("#898989"));
			textView.setTag(value);
			if(value == 10){
				textView.setText("");
				textView.setTextSize(30f);
			}else if(value == -1){
				textView.setText("");
				textView.setBackgroundResource(ResUtil.getDrawableId(context, "ilong_passworld_dialog_delete_onclick"));
				textView.setTextSize(20f);
			}
			textView.setGravity(Gravity.CENTER);
			return textView;
		}
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		passWorld.clear();
		adapter.notifyDataSetChanged();
		changePassworld();
		Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(ResUtil.getStyleId(context, "pay_passworld_animation"));
		super.show();
	}
	
	public void checkPassworld(String uid,String password,String access_token){
		if(uid ==null || password == null || access_token==null || 
				uid.equals("") || password.equals("") || access_token.equals("")){
			Logd.e("getpaypassworld", "check params fail");
			return;
		}
		System.out.println("checkPassworld" + password);
		String url = LyUrlConstant.BASE_URL+LyUrlConstant.CHECK_PASSWORLD;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("password", password);
		params.put("access_token", access_token);
		showGetOrderDialog("验证密码中，请稍候....");
		HttpUtil.newHttpsIntance(context).httpsPostJSON(context, url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				// TODO Auto-generated method stub
				if(content!=null&&!content.equals("")){
					try {
						System.out.println(content);
						JSONObject object = new JSONObject(content);
						if(object.getInt("errno") == 200){
							result.onSuccess(content);
							dismiss();
						}else{
							result.onFail(content);
							ToastUtils.show(context, "支付密码错误");
							dissmissOrderDilaog();
						}
					} catch (JSONException e) {
						result.onFail(content);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				dissmissOrderDilaog();
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				if(reqObject!=null && slException!=null){
					result.onFail(reqObject.toString());
				}
			}
		});
		
	}
	
	private void changePassworld() {
		int passworldLength = passWorld.size();
		passwrold_tv1.setText(null);
		passwrold_tv2.setText(null);
		passwrold_tv3.setText(null);
		passwrold_tv4.setText(null);
		passwrold_tv5.setText(null);
		passwrold_tv6.setText(null);
		passwrold_tv1.setBackgroundColor(new Color().WHITE);
		passwrold_tv2.setBackgroundColor(new Color().WHITE);
		passwrold_tv3.setBackgroundColor(new Color().WHITE);
		passwrold_tv4.setBackgroundColor(new Color().WHITE);
		passwrold_tv5.setBackgroundColor(new Color().WHITE);
		passwrold_tv6.setBackgroundColor(new Color().WHITE);
		if(passworldLength>0){
			passwrold_tv1.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		if(passworldLength>1){
			passwrold_tv2.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		if(passworldLength>2){
			passwrold_tv3.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		if(passworldLength>3){
			passwrold_tv4.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		if(passworldLength>4){
			passwrold_tv5.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		if(passworldLength>5){
			passwrold_tv6.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_dialog_passworld"));
		}
		switch (passworldLength) {
		case 0:
			passwrold_tv1.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		case 1:
			passwrold_tv2.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		case 2:
			passwrold_tv3.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		case 3:
			passwrold_tv4.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		case 4:
			passwrold_tv5.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		case 5:
			passwrold_tv6.setBackgroundResource(ResUtil.getDrawableId(context,"ilong_passworld_radius_white_bg"));
			break;
		}
	}
	
	public String getPassworld(){
		String passworld = "";
		for (int i = 0; i < passWorld.size(); i++) {
			passworld+=passWorld.get(i);
		}
		return passworld;
	}
	
	public interface CheckPassworldResult{
		void onSuccess(String msg);
		void onFail(String msg);
	}
	/**
	 * 显示获取订单
	 */
	private void showGetOrderDialog(String message) {
		if (orderDialog == null) {
			orderDialog = new ProgressDialog(context);
			orderDialog.setCancelable(false);
			orderDialog.setCanceledOnTouchOutside(false);
		}
		orderDialog.setMessage(message);
		orderDialog.show();
	}
	
	/**
	 * 隐藏获取订单
	 */
	private void dissmissOrderDilaog() {
		try {
			if (orderDialog != null && orderDialog.isShowing()) {
				orderDialog.dismiss();
				orderDialog = null;
			}
		} catch (Exception e) {
			LogUtils.error(e);
		}
	}
}
