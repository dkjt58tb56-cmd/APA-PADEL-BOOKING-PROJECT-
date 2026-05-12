package com.paddlecourt.booking.service;

import com.paddlecourt.booking.model.Booking;
import com.paddlecourt.booking.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Async
    public void sendWelcomeEmail(User user) {
        if (!mailEnabled) {
            log.info("[Email Disabled] Would send welcome email to {}", user.getEmail());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Paddle Court Booking!");
            message.setText(String.format("""
                    Hi %s,

                    Welcome to Paddle Court Booking! Your account has been successfully created.

                    Username: %s
                    Email: %s

                    You can now log in and start booking courts.

                    Best regards,
                    The Paddle Court Team
                    """, user.getFullName(), user.getUsername(), user.getEmail()));
            mailSender.send(message);
            log.info("Welcome email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", user.getEmail(), e);
        }
    }

    @Async
    public void sendBookingConfirmation(Booking booking) {
        if (!mailEnabled) {
            log.info("[Email Disabled] Would send booking confirmation to {} for booking #{}",
                    booking.getUser().getEmail(), booking.getId());
            return;
        }
        try {
            User user = booking.getUser();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(user.getEmail());
            message.setSubject("Booking Confirmation #" + booking.getId());
            message.setText(String.format("""
                    Hi %s,

                    Your court booking has been confirmed!

                    Booking Details:
                    -----------------------------
                    Booking ID:  #%d
                    Court:       %s
                    Location:    %s
                    Date:        %s
                    Time:        %s - %s
                    Total Price: $%s
                    Status:      %s
                    -----------------------------

                    See you on the court!

                    The Paddle Court Team
                    """,
                    user.getFullName(),
                    booking.getId(),
                    booking.getCourt().getName(),
                    booking.getCourt().getLocation(),
                    booking.getBookingDate().format(df),
                    booking.getStartTime().format(tf),
                    booking.getEndTime().format(tf),
                    booking.getTotalPrice(),
                    booking.getStatus()));
            mailSender.send(message);
            log.info("Booking confirmation sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation", e);
        }
    }

    @Async
    public void sendCancellationEmail(Booking booking) {
        if (!mailEnabled) {
            log.info("[Email Disabled] Would send cancellation email for booking #{}", booking.getId());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(booking.getUser().getEmail());
            message.setSubject("Booking Cancellation #" + booking.getId());
            message.setText(String.format("""
                    Hi %s,

                    Your booking #%d for %s on %s has been cancelled.

                    Best regards,
                    The Paddle Court Team
                    """,
                    booking.getUser().getFullName(),
                    booking.getId(),
                    booking.getCourt().getName(),
                    booking.getBookingDate()));
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send cancellation email", e);
        }
    }
}
