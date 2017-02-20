package com.holagames.xcds.enums;

public enum PayResultType {
	SUCCESS,//成功
	FAIL_LY,//失败，原因是充值火拉币，即混合支付第一阶段
	FAIL_PAY,//失败，购买道具失败
	CANEL//支付取消
}
