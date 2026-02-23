package com.moveinsync.alertsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import com.moveinsync.alertsystem.repository.AlertRepository;
import com.moveinsync.alertsystem.model.Alert;
import com.moveinsync.alertsystem.rules.RuleEngine;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final RuleEngine ruleEngine;

    public AlertService(AlertRepository alertRepository, RuleEngine ruleEngine) {
        this.alertRepository = alertRepository;
        this.ruleEngine = ruleEngine;
    }

    // ================= CREATE ALERT =================
    public Alert createAlert(Alert alert) {

        // ================= INTELLIGENT COUNT BASED ESCALATION =================

List<Alert> previous = alertRepository
.findByDriverIdAndSourceType(alert.getDriverId(), alert.getSourceType());

long recentCount = previous.stream()
.filter(a -> a.getTimestamp() != null &&
        a.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(60)))
.count();

// example rule: 3 alerts within 60 mins → escalate
if(recentCount >= 2){   // + current alert = 3
alert.setEscalationLevel(1);
alert.setSeverity("CRITICAL");
System.out.println("Intelligent escalation triggered for driver " + alert.getDriverId());
}

        return alertRepository.save(alert);
    }

    // ================= MANUAL RESOLVE =================
    public String resolveAlert(Long id) {
        Alert alert = alertRepository.findById(id).orElse(null);

        if(alert == null){
            return "Alert not found";
        }

        alert.setStatus("RESOLVED");
        alertRepository.save(alert);

        return "Alert resolved successfully";
    }

    // ================= GET ALL =================
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // ================= TOP DRIVERS =================
    public List<String> getTopDrivers() {

        List<Object[]> list = alertRepository.findTopDrivers();
        List<String> result = new java.util.ArrayList<>();

        for(Object[] obj : list){
            String driver = (String)obj[0];
            Long count = (Long)obj[1];

            result.add(driver + " -> " + count + " alerts");
        }

        return result;
    }

    // ================= AUTO ESCALATION ENGINE =================
    // FAST demo version (seconds instead of minutes)
    @Scheduled(fixedRate = 10000) // every 10 sec
    public void checkEscalations() {

        System.out.println("Scheduler running...");

        List<Alert> alerts = alertRepository.findAll();

        for(Alert alert : alerts){

            if(alert.getTimestamp() == null) continue;

            long seconds = Duration.between(
                    alert.getTimestamp(),
                    LocalDateTime.now()
            ).toSeconds();

            if(!"OPEN".equals(alert.getStatus()))
                continue;

            // ================= CRITICAL FLOW =================
            if("CRITICAL".equalsIgnoreCase(alert.getSeverity())){

                if(seconds >= 10 && alert.getEscalationLevel() == 0){
                    alert.setEscalationLevel(1);
                    alertRepository.save(alert);
                    System.out.println("Escalated to Level 1 -> Alert " + alert.getAlertId());
                }

                else if(seconds >= 20 && alert.getEscalationLevel() == 1){
                    alert.setEscalationLevel(2);
                    alertRepository.save(alert);
                    System.out.println("Escalated to Level 2 -> Alert " + alert.getAlertId());
                }

                else if(seconds >= 30){
                    alert.setStatus("AUTO_CLOSED");
                    alertRepository.save(alert);
                    System.out.println("Auto closed -> Alert " + alert.getAlertId());
                }
            }

            // ================= WARNING FLOW =================
            if("WARNING".equalsIgnoreCase(alert.getSeverity())){

                if(seconds >= 20 && alert.getEscalationLevel() == 0){
                    alert.setEscalationLevel(1);
                    alertRepository.save(alert);
                }

                else if(seconds >= 40){
                    alert.setStatus("AUTO_CLOSED");
                    alertRepository.save(alert);
                }
            }

            // ================= INFO FLOW =================
            if("INFO".equalsIgnoreCase(alert.getSeverity())){
                if(seconds >= 20){
                    alert.setStatus("AUTO_CLOSED");
                    alertRepository.save(alert);
                    System.out.println("Info auto closed -> " + alert.getAlertId());
                }
            }
        }
    }
}