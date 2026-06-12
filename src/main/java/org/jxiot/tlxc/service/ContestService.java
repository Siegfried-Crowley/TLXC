package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.ContestParticipation;
import org.jxiot.tlxc.entity.DailyContest;
import org.jxiot.tlxc.mapper.ContestMapper;
import org.jxiot.tlxc.mapper.ContestParticipationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ContestService {

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ContestParticipationMapper participationMapper;

    @PostConstruct
    public void init() {
        createTodayContestIfNotExists();
    }

    private void createTodayContestIfNotExists() {
        DailyContest contest = getTodayContest();
        if (contest == null) {
            contest = new DailyContest();
            contest.setContestDate(getTodayDate());
            contest.setTitle("每日一题");
            contest.setStatus("active");
            contest.setGeneratedAt(new Date());
            contestMapper.insert(contest);
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
        return contestMapper.findByDate(today);
    }

    public List<DailyContest> getRecentContests(int limit) {
        return contestMapper.findRecentContests(limit);
    }

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

    public List<ContestParticipation> getContestRankings(Integer contestId) {
        return participationMapper.findByContestId(contestId);
    }
}