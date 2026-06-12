package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.ContestParticipation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContestParticipationMapper {

    @Select("SELECT * FROM contest_participation WHERE contest_id = #{contestId} AND user_id = #{userId}")
    ContestParticipation findByContestAndUser(@Param("contestId") Integer contestId, @Param("userId") Integer userId);

    @Select("SELECT * FROM contest_participation WHERE contest_id = #{contestId} ORDER BY total_score DESC, total_runtime_ms ASC")
    List<ContestParticipation> findByContestId(Integer contestId);

    @Insert("INSERT INTO contest_participation(contest_id, user_id, started_at, deadline_at, total_score, accepted_count, total_runtime_ms) " +
            "VALUES(#{contestId}, #{userId}, NOW(), #{deadlineAt}, #{totalScore}, #{acceptedCount}, #{totalRuntimeMs})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContestParticipation participation);

    @Update("UPDATE contest_participation SET total_score=#{totalScore}, accepted_count=#{acceptedCount}, " +
            "total_runtime_ms=#{totalRuntimeMs}, submitted_at=NOW() WHERE id=#{id}")
    int update(ContestParticipation participation);
}
