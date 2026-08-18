package com.restaurant.model;

public class MenuCategory {
    private String categoryId;
    private String name;
    private String description;

    public MenuCategory(String categoryId, String name, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
    }

    public String getCategoryId()             { return categoryId; }
    public void   setCategoryId(String id)    { this.categoryId = id; }
    public String getName()                   { return name; }
    public void   setName(String name)        { this.name = name; }
    public String getDescription()            { return description; }
    public void   setDescription(String d)    { this.description = d; }

    @Override
    public String toString() { return name; }
}
