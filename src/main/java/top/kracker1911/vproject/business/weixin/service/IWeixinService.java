package top.kracker1911.vproject.business.weixin.service;

import top.kracker1911.vproject.exception.AesException;

import java.io.InputStream;

public interface IWeixinService {
    String getQRUrl();
    String handleEventFromWeixin(InputStream requestInputStream) throws AesException;
    String createMPMenu(String appId, String appSecret);
}
