package com.zsyj.sharesdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.carozhu.rxhttp.ApiHelper;
import com.carozhu.rxhttp.retrofitOkhttp.RetrofitOkhttpClient;
import com.carozhu.rxhttp.rx.RxSchedulersHelper;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.zsyj.sharesdk.bean.PayResult;
import com.zsyj.sharesdk.utils.DevicesInfoUtil;
import com.zsyj.sharesdk.utils.FileUtils;
import com.zsyj.sharesdk.utils.MySystemIntent;
import com.zsyj.sharesdk.utils.MyTools;
import com.zsyj.sharesdk.utils.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录管理单例
 */

public class CommonManager {
    public static CommonManager wxLoginManager;
    private Context context;
    private IWXAPI weiXinApi = null;
    private Tencent mTencent = null;
    private Application application;

    public static synchronized CommonManager getInstance() {
        if (wxLoginManager == null) {
            wxLoginManager = new CommonManager();
        }
        return wxLoginManager;
    }

    //初始化
    public void init(Context context, String WEIXIN_APP_ID, String WEIXIN_SECRET, String WEIXIN_PAY_ID, String QQ_APP_ID) {
        this.context = context;
        Constant.WEIXIN_APP_ID = WEIXIN_APP_ID;
        Constant.WEIXIN_SECRET = WEIXIN_SECRET;
        Constant.WEIXIN_PAY_ID = WEIXIN_PAY_ID;
        Constant.QQ_APP_ID = QQ_APP_ID;
        //微信初始化
        weiXinApi = WXAPIFactory.createWXAPI(context, Constant.WEIXIN_APP_ID, true);
        weiXinApi.registerApp(Constant.WEIXIN_APP_ID);
        //QQ初始化
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, context);
    }

    private CommonManager() {

    }

    public IWXAPI getWeiXinApi() {
        return weiXinApi;
    }

    public Tencent getTencentpi() {
        return mTencent;
    }

    //微信登录
    public void wxLogin(Application app, String url) {
        application = app;
        Constant.LOGIN_URL = url;
        if (!weiXinApi.isWXAppInstalled()) {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 2);
            context.sendBroadcast(new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION));
            return;
        }

        try {
            RetrofitOkhttpClient.init(application);
            RetrofitOkhttpClient.getInstance()
                    .setBaseUrl(Constant.WX_API_URL)
                    .addLogInterceptor(true,"1.0.6");

            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo";

            //设置是微信登陆
            weiXinApi.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 微信登录的逻辑
     *
     * @param resp
     */
    public void wxLoginLogic(BaseResp resp) {
        try {
            //登录回调,处理登录成功的逻辑
            if (resp instanceof SendAuth.Resp) {
                SendAuth.Resp newResp = (SendAuth.Resp) resp;
                //获取微信传回的code
                String code = newResp.code;
                ApiHelper.getInstance().getAPIService(ApiService.class)
                        .getWxOPenId(Constant.WEIXIN_APP_ID, Constant.WEIXIN_SECRET, code, "authorization_code")
                        .compose(RxSchedulersHelper.io_main())
                        .subscribe(wxOpenIdInfo -> {
                            ApiHelper.getInstance().getAPIService(ApiService.class)
                                    .getWxUserInfo(wxOpenIdInfo.getAccess_token(), wxOpenIdInfo.getOpenid())
                                    .compose(RxSchedulersHelper.io_main())
                                    .subscribe(wxUserInfo -> {
                                        Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                                        intent.putExtra("status", 1);
                                        context.sendBroadcast(intent);
                                        FileUtils.saveCachDataToFile(context, Constant.LOGIN_USER_INFO, wxUserInfo);
                                    }, throwable -> {
                                        Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                                        intent.putExtra("status", 0);
                                        context.sendBroadcast(intent);
                                    });
                        }, throwable -> {
                            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
                            intent.putExtra("status", 0);
                            context.sendBroadcast(intent);
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            context.sendBroadcast(intent);
        }
    }

    //微信支付
    public void wxPay(String partnerid, String prepayid, String noncestr, String timestamp, String sign) {

        try {
            if (!MySystemIntent.isHaveApp(context, Constant.WECHAT_PACKAGENAME)) {
                Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
                intent.putExtra("status", 2);
                context.sendBroadcast(intent);
                return;
            }
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, Constant.WEIXIN_PAY_ID);
            iwxapi.registerApp(Constant.WEIXIN_PAY_ID);
            //微信官方支付
            PayReq req = new PayReq();
            req.appId = Constant.WEIXIN_APP_ID;
            req.partnerId = partnerid + "";
            req.prepayId = prepayid + "";
            req.nonceStr = noncestr;
            req.timeStamp = timestamp + "";
            req.packageValue = "Sign=WXPay";
            req.sign = sign;
            req.extData = "app data";
            iwxapi.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            context.sendBroadcast(intent);
        }
    }

    //支付宝支付
    public void aliPay(Activity activity, String orderInfo) {
        try {
            if (!MySystemIntent.isHaveApp(context, Constant.ALIPAY_PACKAGENAME)) {
                Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
                intent.putExtra("status", 4);
                context.sendBroadcast(intent);
                return;
            }
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(activity);
                    final Map<String, String> result = alipay.payV2(orderInfo, true);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            PayResult payResult = new PayResult(result);
                            // 同步返回需要验证的信息
                            String resultInfo = payResult.getResult();
                            String resultStatus = payResult.getResultStatus();
                            //{"alipay_trade_app_pay_response":{"code":"10000","msg":"Success","app_id":"2016100902063301","auth_app_id":"2016100902063301","charset":"utf-8","timestamp":"2016-11-04 14:26:55","total_amount":"0.01","trade_no":"2016110421001004760222792636","seller_id":"2088521039858812","out_trade_no":"PLS2950"},"sign":"fLMBpQN87e1J6UmSdOW3exgNvjyVbHwqK9lZezMVm4BHRCYFUgV9rDCr9SdmQJPQtfJ6dnw3cvrsmuJJT84aiCwneQ/3+8c9iKfYh1J6KkmxCWqjZY/r95riImUzVh40uiVr+O2WUe79aTp83n5pxvmQUB3qECwRE/gSfSzLHXE=","sign_type":"RSA"}
                            // 判断resultStatus 为9000则代表支付成功
                            if (TextUtils.equals(resultStatus, "9000")) {
                                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                                Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
                                intent.putExtra("status", 1);
                                context.sendBroadcast(intent);
                            } else {
                                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                                Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
                                intent.putExtra("status", 0);
                                context.sendBroadcast(intent);
                            }
                        }
                    });
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            context.sendBroadcast(intent);
        }
    }

    //应用升级
    public void checkUpdate(Activity activity, String updateUrl, String pid, String sid, String version, String vercode) {
        Constant.CHECK_UPDATE_URL = updateUrl;
        RetrofitOkhttpClient.init(activity.getApplication());
        Map<String, String> map = new HashMap<>();
        map.put("pid", pid + "");
        map.put("sid", sid + "");
        map.put("pname", context.getPackageName() + "");
        try {
            map.put("qname", MyTools.getSign(context, context.getPackageName() + ""));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("version", version + "");
        map.put("sver", DevicesInfoUtil.getInstance(context).getSysver() + "");
        map.put("noncestr", StringUtils.getUUID() + "");
        map.put("timestamp", System.currentTimeMillis() / 1000 + "");
        map.put("uuid", DevicesInfoUtil.getInstance(context).getDeviceUuid(context) + "");
        map.put("vercode", vercode + "");
        map.put("style", "1");
        ApiHelper.getInstance().getAPIService(ApiService.class)
                .checkUpdate(Constant.CHECK_UPDATE_URL, map)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(checkUpdateInfo -> {
                    FileUtils.saveCachDataToFile(context, Constant.UPDATE_INFO, checkUpdateInfo);
                    Intent intent = new Intent(Constant.UPDATE_SUCCESS_BROADCAST_ACTION);
                    intent.putExtra("status", 1);
                    context.sendBroadcast(intent);
                }, throwable -> {
                    Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
                    intent.putExtra("status", 0);
                    context.sendBroadcast(intent);
                });
    }
}
