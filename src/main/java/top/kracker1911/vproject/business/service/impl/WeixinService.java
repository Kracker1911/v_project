package top.kracker1911.vproject.business.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.ov.WeixinMpQRData;
import top.kracker1911.vproject.business.service.IWeixinService;
import top.kracker1911.vproject.business.service.IWeixinMessageService;
import top.kracker1911.vproject.constants.Constants;
import top.kracker1911.vproject.exception.AesException;
import top.kracker1911.vproject.util.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WeixinService implements IWeixinService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinService.class);
    private Map<String, String> weixinTextReply = new HashMap<>();

    @Autowired
    private IWeixinMessageService weixinMessageService;

    public WeixinService(){
        this.refreshTextReply();
    }

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
                System.out.println(xmlMap);
                logger.info("receive message object: " + xmlMap.toString());
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
                        String reply = this.getTextReply(Constants.EVENT_SUBSCRIBE, (String) xmlMap.get(Constants.EVENT_KEY));
                        echoMsgMap.put(Constants.CONTENT, reply);
                    } else if ("unsubscribe".equals(event)) {
                        // 取消订阅事件
                        String reply = this.getTextReply(Constants.EVENT_UNSUBSCRIBE, (String) xmlMap.get(Constants.EVENT_KEY));
                        echoMsgMap.put(Constants.CONTENT, reply);
                    } else if ("SCAN".equals(event)) {
                        // 已关注扫描二维码事件
                        String reply = this.getTextReply(Constants.EVENT_UNSUBSCRIBE, (String) xmlMap.get(Constants.EVENT_KEY));
                        echoMsgMap.put(Constants.CONTENT, reply);
                    } else if ("CLICK".equals(event)) {
                        //点击菜单按钮事件
                        //回复文字信息
                        String reply = this.getTextReply(Constants.EVENT_CLICK, (String) xmlMap.get(Constants.EVENT_KEY));
                        echoMsgMap.put(Constants.CONTENT, reply);
                    }
                } else if("text".equals(msgType)) {
                    // 回复消息
                    String content = (String) xmlMap.get(Constants.CONTENT);
                    String openId = (String) xmlMap.get(Constants.FROM_USER_NAME);
                    System.out.println(content);
                    int insertRowCount = weixinMessageService.insertWeixinMessage(openId, content);
                    if(insertRowCount > 0) {
                        echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_CLICK, "ok"));
                    }else {
                        echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_CLICK, "fail"));
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

    @Override
    public void refreshTextReply(){
        String jsonFile = FileUtil.RESOURCE_FILE_PATH + "weixin-reply.json";
        String jsonStr = FileUtil.readJsonFile(jsonFile);
        JSONObject object = JSONObject.parseObject(jsonStr);
        JSONArray subArray = object.getJSONArray(Constants.EVENT_SUBSCRIBE);
        JSONArray unsubArray = object.getJSONArray(Constants.EVENT_UNSUBSCRIBE);
        JSONArray scanArray = object.getJSONArray(Constants.EVENT_SCAN);
        JSONArray clickArray = object.getJSONArray(Constants.EVENT_CLICK);
        if((subArray != null && subArray.size() > 0) || (unsubArray != null && unsubArray.size() > 0) ||
                (scanArray != null && scanArray.size() > 0) || (clickArray != null && clickArray.size() > 0)){
            this.weixinTextReply.clear();
            this.putTextReply(Constants.EVENT_SUBSCRIBE, subArray);
            this.putTextReply(Constants.EVENT_UNSUBSCRIBE, unsubArray);
            this.putTextReply(Constants.EVENT_SCAN, scanArray);
            this.putTextReply(Constants.EVENT_CLICK, clickArray);
        }
    }

    private String putTextReply(String event, JSONArray textArray) {
        if(event == null || event.length() <= 0 || textArray == null || textArray.size() <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        textArray.forEach(o -> {
            JSONObject obj = (JSONObject) o;
            String key = obj.getString("key");
            String reply = obj.getString("reply");
            sb.append(this.putTextReply(event, key, reply));
        });
        return sb.toString();
    }

    private String getTextReply(String event, String key) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(key);
        String reply = weixinTextReply.get(event + "." + key);
        return reply == null ? "" : reply;
    }

    private String putTextReply(String event, String key, String reply) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(key);
        Objects.requireNonNull(reply);
        return weixinTextReply.put(event + "." + key, reply);
    }

}
