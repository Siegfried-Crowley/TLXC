package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.ContestParticipation;
import org.jxiot.tlxc.entity.DailyContest;
import org.jxiot.tlxc.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin
public class ContestController {

    @Autowired
    private ContestService contestService;

    // 获取今日比赛
    @GetMapping("/today")
    public ApiResponse<DailyContest> getTodayContest() {
        DailyContest contest = contestService.getTodayContest();
        return ApiResponse.success(contest);
    }

    // 参加今日比赛（不需要 id 参数，直接使用“today”）
    @PostMapping("/today/join")
    public ApiResponse<ContestParticipation> joinTodayContest(@RequestAttribute Integer userId) {
        // 获取今日比赛
        DailyContest todayContest = contestService.getTodayContest();
        if (todayContest == null) {
            return ApiResponse.error(404, "今日比赛未生成");
        }
        ContestParticipation participation = contestService.joinContest(todayContest.getId(), userId);
        return ApiResponse.success(participation);
    }

    // 获取指定比赛的排行榜（保留原有接口）
    @GetMapping("/{contestId}/rankings")
    public ApiResponse<List<ContestParticipation>> getContestRankings(@PathVariable Integer contestId) {
        List<ContestParticipation> rankings = contestService.getContestRankings(contestId);
        return ApiResponse.success(rankings);
    }
}