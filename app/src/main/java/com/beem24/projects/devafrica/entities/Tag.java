package com.beem24.projects.devafrica.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class Tag {

    public String ID = "";
    public String tagName = "";

    public Tag() {}

    public Tag(String name) {
        tagName = name;
    }

    public static Tag from(JSONObject jsonObject) throws JSONException {
        Tag tag = new Tag();

        tag.tagName = jsonObject.getString("tag");
        tag.ID = jsonObject.getString("tag_id");

        return tag;
    }
}
