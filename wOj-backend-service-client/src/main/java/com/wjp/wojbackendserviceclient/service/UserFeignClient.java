package com.wjp.wojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjp.wojbackendcommon.common.ErrorCode;
import com.wjp.wojbackendcommon.constant.UserConstant;
import com.wjp.wojbackendcommon.exception.BusinessException;
import com.wjp.wojbackendcommon.model.dto.user.UserQueryRequest;
import com.wjp.wojbackendcommon.model.entity.User;
import com.wjp.wojbackendcommon.model.enums.UserRoleEnum;
import com.wjp.wojbackendcommon.model.vo.LoginUserVO;
import com.wjp.wojbackendcommon.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://wjp.icu">编程导航知识星球</a>
 */
// ✨ ✨ ✨ 通过 Feign 调用 wOj-backend-user-service 服务【就是wOj-backend-user-service项目中 application.yml 中name的名字】, 路勁就是 wOj-backend-user-service 中 application.yml 定义的路径
@FeignClient(name = "wOj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {


    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    //✨ @RequestParam("userId") long userId: 表示请求参数的名称，即在请求路径中指定的参数名称
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据 id 获取用户列表
     *
     * @param idList
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }


    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}
