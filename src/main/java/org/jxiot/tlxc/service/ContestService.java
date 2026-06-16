package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.ContestParticipation;
import org.jxiot.tlxc.entity.ContestProblem;
import org.jxiot.tlxc.entity.DailyContest;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.mapper.ContestMapper;
import org.jxiot.tlxc.mapper.ContestParticipationMapper;
import org.jxiot.tlxc.mapper.ContestProblemMapper;
import org.jxiot.tlxc.mapper.ProblemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ContestService {

    private static final Logger log = LoggerFactory.getLogger(ContestService.class);

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ContestParticipationMapper participationMapper;

    @Autowired
    private ContestProblemMapper contestProblemMapper;

    @Autowired
    private ProblemMapper problemMapper;

    /**
     * 应用启动时尝试创建今日比赛（若当天已有则不重复创建）
     * 如果已有比赛但没有关联题目，则补充选题
     */
    @PostConstruct
    public void init() {
        createTodayContestIfNotExists();
        // 修复已有但没选题的比赛
        ensureTodayContestHasProblems();
    }

    /**
     * 每天 00:00 自动创建当天的比赛，并从已发布题目中随机选题
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void scheduledCreateDailyContest() {
        createTodayContestIfNotExists();
    }

    private void ensureTodayContestHasProblems() {
        DailyContest contest = getTodayContest();
        if (contest != null) {
            List<Integer> existingIds = contestProblemMapper.findProblemIdsByContestId(contest.getId());
            if (existingIds.isEmpty()) {
                log.info("今日比赛(id={})未关联题目，正在补充选题...", contest.getId());
                assignRandomProblems(contest.getId());
            }
        }
    }

    private void createTodayContestIfNotExists() {
        DailyContest existing = getTodayContest();
        if (existing != null) {
            return;
        }

        DailyContest contest = new DailyContest();
        contest.setContestDate(getTodayDate());
        contest.setTitle("每日一题");
        contest.setStatus("active");
        contest.setGeneratedAt(new Date());
        contestMapper.insert(contest);

        // 随机选取 1 道已发布的题目
        assignRandomProblems(contest.getId());
    }

    /**
     * 从已发布的题目中随机选取 1 道，关联到每日比赛
     */
    private void assignRandomProblems(Integer contestId) {
        int total = problemMapper.countPublished();
        log.info("为今日比赛(id={})选题，已发布题目总数: {}", contestId, total);

        if (total == 0) {
            // 如果没有已发布题目，尝试将已有题目标记为 published
            int updated = problemMapper.publishAllDrafts();
            log.info("没有已发布题目，尝试发布已有题目，更新了 {} 条", updated);
            total = problemMapper.countPublished();
            if (total == 0) {
                log.warn("数据库中没有任何题目，无法选题");
                return;
            }
        }

        // 利用 MySQL RAND() 随机选取一条
        List<Problem> problems = problemMapper.findRandomPublished(1);
        log.info("随机选题结果: {}", problems.isEmpty() ? "无" : problems.get(0).getId() + " - " + problems.get(0).getTitle());
        for (int i = 0; i < problems.size(); i++) {
            ContestProblem cp = new ContestProblem();
            cp.setContestId(contestId);
            cp.setProblemId(problems.get(i).getId());
            cp.setOrderIndex(i);
            contestProblemMapper.insert(cp);
        }
    }

    private Date getTodayDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取今日比赛（含关联题目信息）
     */
    public DailyContest getTodayContest() {
        Date today = getTodayDate();
        DailyContest contest = contestMapper.findByDate(today);
        if (contest != null) {
            List<Integer> problemIds = contestProblemMapper.findProblemIdsByContestId(contest.getId());
            List<Problem> problems = new ArrayList<>();
            for (Integer pid : problemIds) {
                Problem p = problemMapper.findById(pid);
                if (p != null) {
                    problems.add(p);
                }
            }
            contest.setProblems(problems);
        }
        return contest;
    }

    public List<DailyContest> getRecentContests(int limit) {
        return contestMapper.findRecentContests(limit);
    }

    /**
     * 获取指定比赛的题目列表
     */
    public List<Problem> getContestProblems(Integer contestId) {
        List<Integer> problemIds = contestProblemMapper.findProblemIdsByContestId(contestId);
        List<Problem> problems = new ArrayList<>();
        for (Integer pid : problemIds) {
            Problem p = problemMapper.findById(pid);
            if (p != null) {
                problems.add(p);
            }
        }
        return problems;
    }

    /**
     * 用户参加今日比赛
     */
    @Transactional
    public ContestParticipation joinContest(Integer contestId, Integer userId) {
        ContestParticipation existing = participationMapper.findByContestAndUser(contestId, userId);
        if (existing != null) {
            return existing;
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        Date deadline = cal.getTime();

        ContestParticipation participation = new ContestParticipation();
        participation.setContestId(contestId);
        participation.setUserId(userId);
        participation.setDeadlineAt(deadline);
        participation.setTotalScore(0);
        participation.setAcceptedCount(0);
        participation.setTotalRuntimeMs(0);

        participationMapper.insert(participation);
        return participation;
    }

    /**
     * 获取用户在今日比赛中的参与记录
     */
    public ContestParticipation getUserParticipation(Integer contestId, Integer userId) {
        return participationMapper.findByContestAndUser(contestId, userId);
    }

    /**
     * 用户提交题目后更新比赛成绩
     */
    @Transactional
    public void updateParticipationScore(Integer contestId, Integer userId) {
        ContestParticipation participation = participationMapper.findByContestAndUser(contestId, userId);
        if (participation == null) {
            return;
        }
        // 查询该用户在该比赛关联题目中的所有通过记录
        // 这里简化处理：由前端/提交逻辑单独计算，此处仅标记已提交
    }

    /**
     * 获取比赛排行榜
     */
    public List<ContestParticipation> getContestRankings(Integer contestId) {
        return participationMapper.findByContestId(contestId);
    }
}
