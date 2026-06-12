package org.jxiot.tlxc.service;

import org.jxiot.tlxc.dto.JudgeResult;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class JudgeService {

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    public JudgeResult judgeCode(Integer problemId, String code) {
        List<TestCase> testCases = testCaseMapper.findByProblemId(problemId);

        int passedCases = 0;
        int totalCases = testCases.size();
        int maxRuntime = 0;
        String errorMessage = "";
        String status = "accepted";

        for (TestCase testCase : testCases) {
            try {
                long startTime = System.currentTimeMillis();

                String result = executePythonCode(code, testCase.getInputData());

                long endTime = System.currentTimeMillis();
                int runtime = (int) (endTime - startTime);
                maxRuntime = Math.max(maxRuntime, runtime);

                if (result.trim().equals(testCase.getExpectedOutput().trim())) {
                    passedCases++;
                } else {
                    status = "wrong_answer";
                    errorMessage = "输出不匹配";
                    break;
                }
            } catch (Exception e) {
                status = "error";
                errorMessage = e.getMessage();
                break;
            }
        }

        JudgeResult result = new JudgeResult();
        result.setStatus(status);
        result.setRuntimeMs(maxRuntime);
        result.setPassedCases(passedCases);
        result.setTotalCases(totalCases);
        result.setErrorMessage(errorMessage);

        return result;
    }

    private String executePythonCode(String code, String inputData) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python", "-c", buildPythonScript(code, inputData));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("代码执行失败: " + output.toString());
        }

        return output.toString().trim();
    }

    private String buildPythonScript(String code, String inputData) {
        return code + "\n" +
                "import json\n" +
                "import sys\n" +
                "try:\n" +
                "    input_data = json.loads('" + inputData.replace("'", "\\'") + "')\n" +
                "    result = solve(*input_data) if isinstance(input_data, list) else solve(input_data)\n" +
                "    print(json.dumps(result) if isinstance(result, (list, dict)) else result)\n" +
                "except Exception as e:\n" +
                "    print(str(e), file=sys.stderr)\n" +
                "    sys.exit(1)";
    }

    public Submission saveSubmission(Submission submission) {
        submissionMapper.insert(submission);
        return submission;
    }
}
