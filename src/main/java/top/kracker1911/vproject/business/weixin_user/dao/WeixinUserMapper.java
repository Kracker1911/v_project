package top.kracker1911.vproject.business.weixin_user.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.kracker1911.vproject.business.weixin_user.entity.WeixinUser;
import top.kracker1911.vproject.business.weixin_user.entity.WeixinUserExample;

public interface WeixinUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    long countByExample(WeixinUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int deleteByExample(WeixinUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int deleteByPrimaryKey(Integer userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int insert(WeixinUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int insertSelective(WeixinUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    List<WeixinUser> selectByExample(WeixinUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    WeixinUser selectByPrimaryKey(Integer userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int updateByExampleSelective(@Param("record") WeixinUser record, @Param("example") WeixinUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int updateByExample(@Param("record") WeixinUser record, @Param("example") WeixinUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int updateByPrimaryKeySelective(WeixinUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table weixin_user
     *
     * @mbg.generated Sat Sep 21 09:49:44 CST 2019
     */
    int updateByPrimaryKey(WeixinUser record);
}