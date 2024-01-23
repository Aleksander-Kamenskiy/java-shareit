package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
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
    public BookingDtoOut create(@RequestHeader(USER_HEADER) Long userId,
                                @Valid @RequestBody BookingDto bookingDto) {
        log.info("POST запрос на создание нового бронирования вещи от пользователя c id: {} ", userId);
        BookingDtoOut bookingDtoOut =  bookingService.add(userId, bookingDto);
        log.info("Новая вещь забронирована");
        return bookingDtoOut;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateStatus(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable("bookingId")
                                      Long bookingId,
                                      @RequestParam(name = "approved") Boolean approved) {
        log.info("PATCH запрос на обновление статуса бронирования вещи от владельца с id: {}", userId);
        BookingDtoOut bookingDtoOut = bookingService.update(userId, bookingId, approved);
        log.info("Статус бронирования вещи обновлен");
        return bookingDtoOut;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable("bookingId")
                                         Long bookingId) {
        log.info("GET запрос на получение данных о бронировании от пользователя с id: {}", userId);
        BookingDtoOut bookingDtoOut = bookingService.findBookingByUserId(userId, bookingId);
        log.info("Данные о бронировании получены");
        return bookingDtoOut;
    }

    @GetMapping
    public List<BookingDtoOut> findAll(@RequestHeader(USER_HEADER) Long userId,
                                       @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("GET запрос на получение списка всех бронирований текущего пользователя с id: {} и статусом {}", userId, bookingState);
        List<BookingDtoOut>  bookingDtoOutList = bookingService.findAll(userId, bookingState);
        log.info("Список всех бронирований текущего пользователя получен");
        return bookingDtoOutList;
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                           @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("GET запрос на получение списка всех бронирований текущего владельца с id: {} и статусом {}", ownerId, bookingState);
        List<BookingDtoOut>  bookingDtoOutList = bookingService.findAllOwner(ownerId, bookingState);
        log.info("Список всех бронирований текущего владельца получен");
        return bookingDtoOutList;
    }
}