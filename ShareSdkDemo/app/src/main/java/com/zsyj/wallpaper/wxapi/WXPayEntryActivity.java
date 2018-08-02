package com.zsyj.wallpaper.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zsyj.sharesdk.Constant;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_PAY_ID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int code = resp.errCode;
        //显示充值成功的页面和需要的操作
        if (code == 0) {
            Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 1);
            sendBroadcast(intent);
        }
        //错误
        if (code == -1) {
            Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            sendBroadcast(intent);
        }
        //用户取消
        if (code == -2) {
            Intent intent = new Intent(Constant.PAY_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 0);
            sendBroadcast(intent);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}