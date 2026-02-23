package com.moveinsync.alertsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;   // ⭐ ADD THIS IMPORT

import com.moveinsync.alertsystem.model.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    // find alerts by driver
    List<Alert> findByDriverId(String driverId);

    // find alerts by status
    List<Alert> findByStatus(String status);

    // top drivers with most alerts
    @Query("SELECT a.driverId, COUNT(a) as total FROM Alert a GROUP BY a.driverId ORDER BY total DESC")
    List<Object[]> findTopDrivers();

    List<Alert> findByDriverIdAndSourceType(String driverId, String sourceType);

}