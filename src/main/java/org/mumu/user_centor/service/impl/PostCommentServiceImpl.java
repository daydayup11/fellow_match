package org.mumu.user_centor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.Post;
import org.mumu.user_centor.model.domain.PostComment;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.request.PostCommentAddRequest;
import org.mumu.user_centor.service.PostCommentService;
import org.mumu.user_centor.mapper.PostCommentMapper;
import org.mumu.user_centor.service.PostService;
import org.mumu.user_centor.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author HP
* @description 针对表【post_comment(帖子)】的数据库操作Service实现
* @createDate 2023-04-29 10:39:19
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    /**
     * 添加评论
     * @param postCommentAddRequest 添加请求体
     * @param loginUser 当前用户
     * @return
     */
    @Override
    public boolean addComment(PostCommentAddRequest postCommentAddRequest, User loginUser) {
        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //帖子id存在
        Long postId = postCommentAddRequest.getPostId();
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
        //内容字数小于200，内容不能为空
        String content = postCommentAddRequest.getContent();
        if (StringUtils.isBlank(content) || content.length() == 0 || content.length() >= 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容字数不符合要求");
        }
        //判断评论的pid,pid为null代表该条评论pid是帖子的创建者，反之是回复者的id
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentAddRequest, postComment);
        //判断评论的父id
        Long pid = postComment.getPid();
        //获取帖子的创建者id
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
        //默认为评论的父id为帖子的创建者
        if (pid == null) {
            postComment.setPid(post.getUserId());
        }
        final long userId = loginUser.getId();
        postComment.setUserId(userId);
        //保存评论
        return save(postComment);
    }

    /**
     * 删除评论
     * @param id 评论id
     * @param loginUser 当前用户
     * @return
     */
    @Override
    public boolean deleteComment(Long id, User loginUser) {
        PostComment postComment = getById(id);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
        Long userId = postComment.getUserId();
        Long postId = postComment.getPostId();
        Post post = postService.getById(postId);
        //是否为评论的创建人或者管理员，帖子的创建者
        if (!userService.isAdmin(loginUser) && !userId.equals(loginUser.getId()) && post.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return removeById(id);
    }
}




