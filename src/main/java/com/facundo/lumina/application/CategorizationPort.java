package com.facundo.lumina.application;

import com.facundo.lumina.domain.Category;

public interface CategorizationPort {
    Category categorize(String rawDescription);
}
