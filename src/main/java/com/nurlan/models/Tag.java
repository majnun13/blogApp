package com.nurlan.models;

import com.nurlan.enums.TagColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tag adı boş olamaz")
    @Size(min = 2, max = 24, message = "Tag adı 2–24 karakter olmalı")
    @Pattern(
            regexp = "^[A-Za-z0-9\\- ]+$",
            message = "Tag sadece harf, rakam, boşluk ve '-' içerebilir"
    )
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false, length = 20)
    private TagColor color;

    private Date createdDate;

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts = new ArrayList<>();

}
