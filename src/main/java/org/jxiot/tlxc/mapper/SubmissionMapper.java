package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Submission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubmissionMapper {

    @Select("SELECT s.*, u.username, p.title AS problemTitle, p.difficulty AS problemDifficulty " +
            "FROM submission s " +
            "JOIN user u ON s.user_id = u.id " +
            "JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.id = #{id}")
    Submission findById(Integer id);

    @Select("SELECT s.*, u.username, p.title AS problemTitle, p.difficulty AS problemDifficulty " +
            "FROM submission s " +
            "JOIN user u ON s.user_id = u.id " +
            "JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.user_id = #{userId} ORDER BY s.created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Submission> findByUserId(@Param("userId") Integer userId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM submission WHERE user_id = #{userId}")
    long countByUserId(Integer userId);

    @Select("SELECT s.*, u.username, p.title AS problemTitle, p.difficulty AS problemDifficulty " +
            "FROM submission s " +
            "JOIN user u ON s.user_id = u.id " +
            "JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.problem_id = #{problemId} ORDER BY s.created_at DESC")
    List<Submission> findByProblemId(Integer problemId);

    @Insert("INSERT INTO submission(user_id, problem_id, code, language, status, runtime_ms, passed_cases, total_cases, error_message, score_delta, created_at) " +
            "VALUES(#{userId}, #{problemId}, #{code}, #{language}, #{status}, #{runtimeMs}, #{passedCases}, #{totalCases}, #{errorMessage}, #{scoreDelta}, NOW())")
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

    @Delete("DELETE FROM submission WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);

    // Get submission history for a user on a specific problem for comparison
    @Select("SELECT s.*, p.title AS problemTitle, p.difficulty AS problemDifficulty " +
            "FROM submission s " +
            "JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.user_id = #{userId} AND s.problem_id = #{problemId} " +
            "ORDER BY s.created_at DESC")
    List<Submission> findByUserAndProblem(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    // Get runtime history for stats chart
    @Select("SELECT s.created_at, s.runtime_ms, s.passed_cases, s.total_cases, s.status " +
            "FROM submission s WHERE s.user_id = #{userId} AND s.problem_id = #{problemId} " +
            "ORDER BY s.created_at ASC")
    List<Submission> findHistoryByUserAndProblem(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    // Aggregated stats
    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM submission " +
            "WHERE user_id = #{userId} AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) ORDER BY date ASC")
    List<java.util.Map<String, Object>> countByDay(@Param("userId") Integer userId, @Param("days") int days);

    @Select("SELECT p.difficulty, COUNT(*) as count FROM submission s " +
            "JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.user_id = #{userId} AND s.status = 'accepted' " +
            "GROUP BY p.difficulty")
    List<java.util.Map<String, Object>> countAcceptedByDifficulty(Integer userId);
}
