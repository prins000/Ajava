package com.authentica.service;

import com.authentica.dto.AnalysisDto;
import com.authentica.model.Analysis;
import com.authentica.model.enums.AnalysisStatus;
import com.authentica.model.enums.AnalysisType;
import com.authentica.model.enums.RiskLevel;
import com.authentica.repository.AnalysisRepository;
import com.authentica.repository.UserRepository;
import com.authentica.service.engine.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final UrlAnalysisEngine urlEngine;
    private final TextAnalysisEngine textEngine;
    private final QrAnalysisEngine qrEngine;
    private final VoiceAnalysisEngine voiceEngine;
    private final NumberLookupEngine numberEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisService(AnalysisRepository analysisRepository,
                           UserRepository userRepository,
                           UrlAnalysisEngine urlEngine,
                           TextAnalysisEngine textEngine,
                           QrAnalysisEngine qrEngine,
                           VoiceAnalysisEngine voiceEngine,
                           NumberLookupEngine numberEngine) {
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
        this.urlEngine = urlEngine;
        this.textEngine = textEngine;
        this.qrEngine = qrEngine;
        this.voiceEngine = voiceEngine;
        this.numberEngine = numberEngine;
    }

    public AnalysisDto.ScanResponse performScan(Long userId, AnalysisDto.ScanRequest request) {
        long startTime = System.currentTimeMillis();
        EngineResult engineResult;

        AnalysisType type = request.getType() != null ? request.getType() : AnalysisType.URL;

        switch (type) {
            case URL -> engineResult = urlEngine.analyze(request.getTarget());
            case TEXT -> engineResult = textEngine.analyze(request.getTarget(), request.getSenderNumber());
            case QR -> engineResult = qrEngine.analyze(request.getTarget());
            case VOICE -> engineResult = voiceEngine.analyze(request.getTarget(), request.getDurationSeconds(), request.getTranscript());
            case PHONE -> engineResult = numberEngine.analyze(request.getTarget());
            default -> engineResult = urlEngine.analyze(request.getTarget());
        }

        long processingTime = System.currentTimeMillis() - startTime;

        Analysis analysis = new Analysis();
        analysis.setUserId(userId);
        analysis.setType(type);
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setInputTarget(request.getTarget());
        analysis.setTrustScore(engineResult.getTrustScore());
        analysis.setRiskLevel(engineResult.getRiskLevel());
        analysis.setConfidence(engineResult.getConfidence());
        analysis.setSummary(engineResult.getSummary());
        analysis.setProcessingTimeMs(processingTime);

        try {
            Map<String, Object> details = new HashMap<>();
            details.put("flags", engineResult.getFlags());
            details.put("recommendations", engineResult.getRecommendations());
            analysis.setDetailsJson(objectMapper.writeValueAsString(details));
        } catch (Exception ignored) {
            analysis.setDetailsJson("{}");
        }

        Analysis savedAnalysis = analysisRepository.save(analysis);

        // Update user risk profile if authenticated
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                // If scanned threat is critical/high, recalculate user risk level
                if (engineResult.getRiskLevel() == RiskLevel.CRITICAL || engineResult.getRiskLevel() == RiskLevel.HIGH) {
                    user.setTrustScore(Math.max(10, user.getTrustScore() - 5));
                    if (user.getTrustScore() < 50) user.setRiskLevel(RiskLevel.HIGH);
                    else if (user.getTrustScore() < 70) user.setRiskLevel(RiskLevel.MEDIUM);
                } else {
                    user.setTrustScore(Math.min(100, user.getTrustScore() + 1));
                }
                userRepository.save(user);
            });
        }

        return new AnalysisDto.ScanResponse(savedAnalysis, engineResult.getFlags(), engineResult.getRecommendations());
    }

    public Page<Analysis> getUserHistory(Long userId, int page, int size) {
        return analysisRepository.findByUserIdAndIsArchivedFalseOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public Optional<Analysis> getDetail(Long id, Long userId) {
        return analysisRepository.findByIdAndUserId(id, userId);
    }

    public void archiveScan(Long id, Long userId) {
        analysisRepository.findByIdAndUserId(id, userId).ifPresent(a -> {
            a.setIsArchived(true);
            analysisRepository.save(a);
        });
    }
}
