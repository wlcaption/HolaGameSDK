package com.holagames.xcds.modle;

import java.io.Serializable;

/**
 * 生成订单结果
 * @author niexiaoqiang
 */
public class OrderNumber implements Serializable {
	private static final long serialVersionUID = 1L;
	private String out_trade_no;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

}
