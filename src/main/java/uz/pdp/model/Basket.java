package uz.pdp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Basket {

    private MyUser buyer;

    private LocalDateTime dateTime;

    private List<BasketProduct> basketProducts = new ArrayList<>();

    public Basket(MyUser buyer, LocalDateTime dateTime) {
        this.buyer = buyer;
        this.dateTime = dateTime;
    }
}
