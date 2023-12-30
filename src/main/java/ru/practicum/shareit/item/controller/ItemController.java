package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST Запрос на добавление пользователем с id = " + userId + " предмета " + itemDto.toString());
        ItemDto itemDto1 = itemService.add(userId, itemDto);
        log.info("Предмет добавлен пользователю");
        return itemDto1;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("PATCH Запрос на обновление предмета с id = " + itemId + " пользователем с id = " + userId);
        ItemDto itemDto1 = itemService.update(userId, itemId, itemDto);
        log.info("Предмет обновлен пользователем");
        return itemDto1;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("itemId") Long itemId) {
        log.info("GET Запрос на получение предмета с id = " + itemId + " пользователем с id = " + userId);
        ItemDto itemDto1 = itemService.findItemById(userId, itemId);
        log.info("Предмет получен пользователем");
        return itemDto1;
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("GET Запрос на получение предметов пользователя с id = " + userId);
        List list = itemService.findAll(userId);
        log.info("Предметы получены пользователем");
        return list;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_HEADER) Long userId, @RequestParam(name = "text") String text) {
        log.info("GET Запрос на поиск предметов");
        List<ItemDto> list = itemService.search(userId, text);
        log.info("Предмет найден");
        return list;
    }
}
