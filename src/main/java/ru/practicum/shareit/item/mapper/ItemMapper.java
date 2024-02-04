package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {


    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public ItemDto toDto(Item item, BookingDto lastBooking, List<CommentDto> comments, BookingDto nextBooking) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                comments,
                nextBooking
        );
    }

    public Item toModel(ItemRequestDto itemRequestDto) {
        return new Item(
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getAvailable());
    }
}
