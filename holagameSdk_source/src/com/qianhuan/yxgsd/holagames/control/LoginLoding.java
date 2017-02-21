package com.qianhuan.yxgsd.holagames.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * 登录时候点点加载动画
 * 暂时没有使用
 * @author 邹龙
 *
 */
public class LoginLoding extends View{
	    /**点的数量*/
		private int pointNumber = 3;
		/**延迟时间*/
		private long delyTime =300;
		/**点的颜色*/
		private int pointColor = new Color().parseColor("#272727");
		private int bgColor = new Color().parseColor("#FFFFFF");
		/**圆半径*/
		private int pointRadius=10;
		/**每个点间的距离*/
		private int distance;
		
		private int drawCount = 0;
		
		/**画笔*/
		private Paint paint;
		public LoginLoding(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    	// TODO Auto-generated method stub
	    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    	setData();
	    }
	    /**视图测量后设置参数*/
		private void setData() {
			// TODO Auto-generated method stub
			//设置半径为高的一半
			pointRadius = getMeasuredHeight()/2;
			//设置距离，然后留一个圆点的空隙
			distance = getMeasuredWidth()/pointNumber-getMeasuredHeight();
			//画笔
			paint = new Paint();
			paint.setColor(pointColor);
			//抗锯齿
			paint.setAntiAlias(true);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			for(int i =0;i<drawCount+1;i++)
			canvas.drawCircle((i+1)*pointRadius+distance*i, getMeasuredHeight()/2, pointRadius, paint);
			drawCount++;
			if(drawCount >= pointNumber){
				drawCount = 0;
			}
			try {
				Thread.sleep(delyTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 postInvalidate();
		}
}
