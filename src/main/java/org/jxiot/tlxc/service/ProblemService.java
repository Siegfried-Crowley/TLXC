package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.ProblemMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    public Problem getProblemById(Integer id) {
        return problemMapper.findById(id);
    }

    public List<Problem> getProblemList(String difficulty, String status, Boolean isActive) {
        return problemMapper.findByCondition(difficulty, status, isActive);
    }

    public Problem getProblemWithDetails(Integer id) {
        Problem problem = problemMapper.findById(id);
        return problem;
    }

    public List<TestCase> getTestCases(Integer problemId) {
        return testCaseMapper.findVisibleByProblemId(problemId);
    }

    public Problem createProblem(Problem problem) {
        problem.setCreatedAt(new Date());
        problem.setUpdatedAt(new Date());
        problem.setIsActive(true);
        problemMapper.insert(problem);
        return problem;
    }

    public void updateProblem(Problem problem) {
        problem.setUpdatedAt(new Date());
        problemMapper.update(problem);
    }

    public void deleteProblem(Integer id) {
        problemMapper.deleteById(id);
        testCaseMapper.deleteByProblemId(id);
    }

    public int getProblemCount() {
        return problemMapper.count();
    }
}
