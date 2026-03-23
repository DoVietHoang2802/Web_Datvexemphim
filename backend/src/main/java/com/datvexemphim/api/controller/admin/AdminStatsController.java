package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.RevenueStatsResponse;
import com.datvexemphim.service.admin.AdminStatsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin
public class AdminStatsController {
    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/revenue")
    public RevenueStatsResponse revenue() {
        return new RevenueStatsResponse(adminStatsService.totalRevenue());
    }
}

