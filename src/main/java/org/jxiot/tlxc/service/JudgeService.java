package org.jxiot.tlxc.service;

import org.jxiot.tlxc.dto.JudgeResult;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {

    private static final long EXECUTION_TIMEOUT_MS = 30_000; // 30 seconds max
    private static final int MAX_OUTPUT_LINES = 1000;
    private static final int MAX_OUTPUT_CHARS = 100_000;

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
        // Write user code to a temp file instead of passing via -c to avoid command injection
        Path tempFile = null;
        Path tempInputFile = null;
        try {
            tempFile = Files.createTempFile("user_code_", ".py");
            Files.writeString(tempFile, sanitizeCode(code), StandardCharsets.UTF_8);

            // Write input data to temp file
            tempInputFile = Files.createTempFile("user_input_", ".json");
            Files.writeString(tempInputFile, inputData, StandardCharsets.UTF_8);

            ProcessBuilder pb = new ProcessBuilder("python", tempFile.toAbsolutePath().toString());
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Write stdin from input file
            try (OutputStream os = process.getOutputStream()) {
                os.write(inputData.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int lineCount = 0;
                int totalChars = 0;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    totalChars += line.length() + 1;
                    if (lineCount > MAX_OUTPUT_LINES || totalChars > MAX_OUTPUT_CHARS) {
                        output.append("... (output truncated)");
                        break;
                    }
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(EXECUTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("代码执行超时（超过30秒）");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("代码执行失败: " + output.toString().trim());
            }

            return output.toString().trim();

        } finally {
            // Clean up temp files
            if (tempFile != null) Files.deleteIfExists(tempFile);
            if (tempInputFile != null) Files.deleteIfExists(tempInputFile);
        }
    }

    /**
     * Sanitize code to prevent dangerous operations like file I/O, subprocess execution, etc.
     */
    private String sanitizeCode(String code) {
        if (code == null) return "";

        // Add safety restrictions at the top of the user code
        String safetyPrefix = ""
                + "import builtins\n"
                + "_original_open = builtins.open\n"
                + "_original_import = builtins.__import__\n"
                + "\n"
                + "# Disable dangerous imports and operations\n"
                + "def _safe_import(name, *args, **kwargs):\n"
                + "    blocked = {'os', 'subprocess', 'shutil', 'socket', 'ctypes', 'multiprocessing',\n"
                + "               'threading', 'signal', 'sys', 'http', 'urllib', 'requests',\n"
                + "               'pathlib', 'tempfile', 'pickle', 'shelve', 'dbm', 'sqlite3'}\n"
                + "    base = name.split('.')[0]\n"
                + "    if base in blocked:\n"
                + "        raise ImportError(f'Module \"{base}\" is not allowed in this environment')\n"
                + "    return _original_import(name, *args, **kwargs)\n"
                + "builtins.__import__ = _safe_import\n"
                + "\n"
                + "# Disable file writes\n"
                + "def _safe_open(file, mode='r', *args, **kwargs):\n"
                + "    if 'w' in mode or 'a' in mode or 'x' in mode or '+' in mode:\n"
                + "        raise PermissionError('File writing is not allowed in this environment')\n"
                + "    return _original_open(file, mode, *args, **kwargs)\n"
                + "builtins.open = _safe_open\n"
                + "\n";

        return safetyPrefix + code;
    }

    public Submission saveSubmission(Submission submission) {
        submissionMapper.insert(submission);
        return submission;
    }
}
