package org.jxiot.tlxc.dto;

import lombok.Data;

@Data
public class ProblemDTO {
    private Integer id;
    private String title;
    private String difficulty;
    private String tags;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String examples;
    private String hint;
    private String constraints;
    private String source;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private String starterCode;
}
