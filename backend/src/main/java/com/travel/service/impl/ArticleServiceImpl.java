package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Article;
import com.travel.mapper.ArticleMapper;
import com.travel.service.ArticleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public IPage<Article> listArticles(Integer page, Integer pageSize, Integer status, Long userId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Article::getStatus, status);
        if (userId != null) wrapper.eq(Article::getUserId, userId);
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreatedAt);
        return articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public IPage<Article> listArticlesAdmin(Integer page, Integer pageSize, Integer status, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Article::getStatus, status);
        if (keyword != null && !keyword.isEmpty()) wrapper.like(Article::getTitle, keyword);
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreatedAt);
        return articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public IPage<Article> listHotArticles(Integer page, Integer pageSize, Integer limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1).orderByDesc(Article::getLikeCount).orderByDesc(Article::getViewCount);
        if (limit != null && limit > 0) {
            return articleMapper.selectPage(new Page<>(1, limit), wrapper);
        }
        return articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Article getArticleById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public void createArticle(Article article) {
        article.setStatus(1);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCollectCount(0);
        article.setCommentCount(0);
        article.setIsTop(0);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);
    }

    @Override
    public void updateArticle(Article article) {
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setStatus(status);
            article.setUpdatedAt(LocalDateTime.now());
            articleMapper.updateById(article);
        }
    }

    @Override
    public void toggleTop(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setIsTop(article.getIsTop() == 1 ? 0 : 1);
            article.setUpdatedAt(LocalDateTime.now());
            articleMapper.updateById(article);
        }
    }

    @Override
    public void likeArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public void viewArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }
}
