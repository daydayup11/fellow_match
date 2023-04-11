package org.mumu.user_centor.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class TeamAddRequest {

    private static final long serialVersionUID = -4162304142710323660L;

    /**
     * 队伍名称
     */
    private String name;
    /**
     * 公告
     */
    private String announce;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 地点
     */
    private String place;
    /**
     * 标签
     */
    private String tags;
}