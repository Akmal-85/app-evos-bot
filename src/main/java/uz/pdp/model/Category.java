package uz.pdp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

    private Integer id;

    private String name;

    private Category parent;

    private boolean inline;

    public Category(Integer id, String name, Category parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


}
