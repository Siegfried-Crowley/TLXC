package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Discussion;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DiscussionMapper {

    @Select("SELECT d.*, u.username, u.nickname FROM discussion d " +
            "JOIN user u ON d.user_id = u.id " +
            "WHERE d.id = #{id}")
    Discussion findById(Integer id);

    @Select("SELECT d.*, u.username, u.nickname FROM discussion d " +
            "JOIN user u ON d.user_id = u.id " +
            "WHERE d.problem_id = #{problemId} AND d.type = #{type} " +
            "ORDER BY d.is_pinned DESC, d.created_at DESC")
    List<Discussion> findByProblemIdAndType(@Param("problemId") Integer problemId, @Param("type") String type);

    @Select("SELECT d.*, u.username, u.nickname FROM discussion d " +
            "JOIN user u ON d.user_id = u.id " +
            "WHERE d.type = #{type} ORDER BY d.created_at DESC LIMIT #{limit}")
    List<Discussion> findRecentByType(@Param("type") String type, @Param("limit") int limit);

    @Insert("INSERT INTO discussion(problem_id, user_id, title, content, type, created_at, updated_at) " +
            "VALUES(#{problemId}, #{userId}, #{title}, #{content}, #{type}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Discussion discussion);

    @Update("UPDATE discussion SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Integer id);

    @Update("UPDATE discussion SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Integer id);

    @Update("UPDATE discussion SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(Integer id);

    @Update("UPDATE discussion SET title=#{title}, content=#{content}, updated_at=NOW() WHERE id=#{id}")
    int update(Discussion discussion);

    @Delete("DELETE FROM discussion WHERE id = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM discussion WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Select("SELECT COUNT(*) FROM discussion WHERE problem_id = #{problemId} AND type = 'solution'")
    int countSolutionsByProblemId(Integer problemId);
}
