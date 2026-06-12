package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PointLog {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private Integer points;
    private String reason;
    private Date createdAt;
}
