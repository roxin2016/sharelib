package com.zsyj.wallpaper.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zsyj.sharesdk.Constant;
import com.zsyj.sharesdk.CommonManager;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI weiXinApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weiXinApi = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID, true);
        weiXinApi.handleIntent(getIntent(), this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    //微信登录成功
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        CommonManager.getInstance().wxLoginLogic(resp);
                        break;
                    //微信分享成功
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 1);
                        WXEntryActivity.this.sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                switch (resp.getType()) {
                    //微信登录取消
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        break;
                    //微信分享取消
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        WXEntryActivity.this.sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                switch (resp.getType()) {
                    //微信登录拒绝
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        break;
                    //微信分享拒绝
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        WXEntryActivity.this.sendBroadcast(intent);
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;

        }

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        weiXinApi.handleIntent(intent, this);
        finish();
    }
}
