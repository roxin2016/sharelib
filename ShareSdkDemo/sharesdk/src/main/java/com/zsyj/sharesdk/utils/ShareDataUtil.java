package com.zsyj.sharesdk.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * @类名: shareData
 * @作者: 锋前子影
 * @日期: 2015年1月21日15:34:48
 * @描述: 类<code>CustomLayoutParamas</code>是处理共享数据的类</p>
 */
public class ShareDataUtil {

	/**
	 * 判断布尔值共享文件是否存在，不存在则创建并初始化共享文件
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 */
	public static void initShareFile(Context context, String shareFileName) {
		// 初始化共享数据
		if (!context.getSharedPreferences(shareFileName, Context.MODE_PRIVATE)
				.getBoolean("isExist", false)) {
			Editor sharedataEditor = context.getSharedPreferences(
					shareFileName, Context.MODE_PRIVATE).edit();
			sharedataEditor.putBoolean("isExist", true);
			sharedataEditor.commit();
		}
	}

	/**
	 * 保存String键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param value
	 *            键值
	 */
	public static void saveKeyValue(Context context,
                                    String shareFileName, String key, String value) {
		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 保存键值
		context.getSharedPreferences(shareFileName, Context.MODE_PRIVATE)
				.edit().putString(key, value).commit();
	}

	/**
	 * 获取String键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认键值
	 */
	public static String getKeyValue(Context context, String shareFileName,
                                     String key, String defaultValue) {
		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 获取键值
		String result = context.getSharedPreferences(shareFileName,
				Context.MODE_PRIVATE).getString(key, defaultValue);

		return result;
	}

	/**
	 * 保存Boolean键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param value
	 *            键值
	 */
	public static void saveKeyValue(Context context,
                                    String shareFileName, String key, Boolean value) {
		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 保存键值
		context.getSharedPreferences(shareFileName, Context.MODE_PRIVATE)
				.edit().putBoolean(key, value).commit();
	}

	/**
	 * 获取布尔键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认键值
	 */
	public static Boolean getKeyValue(Context context,
                                      String shareFileName, String key, Boolean defaultValue) {

		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 获取键值
		Boolean result = context.getSharedPreferences(shareFileName,
				Context.MODE_PRIVATE).getBoolean(key, defaultValue);

		return result;
	}

	/**
	 * 保存INT键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param value
	 *            键值
	 */
	public static void saveKeyValue(Context context,
                                    String shareFileName, String key, int value) {
		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 保存键值
		context.getSharedPreferences(shareFileName, Context.MODE_PRIVATE)
				.edit().putInt(key, value).commit();
	}

	/**
	 * 获取INT键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认键值
	 */
	public static int getKeyValue(Context context,
                                  String shareFileName, String key, int defaultValue) {

		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 获取键值
		int result = context.getSharedPreferences(shareFileName,
				Context.MODE_PRIVATE).getInt(key, defaultValue);

		return result;
	}

	/**
	 * 保存Float键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param value
	 *            键值
	 */
	public static void saveKeyValue(Context context,
                                    String shareFileName, String key, float value) {
		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 保存键值
		context.getSharedPreferences(shareFileName, Context.MODE_PRIVATE)
				.edit().putFloat(key, value).commit();
	}

	/**
	 * 获取Float键值
	 * 
	 * @param context
	 *            上下文
	 * @param shareFileName
	 *            共享文件名
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认键值
	 */
	public static float getKeyValue(Context context,
                                    String shareFileName, String key, float defaultValue) {

		// 初始化共享数据
		initShareFile(context, shareFileName);

		// 获取键值
		float result = context.getSharedPreferences(shareFileName,
				Context.MODE_PRIVATE).getFloat(key, defaultValue);

		return result;
	}

}
