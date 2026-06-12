package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.service.ProblemService;
import org.jxiot.tlxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
