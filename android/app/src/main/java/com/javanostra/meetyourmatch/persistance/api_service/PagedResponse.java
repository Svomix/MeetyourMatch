package com.javanostra.meetyourmatch.persistance.api_service;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PagedResponse<T> {

    @SerializedName("content")
    private List<T> content;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("number")
    private int currentPageNumber;

    @SerializedName("size")
    private int pageSize;

    @SerializedName("first")
    private boolean isFirst;

    @SerializedName("last")
    private boolean isLast;

    @SerializedName("empty")
    private boolean isEmpty;

    @SerializedName("numberOfElements")
    private int numberOfElementsOnPage;

    @SerializedName("sort")
    private Object sort;

    public PagedResponse() { }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public boolean hasNext() {
        return !isLast;
    }

    public int getNextPageNumber() {
        return hasNext() ? currentPageNumber + 1 : -1;
    }
}
