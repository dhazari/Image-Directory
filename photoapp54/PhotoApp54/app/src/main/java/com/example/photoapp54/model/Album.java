package com.example.photoapp54.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Album implements Serializable {
    /**
     * SerialID for Album Class
     * Title of album
     * Arraylist of pictures within album
     */
    private static final long serialVersionUID = -9155495546523356913L;
    public String title;
    public ArrayList<Photo> pictureList;// = new ArrayList<Photo>();

    /**
     * Initialize Album
     *
     * @param title of Album
     */
    public Album(String title) {
        this.title = title;
        pictureList = new ArrayList<>();
    }

    /**
     * get the album title
     *
     * @return Title of album
     */
    public String getTitle() {
        return title;
    }

    /**
     * set new title
     *
     * @param x new name of album
     */
    public void setTitle(String x) {
        this.title = x;
    }

    /**
     * Get the list of pictures in the album
     *
     * @return list of pictures in the album
     */
    public ArrayList<Photo> getPictureList(){
        return pictureList;
    }

    /**
     * Set the picture list of the album to the one provided
     *
     * @param newPictureList the picture list to set the album picture list to
     */
    public void setList(ArrayList<Photo> newPictureList) {
        this.pictureList = newPictureList;
    }

    /**
     * Add a picture to the album's picture list
     *
     * @param newPicture picture to add to the album's picture list(As long as it is not already there)
     */
    public void addPicture(Photo newPicture) {
        if (this.pictureList.size() != 0) {
            for(int i=0; i<this.pictureList.size(); i++) {
                if(this.pictureList.get(i).getPath().equals(newPicture.getPath()))
                    return;
            }
        }

        pictureList.add(newPicture);
    }

    /**
     * delete a picture from the album's picture list
     *
     * @param thisPicture Picture to delete
     */
    public void removePicture(Photo thisPicture) {
        int position = 0;
        for(int i=0; i<pictureList.size(); i++) {
            if(thisPicture.getPath().equals(pictureList.get(i).getPath())) {
                position = i;
                break;
            }
        }
        pictureList.remove(position);
    }

    @Override
    public boolean equals(Object other) {
        if(other==null || !(other instanceof Album))
            return false;

        Album curr =(Album) other;
        return curr.getTitle().toLowerCase().equals(title.toLowerCase());
    }

    /**
     * Override hashCode for Album
     */
    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    /**
     * toString method for album to that album display is legible
     * Has album title, date rage, and number of pictures in the album
     */
    public String toString() {
        String ret = this.getTitle();
        ret += "\n"+Integer.toString(this.pictureList.size())+" Photos";
        return ret;
    }

    /*private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readObject();
    }*/

}
