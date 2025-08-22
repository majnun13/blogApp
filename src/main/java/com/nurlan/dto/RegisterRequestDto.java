package com.nurlan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 20, message = "Kullanıcı adı 3-20 karakter arasında olmalı")
    private String username;

    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 30, message = "Ad 2-30 karakter arasında olmalı")
    private String firstName;

    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 30, message = "Soyad 2-30 karakter arasında olmalı")
    private String lastName;

    @Email(message = "Geçerli bir email girin")
    @NotBlank(message = "Email boş olamaz")
    private String email;

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
