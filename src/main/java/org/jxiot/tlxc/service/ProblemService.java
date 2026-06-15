package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Tag;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.ProblemMapper;
import org.jxiot.tlxc.mapper.TagMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TagMapper tagMapper;

    public Problem getProblemById(Integer id) {
        Problem problem = problemMapper.findById(id);
        if (problem != null) {
            problem.setTagList(tagMapper.findByProblemId(id));
        }
        return problem;
    }

    public Map<String, Object> getProblemList(String difficulty, String status, Boolean isActive, String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Problem> problems = problemMapper.findByCondition(difficulty, status, isActive, keyword, offset, pageSize);
        long total = problemMapper.countByCondition(difficulty, status, isActive, keyword);

        // Load tags for each problem
        for (Problem p : problems) {
            p.setTagList(tagMapper.findByProblemId(p.getId()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", problems);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    public Problem getProblemWithDetails(Integer id) {
        return getProblemById(id);
    }

    public List<TestCase> getTestCases(Integer problemId) {
        return testCaseMapper.findVisibleByProblemId(problemId);
    }

    public List<Tag> getAllTags() {
        return tagMapper.findAll();
    }

    public Problem createProblem(Problem problem) {
        problem.setCreatedAt(new Date());
        problem.setUpdatedAt(new Date());
        problem.setIsActive(true);
        problemMapper.insert(problem);
        saveTags(problem);
        return problem;
    }

    public void updateProblem(Problem problem) {
        problem.setUpdatedAt(new Date());
        problemMapper.update(problem);
        saveTags(problem);
    }

    public void updateProblemAdmin(Problem problem) {
        problem.setUpdatedAt(new Date());
        problemMapper.updateAdmin(problem);
        saveTags(problem);
    }

    private void saveTags(Problem problem) {
        if (problem.getTagIds() != null && !problem.getTagIds().isEmpty()) {
            tagMapper.removeAllProblemTags(problem.getId());
            for (Integer tagId : problem.getTagIds()) {
                tagMapper.addProblemTag(problem.getId(), tagId);
            }
        }
    }

    public void deleteProblem(Integer id) {
        tagMapper.removeAllProblemTags(id);
        problemMapper.deleteById(id);
        testCaseMapper.deleteByProblemId(id);
    }

    public int getProblemCount() {
        return problemMapper.count();
    }
}
