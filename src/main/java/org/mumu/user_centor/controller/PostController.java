package org.mumu.user_centor.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.Post;
import org.mumu.user_centor.model.domain.Team;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.dto.UserDTO;
import org.mumu.user_centor.model.request.DeleteRequest;
import org.mumu.user_centor.model.request.ObjectIdRequest;
import org.mumu.user_centor.model.request.PostAddRequest;
import org.mumu.user_centor.model.request.PostCommentAddRequest;
import org.mumu.user_centor.model.vo.PostVo;
import org.mumu.user_centor.model.vo.ScrollResult;
import org.mumu.user_centor.service.PostCommentService;
import org.mumu.user_centor.service.PostService;
import org.mumu.user_centor.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static org.mumu.user_centor.constant.PostConstant.MAX_PAGE_SIZE;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    PostService postService;
    @Resource
    PostCommentService postCommentService;

    /**
     * 上传帖子
     * @param postAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> savePost(@RequestBody PostAddRequest postAddRequest){
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = UserHolder.getUser();
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest,post);
        post.setIsLike(false);
        post.setUserId(loginUser.getId());
        String avatarUrl = post.getAvatarUrl();
        avatarUrl = avatarUrl.substring(1,avatarUrl.length()-1);
        String[] avatarUrls = avatarUrl.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String url : avatarUrls) {
            String substring = url.substring(1, url.length() - 1);
            stringBuilder.append(substring);
            stringBuilder.append(",");
        }
        String images = stringBuilder.substring(0, stringBuilder.length() - 1).toString();
        post.setAvatarUrl(images);
        return postService.savePost(post);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        boolean result = postService.deletePost(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 查看指定用户的帖子
     * @param current 当前页数
     * @param id 用户id
     * @return
     */
    @GetMapping("/of/user")
    public BaseResponse<List<Post>> queryPostByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Post> page = postService.query()
                .eq("userId", id).page(new Page<>(current, MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Post> records = page.getRecords();
        return ResultUtils.success(records);
    }

    /**
     * 给指定的帖子点赞
     * @param id 帖子id
     * @return
     */
    @GetMapping("/like")
    public BaseResponse<Boolean> likePost(Long id) {
        return postService.likePost(id);
    }

    /**
     * 查看我发布的帖子
     * @param current 当前页数
     * @return
     */
    @GetMapping("/of/me")
    public BaseResponse<List<Post>> queryMyPost(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        User user = UserHolder.getUser();
        // 根据用户查询
        Page<Post> page = postService.query()
                .eq("userId", user.getId()).page(new Page<>(current, MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Post> records = page.getRecords();
        return ResultUtils.success(records);
    }

    /**
     * 首页热门帖子分页查询
     * @param current 当前页数
     * @return 按点赞数量返回帖子
     */
    @GetMapping("/hot")
    public BaseResponse<List<Post>> queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return postService.queryHotPost(current);
    }

    /**
     * 具体某个笔记
     * @param id
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<PostVo> queryPostById(Long id){
        return postService.queryPostById(id);
    }

    /**
     * 点赞前五的用户
     * @param id
     * @return
     */
    @GetMapping("/likes")
    public BaseResponse<List<UserDTO>> queryPostLikes(Long id) {
        return postService.queryPostLikes(id);
    }

    /**
     * 查看我关注的帖子
     * @param max 最后的帖子日期
     * @param offset 偏移量
     * @return
     */
    @GetMapping("/of/follow")
    public BaseResponse<ScrollResult> queryPostOfFollow(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return postService.queryPostOfFollow(max, offset);
    }

    /**
     * 添加帖子评论
     *
     * @param postCommentAddRequest
     * @return
     */
    @PostMapping("/addComment")
    public BaseResponse<Boolean> addPostComment(@RequestBody PostCommentAddRequest postCommentAddRequest) {
        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = UserHolder.getUser();
        boolean result = postCommentService.addComment(postCommentAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 删除帖子评论
     * @param objectIdRequest 删除帖子的id请求体
     * @return
     */
    @PostMapping("/deleteComment")
    public BaseResponse<Boolean> deleteComment(@RequestBody ObjectIdRequest objectIdRequest) {
        if (objectIdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = objectIdRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = UserHolder.getUser();
        boolean result = postCommentService.deleteComment(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }
}
