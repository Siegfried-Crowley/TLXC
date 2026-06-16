package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.AuditLog;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.exception.BusinessException;
import org.jxiot.tlxc.mapper.AuditLogMapper;
import org.jxiot.tlxc.mapper.SubmissionMapper;
import org.jxiot.tlxc.mapper.TestCaseMapper;
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

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    private void checkAdmin(String role) {
        if (role == null || !"admin".equals(role)) {
            throw new BusinessException(403, "无权访问管理后台");
        }
    }

    private void logAction(Integer adminId, String action, String targetType, Integer targetId, String detail) {
        AuditLog log = new AuditLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        auditLogMapper.insert(log);
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard(@RequestAttribute String role) {
        checkAdmin(role);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("totalProblems", problemService.countProblems());
        stats.put("totalSubmissions", submissionMapper.count());
        stats.put("todayActive", submissionMapper.countTodayActiveUsers());
        return ApiResponse.success(stats);
    }

    @GetMapping("/audit-logs")
    public ApiResponse<List<AuditLog>> getAuditLogs(@RequestAttribute String role) {
        checkAdmin(role);
        return ApiResponse.success(auditLogMapper.findRecent(100));
    }

    @GetMapping("/problems")
    public ApiResponse<List<Problem>> getAllProblems(@RequestAttribute String role) {
        checkAdmin(role);
        Map<String, Object> result = problemService.getProblemList(null, null, null, null, null, 1, 10000);
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
        logAction(userId, "create", "problem", created.getId(), "创建题目: " + created.getTitle());
        return ApiResponse.success(created);
    }

    @PostMapping("/problems/batch-import")
    public ApiResponse<Map<String, Object>> batchImportProblems(@RequestBody List<Problem> problems,
                                                                  @RequestAttribute Integer userId,
                                                                  @RequestAttribute String role) {
        checkAdmin(role);
        int success = 0;
        int failed = 0;
        for (Problem p : problems) {
            try {
                p.setCreatedBy(userId);
                p.setStatus("published");
                problemService.createProblem(p);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        logAction(userId, "batch_import", "problem", null, "批量导入题目: 成功" + success + " 失败" + failed);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("failed", failed);
        return ApiResponse.success(result);
    }

    @PutMapping("/problems/{id}")
    public ApiResponse<Void> updateProblem(@PathVariable Integer id,
                                           @RequestBody Problem problem,
                                           @RequestAttribute Integer userId,
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
        logAction(userId, "update", "problem", id, "更新题目: " + problem.getTitle());
        return ApiResponse.success();
    }

    @DeleteMapping("/problems/{id}")
    public ApiResponse<Void> deleteProblem(@PathVariable Integer id,
                                           @RequestAttribute Integer userId,
                                           @RequestAttribute String role) {
        checkAdmin(role);
        problemService.deleteProblem(id);
        logAction(userId, "delete", "problem", id, "删除题目 #" + id);
        return ApiResponse.success();
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id,
                                        @RequestAttribute Integer userId,
                                        @RequestAttribute String role) {
        checkAdmin(role);
        userService.deleteUser(id);
        logAction(userId, "delete", "user", id, "删除用户 #" + id);
        return ApiResponse.success();
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<Void> updateUserRole(@PathVariable Integer id,
                                            @RequestBody Map<String, String> body,
                                            @RequestAttribute Integer userId,
                                            @RequestAttribute String role) {
        checkAdmin(role);
        String newRole = body.get("role");
        userService.updateUserRole(id, newRole);
        logAction(userId, "update_role", "user", id, "更改用户角色为: " + newRole);
        return ApiResponse.success();
    }

    // Test case management
    @GetMapping("/problems/{id}/testcases")
    public ApiResponse<List<TestCase>> getTestCases(@PathVariable Integer id, @RequestAttribute String role) {
        checkAdmin(role);
        return ApiResponse.success(problemService.getTestCases(id));
    }

    @PostMapping("/problems/{id}/testcases")
    public ApiResponse<TestCase> addTestCase(@PathVariable Integer id,
                                              @RequestBody TestCase testCase,
                                              @RequestAttribute String role) {
        checkAdmin(role);
        testCase.setProblemId(id);
        testCaseMapper.insert(testCase);
        return ApiResponse.success(testCase);
    }

    @DeleteMapping("/testcases/{caseId}")
    public ApiResponse<Void> deleteTestCase(@PathVariable Integer caseId, @RequestAttribute String role) {
        checkAdmin(role);
        testCaseMapper.deleteById(caseId);
        return ApiResponse.success();
    }
}
