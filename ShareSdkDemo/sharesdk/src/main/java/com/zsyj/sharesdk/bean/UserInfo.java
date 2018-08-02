package com.zsyj.sharesdk.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by luoxingxing on 2017/9/28 0028.
 * 用户信息类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {


    /**
     * Header : {"Result":0,"Msg":"用户访问成功"}
     * Content : {"uid":"1000003","token":"b2abf8f6c92015cae5b61ccc1bcc51fb"}
     */

    public HeaderBean Header;
    public ContentBean Content;

    public HeaderBean getHeader() {
        return Header;
    }

    public void setHeader(HeaderBean Header) {
        this.Header = Header;
    }

    public ContentBean getContent() {
        return Content;
    }

    public void setContent(ContentBean Content) {
        this.Content = Content;
    }

    public static class HeaderBean implements Serializable{
        /**
         * Result : 0
         * Msg : 用户访问成功
         */

        public int Result;
        public String Msg;

        public int getResult() {
            return Result;
        }

        public void setResult(int Result) {
            this.Result = Result;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
    }

    public static class ContentBean implements Serializable{
        /**
         * uid : 1000003
         * token : b2abf8f6c92015cae5b61ccc1bcc51fb
         */

        static String uid;
        static String token;

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
    }
}
