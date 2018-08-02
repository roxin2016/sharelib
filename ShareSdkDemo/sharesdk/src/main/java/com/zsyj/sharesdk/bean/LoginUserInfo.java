package com.zsyj.sharesdk.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 1 on 2017/9/30.
 * 微信获取的用户信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUserInfo implements Serializable {


    /**
     * openid : oGBnLw-deISqxa1SOZaVzKu5KAZw
     * nickname : 短发夏天
     * sex : 0
     * language : zh_CN
     * city :
     * province :
     * country :
     * headimgurl : http://wx.qlogo.cn/mmopen/vi_32/NfCtt08pDASXFXBgvbSPic1tXUIsmyh54P5decg83mTfRULj9vdJWh3tbuBiaNn6bSRDxSiapJhkraCPhGIcG63Ig/0
     * privilege : []
     * unionid : opPrW087RK88oKNH-P486TSucUpU
     */
    public String uid="";
    public String token="";
    public String openid="";
    public String nickname="";
    public int sex;
    public String language="";
    public String city="";
    public String province="";
    public String country="";
    public String headimgurl="";
    public String unionid="";
    public List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        String str="uid=" + uid + "token=" + token + "openId=" + openid + "nickName=" + nickname + "sex=" + sex + "language=" + language
                + "city=" + city + "province=" + province + "country=" + country + "headimgUrl=" + headimgurl + "unionid=" + unionid;
        return str;
    }
}
