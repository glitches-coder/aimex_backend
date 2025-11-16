package com.aimex.backend.controller;


import com.aimex.backend.models.User;
import com.aimex.backend.repository.UserRepository;
import com.aimex.backend.service.AnalyticsService;
import com.aimex.backend.service.dto.CategoryBreakdownDTO;
import com.aimex.backend.service.dto.InsightDTO;
import com.aimex.backend.service.dto.MonthlySummaryDTO;
import com.aimex.backend.service.dto.TrendPointDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/aimex/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService, UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    // 1️⃣ Monthly Summary
    @GetMapping("/{userId}/monthly")
    public MonthlySummaryDTO getMonthlySummary(@PathVariable("userId")  String userId) {
        return analyticsService.getMonthlySummary(userId);
    }

    // 2️⃣ Category Breakdown
    @GetMapping("/{userId}/category")
    public List<CategoryBreakdownDTO> getCategoryBreakdown(@PathVariable("userId")  String userId) {
        return analyticsService.getCategoryBreakdown(userId);
    }

    // 3️⃣ Trends (last 6 months)
    @GetMapping("/{userId}/trends")
    public List<TrendPointDTO> getTrends(@PathVariable("userId")  String userId) {
        return analyticsService.getTrends(userId);
    }

    // 4️⃣ AI Insights (placeholder)
    @GetMapping("/insights")
    public List<InsightDTO> getInsights() {
        return analyticsService.getAIInsights();
    }
}
