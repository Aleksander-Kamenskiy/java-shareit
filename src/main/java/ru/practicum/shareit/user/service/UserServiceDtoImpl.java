package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceDtoImpl implements UserServiceDto {

    private final UserServiceDao userServiceDao;

    UserValidator validator = new UserValidator();

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validator.validate(userDto);
        return UserMapper.toUserDto(userServiceDao.add(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = new User();
        UserDto userFromMemory = findById(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(userFromMemory.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(userFromMemory.getEmail());
        }
        user.setId(id);
        return UserMapper.toUserDto(userServiceDao.update(user));
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> user = userServiceDao.findById(id);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователя с " + id + " не существует");
        }
        return UserMapper.toUserDto(user.get());
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