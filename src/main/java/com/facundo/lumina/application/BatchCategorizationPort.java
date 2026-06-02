package com.facundo.lumina.application;

import com.facundo.lumina.domain.Category;

import java.util.List;

public interface BatchCategorizationPort {
    List<Category> categorize(List<String> descriptions);
}
