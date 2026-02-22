package com.moveinsync.alertsystem.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moveinsync.alertsystem.model.Alert;
import com.moveinsync.alertsystem.repository.AlertRepository;
import com.moveinsync.alertsystem.security.JwtUtil;
import com.moveinsync.alertsystem.service.AlertService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/alerts")
@CrossOrigin
public class AlertController {

    private final AlertService alertService;
    private final AlertRepository alertRepository;
    private final JwtUtil jwtUtil;

    public AlertController(AlertService alertService,
                           AlertRepository alertRepository,
                           JwtUtil jwtUtil) {
        this.alertService = alertService;
        this.alertRepository = alertRepository;
        this.jwtUtil = jwtUtil;
    }

    // 🔐 AUTH CHECK
    private void checkAuth(HttpServletRequest request){

        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")){
            throw new RuntimeException("Missing token");
        }

        String token = header.substring(7);

        boolean valid = jwtUtil.validateToken(token);

        if(!valid){
            throw new RuntimeException("Invalid token");
        }
    }

    // ================= CREATE ALERT =================
    @PostMapping
    public Alert createAlert(@RequestBody Alert alert, HttpServletRequest request){
        checkAuth(request);
        return alertService.createAlert(alert);
    }

    // ================= GET ALL ALERTS =================
    @GetMapping
    public List<Alert> getAllAlerts(HttpServletRequest request){
        checkAuth(request);
        return alertService.getAllAlerts();
    }

    // ================= RESOLVE ALERT =================
    @PutMapping("/{id}/resolve")
    public String resolveAlert(@PathVariable Long id, HttpServletRequest request){
        checkAuth(request);
        return alertService.resolveAlert(id);
    }

    // ================= DRIVER HISTORY =================
    @GetMapping("/driver/{driverId}")
    public List<Alert> getDriverAlerts(@PathVariable String driverId, HttpServletRequest request){
        checkAuth(request);
        return alertRepository.findByDriverId(driverId);
    }

    // ================= TOP DRIVERS =================
    @GetMapping("/top-drivers")
    public List<String> topDrivers(HttpServletRequest request){
        checkAuth(request);
        return alertService.getTopDrivers();
    }

    // ================= STATS FOR GRAPH =================
    @GetMapping("/stats")
    public Map<String, Long> getStats(HttpServletRequest request){
        checkAuth(request);

        Map<String, Long> stats = new HashMap<>();

        stats.put("total", alertRepository.count());
        stats.put("open", (long) alertRepository.findByStatus("OPEN").size());
        stats.put("resolved", (long) alertRepository.findByStatus("RESOLVED").size());
        stats.put("autoClosed", (long) alertRepository.findByStatus("AUTO_CLOSED").size());

        return stats;
    }
}