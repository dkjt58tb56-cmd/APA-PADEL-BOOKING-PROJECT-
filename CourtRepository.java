package com.paddlecourt.booking.repository;

import com.paddlecourt.booking.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByAvailableTrue();
    List<Court> findByType(Court.CourtType type);
}
