package com.example.photoapp54.model;

import java.io.Serializable;

public class Tag implements Serializable {

    /**
     * SerialID for Tag Class
     * name of tag(tag type)
     * value of tag(tag value)
     * if it can have multiple values or not
     */
    private static final long serialVersionUID = 5294496920422236144L;
    public String name;
    public String value;
    public boolean multi;

    /**
     * Initialize tag
     *
     * @param name tag type of tag
     * @param value calue of tag
     * @param multi if tag can have multiple values or not
     */
    public Tag(String name, String value, boolean multi) {
        this.name = name.toLowerCase();
        this.value = value;
        this.multi = multi;
    }

    /**
     * Get the tag type of the tag
     *
     * @return the tag type of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of the tag
     *
     * @return the value of the tag
     */
    public String getValue() {
        return value;
    }

    /**
     * Get whether or not the tag can have multiple values or not
     *
     * @return true of it can have multiple values, false otherwise
     */
    public boolean getMulti() {
        return multi;
    }

    /**
     * Override equals method for tag
     * does it based on the tag type and tag value
     */
    @Override
    public boolean equals(Object other) {
        if(other==null || !(other instanceof Tag))
            return false;

        Tag curr =(Tag ) other;
        return curr.getName().toLowerCase().equals(name.toLowerCase()) && curr.getValue().toLowerCase().equals(value.toLowerCase());
    }

    /**
     * toString method for tag so that tag display is legible
     * Has tag type - tag value
     */
    public String toString() {
        return name + " - " + value;
    }
}