package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Tag;
import org.jxiot.tlxc.entity.TestCase;
import org.jxiot.tlxc.service.FavoriteService;
import org.jxiot.tlxc.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer tagId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> result = problemService.getProblemList(difficulty, status, true, keyword, tagId, page, pageSize);
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

    @GetMapping("/{id}/template")
    public ApiResponse<Map<String, String>> getCodeTemplate(@PathVariable Integer id) {
        Problem problem = problemService.getProblemById(id);
        Map<String, String> templates = new HashMap<>();
        templates.put("python", "def solve(input_str: str) -> str:\n    # Write your code here\n    return input_str");
        templates.put("java", "public static String solve(String input) {\n    // Write your code here\n    return input;\n}");
        templates.put("cpp", "std::string solve(std::string input) {\n    // Write your code here\n    return input;\n}");
        templates.put("javascript", "function solve(input) {\n    // Write your code here\n    return input;\n}");
        return ApiResponse.success(templates);
    }

    @GetMapping("/{id}/favorite-status")
    public ApiResponse<Map<String, Object>> getFavoriteStatus(@PathVariable Integer id,
                                                                HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.success(Map.of("favorited", false, "count", favoriteService.getFavoriteCount(id)));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favoriteService.isFavorited(userId, id));
        result.put("count", favoriteService.getFavoriteCount(id));
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<Problem> createProblem(@RequestBody Problem problem,
                                               @RequestAttribute Integer userId,
                                               @RequestAttribute String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error(403, "仅管理员可创建题目");
        }
        problem.setCreatedBy(userId);
        Problem created = problemService.createProblem(problem);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateProblem(@PathVariable Integer id,
                                           @RequestBody Problem problem,
                                           @RequestAttribute String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error(403, "仅管理员可修改题目");
        }
        problem.setId(id);
        problemService.updateProblem(problem);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProblem(@PathVariable Integer id,
                                           @RequestAttribute String role) {
        if (!"admin".equals(role)) {
            return ApiResponse.error(403, "仅管理员可删除题目");
        }
        problemService.deleteProblem(id);
        return ApiResponse.success();
    }
}
