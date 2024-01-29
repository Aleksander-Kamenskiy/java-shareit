package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto add(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto findBookingByUserId(Long userId, Long bookingId);

    List<BookingDto> findAll(Long userId, BookingState state);

    List<BookingDto> findAllOwner(Long userId, BookingState state);
}
