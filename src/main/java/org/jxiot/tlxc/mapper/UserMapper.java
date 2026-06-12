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

    @Update("UPDATE user SET nickname=#{nickname}, total_points=#{totalPoints}, streak_days=#{streakDays}, " +
            "last_pass_date=#{lastPassDate}, last_login_at=#{lastLoginAt} WHERE id=#{id}")
    int update(User user);

    @Select("SELECT * FROM user ORDER BY total_points DESC LIMIT #{limit}")
    List<User> findTopUsers(int limit);

    @Select("SELECT COUNT(*) FROM user")
    int count();

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);
}
