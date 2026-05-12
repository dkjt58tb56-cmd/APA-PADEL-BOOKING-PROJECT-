package com.paddlecourt.booking.controller;

import com.paddlecourt.booking.dto.BookingDto;
import com.paddlecourt.booking.model.User;
import com.paddlecourt.booking.service.ActivityLogService;
import com.paddlecourt.booking.service.BookingService;
import com.paddlecourt.booking.service.CourtService;
import com.paddlecourt.booking.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CourtService courtService;
    private final BookingService bookingService;
    private final ActivityLogService activityLogService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal,
                            HttpServletRequest request, Model model) {
        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingService.findUserBookings(user));
        model.addAttribute("courts", courtService.findAllAvailable());

        String returningUser = "false";
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("PADDLE_REGISTERED".equals(c.getName())) {
                    returningUser = c.getValue();
                    break;
                }
            }
        }
        model.addAttribute("returningUser", returningUser);
        return "user/dashboard";
    }

    @GetMapping("/courts")
    public String listCourts(Model model) {
        model.addAttribute("courts", courtService.findAllAvailable());
        model.addAttribute("bookingDto", new BookingDto());
        return "user/courts";
    }

    @GetMapping("/book/{courtId}")
    public String bookForm(@PathVariable Long courtId, Model model) {
        BookingDto dto = new BookingDto();
        dto.setCourtId(courtId);
        model.addAttribute("court", courtService.findById(courtId));
        model.addAttribute("bookingDto", dto);
        return "user/book";
    }

    @PostMapping("/book")
    public String createBooking(@Valid @ModelAttribute("bookingDto") BookingDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails principal,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("court", courtService.findById(dto.getCourtId()));
            return "user/book";
        }
        try {
            User user = userService.findByUsername(principal.getUsername());
            bookingService.createBooking(user, dto, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
            return "redirect:/user/bookings";
        } catch (IllegalArgumentException e) {
            model.addAttribute("court", courtService.findById(dto.getCourtId()));
            model.addAttribute("error", e.getMessage());
            return "user/book";
        }
    }

    @GetMapping("/bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("bookings", bookingService.findUserBookings(user));
        return "user/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails principal,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getUsername());
            bookingService.cancelBooking(id, user, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("success", "Booking cancelled");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/bookings";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("activityHistory", activityLogService.getUserHistory(user));
        return "user/profile";
    }
}
