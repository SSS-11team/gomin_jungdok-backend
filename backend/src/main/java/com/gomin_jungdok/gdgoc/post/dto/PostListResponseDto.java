package com.gomin_jungdok.gdgoc.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
public class PostListResponseDto {
    private int size;
    private List<PostListDetailResponseDto> posts;

    public PostListResponseDto(int size, List<PostListDetailResponseDto> postListDetails) {
        this.size = size;
        this.posts = postListDetails;
    }
}