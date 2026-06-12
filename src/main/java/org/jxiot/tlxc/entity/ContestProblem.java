package org.jxiot.tlxc.entity;

import lombok.Data;

@Data
public class ContestProblem {
    private Integer id;
    private Integer contestId;
    private Integer problemId;
    private Integer orderIndex;
}
