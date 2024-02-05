package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long userId, ItemRequestDto itemRequestDto);

    ItemDto update(Long userId, Long itemId, ItemRequestDto itemRequestDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findAll(Long userId);

    List<ItemDto> search(Long userId, String text);

    CommentDto createComment(Long userId, CommentRequestDto commentRequestDto, Long itemId);
}