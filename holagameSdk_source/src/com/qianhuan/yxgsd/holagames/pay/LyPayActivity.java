package com.qianhuan.yxgsd.holagames.pay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.ac.ActivityWeb;
import com.qianhuan.yxgsd.holagames.dialog.IlongBasicDialog;
import com.qianhuan.yxgsd.holagames.dialog.LyPayPassworldDialog;
import com.qianhuan.yxgsd.holagames.dialog.PayEixtDialog;
import com.qianhuan.yxgsd.holagames.dialog.PayResultDialog;
import com.qianhuan.yxgsd.holagames.enums.PayResultType;
import com.qianhuan.yxgsd.holagames.modle.LyPayOrder;
import com.qianhuan.yxgsd.holagames.modle.OrderNumber;
import com.qianhuan.yxgsd.holagames.modle.PayActivities;
import com.qianhuan.yxgsd.holagames.modle.RespModel;
import com.qianhuan.yxgsd.holagames.modle.UserInfo;
import com.qianhuan.yxgsd.holagames.pay.alipay.HolaAlipay;
import com.qianhuan.yxgsd.holagames.pay.alipay.LyAlipay;
import com.qianhuan.yxgsd.holagames.pay.tenpay.LyCftPayActivity;
import com.qianhuan.yxgsd.holagames.pay.uppay.LyUPPay;
import com.qianhuan.yxgsd.holagames.pay.weixinpay.WXPay;
import com.qianhuan.yxgsd.holagames.pay.weixinpay.WXPay.WXPayResultCallBack;
import com.qianhuan.yxgsd.holagames.tools.IlongCode;
import com.qianhuan.yxgsd.holagames.tools.Json;
import com.qianhuan.yxgsd.holagames.tools.LogUtils;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;
import com.qianhuan.yxgsd.holagames.tools.http.DelayJsonHttpResponseHandler;
import com.qianhuan.yxgsd.holagames.tools.http.HttpUtil;
import com.qianhuan.yxgsd.holagames.tools.http.NetException;
import com.qianhuan.yxgsd.holagames.tools.http.SdkJsonReqHandler;

public class LyPayActivity extends Activity implements OnClickListener{
	public static final String M_ALIPAY = "支付宝";
	public static final String M_TEN = "财付通";
	public static final String M_UNIN = "银联";
	public static final String M_LONGYUAN = "龙渊币";
	public static final String M_HRPAY = "浩然币";
	private float ly_amount = 0f;
	private String product_name = "";
	private String uid = "";
	private float total_amount = 0f;
	private String app_order_id = "";
	private String app_uid = "";
	private String product_id = "";
	private String app_username = "";
	private String access_token = "";
	private String notify_uri = "";
	private String pack_key="";
	
	private String Tag = this.getClass().getSimpleName();
	private View messageLayout;//消息布局
	private View orderNumberLayout;//订单信息
	private View lyCurrencyLayout;//龙渊币详情
	private View payModeLayout;//支付方式
	private Button pay_bt;//支付
	
	private ImageButton backe_bt;//返回
	
	private TextView message_tv;//消息正文
	
	private TextView order_number_tv;//订单金额
	private TextView common_number_tv;//商品信息
	
	private TextView ly_number_info_tv;//龙渊币信息
	private ImageView choose_ly_bt;//选择龙渊币
	private ImageView ly_number_refresh;//刷新龙渊币
	
	private TextView coin_full_pormpt;//龙渊币足够提示
	
	private View alipay_mode_rl;
	private View union_mode_rl;
	private View ten_mode_rl;
	
	private LyPayPassworldDialog lyPayPassworld;
	private ProgressDialog orderDialog;
	private IlongBasicDialog GoWebLongBReCharge; 
	
	private LyPayOrder lyPayOrder;
	
	private boolean isCharge = false;//是否使用混合支付
	private CountDownTimer timer;
	private boolean isLand = true;
	
	private PayResultDialog payresultdialog;
	
	private PayActivities payNotice;
	
//	private PayResultType pay_state=PayResultType.FAIL_PAY;//当前支付状态 0.支付成功  1.支付失败  2.支付取消
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_pay"));
		InitScreen(); 
		InitView();
		InitData();
		updataUserInfo(false,0);//刷新龙渊币金额
	}

	/**
	 * 确定屏幕方向
	 */
	private void InitScreen() {
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {    
		       Log.i("info", "landscape");   
		       isLand = true;
		 } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {    
		       Log.i("info", "portrait");  
		       isLand = false;
		 }   
	}

	private void InitView() {
		messageLayout = findViewById(ResUtil.getId(this, "ilong_activity_pay_message"));
		orderNumberLayout = findViewById(ResUtil.getId(this, "ilong_activity_pay_order_number"));
		lyCurrencyLayout = findViewById(ResUtil.getId(this, "ilong_activity_pay_ly"));
		payModeLayout = findViewById(ResUtil.getId(this, "ilong_activity_pay_mode"));
		pay_bt = (Button) findViewById(ResUtil.getId(this, "ilong_activity_pay_goto"));
		
		backe_bt = (ImageButton) findViewById(ResUtil.getId(this, "ilong_activity_pay_back"));
		
		message_tv = (TextView) findViewById(ResUtil.getId(this, "ilong_activity_pay_context"));
		
		order_number_tv = (TextView) findViewById(ResUtil.getId(this, "ilong_order_number"));
		common_number_tv = (TextView) findViewById(ResUtil.getId(this, "ilong_activity_pay_common_number"));
		
		ly_number_info_tv = (TextView) findViewById(ResUtil.getId(this, "ilong_activity_pay_ly_info"));
		choose_ly_bt = (ImageView) findViewById(ResUtil.getId(this, "ilong_activity_pay_ly_choose"));
		ly_number_refresh = (ImageView) findViewById(ResUtil.getId(this, "ilong_activity_pay_ly_refresh"));
		coin_full_pormpt = (TextView) findViewById(ResUtil.getId(this, "ilong_activity_prompt_coin"));
		
		alipay_mode_rl = (View) findViewById(ResUtil.getId(this, "ilong_activity_pay_alipay_rl"));
		union_mode_rl = (View) findViewById(ResUtil.getId(this, "ilong_activity_pay_union_rl"));
		ten_mode_rl = (View) findViewById(ResUtil.getId(this, "ilong_activity_pay_ten_rl"));
	}

	private void InitData() {
		payNotice = IlongSDK.getInstance().packInfoModel.getRecharge_active();
		getIntentData();//获取传入的支付信息
		payresultdialog = new PayResultDialog(LyPayActivity.this, PayResultType.FAIL_PAY);
		payresultdialog.setLyCoin((int)ly_amount);
		pay_bt.setOnClickListener(this);
		backe_bt.setOnClickListener(this);
		order_number_tv.setText("订单金额  ： "+total_amount+"元");
		common_number_tv.setText("商品 : "+product_name);
		setLyNuberInfo();
		choose_ly_bt.setOnClickListener(this);
		ly_number_refresh.setOnClickListener(this);
		
		alipay_mode_rl.setOnClickListener(this);
		union_mode_rl.setOnClickListener(this);
		ten_mode_rl.setOnClickListener(this);
		setPayIsSelected();//取消所有支付选中状态
		alipay_mode_rl.setSelected(true);
		setPayMode();//设置支付显示
		int theme = ResUtil.getStyleId(LyPayActivity.this, "security_dialog");
		lyPayPassworld = new LyPayPassworldDialog(LyPayActivity.this,theme,uid,access_token,new LyPayPassworldDialog.CheckPassworldResult() {
			
			@Override
			public void onSuccess(String msg) {
				initPayInfo();
			}
			
			@Override
			public void onFail(String msg) {
				Logd.d(Tag, msg);
			}
		});
	setNotice();
	}
	/**
	 * 设置消息是否显示
	 */
	private void setNotice() {
		if(payNotice==null|| payNotice.getContent().isEmpty()
				|| !payNotice.getStatus().equals("1")){
			messageLayout.setVisibility(View.GONE);
			return;
		}else{
			message_tv.setText(payNotice.getContent());
		}
	}

	/**
	 * 组织参数，支付
	 */
	private void initPayInfo() {
		// TODO Auto-generated method stub
		int orderType = getOrderType();
		float amount = total_amount;
		if(orderType!=3){
			isCharge = true;
			amount = (total_amount*100 - ly_amount*100)/100;
		}else{
			isCharge = false;
		}
		
		if (lyPayOrder == null) {
			lyPayOrder = new LyPayOrder(pack_key, amount*100+"", app_order_id, app_uid, notify_uri, product_name, product_id, app_username, access_token);
		}
		
		createOrderNumber(orderType, lyPayOrder, lyPayPassworld.getPassworld(),amount+"",(orderType!=3)?"recharge":null);
	}
	
	/**
	 * 获取支付方式，前提条件是勾选了出龙渊币支付外的方式
	 * @return 获取支付方式
	 */
	private int getOrderType() {
		int orderType = 3;
		if(payModeLayout.getVisibility() == View.VISIBLE){
			if(alipay_mode_rl.isSelected()){
				orderType = 0;
			}else if(union_mode_rl.isSelected()){
				orderType = 1;
			}else if(ten_mode_rl.isSelected()){
				orderType = 2;
			}
		}
		return orderType;
	}

	/**
	 * 设置支付按钮
	 * @param total 一共需要付多少
	 * @param ly_amount 龙渊币扣除多少
	 */
	private void setOverPay() {
		if(choose_ly_bt.isSelected()&&ly_amount<total_amount){
			float amount = (total_amount*100-ly_amount*100)/100;
			pay_bt.setText(Html.fromHtml("还需支付<font size = '"+DeviceUtil.convertspTopx(this, 30f)+"' color = '#ffb769'>"+amount+"</font>元，去支付"));
		}else{
			if(choose_ly_bt.isSelected()){
				pay_bt.setText("立即支付");
			}else{
				pay_bt.setText(Html.fromHtml("立即支付<font size = '"+DeviceUtil.convertspTopx(this, 30f)+"' color = '#ffb769'>"+total_amount+"</font>元"));
			}
		}
	}
	
	/**
	 * 设置支付显示初始化的时候设置
	 */
	private void setPayMode() {
		
		if(ly_amount<=0){
			lyCurrencyLayout.setVisibility(View.GONE);
			setLyChosse(false);
			setOverPay();
		}else if(ly_amount>=total_amount){
			payModeLayout.setVisibility(View.VISIBLE);
			if(!isLand){
				payModeLayout.setVisibility(View.GONE);
			}else{
				payModeLayout.setFocusable(false);
				setPayIsSelected();
			}
			coin_full_pormpt.setVisibility(View.VISIBLE);
			setLyChosse(true);
			setOverPay();
		}else{
			lyCurrencyLayout.setVisibility(View.VISIBLE);
			payModeLayout.setFocusable(true);
			payModeLayout.setVisibility(View.VISIBLE);
			coin_full_pormpt.setVisibility(View.GONE);
			setLyChosse(true);
			setOverPay();
		}
	}

	/**
	 * 获取传入的数据
	 */
	private void getIntentData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		this.total_amount= Float.parseFloat(intent.getStringExtra("amount"));
		this.ly_amount = Float.parseFloat(IlongSDK.getInstance().mUserInfo.getMoney());
		this.pack_key=IlongSDK.getInstance().getSid();
		this.uid = bundle.getString("uid");
		this.app_order_id = bundle.getString("app_order_id");
		this.app_uid = bundle.getString("app_uid");
		this.notify_uri = bundle.getString("notify_uri");
		this.product_name = bundle.getString("product_name");
		this.product_id = bundle.getString("product_id");
		this.app_username = bundle.getString("app_username");
		this.access_token = bundle.getString("access_token");
	}

	/**
	 * 取消选中状态
	 */
	private void setPayIsSelected() {
		// TODO Auto-generated method stub
		alipay_mode_rl.setSelected(false);
		union_mode_rl.setSelected(false);
		ten_mode_rl.setSelected(false);
	}
	/**
	 * 设置选择的icon
	 */
	private void setLyChosse(boolean isSelected) {
		choose_ly_bt.setSelected(isSelected);
		choose_ly_bt.setImageResource(ResUtil.getDrawableId(this, 
				choose_ly_bt.isSelected()?"ilong_activity_pay_ly_sure_selected":"ilong_activity_pay_ly_sure"));
	}

	/**
	 * 设置龙渊币数量
	 */
	private void setLyNuberInfo() {
		ly_number_info_tv.setText(Html.fromHtml("使用龙渊币：<font color = '#ff722c'>"+(int)ly_amount+"</font>"));
	}
	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		//去支付
		if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_goto")){
			fixedScreen();
			if(choose_ly_bt.isSelected()){
				payresultdialog.setResultType(PayResultType.FAIL_PAY);
				payresultdialog.setPayState(PayResultType.FAIL_PAY);
				lyPayPassworld.changeCol(isLand);
				if("0".equals(DeviceUtil.getData(LyPayActivity.this, IlongSDK.mUserInfo.getId()+"haspay_pwd"))){
					goSetPWDDialog();
				}else{
					lyPayPassworld.show();
				}
			}else{
				payresultdialog.setLyCoin((int)ly_amount);
				if (lyPayOrder == null) {
					lyPayOrder = new LyPayOrder(pack_key, total_amount*100+"", app_order_id, app_uid, notify_uri, product_name, product_id, app_username, access_token);
				}
				createOrderNumber(getOrderType(), lyPayOrder, "", total_amount+"", null);
			}
		//返回
		}else if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_back")){
			exit();
		//龙渊币勾选
		}else if(viewId == ResUtil.getId(this, "ilong_activity_pay_ly_choose")){
			if(choose_ly_bt.isSelected()){//判断是否在勾选状态
				if(!getIschoosePayMode()){//是否有勾选项
					alipay_mode_rl.setSelected(true);
				}
				payModeLayout.setFocusable(true);
				payModeLayout.setVisibility(View.VISIBLE);
				coin_full_pormpt.setVisibility(View.GONE);
			//即将变成勾选
			}else{
				//判断金额支付账户金额是否大于支付金额
				if(ly_amount>=total_amount){
					//判断是否是横屏
					if(!isLand){
						payModeLayout.setVisibility(View.GONE);
					}else{
						setPayIsSelected();
						payModeLayout.setFocusable(false);
					}
					coin_full_pormpt.setVisibility(View.VISIBLE);
				}else{
					if(!getIschoosePayMode()){
						alipay_mode_rl.setSelected(true);
					}
					payModeLayout.setVisibility(View.VISIBLE);
				}
				
			}
			setLyChosse(!choose_ly_bt.isSelected());
			setOverPay();
		//龙渊币信息刷新
		}else if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_ly_refresh")){
			 Animation animation = AnimationUtils.loadAnimation(LyPayActivity.this,
					 ResUtil.getAnimationID(LyPayActivity.this, "loading"));
			 view.startAnimation(animation);
			 updataUserInfo(false,3000);
		//支付宝支付
		}else if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_alipay_rl")){
			if(!payModeLayout.isFocusable()) return;
				setPayIsSelected();
				alipay_mode_rl.setSelected(true);
		//银联支付
		}else if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_union_rl")){
			if(!payModeLayout.isFocusable()) return;
			setPayIsSelected();
			union_mode_rl.setSelected(true);
		//财付通支付
		}else if(viewId == ResUtil.getId(LyPayActivity.this, "ilong_activity_pay_ten_rl")){
			if(!payModeLayout.isFocusable()) return;
			setPayIsSelected();
			ten_mode_rl.setSelected(true);
		}
		
	}
	
	/**
	 * 生成订单
	 * @param orderType
	 * @param lyPayOrder
	 */
	private void createOrderNumber(final int orderType, LyPayOrder lyPayOrder, final String password,final String amount,String recharge) {
		isCharge = recharge != null;
		showGetOrderDialog("订单获取中，请稍候....");
		//String url = LyUrlConstant.BASE_URL + LyUrlConstant.PAY_ORDER;
		String url = "http://139.129.21.196/hola_sdk_server/pay/create_order.php";
		final Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("pack_key", lyPayOrder.getPack_key());
		//params.put("amount", (int)(Float.parseFloat(amount)*100));
		params.put("amount", amount);
		String app_order_id = lyPayOrder.getApp_order_id();
		params.put("app_order_id",lyPayOrder.getApp_order_id()+(recharge==null?"":"recharge"));
		params.put("app_uid", lyPayOrder.getApp_uid());
		params.put("notify_uri", lyPayOrder.getNotify_uri());
		params.put("product_name", lyPayOrder.getProduct_name());
		params.put("product_id", lyPayOrder.getProduct_id());
		params.put("app_username", lyPayOrder.getApp_username());
		params.put("access_token", lyPayOrder.getAccess_token());
		params.put("platform", "android");
		params.put("game_name", "10001");
		if (orderType == 0) {
			params.put("channel", "alipayquick");
		} else if (orderType == 1) {
			params.put("channel", "wechatpay");
		} else if (orderType == 2) {
			params.put("channel", "ten");
		}else if (orderType == 3) {
			params.put("channel", "ilypay");
		}
		if (!TextUtils.isEmpty(IlongSDK.getInstance().getSid())){
			params.put("pack_key", IlongSDK.getInstance().getSid());
		}
//		Gamer.sdkCenter.setpayment("", "", "CNY", params.get("channel").toString(), Float.parseFloat(amount)*100);
//		Log.d("gst", "生成订单的url-->"+url);
		System.out.println("生成订单："+JSON.toJSON(params));
		HttpUtil.newHttpsIntance(this).httpsPost(this, url, params, new SdkJsonReqHandler(params) {

			@Override
			
			public void ReqYes(Object reqObject, final String content) {
				try {
					dissmissOrderDilaog();
					RespModel respModel = Json.StringToObj(content, RespModel.class);
					if (respModel.getErrno() == 200) {
						JSONObject dataObject=JSONObject.parseObject(respModel.getData());
						OrderNumber orderNumber = Json.StringToObj(respModel.getData(), OrderNumber.class);
						orderNumber.getOut_trade_no();
						if (null == orderNumber || null == orderNumber.getOut_trade_no() || orderNumber.getOut_trade_no().isEmpty()) {
							onMakeOrderFailed();
						} else{
//							Gamer.sdkCenter.paymentSuccess(IlongSDK.AccountId, (String)params.get("app_order_id"), "unknown", amount, "unknown", orderType+"");					    
                            //pay_info这个字段是新加的，只有当支付宝支付是才会用其它时候为空						    
							doStartPay(dataObject.getString("pay_info"),orderType, orderNumber.getOut_trade_no(), password,amount);
						}
					} else {
						onMakeOrderFailed();
					}
				} catch (Exception e) {
					e.printStackTrace();
					onMakeOrderFailed();
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				Log.e(TAG, slException.toString());
				dissmissOrderDilaog();
				onMakeOrderFailed();
			}
		});
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
	
	/**
	 * 显示获取订单
	 */
	private void showGetOrderDialog(String message) {
		if (orderDialog == null) {
			orderDialog = new ProgressDialog(this);
			orderDialog.setCancelable(false);
			orderDialog.setCanceledOnTouchOutside(false);
		}
		orderDialog.setMessage(message);
		orderDialog.show();
	}
	
	public void onMakeOrderFailed(){
		IlongSDK.showToast("生成订单失败");
		IlongSDK.getInstance().callbackPay.onFailed();
		finish();
	}
	
	private void doStartPay(String pay_info,int orderType, String orderId, String password,String amount){
		//已经生成订单号
		switch (orderType) {
			case 0:
				aliyPay(pay_info);
				break;
			case 1:
				doWXPay(pay_info);
				break;
			case 2:
				cftPay(amount+"", orderId, LyUrlConstant.BASE_URL + LyUrlConstant.RETURN_NOTIFY);
				break;
			case 3:
				longPay(orderId, password);
				break;
			default:
				break;
		}
	}
	
	
	private void doWXPay(String pay_info){
		org.json.JSONObject mJsonObject = null;
    	try {
			mJsonObject = new org.json.JSONObject(pay_info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	//String wx_appid = "wx8755c966ad175c4c";
    	String wx_appid = mJsonObject.optString("appid");
    	WXPay.init(getApplicationContext(), wx_appid);
    	WXPay.getInstance().doPay(pay_info, new WXPayResultCallBack() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onError(int error_code) {
				switch (error_code) {
				case WXPay.NO_OR_LOW_WX:
					Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
					break;
					
				case WXPay.ERROR_PAy:
					Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
					break;
					
				case WXPay.ERROR_PAY_PARAM:
					Toast.makeText(getApplicationContext(), "订单参数不对", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onCancel() {
				Toast.makeText(getApplicationContext(), "支付取消", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private LyAlipay alipay;
	/**
	 * 支付宝支付
	 */
	private void aliyPay(String pay_info) {
		if (alipay == null) {
			alipay = new LyAlipay(this, lyPayResult);
		}
		alipay.pay(pay_info);
	}

	private LyUPPay lyUPPay;

	/**
	 * 银联支付
	 */
	private void upPay(String tn) {
		if (lyUPPay == null) {
			lyUPPay = new LyUPPay(this);
		}
		lyUPPay.pay(tn);
	}

	/**
	 * 龙渊币支付
	 * @param out_trade_no
	 * @param password
	 */
	private void longPay(String out_trade_no, String password) {
		String url = LyUrlConstant.BASE_URL + LyUrlConstant.LONGYUAN_PAY;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("out_trade_no", out_trade_no);
		params.put("password", password);
		Log.d("gst", "龙渊币支付的url-->"+url);
		HttpUtil.newHttpsIntance(this).httpsPostJSON(this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissOrderDilaog();
				Log.d("gst","龙渊币支付的时候，返回的 content-->"+content);
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (null == respModel) {
					lyPayResult.lyPayNo(3, "龙渊币支付失败");
				} else {
					if (respModel.getErrno() == 200) {
						lyPayResult.lyPayYes(3);
					} else {
						Log.d("gst", "龙渊币支付失败的--》+errno--》"+respModel.getErrno());
						if(411 == respModel.getErrno()){
							goWebIlongReCharge();
						}else{
							lyPayResult.lyPayNo(3, "支付失败," + Constant.paseError(respModel.getErrno()));
						}
						
					}
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissOrderDilaog();
				lyPayResult.lyPayNo(3, "龙渊币支付失败");
			}
		});
		showGetOrderDialog("支付中，请稍候....");
	}
	
	private static final int CFT_REQ = 20;
	/**
	 * 跳转财付通支付
	 * @param amount
	 * @param out_trade_no
	 * @param notify_uri
	 */
	private void cftPay(String amount, String out_trade_no, String notify_uri) {
		Intent i = new Intent(this, LyCftPayActivity.class);
		i.putExtra("amount", amount);
		i.putExtra("out_trade_no", out_trade_no);
		i.putExtra("notify_uri", notify_uri);
		startActivityForResult(i, CFT_REQ);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CFT_REQ) {
			if (RESULT_OK == resultCode) {
				lyPayResult.lyPayYes(2);
			} else if (RESULT_CANCELED == resultCode) {
				lyPayResult.lyPayNo(2, "支付取消");
			}
		} else {
			//银联
			Bundle b = data.getExtras();
			String str = b.getString("pay_result");
			if (str.equalsIgnoreCase("success")) {
				lyPayResult.lyPayYes(1);
			} else if (str.equalsIgnoreCase("fail")) {
				lyPayResult.lyPayNo(0, "支付失败");
			} else if (str.equalsIgnoreCase("cancel")) {
				lyPayResult.lyPayNo(0, "支付取消");
			}
		}
	}
	
	private LyPayResult lyPayResult = new LyPayResult() {

		@Override
		public void lyPayYes(int payType) {
			if(isCharge){
				showGetOrderDialog("充值订单获取中，请勿离开");
				payresultdialog.setResultType(PayResultType.FAIL_LY);
				payresultdialog.setPayState(PayResultType.FAIL_PAY);
				payresultdialog.setLyCoin((int)total_amount);
				checkLyCoin(true);
			}else{
				updataUserInfo(false,0);
				payresultdialog.setPayState(PayResultType.SUCCESS);
				payresultdialog.setResultType(PayResultType.SUCCESS);
				if(payType == 3){
					payresultdialog.setLyCoin((int)(ly_amount-total_amount));
				}else{
					payresultdialog.setLyCoin((int)ly_amount);
				}
				payresultdialog.show();
//				payresultdialog.setLyCoin((int)total_amount);
//				Log.e("payacti", "success");
//				IlongSDK.getInstance().callbackPay.onSuccess4Bind();
//				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_LONG).show();
//				finish();
			}
		}
	

		@Override
		public void lyPayNo(int payType, String errorInfo) {
			ToastUtils.show(getApplicationContext(), errorInfo);
//			IlongSDK.getInstance().callbackPay.onFailed();
			payresultdialog.setLyCoin((int)ly_amount);
			payresultdialog.show();
			if(errorInfo.equals("支付取消")){
				payresultdialog.setPayState(PayResultType.CANEL);
			}
//			payresultdialog.setResultType(PayResultType.f);
//			//龙渊币支付失败不隐藏
//			if(payType!=3){
//				finish();
//			}
		}
	};
	
	/**
	 * 更新用户信息
	 */
	public void updataUserInfo(final boolean isPay,int delayTime){
		//String url = Constant.httpHost + Constant.USER_DETAIL;
		String url = "http://139.129.21.196/hola_sdk_server/uid.php";
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("access_token", access_token);
    	HttpUtil.newHttpsIntance(LyPayActivity.this).httpPost(LyPayActivity.this, url, params, new DelayJsonHttpResponseHandler(params,System.currentTimeMillis(),delayTime) {
			@Override
			public void ReqYes(Object reqObject, final String content) {
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (null != respModel && respModel.getErrno() == IlongCode.S2C_SUCCESS_CODE) {
					IlongSDK.mUserInfo = Json.StringToObj(respModel.getData(), UserInfo.class);
					DeviceUtil.saveData(LyPayActivity.this,IlongSDK.mUserInfo.getId()+"haspay_pwd", IlongSDK.mUserInfo.getPay_password());
					IlongSDK.AccountId = IlongSDK.mUserInfo.getUid();
					ly_amount = Float.parseFloat(IlongSDK.mUserInfo.getMoney());
					if (isPay && ly_amount>=total_amount) {
						dissmissOrderDilaog();
						lyPayOrder = new LyPayOrder(pack_key, total_amount*100+"", app_order_id, app_uid, notify_uri, product_name, product_id, app_username, access_token);
						createOrderNumber(3, lyPayOrder,lyPayPassworld.getPassworld(), total_amount+"", null);
						payresultdialog.setLyCoin(0);
						timer.cancel();
						timer = null;
					}else if(!isPay){
						InitData();
					}
				}
				ly_number_refresh.clearAnimation();
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				ly_number_refresh.clearAnimation();
			}
		});
	}

	/**
	 * 更新龙渊币信息
	 */
	private void checkLyCoin(final boolean isPay) {
	
		timer = new CountDownTimer(6000,2000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				updataUserInfo(isPay,0);
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dissmissOrderDilaog();
			}
		};
	    timer.start();
	}
	
	public void goWebIlongReCharge(){
		GoWebLongBReCharge = new IlongBasicDialog(LyPayActivity.this, ResUtil.getStyleId(LyPayActivity.this, "IlongBasicDialogStyle"));
		GoWebLongBReCharge.show();
		GoWebLongBReCharge.setCancelable(false);
		GoWebLongBReCharge.setCanceledOnTouchOutside(false);
		GoWebLongBReCharge.getDialogleftBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoWebLongBReCharge.dismiss();
				lyPayResult.lyPayNo(3, "支付失败," + Constant.paseError(411));
			}
		});
		GoWebLongBReCharge.getDialogrightBtn().setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
			Intent it = new Intent(LyPayActivity.this, ActivityWeb.class);
			it.putExtra("url", Constant.goWebRechargeLongBi(IlongSDK.mToken).toString());
			startActivity(it);
			}
		});
	}
	
	/**
	 * 强制设置屏幕方向
	 */
	public void fixedScreen(){
		InitScreen();
		if(isLand){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
	}
	
	/**
	 * 是否选择了支付方式
	 * @return true 选择了支付方式     false没有设置支付方式
	 */
	private boolean getIschoosePayMode(){
		if(ten_mode_rl.isSelected() || alipay_mode_rl.isSelected() || union_mode_rl.isSelected()){
			return true;
		} 
		return false;
	}
	
	/**展示跳转设置支付密码的 dialog */
	public  void goSetPWDDialog(){
		final PayEixtDialog dialog = new PayEixtDialog(LyPayActivity.this);
		dialog.setMessage("您未设置龙渊币支付密码\n是否设置支付密码？");
		dialog.setCanelText("取  消");
		dialog.setPositive("确  定");
		dialog.setPositiveListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent it = new Intent(LyPayActivity.this, ActivityWeb.class);
				it.putExtra("title", "设置支付密码");
				it.putExtra("url", Constant.getSetPayPWDUri(IlongSDK.mToken).toString());
				it.putExtra("id",uid);
//				Log.d("gst","前去设置支付的网页的url--->"+Constant.getSetPayPWDUri(IlongSDK.mToken).toString());
				startActivity(it);
			}
		});
		
		dialog.setCanel(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	/**退出 dialog */
	public void exit(){
		final PayEixtDialog dialog = new PayEixtDialog(LyPayActivity.this);
		dialog.setPositiveListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		dialog.setCanel(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IlongSDK.getInstance().callbackPay.onCancel();
			    dialog.dismiss();
			    finish();
			}
		});
		dialog.show();
	}
	/**
	 * 回调接口
	 * @author lenovo
	 *
	 */
	public interface LyPayResult {
		void lyPayYes(int payType);

		void lyPayNo(int payType, String errorInfo);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			exit();
		}
		return false;
	}
}
