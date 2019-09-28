package top.kracker1911.vproject.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.entity.WeixinMessage;
import top.kracker1911.vproject.business.ov.WeixinMpQRData;
import top.kracker1911.vproject.business.service.IWeixinService;
import top.kracker1911.vproject.business.service.IWeixinMessageService;
import top.kracker1911.vproject.constants.Constants;
import top.kracker1911.vproject.exception.AesException;
import top.kracker1911.vproject.util.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WeixinService implements IWeixinService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinService.class);
    private Map<String, String> weixinTextReply;
    private SimpleDateFormat sdf;
    private Set<String> adminOpenIdSet;

    @Autowired
    private IWeixinMessageService weixinMessageService;

    public WeixinService() {
        weixinTextReply = new HashMap<>();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        adminOpenIdSet = new HashSet<>();
        adminOpenIdSet.add("o92w4v-7OU16w9_6aMiS39CLRyeQ");
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
                } else if ("text".equals(msgType)) {
                    // 回复消息
                    String content = (String) xmlMap.get(Constants.CONTENT);
                    String openId = (String) xmlMap.get(Constants.FROM_USER_NAME);
                    if (adminOpenIdSet.contains(openId) && (content.trim().length() > 0)
                            && (content.startsWith("find:") || content.startsWith("detail:") || content.startsWith("delete:")
                            || content.startsWith("ban:") || content.startsWith("deban:"))) {
                        String traceId = UUIDUtil.get32UUID();
                        String[] contentArr = content.split(":");
                        String operation = contentArr[0];
                        String keyWord = StringUtil.concat(Arrays.copyOfRange(contentArr, 1, contentArr.length), ":");
                        logger.info("traceId:" + traceId + ";admin:" + openId + ";operation:" + content + ";keyWord:" + keyWord);
                        String operationResult = "";
                        if ("find".equals(operation)) {
                            List<WeixinMessage> messageList = weixinMessageService.findWeixinMessageByContent(keyWord);
                            JSONArray array = new JSONArray();
                            for (WeixinMessage m : messageList) {
                                JSONObject obj = new JSONObject();
                                obj.put("msgId", m.getMsgId());
                                obj.put("content", m.getMsgContent());
                                obj.put("yxbz", m.getYxbz());
                                array.add(obj);
                            }
                            operationResult = array.toJSONString();
                        } else if ("detail".equals(operation)) {
                            try {
                                Long msgId = Long.parseLong(keyWord);
                                WeixinMessage message = weixinMessageService.findWeixinMessageById(msgId);
                                operationResult = JSON.toJSONString(message);
                            } catch (NumberFormatException nfe) {
                                operationResult = nfe.getMessage();
                            }
                        } else if ("ban".equals(operation)) {
                            try {
                                Long msgId = Long.parseLong(keyWord);
                                operationResult = operationResult + weixinMessageService.banWeixinMessageById(msgId);
                            } catch (NumberFormatException nfe) {
                                operationResult = nfe.getMessage();
                            }
                        } else if ("deban".equals(operation)) {
                            try {
                                Long msgId = Long.parseLong(keyWord);
                                operationResult = operationResult + weixinMessageService.debanWeixinMessageById(msgId);
                            } catch (NumberFormatException nfe) {
                                operationResult = nfe.getMessage();
                            }
                        } else if ("delete".equals(operation)) {
                            try {
                                Long msgId = Long.parseLong(keyWord);
                                operationResult = operationResult + weixinMessageService.deleteWeixinMessageById(msgId);
                            } catch (NumberFormatException nfe) {
                                operationResult = nfe.getMessage();
                            }
                        } else {
                            operationResult = "invalid operation";
                        }
                        logger.info("traceId:" + traceId + ";admin:" + openId + ";operation:" + content + ";keyWord:" + keyWord + ";operation result:" + operationResult);
                        echoMsgMap.put(Constants.CONTENT, operationResult);
                    } else {
                        String sensitiveWord = SensitiveFilterUtil.filterMessage(content);
                        String receivedMsg = openId + "[" + sdf.format(new Date()) + "]: " + content;
                        if (content.length() > 200 || content.length() < 5) {
                            receivedMsg = "[len]" + receivedMsg;
                            logger.info(receivedMsg);
                            echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_TEXT, "bad_length"));
                        } else if (sensitiveWord != null && sensitiveWord.length() > 0) {
                            receivedMsg = "[bad]" + receivedMsg;
                            logger.info(receivedMsg);
                            echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_TEXT, "bad") + "[" + sensitiveWord + "]");
                        } else {
                            receivedMsg = "[good]" + receivedMsg;
                            logger.info(receivedMsg);
                            int insertRowCount = weixinMessageService.insertWeixinMessage(openId, content);
                            if (insertRowCount > 0) {
                                echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_TEXT, "ok"));
                            } else {
                                echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_TEXT, "fail"));
                            }
                        }
                    }
                } else {
                    echoMsgMap.put(Constants.CONTENT, this.getTextReply(Constants.EVENT_DEFAULT, ""));
                }
                String echoMsgStr = WeixinUtil.mapToMsgXml(echoMsgMap);
                logger.info(echoMsgStr);
                result = WeixinUtil.encryptMsg(echoMsgStr, "" + currentMS, WeixinUtil.getRandomStr());
            }
        }
        return result;
    }

    @Override
    public String createMPMenu(String appId, String appSecret) {
        String result = WeixinUtil.createMPMenu(appId, appSecret);
        JSONObject rObj = JSONObject.parseObject(result);
        if (rObj != null && rObj.get("errcode") != null
                && "0".equals(rObj.get("errcode").toString())) {
            this.refreshTextReply();
        }
        return result;
    }

    @Override
    public void refreshTextReply() {
        String jsonFile = FileUtil.RESOURCE_FILE_PATH + "weixin-reply.json";
        String jsonStr = FileUtil.readJsonFile(jsonFile);
        JSONObject object = JSONObject.parseObject(jsonStr);
        if (object.isEmpty()) {
            logger.info("empty config loaded, reply set not refreshed");
            return;
        }
        this.weixinTextReply.clear();
        this.putTextReply(Constants.EVENT_SUBSCRIBE, object);
        this.putTextReply(Constants.EVENT_UNSUBSCRIBE, object);
        this.putTextReply(Constants.EVENT_SCAN, object);
        this.putTextReply(Constants.EVENT_CLICK, object);
        this.putTextReply(Constants.EVENT_TEXT, object);
        this.putTextReply(Constants.EVENT_DEFAULT, object);
        logger.info("reply set refreshed: " + this.weixinTextReply);
    }

    private void putTextReply(String event, JSONObject mainObj) {
        if (event == null || event.length() <= 0 || mainObj == null || mainObj.isEmpty()) {
            return;
        }
        JSONArray textArray = mainObj.getJSONArray(event);
        if (textArray == null || textArray.size() <= 0) {
            return;
        }
        textArray.forEach(o -> {
            JSONObject obj = (JSONObject) o;
            String key = obj.getString("key");
            String reply = obj.getString("reply");
            this.putTextReply(event, key, reply);
        });
    }

    private String getTextReply(String event, String key) {
        Objects.requireNonNull(event);
        key = key == null ? "" : key;
        String reply = weixinTextReply.get(event + "." + key);
        return reply == null ? "" : reply;
    }

    private void putTextReply(String event, String key, String reply) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(key);
        Objects.requireNonNull(reply);
        weixinTextReply.put(event + "." + key, reply);
    }

}
