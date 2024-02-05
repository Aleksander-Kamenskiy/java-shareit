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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto add(Long userId, BookingRequestDto bookingRequestDto) {
        User user = UserMapper.toModel(userService.findById(userId));
        Item itemId = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> {
                            return new NotFoundException("Вещь не найдена.");
                        }
                );
        bookingValidation(bookingRequestDto, user, itemId);
        Booking booking = BookingMapper.toModel(user, itemId, bookingRequestDto);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateOwnerBookingDetails(userId, bookingId);
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = validateBookerBookingDetails(userId, bookingId);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAll(Long bookerId, BookingState state) {
        UserDto userDto = userService.findById(bookerId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(bookerId, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findAllBookingsByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerIdAndStatusAndStartAfter(bookerId, BookingStatus.WAITING, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllOwner(Long ownerId, BookingState state) {

        User user = userRepository.findById(ownerId).orElseThrow(() -> {
            return new NotFoundException("Пользователя с " + ownerId + " не существует");
        });

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);
        if (userItems.isEmpty()) return Collections.emptyList();

        List<Long> itemIds = userItems
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItemIdIn(itemIds, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findAllBookingsByItemIdInAndStartBeforeAndEndAfter(itemIds, now, now, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByItemIdInAndEndBefore(itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByItemIdInAndStartAfter(itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByItemIdInAndStatusAndStartAfter(itemIds, BookingStatus.WAITING, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByItemIdInAndStatus(itemIds, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }


    private void bookingValidation(BookingRequestDto bookingRequestDto, User user, Item item) {
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вещь не найдена.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступена для бронирования.");
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
