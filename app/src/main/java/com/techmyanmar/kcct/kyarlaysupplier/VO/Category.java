package com.techmyanmar.kcct.kyarlaysupplier.VO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category extends  {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("categories")
    private List<SubCategory> categoryArrayList;

    public Category(){}

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

    public List<SubCategory> getCategoryArrayList() {
        return categoryArrayList;
    }

    public void setCategoryArrayList(List<SubCategory> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
}
