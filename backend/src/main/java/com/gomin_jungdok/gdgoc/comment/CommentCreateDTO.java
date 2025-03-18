package com.gomin_jungdok.gdgoc.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private Integer parentCommentId;
    private String description;
}