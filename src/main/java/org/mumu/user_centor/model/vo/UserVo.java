package org.mumu.user_centor.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类（脱敏）
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = -4879186708855906501L;
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

//    /**
//     * 电话
//     */
//    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    private Double distance;
    private double x;
    private double y;

    /**
     * 联系方式
     */
//    @Field(name = "contactInfo")
    private String contactInfo;
    /**
     * 个人简介
     */
//    @Field(name = "profile",copyTo = "all",type = FieldType.Text,searchAnalyzer="ik_max_word",analyzer="ik_max_word")
    private String profile;

}