package org.mumu.user_centor.model.dto;

import lombok.Data;
import org.mumu.user_centor.common.PageRequest;

import java.util.List;

@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;
    /**
     * id列表
     */
    private List<Long> ids;


    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 同时对队伍描述和名称还有标签进行搜索
     */
    private String searchText;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}