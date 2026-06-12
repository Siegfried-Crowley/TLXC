package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.mapper.UserMapper;
import org.jxiot.tlxc.util.JwtUtil;
import org.jxiot.tlxc.util.PasswordUtil;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!PasswordUtil.verify(password, user.getHashedPassword())) {
            throw new RuntimeException("密码错误");
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
            throw new RuntimeException("用户名已存在");
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

    @PostConstruct
    public void initAdmin() {
        try {
            User admin = userMapper.findByUsername("admin");
            String correctHash = PasswordUtil.encode("admin");
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setHashedPassword(correctHash);
                admin.setNickname("系统管理员");
                admin.setRole("admin");
                admin.setStatus("active");
                admin.setTotalPoints(0);
                admin.setStreakDays(0);
                admin.setLastLoginAt(new Date());
                userMapper.insert(admin);
                System.out.println("创建管理员账号：admin / admin，密码哈希：" + correctHash);
            } else {
                // 关键：如果密码不匹配，更新数据库
                if (!PasswordUtil.verify("admin", admin.getHashedPassword())) {
                    admin.setHashedPassword(correctHash);
                    userMapper.update(admin);
                    System.out.println("已更新 admin 密码哈希为：" + correctHash);
                } else {
                    System.out.println("admin 密码哈希正确，无需更新");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}