package com.nurlan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateDto {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotBlank
    @Size(min = 20)
    private String content;


    @Size(max = 10, message = "At most 10 tags allowed")
    private List<@Pattern(regexp = "^[a-zA-Z0-9-_\\s]{1,30}$",
                message="Tag can contain letters, numbers, spaces, '-' and '_' only, max 30 chars") String> tagNames;

    private Boolean publish;


}

