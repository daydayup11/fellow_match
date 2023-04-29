package org.mumu.user_centor.model.vo;

import lombok.Data;
import org.mumu.user_centor.model.domain.Post;

import java.util.List;
@Data
public class PostVo {
    /**
     * 帖子内容
     */
    private Post post;
    /**
     * 帖子的评论
     */
    private List<PostCommentVO> postCommentList;
    /**
     * 创建人用户信息
     */
    private UserVo createUser;

}
