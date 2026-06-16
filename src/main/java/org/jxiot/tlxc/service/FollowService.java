package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.UserFollow;
import org.jxiot.tlxc.mapper.UserFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {

    @Autowired
    private UserFollowMapper followMapper;

    public List<UserFollow> getFollowing(Integer userId) {
        return followMapper.findFollowing(userId);
    }

    public List<UserFollow> getFollowers(Integer userId) {
        return followMapper.findFollowers(userId);
    }

    public int getFollowingCount(Integer userId) {
        return followMapper.countFollowing(userId);
    }

    public int getFollowerCount(Integer userId) {
        return followMapper.countFollowers(userId);
    }

    public boolean isFollowing(Integer followerId, Integer followingId) {
        return followMapper.isFollowing(followerId, followingId) > 0;
    }

    @Transactional
    public void follow(Integer followerId, Integer followingId) {
        if (!isFollowing(followerId, followingId)) {
            followMapper.insert(followerId, followingId);
        }
    }

    @Transactional
    public void unfollow(Integer followerId, Integer followingId) {
        followMapper.delete(followerId, followingId);
    }

    @Transactional
    public void toggleFollow(Integer followerId, Integer followingId) {
        if (isFollowing(followerId, followingId)) {
            unfollow(followerId, followingId);
        } else {
            follow(followerId, followingId);
        }
    }
}
