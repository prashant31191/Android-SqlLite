package com.dynamicsqllite.map.model;

import com.google.gson.annotations.SerializedName;

public class SearchRouteListModel {

    @SerializedName("rid")
    public String rid;

    @SerializedName("st_lat")
    public String st_lat;

    @SerializedName("st_long")
    public String st_long;

    @SerializedName("name")
    public String name;

    @SerializedName("length")
    public String length;

    @SerializedName("time")
    public String time;

    @SerializedName("rimg")
    public String rimg;

    @SerializedName("dist")
    public String dist;

    @SerializedName("uid")
    public String uid;
}
