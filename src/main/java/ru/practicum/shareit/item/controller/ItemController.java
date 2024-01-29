package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId,
                       @Valid @RequestBody ItemRequestDto itemCreateDto) {
        log.info("POST Запрос на добавление пользователем с id = " + userId + " предмета " + itemCreateDto.toString());
        ItemDto itemDto = itemService.add(userId, itemCreateDto);
        log.info("Предмет добавлен пользователю");
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @RequestBody ItemRequestDto itemUpdateDto,
                          @PathVariable Long itemId) {
        log.info("PATCH Запрос на обновление предмета с id = " + itemId + " пользователем с id = " + userId);
        ItemDto itemDto = itemService.update(userId, itemId, itemUpdateDto);
        log.info("Предмет обновлен пользователем");
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Long userId,
                            @PathVariable("itemId") Long itemId) {
        log.info("GET Запрос на получение предмета с id = " + itemId + " пользователем с id = " + userId);
        ItemDto itemDto = itemService.findById(userId, itemId);
        log.info("Предмет получен пользователем");
        return itemDto;
    }

    @GetMapping
    public List<ItemDto>  findAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("GET Запрос на получение предметов пользователя с id = " + userId);
        List<ItemDto> list = itemService.findAll(userId);
        log.info("Предметы получены пользователем");
        return list;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_HEADER) Long userId,
                                     @RequestParam(name = "text") String text) {
        log.info("GET Запрос на поиск предметов");
        List<ItemDto> list = itemService.search(userId, text);
        log.info("Предмет найден");
        return list;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_HEADER) Long userId,
                                    @Validated @RequestBody CommentRequestDto commentRequestDto,
                                    @PathVariable Long itemId) {
        log.info("POST Запрос на создание комментария id = {}", itemId);
        return itemService.createComment(userId, commentRequestDto, itemId);
    }
}
