package com.projectgroup.project.Controller;

import com.alibaba.fastjson2.JSON;
import com.projectgroup.project.Dto.PasswordChangeDto;
import com.projectgroup.project.Model.UserInfo;
import com.projectgroup.project.ReturnData.LoginReturnData;
import com.projectgroup.project.Model.User;
import com.projectgroup.project.Dto.UserDto;
import com.projectgroup.project.ReturnData.BasicReturnData;
import com.projectgroup.project.ReturnData.RefreshCookieReturnData;
import com.projectgroup.project.Service.UserInfoService;
import com.projectgroup.project.Service.UserService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/")
public class LoginController {
    @Resource
    UserService userService;
    @Resource
    UserInfoService userInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginReturnData login(@RequestBody UserDto user) {
        String userName = user.username();
        String password = user.password();
        Boolean res = userService.checkValidUser(userName, password);
        if (res) {
            User user1 = userService.getUser(userName);
            String cookie = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(cookie, JSON.toJSONString(user1), 1, TimeUnit.HOURS);
            return new LoginReturnData(0, "success",user1.getUsername(),
                    user1.getPrivilege(), user1.getId(), user1.getHead(),
                    cookie);
        } else {
            return new LoginReturnData(-1, "wrong password or username");
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BasicReturnData registerCheck(@RequestBody UserDto user) {
        String userName = user.username();
        String password = user.password();
        User user1 = userService.getUser(userName);
        if (user1 == null) {
            User newUser = new User();
            newUser.setUsername(userName);
            newUser.setPassword(password);
            userService.insertUser(newUser);
            UserInfo newUserInfo =  new UserInfo();
            newUserInfo.setId(userService.getId(userName));
            userInfoService.insertUserInfo(newUserInfo);
            return new BasicReturnData(0, "ok");
        } else {
            return new BasicReturnData(-1, "failed");
        }
    }

    @RequestMapping(value = "/register/check-username", method = RequestMethod.GET)
    public BasicReturnData repeatedNameCheck(@RequestParam("username") String userName) {
        if (!userService.checkRepeatedName(userName)) {
            return new BasicReturnData(0, "ok");
        } else {
            return new BasicReturnData(-1, "repeated!");
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public BasicReturnData logout(@RequestParam("username") String userName,
                                  @CookieValue("login-cookie")String cookie) {
        if (!userService.checkCookie(userName, cookie)) {
            return new BasicReturnData(402, "cookie invalid");
        }
        String jsonValue = (String)redisTemplate.opsForValue().get(cookie);

        User userInCache = JSON.parseObject(jsonValue, User.class);
        if (userInCache == null) {
            return new BasicReturnData(-1, "userInCache is null");
        }
        String nameInCache = userInCache.getUsername();
        if (userName.equals(nameInCache)) {
            redisTemplate.delete(cookie);
            return new BasicReturnData(0, "ok");
        } else {
            return new BasicReturnData(-1, "Failed!");
        }
    }

    @RequestMapping(value = "/check-login-cookie", method = RequestMethod.GET)
    public RefreshCookieReturnData refreshCookie(@RequestParam("username") String userName,
                                                 @CookieValue("login-cookie")String cookie) {
        if (!userService.checkCookie(userName,cookie)) {
            return new RefreshCookieReturnData(402, "cookie invalid");
        }
        redisTemplate.expire(cookie, 30, TimeUnit.MINUTES);
        String jsonValue = (String)redisTemplate.opsForValue().get(cookie);
        User user = JSON.parseObject(jsonValue, User.class);
        if (user == null) {
            return new RefreshCookieReturnData(-1, "'Null' Error!");
        }
        return new RefreshCookieReturnData(0, "ok", userName, user.getPrivilege(),
                user.getId(), user.getHead(), cookie);
    }

    public BasicReturnData changePassword(@RequestBody PasswordChangeDto passwordChangeDto,
                                          @RequestParam("username") String username,
                                          @CookieValue(name = "login-cookie", defaultValue = "NoCookie") String cookie) {
        if(!userService.checkCookie(username,cookie)) {
            return new BasicReturnData(402, "cookie invalid");
        }
        String jsonValue = (String)redisTemplate.opsForValue().get(cookie);
        User user = JSON.parseObject(jsonValue, User.class);
        if (user == null) {
            return new BasicReturnData(-1, "'Null' Error!");
        }
        if (user.getPassword().equals(passwordChangeDto.old_password())) {
            boolean check = userService.updatePassword(user.getId(), passwordChangeDto.new_password());
            if (check) {
                return new BasicReturnData(0, "ok");
            } else {
                return new BasicReturnData(-2, "Update Failed");
            }
        } else {
            return new BasicReturnData(-3, "The origin password is incorrect!");
        }
    }
}



