package com.blog.service.impl;

import com.blog.service.RankService;
import com.blog.vo.RankVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RANK_KEY = "rank";

    @Override
    public void updateRank(String username) {

    }
    @Override
    public void clearRank() {

    }
    @Override
    public Set<RankVo> getRank(String username) {
        return null;
    }

    @Override
    public Set<RankVo> getTodayRank() {
        return null;
    }

    @Override
    public Set<RankVo> getWeekRank() {
        return null;
    }

    @Override
    public Set<RankVo> getMonthRank() {
        return null;
    }

    @Override
    public Set<RankVo> getYearRank() {
        return null;
    }
}
