package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.Tag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tag ORDER BY name ASC")
    List<Tag> findAll();

    @Select("SELECT t.* FROM tag t JOIN problem_tag pt ON t.id = pt.tag_id WHERE pt.problem_id = #{problemId}")
    List<Tag> findByProblemId(Integer problemId);

    @Select("SELECT * FROM tag WHERE name = #{name}")
    Tag findByName(String name);

    @Insert("INSERT INTO tag(name, color, created_at) VALUES(#{name}, #{color}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Insert("INSERT INTO problem_tag(problem_id, tag_id) VALUES(#{problemId}, #{tagId})")
    int addProblemTag(@Param("problemId") Integer problemId, @Param("tagId") Integer tagId);

    @Delete("DELETE FROM problem_tag WHERE problem_id = #{problemId}")
    int removeAllProblemTags(Integer problemId);

    @Delete("DELETE FROM tag WHERE id = #{id}")
    int deleteById(Integer id);
}
