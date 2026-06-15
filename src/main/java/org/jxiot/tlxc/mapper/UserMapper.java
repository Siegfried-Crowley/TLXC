package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user(username, hashed_password, nickname, role, status, total_points, streak_days, created_at) " +
            "VALUES(#{username}, #{hashedPassword}, #{nickname}, #{role}, #{status}, #{totalPoints}, #{streakDays}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("<script>" +
            "UPDATE user SET " +
            "<if test='nickname != null'>nickname=#{nickname},</if>" +
            "<if test='totalPoints != null'>total_points=#{totalPoints},</if>" +
            "<if test='streakDays != null'>streak_days=#{streakDays},</if>" +
            "<if test='lastPassDate != null'>last_pass_date=#{lastPassDate},</if>" +
            "<if test='lastLoginAt != null'>last_login_at=#{lastLoginAt},</if>" +
            "<if test='role != null'>role=#{role},</if>" +
            "<if test='status != null'>status=#{status},</if>" +
            // Remove trailing comma
            "id=#{id} " +
            "WHERE id=#{id}" +
            "</script>")
    int update(User user);

    @Select("SELECT * FROM user ORDER BY total_points DESC LIMIT #{limit}")
    List<User> findTopUsers(int limit);

    @Select("SELECT COUNT(*) FROM user")
    int count();

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);
}
