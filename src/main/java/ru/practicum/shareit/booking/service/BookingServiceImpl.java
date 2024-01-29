package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto add(Long userId, BookingRequestDto bookingRequestDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item itemId = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> {
                            return new NotFoundException("Вещь не найдена.");
                        }
                );
        bookingValidation(bookingRequestDto, user, itemId);
        Booking booking = BookingMapper.toBooking(user, itemId, bookingRequestDto);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateOwnerBookingDetails(userId, bookingId);
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = validateBookerBookingDetails(userId, bookingId);
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAll(Long bookerId, BookingState state) {
        UserDto userDto = userService.findById(bookerId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(UserMapper.toUser(userDto), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllOwner(Long ownerId, BookingState state) {
        UserDto userDto = userService.findById(ownerId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(UserMapper.toUser(userDto), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(UserMapper.toUser(userDto), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(UserMapper.toUser(userDto), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }


    private void bookingValidation(BookingRequestDto bookingRequestDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступена для бронирования.");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вещь не найдена.");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) || bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }
    }


    private Booking validateOwnerBookingDetails(Long userId, Long bookingId) {
        Booking bookingById = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                            throw new NotFoundException("Бронь не найдена.");
                        }
                );
        if (!bookingById.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем");
        }
        if (!bookingById.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Бронь cо статусом WAITING");
        }
        return bookingById;
    }


    private Booking validateBookerBookingDetails(Long userId, Long bookingId) {
        Booking bookingById = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    throw new NotFoundException("Бронь не найдена.");
                });
        if (!bookingById.getBooker().getId().equals(userId)
                && !bookingById.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не владелeц и не автор бронирования ");
        }
        return bookingById;
    }
}