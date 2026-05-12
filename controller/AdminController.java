package com.paddlecourt.booking.controller;

import com.paddlecourt.booking.dto.CourtDto;
import com.paddlecourt.booking.model.Booking;
import com.paddlecourt.booking.model.Role;
import com.paddlecourt.booking.model.User;
import com.paddlecourt.booking.service.ActivityLogService;
import com.paddlecourt.booking.service.BookingService;
import com.paddlecourt.booking.service.CourtService;
import com.paddlecourt.booking.service.UserService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CourtService courtService;
    private final BookingService bookingService;
    private final ActivityLogService activityLogService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User admin = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", admin);
        model.addAttribute("totalUsers", userService.countByRole(Role.USER));
        model.addAttribute("totalAdmins", userService.countByRole(Role.ADMIN));
        model.addAttribute("totalCourts", courtService.findAll().size());
        model.addAttribute("totalBookings", bookingService.findAllBookings().size());
        model.addAttribute("confirmedBookings",
                bookingService.countByStatus(Booking.BookingStatus.CONFIRMED));
        model.addAttribute("cancelledBookings",
                bookingService.countByStatus(Booking.BookingStatus.CANCELLED));
        model.addAttribute("recentBookings",
                bookingService.findAllBookings().stream().limit(10).toList());
        return "admin/dashboard";
    }

    @GetMapping("/courts")
    public String listCourts(Model model) {
        model.addAttribute("courts", courtService.findAll());
        return "admin/courts";
    }

    @GetMapping("/courts/new")
    public String newCourtForm(Model model) {
        model.addAttribute("courtDto", new CourtDto());
        return "admin/court-form";
    }

    @PostMapping("/courts/new")
    public String createCourt(@Valid @ModelAttribute("courtDto") CourtDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "admin/court-form";
        courtService.create(dto);
        redirectAttributes.addFlashAttribute("success", "Court created");
        return "redirect:/admin/courts";
    }

    @GetMapping("/courts/{id}/edit")
    public String editCourtForm(@PathVariable Long id, Model model) {
        var court = courtService.findById(id);
        CourtDto dto = new CourtDto();
        dto.setId(court.getId());
        dto.setName(court.getName());
        dto.setLocation(court.getLocation());
        dto.setType(court.getType());
        dto.setPricePerHour(court.getPricePerHour());
        dto.setDescription(court.getDescription());
        dto.setAvailable(court.isAvailable());
        model.addAttribute("courtDto", dto);
        model.addAttribute("editing", true);
        return "admin/court-form";
    }

    @PostMapping("/courts/{id}/edit")
    public String updateCourt(@PathVariable Long id,
                              @Valid @ModelAttribute("courtDto") CourtDto dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", true);
            return "admin/court-form";
        }
        courtService.update(id, dto);
        redirectAttributes.addFlashAttribute("success", "Court updated");
        return "redirect:/admin/courts";
    }

    @PostMapping("/courts/{id}/delete")
    public String deleteCourt(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courtService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Court deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot delete court (may have existing bookings)");
        }
        return "redirect:/admin/courts";
    }

    @GetMapping("/bookings")
    public String allBookings(Model model) {
        model.addAttribute("bookings", bookingService.findAllBookings());
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String adminCancelBooking(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails principal,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        try {
            User admin = userService.findByUsername(principal.getUsername());
            bookingService.cancelBooking(id, admin, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("success", "Booking cancelled");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleEnabled(id);
        redirectAttributes.addFlashAttribute("success", "User status updated");
        return "redirect:/admin/users";
    }

    @GetMapping("/activity")
    public String activityLog(Model model) {
        model.addAttribute("logs", activityLogService.getRecentLogs());
        return "admin/activity";
    }
}
