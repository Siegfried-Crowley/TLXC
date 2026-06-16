package org.jxiot.tlxc.controller;

import org.jxiot.tlxc.dto.ApiResponse;
import org.jxiot.tlxc.entity.ContestParticipation;
import org.jxiot.tlxc.entity.DailyContest;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin
public class ContestController {

    @Autowired
    private ContestService contestService;

    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayContest(HttpServletRequest request) {
        DailyContest contest = contestService.getTodayContest();
        if (contest == null) {
            return ApiResponse.success(null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("contest", contest);
        result.put("problems", contest.getProblems());

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            ContestParticipation participation = contestService.getUserParticipation(contest.getId(), userId);
            result.put("participation", participation);
        }

        return ApiResponse.success(result);
    }

    @PostMapping("/today/join")
    public ApiResponse<ContestParticipation> joinTodayContest(@RequestAttribute Integer userId) {
        DailyContest todayContest = contestService.getTodayContest();
        if (todayContest == null) {
            return ApiResponse.error(404, "今日比赛未生成");
        }
        ContestParticipation participation = contestService.joinContest(todayContest.getId(), userId);
        return ApiResponse.success(participation);
    }

    @GetMapping("/{contestId}/rankings")
    public ApiResponse<List<ContestParticipation>> getContestRankings(@PathVariable Integer contestId) {
        List<ContestParticipation> rankings = contestService.getContestRankings(contestId);
        return ApiResponse.success(rankings);
    }

    @PostMapping("/{contestId}/submit/{problemId}")
    public ApiResponse<Void> submitInContest(@PathVariable Integer contestId,
                                              @PathVariable Integer problemId,
                                              @RequestAttribute Integer userId) {
        contestService.updateParticipationScore(contestId, userId);
        return ApiResponse.success();
    }

    @GetMapping("/recent")
    public ApiResponse<List<DailyContest>> getRecentContests(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(contestService.getRecentContests(limit));
    }
}
