package com.blog.service;

import com.blog.vo.RankVo;

import java.util.Set;

public interface RankService {
    void updateRank(String username);
    void clearRank();
    Set<RankVo> getRank(String username);
    Set<RankVo> getTodayRank();

    Set<RankVo> getWeekRank();
    Set<RankVo> getMonthRank();
    Set<RankVo> getYearRank();
}
