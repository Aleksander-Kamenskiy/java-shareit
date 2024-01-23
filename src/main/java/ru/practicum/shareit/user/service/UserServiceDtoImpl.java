package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceDtoImpl implements UserServiceDto {

    private final UserServiceDao userServiceDao;


    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userServiceDao.add(user));
    }

    @Override
    public UserDto update(Long id, UserUpdateDto userUpdateDto) {
        User user = new User();
        UserDto userFromMemory = findById(id);

        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        } else {
            user.setName(userFromMemory.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        } else {
            user.setEmail(userFromMemory.getEmail());
        }
        user.setId(id);
        return UserMapper.toUserDto(userServiceDao.update(user));
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> userOptional = userServiceDao.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("Пользователя с " + id + " не существует"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userServiceDao.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userServiceDao.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}