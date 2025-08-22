package com.nurlan.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateDto {

    @Size(max = 100)
    private String title;          // null => dokunma

    @Size(max = 1000)
    private String description;    // null => dokunma

    @Size(min = 20)
    private String content;        // null => dokunma

    private Boolean removeImage;

    private Boolean publish;
}
