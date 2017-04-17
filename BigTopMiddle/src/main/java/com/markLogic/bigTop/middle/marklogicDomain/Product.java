package com.markLogic.bigTop.middle.marklogicDomain;

import java.util.List;

public class Product {
    private String name;
    private float cost;
    private float price;
    private List<String> properties;

    @Override
    public String toString() {
        return "Product [name=" + name + ", cost=" + cost + ", price=" + price + ", properties=" + properties + "]";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public float getCost() {
        return cost;
    }
    public void setCost(float cost) {
        this.cost = cost;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public List<String> getProperties() {
        return properties;
    }
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
