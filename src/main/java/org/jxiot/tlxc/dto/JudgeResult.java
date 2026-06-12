package org.jxiot.tlxc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResult {
    private String status;
    private Integer runtimeMs;
    private Integer passedCases;
    private Integer totalCases;
    private String errorMessage;
}
