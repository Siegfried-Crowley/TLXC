package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.UserFavorite;
import org.jxiot.tlxc.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private UserFavoriteMapper favoriteMapper;

    public List<UserFavorite> getUserFavorites(Integer userId) {
        return favoriteMapper.findByUserId(userId);
    }

    public boolean isFavorited(Integer userId, Integer problemId) {
        return favoriteMapper.countByUserAndProblem(userId, problemId) > 0;
    }

    public int getFavoriteCount(Integer problemId) {
        return favoriteMapper.countByProblemId(problemId);
    }

    @Transactional
    public void toggleFavorite(Integer userId, Integer problemId) {
        if (isFavorited(userId, problemId)) {
            favoriteMapper.delete(userId, problemId);
        } else {
            favoriteMapper.insert(userId, problemId);
        }
    }

    @Transactional
    public void addFavorite(Integer userId, Integer problemId) {
        if (!isFavorited(userId, problemId)) {
            favoriteMapper.insert(userId, problemId);
        }
    }

    @Transactional
    public void removeFavorite(Integer userId, Integer problemId) {
        favoriteMapper.delete(userId, problemId);
    }
}
