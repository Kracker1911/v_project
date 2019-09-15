package top.kracker1911.vproject.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import top.kracker1911.vproject.constants.ErrorCode;
import top.kracker1911.vproject.exception.AesException;
import top.kracker1911.vproject.exception.HttpException;
import top.kracker1911.vproject.business.weixin.entity.WeixinMpQRData;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WeiXin示例
 */
public class WeixinUtil {

    private static final Logger logger = LoggerFactory.getLogger(WeixinUtil.class);
    private static Base64 base64 = new Base64();
    private static Charset CHARSET = Charset.forName("UTF-8");

    //改变公众号时需配置的变量
    private static final String AppId = "wx218af82ca36f72ff";
    private static final String AppSecret = "fd59f0735ed70dfea17b145d8004e795";
    private static final String Token = "a3af8263be2142228a61c1013b39e9f7";
    private static final String AesKeyStr = "hu6j77iyAkAx1D2Q9Dl2pNEXMqc9f9ToFHfER0auSZk";

    private static final byte[] AesKey = Base64.decodeBase64(AesKeyStr + "=");
    private static final String GrantType = "client_credential";
    private static final String AccessTokenURL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final int QRExpireSeconds = 24 * 60 * 60;//默认过期时间为一天（此变量单位为秒）
    private static final String WechatQRUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create";

    private static final String WechatCreateMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create";
    private static final String WechatGetMediaList = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";

    private static String accessToken;
    private static long expireTimeStamp = 0L;

    private static void requestAccessToken() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        if (expireTimeStamp <= currentTimeMillis) {
            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", GrantType);
            params.put("appid", AppId);
            params.put("secret", AppSecret);
            String jsonResult = HttpUtil.get(AccessTokenURL, params);
            if (!StringUtils.isEmpty(jsonResult)) {
                JSONObject jsonObject = JSON.parseObject(jsonResult);
                if (jsonObject.get("errcode") != null) {
                    throw new Exception((String) jsonObject.get("errmsg"));
                }
                if (jsonObject.get("access_token") != null && jsonObject.get("expires_in") != null) {
                    accessToken = (String) jsonObject.get("access_token");
                    long expireSeconds = Long.valueOf(jsonObject.get("expires_in").toString());
                    expireTimeStamp = currentTimeMillis + (expireSeconds * 1000L);
                }
            }
        }
    }

    public static String getAccessToken() {
        try {
            requestAccessToken();
        } catch (Exception e) {
            logger.error("request access token error" + e.getMessage());
            return null;
        }
        return accessToken;
    }

    public static WeixinMpQRData getMpQRData(String sceneStr) {
        return getMpQRData(QRExpireSeconds, sceneStr);
    }

    public static WeixinMpQRData getMpQRData(int expireSeconds, String sceneStr) {
        getAccessToken();
        WeixinMpQRData result;

        if (!StringUtils.isEmpty(accessToken)) {
            expireSeconds = expireSeconds < 60 ? 60 : expireSeconds > 2592000 ? 2592000 : expireSeconds;
            sceneStr = sceneStr == null ? UUIDUtil.get32UUID() : sceneStr;
            String jsonStr = "{\"expire_seconds\": " + expireSeconds +
                    ", \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}";
            String jsonResult = HttpUtil.post(WechatQRUrl + "?access_token=" + accessToken, jsonStr);
            if (!StringUtils.isEmpty(jsonResult)) {
                return JSON.parseObject(jsonResult, WeixinMpQRData.class);
            } else {
                result = new WeixinMpQRData();
                result.setErrCode(1);
                result.setErrMsg("get QR error");
            }
        } else {
            result = new WeixinMpQRData();
            result.setErrCode(1);
            result.setErrMsg("request access token error");
        }
        return result;
    }

    public static String createMPMenu(String appId, String appSecret) {
        if(!AppId.equals(appId) || !AppSecret.equals(appSecret)){
            throw new HttpException(ErrorCode.WX_APPID_SECRET_ERROR);
        }
        getAccessToken();
        String result , jsonFilePath = FileUtil.RESOURCE_FILE_PATH + "weixin-menu.json";
        String menuJsonStr = FileUtil.readJsonFile(jsonFilePath);
        String url = WechatCreateMenuUrl + "?access_token=" + accessToken;
        result = HttpUtil.post(url, menuJsonStr);
        return result;
    }

    public static String getMediaList(String appId, String appSecret, String type, int offset, int count) {
        if(!AppId.equals(appId) || !AppSecret.equals(appSecret)){
            throw new HttpException(ErrorCode.WX_APPID_SECRET_ERROR);
        }
        getAccessToken();
        //获取素材列表（mediaId）用于编辑自定义菜单
        Set<String> types = Arrays.stream(new String[]{"image", "video", "voice", "news"}).collect(Collectors.toSet());
        type = types.contains(type) ? type : "news";
        offset = offset < 0 ? 0 : offset;
        count = count < 1 || count > 20 ? 20 : count;
        String url = WechatGetMediaList + "?access_token=" + accessToken,
                params = "{\"type\": \"" + type + "\", \"offset\":" + offset + ", \"count\":" + count + "}";
        String result = HttpUtil.post(url, params);
        return result;
    }

    /**
     * WeiXin示例
     * 用SHA1算法生成安全签名
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文
     * @return 安全签名
     * @throws AesException
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
        try {
            String[] array = new String[]{token, timestamp, nonce, encrypt};
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ComputeSignatureError);
        }
    }

    // WeiXin示例
    // 生成4个字节的网络字节序
    public static byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // WeiXin示例
    // 还原4个字节的网络字节序
    public static int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // WeiXin示例
    // 随机生成16位字符串
    public static String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * WeiXin示例
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws AesException aes加密失败
     */
    public static String encrypt(String randomStr, String text) throws AesException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = AppId.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + appid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(appidBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(AesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            String base64Encrypted = base64.encodeToString(encrypted);

            return base64Encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.EncryptAESError);
        }
    }

    /**
     * WeiXin示例
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    public static String decrypt(String text) throws AesException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(AesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(AesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.DecryptAESError);
        }

        String xmlContent, from_appid;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            from_appid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
                    CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.IllegalBuffer);
        }

        // appid不相同的情况
        if (!from_appid.equals(AppId)) {
            throw new AesException(AesException.ValidateAppidError);
        }
        return xmlContent;

    }

    /**
     * WeiXin示例
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param replyMsg  公众平台待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce     随机串，可以自己生成，也可以用URL参数的nonce
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public static String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        // 加密
        String encrypt = encrypt(getRandomStr(), replyMsg);

        // 生成安全签名
        if (timeStamp == "") {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String signature = getSHA1(Token, timeStamp, nonce, encrypt);

        // System.out.println("发送给平台的签名是: " + signature[1].toString());
        // 生成发送的xml
        String result = generate(encrypt, signature, timeStamp, nonce);
        return result;
    }

    /**
     * WeiXin示例
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取xml中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param postData     密文，对应POST请求的数据
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public static String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData)
            throws AesException {

        // 密钥，公众账号的app secret
        // 提取密文
        Object[] encrypt = extract(postData);

        // 验证安全签名
        String signature = getSHA1(Token, timeStamp, nonce, encrypt[1].toString());

        // 和URL中的签名比较是否相等
        // System.out.println("第三方收到URL中的签名：" + msg_sign);
        // System.out.println("第三方校验签名：" + signature);
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }

        // 解密
        String result = decrypt(encrypt[1].toString());
        return result;
    }

    /**
     * WeiXin示例（有改动）
     * 验证URL
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public static boolean verifyUrl(String msgSignature, String timeStamp, String nonce)
            throws AesException {
        String signature = getSHA1(Token, timeStamp, nonce, "");
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        } else {
            return true;
        }
    }

    /**
     * WeiXin示例
     * 提取出xml数据包中的加密消息
     *
     * @param xmltext 待提取的xml字符串
     * @return 提取出的加密消息字符串
     * @throws AesException
     */
    public static Object[] extract(String xmltext) throws AesException {
        Object[] result = new Object[3];
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xmltext);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);
            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("Encrypt");
            NodeList nodelist2 = root.getElementsByTagName("ToUserName");
            result[0] = 0;
            result[1] = nodelist1.item(0).getTextContent();
            result[2] = nodelist2.item(0).getTextContent();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ParseXmlError);
        }
    }

    /**
     * WeiXin示例
     * 生成xml消息
     *
     * @param encrypt   加密后的消息密文
     * @param signature 安全签名
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 生成的xml字符串
     */
    public static String generate(String encrypt, String signature, String timestamp, String nonce) {

        String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
                + "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
                + "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
        return String.format(format, encrypt, signature, timestamp, nonce);
    }

    /**
     * 参数map中的value只接受String，Integer，Long和Map<String, Object>
     * @param map
     * @return
     */
    public static String mapToMsgXml(Map<String, Object> map){
        Objects.requireNonNull(map);
        StringBuilder sb = new StringBuilder();
        if(map.size() > 0) {
            sb.append(mapToMsgXmlNode("xml", map));
        }
        return sb.toString();
    }

    private static String mapToMsgXmlNode(String nodeName, Map<String, Object> map){
        Objects.requireNonNull(nodeName);
        Objects.requireNonNull(map);
        StringBuilder sb = new StringBuilder();
        if(map.size() > 0){
            sb.append("<").append(nodeName).append(">\n");
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if(e.getValue() instanceof Integer || e.getValue() instanceof Long) {
                    sb.append("\t<").append(e.getKey()).append(">").append(e.getValue()).append("</").append(e.getKey()).append(">\n");
                }else if(e.getValue() instanceof Map) {
                    sb.append(mapToMsgXmlNode(e.getKey(), (Map<String, Object>) e.getValue()));
                }else {
                    sb.append("\t<").append(e.getKey()).append("><![CDATA[").append(e.getValue().toString()).append("]]></").append(e.getKey()).append(">\n");
                }
            }
            sb.append("</").append(nodeName).append(">\n");
        }
        return sb.toString();
    }
}
