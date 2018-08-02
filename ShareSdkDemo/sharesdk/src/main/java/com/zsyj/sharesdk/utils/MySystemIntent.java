package com.zsyj.sharesdk.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * @类名: SystemIntent
 * @作者: ChellyChi
 * @版本: V1.0
 * @日期: 2012-11-2 上午11:03:20
 * @描述: 类<code>SystemIntent</code>是封装调用系统应用函数的类</p>
 * <p/>
 * Copyright 2012 艾秀信息技术有限公司。 All rights reserved.
 * <p/>
 * 该源码允许添加、删除和修改。
 */
public class MySystemIntent {

    public static void mLog(String msg) {
        // //// mMsg.LogI("SystemIntent", msg);
    }

    /**
     * 打开对应包名的应用程序
     *
     * @param context  上下文
     * @param packName 要运行的程序包名
     * @return 是否打开成功
     */
    public static Boolean runApplication(Context context, String packName) {

        try {
            PackageManager pm = context.getPackageManager();
            Intent startIntent = pm.getLaunchIntentForPackage(packName);
            context.startActivity(startIntent);

            // //mLog("应用打开成功");
            return true;
        } catch (Exception e) {
            // //mLog("没有对应此包名：" + packName + " ,的应用");
            return false;
        }
    }

    /**
     * 以入口方式启动包名为componentNames[0]中的名为componentNames[1]的Acitivity
     *
     * @param context      上下文
     * @param packageName  包名
     * @param activityName Activity名
     */
    public static void startMainActivity(Context context, String packageName,
                                         String activityName) {

        Intent startIntent = new Intent();
        try {
            startIntent.setComponent(new ComponentName(packageName,
                    activityName));
            startIntent.setAction("android.intent.action.MAIN");
            startIntent.addCategory("android.intent.category.LAUNCHER");
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以入口方式启动包名为componentNames[0]中的名为componentNames[1]的Acitivity
     *
     * @param context        上下文
     * @param componentNames Activity绝对路径名的字符串数组
     */
    public static void startMainActivity(Activity context,
                                         String[] componentNames) {

        Intent startIntent = new Intent();
        try {
            startIntent.setComponent(new ComponentName(componentNames[0],
                    componentNames[1]));
            startIntent.setAction("android.intent.action.MAIN");
            startIntent.addCategory("android.intent.category.LAUNCHER");
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
            context.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以入口方式启动本应用的Activity
     *
     * @param context      上下文
     * @param activityName 要启动的Activity名字
     */
    public static void startMainActivity(Activity context, String activityName) {
        startMainActivity(context, new String[]{context.getPackageName(),
                activityName});
    }

    /**
     * 回到桌面
     *
     * @param context 上下文
     */
    public static void goHome(Context context) {
        try {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否存在对应包名的APP
     *
     * @param context 上下文
     */
    public static Boolean isHaveApp(Context context, String packageName) {
        try {
            return AppInfoHelper.isAppOk(context, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
