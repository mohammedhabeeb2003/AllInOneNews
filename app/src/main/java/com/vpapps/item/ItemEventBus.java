package com.vpapps.item;

public class ItemEventBus {
    private String message;
    private int pos;
    private ItemComment itemComment;

    public ItemEventBus(String message, ItemComment itemComment, int pos) {
        this.message = message;
        this.itemComment = itemComment;
        this.pos = pos;
    }

    public String getMessage() {
        return message;
    }

    public ItemComment getItemComment() {
        return itemComment;
    }

    public int getPos() {
        return pos;
    }
}