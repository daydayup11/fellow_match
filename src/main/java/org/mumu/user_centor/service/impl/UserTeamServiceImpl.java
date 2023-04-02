package org.mumu.user_centor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mumu.user_centor.model.domain.UserTeam;
import org.mumu.user_centor.service.UserTeamService;
import org.mumu.user_centor.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author HP
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-03-27 19:54:30
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




