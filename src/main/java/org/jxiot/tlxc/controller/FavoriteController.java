package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ApiResponse getFavorites(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(favoriteService.getUserFavorites(userId));
    }

    @GetMapping("/check/{problemId}")
    public ApiResponse checkFavorite(@PathVariable Integer problemId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return ApiResponse.success(favoriteService.isFavorited(userId, problemId));
    }

    @GetMapping("/count/{problemId}")
    public ApiResponse getFavoriteCount(@PathVariable Integer problemId) {
        return ApiResponse.success(favoriteService.getFavoriteCount(problemId));
    }

    @PostMapping("/{problemId}")
    public ApiResponse toggleFavorite(@PathVariable Integer problemId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean isFav = favoriteService.isFavorited(userId, problemId);
        favoriteService.toggleFavorite(userId, problemId);
        return ApiResponse.success(!isFav);
    }
}
