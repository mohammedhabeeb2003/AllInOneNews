package com.vpapps.interfaces;

import com.vpapps.item.ItemComment;

public interface CommentDeleteListener {
    void onStart();
    void onEnd(String success, String isDeleted, String message, ItemComment itemComment);
}