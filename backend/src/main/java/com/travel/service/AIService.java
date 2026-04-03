package com.travel.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.*;
import java.time.*;
import java.time.format.*;

@Service
public class AIService {

    /**
     * 根据用户消息和历史获取AI回复
     */
    public String getReply(String message, List<Map<String, String>> history) {
        String msg = message.trim().toLowerCase();
        StringBuilder context = new StringBuilder();
        
        // 构建上下文（最近3轮对话）
        int start = Math.max(0, history.size() - 3);
        for (int i = start; i < history.size(); i++) {
            Map<String, String> turn = history.get(i);
            context.append("用户: ").append(turn.get("user")).append("\n");
            context.append("小陈: ").append(turn.get("assistant")).append("\n");
        }

        // 意图识别
        // 智能检测：有目的地 + 天数 = 行程规划
        boolean hasDays = Pattern.compile("(\\d+)天").matcher(msg).find();
        boolean hasDestination = extractDestination(message) != null;
        if (hasDestination && hasDays) {
            return buildTripPlan(message, context.toString());
        }
        if (msg.contains("规划") || msg.contains("安排") || msg.contains("行程") || msg.contains("几天") || msg.contains("亲子游") || msg.contains("蜜月") || msg.contains("自由行") || msg.contains("自驾游") || msg.contains("穷游") || msg.contains("度假")) {
            return buildTripPlan(message, context.toString());
        } else if (msg.contains("美食") || msg.contains("好吃") || msg.contains("餐厅") || msg.contains("吃什么")) {
            return buildFoodRecommend(message);
        } else if (msg.contains("酒店") || msg.contains("住宿") || msg.contains("住哪里")) {
            return buildHotelRecommend(message);
        } else if (msg.contains("景点") || msg.contains("好玩")) {
            return buildAttractionRecommend(message);
        } else if (msg.contains("交通") || msg.contains("怎么去") || msg.contains("出行")) {
            return buildTransportAdvice(message);
        } else if (msg.contains("预算") || msg.contains("多少钱") || msg.contains("花费")) {
            return buildBudgetAdvice(message);
        } else if (msg.contains("季节") || msg.contains("天气") || msg.contains("什么时候去")) {
            return buildSeasonAdvice(message);
        } else if (msg.contains("谢谢") || msg.contains("太好了") || msg.contains("帮了我大忙")) {
            return "很高兴能帮到你！祝你的旅行愉快~ 🌴 如果还有其他问题，随时问小陈 😊";
        } else if (msg.contains("你好") || msg.contains("您好") || msg.startsWith("hi") || msg.startsWith("hello")) {
            return greet(message);
        } else {
            return buildGeneralResponse(message, context.toString());
        }
    }

    private String greet(String message) {
        String g = "你好！我是小陈，你的专属旅行规划助手 🤖\n\n" +
                   "我可以帮你：\n" +
                   "🗓️ 制定详细行程计划\n" +
                   "🍜 推荐当地美食\n" +
                   "🏨 建议住宿区域\n" +
                   "🎯 规划景点游览路线\n" +
                   "💰 估算旅行预算\n\n" +
                   "告诉我你想去哪里、玩几天、有什么偏好，我来帮你安排！";
        return g;
    }

    private String buildTripPlan(String message, String context) {
        // 提取目的地和天数
        String destination = extractDestination(message);
        int days = extractDays(message);
        
        if (destination == null) {
            return "规划行程需要知道目的地呢 📍\n\n" +
                   "请告诉我：\n" +
                   "• 你想去哪里旅行？\n" +
                   "• 计划玩几天？\n" +
                   "• 有特别的偏好吗（亲子游/情侣/独自旅行/休闲/打卡）\n\n" +
                   "比如：「我想去云南玩5天，推荐一下行程」";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📍 **").append(destination).append("攻略**（").append(days).append("天").append("）\n\n");
        
        // 根据目的地生成行程
        switch (destination) {
            case "云南", "丽江", "大理", "香格里拉", "西双版纳":
                sb.append(buildYunnanPlan(days));
                break;
            case "海南", "三亚", "海口":
                sb.append(buildHainanPlan(days));
                break;
            case "四川", "成都", "九寨沟", "稻城亚丁":
                sb.append(buildSichuanPlan(days));
                break;
            case "浙江", "杭州", "乌镇", "西塘":
                sb.append(buildZhejiangPlan(days));
                break;
            case "北京":
                sb.append(buildBeijingPlan(days));
                break;
            case "上海":
                sb.append(buildShanghaiPlan(days));
                break;
            case "重庆":
                sb.append(buildChongqingPlan(days));
                break;
            default:
                sb.append(buildGenericPlan(destination, days));
        }

        // 预算估算
        sb.append("\n💰 **预算参考**（").append(days).append("天）：\n");
        sb.append("• 经济型：").append(days * 400).append("元/人\n");
        sb.append("• 舒适型：").append(days * 800).append("元/人\n");
        sb.append("• 品质型：").append(days * 1500).append("元/人\n\n");
        sb.append("📌 小贴士：提前预订机票和酒店能省不少钱哦！有任何具体问题欢迎继续问我~");
        
        return sb.toString();
    }

    private String buildYunnanPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌸 **云南").append(days).append("日游推荐**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 昆明 → 滇池/翠湖公园 → 老街\n");
            sb.append("Day2: 丽江古城 → 黑龙潭\n");
        } else if (days <= 4) {
            sb.append("Day1: 昆明 → 翠湖 → 滇池\n");
            sb.append("Day2: 高铁至大理 → 洱海骑行 → 双廊\n");
            sb.append("Day3: 大理古城 → 三塔 → 喜洲古镇\n");
            sb.append("Day4: 大理 → 丽江 → 古城漫游\n");
        } else {
            sb.append("Day1: 昆明 → 翠湖/滇池\n");
            sb.append("Day2: 昆明→丽江 → 丽江古城\n");
            sb.append("Day3: 玉龙雪山一日游（冰川公园+蓝月谷）\n");
            sb.append("Day4: 拉市海+束河古镇\n");
            sb.append("Day5: 泸沽湖（建议住一晚）\n");
            if (days > 5) {
                sb.append("Day6: 香格里拉（松赞林寺+普达措）\n");
                sb.append("Day7: 返程\n");
            }
        }
        
        sb.append("\n🍜 必吃美食：过桥米线、腊排骨、鲜花饼、饵块\n");
        sb.append("🏠 住宿推荐：丽江古城内客栈、大理双廊海景房\n");
        return sb.toString();
    }

    private String buildHainanPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌴 **海南").append(days).append("日游攻略**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 三亚湾 → 天涯海角\n");
            sb.append("Day2: 亚龙湾 → 热带天堂森林公园\n");
        } else if (days <= 4) {
            sb.append("Day1: 抵达三亚 → 三亚湾椰梦长廊\n");
            sb.append("Day2: 蜈支洲岛（建议住岛上看日出）\n");
            sb.append("Day3: 亚龙湾 → 太阳湾公路\n");
            sb.append("Day4: 南山寺 → 天涯海角 → 返程\n");
        } else {
            sb.append("Day1: 三亚湾+第一市场海鲜\n");
            sb.append("Day2: 蜈支洲岛全天\n");
            sb.append("Day3: 亚龙湾+热带天堂森林公园\n");
            sb.append("Day4: 南山寺+天涯海角\n");
            sb.append("Day5: 海口骑楼老街 → 万宁日月湾\n");
            sb.append("Day6: 分界洲岛 → 返程\n");
        }
        
        sb.append("\n🍜 必吃：海鲜（第一市场加工）、椰子鸡、清补凉、热带水果\n");
        sb.append("🏠 住宿：亚龙湾高端度假、三亚湾经济实惠\n");
        return sb.toString();
    }

    private String buildSichuanPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🐼 **四川").append(days).append("日游推荐**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 成都 → 宽窄巷子 → 锦里\n");
            sb.append("Day2: 熊猫基地 → 太古里\n");
        } else if (days <= 4) {
            sb.append("Day1: 成都 → 熊猫基地 → 宽窄巷子\n");
            sb.append("Day2: 峨眉山一日游\n");
            sb.append("Day3: 乐山大佛\n");
            sb.append("Day4: 锦里 → 武侯祠 → 返程\n");
        } else {
            sb.append("Day1: 成都 → 熊猫基地 → 宽窄巷子\n");
            sb.append("Day2: 峨眉山（住山顶看日出）\n");
            sb.append("Day3: 乐山大佛 → 成都\n");
            sb.append("Day4-5: 九寨沟（两天深度游）\n");
            if (days > 5) {
                sb.append("Day6: 黄龙景区\n");
                sb.append("Day7: 返程成都\n");
            }
        }
        
        sb.append("\n🍜 必吃：火锅、串串、冒菜、兔头、甜水面\n");
        sb.append("🏠 住宿：成都选太古里/宽窄附近、九寨沟沟口住宿\n");
        return sb.toString();
    }

    private String buildZhejiangPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏯 **浙江").append(days).append("日游攻略**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 西湖 → 断桥 → 雷峰塔\n");
            sb.append("Day2: 灵隐寺 → 龙井村 → 清河坊\n");
        } else if (days <= 4) {
            sb.append("Day1: 西湖（环湖骑行）\n");
            sb.append("Day2: 灵隐寺 → 龙井村 → 宋城\n");
            sb.append("Day3: 乌镇（东栅+西栅夜景）\n");
            sb.append("Day4: 苏州拙政园 → 平江路\n");
        } else {
            sb.append("Day1: 杭州西湖全天（断桥、白堤、苏堤）\n");
            sb.append("Day2: 灵隐寺 → 龙井村 → 清河坊\n");
            sb.append("Day3: 乌镇西栅（日景+夜景）\n");
            sb.append("Day4: 西塘古镇（汉服拍照超美）\n");
            sb.append("Day5: 苏州 → 拙政园 → 狮子林\n");
            sb.append("Day6: 周庄 → 返程\n");
        }
        
        sb.append("\n🍜 必吃：东坡肉、西湖醋鱼、龙井虾仁、叫化鸡、定胜糕\n");
        sb.append("🏠 住宿：杭州西湖边民宿、乌镇西栅内客栈\n");
        return sb.toString();
    }

    private String buildBeijingPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏛️ **北京").append(days).append("日游攻略**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 天安门 → 故宫 → 王府井\n");
            sb.append("Day2: 长城（八达岭）→ 鸟巢水立方夜景\n");
        } else if (days <= 4) {
            sb.append("Day1: 天安门 → 故宫（需预约）\n");
            sb.append("Day2: 八达岭长城\n");
            sb.append("Day3: 颐和园 → 圆明园\n");
            sb.append("Day4: 天坛 → 什刹海 → 南锣鼓巷\n");
        } else {
            sb.append("Day1: 天安门 → 故宫（深度游，3-4小时）\n");
            sb.append("Day2: 八达岭长城\n");
            sb.append("Day3: 颐和园 → 北大/清华外景\n");
            sb.append("Day4: 天坛 → 雍和宫\n");
            sb.append("Day5: 798艺术区 → 三里屯\n");
            sb.append("Day6: 恭王府 → 什刹海 → 返程\n");
        }
        
        sb.append("\n🍜 必吃：北京烤鸭、铜锅涮肉、卤煮、爆肚、豆汁儿（慎重）\n");
        sb.append("🏠 住宿：二环内地铁沿线（前门/东四）\n");
        sb.append("⚠️ 故宫需提前7天在故宫博物院公众号预约！\n");
        return sb.toString();
    }

    private String buildShanghaiPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌆 **上海").append(days).append("日游攻略**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 外滩 → 豫园城隍庙 → 南京路\n");
            sb.append("Day2: 田子坊 → 新天地 → 陆家嘴\n");
        } else {
            sb.append("Day1: 外滩万国建筑群 → 豫园 → 南京路步行街\n");
            sb.append("Day2: 陆家嘴（东方明珠/上海中心）→ 世纪公园\n");
            sb.append("Day3: 田子坊 → 新天地 → 徐家汇\n");
            sb.append("Day4: 迪士尼乐园（建议全天）\n");
            if (days > 4) {
                sb.append("Day5: 朱家角古镇 → 七宝古镇\n");
            }
        }
        
        sb.append("\n🍜 必吃：生煎、小笼包、蟹壳黄、红烧肉、葱油拌面\n");
        sb.append("🏠 住宿：南京路/外滩附近交通便利\n");
        return sb.toString();
    }

    private String buildChongqingPlan(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌶️ **重庆").append(days).append("日游攻略**\n\n");
        
        if (days <= 2) {
            sb.append("Day1: 解放碑 → 洪崖洞 → 千厮门大桥\n");
            sb.append("Day2: 长江索道 → 磁器口 → 鹅岭二厂\n");
        } else if (days <= 4) {
            sb.append("Day1: 解放碑 → 长江索道 → 南山一棵树夜景\n");
            sb.append("Day2: 武隆天生三桥（变形金刚取景地）\n");
            sb.append("Day3: 磁器口 → 白公馆 → 渣滓洞\n");
            sb.append("Day4: 鹅岭二厂 → 李子坝轻轨穿楼 → 返程\n");
        } else {
            sb.append("Day1: 解放碑 → 洪崖洞（夜景）\n");
            sb.append("Day2: 武隆天生三桥+仙女山\n");
            sb.append("Day3: 磁器口 → 白公馆 → 渣滓洞\n");
            sb.append("Day4: 长江索道 → 南山一棵树\n");
            sb.append("Day5: 大足石刻一日游\n");
            sb.append("Day6: 鹅岭公园 → 李子坝 → 返程\n");
        }
        
        sb.append("\n🍜 必吃：火锅（瓜西西/珮姐）、小面、酸辣粉、冰粉、抄手\n");
        sb.append("🏠 住宿：解放碑/洪崖洞附近、观音桥\n");
        return sb.toString();
    }

    private String buildGenericPlan(String destination, int days) {
        return "🌟 **" + destination + " " + days + "日游推荐行程**\n\n" +
               "Day1: 抵达 " + destination + " → 入住酒店 → 逛当地老街/夜市\n" +
               "Day2: 游览" + destination + "最著名的景点（建议提前查好门票）\n" +
               "Day3: 深度游：特色景区 + 当地体验\n" +
               "Day4: 周边一日游或城市休闲游\n" +
               "Day5+: 根据兴趣灵活安排（博物馆/自然风光/购物）\n\n" +
               "📌 温馨提示：\n" +
               "• 提前查好景区开放时间和门票预约要求\n" +
               "• 关注" + destination + "天气，准备好衣物\n" +
               "• 试试当地的特色美食和菜市场，那是旅行的灵魂\n\n" +
               "告诉我更具体的信息（如：亲子游/情侣/独自旅行/季节偏好），我可以给你更精准的建议！";
    }

    private String buildFoodRecommend(String message) {
        String dest = extractDestination(message);
        if (dest == null) {
            dest = "当地";
        }
        
        String[][] foodMap = {
            {"云南", "过桥米线、腊排骨火锅、鲜花饼、烤乳扇、饵块、野生菌"},
            {"海南", "海鲜（皮皮虾/龙虾/石斑鱼）、椰子鸡、清补凉、热带水果（芒果/莲雾）"},
            {"四川", "火锅、串串、冒菜、兔头、甜水面、钟水饺、龙抄手"},
            {"浙江", "东坡肉、西湖醋鱼、龙井虾仁、宋嫂鱼羹、叫化鸡、片儿川"},
            {"北京", "北京烤鸭、铜锅涮肉、卤煮、爆肚、艾窝窝、驴打滚"},
            {"上海", "生煎、小笼包、蟹壳黄、红烧肉、葱油拌面、腌笃鲜"},
            {"重庆", "重庆火锅、小面、酸辣粉、冰粉、抄手、磁器口麻花"},
            {"广州", "早茶（虾饺/凤爪/叉烧包）、烧味、白切鸡、艇仔粥、肠粉"},
            {"西安", "肉夹馍、羊肉泡馍、凉皮、Biangbiang面、葫芦鸡"},
            {"厦门", "沙茶面、土笋冻、烧肉粽、花生汤、扁食、海蛎煎"}
        };
        
        StringBuilder sb = new StringBuilder();
        sb.append("🍜 **").append(dest).append("必吃美食**\n\n");
        
        boolean found = false;
        // 城市名映射到省份
        String mappedDest = dest;
        if (dest.equals("成都")) mappedDest = "四川";
        else if (dest.equals("杭州")) mappedDest = "浙江";
        else if (dest.equals("三亚") || dest.equals("海口")) mappedDest = "海南";
        else if (dest.equals("丽江") || dest.equals("大理")) mappedDest = "云南";
        else if (dest.equals("北京")) mappedDest = "北京";
        else if (dest.equals("上海")) mappedDest = "上海";
        else if (dest.equals("重庆")) mappedDest = "重庆";
        else if (dest.equals("西安")) mappedDest = "西安";
        else if (dest.equals("广州")) mappedDest = "广州";
        else if (dest.equals("厦门")) mappedDest = "厦门";
        else if (dest.equals("青岛")) mappedDest = "青岛";
        else if (dest.equals("南京")) mappedDest = "南京";
        else if (dest.equals("武汉")) mappedDest = "武汉";
        else if (dest.equals("长沙")) mappedDest = "长沙";
        
        for (String[] pair : foodMap) {
            if (mappedDest.contains(pair[0]) || pair[0].contains(mappedDest) || mappedDest.equals(pair[0])) {
                sb.append("• ").append(pair[1]).append("\n\n");
                sb.append("📍 觅食好去处：\n");
                switch (pair[0]) {
                    case "云南": sb.append("昆明：南屏街、大理：古城人民路、丽江：古城五一街\n"); break;
                    case "四川": sb.append("成都：锦里、宽窄巷子、建设路小吃街\n"); break;
                    case "重庆": sb.append("解放碑好吃街、观音桥、九街\n"); break;
                    case "海南": sb.append("三亚：第一市场、海口：骑楼老街\n"); break;
                    case "北京": sb.append("簋街、王府井小吃街、南锣鼓巷\n"); break;
                    default: sb.append("当地菜市场/夜市是灵魂！问当地人最靠谱 😄\n"); break;
                }
                found = true;
                break;
            }
        }
        
        if (!found) {
            sb.append("• 当地特色美食（建议问当地人或查阅美食攻略）\n\n");
            sb.append("💡 找到地道美食的小技巧：\n");
            sb.append("1. 远离景区，步行10分钟到附近居民区\n");
            sb.append("2. 打开地图，看哪家餐厅本地车多\n");
            sb.append("3. 大众点评「当地人常吃」筛选\n");
            sb.append("4. 菜市场附近的店通常很地道！\n");
        }
        
        return sb.toString();
    }

    private String buildHotelRecommend(String message) {
        String dest = extractDestination(message);
        if (dest == null) dest = "当地";
        
        return "🏨 **" + dest + "住宿选择指南**\n\n" +
               "选择住宿主要看这3个因素：\n\n" +
               "📍 **位置建议**\n" +
               "• 第一次去：住市中心或景区附近，出行方便\n" +
               "• 深度游：住当地特色民宿，体验风土人情\n" +
               "• 海边/度假：选海景房或一线海景，早起看日出超美\n\n" +
               "💰 **价位参考**（每晚）\n" +
               "• 经济型（青旅/快捷）：100-300元\n" +
               "• 舒适型（三星/精品）：300-600元\n" +
               "• 品质型（四星/五星）：600-2000元\n\n" +
               "⚠️ **避坑提示**\n" +
               "• 提前3天以上预订，热门目的地提前1周\n" +
               "• 仔细看评论的差评，有时「位置好」不代表房间好\n" +
               "• 海边/古镇建议住一晚，夜晚的安静和清晨的美景值得\n\n" +
               "告诉我你的目的地和预算，我帮你精确推荐！";
    }

    private String buildAttractionRecommend(String message) {
        String dest = extractDestination(message);
        if (dest == null) dest = "当地";
        
        StringBuilder sb = new StringBuilder();
        sb.append("🎯 **").append(dest).append("景点推荐**\n\n");
        sb.append("我来帮你筛选最值得去的景点：\n\n");
        sb.append("请告诉我：\n");
        sb.append("🧳 你喜欢什么类型？\n");
        sb.append("  • 自然风光（山水/海岛/森林）\n");
        sb.append("  • 人文历史（古迹/博物馆/古镇）\n");
        sb.append("  • 城市打卡（地标/网红/购物）\n");
        sb.append("  • 亲子互动（动物园/游乐园）\n\n");
        sb.append("⏰ 计划几天？\n");
        sb.append("🚌 出行方式（自驾/公共交通/跟团）\n\n");
        sb.append("给我更多信息，小陈帮你定制专属行程！ 😊");
        
        return sb.toString();
    }

    private String buildTransportAdvice(String message) {
        String dest = extractDestination(message);
        if (dest == null) dest = "当地";
        
        return "🚌 **" + dest + "交通攻略**\n\n" +
               "**外部交通**\n" +
               "• 飞机：查一下有没有直飞，高铁通达城市优先选高铁\n" +
               "• 高铁：提前15天放票，旺季秒没，G/D字头速度快\n\n" +
               "**市内交通**\n" +
               "• 地铁：最靠谱的公共交通，下载「Metro大都会」或亿通行\n" +
               "• 公交：短途很方便，支付宝/微信刷乘车码\n" +
               "• 打车：滴滴/高德比出租车便宜，记得勾选一口价\n" +
               "• 共享单车：短途超方便，美团/哈啰/青桔\n\n" +
               "⚠️ **小陈提醒**\n" +
               "• 旺季景区停车难，不建议自驾\n" +
               "• 山区景点（如九寨沟）跟一日游团反而更省心\n" +
               "• 带老人小孩包车游是性价比最高的选择\n\n" +
               "告诉我你的具体行程，我帮你规划最优路线！";
    }

    private String buildBudgetAdvice(String message) {
        String dest = extractDestination(message);
        int days = extractDays(message);
        if (dest == null) dest = "普通目的地";
        if (days == 0) days = 3;
        
        int low = days * 400;
        int mid = days * 800;
        int high = days * 1500;
        
        return "💰 **" + dest + " " + days + "天预算参考**\n\n" +
               "**经济型（" + low + "元/人）**\n" +
               "• 交通：绿皮火车 + 公交/共享单车\n" +
               "• 住宿：青旅/经济型酒店（100-200元/晚）\n" +
               "• 吃饭：当地小吃+快餐（30-50元/顿）\n" +
               "• 门票：买联票或免费景点为主\n\n" +
               "**舒适型（" + mid + "元/人）**\n" +
               "• 交通：高铁/打折机票 + 打车/包车\n" +
               "• 住宿：三星酒店或精品民宿（200-500元/晚）\n" +
               "• 吃饭：品尝当地代表餐厅（80-150元/顿）\n" +
               "• 门票：核心景点全覆盖\n\n" +
               "**品质型（" + high + "元/人）**\n" +
               "• 交通：飞机公务舱/高铁特等座 + 包车\n" +
               "• 住宿：四星以上酒店/海景房/精品度假村\n" +
               "• 吃饭：米其林/特色餐厅任性吃\n" +
               "• 体验：VIP景区专团、特色体验活动\n\n" +
               "📌 省钱技巧：提前1个月订机票和酒店，能省30-50%！";
    }

    private String buildSeasonAdvice(String message) {
        return "🌤️ **出行季节选择指南**\n\n" +
               "**春季（3-5月）🌸**\n" +
               "• 云南：油菜花、樱花盛开最佳\n" +
               "• 江南：水乡古镇春意盎然\n" +
               "• 北方：天气回暖，沙漠/草原开始绿了\n\n" +
               "**夏季（6-8月）☀️**\n" +
               "• 贵州/云南：避暑圣地，平均25°C\n" +
               "• 西北：青海湖、敦煌、318自驾\n" +
               "• 海边：海南、青岛、厦门（旺季！提前订）\n" +
               "⚠️ 注意：7-8月全国普遍高温，避开重庆/杭州等火炉城市\n\n" +
               "**秋季（9-11月）🍂**\n" +
               "• 西北：胡杨林、额济纳旗（金秋绝美）\n" +
               "• 川西/稻城亚丁：秋色天花板\n" +
               "• 北京：香山红叶、故宫银杏\n" +
               "• 婺源：晒秋、篁岭晒秋节\n\n" +
               "**冬季（12-2月）❄️**\n" +
               "• 东北：冰雪大世界、滑雪、雪乡\n" +
               "• 云南/海南：避寒首选\n" +
               "• 川西：温泉+雪山（小众秘境）\n" +
               "• 哈尔滨：冰雪节（1-2月）\n\n" +
               "📌 告诉小陈你计划的出发时间，我给你推荐最适合的目的地！";
    }

    private String buildGeneralResponse(String message, String context) {
        return "😊 嗯嗯，小陈听明白了！\n\n" +
               "作为你的旅行规划助手，我可以帮你：\n\n" +
               "🗓️ **制定行程** — 告诉我目的地+天数，帮你安排每一天\n" +
               "🍜 **推荐美食** — 当地人私藏的馆子，少走弯路\n" +
               "🏨 **住宿建议** — 根据预算推荐最佳位置和酒店\n" +
               "🎫 **景点攻略** — 哪些值得去、哪些是坑、怎么购票\n" +
               "💰 **预算规划** — 帮你算好每一分钱\n\n" +
               "💬 试着这样问我：\n" +
               "• 「云南5天4晚推荐行程」\n" +
               "• 「三亚有什么好吃的」\n" +
               "• 「去北京玩3天需要多少预算」\n\n" +
               "或者直接说你的旅行想法，我们一起来规划！";
    }

    // ============ 工具方法 ============

    private String extractDestination(String text) {
        String[][] known = {
            {"云南", "丽江", "大理", "香格里拉", "西双版纳", "昆明", "滇池", "石林"},
            {"海南", "三亚", "海口", "万宁", "琼海", "文昌"},
            {"四川", "成都", "九寨沟", "稻城", "亚丁", "峨眉山", "乐山", "峨眉", "川西"},
            {"浙江", "杭州", "乌镇", "西塘", "千岛湖", "宁波", "温州", "绍兴", "莫干山", "乌镇", "宁波"},
            {"北京", "故宫", "长城", "天安门", "颐和园", "天坛"},
            {"上海", "外滩", "迪士尼", "豫园", "田子坊", "陆家嘴"},
            {"重庆", "洪崖洞", "解放碑", "磁器口", "武隆", "长江"},
            {"广州", "长隆", "白云山", "沙面", "小蛮腰"},
            {"深圳", "华侨城", "大小梅沙", "世界之窗"},
            {"西安", "兵马俑", "回民街", "大雁塔", "华山"},
            {"厦门", "鼓浪屿", "曾厝垵", "中山路", "集美"},
            {"青岛", "崂山", "栈桥", "金沙滩", "八大关"},
            {"苏州", "拙政园", "平江路", "周庄", "同里", "虎丘"},
            {"南京", "夫子庙", "中山陵", "秦淮河", "总统府"},
            {"武汉", "黄鹤楼", "东湖", "户部巷", "武大"},
            {"长沙", "橘子洲", "岳麓山", "太平街", "五一广场"},
            {"桂林", "漓江", "阳朔", "龙脊梯田", "象山"},
            {"贵州", "黄果树", "西江千户苗寨", "荔波", "梵净山"},
            {"西藏", "拉萨", "布达拉宫", "纳木措", "林芝", "羊湖", "珠峰"},
            {"青海", "青海湖", "茶卡盐湖", "敦煌", "可可西里"},
            {"新疆", "天山", "喀纳斯", "伊犁", "赛里木湖", "独库公路"}
        };

        String lower = text.toLowerCase();
        
        // 先尝试精确匹配目的地名
        for (String[] group : known) {
            // 先检查省份名（group[0]）是否在文本中
            if (lower.contains(group[0].toLowerCase())) {
                // 返回省份
                return group[0];
            }
            // 再检查各城市名
            for (int i = 1; i < group.length; i++) {
                if (lower.contains(group[i].toLowerCase())) {
                    // 如果匹配到的是省名下面的城市，返回城市
                    if (group[0].equals("云南") && (group[i].equals("丽江") || group[i].equals("大理"))) return group[i];
                    if (group[0].equals("海南") && group[i].equals("三亚")) return "三亚";
                    if (group[0].equals("四川") && (group[i].equals("成都") || group[i].equals("九寨沟"))) return group[i];
                    if (group[0].equals("浙江") && group[i].equals("杭州")) return "杭州";
                    if (group[0].equals("北京")) return "北京";
                    if (group[0].equals("上海")) return "上海";
                    if (group[0].equals("重庆")) return "重庆";
                    if (group[0].equals("陕西") && group[i].equals("西安")) return "西安";
                    // 返回省份作为目的地
                    return group[0];
                }
            }
        }
        
        // 通用目的地关键词
        if (lower.contains("旅行") || lower.contains("旅游") || lower.contains("玩")) {
            // 提取「去+xxx」的模式
            Pattern p = Pattern.compile("去([\\u4e00-\\u9fa5]{2,5})");
            Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group(1);
            }
        }
        
        return null;
    }

    private int extractDays(String text) {
        // 匹配「X天X夜」模式
        Pattern p = Pattern.compile("(\\d+)天(\\d+)夜");
        Matcher m = p.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));
        
        // 匹配「X天」或「X天X晚」模式
        p = Pattern.compile("(\\d+)天");
        m = p.matcher(text);
        if (m.find()) return Integer.parseInt(m.group(1));
        
        // 匹配「几天」等模糊表达
        if (text.contains("几天") || text.contains("一周")) return 5;
        if (text.contains("周末") || text.contains("2天1夜")) return 2;
        if (text.contains("短途") || text.contains("三天两夜")) return 3;
        
        return 3; // 默认3天
    }
}
