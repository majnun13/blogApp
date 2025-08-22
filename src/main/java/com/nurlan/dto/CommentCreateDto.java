package com.nurlan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {

    //bunu commentupdate için de kullanıyoruz


    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;
}
