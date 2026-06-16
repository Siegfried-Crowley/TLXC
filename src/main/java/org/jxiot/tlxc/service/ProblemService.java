package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Tag;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.mapper.ProblemMapper;
import org.jxiot.tlxc.mapper.TagMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    public Map<String, Object> getProblemList(String difficulty, String status, Boolean isActive,
                                               String keyword, Integer tagId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Problem> list;
        long total;
        if (status == null && isActive == null) {
            // Use tag-filtered search for published problems
            list = problemMapper.findPublishedWithFilter(difficulty, tagId, keyword, offset, pageSize);
            total = problemMapper.countPublishedWithFilter(difficulty, tagId, keyword);
        } else {
            list = problemMapper.findByCondition(difficulty, status, isActive, keyword, offset, pageSize);
            total = problemMapper.countByCondition(difficulty, status, isActive, keyword);
        }
        // Attach tags to each problem
        for (Problem p : list) {
            p.setTagList(tagMapper.findByProblemId(p.getId()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    // Overloaded method for backward compatibility
    public Map<String, Object> getProblemList(String difficulty, String status, Boolean isActive,
                                               String keyword, int page, int pageSize) {
        return getProblemList(difficulty, status, isActive, keyword, null, page, pageSize);
    }

    public Problem getProblemById(Integer id) {
        Problem problem = problemMapper.findById(id);
        if (problem != null) {
            problem.setTagList(tagMapper.findByProblemId(id));
        }
        return problem;
    }

    public List<Tag> getAllTags() {
        return tagMapper.findAll();
    }

    public List<TestCase> getTestCases(Integer problemId) {
        return testCaseMapper.findByProblemId(problemId);
    }

    @Transactional
    public Problem createProblem(Problem problem) {
        problemMapper.insert(problem);
        // Handle tags
        if (problem.getTagIds() != null) {
            for (Integer tagId : problem.getTagIds()) {
                tagMapper.addProblemTag(problem.getId(), tagId);
            }
        }
        return problem;
    }

    @Transactional
    public void updateProblem(Problem problem) {
        problemMapper.update(problem);
        if (problem.getTagIds() != null) {
            tagMapper.removeAllProblemTags(problem.getId());
            for (Integer tagId : problem.getTagIds()) {
                tagMapper.addProblemTag(problem.getId(), tagId);
            }
        }
    }

    @Transactional
    public void updateProblemAdmin(Problem problem) {
        problemMapper.updateAdmin(problem);
        if (problem.getTagIds() != null) {
            tagMapper.removeAllProblemTags(problem.getId());
            for (Integer tagId : problem.getTagIds()) {
                tagMapper.addProblemTag(problem.getId(), tagId);
            }
        }
    }

    @Transactional
    public void deleteProblem(Integer id) {
        problemMapper.deleteById(id);
    }

    public int countProblems() {
        return problemMapper.count();
    }

    public List<Problem> getAcceptedProblems(Integer userId) {
        return problemMapper.findAcceptedByUserId(userId);
    }
}
