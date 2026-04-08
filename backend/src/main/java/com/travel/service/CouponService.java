package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Coupon;
import com.travel.entity.UserCoupon;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CouponService extends IService<Coupon> {
    Page<Coupon> listAvailableCoupons(Integer page, Integer pageSize);
    List<UserCoupon> listMyCoupons(Long userId, Integer status);
    UserCoupon receiveCoupon(Long userId, Long couponId);
    void useCoupon(Long userCouponId, String orderNo);
    Map<String, Object> calculateDiscount(Long couponId, BigDecimal orderAmount);
    Boolean canUse(Long couponId, BigDecimal orderAmount);

    // ========== 管理端方法 ==========
    Page<Coupon> listCouponsForAdmin(Integer page, Integer pageSize, Integer status);
    boolean createCoupon(Coupon coupon);
    boolean updateCoupon(Coupon coupon);
    boolean deleteCoupon(Long id);
    boolean toggleCouponStatus(Long id);
    int getUsedCount(Long couponId);
}
