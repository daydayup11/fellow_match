package org.mumu.user_centor.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @TableName post
 */
@TableName(value ="post")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class Post implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 照片，最多9张，多张以","隔开
     */
    private String images;

    /**
     * 文字描述
     */
    private String content;

    /**
     * 点赞数量
     */
    private Integer liked;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
    /**
     * 用户图标
     */
    @TableField(exist = false)
    private String avatar;
    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String name;
    /**
     * 是否点赞过了
     */
    @TableField(exist = false)
    private Boolean isLike;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}