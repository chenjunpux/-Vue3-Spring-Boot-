package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.Article;
import com.travel.entity.User;
import com.travel.service.ArticleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public Result<IPage<Article>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        // 只显示已发布的
        return Result.ok(articleService.listArticles(page, pageSize, status == null ? 1 : status, null));
    }

    @GetMapping("/hot")
    public Result<IPage<Article>> hot(
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(articleService.listHotArticles(page, pageSize, limit));
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        if (article != null) {
            articleService.viewArticle(id);
        }
        return Result.ok(article);
    }

    // 我的游记
    @GetMapping("/my")
    public Result<IPage<Article>> myArticles(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return Result.ok(articleService.listArticles(page, pageSize, null, user.getId()));
        }
        return Result.error(401, "未登录");
    }

    // 发布游记
    @PostMapping
    public Result<Void> create(Authentication authentication, @RequestBody Article article) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            article.setUserId(user.getId());
            articleService.createArticle(article);
            return Result.ok();
        }
        return Result.error(401, "未登录");
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

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        articleService.likeArticle(id);
        return Result.ok();
    }
}
