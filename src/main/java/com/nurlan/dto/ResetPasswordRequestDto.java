package com.nurlan.dto;// dto/ResetPasswordRequest.java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPasswordRequestDto {

    @NotBlank(message = "Token zorunludur")
    private String token;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalı")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermeli"
    )
    private String password;

    @NotBlank(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;
}
