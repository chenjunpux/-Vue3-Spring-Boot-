package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Coupon;
import com.travel.entity.UserCoupon;
import com.travel.mapper.CouponMapper;
import com.travel.mapper.UserCouponMapper;
import com.travel.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final UserCouponMapper userCouponMapper;

    public CouponServiceImpl(UserCouponMapper userCouponMapper) {
        this.userCouponMapper = userCouponMapper;
    }

    @Override
    public Page<Coupon> listAvailableCoupons(Integer page, Integer pageSize) {
        Page<Coupon> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
               .gt(Coupon::getRemainCount, 0)
               .le(Coupon::getValidStart, LocalDateTime.now())
               .ge(Coupon::getValidEnd, LocalDateTime.now())
               .orderByDesc(Coupon::getCreatedAt);
        return baseMapper.selectPage(p, wrapper);
    }

    @Override
    public List<UserCoupon> listMyCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) wrapper.eq(UserCoupon::getStatus, status);
        wrapper.orderByDesc(UserCoupon::getReceiveTime);
        return userCouponMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public UserCoupon receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = baseMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券不存在或已下架");
        }
        if (coupon.getRemainCount() <= 0) {
            throw new RuntimeException("优惠券已领完");
        }
        if (coupon.getValidEnd().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        LambdaQueryWrapper<UserCoupon> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, couponId);
        long received = userCouponMapper.selectCount(existWrapper);
        if (received >= coupon.getPerUserLimit()) {
            throw new RuntimeException("该优惠券已达领取上限");
        }

        coupon.setRemainCount(coupon.getRemainCount() - 1);
        baseMapper.updateById(coupon);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setDiscountValue(coupon.getDiscountValue());
        userCoupon.setMinAmount(coupon.getMinAmount());
        userCoupon.setMaxDiscount(coupon.getMaxDiscount());
        userCoupon.setStatus(0);
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCoupon.setExpireTime(coupon.getValidEnd());
        userCouponMapper.insert(userCoupon);
        return userCoupon;
    }

    @Override
    @Transactional
    public void useCoupon(Long userCouponId, String orderNo) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || userCoupon.getStatus() != 0) {
            throw new RuntimeException("优惠券不存在或已使用");
        }
        userCoupon.setStatus(1);
        userCoupon.setOrderNo(orderNo);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
    }

    @Override
    public Map<String, Object> calculateDiscount(Long couponId, BigDecimal orderAmount) {
        Map<String, Object> result = new HashMap<>();
        Coupon coupon = baseMapper.selectById(couponId);
        if (coupon == null) {
            result.put("canUse", false);
            result.put("reason", "优惠券不存在");
            return result;
        }
        result.put("canUse", canUse(couponId, orderAmount));
        result.put("couponName", coupon.getName());
        result.put("couponType", coupon.getType());
        result.put("discountValue", coupon.getDiscountValue());
        result.put("minAmount", coupon.getMinAmount());
        if (Boolean.TRUE.equals(result.get("canUse"))) {
            result.put("discountAmount", calculateDiscountAmount(coupon, orderAmount));
        } else {
            result.put("reason", orderAmount.compareTo(coupon.getMinAmount()) < 0
                ? "订单金额未达到最低消费 " + coupon.getMinAmount() + " 元"
                : "优惠券不可用");
            result.put("discountAmount", BigDecimal.ZERO);
        }
        return result;
    }

    @Override
    public Boolean canUse(Long couponId, BigDecimal orderAmount) {
        Coupon coupon = baseMapper.selectById(couponId);
        if (coupon == null) return false;
        return orderAmount.compareTo(coupon.getMinAmount()) >= 0
            && LocalDateTime.now().isBefore(coupon.getValidEnd())
            && LocalDateTime.now().isAfter(coupon.getValidStart().minusDays(1))
            && coupon.getStatus() == 1;
    }

    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount) {
        if (coupon.getType() == 1) {
            return coupon.getDiscountValue().min(orderAmount);
        } else if (coupon.getType() == 2) {
            BigDecimal discount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountValue()));
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
            return discount.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // ========== 管理端方法实现 ==========

    @Override
    public Page<Coupon> listCouponsForAdmin(Integer page, Integer pageSize, Integer status) {
        Page<Coupon> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        wrapper.orderByDesc(Coupon::getCreatedAt);
        return baseMapper.selectPage(p, wrapper);
    }

    @Override
    @Transactional
    public boolean createCoupon(Coupon coupon) {
        // 设置默认值
        if (coupon.getRemainCount() == null) {
            coupon.setRemainCount(coupon.getTotalCount());
        }
        if (coupon.getPerUserLimit() == null) {
            coupon.setPerUserLimit(1);
        }
        if (coupon.getApplicableType() == null) {
            coupon.setApplicableType(1); // 默认全场通用
        }
        if (coupon.getStatus() == null) {
            coupon.setStatus(1); // 默认上架
        }
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        return baseMapper.insert(coupon) > 0;
    }

    @Override
    @Transactional
    public boolean updateCoupon(Coupon coupon) {
        coupon.setUpdatedAt(LocalDateTime.now());
        return baseMapper.updateById(coupon) > 0;
    }

    @Override
    @Transactional
    public boolean deleteCoupon(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean toggleCouponStatus(Long id) {
        Coupon coupon = baseMapper.selectById(id);
        if (coupon == null) {
            return false;
        }
        coupon.setStatus(coupon.getStatus() == 1 ? 0 : 1);
        coupon.setUpdatedAt(LocalDateTime.now());
        return baseMapper.updateById(coupon) > 0;
    }

    @Override
    public int getUsedCount(Long couponId) {
        Long result = userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getCouponId, couponId)
                .eq(UserCoupon::getStatus, 1)
        );
        return result != null ? result.intValue() : 0;
    }
}
