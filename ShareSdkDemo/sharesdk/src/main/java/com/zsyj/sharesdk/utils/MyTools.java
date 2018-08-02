package com.zsyj.sharesdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyTools {
	public static String getSign(Context context, String packageName)
			throws NoSuchAlgorithmException {
		Signature[] signs = getRawSignature(context, packageName);
		if ((signs == null) || (signs.length == 0)) {
			return null;
		} else {
			Signature sign = signs[0];
			// String signMd5 = MD5.getMessageDigest(sign.toByteArray());
			String signMd5 = getMD5(sign.toByteArray());

			return signMd5;
		}
	}

	public static Signature[] getRawSignature(Context context,
                                              String packageName) {
		if ((packageName == null) || (packageName.length() == 0)) {
			return null;
		}
		PackageManager pkgMgr = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pkgMgr.getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
		if (info == null) {
			return null;
		}
		return info.signatures;
	}

	/**
	 * �����ṩgetMD5(String)����
	 * 
	 * @return ����ʧ�ܷ���""
	 */
	public static String getMD5(byte[] cs) throws NoSuchAlgorithmException {

		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(cs);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

		try {
			StringBuilder hex = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				if ((b & 0xFF) < 0x10) {
					hex.append("0");
				}
				hex.append(Integer.toHexString(b & 0xFF));
			}

			return hex.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
