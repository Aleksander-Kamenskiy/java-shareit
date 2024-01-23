package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceDao {

    User add(User user);

    User update(User user);

    Optional<User> findById(Long id);

    void delete(Long id);

    List<User> findAll();
}
