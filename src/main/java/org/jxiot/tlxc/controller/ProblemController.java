package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Tag;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> result = problemService.getProblemList(difficulty, status, true, keyword, page, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/tags")
    public ApiResponse<List<Tag>> getAllTags() {
        return ApiResponse.success(problemService.getAllTags());
    }

    @GetMapping("/{id}")
    public ApiResponse<Problem> getProblem(@PathVariable Integer id) {
        Problem problem = problemService.getProblemById(id);
        return ApiResponse.success(problem);
    }

    @GetMapping("/{id}/testcases")
    public ApiResponse<List<TestCase>> getTestCases(@PathVariable Integer id) {
        List<TestCase> testCases = problemService.getTestCases(id);
        return ApiResponse.success(testCases);
    }

    @PostMapping
    public ApiResponse<Problem> createProblem(@RequestBody Problem problem, @RequestAttribute Integer userId) {
        problem.setCreatedBy(userId);
        Problem created = problemService.createProblem(problem);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateProblem(@PathVariable Integer id, @RequestBody Problem problem) {
        problem.setId(id);
        problemService.updateProblem(problem);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProblem(@PathVariable Integer id) {
        problemService.deleteProblem(id);
        return ApiResponse.success();
    }
}
