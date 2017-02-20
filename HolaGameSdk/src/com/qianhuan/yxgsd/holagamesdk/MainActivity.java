package com.qianhuan.yxgsd.holagamesdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.holagame.tool.Gamer;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.i.ILongExitCallback;
import com.holagames.xcds.i.ILongInitCallback;
import com.holagames.xcds.i.ILongPayCallback;
import com.holagames.xcds.i.IToken2UserInfo;
import com.holagames.xcds.i.IlongLoginCallBack;
import com.holagames.xcds.modle.UserInfo;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.http.Constant;
import com.qianhuan.yxgsd.holagamesdk.tools.http.AccessTokenModel;
import com.qianhuan.yxgsd.holagamesdk.tools.http.HttpUtil;
import com.qianhuan.yxgsd.holagamesdk.tools.http.Json;
import com.qianhuan.yxgsd.holagamesdk.tools.http.NetException;
import com.qianhuan.yxgsd.holagamesdk.tools.http.RespModel;
import com.qianhuan.yxgsd.holagamesdk.tools.http.SdkJsonReqHandler;

/**
 * sdk入口activity
 * @author 小魏 
 * @Email: wlcaption@gmail.com
 * 2017-2-13 下午5:10:29
 */
public class MainActivity extends Activity {
	private static String authorizationCode = "";
	public static final String TAG = "MainActivity";
//	TextView log;
//	ScrollView mScrollView; 
	int lognum=1;
	
	public static String objectName = "VOEZMAIN";
	
	private boolean isExit = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this, "activity_demo_main"));
		
		doInit();
		doInitView();
		

	}
	
	
	//初始化回调
	public ILongInitCallback mInitCallback = new ILongInitCallback() {

		@Override
		public void onSuccess() {
			// 初始化成功
			Log.e(TAG, "init success");
		}

		@Override
		public void onFailed() {
			// 初始化失败
			Log.e(TAG, "init failed");
		}
	};
	
	//登录回调
	public IlongLoginCallBack mLoginCallback = new IlongLoginCallBack() {

		@Override
		public void onSuccess(String auth_code) {
			// 登录成功，获得auth_code，此时应该换取token
			authorizationCode = auth_code;
			getToken(authorizationCode);
			Log.e(TAG, "login success" + ", " + auth_code);
		}

		@Override
		public void onLogout() {
			// 如果会话失效，会调用此回调，此时游戏应当回到登录界面
			Log.e(TAG, "logout");
		}

		@Override
		public void onFailed(String msg) {
			// 登录失败
			Log.e(TAG, "login failed");
		}

		@Override
		public void onCancel() {
			// 取消登录的回调
			Log.e(TAG, "login cancel");
		}

		@Override
		public void onSwitchAccount(String auth_code) {
			// 切换账号的回调
			Log.e(TAG, "onSwitchAccount" + ", " + auth_code);
			getToken(auth_code);
		}
	};
	
	//支付回调
	public ILongPayCallback mPayCallback = new ILongPayCallback() {

		@Override
		public void onSuccess() {
			// 支付成功的回调
			Log.e(TAG, "pay success");
		}

		@Override
		public void onFailed() {
			// 支付失败的回调
			Log.e(TAG, "pay failed");
		}

		@Override
		public void onCancel() {
			// 支付取消的回调
			Log.e(TAG, "pay cancel");
		}
	};
	
	
	//退出回调
	public ILongExitCallback mExitCallback = new ILongExitCallback() {

		@Override
		public void onExit() {
			// 退出登录的回调,此时游戏需要关闭游戏界面
			Log.e(TAG, "exit success");
			finish();
		}
	};
	
	private void doInit(){
		//初始化
		IlongSDK.getInstance().setDebugModel(true)// 是否是debug模式
		.setBackEable(true)// 登录界面是否响应返回键，默认true
		.setShowLoginView(true) //是否显示自动登录界面  默认为true
		.init(this, mInitCallback, mLoginCallback, mPayCallback, mExitCallback);
		
		//设置用户在游戏里面的信息
		IlongSDK.getInstance().setUserInfo(getUserInfo());
	}
	/**	
 	 *游戏区服    String gameService;  </br>
	 * 用户的游戏ID  String userId</br>
	 * 角色名称  String roleName</br>
	 * */
	private String getUserInfo(){
		String userInfo = "";
		try {
			JSONObject json = new JSONObject();
			json.put("gameService", "33区");
			json.put("userId", "ly_10001");
			json.put("roleName", "roleName");
			userInfo = json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	
	private void doInitView(){
		findViewById(ResUtil.getId(this, "btn_login")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IlongSDK.getInstance().setShowLoginView(true);
				doLogin();
			}
		});
		findViewById(ResUtil.getId(this, "btn_login_hide")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				IlongSDK.getInstance().setShowLoginView(false);
				doLogin();

			}
		});
		findViewById(ResUtil.getId(this, "btn_pay")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				doPay();
				getAmount();
			}
		});
		findViewById(ResUtil.getId(this, "btn_exit")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isExit){
					doExit();
				}else{
					isExit = true;
				}
				
			}
		});

		findViewById(ResUtil.getId(this, "btn_logout")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IlongSDK.getInstance().logout();
			}
		});
		
		findViewById(ResUtil.getId(this, "btn_exit")).setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				isExit = false;
				IlongSDK.getInstance().deleteCache();
				Toast.makeText(MainActivity.this, "清除数据成功",Toast.LENGTH_LONG).show();
				return false;
			}
		});
		
		findViewById(ResUtil.getId(this, "btn_showuser")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				IlongSDK.getInstance().showUserCenter();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void doExit() {
		IlongSDK.getInstance().exitSDK();
	}
	String mAmount = "0.01";
	public void getAmount(){
		
		try {
			final Dialog dialog = new Dialog(this);
			View view = LayoutInflater.from(this).inflate(ResUtil.getLayoutId(this, "view_amount"), null);
			dialog.setContentView(view);
			final EditText edit = (EditText) view.findViewById(ResUtil.getId(this, "edit"));
			Button btn = (Button) view.findViewById(ResUtil.getId(this, "btn"));
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mAmount = edit.getText().toString().trim();
					if(mAmount == null || mAmount.length() == 0) mAmount = "0.01";
					dialog.cancel();
					doPay();
				}
			});
			dialog.setTitle("充值");
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void doPay() {
		
		
		String amount = mAmount;
		// 订单号由游戏内部生成， 订单号不可重复提交
		String orderId = "orderId" + Math.random();
		// 商品名称
		String productName = "钻石";
		// 游戏内部的角色id
		String appUid = "game_user_id";
		// 回调地址
		String notifyUrl = "http://api.sandbox.test.ilongyuan.cn/api/pay/notify_test";
		// 商品id
		String product_id = "product_id";
		// 游戏内部的角色昵称
		String app_username = "app_username";
		startPay(amount, orderId, appUid, notifyUrl, productName, product_id,
				app_username);
	}

	private void startPay(final String amount, final String orderId,
			String app_uid, String notify_uri, String product_name,
			String product_id, String app_username) {
		Bundle bundle = new Bundle();
		bundle.putString("amount", amount);// 金额，两位小数的浮点数，如1分钱：1.01；1元钱：1.00
		bundle.putString("app_order_id", orderId);// 订单号
		bundle.putString("app_uid", app_uid);// 角色id
		bundle.putString("notify_uri", notify_uri);// 支付成功之后游戏接入放的需要填入的回调地址
		bundle.putString("product_name", product_name);// 游戏内部商品名称
		bundle.putString("product_id", product_id);// 游戏内部商品id
		bundle.putString("app_username", app_username);// 游戏内部角色昵称

		IlongSDK.getInstance().pay(bundle);
	}

	public void doLogin() {
		IlongSDK.getInstance().login();
	}

	/**
	 * 实现获取TOKEN
	 * 
	 * @param code
	 */
	public void getToken(String code) {
		System.out.println("code:"+code);
		
		String url = "http://api.sandbox.test.ilongyuan.cn/api/test/login";
//		if(! Constant.isRelease)
//			url = "http://115.29.149.69/api/test/login";
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("auth_code", code);
		if(Constant.isRelease){
		params.put("version", "release");		
		}
		Log.e(TAG, url + ", " + params.toString());
		HttpUtil.newHttpsIntance(MainActivity.this).httpsPost(
				MainActivity.this, url, params, new SdkJsonReqHandler(params) {

					@Override
					public void ReqYes(Object reqObject, final String content) {
						Log.e("auth_code111", content);
						try {
							RespModel respModel = Json.StringToObj(content,RespModel.class);
							AccessTokenModel tokenModel = Json.StringToObj(respModel.getData(), AccessTokenModel.class);
							String token = tokenModel.getAccess_token();
							if (token != null) {
								// CODE换TOKEN成功
//								Log.d("gst", "token-->"+token);
								IlongSDK.getInstance().setUserToken(MainActivity.this, token,new IToken2UserInfo() {

											@Override
											public void onSuccess(UserInfo userinfo) {
												IlongSDK.getInstance().showFloatView();
												// 进入游戏
												Log.d("tag", "用户id：" + userinfo.getId());
												Toast.makeText(MainActivity.this, "登录成功，用户id是" + userinfo.getId(),Toast.LENGTH_LONG).show();
											}

											@Override
											public void onFailed() {
												Toast.makeText(MainActivity.this, "登录失败",Toast.LENGTH_LONG).show();
											}
										});
							} else {
								Toast.makeText(MainActivity.this, "登录失败",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							Toast.makeText(MainActivity.this, "登录失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}

					@Override
					public void ReqNo(Object reqObject, NetException slException) {
						Toast.makeText(MainActivity.this,
								"登录失败," + slException.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// onPause时候调用，关闭悬浮窗
		IlongSDK.getInstance().onPause();
		Gamer.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// onResume时候调用, 显示悬浮窗
		IlongSDK.getInstance().onResume();
		Gamer.onResume();
	}

}
