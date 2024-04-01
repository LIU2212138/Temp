package com.projectgroup.project.Dao;

import com.projectgroup.project.Model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao{
    boolean insertUser(User user);
    boolean deleteUser(String userName);
    boolean updateUser(User user);

    boolean updatePassword(int id, String password);
    //@Select("select (count(user_id) > 0) from users where user_name = #{userName} and user_password = #{password}")
    Boolean checkValidUser(@Param("userName") String userName, @Param("password") String password);
    User getUser(@Param("userName") String userName);
    Boolean checkRepeatedName(@Param("userName") String userName);
//    Boolean updateCookie(@Param("id") int id, @Param("cookie" ) String cookie);
    int getId (String userName);
    boolean updateHeadPortrait(String head, int id);



}
