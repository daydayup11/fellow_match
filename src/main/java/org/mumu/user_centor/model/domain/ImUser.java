package org.mumu.user_centor.model.domain;

import lombok.Data;

@Data
public class ImUser {
    /**
     * 用户id
     */
    private Long uid = 1L;
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String profile;
}
