package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.PointLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointLogMapper {

    @Select("SELECT * FROM point_log WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<PointLog> findByUserId(Integer userId);

    @Insert("INSERT INTO point_log(user_id, problem_id, points, reason, created_at) " +
            "VALUES(#{userId}, #{problemId}, #{points}, #{reason}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointLog pointLog);
}
