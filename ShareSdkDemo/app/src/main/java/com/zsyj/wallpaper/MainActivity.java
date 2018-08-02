package com.zsyj.wallpaper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zsyj.sharesdk.CommonManager;
import com.zsyj.sharesdk.Constant;
import com.zsyj.sharesdk.QQLoginActivity;
import com.zsyj.sharesdk.bean.CheckUpdateInfo;
import com.zsyj.sharesdk.bean.LoginUserInfo;
import com.zsyj.sharesdk.download.DownloadListener;
import com.zsyj.sharesdk.download.FileDownloader;
import com.zsyj.sharesdk.download.entity.FileInfo;
import com.zsyj.sharesdk.utils.AppInfoHelper;
import com.zsyj.sharesdk.utils.FileUtils;
import com.zsyj.sharesdk.utils.ShareUtil;
import com.zsyj.sharesdk.utils.StringUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button wxLogin;
    private Button qqLogin;
    private Button bt_wxShareFriend;
    private Button bt_wxShareCircle;
    private Button bt_qqShareFriend;
    private Button bt_qqShareCircle;
    private Button bt_wxPay;
    private Button bt_alipay;
    private Button bt_checkUpdae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wxLogin = (Button) findViewById(R.id.bt_wxlogin);
        wxLogin.setOnClickListener(this);
        qqLogin = (Button) findViewById(R.id.bt_qqlogin);
        qqLogin.setOnClickListener(this);
        bt_wxShareFriend = (Button) findViewById(R.id.bt_wxShareFriend);
        bt_wxShareFriend.setOnClickListener(this);
        bt_wxShareCircle = (Button) findViewById(R.id.bt_wxShareCircle);
        bt_wxShareCircle.setOnClickListener(this);
        bt_qqShareFriend = (Button) findViewById(R.id.bt_qqShareFriend);
        bt_qqShareFriend.setOnClickListener(this);
        bt_qqShareCircle = (Button) findViewById(R.id.bt_qqShareCircle);
        bt_qqShareCircle.setOnClickListener(this);
        bt_wxPay = (Button) findViewById(R.id.bt_wxPay);
        bt_wxPay.setOnClickListener(this);
        bt_alipay = (Button) findViewById(R.id.bt_alipay);
        bt_alipay.setOnClickListener(this);
        bt_checkUpdae = (Button) findViewById(R.id.bt_checkUpdae);
        bt_checkUpdae.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
        intentFilter.addAction(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
        intentFilter.addAction(Constant.PAY_SUCCESS_BROADCAST_ACTION);
        intentFilter.addAction(Constant.UPDATE_SUCCESS_BROADCAST_ACTION);
        registerReceiver(receiver, intentFilter);
        //初始化
        CommonManager.getInstance().init(this, Constant.WEIXIN_APP_ID, Constant.WEIXIN_SECRET, Constant.WEIXIN_PAY_ID, Constant.QQ_APP_ID);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //微信登录
            case R.id.bt_wxlogin:
                CommonManager.getInstance().wxLogin(getApplication(), Constant.LOGIN_URL);
                break;
            //QQ登录
            case R.id.bt_qqlogin:
                Intent intent = new Intent(MainActivity.this, QQLoginActivity.class);
                intent.putExtra(Constant.LOGIN_URL_ACTION, Constant.LOGIN_URL);
                startActivity(intent);
                break;
            //微信好友分享
            case R.id.bt_wxShareFriend:
                //app icon
                Bitmap thumb = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                        R.mipmap.ic_launcher);
                //参数：context，flag：0，分享到朋友，1，分享到朋友圈，title,标题，description:分享内容，url,分享点击跳转地址，bitmap,分享图标
                ShareUtil.share2weixin(MainActivity.this, 0, "标题", "描述", "http://vs.zsyj.com.cn/info/recharge/shape.php", thumb);
                break;
            //微信朋友圈分享
            case R.id.bt_wxShareCircle:
                //app icon
                Bitmap thumb1 = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                        R.mipmap.ic_launcher);
                //参数：context，flag：0，分享到朋友，1，分享到朋友圈，title,标题，description:分享内容，url,分享点击跳转地址，bitmap,分享图标
                ShareUtil.share2weixin(MainActivity.this, 1, "标题", "描述", "http://vs.zsyj.com.cn/info/recharge/shape.php", thumb1);
                break;
            //QQ好友分享
            case R.id.bt_qqShareFriend:
                //参数：context，title,标题，description:分享内容，url,分享点击跳转地址，shareIconUrl,应用图片在线地址，bitmap,分享图标,type：0，分享到朋友，1，分享到空间，
                ShareUtil.share2Qq(MainActivity.this, "标题", "描述", "http://vs.zsyj.com.cn/info/recharge/shape.php", "http://vs.zsyj.com.cn/info/recharge/images/head.png", 0);
                break;
            //QQ空间分享
            case R.id.bt_qqShareCircle:
                //参数：context，title,标题，description:分享内容，url,分享点击跳转地址，shareIconUrl,应用图片在线地址，bitmap,分享图标,type：0，分享到朋友，1，分享到空间，
                ShareUtil.share2Qq(MainActivity.this, "标题", "描述", "http://vs.zsyj.com.cn/info/recharge/shape.php", "http://vs.zsyj.com.cn/info/recharge/images/head.png", 1);
                break;
            //微信支付
            case R.id.bt_wxPay:
                CommonManager.getInstance().wxPay("partnerid", "prepayid", "noncestr", "timestamp", "sign");
                break;
            //支付宝支付
            case R.id.bt_alipay:
                CommonManager.getInstance().aliPay(MainActivity.this, "orderInfo");
                break;
            //检测升级
            case R.id.bt_checkUpdae:
                FileDownloader.init(this, "/Download/");
                CommonManager.getInstance().checkUpdate(MainActivity.this, Constant.CHECK_UPDATE_URL, Constant.PID, AppInfoHelper.getSid(getApplicationContext()), AppInfoHelper.getAppVersionString(getApplicationContext()), AppInfoHelper.getVersionCode(getApplicationContext()) + "");
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //status:1:成功，0：失败，2：未安装微信,3未安装qq，4：未安装支付宝
            if (StringUtils.isSameStr(intent.getAction(), Constant.LOGIN_SUCCESS_BROADCAST_ACTION)) {
                //登录成功
                if (intent.getIntExtra("status", 0) == 1) {
                    LoginUserInfo loginUserInfo = (LoginUserInfo) FileUtils.getCacheDataFromFile(MainActivity.this, Constant.LOGIN_USER_INFO);
                    Log.e("luo", "loginUserInfo--------->>>>>>>>>>>>" + loginUserInfo);
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 0) {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 2) {
                    Toast.makeText(MainActivity.this, "未安装微信", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 3) {
                    Toast.makeText(MainActivity.this, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
            } else if (StringUtils.isSameStr(intent.getAction(), Constant.SHARE_SUCCESS_BROADCAST_ACTION)) {
                //分享成功
                if (intent.getIntExtra("status", 0) == 1) {
                    Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 0) {
                    Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 2) {
                    Toast.makeText(MainActivity.this, "未安装微信", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 3) {
                    Toast.makeText(MainActivity.this, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
            } else if (StringUtils.isSameStr(intent.getAction(), Constant.PAY_SUCCESS_BROADCAST_ACTION)) {
                //支付成功
                if (intent.getIntExtra("status", 0) == 1) {
                    Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 0) {
                    Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 2) {
                    Toast.makeText(MainActivity.this, "未安装微信", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("status", 0) == 4) {
                    Toast.makeText(MainActivity.this, "未安装支付宝", Toast.LENGTH_SHORT).show();
                }
            } else if (StringUtils.isSameStr(intent.getAction(), Constant.UPDATE_SUCCESS_BROADCAST_ACTION)) {
                if (intent.getIntExtra("status", 0) == 1) {
                    Toast.makeText(MainActivity.this, "获取升级信息成功", Toast.LENGTH_SHORT).show();
                    CheckUpdateInfo checkUpdateInfo = (CheckUpdateInfo) FileUtils.getCacheDataFromFile(MainActivity.this, Constant.UPDATE_INFO);
                    Log.e("luo", "checkUpdateInfo----->>>>>>>>>>>" + checkUpdateInfo);
                } else if (intent.getIntExtra("status", 0) == 0) {
                    Toast.makeText(MainActivity.this, "获取升级信息失败", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    /**
     * 下载调用示例
     */
    private void downLoadApp(String url, String name) {
        FileDownloader.start(url, name, new DownloadListener() {
            double progress = 0;

            @Override
            public void onStart(FileInfo fileInfo) {
            }

            @Override
            public void onUpdate(FileInfo fileInfo) {
                double loadBytes = fileInfo.getLoadBytes();
                double totalBytes = fileInfo.getTotalBytes();
                progress = (loadBytes / totalBytes) * 100;
            }

            @Override
            public void onStop(FileInfo fileInfo) {
            }

            @Override
            public void onComplete(FileInfo fileInfo) {

            }

            @Override
            public void onCancel(FileInfo fileInfo) {
            }

            @Override
            public void onError(FileInfo fileInfo, String errorMsg) {
            }
        }, true);
    }

    /**
     * 安装
     */
    private void installApk(Activity activity, String apkPath) {
        AppInfoHelper.installAPK(activity, apkPath);
    }
}

