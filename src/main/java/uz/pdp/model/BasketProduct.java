package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasketProduct {

//    private Basket basket;

    private Product product;

    private Integer count;

    private boolean clientAdded;


}
