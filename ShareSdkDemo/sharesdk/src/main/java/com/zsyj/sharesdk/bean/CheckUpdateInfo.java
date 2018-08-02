package com.zsyj.sharesdk.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by 1 on 2017/10/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckUpdateInfo implements Serializable {


    /**
     * Header : {"Result":0,"Msg":"获取相应渠道更新版本方式"}
     * Content : {"updateurl":"121","remark":"111"}
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HeaderBean implements Serializable{
        /**
         * Result : 0
         * Msg : 获取相应渠道更新版本方式
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentBean implements Serializable{
        /**
         * updateurl : 121
         * remark : 111
         */

        public String updateurl;
        public String remark;
        public String vercode;
        public String is_up;

        public String getUpdateurl() {
            return updateurl;
        }

        public void setUpdateurl(String updateurl) {
            this.updateurl = updateurl;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getVercode() {
            return vercode;
        }

        public void setVercode(String vercode) {
            this.vercode = vercode;
        }

        public String getIs_up() {
            return is_up;
        }

        public void setIs_up(String is_up) {
            this.is_up = is_up;
        }
    }

    @Override
    public String toString() {
        String str = "updateurl=" + Content.getUpdateurl() + "remark=" + Content.getRemark() + "vercode=" + Content.getVercode() + "is_up=" + Content.getIs_up();
        return str;
    }
}
