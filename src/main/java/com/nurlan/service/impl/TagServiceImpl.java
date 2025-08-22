package com.nurlan.service.impl;

import com.nurlan.enums.TagColor;
import com.nurlan.exception.BaseException;
import com.nurlan.exception.ErrorMessage;
import com.nurlan.exception.MessageType;
import com.nurlan.models.Tag;
import com.nurlan.repository.TagRepository;
import com.nurlan.service.interfaces.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements ITagService {


    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag getOrCreateByName(String rawName) {
        return getOrCreateByName(rawName, null);
    }

    @Override
    public Tag getOrCreateByName(String rawName, TagColor color) {
        String name = normalizeName(rawName);
        if(name.isEmpty())
            throw new BaseException(new ErrorMessage(MessageType.TAG_CAN_NOT_BE_EMPTY, rawName));

        Optional<Tag> existing = tagRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        Tag t = new Tag();
        t.setName(name);
        t.setColor(color != null ? color : TagColor.BLUE);
        t.setCreatedDate(new Date());
        return tagRepository.save(t);
    }

    @Override
    public List<Tag> getOrCreateByNames(List<String> rawNames) {
        if (rawNames == null || rawNames.isEmpty())
            return Collections.emptyList();

        // normalize + boşları at + tekrarları kaldır
        List<String> names = rawNames.stream()
                .map(this::normalizeName)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        List<Tag> result = new ArrayList<>(names.size());
        for (String n : names) {
            result.add(getOrCreateByName(n));
        }
        return result;
    }

    private String normalizeName(String s) {
        if (s == null) return "";
        // trim + fazla boşlukları teke indir
        String trimmed = s.trim().replaceAll("\\s{2,}", " ");
        return trimmed;
    }
}
