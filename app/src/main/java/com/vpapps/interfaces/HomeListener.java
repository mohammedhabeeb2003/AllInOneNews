package com.vpapps.interfaces;

import com.vpapps.item.ItemCat;
import com.vpapps.item.ItemNews;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemNews> arrayListLatest, ArrayList<ItemNews> arrayListTrending, ArrayList<ItemNews> arrayListTop, ArrayList<ItemCat> arrayListCat);
}