package com.utopia.day23.dto;

import java.util.List;

public class PageResponse<T> {

    // 当前页真正返回的数据
    private List<T> content;

    // 当前页码，从 1 开始
    private int page;

    // 每页数量
    private int pageSize;

    // 符合搜索和分类条件的总记录数
    private long total;

    // 总页数
    private int totalPages;

    public PageResponse() {
    }

    public PageResponse(
            List<T> content,
            int page,
            int pageSize,
            long total,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
