package com.vpapps.interfaces;

import com.vpapps.item.ItemCat;
import com.vpapps.item.ItemNews;

import java.util.ArrayList;

public interface NewsListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemNews> arrayList);
}