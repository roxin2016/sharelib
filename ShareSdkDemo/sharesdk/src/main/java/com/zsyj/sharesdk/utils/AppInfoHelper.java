package com.zsyj.sharesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @类名: AppInfoHelper
 * @描述: 类<code>AppInfoHelper</code>是和应用信息相关的帮助类</p>
 */
public class AppInfoHelper {

    /**
     * 获取已安装应用的Drawable图标，获取失败返回null
     */
    public static Drawable getAppIcon(Context context, String pkgName) {

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = AppInfoHelper.getPackageInfo(context,
                    pkgName).applicationInfo;

            return appInfo.loadIcon(pm);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取app的安装时间，需要api最低为9才有效
     */
    public static String getAppInstallDate(Context context,
                                           String appPackageName) {
        String msg = "";

        try {
            long time = AppInfoHelper.getPackageInfo(context, appPackageName).firstInstallTime;
            Date date = new Date(time);
            msg = "" + DateFormat.format("yyyy-MM-dd EE HH:mm:ss", date);
            msg = msg.substring(0, "yyyy-mm-dd".length()); // 只需要日期
        } catch (Exception e) {
        }

        return msg;
    }

    /**
     * 获取含有指定关键字应用的包名
     *
     * @param context     上下文
     * @param searchChars 关键字
     * @return strPackagename 含有指定关键字应用的包名,未获取到则返回""
     */
    public static String getAppPackageName(Context context, String searchChars) {

        try {
            searchChars = searchChars.toLowerCase(); // 大写转小写;
            PackageManager pManager = context.getPackageManager();
            List<PackageInfo> appList = getAllApps(context);
            String strPackagename = "";
            String strname = "";

            for (int i = 0; i < appList.size(); i++) {
                PackageInfo pinfo = appList.get(i);
                strPackagename = pinfo.applicationInfo.packageName;
                strname = pManager.getApplicationLabel(pinfo.applicationInfo)
                        .toString();

                // 先判断是否有对应全名的应用，没有再判断是否有包含关键字的应用
                if (containsAny(strname, searchChars)) {
                    // mLog("关键字：" + searchChars + ", 全名：" + strname + ", 包名："
                    // + strPackagename);
                    return strPackagename;
                }
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取手机内所有非系统预装的应用列表
     *
     * @return apps app信息列表
     */
    @SuppressWarnings("static-access")
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            // 判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    static boolean containsAny(String str, String searchChars) {

        // test.toLowerCase() //大写转小写
        return str.toLowerCase().contains(searchChars);
    }

    /**
     * 获取App包名，版本号
     */
    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取App版本字符串，没获取到则返回""
     */
    public static String getAppVersionString(Context context) {

        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取App版本号，没获取到则返回-1
     */
    public static int getAppVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            int versionCode = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionCode;
            return versionCode;
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 检测该包名所对应的应用是否存在
     *
     * @param packageName
     * @return
     */

    public static boolean checkPackage(Context context, String packageName)

    {

        if (packageName == null || "".equals(packageName))
            return false;

        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }

    }

    /**
     * 读取配置文件中指定Meta字段的值
     */
    public static String GetMetaData(Context context, String name) {
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bundle bundle = info.metaData;
        String value = String.valueOf(bundle.get(name));

        return value;
    }

    /**
     * 保存产品号、渠道号和预装性信息的文件名
     */
    public static String INIT_FILE_NAME = "init.xml";

    /**
     * 从manifest获取对应字段的值（没有读取到为 ""）
     *
     * @param context 上下文
     * @param field   字段
     * @return
     */
    public static String getManifestInitInfo(Context context, String field) {

        String initInfo = "";

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            initInfo = bundle.getString(field) + "";

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return initInfo;
    }

    /**
     * 获取本应用的版本
     *
     * @param context 上下文
     * @return
     */
    public static String getVersion(Context context) {

        String version = null;

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 获取本应用的版本号
     *
     * @param context 上下文
     * @return int型版本号，用于比较版本
     */
    public static int getVersionCode(Context context) {

        int versionCode = 0;

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * 获取产品号String[0]、渠道号String[1]和预装性String[2]（没有读取到为""）， 从XML文件中获取字段的方法
     *
     * @param context 上下文
     * @return
     * @see AppInfoHelper#getInitInfo(Context)
     * @deprecated
     */
    public static String[] getInitInfo(Context context) {

        String initInfo[] = new String[]{"", "", ""};

        try {
            InputStream inputStream = context.getAssets().open(INIT_FILE_NAME);
            byte[] buffer = new byte[1024];
            int len = 0;
            String text = "";
            while ((len = inputStream.read(buffer)) != -1) {
                String temp = new String(buffer, 0, len);
                text += temp;
            }
            initInfo[0] = text.substring(text.indexOf("<product>")
                    + "<product>".length(), text.indexOf("</product>"));
            initInfo[1] = text.substring(
                    text.indexOf("<chanel>") + "<chanel>".length(),
                    text.indexOf("</chanel>"));
            initInfo[2] = text.substring(
                    text.indexOf("<type>") + "<type>".length(),
                    text.indexOf("</type>"));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return initInfo;
    }

    /**
     * 获取已安装应用的 PackageInfo，没有返回 null
     *
     * @param context     上下文
     * @param packageName 包名
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {

        PackageInfo packageInfo = null;
        PackageManager pm = context.getPackageManager();

        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return packageInfo;
    }

    /**
     * 判断某已安装应用是否完整,也可以判断应用是否存在
     *
     * @param context  上下文
     * @param packName 包名
     * @return
     */
    public static boolean isAppOk(Context context, String packName) {

        boolean appIsOk = false;

        try {
            // ApplicationInfo applicationInfo = context.getPackageManager()
            // .getApplicationInfo(packName,
            // PackageManager.GET_UNINSTALLED_PACKAGES);
            // if (applicationInfo != null) {
            // appIsOk = true;
            // }

            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packName, 0);
            if (packageInfo != null) {
                appIsOk = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            // e.printStackTrace();
        }

        return appIsOk;
    }

    /**
     * 判断是否是系统应用
     *
     * @param context  上下文
     * @param packName 应用包名
     * @return
     */
    public static boolean isSysApp(Context context, String packName) {

        boolean isSysApp = false;

        try {

            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(packName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            if (applicationInfo != null) {
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {// 系统应用
                    isSysApp = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return isSysApp;
    }

    /**
     * 检测 下载的APK包是否存在且完整
     *
     * @param apkPath APK文件存放的完整路径
     * @return
     */
    public static boolean isApKFileOk(Context context, String apkPath) {

        boolean fileIsOk = false;

        File file = new File(apkPath);
        if (file.exists()) {
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageArchiveInfo(apkPath,
                                PackageManager.GET_ACTIVITIES);
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if (applicationInfo != null) {
                    fileIsOk = true;
                }
            } catch (Exception e) {
                // TODO: handle exception
                // e.printStackTrace();
            }
        }

        return fileIsOk;
    }

    /**
     * 获取未安装应用的图标
     */
    public static Drawable getUiApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                // //// mMsg.LogI("ApkIconLoader", e.toString());
            }
        }
        return null;
    }

    /**
     * 获取未安装应用的包名
     *
     * @param context 上下文
     * @param apkPath 文件路径
     * @return 包名，为获取到则返回""
     */
    public static String getUiApkPackageName(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                String packageName = appInfo.packageName;
                // //mLog("包名：" + packageName);
                return packageName;
            }
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 获取未安装应用的版本号
     *
     * @param context 上下文
     * @param apkPath 文件路径
     * @return 版本号，为获取到则返回""
     */
    public static String getUiApkVersion(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String version = info.versionName == null ? "0"
                        : info.versionName;
                return version;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "";
    }

    /**
     * 获取已安装的指定应用的启动界面,未获取到则返回 “”
     *
     * @param context 上下文
     * @param pckName 已安装应用的包名
     * @return 启动界面
     */
    public static String getMainActivity(Context context, String pckName) {
        String result = "";

        if (StringUtils.isNullStr(pckName)) {
            return result;
        }

        try {
            List<ResolveInfo> mApps = null;
            ResolveInfo info = null;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mApps = context.getPackageManager().queryIntentActivities(
                    mainIntent, 0);

            for (int i = 0; i < mApps.size(); i++) {
                info = mApps.get(i);
                String packagename = info.activityInfo.packageName;
                String activityName = info.activityInfo.name;

                if (containsAny(packagename, pckName)) {

                    result = activityName;

                    return result;
                }

            }
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * 通过包名获取应用程序的名称。
     *
     * @param context     Context对象。
     * @param packageName 包名。
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context,
                                                     String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = "";
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取渠道号
     *
     * @return
     */
    public static String getSid(Context context) {

        try {
            String result = ShareDataUtil.getKeyValue(context,
                    "shareData", "mySid", "admin");

            if (StringUtils.isNullStr(result)) {
                result = AppInfoHelper.GetMetaData(context,
                        "UMENG_CHANNEL");

                ShareDataUtil.saveKeyValue(context, "shareData",
                        "mySid", result);
            }


            return result;
        } catch (Exception e) {
            return "admin";
        }
    }

    /**
     * 获取指定包名的版本号
     *
     * @param context     本应用程序上下文
     * @param packageName 你想知道版本信息的应用程序的包名
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context, String packageName) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 安装apk
     */
    public static void installAPK(Activity activity, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
