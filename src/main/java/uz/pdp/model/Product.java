package uz.pdp.model;

import lombok.Getter;

@Getter
public class Product {

    private Integer id;

    private String name;

    private String photoUrl;

    private double price;

    private Category category;

    public Product(Integer id, String name, String photoUrl, double price, Category category) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.price = price;
        this.category = category;
    }
}
