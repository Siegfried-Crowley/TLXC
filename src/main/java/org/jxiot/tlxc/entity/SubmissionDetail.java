package org.jxiot.tlxc.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SubmissionDetail {
    private Integer id;
    private Integer submissionId;
    private Integer testCaseId;
    private Boolean passed;
    private String inputData;
    private String expectedOutput;
    private String actualOutput;
    private Integer runtimeMs;
    private Date createdAt;
}
