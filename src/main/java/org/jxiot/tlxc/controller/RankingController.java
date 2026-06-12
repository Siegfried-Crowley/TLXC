package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin
public class RankingController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getRankings(@RequestParam(defaultValue = "100") int limit) {
        List<User> users = userService.getTopUsers(limit);
        return ApiResponse.success(users);
    }
}
