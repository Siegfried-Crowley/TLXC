package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Submission {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private String code;
    private String language;
    private String status;
    private Integer runtimeMs;
    private Integer passedCases;
    private Integer totalCases;
    private String errorMessage;
    private Integer scoreDelta;
    private Date createdAt;

    // transient
    private String username;
    private String problemTitle;
    private String problemDifficulty;
    private List<SubmissionDetail> details;
}
