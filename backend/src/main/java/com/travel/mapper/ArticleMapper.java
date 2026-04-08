package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Select("SELECT * FROM articles WHERE deleted = 0 AND status = 1 AND (title LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%')) ORDER BY like_count DESC LIMIT #{limit}")
    List<Article> selectArticleByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
}
