package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.entity.UserProfile;
import org.jxiot.tlxc.service.FollowService;
import org.jxiot.tlxc.service.UserProfileService;
import org.jxiot.tlxc.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private FollowService followService;

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/{userId}")
    public ApiResponse getProfile(@PathVariable Integer userId) {
        Map<String, Object> data = new HashMap<>();
        User user = userProfileService.getUserInfo(userId);
        UserProfile profile = userProfileService.getProfile(userId);
        data.put("user", user);
        data.put("profile", profile);
        data.put("followingCount", followService.getFollowingCount(userId));
        data.put("followerCount", followService.getFollowerCount(userId));
        return ApiResponse.success(data);
    }

    @GetMapping("/me")
    public ApiResponse getMyProfile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return getProfile(userId);
    }

    @PutMapping
    public ApiResponse updateProfile(@RequestBody UserProfile profile, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        profile.setUserId(userId);
        userProfileService.updateProfile(profile);
        return ApiResponse.success(null);
    }

    @GetMapping("/stats/{userId}")
    public ApiResponse getUserStats(@PathVariable Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("submissionTrend", submissionService.getUserSubmissionTrend(userId, 30));
        stats.put("acceptedByDifficulty", submissionService.getUserAcceptedByDifficulty(userId));
        return ApiResponse.success(stats);
    }
}
