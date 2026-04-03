package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.entity.Article;

public interface ArticleService {
    IPage<Article> listArticles(Integer page, Integer pageSize, Integer status, Long userId);
    IPage<Article> listArticlesAdmin(Integer page, Integer pageSize, Integer status, String keyword);
    IPage<Article> listHotArticles(Integer page, Integer pageSize, Integer limit);
    Article getArticleById(Long id);
    void createArticle(Article article);
    void updateArticle(Article article);
    void deleteArticle(Long id);
    void updateStatus(Long id, Integer status);
    void toggleTop(Long id);
    void likeArticle(Long id);
    void viewArticle(Long id);
}
