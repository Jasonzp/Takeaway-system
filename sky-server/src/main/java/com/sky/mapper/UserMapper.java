package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    // 根据openid查询用户
    @Select("select * from user where openid =#{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    Integer sumCurrentDate(Map map);
}
