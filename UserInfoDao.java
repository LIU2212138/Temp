package com.projectgroup.project.Dao;


import com.projectgroup.project.Model.User;
import com.projectgroup.project.Model.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoDao {
    boolean insertUserInfo(UserInfo userInfo);
    boolean deleteUserInfo(int id);

    boolean updateUserInfo(UserInfo userInfo);

    UserInfo selectUserInfo(int id);

}
