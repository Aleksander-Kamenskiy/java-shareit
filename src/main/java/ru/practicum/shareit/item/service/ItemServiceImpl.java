package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemRequestDto itemRequestDto) {
        UserDto user = userService.findById(userId);
        Item item = ItemMapper.toItem(itemRequestDto);
        item.setOwner((UserMapper.toUser(user)));
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemRequestDto itemRequestDto) {
        UserDto user = userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с " + itemId + " не существует")
                );
        if (!UserMapper.toUser(user).equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с id = " + userId + " не является собственником вещи id = " + itemId);
        }
        Boolean isAvailable = itemRequestDto.getAvailable();
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        String description = itemRequestDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        String name = itemRequestDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item itemGet = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new NotFoundException("У пользователя с id = " + userId + " не " +
                            "существует вещи с id = " + itemId);
                });

        ItemDto itemDto = ItemMapper.toItemDtoOut(itemGet);
        itemDto.setComments(getAllItemComments(itemId));
        if (!itemGet.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(itemGet, BookingStatus.APPROVED);
        List<BookingDto> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(toList());

        itemDto.setLastBooking(getLastBooking(bookingDTOList, LocalDateTime.now()));
        itemDto.setNextBooking(getNextBooking(bookingDTOList, LocalDateTime.now()));
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAll(Long userId) {
        UserDto owner = userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, List<CommentDto>> comments = commentRepository.findAllByItemIdIn(idList)
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDto::getItemId, toList()));

        Map<Long, List<BookingDto>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingDto::getItemId, toList()));

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(Long userId, String text) {
        userService.findById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.search(text);
        return itemList.stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, CommentRequestDto commentRequestDto, Long itemId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item itemById = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new NotFoundException("У пользователя с id = " + userId + " не существует вещи с id = " + itemId);
                });

        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("У пользователя с id   " + userId + " должно быть хотя бы одно бронирование предмета с id " + itemId);
        }

        return CommentMapper.toCommentDtoOut(commentRepository.save(CommentMapper.toComment(commentRequestDto, itemById, user)));
    }

    private List<CommentDto> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    private BookingDto getLastBooking(List<BookingDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingDto getNextBooking(List<BookingDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}
