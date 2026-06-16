package org.jxiot.tlxc.mapper;

import org.apache.ibatis.annotations.*;
import org.jxiot.tlxc.entity.ContestProblem;
import java.util.List;

@Mapper
public interface ContestProblemMapper {

    @Select("SELECT * FROM contest_problem WHERE contest_id = #{contestId} ORDER BY order_index ASC")
    List<ContestProblem> findByContestId(Integer contestId);

    @Select("SELECT problem_id FROM contest_problem WHERE contest_id = #{contestId} ORDER BY order_index ASC")
    List<Integer> findProblemIdsByContestId(Integer contestId);

    @Insert("INSERT INTO contest_problem(contest_id, problem_id, order_index) VALUES(#{contestId}, #{problemId}, #{orderIndex})")
    int insert(ContestProblem contestProblem);

    @Delete("DELETE FROM contest_problem WHERE contest_id = #{contestId}")
    int deleteByContestId(Integer contestId);
}
