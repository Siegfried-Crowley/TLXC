package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ApiResponse<List<Problem>> getProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status) {
        List<Problem> problems = problemService.getProblemList(difficulty, status, true);
        return ApiResponse.success(problems);
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
