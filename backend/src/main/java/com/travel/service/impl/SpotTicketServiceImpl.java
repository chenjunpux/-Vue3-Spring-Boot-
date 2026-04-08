package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.SpotTicket;
import com.travel.mapper.SpotTicketMapper;
import com.travel.service.SpotTicketService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpotTicketServiceImpl extends ServiceImpl<SpotTicketMapper, SpotTicket> implements SpotTicketService {

    @Override
    public List<SpotTicket> getBySpotId(Long spotId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<SpotTicket>()
                .eq(SpotTicket::getSpotId, spotId)
                .eq(SpotTicket::getStatus, 1)
                .orderByAsc(SpotTicket::getSortOrder)
        );
    }
}
