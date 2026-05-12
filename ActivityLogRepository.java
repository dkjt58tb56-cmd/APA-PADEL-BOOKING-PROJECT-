package com.paddlecourt.booking.repository;

import com.paddlecourt.booking.model.ActivityLog;
import com.paddlecourt.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT a FROM ActivityLog a LEFT JOIN FETCH a.user ORDER BY a.createdAt DESC")
    List<ActivityLog> findTop100ByOrderByCreatedAtDesc();
}
