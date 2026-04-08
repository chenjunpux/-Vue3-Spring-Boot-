<template>
  <div class="hotel-detail-page">
    <div class="carousel-section" ref="carouselRef">
      <div class="carousel-wrapper" @mouseenter="pauseDanmaku" @mouseleave="resumeDanmaku">
        <div v-for="(img, i) in currentHotel.images" :key="i" class="carousel-item" :class="{ active: carouselIdx === i }">
          <img :src="img" :alt="currentHotel.name" />
          <div class="carousel-overlay"></div>
        </div>
        <div class="danmaku-layer" ref="danmakuLayerRef">
          <div v-for="dm in visibleDanmaku" :key="dm.id" class="danmaku-item" :style="{ top: dm.top + 'px', animationDuration: dm.duration + 's', animationDelay: dm.delay + 's', '--dm-color': dm.color }">
            <span class="dm-avatar">{{ dm.avatar }}</span>
            <span class="dm-text">{{ dm.text }}</span>
          </div>
        </div>
        <div class="carousel-controls">
          <button class="ctrl-btn" @click="prevImage">‹</button>
          <div class="carousel-dots">
            <span v-for="(_, i) in currentHotel.images" :key="i" class="dot" :class="{ active: carouselIdx === i }" @click="carouselIdx = i"></span>
          </div>
          <button class="ctrl-btn" @click="nextImage">›</button>
        </div>
        <div class="carousel-info">
          <div class="info-left">
            <div class="hotel-score">
              <span class="score-num">{{ currentHotel.score }}</span>
              <span class="score-label">分</span>
            </div>
            <div class="hotel-reviews">{{ currentHotel.commentCount }}条评价</div>
          </div>
          <div class="info-right">
            <h1 class="hotel-name">{{ currentHotel.name }}</h1>
            <div class="hotel-tags">
              <span v-for="t in currentHotel.tags" :key="t">{{ t }}</span>
            </div>
            <div class="hotel-address">📍 {{ currentHotel.city }} · {{ currentHotel.address }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="booking-section" ref="bookingRef">
      <div class="booking-inner">
        <div class="room-list" ref="roomListRef">
          <div class="room-list-title">选择房型</div>
          <div v-for="room in currentHotel.rooms" :key="room.id" class="room-card" :class="{ selected: selectedRoom?.id === room.id }" @click="selectedRoom = room">
            <div class="room-img">
              <img :src="room.img" :alt="room.name" />
              <div v-if="room.popular" class="room-popular-tag">人气爆款</div>
            </div>
            <div class="room-info">
              <div class="room-name">{{ room.name }}</div>
              <div class="room-facilities">
                <span v-for="f in room.facilities.slice(0, 3)" :key="f">{{ f }}</span>
              </div>
              <div class="room-price">
                <span class="price-num">¥{{ room.price }}</span>
                <span class="price-unit">/晚</span>
              </div>
            </div>
            <div class="room-select-indicator">
              <span v-if="selectedRoom?.id === room.id">✓ 已选</span>
              <span v-else>选择</span>
            </div>
          </div>
        </div>
        <div class="booking-form" ref="bookingFormRef">
          <div class="booking-card">
            <div class="booking-price-header">
              <div class="selected-room-name">{{ selectedRoom?.name || '请先选择房型' }}</div>
              <div class="price-display">
                <span class="big-price">¥{{ selectedRoom?.price || 0 }}</span>
                <span class="price-unit">/晚</span>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">入住日期</label>
              <el-date-picker v-model="checkInDate" type="date" placeholder="选择入住日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled-date="disabledCheckInDate" size="large" style="width: 100%;"></el-date-picker>
            </div>
            <div class="form-group">
              <label class="form-label">退房日期</label>
              <el-date-picker v-model="checkOutDate" type="date" placeholder="选择退房日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled-date="disabledCheckOutDate" size="large" style="width: 100%;"></el-date-picker>
            </div>
            <div v-if="nightCount > 0" class="night-info">共 <b>{{ nightCount }}</b> 晚</div>
            <div class="form-group">
              <label class="form-label">入住人数</label>
              <el-input-number v-model="guestCount" :min="1" :max="selectedRoom?.maxGuest || 10" size="large"></el-input-number>
            </div>
            <div v-if="nightCount > 0 && selectedRoom" class="price-breakdown">
              <div class="breakdown-row">
                <span>¥{{ selectedRoom.price }} × {{ nightCount }}晚</span>
                <span>¥{{ (selectedRoom.price * nightCount).toLocaleString() }}</span>
              </div>
              <div class="breakdown-row">
                <span>服务费</span>
                <span>¥{{ serviceFee }}</span>
              </div>
              <div class="breakdown-total">
                <span>合计</span>
                <span class="total-num">¥{{ totalPrice.toLocaleString() }}</span>
              </div>
            </div>
            <button class="book-btn" :disabled="!canBook" @click="handleBook">{{ canBook ? '立即预订' : '请选择日期和房型' }}</button>
            <div class="hotel-facilities">
              <div class="facilities-title">酒店设施</div>
              <div class="facilities-tags">
                <span v-for="f in currentHotel.facilities" :key="f">{{ f }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="hotel-intro-section" ref="introSectionRef">
      <div class="intro-inner">
        <div class="intro-label">关于酒店</div>
        <h2 ref="introTitleRef">{{ currentHotel.name }}</h2>
        <p class="intro-desc" ref="introDescRef">{{ currentHotel.description }}</p>
        <div class="intro-gallery" ref="galleryRef">
          <div v-for="(img, i) in currentHotel.images.slice(0, 4)" :key="i" class="gallery-item" :class="{ wide: i === 0 }" @click="carouselIdx = i">
            <img :src="img" alt="" />
          </div>
        </div>
      </div>
    </div>

    <div class="reviews-section" ref="reviewsSectionRef">
      <div class="reviews-inner">
        <div class="reviews-header">
          <div class="reviews-title">住客点评</div>
          <div class="reviews-summary">
            <span class="summary-score">{{ currentHotel.score }}</span>
            <div class="summary-info">
              <div class="summary-stars">{{ '⭐'.repeat(Math.round(currentHotel.score)) }}</div>
              <div class="summary-count">{{ currentHotel.commentCount }}条评价</div>
            </div>
          </div>
        </div>
        <div class="reviews-list" ref="reviewsListRef">
          <div v-for="review in currentHotel.reviews" :key="review.id" class="review-card">
            <div class="review-header">
              <div class="reviewer-avatar">{{ review.nickname.slice(0, 1) }}</div>
              <div class="reviewer-info">
                <div class="reviewer-name">{{ review.nickname }}</div>
                <div class="reviewer-meta">{{ review.roomType }} · {{ review.date }}</div>
              </div>
              <div class="review-score">⭐ {{ review.score }}</div>
            </div>
            <div class="review-body">{{ review.content }}</div>
            <div v-if="review.images?.length" class="review-images">
              <img v-for="(img, i) in review.images" :key="i" :src="img" alt="" />
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
