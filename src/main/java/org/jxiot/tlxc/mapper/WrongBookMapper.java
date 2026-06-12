package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.WrongBook;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface WrongBookMapper {

    @Select("SELECT * FROM wrong_book WHERE user_id = #{userId} AND status = #{status}")
    List<WrongBook> findByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);

    @Select("SELECT * FROM wrong_book WHERE user_id = #{userId} AND problem_id = #{problemId}")
    WrongBook findByUserIdAndProblemId(@Param("userId") Integer userId, @Param("problemId") Integer problemId);

    @Insert("INSERT INTO wrong_book(user_id, problem_id, last_submission_id, status, note, wrong_count, created_at, updated_at) " +
            "VALUES(#{userId}, #{problemId}, #{lastSubmissionId}, #{status}, #{note}, #{wrongCount}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WrongBook wrongBook);

    @Update("UPDATE wrong_book SET last_submission_id=#{lastSubmissionId}, wrong_count=wrong_count+1, updated_at=NOW() " +
            "WHERE user_id=#{userId} AND problem_id=#{problemId}")
    int updateWrongCount(@Param("userId") Integer userId, @Param("problemId") Integer problemId, @Param("lastSubmissionId") Integer lastSubmissionId);

    @Update("UPDATE wrong_book SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    int updateStatus(WrongBook wrongBook);
}
