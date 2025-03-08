package com.gomin_jungdok.gdgoc.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private long user_id;
    private long post_id;
    private Integer parent_comment_id;
    private String comment_desc;
    private int hierarchy;
}