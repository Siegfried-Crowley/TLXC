package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.UserFollow;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserFollowMapper {

    @Select("SELECT f.*, u.username AS followingUsername, u.nickname AS followingNickname, u.total_points AS followingTotalPoints " +
            "FROM user_follow f JOIN user u ON f.following_id = u.id " +
            "WHERE f.follower_id = #{followerId} ORDER BY f.created_at DESC")
    List<UserFollow> findFollowing(Integer followerId);

    @Select("SELECT f.*, u.username AS followingUsername, u.nickname AS followingNickname " +
            "FROM user_follow f JOIN user u ON f.follower_id = u.id " +
            "WHERE f.following_id = #{followingId} ORDER BY f.created_at DESC")
    List<UserFollow> findFollowers(Integer followingId);

    @Select("SELECT COUNT(*) FROM user_follow WHERE follower_id = #{userId}")
    int countFollowing(Integer userId);

    @Select("SELECT COUNT(*) FROM user_follow WHERE following_id = #{userId}")
    int countFollowers(Integer userId);

    @Select("SELECT COUNT(*) FROM user_follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int isFollowing(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    @Insert("INSERT INTO user_follow(follower_id, following_id, created_at) VALUES(#{followerId}, #{followingId}, NOW())")
    int insert(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    @Delete("DELETE FROM user_follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int delete(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    @Delete("DELETE FROM user_follow WHERE follower_id = #{userId} OR following_id = #{userId}")
    int deleteByUserId(Integer userId);
}
