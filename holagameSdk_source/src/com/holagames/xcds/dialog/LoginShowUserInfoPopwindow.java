package com.holagames.xcds.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.array;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.ac.SdkLoginActivity;
import com.holagames.xcds.modle.HistoryUserInfo;
import com.holagames.xcds.modle.RespModel;
import com.holagames.xcds.modle.UserInfo;
import com.holagames.xcds.tools.Json;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.ToastUtils;
import com.holagames.xcds.tools.http.Constant;
import com.holagames.xcds.tools.http.HttpUtil;
import com.holagames.xcds.tools.http.NetException;
import com.holagames.xcds.tools.http.SdkJsonReqHandler;
/**
 * 历史记录下拉弹框
 * 数据加载顺序：
 * 0.从服务器获取用户信息
 * 1.创建历史用户信息队列
 * 
 * @author zoulong
 *
 */
public class LoginShowUserInfoPopwindow extends PopupWindow implements OnClickListener{
	private String TAG = LoginShowUserInfoPopwindow.class.getSimpleName();
	
	/**弹窗的填充RecyclerView*/
	private RecyclerView userinfo_rv;
	/**适配器*/
    private UserInfoAdapter adapter;
    /**数据源*/
    private ArrayList<HistoryUserInfo> userInfos;
    /**点击item回调*/
    private OnItemOnclick onItemOnclick;
    /**子视图高度*/
    private int childViewHight;
    /**子视图宽度*/
    private int childViewWidth;
    /**锚点视图，在弹窗更新时使用*/
    private View anchor;
    private Activity mActivity;
    
	@SuppressLint("NewApi")
	public	LoginShowUserInfoPopwindow(Activity context,OnItemOnclick onItemOnclick){
		mActivity = context;
		userInfos = new ArrayList<HistoryUserInfo>();
		this.onItemOnclick = onItemOnclick;
		userinfo_rv  = new RecyclerView(context);
		//设置管理
		userinfo_rv.setLayoutManager(new LinearLayoutManager(context));
		adapter = new UserInfoAdapter();
		userinfo_rv.setAdapter(adapter);
		setContentView(userinfo_rv);
		setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		//设置背景
		setBackgroundDrawable(context.getResources().getDrawable(ResUtil.getDrawableId(context, IlongSDK.ISLONG?"ilong_square_bottom_radiu":"hr_square_bottom_radiu")));
	}

	public class UserInfoAdapter extends Adapter<UserInfoHolder>{

		@Override
		public int getItemCount() {
			// TODO Auto-generated method stub
			return userInfos.size();
		}

		@Override
		public void onBindViewHolder(UserInfoHolder viewHolder, int position) {
			// TODO Auto-generated method stub
			HistoryUserInfo userInfo = userInfos.get(position);
			if(userInfo.getType().equals(Constant.TYPE_USER_NORMAL)){
				viewHolder.userName_tv.setText(userInfo.getUsername());
			}else{
				viewHolder.userName_tv.setText("游客登录");
			}
			if(position == 0 || !userInfo.getType().equals(Constant.TYPE_USER_NORMAL)){
				viewHolder.del_bt.setVisibility(View.GONE);
			}else{
				viewHolder.del_bt.setVisibility(View.VISIBLE);
			}
			String temp = "最后登录："+userInfo.translateTime();
			viewHolder.userLastLoginTime_tv.setText(temp);
			viewHolder.del_bt.setTag(userInfos.get(position));
			viewHolder.del_bt.setOnClickListener(LoginShowUserInfoPopwindow.this);
		}

		@Override
		public UserInfoHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			int viewId = ResUtil.getLayoutId(parent.getContext(), "history_userinfo_item");
			View view = LayoutInflater.from(parent.getContext()).inflate(viewId,  parent,false);
			return new UserInfoHolder(view);
		}
	}
	
	/**
	 * RecyclerView的viewholder
	 * @author lenovo
	 *
	 */
	public class UserInfoHolder extends ViewHolder implements OnClickListener{
		private TextView userName_tv;
		private TextView userLastLoginTime_tv;
		private Button del_bt;
		private View line;
		public UserInfoHolder(View itemView) {
			super(itemView);
			userName_tv = (TextView) itemView.findViewById(ResUtil.getId(itemView.getContext(), "userinfo_time_username"));
			userLastLoginTime_tv = (TextView) itemView.findViewById(ResUtil.getId(itemView.getContext(), "userinfo_time_lastlogintime"));
			del_bt =  (Button) itemView.findViewById(ResUtil.getId(itemView.getContext(), "userinfo_clear"));
			line = itemView.findViewById(ResUtil.getId(itemView.getContext(), "history_item_line"));
			itemView.setOnClickListener(this);	
		}
		@Override
		public void onClick(View v) {
			HistoryUserInfo object = userInfos.get(getPosition());
			if(onItemOnclick!=null){
				onItemOnclick.onclick(object.toMap(),getPosition());
			}
		}
	}
	
	/**
	 *每列的点击事件，因为RecyclerView没有item的点击事件，所以需要自己去实现
	 * @author zoulong
	 *
	 */
	public interface OnItemOnclick{
		public void onclick(Map<String, String> map,int position);
	}

	//点击删除按钮
	@Override
	public void onClick(View view) {
		HistoryUserInfo object = (HistoryUserInfo) view.getTag();
		adapter.notifyItemRemoved(userInfos.indexOf(object));
		userInfos.remove(object);
		if(userInfos.size()<=3){
			update(anchor,childViewWidth, childViewHight*userInfos.size());
		}
		if(userInfos.size()==0){
			dismiss();
		}
		delRecord(object);
	}
	
	/**
	 * 删除服务器上的用户信息
	 * @param object 用户对象
	 */
	private void delRecord(HistoryUserInfo object) {
		String url = Constant.httpHost +Constant.DEL_RECORD;
		Map<String, Object> params = object.toMap(mActivity);
		HttpUtil.newHttpsIntance(mActivity).httpsPostJSON(mActivity, url,params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "删除账号信息和设备成功打印："+content);
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "删除账号信息和设备失败打印："+slException.toString());
			}
		});
		
	}

	/*
	 * 设置子视图的高度，也就是弹窗的高度
	 */
	public void setChildViewHight(int childViewHight) {
		this.childViewHight = childViewHight;
	}

	/**
	 * 设置弹框的高度，如果大于3高度为3.5，小于三为数据长度高度，数据长度为0不显示
	 * 这个方法中所做的技术都基于弹框处于屏幕中央，而随着弹窗大小的改变，我们应该将弹窗的位置动态的做偏移
	 */
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		anchor = parent;
		if(userInfos.size()==0){
			return;
		}
		if(userInfos.size()<3){
			setHeight(userInfos.size()*childViewHight);
			y-=childViewHight/userInfos.size();
		}
		if(userInfos.size()>3){
			setHeight((int)(childViewHight*3.5));
			y+=childViewHight*0.25;
		}
		super.showAtLocation(parent, gravity, x, y);
	}
	
	/*
	 * 设置子视图的宽度，也就是弹窗的宽度
	 */
	public void setChildViewWidth(int childViewWidth) {
		this.childViewWidth = childViewWidth;
	}
	/**服务器获取到的数据插入到用户列表中*/
	public void setServiceUserInfo(String data){
		if(data == null) return;
		RespModel respModel = Json.StringToObj(data, RespModel.class);
		if(respModel.getErrno() != 200) return;
		if(respModel.getData()==null) return;
		userInfos = (ArrayList<HistoryUserInfo>) JSON.parseArray(respModel.getData(), HistoryUserInfo.class);
//		userInfos = removeDuplicateWithOrder(userInfos);
		if(isShowing()){
			adapter.notifyDataSetChanged();
		}
	}
}
