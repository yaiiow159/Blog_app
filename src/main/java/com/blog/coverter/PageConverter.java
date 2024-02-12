package com.blog.coverter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageConverter {
    public static  Page covertToPage(List list,int page, int size) {
        // 如果size 跟 page 都是0會報錯
        Pageable pageable;
        if(size == 0) {
            return new PageImpl(List.of(), PageRequest.of(0, 10), 0);
        }
        if(page == 0) {
           pageable = PageRequest.of(page , size);
        } else {
            pageable = PageRequest.of(page - 1, size);
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min((startIndex + pageable.getPageSize()), list.size());

        if(startIndex >= list.size()) {
            return new PageImpl(List.of(), pageable, 0);
        }
        return new PageImpl(list.subList(startIndex, endIndex), pageable, list.size());
    }
}
