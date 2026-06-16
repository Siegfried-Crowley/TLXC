package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.ContestParticipation;
import org.jxiot.tlxc.entity.ContestProblem;
import org.jxiot.tlxc.entity.DailyContest;
import org.jxiot.tlxc.entity.Problem;
import org.jxiot.tlxc.entity.Submission;
import org.jxiot.tlxc.mapper.ContestMapper;
import org.jxiot.tlxc.mapper.ContestParticipationMapper;
import org.jxiot.tlxc.mapper.ContestProblemMapper;
import org.jxiot.tlxc.mapper.ProblemMapper;
import org.jxiot.tlxc.mapper.SubmissionMapper;
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
    private static final int PROBLEMS_PER_CONTEST = 3;

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ContestParticipationMapper participationMapper;

    @Autowired
    private ContestProblemMapper contestProblemMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private NotificationService notificationService;

    @PostConstruct
    public void init() {
        createTodayContestIfNotExists();
        ensureTodayContestHasProblems();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void scheduledCreateDailyContest() {
        // 先结算前一天的比赛
        finalizeYesterdayContest();
        createTodayContestIfNotExists();
    }

    private void finalizeYesterdayContest() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date yesterday = cal.getTime();

        DailyContest contest = contestMapper.findByDate(yesterday);
        if (contest != null && "active".equals(contest.getStatus())) {
            // 更新状态为 finalized
            contestMapper.updateStatus(contest.getId(), "finalized");
            log.info("比赛(id={})已自动结算", contest.getId());

            // 给参与者发放奖励积分 (前3名额外加分)
            List<ContestParticipation> rankings = participationMapper.findByContestId(contest.getId());
            for (int i = 0; i < rankings.size(); i++) {
                ContestParticipation cp = rankings.get(i);
                cp.setRank(i + 1);
                // 发放奖励
                int bonus = 0;
                if (i == 0) bonus = 30;
                else if (i == 1) bonus = 20;
                else if (i == 2) bonus = 10;
                // 发送通知
                if (bonus > 0) {
                    org.jxiot.tlxc.entity.Notification note = new org.jxiot.tlxc.entity.Notification();
                    note.setUserId(cp.getUserId());
                    note.setType("contest_reward");
                    note.setTitle("竞赛奖励");
                    note.setContent("您在 " + contest.getTitle() + " 中获得第" + (i+1) + "名，获得 " + bonus + " 积分奖励！");
                    notificationService.sendNotification(note);
                }
            }
        }
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
        if (existing != null) return;

        DailyContest contest = new DailyContest();
        contest.setContestDate(getTodayDate());
        contest.setTitle("每日挑战");
        contest.setStatus("active");
        contest.setGeneratedAt(new Date());
        contestMapper.insert(contest);
        assignRandomProblems(contest.getId());
    }

    private void assignRandomProblems(Integer contestId) {
        int total = problemMapper.countPublished();
        log.info("为今日比赛(id={})选题，已发布题目总数: {}", contestId, total);

        if (total == 0) {
            int updated = problemMapper.publishAllDrafts();
            log.info("没有已发布题目，尝试发布已有题目，更新了 {} 条", updated);
            total = problemMapper.countPublished();
            if (total == 0) {
                log.warn("数据库中没有任何题目，无法选题");
                return;
            }
        }

        int pickCount = Math.min(PROBLEMS_PER_CONTEST, total);
        List<Problem> problems = problemMapper.findRandomPublished(pickCount);
        log.info("随机选题结果: {} 道题", problems.size());
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

    public DailyContest getTodayContest() {
        Date today = getTodayDate();
        DailyContest contest = contestMapper.findByDate(today);
        if (contest != null) {
            List<Integer> problemIds = contestProblemMapper.findProblemIdsByContestId(contest.getId());
            List<Problem> problems = new ArrayList<>();
            for (Integer pid : problemIds) {
                Problem p = problemMapper.findById(pid);
                if (p != null) problems.add(p);
            }
            contest.setProblems(problems);
        }
        return contest;
    }

    public List<DailyContest> getRecentContests(int limit) {
        return contestMapper.findRecentContests(limit);
    }

    public List<Problem> getContestProblems(Integer contestId) {
        List<Integer> problemIds = contestProblemMapper.findProblemIdsByContestId(contestId);
        List<Problem> problems = new ArrayList<>();
        for (Integer pid : problemIds) {
            Problem p = problemMapper.findById(pid);
            if (p != null) problems.add(p);
        }
        return problems;
    }

    @Transactional
    public ContestParticipation joinContest(Integer contestId, Integer userId) {
        ContestParticipation existing = participationMapper.findByContestAndUser(contestId, userId);
        if (existing != null) return existing;

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

    public ContestParticipation getUserParticipation(Integer contestId, Integer userId) {
        return participationMapper.findByContestAndUser(contestId, userId);
    }

    @Transactional
    public void updateParticipationScore(Integer contestId, Integer userId) {
        ContestParticipation participation = participationMapper.findByContestAndUser(contestId, userId);
        if (participation == null) return;

        // 计算该用户在比赛中的答题情况
        DailyContest contest = contestMapper.findByDate(getTodayDate());
        if (contest == null) return;

        List<Integer> problemIds = contestProblemMapper.findProblemIdsByContestId(contestId);
        int totalScore = 0;
        int acceptedCount = 0;
        int totalRuntime = 0;

        for (Integer pid : problemIds) {
            List<Submission> subs = submissionMapper.findByUserAndProblem(userId, pid);
            if (!subs.isEmpty()) {
                Submission best = subs.get(0);
                if ("accepted".equals(best.getStatus())) {
                    totalScore += 100;
                    acceptedCount++;
                }
                totalRuntime += best.getRuntimeMs() != null ? best.getRuntimeMs() : 0;
            }
        }

        participation.setTotalScore(totalScore);
        participation.setAcceptedCount(acceptedCount);
        participation.setTotalRuntimeMs(totalRuntime);
        participation.setSubmittedAt(new Date());
        participationMapper.update(participation);
    }

    public List<ContestParticipation> getContestRankings(Integer contestId) {
        return participationMapper.findByContestId(contestId);
    }
}
