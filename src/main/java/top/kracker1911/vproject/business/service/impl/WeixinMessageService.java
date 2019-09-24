package top.kracker1911.vproject.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.dao.WeixinMessageMapper;
import top.kracker1911.vproject.business.entity.WeixinMessage;
import top.kracker1911.vproject.business.service.IWeixinMessageService;

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
