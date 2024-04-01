package com.projectgroup.project.Service;


import com.alibaba.fastjson2.JSON;
import com.projectgroup.project.Dao.UserDao;
import com.projectgroup.project.Model.User;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    UserDao userDao;

    @Resource
    RedisTemplate<String,Object> redisTemplate;
    public boolean insertUser(User user) {
        return userDao.insertUser(user);
    }
    public boolean deleteUser(String userName) {
        return userDao.deleteUser(userName);
    }
    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }
    public boolean updatePassword(int id, String newPassword) {
        return userDao.updatePassword(id, newPassword);
    }
    public Boolean checkValidUser(String userName,String password) {
        return userDao.checkValidUser(userName,password);
    }
    public User getUser(String userName) {
        return userDao.getUser(userName);
    }
    public Boolean checkRepeatedName(String userName) {
        return userDao.checkRepeatedName(userName);
    }

    public int getId(String userName) {
        return userDao.getId(userName);
    }
    public boolean checkCookie(String userName,String cookie) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cookie))) {
            return false;
        }
        String jsonValue = (String)redisTemplate.opsForValue().get(cookie);

        User userInCache = JSON.parseObject(jsonValue, User.class);
        if (userInCache == null) {
            return false;
        }
        if (!userInCache.getUsername().equals(userName)) {
            return false;
        }
        return true;
    }
    public boolean updateHead(int id, String head) {
        return userDao.updateHeadPortrait(head, id);
    }


}
