package com.nurlan.service.interfaces;

import com.nurlan.enums.TagColor;
import com.nurlan.models.Tag;

import java.util.List;

public interface ITagService {

    Tag getOrCreateByName(String rawName);
    Tag getOrCreateByName(String name, TagColor color);
    List<Tag> getOrCreateByNames(List<String> rawNames);
}
