package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Set<Long>> userItems = new HashMap<>();

    private final Map<Long, Item> items = new HashMap<>();
    private Long generatorId = 1L;

    @Override
    public Item add(Item item) {
        item.setId(generatorId);
        generatorId++;
        Set<Long> setItems = new HashSet<>();
        setItems.add(item.getId());
        userItems.put(item.getOwner().getId(), setItems);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }


    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAll(Long userId) {
        List<Item> itemsList = new ArrayList<>();
        for (Long itemId : userItems.get(userId)) {
            itemsList.add(items.get(itemId));
        }
        return itemsList;
    }

    @Override
    public List<Item> search(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
