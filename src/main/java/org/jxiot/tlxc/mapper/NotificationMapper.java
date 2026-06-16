package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification findById(Integer id);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<Notification> findByUserId(@Param("userId") Integer userId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = FALSE")
    int countUnread(Integer userId);

    @Insert("INSERT INTO notification(user_id, type, title, content, related_id, is_read, created_at) " +
            "VALUES(#{userId}, #{type}, #{title}, #{content}, #{relatedId}, FALSE, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Insert("<script>" +
            "INSERT INTO notification(user_id, type, title, content, related_id, is_read, created_at) " +
            "VALUES " +
            "<foreach collection='list' item='n' separator=','>" +
            "(#{n.userId}, #{n.type}, #{n.title}, #{n.content}, #{n.relatedId}, FALSE, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<Notification> notifications);

    @Update("UPDATE notification SET is_read = TRUE WHERE id = #{id}")
    int markAsRead(Integer id);

    @Update("UPDATE notification SET is_read = TRUE WHERE user_id = #{userId} AND is_read = FALSE")
    int markAllAsRead(Integer userId);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM notification WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
}
