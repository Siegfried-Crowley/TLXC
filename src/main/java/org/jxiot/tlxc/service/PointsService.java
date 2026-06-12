package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.PointLog;
import org.jxiot.tlxc.entity.User;
import org.jxiot.tlxc.mapper.PointLogMapper;
import org.jxiot.tlxc.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PointsService {

    @Autowired
    private PointLogMapper pointLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void addPoints(Integer userId, Integer problemId, Integer points, String reason) {
        PointLog pointLog = new PointLog();
        pointLog.setUserId(userId);
        pointLog.setProblemId(problemId);
        pointLog.setPoints(points);
        pointLog.setReason(reason);
        pointLogMapper.insert(pointLog);

        User user = userMapper.findById(userId);
        if (user != null) {
            user.setTotalPoints(user.getTotalPoints() + points);
            userMapper.update(user);
        }
    }

    public List<PointLog> getUserPointLogs(Integer userId) {
        return pointLogMapper.findByUserId(userId);
    }
}
