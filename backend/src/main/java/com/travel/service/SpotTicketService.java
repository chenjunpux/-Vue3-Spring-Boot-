package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.SpotTicket;
import java.util.List;

public interface SpotTicketService extends IService<SpotTicket> {
    List<SpotTicket> getBySpotId(Long spotId);
}
