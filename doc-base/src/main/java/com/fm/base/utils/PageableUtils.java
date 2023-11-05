package com.fm.base.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils extends PageRequest {
    public PageableUtils(int page, int size, String orderBy, boolean desc) {
        super(page - 1, size, desc ? Sort.by(orderBy).descending() : Sort.by(orderBy));
    }

    public static Pageable pageable(int page, int size, String orderBy, boolean desc) {
        return new PageableUtils(page, size, orderBy, desc);
    }
}
