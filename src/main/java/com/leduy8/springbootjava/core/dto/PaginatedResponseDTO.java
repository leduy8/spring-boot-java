package com.leduy8.springbootjava.core.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PaginatedResponseDTO<T extends BaseResponseDTO<?>> {
    private final List<T> data;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;
    private final int pageSize;
    private final boolean isLastPage;

    private PaginatedResponseDTO(List<T> data, int currentPage, int totalPages, long totalItems, int pageSize, boolean isLastPage) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.isLastPage = isLastPage;
    }

    public static <E, D extends BaseResponseDTO<E>> PaginatedResponseDTO<D> of(Page<E> page, Class<D> dtoClass) {
        List<D> dtoList = page
                .getContent()
                .stream()
                .map(entity -> BaseResponseDTO.of(entity, dtoClass))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                dtoList,
                page.getNumber() + 1, // Spring Page is 0-based
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isLast()
        );
    }
}

