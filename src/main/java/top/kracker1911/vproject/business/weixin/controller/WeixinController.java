package top.kracker1911.vproject.business.weixin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kracker1911.vproject.business.weixin.service.IWeixinService;
import top.kracker1911.vproject.constants.Constants;
import top.kracker1911.vproject.exception.AesException;
import top.kracker1911.vproject.util.FileUtil;
import top.kracker1911.vproject.util.StringUtil;
import top.kracker1911.vproject.util.WeixinUtil;
import top.kracker1911.vproject.util.XmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class WeixinController {

    private static final Logger logger = LoggerFactory.getLogger(WeixinController.class);

    @Autowired
    private IWeixinService weixinService;

    /**
     * 用于验证Token的接口
     * @param request
     * @param response
     * @throws AesException
     * @throws IOException
     */
    @GetMapping(value = "/wx/getEvent")
    public void verifyToken(HttpServletRequest request, HttpServletResponse response) throws AesException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        PrintWriter writer = response.getWriter();
        if (WeixinUtil.verifyUrl(signature, timestamp, nonce)) {
            writer.print(echostr);
            writer.flush();
            writer.close();
        }
    }

    @ResponseBody
//    @GetMapping(value = "/wx/qrurl")
    public String getMPQRUrl(){
        return weixinService.getQRUrl();
    }

    /**
     * 开发模式下，微信公众号的消息/事件推送接口
     * 需要和验证Token的接口保持一致
     * 在微信公众号的 开发-基本配置 里进行验证绑定
     * @param request
     * @param response
     * @throws AesException
     * @throws IOException
     */
    @PostMapping(value = "/wx/getEvent")
    public void getWXEventPush(HttpServletRequest request, HttpServletResponse response) throws AesException, IOException {
        String result = weixinService.handleEventFromWeixin(request.getInputStream());
        PrintWriter writer = response.getWriter();
        writer.print(result);
        writer.flush();
        writer.close();
    }

    /**
     * 在开发模式下，查询公众号素材列表
     * 接收参数：
     * appId: 与WeixinUtil中配置的appId保持一致，否则操作失败
     * appSecret: 与WeixinUtil中配置的appSecret保持一致，否则操作失败
     *
     * 在开发模式下，设置自定义菜单和相应自动回复
     * 自定义菜单文件为/resources/weixin-menu.json
     * 自定义回复文件为/resources/weixin-reply.json
     * @param params
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/wx/createMenu")
    public String createMPCustomMenu(@RequestParam Map<String, String> params){
        //在微信公众号后台设置自定义菜单
        String appId = params.get("appId");
        String appSecret = params.get("appSecret");

        if(StringUtils.isEmpty(appId) || StringUtils.isEmpty(appSecret)){
            return "{\"errcode\":99999,\"errmsg\":\"invalid app id or app secret\"}";
        }
        return weixinService.createMPMenu(appId, appSecret);
    }

    /**
     * 在开发模式下，查询公众号素材列表
     * 接收参数：
     * appId: 与WeixinUtil中配置的appId保持一致，否则操作失败
     * appSecret: 与WeixinUtil中配置的appSecret保持一致，否则操作失败
     * type: 素材类型（默认news）：image, video, voice, news
     * offset: 分页用，起始index，从0开始
     * count: 分页用，单页查询数量，1-20
     *
     * @param params
     * @return
     */
    @ResponseBody
//    @GetMapping(value = "/wx/getMedias")
    public String getMPMediaList(@RequestParam Map<String, String> params){
        String appId = params.get("appId");
        String appSecret = params.get("appSecret");
        String type = params.get("type");
        String offset = params.get("offset");
        String count = params.get("count");
        if(StringUtils.isEmpty(appId) || StringUtils.isEmpty(appSecret) || StringUtils.isEmpty(type)
                || StringUtils.isEmpty(offset) || StringUtils.isEmpty(count)){
            return "{\"errcode\":99999,\"errmsg\":\"invalid params\"}";
        }
        return WeixinUtil.getMediaList(appId, appSecret, type, Integer.valueOf(offset), Integer.valueOf(count));
    }
}
