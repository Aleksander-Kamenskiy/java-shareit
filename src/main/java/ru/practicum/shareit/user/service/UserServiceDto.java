package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserServiceDto {

    UserDto add(UserDto userDto);

    UserDto update(Long id, UserUpdateDto userUpdateDto);

    UserDto findById(Long id);

    void delete(Long id);

    List<UserDto> findAll();
}