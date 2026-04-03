package com.travel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.*;
import com.travel.mapper.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final SpotMapper spotMapper;
    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final ArticleMapper articleMapper;
    private final OrderMapper orderMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, SpotMapper spotMapper, HotelMapper hotelMapper,
                           RoomTypeMapper roomTypeMapper, ArticleMapper articleMapper,
                           OrderMapper orderMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.spotMapper = spotMapper;
        this.hotelMapper = hotelMapper;
        this.roomTypeMapper = roomTypeMapper;
        this.articleMapper = articleMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initSpots();
        initHotels();
        initArticles();
        initOrders();
    }

    private void initUsers() {
        // 管理员账号
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin")) == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("系统管理员");
            admin.setRole(2);
            admin.setStatus(1);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(admin);
            System.out.println("✅ 管理员账号已创建: admin / admin123");
        }

        // 测试用户账号
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, "testuser")) == 0) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setNickname("测试用户");
            user.setPhone("13800138000");
            user.setRole(1);
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
            System.out.println("✅ 测试用户账号已创建: testuser / user123");
        }
    }

    private void initSpots() {
        if (spotMapper.selectCount(null) > 0) return;

        List<Spot> spots = Arrays.asList(
            createSpot("故宫博物院", "https://picsum.photos/seed/spot1/800/600", "北京", "北京市东城区景山前街4号", new BigDecimal("116.397128"), new BigDecimal("39.916527"), "世界上现存规模最大、保存最为完整的木质结构古建筑之一", new BigDecimal("60"), "08:30-17:00", 4, "5A", "历史,文化,博物馆", 9800, 125600),
            createSpot("杭州西湖", "https://picsum.photos/seed/spot2/800/600", "杭州", "浙江省杭州市西湖区", new BigDecimal("120.155070"), new BigDecimal("30.246359"), "世界文化遗产，以秀丽的湖光山色和众多名胜古迹闻名", new BigDecimal("0"), "全天开放", 6, "5A", "自然,湖泊,休闲", 9500, 98000),
            createSpot("黄山风景区", "https://picsum.photos/seed/spot3/800/600", "黄山", "安徽省黄山市黄山区", new BigDecimal("118.157677"), new BigDecimal("30.128417"), "以奇松、怪石、云海、温泉著称于世", new BigDecimal("230"), "06:30-17:30", 1, "5A", "山岳,自然,摄影", 9200, 87000),
            createSpot("厦门鼓浪屿", "https://picsum.photos/seed/spot4/800/600", "厦门", "福建省厦门市思明区", new BigDecimal("118.061722"), new BigDecimal("24.446483"), "世界文化遗产，享有海上花园的美誉", new BigDecimal("0"), "全天开放", 8, "5A", "海岛,文艺,历史", 8900, 78000),
            createSpot("成都大熊猫基地", "https://picsum.photos/seed/spot5/800/600", "成都", "四川省成都市成华区", new BigDecimal("104.143534"), new BigDecimal("30.739060"), "世界著名的大熊猫迁地保护基地", new BigDecimal("58"), "07:30-18:00", 4, "4A", "动物,亲子,科普", 8600, 72000),
            createSpot("张家界国家森林公园", "https://picsum.photos/seed/spot6/800/600", "张家界", "湖南省张家界市武陵源区", new BigDecimal("110.449902"), new BigDecimal("29.117040"), "中国第一个国家森林公园，以独特的石英砂岩峰林地貌著称", new BigDecimal("248"), "07:00-18:00", 2, "5A", "自然,山岳,探险", 8400, 69000),
            createSpot("丽江古城", "https://picsum.photos/seed/spot7/800/600", "丽江", "云南省丽江市古城区", new BigDecimal("100.235532"), new BigDecimal("26.875022"), "世界文化遗产，是中国历史文化名城中保存最为完整的古城之一", new BigDecimal("0"), "全天开放", 2, "5A", "古城,民族,夜生活", 8100, 65000),
            createSpot("秦始皇兵马俑", "https://picsum.photos/seed/spot8/800/600", "西安", "陕西省西安市临潼区", new BigDecimal("109.278372"), new BigDecimal("34.384414"), "世界第八大奇迹", new BigDecimal("150"), "08:30-18:30", 3, "5A", "历史,文化,博物馆", 8800, 81000)
        );

        for (Spot spot : spots) {
            spot.setCreatedAt(LocalDateTime.now());
            spot.setUpdatedAt(LocalDateTime.now());
            spotMapper.insert(spot);
        }
        System.out.println("✅ 景点数据已初始化 (8条)");
    }

    private Spot createSpot(String name, String cover, String city, String address,
                            BigDecimal lng, BigDecimal lat, String desc,
                            BigDecimal price, String openTime, Integer suggested, String level, String tags, Integer hot, Integer view) {
        Spot s = new Spot();
        s.setName(name); s.setCoverImage(cover); s.setCity(city); s.setAddress(address);
        s.setLongitude(lng); s.setLatitude(lat); s.setDescription(desc);
        s.setTicketPrice(price); s.setOpenTime(openTime); s.setSuggestedTime(suggested);
        s.setLevel(level); s.setTags(tags); s.setHotScore(hot); s.setViewCount(view); s.setStatus(1);
        return s;
    }

    private void initHotels() {
        if (hotelMapper.selectCount(null) > 0) return;

        List<Hotel> hotels = Arrays.asList(
            createHotel("北京华尔道夫酒店", "https://picsum.photos/seed/hotel1/800/600", "北京", "北京市东城区金宝街8号", new BigDecimal("116.415150"), new BigDecimal("39.918234"), "位于王府井商圈，奢华五星级酒店", 5, "WiFi,游泳池,健身房,餐厅,酒吧", 9200),
            createHotel("上海外滩悦榕庄", "https://picsum.photos/seed/hotel2/800/600", "上海", "上海市虹口区海平路19号", new BigDecimal("121.491900"), new BigDecimal("31.245680"), "坐拥黄浦江迷人景致，顶级都市度假酒店", 5, "WiFi,江景房,SPA,健身房", 9000),
            createHotel("杭州西子湖四季酒店", "https://picsum.photos/seed/hotel3/800/600", "杭州", "浙江省杭州市西湖区", new BigDecimal("120.146890"), new BigDecimal("30.267120"), "紧邻西湖，园林式度假酒店", 5, "WiFi,湖景房,游泳池,SPA,茶室", 8800),
            createHotel("成都瑞吉酒店", "https://picsum.photos/seed/hotel4/800/600", "成都", "四川省成都市锦江区太升南路88号", new BigDecimal("104.078920"), new BigDecimal("30.675410"), "春熙路核心商圈，精致奢华五星级酒店", 5, "WiFi,健身房,游泳池,餐厅", 8500),
            createHotel("厦门海悦山庄", "https://picsum.photos/seed/hotel5/800/600", "厦门", "福建省厦门市思明区环岛南路3999号", new BigDecimal("118.142320"), new BigDecimal("24.468260"), "临海度假酒店，花园式环境，私人沙滩", 5, "WiFi,海景房,私人沙滩,游泳池", 8700),
            createHotel("张家界京武铂尔曼", "https://picsum.photos/seed/hotel6/800/600", "张家界", "湖南省张家界市永定区", new BigDecimal("110.473520"), new BigDecimal("29.128230"), "距离天门山景区仅10分钟车程", 4, "WiFi,山景房,健身房,餐厅", 8200)
        );

        for (Hotel hotel : hotels) {
            hotel.setCreatedAt(LocalDateTime.now());
            hotel.setUpdatedAt(LocalDateTime.now());
            hotelMapper.insert(hotel);
        }

        // 插入房型
        long[][] roomData = { {1, 1888, 15}, {1, 2288, 10}, {2, 2688, 20}, {2, 2988, 15}, {3, 1588, 12}, {4, 1288, 18}, {5, 1888, 16} };
        String[] roomNames = {"豪华大床房", "行政双床房", "江景大床房", "江景双床房", "园林大床房", "豪华大床房", "海景大床房"};
        String[] bedTypes = {"大床1.8m", "双床1.2m", "大床1.8m", "双床1.3m", "大床1.8m", "大床1.8m", "大床1.8m"};
        String[] amenities = {"免费WiFi,含双早,迷你吧", "免费WiFi,含双早,行政酒廊", "免费WiFi,含双早,江景浴缸",
            "免费WiFi,含双早,行政酒廊", "免费WiFi,含双早,庭院景观", "免费WiFi,含双早", "免费WiFi,含双早,海景阳台"};

        for (int i = 0; i < roomData.length; i++) {
            RoomType rt = new RoomType();
            rt.setHotelId(Long.valueOf((long)roomData[i][0]));
            rt.setName(roomNames[i]);
            rt.setPrice(new BigDecimal((int)roomData[i][1]));
            rt.setBedType(bedTypes[i]);
            rt.setMaxGuest(2);
            rt.setTotalRooms((int)roomData[i][2]);
            rt.setAmenities(amenities[i]);
            rt.setImages("https://picsum.photos/seed/room" + i + "/400/300");
            rt.setCreatedAt(LocalDateTime.now());
            rt.setUpdatedAt(LocalDateTime.now());
            roomTypeMapper.insert(rt);
        }
        System.out.println("✅ 酒店数据已初始化 (6酒店 + 7房型)");
    }

    private Hotel createHotel(String name, String cover, String city, String address,
                               BigDecimal lng, BigDecimal lat, String desc, Integer star, String facilities, Integer hot) {
        Hotel h = new Hotel();
        h.setName(name); h.setCoverImage(cover); h.setCity(city); h.setAddress(address);
        h.setLongitude(lng); h.setLatitude(lat); h.setDescription(desc);
        h.setStarLevel(star); h.setFacilities(facilities); h.setHotScore(hot); h.setStatus(1);
        return h;
    }

    private void initArticles() {
        if (articleMapper.selectCount(null) > 0) return;

        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin == null) return;

        List<Article> articles = Arrays.asList(
            createArticle(admin.getId(), "北京三日深度游攻略｜故宫颐和园长城一次玩遍", "https://picsum.photos/seed/art1/800/600", "这次北京之旅真的太棒了！从故宫的红墙金瓦到长城的巍峨壮丽，每一处都让人震撼。故宫需要提前预约门票，建议早上8点半开门就进去，人少体验更好。长城推荐去八达岭，有缆车上下比较轻松。", "北京,故宫,长城,攻略", 8560, 324, 56, 1, 1),
            createArticle(admin.getId(), "杭州西湖边的慢生活｜三天两夜治愈之旅", "https://picsum.photos/seed/art2/800/600", "杭州是一座让人来了就不想走的城市，西湖边的清晨太美好了。建议住在西湖边的酒店，早上6点起来沿着苏堤走走，几乎没有游客。雷峰塔下午去看夕阳非常浪漫。", "杭州,西湖,慢生活,美食", 6230, 256, 38, 1, 0),
            createArticle(admin.getId(), "张家界天门山探险｜玻璃栈道真的腿软了", "https://picsum.photos/seed/art3/800/600", "天门山的玻璃栈道真的不是一般的刺激！整理了一份详细的攻略分享给大家。门票建议提前在网上预订，比现场便宜。山上天气多变，带好雨具和外套。", "张家界,天门山,探险,玻璃栈道", 4920, 189, 27, 1, 0)
        );

        for (Article article : articles) {
            article.setCreatedAt(LocalDateTime.now());
            article.setUpdatedAt(LocalDateTime.now());
            articleMapper.insert(article);
        }
        System.out.println("✅ 游记数据已初始化 (3条)");
    }

    private Article createArticle(Long userId, String title, String cover, String content, String tags, Integer view, Integer like, Integer comment, Integer status, Integer isTop) {
        Article a = new Article();
        a.setUserId(userId); a.setTitle(title); a.setCoverImage(cover); a.setContent(content);
        a.setTags(tags); a.setViewCount(view); a.setLikeCount(like); a.setCommentCount(comment);
        a.setStatus(status); a.setIsTop(isTop);
        return a;
    }

    private void initOrders() {
        if (orderMapper.selectCount(null) > 0) return;

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "testuser"));
        if (user == null) return;

        Order o1 = new Order();
        o1.setOrderNo("ORD202604010001"); o1.setUserId(user.getId()); o1.setOrderType(1);
        o1.setTargetId(1L); o1.setTargetName("故宫博物院");
        o1.setTotalAmount(new BigDecimal("120")); o1.setPayAmount(new BigDecimal("120"));
        o1.setStatus(2); o1.setPayTime(LocalDateTime.now().minusDays(1));
        o1.setPayChannel("wechat"); o1.setContactName("测试用户"); o1.setContactPhone("13800138000");
        o1.setQuantity(2); o1.setCreatedAt(LocalDateTime.now().minusDays(1)); o1.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(o1);

        Order o2 = new Order();
        o2.setOrderNo("ORD202604010002"); o2.setUserId(user.getId()); o2.setOrderType(1);
        o2.setTargetId(2L); o2.setTargetName("杭州西湖");
        o2.setTotalAmount(new BigDecimal("0")); o2.setPayAmount(new BigDecimal("0"));
        o2.setStatus(2); o2.setPayTime(LocalDateTime.now().minusDays(1));
        o2.setContactName("测试用户"); o2.setContactPhone("13800138000");
        o2.setQuantity(1); o2.setCreatedAt(LocalDateTime.now().minusDays(1)); o2.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(o2);

        System.out.println("✅ 订单数据已初始化 (2条)");
    }
}
