package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Hotel;
import com.travel.entity.RoomType;
import com.travel.mapper.HotelMapper;
import com.travel.mapper.RoomTypeMapper;
import com.travel.service.HotelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;

    public HotelServiceImpl(HotelMapper hotelMapper, RoomTypeMapper roomTypeMapper) {
        this.hotelMapper = hotelMapper;
        this.roomTypeMapper = roomTypeMapper;
    }

    @Override
    public IPage<Hotel> listHotels(Integer page, Integer pageSize, String city, String keyword, Integer status) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(city)) wrapper.eq(Hotel::getCity, city);
        if (StringUtils.hasText(keyword)) wrapper.and(w -> w.like(Hotel::getName, keyword).or().like(Hotel::getDescription, keyword));
        if (status != null) wrapper.eq(Hotel::getStatus, status);
        wrapper.orderByDesc(Hotel::getHotScore);
        return hotelMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public IPage<Hotel> listHotHotels(Integer page, Integer pageSize, Integer limit) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1).orderByDesc(Hotel::getHotScore);
        if (limit != null && limit > 0) {
            return hotelMapper.selectPage(new Page<>(1, limit), wrapper);
        }
        return hotelMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Hotel getHotelById(Long id) {
        return hotelMapper.selectById(id);
    }

    @Override
    public List<RoomType> getRoomTypesByHotelId(Long hotelId) {
        return roomTypeMapper.selectList(
            new LambdaQueryWrapper<RoomType>().eq(RoomType::getHotelId, hotelId)
        );
    }

    @Override
    public void createHotel(Hotel hotel) {
        hotel.setHotScore(0);
        hotel.setStatus(1);
        hotelMapper.insert(hotel);
    }

    @Override
    public void updateHotel(Hotel hotel) {
        hotelMapper.updateById(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelMapper.deleteById(id);
        // 删除关联房型
        roomTypeMapper.delete(new LambdaQueryWrapper<RoomType>().eq(RoomType::getHotelId, id));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel != null) {
            hotel.setStatus(status);
            hotelMapper.updateById(hotel);
        }
    }

    @Override
    public void createRoomType(RoomType roomType) {
        roomTypeMapper.insert(roomType);
    }

    @Override
    public void updateRoomType(RoomType roomType) {
        roomTypeMapper.updateById(roomType);
    }

    @Override
    public void deleteRoomType(Long id) {
        roomTypeMapper.deleteById(id);
    }
}
