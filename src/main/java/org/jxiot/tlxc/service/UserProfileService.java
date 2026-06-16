package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.entity.UserProfile;
import org.jxiot.tlxc.mapper.UserMapper;
import org.jxiot.tlxc.mapper.UserProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileMapper profileMapper;

    @Autowired
    private UserMapper userMapper;

    public UserProfile getProfile(Integer userId) {
        UserProfile profile = profileMapper.findByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setBio("");
            profile.setAvatarUrl("");
            profile.setGithubUrl("");
            profile.setWebsiteUrl("");
            profile.setOrganization("");
            profile.setLocation("");
            profile.setAcceptedProblems(0);
            profile.setTotalSubmissions(0);
            profile.setAcceptanceRate(java.math.BigDecimal.ZERO);
            profileMapper.insert(profile);
        }
        return profile;
    }

    @Transactional
    public void updateProfile(UserProfile profile) {
        UserProfile existing = profileMapper.findByUserId(profile.getUserId());
        if (existing != null) {
            profileMapper.update(profile);
        } else {
            profileMapper.insert(profile);
        }
    }

    @Transactional
    public void refreshStats(Integer userId) {
        profileMapper.refreshStats(userId);
    }

    public User getUserInfo(Integer userId) {
        return userMapper.findById(userId);
    }
}
