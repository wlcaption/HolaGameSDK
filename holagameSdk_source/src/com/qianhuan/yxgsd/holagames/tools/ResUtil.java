package com.qianhuan.yxgsd.holagames.tools;

import android.content.Context;

/**
 * 资源文件工具
 * @author niexiaoqiang
 */
public class ResUtil {

	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout", paramContext.getPackageName());
	}

	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string", paramContext.getPackageName());
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
	}

	public static int getStyleId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style", paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
	}

	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color", paramContext.getPackageName());
	}

	public static int getDimenId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "dimen", paramContext.getPackageName());
	}
    
	public static int getAnimationID(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "anim", paramContext.getPackageName());
	}
}
