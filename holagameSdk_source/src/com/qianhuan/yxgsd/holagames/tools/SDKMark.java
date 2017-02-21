package com.qianhuan.yxgsd.holagames.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SDKMark {

	public static String getMark(Context c) {
		try {
			if (c == null)
				return "";
			String str = ZipContraFile(getApkPath(c, c.getPackageName()), "META-INF/9d0b011a75f9");
			return str;
		} catch (Exception e) {
			LogUtils.error(e);
			return "";
		}
	}

	private static String getApkPath(Context c, String pkgName) throws NameNotFoundException {
		PackageManager pm = c.getPackageManager();
		ApplicationInfo pi = null;
		pi = pm.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
		if (pi != null)
			return pi.sourceDir;
		else
			return null;
	}

	public static String ZipContraFile(String zippath, String filename) throws IOException {
		File file = new File(zippath);//压缩文件路径和文件名
		ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = zipFile.getEntry(filename);//所解压的文件名
		if (entry == null) {
			zipFile.close();
			return "";
		}
		InputStream input = zipFile.getInputStream(entry);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count;
		while ((count = input.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		byte data[] = baos.toByteArray();
		String str = new String(data, "UTF-8");
		input.close();
		baos.close();
		zipFile.close();
		return str;
	}

}
