package uz.pdp.service;

import uz.pdp.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DBService {

    public static final Set<Category> categories = new HashSet<>();

    public static final Set<Product> products = new HashSet<>();

    public static final Map<Long, MyUser> users = new ConcurrentHashMap<>();

    public static final Map<Long, Set<Order>> orders = new ConcurrentHashMap<>();

    public static final Map<Long, Basket> baskets = new ConcurrentHashMap<>();

    static {
        Category lavash = new Category(1, "Lavash");
        Category shaurma = new Category(2, "Shaurma");
        Category burger = new Category(3, "Burger");
        Category salqin = new Category(4, "Salqin");

        Category tovuqli = new Category(5, "Tovuqli", lavash);
        tovuqli.setInline(true);
        Category mollik = new Category(6, "Mollik", lavash);

        categories.addAll(List.of(lavash, shaurma, burger, salqin, tovuqli, mollik));


        Product miniLavash = new Product(1, "MiniLavash", "", 28000, tovuqli);
        Product bigLavash = new Product(2, "BigLavash", "", 31000, tovuqli);

        Product miniLavashMol = new Product(3, "MiniLavash", "", 28000, mollik);
        Product bigLavashMol = new Product(4, "BigLavash", "", 31000, mollik);

        Product fitter = new Product(5, "Fitter", "", 30000, lavash);

        products.addAll(List.of(miniLavash,
                bigLavash,
                miniLavashMol,
                bigLavashMol,
                fitter));
    }


}
