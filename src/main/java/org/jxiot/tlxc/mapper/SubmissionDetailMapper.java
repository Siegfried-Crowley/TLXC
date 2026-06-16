package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.SubmissionDetail;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubmissionDetailMapper {

    @Select("SELECT * FROM submission_detail WHERE submission_id = #{submissionId}")
    List<SubmissionDetail> findBySubmissionId(Integer submissionId);

    @Insert("<script>" +
            "INSERT INTO submission_detail(submission_id, test_case_id, passed, input_data, expected_output, actual_output, runtime_ms, created_at) " +
            "VALUES " +
            "<foreach collection='list' item='d' separator=','>" +
            "(#{d.submissionId}, #{d.testCaseId}, #{d.passed}, #{d.inputData}, #{d.expectedOutput}, #{d.actualOutput}, #{d.runtimeMs}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<SubmissionDetail> details);

    @Delete("DELETE FROM submission_detail WHERE submission_id = #{submissionId}")
    int deleteBySubmissionId(Integer submissionId);
}
