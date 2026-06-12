package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.DailyContest;
import org.apache.ibatis.annotations.*;
import java.util.Date;
import java.util.List;

@Mapper
public interface ContestMapper {

    @Select("SELECT * FROM daily_contest WHERE contest_date = #{contestDate}")
    DailyContest findByDate(Date contestDate);

    @Select("SELECT * FROM daily_contest ORDER BY contest_date DESC LIMIT #{limit}")
    List<DailyContest> findRecentContests(int limit);

    @Insert("INSERT INTO daily_contest(contest_date, title, status, generated_at) " +
            "VALUES(#{contestDate}, #{title}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DailyContest contest);

    @Update("UPDATE daily_contest SET status=#{status}, finalized_at=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
}
