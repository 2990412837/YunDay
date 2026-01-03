/**
 * 通过用户名查询用户对象
 *  1. 定义sql语句
 *  2. 设置参数集合
 *  3. 调用BaseDao的查询方法
 * @param userName
 * @return
 */
package com.yjx.note.dao;

import com.yjx.note.po.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    //根据用户名字查询User用户
    User queryUserByName(String uname);

    User queryUserByNickAndUserId(@Param("nick") String nick, @Param("userId") Integer
            userId);

    int updateUser(User user);
}
