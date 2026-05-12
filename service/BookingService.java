package com.paddlecourt.booking.service;

import com.paddlecourt.booking.dto.BookingDto;
import com.paddlecourt.booking.model.Booking;
import com.paddlecourt.booking.model.Court;
import com.paddlecourt.booking.model.User;
import com.paddlecourt.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtService courtService;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    @Transactional
    public Booking createBooking(User user, BookingDto dto, String ipAddress) {
        validateBookingTimes(dto.getStartTime(), dto.getEndTime());

        Court court = courtService.findById(dto.getCourtId());
        if (!court.isAvailable()) {
            throw new IllegalArgumentException("This court is currently unavailable");
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                court.getId(), dto.getBookingDate(), dto.getStartTime(), dto.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException(
                    "This time slot is already booked. Please choose another time.");
        }

        long minutes = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalPrice = court.getPricePerHour().multiply(hours);

        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(dto.getBookingDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .totalPrice(totalPrice)
                .status(Booking.BookingStatus.CONFIRMED)
                .notes(dto.getNotes())
                .build();

        Booking saved = bookingRepository.save(booking);

        emailService.sendBookingConfirmation(saved);
        activityLogService.log(user, "BOOKING_CREATED",
                "Booked " + court.getName() + " on " + dto.getBookingDate(), ipAddress);

        return saved;
    }

    private void validateBookingTimes(LocalTime start, LocalTime end) {
        if (start.equals(end) || start.isAfter(end)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 30) {
            throw new IllegalArgumentException("Booking must be at least 30 minutes");
        }
        if (minutes > 4 * 60) {
            throw new IllegalArgumentException("Booking cannot exceed 4 hours");
        }
    }

    public List<Booking> findUserBookings(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Booking> findAllBookings() {
        return bookingRepository.findAllOrderByCreatedAtDesc();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public void cancelBooking(Long bookingId, User user, String ipAddress) {
        Booking booking = findById(bookingId);

        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new SecurityException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        emailService.sendCancellationEmail(booking);
        activityLogService.log(user, "BOOKING_CANCELLED",
                "Cancelled booking #" + bookingId, ipAddress);
    }

    public List<Booking> getCourtSchedule(Long courtId, java.time.LocalDate date) {
        return bookingRepository.findByCourtIdAndBookingDate(courtId, date);
    }

    public long countByStatus(Booking.BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }
}
