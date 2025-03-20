package com.etsyautomation.services;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", 125);
        stats.put("totalRevenue", 7450.50);
        stats.put("topSelling", "Minimalist Poster");
        stats.put("recentOrders", 8);
        return stats;
    }
}
