package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceDtoImpl;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceDtoImpl userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.info("POST Запрос на добавление пользователя " + userDto.toString());
        UserDto userDto1 = userService.add(userDto);
        log.info("Добавлен пользователь " + userDto.toString());
        return userDto1;
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
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH Запрос на обновление пользователя с id = " + userId);
        UserDto userDto1 = userService.update(userId, userDto);
        log.info("Обновлен пользователь с id = " + userId);
        return userDto1;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("DELETE Запрос на удаление пользователя с id = " + userId);
        userService.delete(userId);
        log.info("Удален пользователь с id = " + userId);
    }
}
