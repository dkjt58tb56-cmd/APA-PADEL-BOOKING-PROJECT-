package com.paddlecourt.booking.service;

import com.paddlecourt.booking.dto.CourtDto;
import com.paddlecourt.booking.model.Court;
import com.paddlecourt.booking.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;

    public List<Court> findAll() {
        return courtRepository.findAll();
    }

    public List<Court> findAllAvailable() {
        return courtRepository.findByAvailableTrue();
    }

    public Court findById(Long id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
    }

    @Transactional
    public Court create(CourtDto dto) {
        Court court = Court.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .type(dto.getType())
                .pricePerHour(dto.getPricePerHour())
                .description(dto.getDescription())
                .available(dto.isAvailable())
                .build();
        return courtRepository.save(court);
    }

    @Transactional
    public Court update(Long id, CourtDto dto) {
        Court court = findById(id);
        court.setName(dto.getName());
        court.setLocation(dto.getLocation());
        court.setType(dto.getType());
        court.setPricePerHour(dto.getPricePerHour());
        court.setDescription(dto.getDescription());
        court.setAvailable(dto.isAvailable());
        return courtRepository.save(court);
    }

    @Transactional
    public void delete(Long id) {
        courtRepository.deleteById(id);
    }
}
