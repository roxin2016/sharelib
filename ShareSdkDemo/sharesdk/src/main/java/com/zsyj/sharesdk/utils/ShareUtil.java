package com.zsyj.sharesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zsyj.sharesdk.Constant;
import com.zsyj.sharesdk.CommonManager;

import java.util.ArrayList;

/**
 * 微信、qq分享
 */

public class ShareUtil {
    /**
     * 分享到QQ
     */
    public static void share2Qq(final Context context, String title, String content, String url, String shareIconUrl, int type) {
        if (MySystemIntent.isHaveApp(context, Constant.QQ_PACKAGENAME)) {
            Bundle bundle = new Bundle();
            // 这条分享消息被好友点击后的跳转URL。
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
            // 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            // 分享的图片URL
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareIconUrl);
            // 分享的消息摘要，最长50个字
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
            // 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
            // bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "我在测试_appname");
            // 标识该消息的来源应用，值为应用名称+AppId。
            // bundle.putString(QQShare.SHARE_TO_QQ_KEY_TYPE, "星期几");
            if (type == 0) {
                CommonManager.getInstance().getTencentpi().shareToQQ((Activity) context, bundle, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 1);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(UiError e) {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onCancel() {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        context.sendBroadcast(intent);
                    }
                });
            } else if (type == 1) {
                bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                //下面这个必须加上  不然无法调动 qq空间
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(shareIconUrl);
                bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                Tencent.createInstance(Constant.QQ_APP_ID, context).shareToQzone((Activity) context, bundle, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 1);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(UiError e) {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onCancel() {
                        Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
                        intent.putExtra("status", 0);
                        context.sendBroadcast(intent);
                    }
                });
            }
        } else {
            Intent intent = new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 3);
            context.sendBroadcast(new Intent(Constant.LOGIN_SUCCESS_BROADCAST_ACTION));
        }
    }

    /**
     * 分享到微信
     *
     * @param flag 0=分享给朋友，1=分享到朋友圈
     */
    public static void share2weixin(Context context, int flag, String title,
                                    String description, String url, Bitmap app_con) {
        IWXAPI weiXinApi = CommonManager.getInstance().getWeiXinApi();
        if (!weiXinApi.isWXAppInstalled()) {
            Intent intent = new Intent(Constant.SHARE_SUCCESS_BROADCAST_ACTION);
            intent.putExtra("status", 2);
            context.sendBroadcast(intent);
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);

        msg.title = title;
        msg.description = description;
        msg.setThumbImage(app_con);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag;
        weiXinApi.sendReq(req);
    }
}
