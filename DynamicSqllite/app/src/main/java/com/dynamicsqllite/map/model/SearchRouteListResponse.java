package com.dynamicsqllite.map.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by prashant.patel on 12/13/2016.
 */

public class SearchRouteListResponse  {

    @SerializedName("r")
    public ArrayList<SearchRouteListModel> arrayListSearchRouteListModel;

    @SerializedName("t")
    public String t;

}
