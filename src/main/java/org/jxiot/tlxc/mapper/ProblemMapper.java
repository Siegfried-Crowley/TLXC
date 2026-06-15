package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Problem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProblemMapper {

    @Select("SELECT * FROM problem WHERE id = #{id}")
    Problem findById(Integer id);

    @Select("<script>" +
            "SELECT * FROM problem WHERE 1=1" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='isActive != null'> AND is_active = #{isActive}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<Problem> findByCondition(@Param("difficulty") String difficulty,
                                  @Param("status") String status,
                                  @Param("isActive") Boolean isActive);

    @Insert("INSERT INTO problem(title, difficulty, tags, description, input_description, output_description, " +
            "examples, hint, constraints, source, status, time_limit_ms, memory_limit_mb, created_by, starter_code, is_active, created_at, updated_at) " +
            "VALUES(#{title}, #{difficulty}, #{tags}, #{description}, #{inputDescription}, #{outputDescription}, " +
            "#{examples}, #{hint}, #{constraints}, #{source}, #{status}, #{timeLimitMs}, #{memoryLimitMb}, " +
            "#{createdBy}, #{starterCode}, #{isActive}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Problem problem);

    @Update("UPDATE problem SET title=#{title}, difficulty=#{difficulty}, tags=#{tags}, description=#{description}, " +
            "input_description=#{inputDescription}, output_description=#{outputDescription}, examples=#{examples}, " +
            "hint=#{hint}, constraints=#{constraints}, source=#{source}, status=#{status}, time_limit_ms=#{timeLimitMs}, " +
            "memory_limit_mb=#{memoryLimitMb}, starter_code=#{starterCode}, is_active=#{isActive}, updated_at=NOW() " +
            "WHERE id=#{id}")
    int update(Problem problem);

    @Update("UPDATE problem SET title=#{title}, difficulty=#{difficulty}, tags=#{tags}, description=#{description}, " +
            "input_description=#{inputDescription}, output_description=#{outputDescription}, examples=#{examples}, " +
            "starter_code=#{starterCode}, status=#{status}, is_active=TRUE, updated_at=NOW() WHERE id=#{id}")
    int updateAdmin(Problem problem);

    @Delete("DELETE FROM problem WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM problem")
    int count();
}
