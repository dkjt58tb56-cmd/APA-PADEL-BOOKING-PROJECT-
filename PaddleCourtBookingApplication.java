package com.paddlecourt.booking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PaddleCourtBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaddleCourtBookingApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Paddle Court Booking System Started");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
