package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.Comment;
import org.jxiot.tlxc.entity.Discussion;
import org.jxiot.tlxc.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/problem/{problemId}")
    public ApiResponse getByProblem(@PathVariable Integer problemId,
                                     @RequestParam(defaultValue = "discussion") String type) {
        List<Discussion> list = discussionService.getDiscussionsByProblem(problemId, type);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        Discussion d = discussionService.getDiscussionById(id);
        return d != null ? ApiResponse.success(d) : ApiResponse.error(404, "讨论不存在");
    }

    @PostMapping
    public ApiResponse create(@RequestBody Discussion discussion, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        discussion.setUserId(userId);
        discussionService.createDiscussion(discussion);
        return ApiResponse.success(discussion);
    }

    @PostMapping("/{id}/comment")
    public ApiResponse addComment(@PathVariable Integer id, @RequestBody Comment comment,
                                   HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        comment.setDiscussionId(id);
        comment.setUserId(userId);
        discussionService.addComment(comment);
        return ApiResponse.success(comment);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteDiscussion(@PathVariable Integer id) {
        discussionService.deleteDiscussion(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/comment/{commentId}")
    public ApiResponse deleteComment(@PathVariable Integer commentId) {
        discussionService.deleteComment(commentId);
        return ApiResponse.success(null);
    }

    @GetMapping("/solutions/recent")
    public ApiResponse getRecentSolutions(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(discussionService.getRecentSolutions(limit));
    }
}
