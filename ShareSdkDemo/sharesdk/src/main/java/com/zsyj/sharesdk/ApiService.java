package com.zsyj.sharesdk;

import com.zsyj.sharesdk.bean.CheckUpdateInfo;
import com.zsyj.sharesdk.bean.LoginUserInfo;
import com.zsyj.sharesdk.bean.UserInfo;
import com.zsyj.sharesdk.bean.WxOpenIdInfo;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/5/11.
 */

public interface ApiService {
    /**
     * 微信登录获取openid
     */
    @GET("sns/oauth2/access_token")
    Observable<WxOpenIdInfo> getWxOPenId(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type);

    /**
     * 微信登录获取userinfo
     */
    @GET("sns/userinfo")
    Observable<LoginUserInfo> getWxUserInfo(@Query("access_token") String access_token, @Query("openid") String openid);

    /**
     * 登录后台服务器
     */
    @FormUrlEncoded
    @POST
    Observable<UserInfo> getLoginData(@Url String url, @FieldMap Map<String, String> postMap);

    /**
     * 应用升级
     */
    @FormUrlEncoded
    @POST
    Observable<CheckUpdateInfo> checkUpdate(@Url String url, @FieldMap Map<String, String> postMap);
}
