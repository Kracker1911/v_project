package top.kracker1911.vproject.business.weixin.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.stereotype.Component;

@Component
public class WeixinMpQRData {
    @JSONField(name = "ticket")
    private String ticket;

    @JSONField(name = "expire_seconds")
    private Integer expireSeconds;

    @JSONField(name = "url")
    private String url;

    @JSONField(name = "errcode")
    private Integer errCode;

    @JSONField(name = "errmsg")
    private String errMsg;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
