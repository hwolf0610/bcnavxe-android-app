package com.crittermap.backcountrynavigator.xe.service.bookmark;

import java.util.List;

public class BCBookmarkListResponse {
    private List<BCBookmarkData> bookmarkInfo;

    public List<BCBookmarkData> getBookmarkInfo() {
        return bookmarkInfo;
    }

    public void setBookmarkInfo(List<BCBookmarkData> bookmarkInfo) {
        this.bookmarkInfo = bookmarkInfo;
    }
}
