package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.Article;
import com.travel.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/article/admin")
public class ArticleAdminController {

    private final ArticleService articleService;

    public ArticleAdminController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public Result<IPage<Article>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(articleService.listArticlesAdmin(page, pageSize, status, keyword));
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        return Result.ok(articleService.getArticleById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        articleService.updateArticle(article);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        articleService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @PutMapping("/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        articleService.toggleTop(id);
        return Result.ok();
    }
}
