package com.beem24.projects.devafrica.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class Language {

    public String name = "";
    public boolean selected = false;
    public String wikiLink = "";

    public Language(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.selected = false;
        this.wikiLink = jsonObject.getString("@id");
    }
    public Language() {}
}
