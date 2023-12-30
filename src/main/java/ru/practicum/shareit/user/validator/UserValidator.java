package ru.practicum.shareit.user.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class UserValidator {
    public void validate(UserDto user) {

        if (user.getEmail().isEmpty()) {
            log.error("не заполнена почта " + user.getId());
            throw new ValidationException("почта");
        }
        if (!user.getEmail().contains("@")) {
            log.error("почта без @ " + user.getId());
            throw new ValidationException("почта");
        }

    }
}
