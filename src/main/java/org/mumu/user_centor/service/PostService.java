package org.mumu.user_centor.service;

import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.model.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.dto.UserDTO;
import org.mumu.user_centor.model.request.DeleteRequest;
import org.mumu.user_centor.model.request.PostCommentAddRequest;
import org.mumu.user_centor.model.vo.PostVo;
import org.mumu.user_centor.model.vo.ScrollResult;

import java.util.List;

/**
* @author HP
* @description 针对表【post】的数据库操作Service
* @createDate 2023-04-28 16:19:35
*/
public interface PostService extends IService<Post> {

    BaseResponse<Long> savePost(Post post);

    BaseResponse<Boolean> likePost(Long id);

    BaseResponse<List<Post>> queryHotPost(Integer current);

    BaseResponse<PostVo> queryPostById(Long id);

    BaseResponse<List<UserDTO>> queryPostLikes(Long id);

    BaseResponse<ScrollResult> queryPostOfFollow(Long max, Integer offset);

    Boolean deletePost(Long id);
}
