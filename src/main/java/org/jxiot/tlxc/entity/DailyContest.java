package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DailyContest {
    private Integer id;
    private Date contestDate;
    private String title;
    private String status;
    private Date generatedAt;
    private Date finalizedAt;

    // 非数据库字段，用于传输关联题目
    private List<Problem> problems;
}
