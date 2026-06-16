package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT c.*, u.username, u.nickname FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.id = #{id}")
    Comment findById(Integer id);

    @Select("SELECT c.*, u.username, u.nickname FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.discussion_id = #{discussionId} AND c.parent_id IS NULL " +
            "ORDER BY c.created_at ASC")
    List<Comment> findByDiscussionId(Integer discussionId);

    @Select("SELECT c.*, u.username, u.nickname FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.parent_id = #{parentId} ORDER BY c.created_at ASC")
    List<Comment> findRepliesByParentId(Integer parentId);

    @Insert("INSERT INTO comment(discussion_id, user_id, parent_id, content, created_at) " +
            "VALUES(#{discussionId}, #{userId}, #{parentId}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM comment WHERE discussion_id = #{discussionId}")
    int deleteByDiscussionId(Integer discussionId);

    @Delete("DELETE FROM comment WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
}
