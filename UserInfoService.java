package com.projectgroup.project.Service;


import com.projectgroup.project.Dao.UserInfoDao;
import com.projectgroup.project.Model.UserInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
    @Resource
    UserInfoDao userInfoDao;

    public void insertUserInfo(UserInfo userInfo) {
        userInfoDao.insertUserInfo(userInfo);
    }

    public UserInfo selectUserInfo(int id) {
        return userInfoDao.selectUserInfo(id);
    }

    public boolean updateUserInfo(UserInfo userInfo) {
        return userInfoDao.updateUserInfo(userInfo);
    }

    public boolean deleteUserInfo(int id) {
        return userInfoDao.deleteUserInfo(id);
    }

}
