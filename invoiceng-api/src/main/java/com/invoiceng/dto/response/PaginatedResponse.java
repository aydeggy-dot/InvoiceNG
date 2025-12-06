package com.invoiceng.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static <E, T> PaginatedResponse<T> fromPage(Page<E> page, Function<E, T> mapper) {
        List<T> data = page.getContent().stream()
                .map(mapper)
                .toList();

        return PaginatedResponse.<T>builder()
                .data(data)
                .pagination(PaginationInfo.builder()
                        .page(page.getNumber() + 1) // Convert to 1-based
                        .limit(page.getSize())
                        .total(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }
}
