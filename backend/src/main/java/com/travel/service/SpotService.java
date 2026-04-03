package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.entity.Spot;

public interface SpotService {
    IPage<Spot> listSpots(Integer page, Integer pageSize, String city, String keyword, String tags, Integer status);
    IPage<Spot> listHotSpots(Integer page, Integer pageSize, Integer limit);
    Spot getSpotById(Long id);
    void createSpot(Spot spot);
    void updateSpot(Spot spot);
    void deleteSpot(Long id);
    void updateStatus(Long id, Integer status);
}
