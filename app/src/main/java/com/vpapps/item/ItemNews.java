package com.vpapps.item;

import java.util.ArrayList;

public class ItemNews {

    private String id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, imageThumb, tot_views;
    private ArrayList<String> arrayList;

    public ItemNews(String id, String cat_id, String cat_name, String type, String heading, String desc, String video_id, String video_url, String date, String image,  String imageThumb, String tot_views, ArrayList<String> arrayList) {
        this.id = id;
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.date = date;
        this.heading = heading;
        this.image = image;
        this.desc = desc;
        this.type = type;
        this.video_id = video_id;
        this.video_url = video_url;
        this.arrayList = arrayList;
        this.tot_views = tot_views;
        this.imageThumb = imageThumb;
    }

    public String getId() {
        return id;
    }

    public String getCatId() {
        return cat_id;
    }

    public String getCatName() {
        return cat_name;
    }

    public String getDate() {
        return date;
    }

    public String getHeading() {
        return heading;
    }

    public String getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public String getVideoId() {
        return video_id;
    }

    public String getVideoUrl() {
        return video_url;
    }

    public String getTotalViews() {
        return tot_views;
    }

    public ArrayList<String> getGalleryList() {
        return arrayList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCatId(String cat_id) {
        this.cat_id = cat_id;
    }

    public void setCatName(String cat_name) {
        this.cat_name = cat_name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVideoId(String video_id) {
        this.video_id = video_id;
    }

    public void setVideoUrl(String video_url) {
        this.video_url = video_url;
    }

    public void setTotalViews(String tot_views) {
        this.tot_views = tot_views;
    }

    public void setGalleryList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public String getImageThumb() {
        return imageThumb;
    }
}