package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.entity.Hotel;
import com.travel.entity.RoomType;
import java.util.List;

public interface HotelService {
    IPage<Hotel> listHotels(Integer page, Integer pageSize, String city, String keyword, Integer status);
    IPage<Hotel> listHotHotels(Integer page, Integer pageSize, Integer limit);
    Hotel getHotelById(Long id);
    List<RoomType> getRoomTypesByHotelId(Long hotelId);
    void createHotel(Hotel hotel);
    void updateHotel(Hotel hotel);
    void deleteHotel(Long id);
    void updateStatus(Long id, Integer status);
    void createRoomType(RoomType roomType);
    void updateRoomType(RoomType roomType);
    void deleteRoomType(Long id);
}
