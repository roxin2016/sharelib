package com.zsyj.sharesdk.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 跟网络相关的工具类
 *
 * @author luoxingxing
 */
public class NetUtil {
    public static final int NULLNET = 0;
    /**
     * CMWAP
     */
    public static final int CMWAP = 1;
    /**
     * CMNET
     */
    public static final int CMNET = 2;
    /**
     * WIFI
     */
    public static final int WIFI = 3;
    public static final int CT = 4;
    public static final String CMCC_PROXY = "10.0.0.172";
    public static final String CT_PROXY = "10.0.0.200";

    private NetUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    public static int getNetType(Context context) {

        if (context == null) {
            return CMNET;
        }

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return NULLNET;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return WIFI;
        } else {
            String host = android.net.Proxy.getDefaultHost();
            if (host != null) {
                if (host.indexOf(CMCC_PROXY) != -1)
                    return CMWAP;
                else if (host.indexOf(CT_PROXY) != -1) {
                    return CT;
                }
                return NULLNET;
            } else {
                return CMNET;
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        boolean available = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            try {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    available = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return available;
    }
}
