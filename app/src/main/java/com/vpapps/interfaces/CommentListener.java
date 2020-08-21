package com.vpapps.interfaces;

import com.vpapps.item.ItemComment;
import com.vpapps.item.ItemNews;

import java.util.ArrayList;

public interface CommentListener {
    void onStart();

    void onEnd(String success, ArrayList<ItemComment> arrayListComment);
}