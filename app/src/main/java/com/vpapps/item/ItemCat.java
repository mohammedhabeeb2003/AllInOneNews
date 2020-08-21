package com.vpapps.item;

public class ItemCat {

    private String id, name, image, imageThumb;

    public ItemCat(String id, String name, String image, String imageThumb) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.imageThumb = imageThumb;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getImageThumb() {
        return imageThumb;
    }
}