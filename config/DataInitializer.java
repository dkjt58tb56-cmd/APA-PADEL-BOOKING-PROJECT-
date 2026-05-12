package com.paddlecourt.booking.config;

import com.paddlecourt.booking.model.Court;
import com.paddlecourt.booking.model.Role;
import com.paddlecourt.booking.model.User;
import com.paddlecourt.booking.repository.CourtRepository;
import com.paddlecourt.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@paddlecourt.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .phone("+1234567890")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info(">>> Admin user created: username=admin, password=admin123");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = User.builder()
                    .username("user")
                    .email("user@paddlecourt.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("Test User")
                    .phone("+1234567891")
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info(">>> Test user created: username=user, password=user123");
        }

        if (courtRepository.count() == 0) {
            courtRepository.save(Court.builder()
                    .name("Court A - Center")
                    .location("Main Arena, Floor 1")
                    .type(Court.CourtType.INDOOR)
                    .pricePerHour(new BigDecimal("25.00"))
                    .description("Premium indoor court with professional lighting")
                    .available(true)
                    .build());

            courtRepository.save(Court.builder()
                    .name("Court B - Side")
                    .location("Main Arena, Floor 1")
                    .type(Court.CourtType.INDOOR)
                    .pricePerHour(new BigDecimal("20.00"))
                    .description("Standard indoor court")
                    .available(true)
                    .build());

            courtRepository.save(Court.builder()
                    .name("Court C - Garden")
                    .location("Outdoor Section")
                    .type(Court.CourtType.OUTDOOR)
                    .pricePerHour(new BigDecimal("15.00"))
                    .description("Outdoor court with natural surroundings")
                    .available(true)
                    .build());

            courtRepository.save(Court.builder()
                    .name("Court D - Covered")
                    .location("West Wing")
                    .type(Court.CourtType.COVERED)
                    .pricePerHour(new BigDecimal("18.00"))
                    .description("Covered court for all-weather play")
                    .available(true)
                    .build());

            log.info(">>> Sample courts created");
        }
    }
}
