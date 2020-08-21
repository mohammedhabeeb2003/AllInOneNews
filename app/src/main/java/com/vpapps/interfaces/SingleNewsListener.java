package com.vpapps.interfaces;

import com.vpapps.item.ItemComment;
import com.vpapps.item.ItemNews;

import java.util.ArrayList;

public interface SingleNewsListener {
    void onStart();

    void onEnd(String success, ItemNews itemNews, ArrayList<ItemComment> arrayListComments);
}