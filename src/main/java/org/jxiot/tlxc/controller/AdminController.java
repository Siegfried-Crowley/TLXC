package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.User;
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

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("totalProblems", problemService.getProblemCount());
        stats.put("totalSubmissions", submissionMapper.count());
        stats.put("todayActive", submissionMapper.countTodayActiveUsers());
        return ApiResponse.success(stats);
    }

    @GetMapping("/problems")
    public ApiResponse<List<Problem>> getAllProblems() {
        List<Problem> problems = problemService.getProblemList(null, null, null);
        return ApiResponse.success(problems);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getTopUsers(1000);
        return ApiResponse.success(users);
    }

    @PostMapping("/problems")
    public ApiResponse<Problem> createProblem(@RequestBody Problem problem, @RequestAttribute Integer userId) {
        problem.setCreatedBy(userId);
        Problem created = problemService.createProblem(problem);
        return ApiResponse.success(created);
    }

    @PutMapping("/problems/{id}")
    public ApiResponse<Void> updateProblem(@PathVariable Integer id, @RequestBody Problem problem) {
        problem.setId(id);
        problemService.updateProblem(problem);
        return ApiResponse.success();
    }

    @DeleteMapping("/problems/{id}")
    public ApiResponse<Void> deleteProblem(@PathVariable Integer id) {
        problemService.deleteProblem(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<Void> updateUserRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        userService.updateUserRole(id, role);
        return ApiResponse.success();
    }
}
