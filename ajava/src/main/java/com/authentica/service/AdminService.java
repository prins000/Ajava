package com.authentica.service;

import com.authentica.model.AppSettings;
import com.authentica.model.User;
import com.authentica.model.enums.ReportStatus;
import com.authentica.model.enums.TicketStatus;
import com.authentica.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final CommunityReportRepository reportRepository;
    private final SupportTicketRepository ticketRepository;
    private final AppSettingsRepository settingsRepository;

    public AdminService(UserRepository userRepository,
                        AnalysisRepository analysisRepository,
                        CommunityReportRepository reportRepository,
                        SupportTicketRepository ticketRepository,
                        AppSettingsRepository settingsRepository) {
        this.userRepository = userRepository;
        this.analysisRepository = analysisRepository;
        this.reportRepository = reportRepository;
        this.ticketRepository = ticketRepository;
        this.settingsRepository = settingsRepository;
    }

    public Map<String, Object> getAnalyticsOverview() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalUsers", userRepository.count());
        metrics.put("activeUsers", userRepository.countByIsActiveTrue());
        metrics.put("totalScans", analysisRepository.count());
        metrics.put("totalReports", reportRepository.count());
        metrics.put("verifiedReports", reportRepository.countByStatus(ReportStatus.VERIFIED));
        metrics.put("pendingReports", reportRepository.countByStatus(ReportStatus.PENDING));
        metrics.put("openTickets", ticketRepository.countByStatus(TicketStatus.OPEN));
        return metrics;
    }

    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public AppSettings updateSetting(String key, String value, String description, String category) {
        AppSettings setting = settingsRepository.findBySettingKey(key)
                .orElse(new AppSettings(key, value, description, category));
        setting.setSettingValue(value);
        if (description != null) setting.setDescription(description);
        if (category != null) setting.setCategory(category);
        return settingsRepository.save(setting);
    }
}
