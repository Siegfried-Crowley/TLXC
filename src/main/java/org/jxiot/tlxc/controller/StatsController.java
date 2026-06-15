package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.PointLog;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.service.PointsService;
import org.jxiot.tlxc.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PointsService pointsService;

    @GetMapping("/points/logs")
    public ApiResponse<List<PointLog>> getPointLogs(@RequestAttribute Integer userId) {
        List<PointLog> logs = pointsService.getUserPointLogs(userId);
        return ApiResponse.success(logs);
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview(@RequestAttribute Integer userId) {
        Map<String, Object> subResult = submissionService.getUserSubmissions(userId, 1, 10000);
        List<Submission> submissions = (List<Submission>) subResult.get("list");
        List<PointLog> pointLogs = pointsService.getUserPointLogs(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSubmissions", submissions.size());
        stats.put("totalPoints", pointLogs.stream().mapToInt(PointLog::getPoints).sum());
        stats.put("acceptedCount", submissions.stream().filter(s -> "accepted".equals(s.getStatus())).count());

        return ApiResponse.success(stats);
    }
}
