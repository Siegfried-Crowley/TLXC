package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.UserProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserProfileMapper {

    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    UserProfile findByUserId(Integer userId);

    @Insert("INSERT INTO user_profile(user_id, bio, avatar_url, github_url, website_url, organization, location, accepted_problems, total_submissions, acceptance_rate) " +
            "VALUES(#{userId}, #{bio}, #{avatarUrl}, #{githubUrl}, #{websiteUrl}, #{organization}, #{location}, #{acceptedProblems}, #{totalSubmissions}, #{acceptanceRate})")
    int insert(UserProfile profile);

    @Update("UPDATE user_profile SET bio=#{bio}, avatar_url=#{avatarUrl}, github_url=#{githubUrl}, " +
            "website_url=#{websiteUrl}, organization=#{organization}, location=#{location}, " +
            "accepted_problems=#{acceptedProblems}, total_submissions=#{totalSubmissions}, " +
            "acceptance_rate=#{acceptanceRate}, updated_at=NOW() WHERE user_id=#{userId}")
    int update(UserProfile profile);

    @Update("UPDATE user_profile SET accepted_problems = (SELECT COUNT(DISTINCT problem_id) FROM submission WHERE user_id = #{userId} AND status = 'accepted'), " +
            "total_submissions = (SELECT COUNT(*) FROM submission WHERE user_id = #{userId}), " +
            "acceptance_rate = CASE WHEN (SELECT COUNT(*) FROM submission WHERE user_id = #{userId}) > 0 " +
            "THEN (SELECT COUNT(DISTINCT problem_id) FROM submission WHERE user_id = #{userId} AND status = 'accepted') * 100.0 / " +
            "(SELECT COUNT(DISTINCT problem_id) FROM submission WHERE user_id = #{userId}) ELSE 0 END, " +
            "updated_at = NOW() WHERE user_id = #{userId}")
    int refreshStats(Integer userId);

    @Delete("DELETE FROM user_profile WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
}
