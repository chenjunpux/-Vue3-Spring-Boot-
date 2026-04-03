package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Spot;
import com.travel.mapper.SpotMapper;
import com.travel.service.SpotService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SpotServiceImpl implements SpotService {

    private final SpotMapper spotMapper;

    public SpotServiceImpl(SpotMapper spotMapper) {
        this.spotMapper = spotMapper;
    }

    @Override
    public IPage<Spot> listSpots(Integer page, Integer pageSize, String city, String keyword, String tags, Integer status) {
        LambdaQueryWrapper<Spot> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(city)) wrapper.eq(Spot::getCity, city);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Spot::getName, keyword).or().like(Spot::getDescription, keyword));
        }
        if (status != null) wrapper.eq(Spot::getStatus, status);
        wrapper.orderByDesc(Spot::getHotScore);

        return spotMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public IPage<Spot> listHotSpots(Integer page, Integer pageSize, Integer limit) {
        LambdaQueryWrapper<Spot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Spot::getStatus, 1).orderByDesc(Spot::getHotScore);
        if (limit != null && limit > 0) {
            return spotMapper.selectPage(new Page<>(1, limit), wrapper);
        }
        return spotMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Spot getSpotById(Long id) {
        Spot spot = spotMapper.selectById(id);
        if (spot != null) {
            // 浏览次数+1
            spot.setViewCount(spot.getViewCount() + 1);
            spotMapper.updateById(spot);
        }
        return spot;
    }

    @Override
    public void createSpot(Spot spot) {
        spot.setHotScore(0);
        spot.setViewCount(0);
        spot.setStatus(1);
        spotMapper.insert(spot);
    }

    @Override
    public void updateSpot(Spot spot) {
        spotMapper.updateById(spot);
    }

    @Override
    public void deleteSpot(Long id) {
        spotMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Spot spot = spotMapper.selectById(id);
        if (spot != null) {
            spot.setStatus(status);
            spotMapper.updateById(spot);
        }
    }
}
