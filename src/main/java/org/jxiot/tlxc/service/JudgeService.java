package org.jxiot.tlxc.service;

import org.jxiot.tlxc.dto.JudgeResult;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {

    private static final long EXECUTION_TIMEOUT_MS = 30_000;
    private static final int MAX_OUTPUT_LINES = 1000;
    private static final int MAX_OUTPUT_CHARS = 100_000;

    @Value("${judge.java.compiler:javac}")
    private String javaCompiler;

    @Value("${judge.cpp.compiler:g++}")
    private String cppCompiler;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    public JudgeResult judgeCode(Integer problemId, String code, String language) {
        List<TestCase> testCases = testCaseMapper.findByProblemId(problemId);

        int passedCases = 0;
        int totalCases = testCases.size();
        int maxRuntime = 0;
        String errorMessage = "";
        String status = "accepted";

        for (TestCase testCase : testCases) {
            try {
                long startTime = System.currentTimeMillis();

                String result = executeCode(code, language, testCase.getInputData());

                long endTime = System.currentTimeMillis();
                int runtime = (int) (endTime - startTime);
                maxRuntime = Math.max(maxRuntime, runtime);

                if (result.trim().equals(testCase.getExpectedOutput().trim())) {
                    passedCases++;
                } else {
                    status = "wrong_answer";
                    errorMessage = "输出不匹配 - 预期: " + testCase.getExpectedOutput().trim() + ", 实际: " + result.trim();
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

    private String executeCode(String code, String language, String inputData) throws Exception {
        return switch (language == null ? "python" : language.toLowerCase()) {
            case "java" -> executeJava(code, inputData);
            case "cpp", "c++" -> executeCpp(code, inputData);
            case "javascript", "js" -> executeJavaScript(code, inputData);
            default -> executePython(code, inputData);
        };
    }

    private String executePython(String code, String inputData) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("user_code_", ".py");
            Files.writeString(tempFile, sanitizePythonCode(code), StandardCharsets.UTF_8);

            ProcessBuilder pb = new ProcessBuilder("python", tempFile.toAbsolutePath().toString());
            return runProcess(pb, inputData);
        } finally {
            if (tempFile != null) Files.deleteIfExists(tempFile);
        }
    }

    private String executeJava(String code, String inputData) throws Exception {
        Path tempDir = Files.createTempDirectory("javacode_");
        Path sourceFile = tempDir.resolve("Main.java");
        try {
            // Wrap user code in a Main class with solve method
            String wrappedCode = "import java.util.*;\nimport java.math.*;\n\npublic class Main {\n"
                    + code + "\n"
                    + "    public static void main(String[] args) {\n"
                    + "        Scanner sc = new Scanner(System.in);\n"
                    + "        String input = sc.useDelimiter(\"\\\\Z\").next();\n"
                    + "        System.out.println(solve(input));\n"
                    + "    }\n"
                    + "}\n";
            Files.writeString(sourceFile, wrappedCode, StandardCharsets.UTF_8);

            // Compile
            ProcessBuilder compilePb = new ProcessBuilder(javaCompiler, sourceFile.toAbsolutePath().toString());
            Process compileProcess = compilePb.start();
            boolean compiled = compileProcess.waitFor(15, TimeUnit.SECONDS);
            if (!compiled) {
                compileProcess.destroyForcibly();
                throw new RuntimeException("Java 编译超时");
            }
            if (compileProcess.exitValue() != 0) {
                String err = readStream(compileProcess.getErrorStream());
                throw new RuntimeException("Java 编译错误:\n" + err);
            }

            // Run
            ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempDir.toAbsolutePath().toString(), "Main");
            return runProcess(runPb, inputData);
        } finally {
            deleteDirectory(tempDir);
        }
    }

    private String executeCpp(String code, String inputData) throws Exception {
        Path tempDir = Files.createTempDirectory("cppcode_");
        Path sourceFile = tempDir.resolve("main.cpp");
        Path outputFile = tempDir.resolve("main.exe");
        try {
            String wrappedCode = "#include <iostream>\n#include <string>\n#include <vector>\n#include <sstream>\nusing namespace std;\n\n"
                    + code + "\n\n"
                    + "int main() {\n"
                    + "    string input, line;\n"
                    + "    while (getline(cin, line)) input += line + \"\\n\";\n"
                    + "    cout << solve(input);\n"
                    + "    return 0;\n"
                    + "}\n";
            Files.writeString(sourceFile, wrappedCode, StandardCharsets.UTF_8);

            ProcessBuilder compilePb = new ProcessBuilder(cppCompiler, "-o", outputFile.toString(), sourceFile.toAbsolutePath().toString());
            Process compileProcess = compilePb.start();
            boolean compiled = compileProcess.waitFor(15, TimeUnit.SECONDS);
            if (!compiled) {
                compileProcess.destroyForcibly();
                throw new RuntimeException("C++ 编译超时");
            }
            if (compileProcess.exitValue() != 0) {
                String err = readStream(compileProcess.getErrorStream());
                throw new RuntimeException("C++ 编译错误:\n" + err);
            }

            ProcessBuilder runPb = new ProcessBuilder(outputFile.toAbsolutePath().toString());
            return runProcess(runPb, inputData);
        } finally {
            deleteDirectory(tempDir);
        }
    }

    private String executeJavaScript(String code, String inputData) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("user_code_", ".js");
            String wrappedCode = "const readline = require('readline');\n"
                    + "const rl = readline.createInterface({ input: process.stdin });\n"
                    + "let input = '';\n"
                    + "rl.on('line', (line) => { input += line + '\\n'; });\n"
                    + "rl.on('close', () => {\n"
                    + code + "\n"
                    + "    console.log(solve(input.trim()));\n"
                    + "});\n";
            Files.writeString(tempFile, wrappedCode, StandardCharsets.UTF_8);

            ProcessBuilder pb = new ProcessBuilder("node", tempFile.toAbsolutePath().toString());
            return runProcess(pb, inputData);
        } finally {
            if (tempFile != null) Files.deleteIfExists(tempFile);
        }
    }

    private String runProcess(ProcessBuilder pb, String inputData) throws Exception {
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Feed input
        try (OutputStream os = process.getOutputStream()) {
            os.write(inputData.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        String output = readStream(process.getInputStream());

        boolean finished = process.waitFor(EXECUTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("代码执行超时（超过30秒）");
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("代码执行失败: " + output.trim());
        }

        return output.trim();
    }

    private String readStream(InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
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
        return output.toString();
    }

    private String sanitizePythonCode(String code) {
        if (code == null) return "";
        String safetyPrefix = ""
                + "import builtins\n"
                + "_original_open = builtins.open\n"
                + "_original_import = builtins.__import__\n"
                + "\n"
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
                + "def _safe_open(file, mode='r', *args, **kwargs):\n"
                + "    if 'w' in mode or 'a' in mode or 'x' in mode or '+' in mode:\n"
                + "        raise PermissionError('File writing is not allowed in this environment')\n"
                + "    return _original_open(file, mode, *args, **kwargs)\n"
                + "builtins.open = _safe_open\n"
                + "\n";
        return safetyPrefix + code;
    }

    private void deleteDirectory(Path dir) {
        try {
            Files.walk(dir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }

    public Submission saveSubmission(Submission submission) {
        submissionMapper.insert(submission);
        return submission;
    }
}
