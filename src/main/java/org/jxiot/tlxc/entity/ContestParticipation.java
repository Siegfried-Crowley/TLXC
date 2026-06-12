package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ContestParticipation {
    private Integer id;
    private Integer contestId;
    private Integer userId;
    private Date startedAt;
    private Date deadlineAt;
    private Date submittedAt;
    private Integer totalScore;
    private Integer acceptedCount;
    private Integer totalRuntimeMs;
    private Integer rank;
}
