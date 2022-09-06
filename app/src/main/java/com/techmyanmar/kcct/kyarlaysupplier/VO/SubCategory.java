package com.techmyanmar.kcct.kyarlaysupplier.VO;

import com.google.gson.annotations.SerializedName;

public class SubCategory extends UniversalPost {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public SubCategory(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
