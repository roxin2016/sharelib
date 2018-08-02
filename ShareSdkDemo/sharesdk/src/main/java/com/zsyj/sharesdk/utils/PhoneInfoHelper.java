package com.zsyj.sharesdk.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class PhoneInfoHelper {

    private static PhoneInfoHelper instance = null;

    public static PhoneInfoHelper getInstance() {
        if (instance == null) {
            instance = new PhoneInfoHelper();
        }
        return instance;
    }

    private PhoneInfoHelper() {
    }

    /**
     * 无运营商
     */
    public final int PROVIDER_NULL = 0;
    /**
     * 中国移动
     */
    public final int PROVIDER_CMCC = 1;
    /**
     * 中国联通
     */
    public final int PROVIDER_CUCC = 2;
    /**
     * 中国电信
     */
    public final int PROVIDER_CTCC = 3;

    /**
     * SIM卡状态是否良好
     *
     * @param context 上下文
     * @return
     */
    public boolean isSimOk(Context context) {

        boolean isOk = false;

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            isOk = true;
        }

        return isOk;
    }

    /**
     * @return 手机分辨率，int[0]表示宽，int[1]表示高
     */
    public int[] getResolution(Context context) {

        int[] resolution = new int[2];

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        resolution[0] = outMetrics.widthPixels;
        resolution[1] = outMetrics.heightPixels;

        return resolution;
    }

    /**
     * 获取当前手机网络类型代表的数字（0：没有任何连接；1：CMWAP；2：CMNET；3：WIFI；4：CT）
     *
     * @param context 上下文
     * @return
     */
    public int getNetType(Context context) {

        return NetUtil.getNetType(context);
    }

    /**
     * @return 获取系统版本
     */
    public String getOSVersion() {

        return Build.VERSION.RELEASE;
    }

    /**
     * @return 获取系统版本号
     */
    public int getOSVersionCode() {

        return Build.VERSION.SDK_INT;
    }

    /**
     * @return 返回手机品牌和型号, 未获取则返回“”
     */
    public String getPhoneModel() {
        String phoneModel = "";

        try {
            String model = Build.MODEL;
            String brand = Build.BRAND;

            // 去掉重复的字段
            phoneModel = brand + " "
                    + model.toLowerCase().replace(brand.toLowerCase(), "");
            phoneModel = phoneModel.replace("  ", " ");
            phoneModel = phoneModel.toUpperCase();
        } catch (Exception e) {
        }

        return phoneModel + "";
    }

    /**
     * @return 手机是否已 Root
     */
    public boolean isPhoneRoot() {

        boolean isRoot = false;
        String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        for (int i = 0; i < kSuSearchPaths.length; i++) {
            File suFile = new File(kSuSearchPaths[i] + "su");
            if (suFile.exists()) {
                isRoot = true;
            }
        }
        return isRoot;
    }

    /**
     * 获取手机卡运营商（0：无运营商；1：CMCC中国移动；2：CUCC中国联通；3：CTCC中国电信）
     *
     * @param context 上下文
     * @return
     */
    public int getSimProvider(Context context) {

        int provider = PROVIDER_NULL;

        String imsi = getIMSI(context);
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                provider = PROVIDER_CMCC;
            } else if (imsi.startsWith("46001")) {
                provider = PROVIDER_CUCC;
            } else if (imsi.startsWith("46003")) {
                provider = PROVIDER_CTCC;
            }
        }

        return provider;
    }

    /**
     * 判断手机屏幕是否点亮
     *
     * @param context 上下文
     * @return
     */
    public boolean isScreenOn(Context context) {

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 获取手机号码
     *
     * @param context 上下文
     * @return
     */
    public String getPhoneNumber(Context context) {
        // 获取手机号码
        String te1 = "";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            te1 = tm.getLine1Number();// 获取本机号码
        } catch (Exception e) {
            e.printStackTrace();
        }
        return te1;
    }

    /**
     * 读取短信中心号
     */
    public String getSmsc(Context context) {
        String smsc = "";
        try {
            SmsManager sm = SmsManager.getDefault();
            String methodName = "getServiceCenterAddress";
            Class<SmsManager> clazz = SmsManager.class;
            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);
            smsc = (String) method.invoke(sm);
        } catch (Exception e) {
            // LogUtil.v(TAG, e.getMessage());
        }
        return smsc;
    }

    /*
     * 获得 客户端ip
     */
    public String GetHostIp(Context context) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }

    // Android获取系统正在运行的后台服务列表
    public String getRunningServicesInfo(Context context) {
        StringBuffer serviceInfo = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager
                .getRunningServices(100);

        Iterator<ActivityManager.RunningServiceInfo> l = services.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo) l
                    .next();
            serviceInfo.append("\nservice: ").append(si.service);
        }
        return serviceInfo.toString();
    }

    // 获取任务列表
    public ArrayList<String> getTasks(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        ArrayList<String> aryListTaskID = new ArrayList<String>();
        ArrayList<String> aryListTaskName = new ArrayList<String>();
        List<ActivityManager.RunningTaskInfo> mRunningTasks = mActivityManager
                .getRunningTasks(30);
        for (ActivityManager.RunningTaskInfo amTask : mRunningTasks) {
            aryListTaskName.add(amTask.baseActivity.getClassName());
        }

        return aryListTaskName;
    }

    // 获取手机经纬度
    public String getLatitude(Context context) {

        String latitude = null;

        LocationManager manager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);// 获取手机位置信息
        // 获取的条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 获取精准位置
        criteria.setCostAllowed(true);// 允许产生开销
        criteria.setPowerRequirement(Criteria.POWER_HIGH);// 消耗大的话，获取的频率高
        criteria.setSpeedRequired(true);// 手机位置移动
        criteria.setAltitudeRequired(true);// 海拔

        // 获取最佳provider: 手机或者模拟器上均为gps
        String bestProvider = manager.getBestProvider(criteria, true);// 使用GPS卫星

        System.out.println("最好的位置提供者是" + bestProvider);

        Location location = new Location(bestProvider);

        latitude = location.getLatitude() + "#" + location.getLongitude();// 经度

        System.out.println(">>>>>>>>>>>>>>>>latitude:" + latitude);

        return latitude;

    }

    // 获取当前正在运行的非系统应用
    public ArrayList<String> getTaskApks(Context context) {

        ArrayList<String> apks = new ArrayList<String>();

        try {

            ArrayList<String> taskInfos = getTasks(context);

            List<PackageInfo> packages = context.getPackageManager()
                    .getInstalledPackages(0);

            for (int k = 0; k < taskInfos.size(); k++) {

                String packageName = taskInfos.get(k);

                for (int i = 0; i < packages.size(); i++) {

                    ApplicationInfo packageInfo = packages.get(i).applicationInfo;

                    String name = packageInfo.packageName;

                    if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                            && packageName.indexOf(name) != -1) {
                        String appName = packageInfo.loadLabel(
                                context.getPackageManager()).toString();
                        apks.add(appName);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return apks;

    }

    /**
     * 返回当前系统安装的第三方应用的软件信息
     *
     * @return 应用程序的集合
     */
    public List<String> getMyAllApps(Context context) {

        List<String> apkNames = new ArrayList<String>();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pInfo = manager
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (int i = 0; i < pInfo.size(); i++) {
            ApplicationInfo appInfo = pInfo.get(i).applicationInfo;

            if (!isSystemApp(appInfo)) {
                apkNames.add(appInfo.loadLabel(manager).toString());
            }
        }
        return apkNames;
    }

    /**
     * 判断是否是系统应用
     *
     * @param info
     * @return
     */
    private boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        }
        return true;
    }

    /**
     * Get the current time format yyyyMMddHHmmss
     *
     * @return
     */
    public String getTimeForFeeInfo() {
        Date date = new Date();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        return localSimpleDateFormat.format(date);
    }

    public String getDevice(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        } else {
            return "000000000000000";
        }
    }

    public String getModel() {

        return Build.MODEL.replaceAll(" ", "");
    }

    /**
     * 获取手机厂商
     */

    public String getChangShang() {

        return Build.MANUFACTURER;

    }

    /**
     * 获取当前系统版本
     *
     * @param context context 当前context
     * @return String osVer 当前系统版本
     */
    public String getOsVer(Context context) {
        String osVer = "";
        try {
            osVer = Build.VERSION.RELEASE;
            return osVer + "";
        } catch (Exception e) {
        }
        return osVer;

    }

    /**
     * 获取当前应用的版本
     */
    public String getCurronProgramVer(Context context) {
        String currVer = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            currVer = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currVer;

    }

    /**
     * 获取当前系统时间 yyyy-MM-dd HH:mm:ss
     */
    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * 此方法一般都能够获取到IMSI 无高通手机没有亲测 MTK 和展讯手机亲测可行 一般放在卡1的SIM卡 普通方法即可获取 卡2的SIM卡
     * 一般需利用反射来获取 不同芯片的手机底层的方法也不一样 可能经过深度定制的手机无法获取到
     *
     * @param context
     * @return
     */
    @SuppressLint("UseValueOf")
    public String getIMSI(Context context) {
        String imsi = "";
        try { // 普通方法获取imsi
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                imsi = tm.getSubscriberId();
            }
            if (imsi == null || "".equals(imsi))
                if (tm != null) {
                    imsi = tm.getSimOperator();
                }
            Class<?>[] resources = new Class<?>[]{int.class};
            Integer resourcesId = new Integer(1);
            if (imsi == null || "".equals(imsi)) {
                try { // 利用反射获取 MTK手机
                    Method addMethod = tm.getClass().getDeclaredMethod(
                            "getSubscriberIdGemini", resources);
                    addMethod.setAccessible(true);
                    imsi = (String) addMethod.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try { // 利用反射获取 展讯手机
                    Class<?> c = Class
                            .forName("com.android.internal.telephony.PhoneFactory");
                    Method m = c.getMethod("getServiceName", String.class,
                            int.class);
                    String spreadTmService = (String) m.invoke(c,
                            Context.TELEPHONY_SERVICE, 1);
                    TelephonyManager tm1 = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    imsi = tm1.getSubscriberId();
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try { // 利用反射获取 高通手机
                    Method addMethod2 = tm.getClass().getDeclaredMethod(
                            "getSimSerialNumber", resources);
                    addMethod2.setAccessible(true);
                    imsi = (String) addMethod2.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                imsi = "000000";
            }
            return imsi;
        } catch (Exception e) {
            return "000000";
        }
    }

    /**
     * 获取 国际移动装备辨识码
     *
     * @param context context 当前context
     * @return String imei 国际移动装备辨识码
     */

    public String getIMEI(Context context) {
        String imei = "";
        try {

            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (tm.getDeviceId() != null) {
                    imei = tm.getDeviceId() + "";
                }
            }
        } catch (Exception e) {
        }
        return imei;
    }

    /**
     * Integrate circuit card identity 集成电路卡识别码（固化在手机SIM卡中）
     * ICCID为IC卡的唯一识别号码，共有20位数字组成，其编码格式为：XXXXXX 0MFSS YYGXX XXXXX。 分别介绍如下：
     * 前六位运营商代码：中国移动的为：898600；898602 ，中国联通的为：898601，中国电信898603
     *
     * @param context context 当前context
     * @return String iccid 集成电路卡识别码
     */
    public String getICCID(Context context) {
        String iccid = "";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (tm.getDeviceId() != null) {
                    iccid = tm.getSimSerialNumber() + "";
                }
            }
            if (iccid == null || iccid.equals("null")) {
                iccid = "";
            }
        } catch (Exception e) {
        }
        return iccid + "";
    }

    /**
     * 判断当前应用是否退出
     *
     * @param context
     * @return
     */
    public boolean isRunning(Context context) {

        try {

            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            String packageName = info.packageName;

            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningAppProcessInfo> infos = am
                    .getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo rapi : infos) {
                if (rapi.processName.equals(packageName))
                    return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取 移动 联通 当前手机的基站信息 获取 lac
     */

    public String etGSMCellLocation_lac(Context context) {
        try {

            if (getIMSI(context).startsWith("46000")
                    || getIMSI(context).startsWith("46001")
                    || getIMSI(context).startsWith("46002")) {

                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);

                GsmCellLocation location = (GsmCellLocation) manager
                        .getCellLocation();

                /** 通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
                int lac = location.getLac();
                // int cellid = location.getCid();
                return lac + "";
            } else if (getIMSI(context).startsWith("46003")) {
                return "123456";
            } else {
                return "014785";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "00000";
        }
    }

    /**
     * 获取 移动 联通 当前手机的基站信息 获取 cellid
     */

    public String etGSMCellLocation_cellid(Context context) {
        try {
            if (getIMSI(context).startsWith("46000")
                    || getIMSI(context).startsWith("46001")
                    || getIMSI(context).startsWith("46002")) {
                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);

                GsmCellLocation location = (GsmCellLocation) manager
                        .getCellLocation();

                /** 通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
                // int lac = location.getLac();
                int cellid = location.getCid();
                return cellid + "";
            } else if (getIMSI(context).startsWith("46003")) {
                return "123456";
            } else {
                return "014785";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "00000";
        }
    }

    /**
     * 获取 电信 当前手机的基站信息 获取 lac
     */

    public String dianxin_etGSMCellLocation_lac(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            CdmaCellLocation location1 = (CdmaCellLocation) manager
                    .getCellLocation();

            int lac = location1.getNetworkId();
            return lac + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "123456";
        }
    }

    /**
     * 获取 电信 当前手机的基站信息 获取 cellid
     */
    public String dianxin_etGSMCellLocation_cellid(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            GsmCellLocation location = (GsmCellLocation) manager
                    .getCellLocation();

            int cellid = location.getCid();
            return cellid + "";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "123456";
        }
    }

    /**
     * 获取当前Mac地址
     *
     * @param context context 当前context
     * @return String macId Mac地址
     */

    public String getMacId(Context context) {

        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (wifi != null) {
            info = wifi.getConnectionInfo();
        }
        if (info.getMacAddress() == null) {
            return "0.0.0.0";// getLocalMacAddressFromBusybox();
        }
        return info.getMacAddress() + "";
    }

    // 根据busybox获取本地Mac
    public String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "网络出错，请检查网络";
        }

        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            result = Mac;
        }
        return result;
    }

    private String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
            }

            result = line;
            // Log.i("test","result: "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取当前手机卡状态,simState[0]为5时标明卡可用
     *
     * @param context context 当前context
     * @return String[] simState 0为状态，1为状态说明
     */
    public String[] getSimState(Context context) {
        String[] simState = new String[2];
        try {

            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getSimState()) {
                case 0:// 未知状态
                    simState[0] = "0";
                    simState[1] = "未知状态";
                    break;
                case 1:// 卡不存在
                    simState[0] = "1";
                    simState[1] = "卡不存在";
                    break;
                case 2:// 锁定状态，需要用户的PIN码解锁
                    simState[0] = "2";
                    simState[1] = "锁定状态，需要用户的PIN码解锁";
                    break;
                case 3:// 锁定状态，需要用户的PUK码解锁
                    simState[0] = "3";
                    simState[1] = "锁定状态，需要用户的PUK码解锁";
                    break;
                case 4:// 锁定状态，需要网络的PIN码解锁
                    simState[0] = "4";
                    simState[1] = "锁定状态，需要网络的PIN码解锁";
                    break;
                case 5:// 就绪状态
                    simState[0] = "5";
                    simState[1] = "就绪状态";
                    break;

                default:
                    simState[0] = "0";
                    simState[1] = "未知状态";
                    break;
            }

        } catch (Exception e) {
        }
        return simState;

    }

    /**
     * 网络是否连接
     */
    public boolean isNetworkAvailable(Context context) {

        try {

            ConnectivityManager cManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * Role:Telecom service providers获取手机服务商信息 <BR>
     * 需要加入权限<uses-permission
     * android:name="android.permission.READ_PHONE_STATE"/> <BR>
     */
    public String getProvidersName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String ProvidersName = "运营商";

        if (getSimState(context)[0].equals("5")) {
            // 返回唯一的用户ID;就是这张卡的编号神马的
            String IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        }
        return ProvidersName;
    }

    /**
     * 获取手机号的运营商信息
     *
     * @param context
     * @return
     */
    public String getProvidersStr(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String providersStr = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = telephonyManager.getSubscriberId();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            providersStr = "本次支付由中国移动代收\n客服电话：10086";
        } else if (IMSI.startsWith("46001")) {
            providersStr = "本次支付由中国联通代收\n客服电话：10010";
        } else if (IMSI.startsWith("46003")) {
            providersStr = "本次支付由中国电信代收\n客服电话：10000";
        }
        return providersStr;
    }

    /**
     * NETWORK_TYPE_CDMA 网络类型为CDMA NETWORK_TYPE_EDGE 网络类型为EDGE
     * NETWORK_TYPE_EVDO_0 网络类型为EVDO0 NETWORK_TYPE_EVDO_A 网络类型为EVDOA
     * NETWORK_TYPE_GPRS 网络类型为GPRS NETWORK_TYPE_HSDPA 网络类型为HSDPA
     * NETWORK_TYPE_HSPA 网络类型为HSPA NETWORK_TYPE_HSUPA 网络类型为HSUPA
     * NETWORK_TYPE_UMTS 网络类型为UMTS
     */
    public int getCurrentNetType(Context context) {

        try {

            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connectMgr.getActiveNetworkInfo();

            return info.getSubtype();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return -1;

    }

    /**
     * 得到当前的手机网络类型
     *
     * @param context
     * @return
     */
    public String getSubtype(Context context) {
        String type = "null";
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                type = "null";
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                type = "wifi";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                int subType = info.getSubtype();
                if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                        || subType == TelephonyManager.NETWORK_TYPE_GPRS
                        || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                    type = "2g";
                } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                        || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                    type = "3g";
                } else if (subType == 14) {
                    // TelephonyManager.NETWORK_TYPE_LTE ,
                    // LTE是3g到4g的过渡，是3.9G的全球标准
                    type = "4g";
                }
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
        return type;
    }

    /**
     * 判断手机号是否合法
     *
     * @param mobiles
     * @return
     */
    public boolean isMobileNumber(String mobiles) {

        /**
         * 移动手机号码的正则表达式。
         */
        String REGEX_CHINA_MOBILE = "1(3[4-9]|4[7]|5[012789]|8[278])\\d{8}";

        /**
         * 联通手机号码的正则表达式。
         */
        String REGEX_CHINA_UNICOM = "1(3[0-2]|5[56]|8[56])\\d{8}";

        /**
         * 电信手机号码的正则表达式。
         */
        String REGEX_CHINA_TELECOM = "(?!00|015|013)(0\\d{9,11})|(1(33|53|80|89)\\d{8})";

        if (mobiles != null
                && (mobiles.matches(REGEX_CHINA_MOBILE)
                || mobiles.matches(REGEX_CHINA_UNICOM) || mobiles
                .matches(REGEX_CHINA_TELECOM))) {

            return true;

        }
        return false;
    }

    public String hmacSign(String body, String appKey) {
        byte k_ipad[] = new byte[64];
        byte k_opad[] = new byte[64];
        byte keyb[];
        byte value[];
        try {
            keyb = appKey.getBytes("UTF-8");
            value = body.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            keyb = appKey.getBytes();
            value = body.getBytes();
        }
        Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
        Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
        for (int i = 0; i < keyb.length; i++) {
            k_ipad[i] = (byte) (keyb[i] ^ 0x36);
            k_opad[i] = (byte) (keyb[i] ^ 0x5c);
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(k_ipad);
        md.update(value);
        byte dg[] = md.digest();
        md.reset();
        md.update(k_opad);
        md.update(dg, 0, 16);
        dg = md.digest();
        return toHex(dg);
    }

    public String toHex(byte input[]) {
        if (input == null)
            return null;
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16)
                output.append("0");
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }
}
