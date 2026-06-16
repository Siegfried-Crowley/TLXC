package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("/following")
    public ApiResponse getFollowing(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(followService.getFollowing(userId));
    }

    @GetMapping("/followers")
    public ApiResponse getFollowers(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(followService.getFollowers(userId));
    }

    @GetMapping("/count/{userId}")
    public ApiResponse getCount(@PathVariable Integer userId) {
        java.util.Map<String, Object> counts = new java.util.HashMap<>();
        counts.put("following", followService.getFollowingCount(userId));
        counts.put("followers", followService.getFollowerCount(userId));
        return ApiResponse.success(counts);
    }

    @GetMapping("/check/{followingId}")
    public ApiResponse checkFollow(@PathVariable Integer followingId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(followService.isFollowing(userId, followingId));
    }

    @PostMapping("/{followingId}")
    public ApiResponse toggleFollow(@PathVariable Integer followingId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean wasFollowing = followService.isFollowing(userId, followingId);
        followService.toggleFollow(userId, followingId);
        return ApiResponse.success(!wasFollowing);
    }
}
