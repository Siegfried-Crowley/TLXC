package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.UserFavorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    @Select("SELECT f.*, p.title AS problemTitle, p.difficulty AS problemDifficulty " +
            "FROM user_favorite f JOIN problem p ON f.problem_id = p.id " +
            "WHERE f.user_id = #{userId} ORDER BY f.created_at DESC")
    List<UserFavorite> findByUserId(Integer userId);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND problem_id = #{problemId}")
    int countByUserAndProblem(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE problem_id = #{problemId}")
    int countByProblemId(Integer problemId);

    @Insert("INSERT INTO user_favorite(user_id, problem_id, created_at) VALUES(#{userId}, #{problemId}, NOW())")
    int insert(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND problem_id = #{problemId}")
    int delete(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
}
