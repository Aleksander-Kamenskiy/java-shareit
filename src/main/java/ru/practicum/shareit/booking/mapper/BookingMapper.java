package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class BookingMapper {
    public Booking toBooking(User user, Item item, BookingRequestDto bookingRequestDto) {
        return new Booking(
                item,
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                user,
                BookingStatus.WAITING);
    }

    public BookingDto toBookingOut(Booking booking) {
        return new BookingDto(
                booking.getId(),
                ItemMapper.toItemDtoOut(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}