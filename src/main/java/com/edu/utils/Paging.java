package com.edu.utils;

import lombok.Getter;

@Getter
public class Paging {
    private int page;           // 현재 페이지 (1부터 시작)
    private int size;           // 한 페이지 당 항목 수
    private int totalElements;  // 전체 데이터 개수
    private int totalPages;     // 전체 페이지 수
    private int startPage;      // 보여줄 첫 페이지 번호
    private int endPage;        // 보여줄 마지막 페이지 번호

    public Paging(int page, int size, int totalElements) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);

        // 보여줄 페이지 번호 범위 계산 (예: 1~5, 6~10 ...)
        int pageBlock = 5; // 페이지 블록(몇 개씩 보여줄지)
        this.startPage = ((page - 1) / pageBlock) * pageBlock + 1;
        this.endPage = Math.min(startPage + pageBlock - 1, totalPages);
    }

    // 이전 페이지가 있는지
    public boolean hasPrev() {
        return startPage > 1;
    }
    // 다음 페이지가 있는지
    public boolean hasNext() {
        return endPage < totalPages;
    }
}