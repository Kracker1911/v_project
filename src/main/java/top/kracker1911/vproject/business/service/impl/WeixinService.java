package top.kracker1911.vproject.business.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.ov.WeixinMpQRData;
import top.kracker1911.vproject.business.service.IWeixinCacheService;
import top.kracker1911.vproject.business.service.IWeixinService;
import top.kracker1911.vproject.business.service.IWeixinMessageService;
import top.kracker1911.vproject.constants.Constants;
import top.kracker1911.vproject.exception.AesException;
import top.kracker1911.vproject.util.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WeixinService implements IWeixinService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinService.class);
    private Map<String, String> weixinTextReply = new HashMap<>();

    @Autowired
    private IWeixinCacheService weixinCacheService;

    @Autowired
    private IWeixinMessageService weixinMessageService;

    @Override
    public String getQRUrl() {
        String sequences = UUIDUtil.get32UUID();
        WeixinMpQRData qrData = WeixinUtil.getMpQRData(sequences);
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrData.getTicket();
    }

    @Override
    public String handleEventFromWeixin(InputStream requestInputStream) throws AesException {
        String result = "";
        if (null != requestInputStream) {
            InputStreamReader isr = new InputStreamReader(requestInputStream);
            BufferedReader br = new BufferedReader(isr);
            Stream<String> lines = br.lines();
            String incomeXmlStr = StringUtil.toString(lines.collect(Collectors.toList()));
            Object[] objs = WeixinUtil.extract(incomeXmlStr);
            if (objs[1] != null) {
                String decrypt = WeixinUtil.decrypt(objs[1].toString());
                Map<String, Object> xmlMap = XmlUtil.extractToMap(decrypt);
                String msgType = (String) xmlMap.get(Constants.MESSAGE_TYPE);
                System.out.println((String) xmlMap.get(Constants.EVENT_KEY));
                System.out.println(xmlMap);

                String event = (String) xmlMap.get(Constants.EVENT);
                long currentMS = System.currentTimeMillis() / 1000L;
                Map<String, Object> echoMsgMap = new HashMap<>();
                echoMsgMap.put(Constants.TO_USER_NAME, xmlMap.get(Constants.FROM_USER_NAME));
                echoMsgMap.put(Constants.FROM_USER_NAME, xmlMap.get(Constants.TO_USER_NAME));
                echoMsgMap.put(Constants.CREATE_TIME, currentMS);
                echoMsgMap.put(Constants.MESSAGE_TYPE, "text");
                if ("event".equals(msgType) && xmlMap.get(Constants.EVENT_KEY) != null) {
                    //公众号事件推送
                    if ("subscribe".equals(event)) {
                        // 扫描二维码订阅
                        echoMsgMap.put(Constants.CONTENT, "欢迎参加许振涛&&孙小康的结婚典礼！");
                        //sh02扫码注册
                        weixinCacheService.associateSeqOpnByEvent((String) xmlMap.get(Constants.EVENT_KEY), (String) xmlMap.get(Constants.FROM_USER_NAME));
                    } else if ("unsubscribe".equals(event)) {
                        // 取消订阅事件
                        echoMsgMap.put(Constants.CONTENT, "");
                    } else if ("SCAN".equals(event)) {
                        // 已关注扫描二维码事件
                        echoMsgMap.put(Constants.CONTENT, "");
                        //sh02扫码登录
                        weixinCacheService.associateSeqOpnByEvent((String) xmlMap.get(Constants.EVENT_KEY), (String) xmlMap.get(Constants.FROM_USER_NAME));
                    } else if ("CLICK".equals(event)) {
                        //点击菜单按钮事件
                        //回复文字信息
                        String reply = this.weixinTextReply.get((String) xmlMap.get(Constants.EVENT_KEY));
                        echoMsgMap.put(Constants.CONTENT, reply == null ? "no contents" : reply);
                    }
                } else if("text".equals(msgType)) {
                    // 回复消息
                    String content = (String) xmlMap.get(Constants.CONTENT);
                    String openId = (String) xmlMap.get(Constants.FROM_USER_NAME);
                    System.out.println(content);
                    int insertRowCount = weixinMessageService.insertWeixinMessage(openId, content);
                    if(insertRowCount > 0) {
                        echoMsgMap.put(Constants.CONTENT, "已收到祝福，谢谢！");
                    }else {
                        echoMsgMap.put(Constants.CONTENT, "好像没有保存好呢，请重试一下，谢谢！");
                    }
                }
                String echoMsgStr = WeixinUtil.mapToMsgXml(echoMsgMap);
                System.out.println(echoMsgStr);
                result = WeixinUtil.encryptMsg(echoMsgStr, "" + currentMS, WeixinUtil.getRandomStr());
            }
        }

        return result;
    }

    @Override
    public String createMPMenu(String appId, String appSecret) {
        String result = WeixinUtil.createMPMenu(appId, appSecret);
        JSONObject rObj = JSONObject.parseObject(result);
        if(rObj != null && rObj.get("errcode") != null
                && "0".equals(rObj.get("errcode").toString())) {
            this.refreshTextReply();
        }
        return result;
    }

    private void refreshTextReply(){
        String jsonFile = FileUtil.RESOURCE_FILE_PATH + "weixin-reply.json";
        String jsonStr = FileUtil.readJsonFile(jsonFile);
        JSONObject object = JSONObject.parseObject(jsonStr);
        JSONArray textArray = object.getJSONArray("text");
        this.weixinTextReply.clear();
        for (Object value : textArray) {
            JSONObject o = (JSONObject) value;
            this.weixinTextReply.put((String) o.get("key"), (String) o.get("reply"));
        }
    }
}
