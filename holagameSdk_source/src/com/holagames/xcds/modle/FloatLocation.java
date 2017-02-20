package com.holagames.xcds.modle;

import com.holagames.xcds.enums.LocationType;

import android.R.integer;
import android.text.StaticLayout;

/**
 * 枚举类型
 * 
 * @author 邹龙
 *
 */
public class FloatLocation {
	public int X;
	public int Y;
	/**
	 * 根据布尔类型计算出位置
	 * @param totalSize 总长度
	 * @param floatLocation 位置
	 * @return
	 */
	public int getLocation(int totalSize, LocationType locationType) {
	    int size = 0;
	   if(LocationType.LEFT == locationType){
		   size = 0;
	   }else if(LocationType.RIGHT == locationType){
		   size = totalSize;
	   }
		return size;
	}
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public void setX(int totle,LocationType locationType) {
		X = getLocation(totle, locationType);
	}
	public void setX(int totle,Float floatX) {
		X = (int)(totle*floatX);
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public void setY(int totle,LocationType locationType) {
		Y = getLocation(totle, locationType);
	}
	public void setY(int totle,Float floatY) {
		Y = (int)(totle*floatY);
	}
	
}
