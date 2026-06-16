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
            "<if test='keyword != null'> AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY id ASC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Problem> findByCondition(@Param("difficulty") String difficulty,
                                  @Param("status") String status,
                                  @Param("isActive") Boolean isActive,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM problem WHERE 1=1" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='isActive != null'> AND is_active = #{isActive}</if>" +
            "<if test='keyword != null'> AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "</script>")
    long countByCondition(@Param("difficulty") String difficulty,
                          @Param("status") String status,
                          @Param("isActive") Boolean isActive,
                          @Param("keyword") String keyword);

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

    @Select("SELECT * FROM problem WHERE status = 'published' AND is_active = TRUE ORDER BY RAND() LIMIT #{limit}")
    List<Problem> findRandomPublished(@Param("limit") int limit);

    @Update("UPDATE problem SET status = 'published', is_active = TRUE WHERE status != 'published' OR is_active != TRUE")
    int publishAllDrafts();

    @Select("SELECT COUNT(*) FROM problem WHERE status = 'published' AND is_active = TRUE")
    int countPublished();

    @Select("SELECT COUNT(*) FROM problem")
    int count();
}
