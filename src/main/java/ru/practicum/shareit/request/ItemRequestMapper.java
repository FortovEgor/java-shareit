package ru.practicum.shareit.request;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    ItemRequestDto toDto(ItemRequest itemRequest);

    List<ItemRequestDto> toDto(List<ItemRequest> itemRequest);

    ItemRequest toItemRequest(ItemRequestDto request);
//
//    Comment toComment(CreateCommentRequest request);
//
//    @Mapping(target = "authorName", source = "comment.author.name")
//    CommentDto toCommentDto(Comment comment);
//
//    List<CommentDto> toCommentDto(List<Comment> comments);
}
