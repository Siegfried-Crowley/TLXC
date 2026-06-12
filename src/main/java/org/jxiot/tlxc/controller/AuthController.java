package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.dto.LoginRequest;
import org.jxiot.tlxc.dto.RegisterRequest;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request.getUsername(), request.getPassword());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.getUsername(), request.getPassword(), request.getNickname());
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(@RequestAttribute Integer userId) {
        User user = userService.getUserById(userId);
        return ApiResponse.success(user);
    }
}
