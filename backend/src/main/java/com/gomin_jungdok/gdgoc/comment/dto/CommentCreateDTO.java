package com.gomin_jungdok.gdgoc.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private Long parentCommentId;
    private String description;
}