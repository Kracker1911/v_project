package top.kracker1911.vproject.business.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.kracker1911.vproject.business.service.IWeixinMessageService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WeixinMessageServiceTest {

    @Autowired
    private IWeixinMessageService weixinMessageService;

    @Test
    public void insertWeixinMessage() {
        System.out.println(weixinMessageService);
        int count = weixinMessageService.insertWeixinMessage("test_openId_1", "test_msgContent_1");
        Assert.assertTrue(count > 0);
    }
}
