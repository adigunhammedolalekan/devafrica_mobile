package com.beem24.projects.devafrica.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 6/13/2017.
 */

public class Photo {

    public String mPath = "";
    public boolean isSelected = false;


    public static Photo from(JSONObject jsonObject) throws JSONException {
        Photo photo = new Photo();
        photo.mPath = jsonObject.getString("url");

        return photo;
    }

    public Photo() {}
    public Photo(String path) {
        mPath = path;
    }
}
