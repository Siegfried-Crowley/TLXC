package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.exception.BusinessException;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.jxiot.tlxc.service.ProblemService;
import org.jxiot.tlxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionMapper submissionMapper;

    private void checkAdmin(String role) {
        if (role == null || !"admin".equals(role)) {
            throw new BusinessException(403, "无权访问管理后台");
        }
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard(@RequestAttribute String role) {
        checkAdmin(role);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("totalProblems", problemService.getProblemCount());
        stats.put("totalSubmissions", submissionMapper.count());
        stats.put("todayActive", submissionMapper.countTodayActiveUsers());
        return ApiResponse.success(stats);
    }

    @GetMapping("/problems")
    public ApiResponse<List<Problem>> getAllProblems(@RequestAttribute String role) {
        checkAdmin(role);
        Map<String, Object> result = problemService.getProblemList(null, null, null, null, 1, 10000);
        List<Problem> problems = (List<Problem>) result.get("list");
        return ApiResponse.success(problems);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers(@RequestAttribute String role) {
        checkAdmin(role);
        List<User> users = userService.getTopUsers(1000);
        return ApiResponse.success(users);
    }

    @PostMapping("/problems")
    public ApiResponse<Problem> createProblem(@RequestBody Problem problem,
                                              @RequestAttribute Integer userId,
                                              @RequestAttribute String role) {
        checkAdmin(role);
        problem.setCreatedBy(userId);
        Problem created = problemService.createProblem(problem);
        return ApiResponse.success(created);
    }

    @PutMapping("/problems/{id}")
    public ApiResponse<Void> updateProblem(@PathVariable Integer id,
                                           @RequestBody Problem problem,
                                           @RequestAttribute String role) {
        checkAdmin(role);
        Problem existing = problemService.getProblemById(id);
        if (existing == null) {
            return ApiResponse.error(404, "题目不存在");
        }
        if (problem.getTitle() == null) problem.setTitle(existing.getTitle());
        if (problem.getDifficulty() == null) problem.setDifficulty(existing.getDifficulty());
        if (problem.getTags() == null) problem.setTags(existing.getTags());
        if (problem.getDescription() == null) problem.setDescription(existing.getDescription());
        if (problem.getInputDescription() == null) problem.setInputDescription(existing.getInputDescription());
        if (problem.getOutputDescription() == null) problem.setOutputDescription(existing.getOutputDescription());
        if (problem.getExamples() == null) problem.setExamples(existing.getExamples());
        if (problem.getStarterCode() == null) problem.setStarterCode(existing.getStarterCode());
        if (problem.getStatus() == null) problem.setStatus(existing.getStatus());
        problem.setId(id);
        problemService.updateProblemAdmin(problem);
        return ApiResponse.success();
    }

    @DeleteMapping("/problems/{id}")
    public ApiResponse<Void> deleteProblem(@PathVariable Integer id, @RequestAttribute String role) {
        checkAdmin(role);
        problemService.deleteProblem(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id, @RequestAttribute String role) {
        checkAdmin(role);
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<Void> updateUserRole(@PathVariable Integer id,
                                            @RequestBody Map<String, String> body,
                                            @RequestAttribute String role) {
        checkAdmin(role);
        String newRole = body.get("role");
        userService.updateUserRole(id, newRole);
        return ApiResponse.success();
    }
}
