package com.restaurant.model;

public class MenuItem {
    private String itemId;
    private String name;
    private double price;
    private String description;
    private MenuCategory category;
    private boolean available;

    public MenuItem(String itemId, String name, double price, String description, MenuCategory category, boolean available) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.itemId      = itemId;
        this.name        = name;
        this.price       = price;
        this.description = description;
        this.category    = category;
        this.available   = available;
    }

    public String       getItemId()                     { return itemId; }
    public String       getName()                       { return name; }
    public void         setName(String name)            { this.name = name; }
    public double       getPrice()                      { return price; }
    public void         setPrice(double price)          {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    public String       getDescription()                { return description; }
    public void         setDescription(String d)        { this.description = d; }
    public MenuCategory getCategory()                   { return category; }
    public void         setCategory(MenuCategory c)     { this.category = c; }
    public boolean      isAvailable()                   { return available; }
    public void         setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return name + " - (" + price + " EGP)" + (available ? "" : " [UNAVAILABLE]");
    }
}
