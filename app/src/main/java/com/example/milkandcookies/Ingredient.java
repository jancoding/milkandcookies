package com.example.milkandcookies;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject implements Serializable {
    public static final String KEY_ORIGINAL = "original_string";
    public static final String KEY_MODIFIED = "modified_string";
    public static final String KEY_NAME = "name";
    public static final String KEY_USAMOUNT = "us_amount";
    public static final String KEY_USUNIT = "us_unit";
    public static final String KEY_METRICAMOUNT = "metric_amount";
    public static final String KEY_METRICUNIT = "metric_unit";


    public String getOriginal() {
        return getString(KEY_ORIGINAL);
    }

    public void setOriginal(String original) {
        put(KEY_ORIGINAL, original);
    }

    public String getModified() {
        return getString(KEY_MODIFIED);
    }

    public void setModified(String original) {
        put(KEY_MODIFIED, original);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public double getUSAmount() {
        return getDouble(KEY_USAMOUNT);
    }

    public void setUSAmount(double amount) {
        put(KEY_USAMOUNT, amount);
    }

    public String getUSUnit() {
        return getString(KEY_USUNIT);
    }

    public void setUSUnit(String unit) {
        put(KEY_USUNIT, unit);
    }

    public double getMetricAmount() {
        return getDouble(KEY_METRICAMOUNT);
    }

    public void setMetricAmount(double amount) {
        put(KEY_METRICAMOUNT, amount);
    }

    public String getMetricUnit() {
        return getString(KEY_METRICUNIT);
    }

    public void setMetricUnit(String unit) {
        put(KEY_METRICUNIT, unit);
    }


}
