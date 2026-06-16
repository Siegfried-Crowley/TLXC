package org.jxiot.tlxc.service;

import org.jxiot.tlxc.dto.JudgeResult;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.mapper.SubmissionDetailMapper;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private WrongBookService wrongBookService;

    @Autowired
    private SubmissionDetailMapper submissionDetailMapper;

    @Transactional
    public Submission submitCode(Integer userId, Integer problemId, String code, String language) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setProblemId(problemId);
        submission.setCode(code);
        submission.setLanguage(language != null ? language : "python");

        JudgeResult result = judgeService.judgeCode(problemId, code, language);

        submission.setStatus(result.getStatus());
        submission.setRuntimeMs(result.getRuntimeMs());
        submission.setPassedCases(result.getPassedCases());
        submission.setTotalCases(result.getTotalCases());
        submission.setErrorMessage(result.getErrorMessage());
        submission.setScoreDelta(0);

        // 先保存提交记录，获得自增 ID
        submissionMapper.insert(submission);

        // 保存每个测试用例的详细结果
        if (result.getCaseResults() != null && !result.getCaseResults().isEmpty()) {
            judgeService.saveSubmissionDetails(submission.getId(), result.getCaseResults());
        }

        if ("accepted".equals(result.getStatus())) {
            int acceptedCount = submissionMapper.countAcceptedByUserAndProblem(userId, problemId);

            if (acceptedCount == 1) {
                int points = calculatePoints(problemId);
                submission.setScoreDelta(points);
                pointsService.addPoints(userId, problemId, points, "首次通过题目");
                submissionMapper.updateScoreDelta(submission.getId(), points);
            }
        } else {
            wrongBookService.addToWrongBook(userId, problemId, submission.getId());
        }

        return submission;
    }

    private int calculatePoints(Integer problemId) {
        return 10;
    }

    public Map<String, Object> getUserSubmissions(Integer userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Submission> list = submissionMapper.findByUserId(userId, offset, pageSize);
        long total = submissionMapper.countByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    public List<Submission> getProblemSubmissions(Integer problemId) {
        return submissionMapper.findByProblemId(problemId);
    }

    public Submission getSubmissionById(Integer id) {
        Submission s = submissionMapper.findById(id);
        if (s != null) {
            s.setDetails(submissionDetailMapper.findBySubmissionId(id));
        }
        return s;
    }

    public List<Map<String, Object>> getUserSubmissionTrend(Integer userId, int days) {
        return submissionMapper.countByDay(userId, days);
    }

    public List<Map<String, Object>> getUserAcceptedByDifficulty(Integer userId) {
        return submissionMapper.countAcceptedByDifficulty(userId);
    }

    public List<Submission> getUserProblemHistory(Integer userId, Integer problemId) {
        return submissionMapper.findByUserAndProblem(userId, problemId);
    }
}
