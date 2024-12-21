package com.wjp.wojbackenduserservice.controller.inner;

import com.wjp.wojbackendcommon.model.entity.User;
import com.wjp.wojbackendserviceclient.service.UserFeignClient;
import com.wjp.wojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    //✨ @RequestParam("userId") long userId: 表示请求参数的名称，即在请求路径中指定的参数名称
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);

    }

    /**
     * 根据 id 获取用户列表
     *
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }

}
