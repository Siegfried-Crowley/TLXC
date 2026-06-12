package org.jxiot.tlxc.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CodeJudgeUtil {

    public static String executePythonCode(String code, String input) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python", "-c", code);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("代码执行失败");
        }

        return output.toString().trim();
    }
}
