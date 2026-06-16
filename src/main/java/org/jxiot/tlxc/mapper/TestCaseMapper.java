package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.TestCase;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TestCaseMapper {

    @Select("SELECT * FROM test_case WHERE problem_id = #{problemId}")
    List<TestCase> findByProblemId(Integer problemId);

    @Select("SELECT * FROM test_case WHERE problem_id = #{problemId} AND is_hidden = 0")
    List<TestCase> findVisibleByProblemId(Integer problemId);

    @Insert("INSERT INTO test_case(problem_id, input_data, expected_output, is_hidden, score_weight, remark, updated_at) " +
            "VALUES(#{problemId}, #{inputData}, #{expectedOutput}, #{isHidden}, #{scoreWeight}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TestCase testCase);

    @Delete("DELETE FROM test_case WHERE problem_id = #{problemId}")
    int deleteByProblemId(Integer problemId);

    @Delete("DELETE FROM test_case WHERE id = #{id}")
    int deleteById(Integer id);
}
