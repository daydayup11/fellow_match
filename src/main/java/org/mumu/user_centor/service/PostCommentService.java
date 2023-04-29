package org.mumu.user_centor.service;

import org.mumu.user_centor.model.domain.PostComment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.request.PostCommentAddRequest;

/**
* @author HP
* @description 针对表【post_comment(帖子)】的数据库操作Service
* @createDate 2023-04-29 10:39:19
*/
public interface PostCommentService extends IService<PostComment> {
    boolean addComment(PostCommentAddRequest postCommentAddRequest, User loginUser);

    boolean deleteComment(Long id, User loginUser);
}
