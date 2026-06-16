package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.mapper.*;
import org.jxiot.tlxc.util.JwtUtil;
import org.jxiot.tlxc.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussionMapper discussionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private PointLogMapper pointLogMapper;

    @Autowired
    private WrongBookMapper wrongBookMapper;

    @Autowired
    private ContestParticipationMapper contestParticipationMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !PasswordUtil.verify(password, user.getHashedPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setLastLoginAt(new Date());
        userMapper.update(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Transactional
    public User register(String username, String password, String nickname) {
        User existing = userMapper.findByUsername(username);
        if (existing != null) {
            throw new RuntimeException("用户名已被使用");
        }
        User user = new User();
        user.setUsername(username);
        user.setHashedPassword(PasswordUtil.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setRole("user");
        user.setStatus("active");
        user.setTotalPoints(0);
        user.setStreakDays(0);
        userMapper.insert(user);
        return user;
    }

    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }

    public List<User> getTopUsers(int limit) {
        return userMapper.findTopUsers(limit);
    }

    @Transactional
    public void updatePoints(Integer userId, Integer points) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setTotalPoints(user.getTotalPoints() + points);
            userMapper.update(user);
        }
    }

    public int getUserCount() {
        return userMapper.count();
    }

    @Transactional
    public void deleteUser(Integer id) {
        // Cascade delete all related data
        userProfileMapper.deleteByUserId(id);
        submissionMapper.deleteByUserId(id);
        commentMapper.deleteByUserId(id);
        discussionMapper.deleteByUserId(id);
        notificationMapper.deleteByUserId(id);
        userFavoriteMapper.deleteByUserId(id);
        userFollowMapper.deleteByUserId(id);
        pointLogMapper.deleteByUserId(id);
        wrongBookMapper.deleteByUserId(id);
        contestParticipationMapper.deleteByUserId(id);
        auditLogMapper.deleteByAdminId(id);
        // Finally delete the user
        userMapper.deleteById(id);
    }

    @Transactional
    public void updateUserRole(Integer id, String role) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setRole(role);
            userMapper.update(user);
        }
    }

    @PostConstruct
    public void initAdmin() {
        try {
            User admin = userMapper.findByUsername("admin");
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setHashedPassword(PasswordUtil.encode("admin"));
                admin.setNickname("系统管理员");
                admin.setRole("admin");
                admin.setStatus("active");
                admin.setTotalPoints(0);
                admin.setStreakDays(0);
                admin.setLastLoginAt(new Date());
                userMapper.insert(admin);
                log.info("已创建默认管理员账号 (admin)");
            }
        } catch (Exception e) {
            log.error("初始化管理员账号失败", e);
        }
    }}