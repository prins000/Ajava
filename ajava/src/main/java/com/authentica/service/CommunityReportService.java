package com.authentica.service;

import com.authentica.dto.CommunityReportDto;
import com.authentica.model.CommunityReport;
import com.authentica.model.FraudNumber;
import com.authentica.model.FraudURL;
import com.authentica.model.enums.ReportStatus;
import com.authentica.repository.CommunityReportRepository;
import com.authentica.repository.FraudNumberRepository;
import com.authentica.repository.FraudURLRepository;
import com.authentica.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityReportService {

    private final CommunityReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FraudNumberRepository fraudNumberRepository;
    private final FraudURLRepository fraudURLRepository;

    public CommunityReportService(CommunityReportRepository reportRepository,
                                  UserRepository userRepository,
                                  FraudNumberRepository fraudNumberRepository,
                                  FraudURLRepository fraudURLRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.fraudNumberRepository = fraudNumberRepository;
        this.fraudURLRepository = fraudURLRepository;
    }

    public CommunityReport createReport(Long userId, CommunityReportDto.CreateRequest request) {
        CommunityReport report = new CommunityReport();
        report.setUserId(userId);

        if (request.getReporterName() != null && !request.getReporterName().trim().isEmpty()) {
            report.setReporterName(request.getReporterName().trim());
        } else if (userId != null) {
            userRepository.findById(userId).ifPresent(u -> report.setReporterName(u.getName()));
        } else {
            report.setReporterName("Anonymous");
        }

        report.setType(request.getType() != null ? request.getType().toUpperCase() : "SMS");
        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setTargetIdentifier(request.getTargetIdentifier());
        report.setAdditionalInfo(request.getAdditionalInfo());
        report.setTags(request.getTags());
        report.setCity(request.getCity());
        report.setState(request.getState());
        report.setStatus(ReportStatus.PENDING);

        return reportRepository.save(report);
    }

    public Page<CommunityReport> getVerifiedReports(int page, int size) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.VERIFIED, PageRequest.of(page, size));
    }

    public Page<CommunityReport> getAllReports(int page, int size) {
        return reportRepository.findAll(PageRequest.of(page, size));
    }

    public List<CommunityReport> getRecentReports() {
        return reportRepository.findTop5ByStatusOrderByCreatedAtDesc(ReportStatus.VERIFIED);
    }

    public CommunityReport upvote(Long reportId) {
        CommunityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setUpvotes(report.getUpvotes() + 1);

        // Auto-verify if community upvotes exceed threshold (e.g. 5 upvotes)
        if (report.getUpvotes() >= 5 && report.getStatus() == ReportStatus.PENDING) {
            report.setStatus(ReportStatus.VERIFIED);
            syncWithFraudIntelligence(report);
        }

        return reportRepository.save(report);
    }

    public CommunityReport downvote(Long reportId) {
        CommunityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setDownvotes(report.getDownvotes() + 1);
        return reportRepository.save(report);
    }

    public CommunityReport updateStatus(Long reportId, ReportStatus status) {
        CommunityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(status);

        if (status == ReportStatus.VERIFIED) {
            syncWithFraudIntelligence(report);
        }

        return reportRepository.save(report);
    }

    private void syncWithFraudIntelligence(CommunityReport report) {
        String target = report.getTargetIdentifier();
        if (target == null || target.isBlank()) return;

        if ("CALL".equalsIgnoreCase(report.getType()) || "SMS".equalsIgnoreCase(report.getType())) {
            String cleanNumber = target.replaceAll("[^0-9+]", "");
            if (!cleanNumber.isBlank()) {
                fraudNumberRepository.findByPhoneNumber(cleanNumber).ifPresentOrElse(
                        fn -> {
                            fn.setReportCount(fn.getReportCount() + 1);
                            fraudNumberRepository.save(fn);
                        },
                        () -> {
                            FraudNumber fn = new FraudNumber(cleanNumber, 90, report.getTags() != null ? report.getTags() : "Scam Call", 1, report.getTitle());
                            fraudNumberRepository.save(fn);
                        }
                );
            }
        } else if ("URL".equalsIgnoreCase(report.getType())) {
            if (!fraudURLRepository.existsByUrl(target)) {
                String domain = target.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "").split("/")[0];
                FraudURL fu = new FraudURL(target, domain, 95, "Phishing", report.getTitle());
                fraudURLRepository.save(fu);
            }
        }
    }

    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }
}

