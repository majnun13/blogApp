package com.nurlan.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    NO_RECORD_EXIST("1000","Kayıt bulunamadı"),
    USERNAME_NOT_FOUND("1001", "Böyle bir kullanıcı adı bulunamadı"),
    TOKEN_EXPIRED_OR_INVALID("1002", "JWT token geçersiz veya süresi dolmuş"),
    USERNAME_ALREADY_EXISTS("1003", "Bu kullanıcı adı zaten kullanılmaktadır"),
    EMAIL_ALREADY_EXISTS("1004", "Bu email adresi zaten kayıtlıdır"),
    USER_ALREADY_EXISTS("1005", "Bu kullanıcı adı veya email adresi zaten kayıtlıdır"),
    USERNAME_OR_PASSWORD_INVALID("1006", "Kullanıcı adı veya Şifre hatalı"),
    REFRESH_TOKEN_NOT_FOUND("1007", "Refresh Token Bulunamadı"),
    REFRESH_TOKEN_IS_EXPIRED("1008", "Refresh Tokenin Süresi bitmiştir"),
    ACCESS_DENIED("1009", "Bu işlem için yetkiniz bulunmamaktadır"),
    EMAIL_MUST_BE_VERIFICATED("1010", "Email'inizi doğrulamanız lazım"),
    USER_HAS_BEEN_BANNED("1011", "Kullanıcı banlandı."),
    POST_NOT_FOUND("2000", "Böyle bir gönderi bulunamadı"),
    POST_IS_NOT_PUBLISHED("2001", "Post Yayınlanmamış"),
    POST_IS_NOT_APPROVED("2002", "Post Onaylanmamış"),
    COMMENT_NOT_FOUND("2003", "Böyle Bir Yorum Bulunamadı"),
    IMAGE_FILE_IS_EMPTY("3000", "Fotoğraf dosyası boş"),
    IMAGE_FILE_NAME_NOT_FOUND("3001", "Fotoğraf dosya adı bulunamadı"),
    FILE_MUST_BE_JPG_OR_PNG("3002", "Dosya png ya da jpg olmalı"),
    FILE_UPLOAD_ERROR("3003", "Dosya yüklenemedi"),
    FILE_MUST_BE_BIGGER_THAN_100_KBPS("3004", "Dosya 100 KBPS'den büyük olmalı"),
    FILE_MUST_BE_BIGGER_THAN_350KBPS("3005", "Dosya 500kbps'den büyük olmalı"),
    FILE_MUST_BE_SMALLER_THAN_10_MB("3005", "Dosya 10 mb'dan küçük olmalı"),
    CONFIRM_PASSWORD_ERROR("5000", "Şifreler eşleşmiyor"),
    OLD_PASSWORD_IS_WRONG("5001", "Eski şifren yanlış"),
    PASSWORDS_CANNOT_BE_SAME("5002", "Eski şifreyle yeni şifren aynı olamaz"),
    PASSWORD_IS_WRONG("5003", "Şifreyi yanlış girdiniz"),
    TAG_CAN_NOT_BE_EMPTY("6000", "Tag alanı boş olamaz"),
    INVALID_TAG_COLOR("6001", "Geçersiz tag rengi"),
    INVALID_TAG("6002", "Geçersiz tag"),
    GENERAL_EXCEPTION("9999", "Genel bir hata oluştu");


    private String message;
    private String code;

    MessageType(String code, String message){
        this.message = message;
        this.code = code;
    }
}
