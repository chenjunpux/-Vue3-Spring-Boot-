package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Coupon;
import com.travel.entity.User;
import com.travel.entity.UserCoupon;
import com.travel.service.CouponService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/api/v1/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * 获取可用优惠券列表
     */
    @GetMapping("/available")
    public Result<Page<Coupon>> availableCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.ok(couponService.listAvailableCoupons(page, pageSize));
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/receive/{couponId}")
    public Result<UserCoupon> receiveCoupon(
            Authentication authentication,
            @PathVariable Long couponId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        UserCoupon userCoupon = couponService.receiveCoupon(user.getId(), couponId);
        return Result.ok(userCoupon);
    }

    /**
     * 获取我的优惠券
     */
    @GetMapping("/my")
    public Result<List<UserCoupon>> myCoupons(
            Authentication authentication,
            @RequestParam(required = false) Integer status) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        return Result.ok(couponService.listMyCoupons(user.getId(), status));
    }

    /**
     * 计算优惠金额
     */
    @PostMapping("/calculate")
    public Result<Map<String, Object>> calculateDiscount(
            Authentication authentication,
            @RequestBody Map<String, Object> params) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return Result.error(401, "请先登录");
        }
        Long couponId = Long.valueOf(params.get("couponId").toString());
        BigDecimal orderAmount = new BigDecimal(params.get("orderAmount").toString());
        return Result.ok(couponService.calculateDiscount(couponId, orderAmount));
    }

    /**
     * 验证优惠券是否可用
     */
    @GetMapping("/canUse/{couponId}")
    public Result<Map<String, Object>> canUse(
            @PathVariable Long couponId,
            @RequestParam BigDecimal orderAmount) {
        Map<String, Object> result = couponService.calculateDiscount(couponId, orderAmount);
        return Result.ok(result);
    }

    // ========== 管理端 API ==========

    /**
     * 获取优惠券列表（管理端）
     */
    @GetMapping("/admin/list")
    public Result<Page<Coupon>> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.ok(couponService.listCouponsForAdmin(page, pageSize, status));
    }

    /**
     * 创建优惠券
     */
    @PostMapping("/admin/create")
    public Result<Boolean> createCoupon(@RequestBody Coupon coupon) {
        boolean success = couponService.createCoupon(coupon);
        return success ? Result.ok(true) : Result.error("创建失败");
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/admin/update")
    public Result<Boolean> updateCoupon(@RequestBody Coupon coupon) {
        boolean success = couponService.updateCoupon(coupon);
        return success ? Result.ok(true) : Result.error("更新失败");
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/admin/{id}")
    public Result<Boolean> deleteCoupon(@PathVariable Long id) {
        boolean success = couponService.deleteCoupon(id);
        return success ? Result.ok(true) : Result.error("删除失败");
    }

    /**
     * 切换优惠券状态（上架/下架）
     */
    @PutMapping("/admin/toggle/{id}")
    public Result<Boolean> toggleStatus(@PathVariable Long id) {
        boolean success = couponService.toggleCouponStatus(id);
        return success ? Result.ok(true) : Result.error("操作失败");
    }

    /**
     * 获取优惠券详情（管理端）
     */
    @GetMapping("/admin/{id}")
    public Result<Map<String, Object>> getCouponDetail(@PathVariable Long id) {
        Coupon coupon = couponService.getById(id);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        // 构建详情Map
        Map<String, Object> detail = new java.util.HashMap<>();
        detail.put("id", coupon.getId());
        detail.put("name", coupon.getName());
        detail.put("description", coupon.getDescription());
        detail.put("type", coupon.getType());
        detail.put("discountValue", coupon.getDiscountValue());
        detail.put("minAmount", coupon.getMinAmount());
        detail.put("maxDiscount", coupon.getMaxDiscount());
        detail.put("totalCount", coupon.getTotalCount());
        detail.put("remainCount", coupon.getRemainCount());
        detail.put("usedCount", couponService.getUsedCount(id));
        detail.put("perUserLimit", coupon.getPerUserLimit());
        detail.put("applicableType", coupon.getApplicableType());
        detail.put("validStart", coupon.getValidStart());
        detail.put("validEnd", coupon.getValidEnd());
        detail.put("status", coupon.getStatus());
        return Result.ok(detail);
    }
}
