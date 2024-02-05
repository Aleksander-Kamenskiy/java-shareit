package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userCreateDto) {
        log.info("POST Запрос на добавление пользователя " + userCreateDto.toString());
        UserDto userDto = userService.add(userCreateDto);
        log.info("Добавлен пользователь " + userCreateDto.toString());
        return userDto;
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("GET Запрос на получение пользователя с id = " + userId);
        UserDto userDto = userService.findById(userId);
        log.info("Получен пользователь с id = " + userId);
        return userDto;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET Запрос на получение всех пользователей");
        List<UserDto> list = userService.findAll();
        log.info("Все пользователи получены");
        return list;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PATCH Запрос на обновление пользователя с id = " + userId);
        userUpdateDto.setId(userId);
        UserDto userDto = userService.update(userUpdateDto);
        log.info("Обновлен пользователь с id = " + userId);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("DELETE Запрос на удаление пользователя с id = " + userId);
        userService.delete(userId);
        log.info("Удален пользователь с id = " + userId);
    }
}
