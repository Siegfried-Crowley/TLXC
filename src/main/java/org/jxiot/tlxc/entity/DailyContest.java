package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class DailyContest {
    private Integer id;
    private Date contestDate;
    private String title;
    private String status;
    private Date generatedAt;
    private Date finalizedAt;
}
