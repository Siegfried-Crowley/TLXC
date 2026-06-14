package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Submission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubmissionMapper {

    @Select("SELECT * FROM submission WHERE id = #{id}")
    Submission findById(Integer id);

    @Select("SELECT * FROM submission WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<Submission> findByUserId(@Param("userId") Integer userId, @Param("limit") int limit);

    @Select("SELECT * FROM submission WHERE problem_id = #{problemId} ORDER BY created_at DESC")
    List<Submission> findByProblemId(Integer problemId);

    @Insert("INSERT INTO submission(user_id, problem_id, code, status, runtime_ms, passed_cases, total_cases, error_message, score_delta, created_at) " +
            "VALUES(#{userId}, #{problemId}, #{code}, #{status}, #{runtimeMs}, #{passedCases}, #{totalCases}, #{errorMessage}, #{scoreDelta}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Submission submission);

    @Select("SELECT COUNT(*) FROM submission WHERE user_id = #{userId} AND problem_id = #{problemId} AND status = 'accepted'")
    int countAcceptedByUserAndProblem(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    @Update("UPDATE submission SET score_delta = #{scoreDelta} WHERE id = #{id}")
    int updateScoreDelta(@Param("id") Integer id, @Param("scoreDelta") Integer scoreDelta);

    @Select("SELECT COUNT(*) FROM submission")
    int count();

    @Select("SELECT COUNT(DISTINCT user_id) FROM submission WHERE DATE(created_at) = CURDATE()")
    int countTodayActiveUsers();
}
