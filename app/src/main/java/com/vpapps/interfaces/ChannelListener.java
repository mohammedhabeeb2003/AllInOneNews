package com.vpapps.interfaces;

import com.vpapps.item.ItemCat;

import java.util.ArrayList;

public interface ChannelListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, String name, String desc, String url, String type);
}