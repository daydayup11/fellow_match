package org.mumu.user_centor.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeamUserVo implements Serializable {


    private static final long serialVersionUID = -1370190945969767617L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;
    /**
     * 队伍标签
     */
    private String tags;

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
     * 公告
     */
    private String announce;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVo createUser;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /**
     * 已加入用户列表
     */
    private List<UserVo> joinUserList;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;
}