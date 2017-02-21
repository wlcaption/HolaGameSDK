package com.qianhuan.yxgsd.holagames.tools;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;

public class TimerDown extends CountDownTimer {
	private Button mButton;

	public TimerDown(Button button, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		this.mButton = button;
	}

	@Override
	public void onFinish() {
		mButton.setText("获取验证码");
		mButton.setTextColor(new Color().WHITE);
		mButton.setEnabled(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		mButton.setEnabled(false);
		mButton.setTextColor(new Color().WHITE);
		mButton.setText("重新获取(" + millisUntilFinished / 1000 + ")S");
	}
}