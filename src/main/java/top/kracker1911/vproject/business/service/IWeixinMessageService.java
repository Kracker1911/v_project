package top.kracker1911.vproject.business.service;

import top.kracker1911.vproject.business.entity.WeixinMessage;

import java.util.List;

public interface IWeixinMessageService {

    int insertWeixinMessage(String openId, String messageContent);
    List<WeixinMessage> findWeixinMessageByContent(String messageContent);
    WeixinMessage findWeixinMessageById(Long msgId);
    int deleteWeixinMessageById(Long msgId);
    int banWeixinMessageById(Long msgId);
    int debanWeixinMessageById(Long msgId);
}
