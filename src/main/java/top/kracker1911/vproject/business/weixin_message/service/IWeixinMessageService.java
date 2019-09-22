package top.kracker1911.vproject.business.weixin_message.service;

public interface IWeixinMessageService {

    int insertWeixinMessage(String openId, String messageContent);
}
