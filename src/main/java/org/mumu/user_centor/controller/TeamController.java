package org.mumu.user_centor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.Team;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.domain.UserTeam;
import org.mumu.user_centor.model.dto.TeamQuery;
import org.mumu.user_centor.model.dto.UserQuery;
import org.mumu.user_centor.model.request.*;
import org.mumu.user_centor.model.vo.TeamUserVo;
import org.mumu.user_centor.service.TeamService;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.service.UserTeamService;
import org.mumu.user_centor.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://10.174.224.237:3000","http://192.168.233.51:3000"},allowCredentials = "true")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<TeamUserVo> getTeamById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        TeamUserVo teamUserVo = teamService.getTeamById(id, true,loginUser);

        if (teamUserVo == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamUserVo);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, isAdmin);
        if (teamList.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未找到队伍信息");
        }
        queryTeamCount(request, teamList);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        BeanUtils.copyProperties(teamQuery, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(teamPage);
    }

    @GetMapping("/list/user")
    public BaseResponse<List<Team>> getTeamListByUserId(Long userId){
        if(userId == null || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Team> teamList = teamService.getTeamByUserId(userId);
        return ResultUtils.success(teamList);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = UserHolder.getUser();
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        User loginUser = userService.getCurrentUser(request);
        User loginUser = userService.getById(2);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //按teamId分组，返回Map，Map的key就是去重后的teamId
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> ids = new ArrayList<>(listMap.keySet());
        teamQuery.setIds(ids);
        List<TeamUserVo> teamList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(ids)){
            teamList = teamService.listTeams(teamQuery, true);
            queryTeamCount(request, teamList);
        }
        return ResultUtils.success(teamList);
    }

    /**
     * 填充队伍人数字段
     *
     * @param request
     * @param teamList
     */
    private void queryTeamCount(HttpServletRequest request, List<TeamUserVo> teamList) {
        //条件查询出的队伍列表
        List<Long> teamIdList = teamList.stream().map(TeamUserVo::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
//            User loginUser = userService.getCurrentUser(request);
            User loginUser = userService.getById(2);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            //已加入队伍集合
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //已加入的队伍的id集合
            Set<Long> hasJoinTeamIdList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdList.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
        }

        List<UserTeam> userTeamJoinList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(teamIdList)){
            QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
            userTeamJoinQueryWrapper.in("teamId", teamIdList);
            userTeamJoinList = userTeamService.list(userTeamJoinQueryWrapper);
        }
        //按每个队伍Id分组
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamJoinList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));

        teamList.forEach(team -> {
            team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size());
        });
    }


}
