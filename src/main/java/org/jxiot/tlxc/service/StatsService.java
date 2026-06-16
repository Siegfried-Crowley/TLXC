package org.jxiot.tlxc.service;

import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatsService {

    @Autowired
    private SubmissionMapper submissionMapper;

    public List<Map<String, Object>> getSubmissionTrend(Integer userId, int days) {
        return submissionMapper.countByDay(userId, days);
    }

    public List<Map<String, Object>> getAcceptedByDifficulty(Integer userId) {
        return submissionMapper.countAcceptedByDifficulty(userId);
    }

    public Map<String, Object> getUserSummary(Integer userId) {
        Map<String, Object> summary = new HashMap<>();
        long totalSubmissions = submissionMapper.countByUserId(userId);
        int acceptedProblems = (int) submissionMapper.countByUserId(userId); // approximate
        summary.put("totalSubmissions", totalSubmissions);
        summary.put("acceptedProblems", acceptedProblems);
        return summary;
    }
}
