package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userService;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        UserDto user = UserMapper.toUserDto(userService.findById(userId).get());
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(user));
        return ItemMapper.toItemDto(itemDao.add(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> itemOptional = itemDao.findItemById(itemId);
        if (!itemOptional.isPresent() || !itemOptional.get().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %s " +
                    "не является владельцем вещи id %s.", userId, itemId));
        }
        Item itemFromStorage = itemOptional.get();
        if (!Objects.isNull(itemDto.getAvailable())) {
            itemFromStorage.setAvailable(itemDto.getAvailable());
        }
        if (!Objects.isNull(itemDto.getDescription())) {
            itemFromStorage.setDescription(itemDto.getDescription());
        }
        if (!Objects.isNull(itemDto.getName())) {
            itemFromStorage.setName(itemDto.getName());
        }
        return ItemMapper.toItemDto(itemDao.update(itemFromStorage));
    }

    @Override
    public ItemDto findItemById(Long userId, Long itemId) {
        userService.findById(userId);
        Optional<Item> itemGet = itemDao.findItemById(itemId);
        if (itemGet.isEmpty()) {
            throw new NotFoundException(String.format("У пользователя с id %s не " +
                    "существует вещи с id %s", userId, itemId));
        }
        return ItemMapper.toItemDto(itemGet.get());
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        userService.findById(userId);
        List<Item> itemList = itemDao.findAll(userId);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userService.findById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemDao.search(text);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
