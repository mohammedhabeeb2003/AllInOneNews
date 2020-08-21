package com.vpapps.interfaces;

import com.vpapps.item.ItemComment;

public interface PostCommentListener {
    void onStart();
    void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment);
}