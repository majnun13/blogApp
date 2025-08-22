package com.nurlan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagInfoDto {

    private Long id;

    @NotBlank(message="Tag adı boş olamaz")
    @Size(min=2, max=24)
    @Pattern(regexp="^[A-Za-z0-9\\- ]+$")
    private String name;

    private String color;
}
