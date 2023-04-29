package org.mumu.user_centor.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.constant.UserConstant;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.mapper.UserMapper;
import org.mumu.user_centor.model.vo.UserVo;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.utils.JaccardSimilarity;
import org.mumu.user_centor.utils.UserHolder;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.mumu.user_centor.constant.RedisConstant.*;
import static org.mumu.user_centor.constant.UserConstant.ADMIN_ROLE;

/**
* @author HP
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-03-16 19:23:52
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public static final String SALT = "mumu";
    public static final String USER_LOGIN_STATE = "userLoginState";
    @Resource
    UserMapper userMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    /**
     * 进行用户注册
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 再次密码
     * @return 用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword,String checkPassword) {
        //校验是否为空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");

        }
        if(userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");

        }
        //账户不能包含特殊字符，这里的正则表达式可以上网搜
        String  validPattern  =  "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含特殊字符");
        }
        //密码一致
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不一致");
        }
        //账户不能重复，因为涉及到查询数据库，所以放在后面避免浪费资源
        //构建一个查询的wrapper
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        String url = "https://img.ixintu.com/download/jpg/202001/b319c1054eb817a437fb518f92597b0a.jpg!ys";
        user.setAvatarUrl(url);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存用户失败");
        }
        return user.getId();
    }

    @Override
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response) {
        //校验是否为空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"长度小于4");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"长度小于8");
        }
        //账户不能包含特殊字符，这里的正则表达式可以上网搜
        String  validPattern  =  "[a-zA-Z0-9]{1,16}";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(!matcher.matches()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"包含特殊字符");
        }
        //查询数据库
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //密码不一致
        if(user == null){
            log.info("user login failed,userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }
        User safeUser = getSafetyUser(user);
        //将用户保存到redis
        String token = UUID.randomUUID().toString();
        Map<String ,Object> userMap = BeanUtil.beanToMap(
                safeUser,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        //解决方法：在setFieldValueEditor中也需要判空
                .setFieldValueEditor((fieldName,fieldValue) -> {
                    if (fieldValue == null){
                        fieldValue = " ";
                    }else {
                        fieldValue = fieldValue.toString();
                    }
                    return fieldValue;}));
//                    setFieldValueEditor((fieldName, fieldValue)->fieldValue.toString()));
        //存储
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        //设置token有效期
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL, TimeUnit.MINUTES);
        //8.返回token
        return token;
    }

    @Override
    public User getSafetyUser(User user) {
        if(user == null){
            return null;
        }
        //脱敏
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setProfile(user.getProfile());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setTags(user.getTags());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        return safeUser;
    }

    @Override
    public int logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String key = LOGIN_USER_KEY + token;
        Long delete = stringRedisTemplate.opsForHash().delete(key);
        return Integer.parseInt(delete.toString());
    }

    /**
     * 根据标签搜索用户（使用内存过滤）
     * @param tagNameList 标签列表
     * @return 目标用户
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        //遇事先判空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.先查询所有的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        return userList.stream().filter(user ->{
            String tagStr = user.getTags();
            //json解析为对象，不能直接.class获得类型，需要借助TypeToken
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            //相当于if else，判空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for(String tagName : tagNameList){
                //当前用户所有标签是否包含查询的
                if(!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = UserHolder.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 校验权限
        // 2.1 管理员可以更新任意信息
        // 2.2 用户只能更新自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.getById(user.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        // 3. 触发更新
        return this.baseMapper.updateById(user);
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "[]");
        //查出所有的用户
        List<User> userList = this.list(queryWrapper);
        //获得当前用户的标签
        String tags = loginUser.getTags();
        //将用户标签解析为一个个字符串
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //如果当前用户没有设置标签，就返回空
        if(CollectionUtils.isEmpty(tagList)){
            return new ArrayList<>();
        }
        Set<String> currentUserTags = new HashSet<>(tagList);
        // 用户列表的下标 => 相似度
        List<Pair<User, Double>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())) {
                continue;
            }
            List<String> tagsList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            Set<String> userTagSet = new HashSet<>(tagsList);
            // 计算分数
            double distance = JaccardSimilarity.matchUser(userTagSet, currentUserTags);
            list.add(new Pair<>(user, distance));
        }
        // 按相似度由大到小排序
        List<Pair<User, Double>> topUserPairList = list.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;

    }

    @Override
    public BaseResponse<Page<User>> searchUserByDistance(Integer pageSize, Integer pageNum, Double x, Double y) {
        //1.判断是否需求根据坐标查询
        if(x == null||y == null){
            Page<User> page = query().page(new Page<>(pageNum,pageSize));
            return ResultUtils.success(page);

        }
        //2.计算分页参数
        int from = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize;
        //3.查询redis，按照距离排序、分页。结果:shopId、distance
        // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
        String key = USER_GEO_KEY;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(key, GeoReference.fromCoordinate(x, y),
                new Distance(10000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end));
        //4.解析出id
        if(results == null){
            return ResultUtils.success((Collections.emptyList()));
        }
        //截取从from到end的数据，redis查出0-end
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // 没有下一页了，结束
            return ResultUtils.success((Collections.emptyList()));
        }
        //获得用户id
        Map<String ,Distance> distanceMap = new HashMap<>(list.size());
        ArrayList<Long> ids = new ArrayList<>(list.size());
        list.stream().skip(from).forEach(result ->{
            //获取用户id
            String userId = result.getContent().getName();
            ids.add(Long.valueOf(userId));
            //获取距离
            Distance distance = result.getDistance();
            distanceMap.put(userId,distance);
        });
        //5.根据id查询user
        String idStr = StrUtil.join(",", ids);
        List<User> users = query().in("id", ids).last("order by field(id," + idStr + ")").list();
        List<UserVo> userVoList = new ArrayList<>();
        for (User user : users) {
            UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
            userVo.setDistance(distanceMap.get(user.getId().toString()).getValue());
            userVoList.add(userVo);
        }
        return ResultUtils.success(userVoList);
    }

    /**
     * 标签查询sql版
     * @param tagNameList
     * @return
     */
    public List<User> searchUserByTagsBySQL(List<String> tagNameList){
        //遇事先判空
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for(String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

}



