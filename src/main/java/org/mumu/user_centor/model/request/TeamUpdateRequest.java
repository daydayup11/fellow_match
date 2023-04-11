package org.mumu.user_centor.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 地区
     */
    private String place;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 最大人数
     */
    private Integer maxNum;


    /**
     * 密码
     */
    private String password;

    /**
     * 密码
     */
    private String announce;
    /**
     * 标签
     */
    private String tags;
}