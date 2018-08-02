package com.zsyj.sharesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.carozhu.rxhttp.ApiHelper;
import com.carozhu.rxhttp.retrofitOkhttp.RetrofitOkhttpClient;
import com.carozhu.rxhttp.rx.RxSchedulersHelper;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zsyj.sharesdk.bean.LoginUserInfo;
import com.zsyj.sharesdk.utils.AppInfoHelper;
import com.zsyj.sharesdk.utils.DevicesInfoUtil;
import com.zsyj.sharesdk.utils.FileUtils;
import com.zsyj.sharesdk.utils.MySystemIntent;
import com.zsyj.sharesdk.utils.MyTools;
import com.zsyj.sharesdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QQ登录
 */

public class QQLoginActivity extends Activity {
    private String openId, name, iconUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String loginUrl = getIntent().getStringExtra(Constant.LOGIN_URL_ACTION);
        Constant.LOGIN_URL = loginUrl;
        qqLogin();
    }

    /**
     * 点击调起qq登录
     */
    private void qqLogin() {
        if (MySystemIntent.isHaveApp(QQLoginActivity.this, Constant.QQ_PACKAGENAME)) {
            CommonManager.getInstance().getTencentpi().login(QQLoginActivity.this, "all", loginUiListener);
        } else {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 3);
            QQLoginActivity.this.sendBroadcast(new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION));
            finish();
        }

    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    private IUiListener loginUiListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            try {
                JSONObject jsonObject = (JSONObject) response;
                String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                    CommonManager.getInstance().getTencentpi().setAccessToken(token, expires);
                    CommonManager.getInstance().getTencentpi().setOpenId(openId);
                    com.tencent.connect.UserInfo userInfo = new com.tencent.connect.UserInfo(QQLoginActivity.this, CommonManager.getInstance().getTencentpi().getQQToken());
                    userInfo.getUserInfo(userUiListener);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            QQLoginActivity.this.sendBroadcast(intent);
            finish();
        }

        @Override
        public void onCancel() {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            QQLoginActivity.this.sendBroadcast(intent);
            finish();
        }
    };
    private String gender;
    private IUiListener userUiListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            try {
                JSONObject jsonObject = (JSONObject) response;
                name = jsonObject.getString("nickname");
                iconUrl = jsonObject.getString("figureurl_qq_2");
                gender = jsonObject.getString("gender");
                if (iconUrl.equals("")) {
                    iconUrl = "-1";
                }
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(iconUrl) && !TextUtils.isEmpty(openId)) {

                    LoginUserInfo loginUserInfo = new LoginUserInfo();
                    loginUserInfo.setOpenid(openId);
                    loginUserInfo.setNickname(name);
                    loginUserInfo.setHeadimgurl(iconUrl);
                    try {
                        if (gender.equals("男")) {
                            loginUserInfo.setSex(1);
                        } else {
                            loginUserInfo.setSex(2);
                        }
                        FileUtils.saveCachDataToFile(getApplicationContext(), Constant.LOGIN_USER_INFO, loginUserInfo);
                        Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 1);
                        QQLoginActivity.this.sendBroadcast(intent);
                        QQLoginActivity.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        QQLoginActivity.this.sendBroadcast(intent);
                        QQLoginActivity.this.finish();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                intent.putExtra("status", 0);
                QQLoginActivity.this.sendBroadcast(intent);
                QQLoginActivity.this.finish();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            QQLoginActivity.this.sendBroadcast(intent);
            finish();
        }

        @Override
        public void onCancel() {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            QQLoginActivity.this.sendBroadcast(intent);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN ||
                requestCode == com.tencent.connect.common.Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


