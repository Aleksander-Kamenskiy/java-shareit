package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class CommentMapper {
    public CommentRequestDto toCommentDto(Comment comment) {
        return new CommentRequestDto(
                comment.getText());
    }

    public CommentDto toCommentDtoOut(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getId());
    }

    public Comment toComment(CommentRequestDto commentRequestDto, Item item, User user) {
        return new Comment(
                commentRequestDto.getText(),
                item,
                user);
    }
}
