package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Submission {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private String code;
    private String status;
    private Integer runtimeMs;
    private Integer passedCases;
    private Integer totalCases;
    private String errorMessage;
    private Integer scoreDelta;
    private Date createdAt;
}
