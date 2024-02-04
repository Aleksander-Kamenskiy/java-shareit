package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "comment-entity-graph")
    List<Comment> findAllByItemId(Long itemId);

    @EntityGraph(value = "comment-entity-graph")
    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}
