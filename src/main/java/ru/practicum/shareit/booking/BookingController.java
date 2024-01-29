package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;


import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("POST запрос на создание нового бронирования вещи от пользователя c id: {} ", userId);
        BookingDto bookingDto =  bookingService.add(userId, bookingRequestDto);
        log.info("Новая вещь забронирована");
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(USER_HEADER) Long userId,
                                   @PathVariable("bookingId")
                                      Long bookingId,
                                   @RequestParam(name = "approved") Boolean approved) {
        log.info("PATCH запрос на обновление статуса бронирования вещи от владельца с id: {}", userId);
        BookingDto bookingDto = bookingService.update(userId, bookingId, approved);
        log.info("Статус бронирования вещи обновлен");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable("bookingId")
                                         Long bookingId) {
        log.info("GET запрос на получение данных о бронировании от пользователя с id: {}", userId);
        BookingDto bookingDto = bookingService.findBookingByUserId(userId, bookingId);
        log.info("Данные о бронировании получены");
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("GET запрос на получение списка всех бронирований текущего пользователя с id: {} и статусом {}", userId, bookingState);
        List<BookingDto> bookingDtoList = bookingService.findAll(userId, validState(bookingState));
        log.info("Список всех бронирований текущего пользователя получен");
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                        @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("GET запрос на получение списка всех бронирований текущего владельца с id: {} и статусом {}", ownerId, bookingState);
        List<BookingDto> bookingDtoList = bookingService.findAllOwner(ownerId, validState(bookingState));
        log.info("Список всех бронирований текущего владельца получен");
        return bookingDtoList;
    }

    private BookingState validState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
        return state;
    }
}