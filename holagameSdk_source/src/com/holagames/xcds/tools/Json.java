/**
 * 2014-3-3 下午3:55:51 Created By niexiaoqiang
 */

package com.holagames.xcds.tools;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * String转JSON
 */
public class Json {
	public static final String TAG = Json.class.getSimpleName();

	/**
	 * 从字符串转位对应的对象
	 * @param str
	 * @param beanObj
	 * @return
	 */
	public static <T> T StringToObj(String str, Class<T> beanObj) {
		try {
			return JSON.parseObject(str, beanObj);
		} catch (Exception e) {
			LogUtils.error(e);
			return null;
		}
	}

	/**
	 * 从字符串转位对应的对象
	 * @param <T>
	 * @param str
	 * @param beanObj
	 * @return
	 */
	public static <T> List<T> StringToList(String str, Class<T> beanObj) {
		try {
			return JSON.parseArray(str, beanObj);
		} catch (Exception e) {
			LogUtils.error(e);
			return null;
		}
	}

	/**
	 * 从对象转为String
	 * @param object
	 * @return
	 */
	public static String ObjToString(Object object) {
		try {
			return JSON.toJSONString(object);
		} catch (Exception e) {
			LogUtils.error(e);
			return null;
		}
	}

}
