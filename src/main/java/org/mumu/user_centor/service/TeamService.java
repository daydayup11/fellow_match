package org.mumu.user_centor.service;

import org.mumu.user_centor.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.dto.TeamQuery;
import org.mumu.user_centor.model.request.TeamJoinRequest;
import org.mumu.user_centor.model.request.TeamQuitRequest;
import org.mumu.user_centor.model.request.TeamUpdateRequest;
import org.mumu.user_centor.model.vo.TeamUserVo;

import java.util.List;

/**
* @author HP
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-27 19:53:17
*/
public interface TeamService extends IService<Team> {
    long addTeam(Team team, User loginUser);

    List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(long id, User loginUser);
}
