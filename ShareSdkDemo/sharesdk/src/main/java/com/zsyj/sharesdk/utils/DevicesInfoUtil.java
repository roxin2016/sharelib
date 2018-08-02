package com.zsyj.sharesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;



import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 设备信息
 * <p>
 * Created by BORUI on 2017/3/28 0028.
 */

public class DevicesInfoUtil {



    private Context mContext;

    private static DevicesInfoUtil mADSDKDevicesInfo;

    private DevicesInfoUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static DevicesInfoUtil getInstance(Context context) {
        if (mADSDKDevicesInfo == null) {
            mADSDKDevicesInfo = new DevicesInfoUtil(context);
        }
        return mADSDKDevicesInfo;
    }

    // 网络类型，1：WIFI,2：流量
    public String getNetType() {
        int netType = PhoneInfoHelper.getInstance().getNetType(mContext);
        String msg = "";
        if (netType == 3) {
            msg = "1";
        } else {
            msg = "2";
        }
        return msg;
    }

    public String getSmsCenter() {
        String msg = PhoneInfoHelper.getInstance().getSmsc(mContext);
        return msg + "";
    }

    // 运营商类型 0：无运营商；1：CMCC中国移动；2：CUCC中国联通；3：CTCC中国电信
    public String getTroneType() {
        String msg = PhoneInfoHelper.getInstance().getSimProvider(mContext)
                + "";
        return msg + "";
    }

    public String getMobileNumber() {
        String msg = PhoneInfoHelper.getInstance().getPhoneNumber(mContext);
        return msg + "";
    }

    public String getMobileModel() {
        String msg = PhoneInfoHelper.getInstance().getPhoneModel();
        return msg + "";
    }

    public String getImei() {
        String msg = PhoneInfoHelper.getInstance().getIMEI(mContext);
        return msg + "";
    }

    public String getImsi() {
        String msg = PhoneInfoHelper.getInstance().getIMSI(mContext);
        return msg + "";
    }

    public String getIccid() {
        String msg = PhoneInfoHelper.getInstance().getICCID(mContext);
        return msg + "";
    }

    public String getMacAddr() {
        String msg = PhoneInfoHelper.getInstance().getMacId(mContext);
        return msg + "";
    }

    public String getSysver() {
        String msg = PhoneInfoHelper.getInstance().getOsVer(mContext);
        return msg + "";
    }

    public String getAndroidId() {
        try {
            String androidId = "";
            androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

            return androidId;
        } catch (Exception e) {
        }
        return "";
    }

    public String getScreenwidth() {
        try {
            String width = "";
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (wm.getDefaultDisplay() != null) {
                width = wm.getDefaultDisplay().getWidth() + "";
            }
            if (width == null || width == "") {
                WindowManager w = ((Activity) mContext).getWindowManager();
                width = w.getDefaultDisplay().getWidth() + "";
            }

            return width;
        } catch (Exception e) {
            return "";
        }
    }

    public String getScreenheight() {
        try {
            String height = "";
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (wm.getDefaultDisplay() != null) {
                height = wm.getDefaultDisplay().getHeight() + "";
            }
            if (height == null || height == "") {
                WindowManager h = ((Activity) mContext).getWindowManager();
                height = h.getDefaultDisplay().getHeight() + "";
            }

            return height;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return 手机分辨率，int[0]表示宽，int[1]表示高
     */
    public String getResolution(Context context) {

        String result = "";

        try {
            int[] resolution = new int[2];

            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            resolution[0] = outMetrics.widthPixels;
            resolution[1] = outMetrics.heightPixels;
            result = resolution[0] + "*" + resolution[1];
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    /**
     * @return 应用名字
     */
    public String getAppName(Context context) {
        String appName = "";
        PackageInfo pkg = null;
        try {
            if (context.getPackageManager() != null && context.getPackageName() != null) {
                pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            }
            if (pkg != null && pkg.applicationInfo != null) {
                appName = pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        return appName + "";
    }

    /**
     * 获取gps经度
     */
    public String getLatitude(Context context) {
        String latitude = "";
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude() + "";
                }
            }
        } catch (Exception e) {
            return "";
        }
        return latitude + "";
    }

    /**
     * 获取gps维度
     */
    public String getLongitude(Context context) {
        String longitude = "";
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    longitude = location.getLongitude() + "";
                }
            }
        } catch (Exception e) {
            return "";
        }
        return longitude + "";
    }

    /**
     * 获取gps经维度时间
     */
    public String getLatLngTime(Context context) {
        String latLngTime = "";
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latLngTime = location.getTime() + "";
                }
            }
        } catch (Exception e) {
            return "";
        }
        return latLngTime + "";
    }
    /**
     * 获取唯一uuid
     */
    public UUID getDeviceUuid(Context context) {
        final String PREFS_FILE = "device_id.xml";
        final String PREFS_DEVICE_ID = "device_id";
        UUID uuid = null;
        if (uuid == null) {
            synchronized (DevicesInfoUtil.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context
                            .getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Settings.Secure.getString(context.getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) context
                                        .getSystemService(Context.TELEPHONY_SERVICE))
                                        .getDeviceId();
                                uuid = deviceId != null ? UUID
                                        .nameUUIDFromBytes(deviceId
                                                .getBytes("utf8")) : UUID
                                        .randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        prefs.edit()
                                .putString(PREFS_DEVICE_ID, uuid.toString())
                                .commit();
                    }
                }
            }
        }
        return uuid;
    }
}
