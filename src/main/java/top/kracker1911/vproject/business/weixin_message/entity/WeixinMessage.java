package top.kracker1911.vproject.business.weixin_message.entity;

import java.io.Serializable;
import java.util.Date;

public class WeixinMessage implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.msg_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private Long msgId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.user_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.open_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private String openId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.user_name
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private String userName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.msg_time
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private Date msgTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.msg_content
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private String msgContent;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column weixin_message.yxbz
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private String yxbz;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table weixin_message
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.msg_id
     *
     * @return the value of weixin_message.msg_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public Long getMsgId() {
        return msgId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.msg_id
     *
     * @param msgId the value for weixin_message.msg_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.user_id
     *
     * @return the value of weixin_message.user_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.user_id
     *
     * @param userId the value for weixin_message.user_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.open_id
     *
     * @return the value of weixin_message.open_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.open_id
     *
     * @param openId the value for weixin_message.open_id
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.user_name
     *
     * @return the value of weixin_message.user_name
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.user_name
     *
     * @param userName the value for weixin_message.user_name
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.msg_time
     *
     * @return the value of weixin_message.msg_time
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public Date getMsgTime() {
        return msgTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.msg_time
     *
     * @param msgTime the value for weixin_message.msg_time
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.msg_content
     *
     * @return the value of weixin_message.msg_content
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public String getMsgContent() {
        return msgContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.msg_content
     *
     * @param msgContent the value for weixin_message.msg_content
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent == null ? null : msgContent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column weixin_message.yxbz
     *
     * @return the value of weixin_message.yxbz
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public String getYxbz() {
        return yxbz;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column weixin_message.yxbz
     *
     * @param yxbz the value for weixin_message.yxbz
     *
     * @mbg.generated Sun Sep 22 21:36:03 CST 2019
     */
    public void setYxbz(String yxbz) {
        this.yxbz = yxbz == null ? null : yxbz.trim();
    }
}