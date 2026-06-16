package org.jxiot.tlxc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResult {
    private String status;
    private Integer runtimeMs;
    private Integer passedCases;
    private Integer totalCases;
    private String errorMessage;
    private List<TestCaseResult> caseResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseResult {
        private Integer testCaseId;
        private String inputData;
        private String expectedOutput;
        private String actualOutput;
        private Integer runtimeMs;
        private boolean passed;
        private boolean hidden;
    }
}
