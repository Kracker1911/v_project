package top.kracker1911.vproject.business.weixin_message.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.weixin_message.dao.WeixinMessageMapper;
import top.kracker1911.vproject.business.weixin_message.entity.WeixinMessage;
import top.kracker1911.vproject.business.weixin_message.entity.WeixinMessageExample;
import top.kracker1911.vproject.business.weixin_message.service.IWeixinMessageService;

import java.util.Date;

@Service
public class WeixinMessageService implements IWeixinMessageService {

    @Autowired
    private WeixinMessageMapper weixinMessageMapper;

    @Override
    public int insertWeixinMessage(String openId, String messageContent) {
        WeixinMessage message = new WeixinMessage();
        message.setMsgContent(messageContent);
        message.setMsgTime(new Date());
        message.setOpenId(openId);
        message.setUserId(0);
        message.setUserName(openId);
        message.setYxbz("Y");
        return weixinMessageMapper.insert(message);
    }
}
