package com.paddlecourt.booking.repository;

import com.paddlecourt.booking.model.Booking;
import com.paddlecourt.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.court WHERE b.user = :user ORDER BY b.createdAt DESC")
    List<Booking> findByUserOrderByCreatedAtDesc(@Param("user") User user);

    List<Booking> findByCourtIdAndBookingDate(Long courtId, LocalDate bookingDate);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.court.id = :courtId
              AND b.bookingDate = :date
              AND b.status <> com.paddlecourt.booking.model.Booking$BookingStatus.CANCELLED
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    List<Booking> findConflictingBookings(@Param("courtId") Long courtId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);

   @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.court ORDER BY b.createdAt DESC")
    List<Booking> findAllOrderByCreatedAtDesc();

    long countByStatus(Booking.BookingStatus status);
}
