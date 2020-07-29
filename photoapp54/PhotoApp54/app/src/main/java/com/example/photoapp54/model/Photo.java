package com.example.photoapp54.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {

    private static final long serialVersionUID = 6955723612371190680L;
    private ArrayList<Tag> tagList;
    private String photoName;
    //private SerializableBitmap bitmap;
    //private byte[] bytes;
    //String bytes;
    String path;

    public Photo(String path) {
        this.path = path;
        this.tagList = new ArrayList<>();
        if (path.contains("/"))
            this.photoName = path.substring(path.lastIndexOf('/') + 1);
        else
            this.photoName = path;
    }

    public String getPhotoName() {
        return photoName;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Tag> getTags() {
        return tagList;
    }

    public void addTag(Tag newTag) {
        for (int i = 0; i < tagList.size(); i++) {
            if (newTag.getName().equals(tagList.get(i).getName()) && !tagList.get(i).getMulti())
                return;

            if (newTag.equals(tagList.get(i)))
                return;
        }

        tagList.add(newTag);
    }

    public void removeTag(String name, String value) {
        Tag thisTag = new Tag(name,value,false);
        int position = 0;
        for(int i=0; i<tagList.size(); i++) {
            if(thisTag.equals(tagList.get(i))) {
                position = i;
                break;
            }
        }
        tagList.remove(position);
    }

    public boolean equals(Photo other) {
        return this.path.equals(other.getPath());
    }
}
