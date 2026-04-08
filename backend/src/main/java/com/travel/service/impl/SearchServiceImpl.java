package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Article;
import com.travel.entity.Hotel;
import com.travel.entity.Spot;
import com.travel.mapper.ArticleMapper;
import com.travel.mapper.HotelMapper;
import com.travel.mapper.SpotMapper;
import com.travel.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private final SpotMapper spotMapper;
    private final HotelMapper hotelMapper;
    private final ArticleMapper articleMapper;

    public SearchServiceImpl(SpotMapper spotMapper, HotelMapper hotelMapper, ArticleMapper articleMapper) {
        this.spotMapper = spotMapper;
        this.hotelMapper = hotelMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public Map<String, Object> globalSearch(String keyword, Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("spots", searchSpots(keyword, 10));
        result.put("hotels", searchHotels(keyword, 10));
        result.put("articles", searchArticles(keyword, 10));
        return result;
    }

    @Override
    public List<Map<String, Object>> searchSpots(String keyword, Integer limit) {
        List<Spot> spots = spotMapper.selectSpotByKeyword(keyword, limit);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Spot spot : spots) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", spot.getId());
            item.put("name", spot.getName());
            item.put("city", spot.getCity());
            item.put("coverImage", spot.getCoverImage());
            item.put("ticketPrice", spot.getTicketPrice());
            item.put("level", spot.getLevel());
            item.put("type", "spot");
            results.add(item);
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> searchHotels(String keyword, Integer limit) {
        List<Hotel> hotels = hotelMapper.selectHotelByKeyword(keyword, limit);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Hotel hotel : hotels) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", hotel.getId());
            item.put("name", hotel.getName());
            item.put("city", hotel.getCity());
            item.put("coverImage", hotel.getCoverImage());
            item.put("starLevel", hotel.getStarLevel());
            item.put("type", "hotel");
            results.add(item);
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> searchArticles(String keyword, Integer limit) {
        List<Article> articles = articleMapper.selectArticleByKeyword(keyword, limit);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Article article : articles) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", article.getId());
            item.put("title", article.getTitle());
            item.put("coverImage", article.getCoverImage());
            item.put("likeCount", article.getLikeCount());
            item.put("commentCount", article.getCommentCount());
            item.put("type", "article");
            results.add(item);
        }
        return results;
    }
}
