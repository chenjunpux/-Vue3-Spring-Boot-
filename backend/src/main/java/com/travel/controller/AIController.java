package com.travel.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.travel.service.AIService;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) body.getOrDefault("history", new ArrayList<>());

        Map<String, Object> result = new HashMap<>();
        if (message == null || message.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "消息不能为空");
            result.put("data", null);
            return result;
        }

        String reply = aiService.getReply(message, history);
        
        result.put("code", 200);
        result.put("msg", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("reply", reply);
        data.put("timestamp", System.currentTimeMillis());
        result.put("data", data);
        
        return result;
    }
}
