package org.jxiot.tlxc.service;

import org.jxiot.tlxc.dto.JudgeResult;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public Submission submitCode(Integer userId, Integer problemId, String code) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setProblemId(problemId);
        submission.setCode(code);

        JudgeResult result = judgeService.judgeCode(problemId, code);

        submission.setStatus(result.getStatus());
        submission.setRuntimeMs(result.getRuntimeMs());
        submission.setPassedCases(result.getPassedCases());
        submission.setTotalCases(result.getTotalCases());
        submission.setErrorMessage(result.getErrorMessage());
        submission.setScoreDelta(0);

        // 先保存提交记录，获得自增 ID
        submissionMapper.insert(submission);

        if ("accepted".equals(result.getStatus())) {
            int acceptedCount = submissionMapper.countAcceptedByUserAndProblem(userId, problemId);

            if (acceptedCount == 1) { // 包含刚插入的这次，所以 ==1 表示首次通过
                int points = calculatePoints(problemId);
                submission.setScoreDelta(points);
                pointsService.addPoints(userId, problemId, points, "首次通过题目");
                // 更新 score_delta
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

    public List<Submission> getUserSubmissions(Integer userId, int limit) {
        return submissionMapper.findByUserId(userId, limit);
    }

    public List<Submission> getProblemSubmissions(Integer problemId) {
        return submissionMapper.findByProblemId(problemId);
    }

    public Submission getSubmissionById(Integer id) {
        return submissionMapper.findById(id);
    }
}
