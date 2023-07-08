package uz.pdp.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import static uz.pdp.service.DBService.*;


public class MyLogic {


    public SendMessage startMsg(Update update) {
        Long chatId = update.getMessage().getChatId();

        users.putIfAbsent(chatId, new MyUser(update.getMessage().getFrom(), StateEnum.MAIN_PAGE));

        SendMessage sendMessage = mainPage(chatId);

        return sendMessage;
    }

    private SendMessage mainPage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите одно из следующих");

        ReplyKeyboardMarkup buttons = mainPage();
        sendMessage.setReplyMarkup(buttons);
        return sendMessage;
    }


    public SendMessage defaultMessage(Update update) {
        return null;
    }


    public SendMessage address(Update update) {
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        myUser.setState(StateEnum.ADDRESS);


        SendMessage sendMessage = new SendMessage(chatId.toString(), "\uD83D\uDCCD Geolokatsiyani yuboring yoki yetkazib berish manzilini tanlang");

        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardButton button1 = new KeyboardButton("\uD83D\uDDFA Mening manzillarim");
        KeyboardButton button2 = new KeyboardButton("\uD83D\uDCCD Geolokatsiyani yuboring");
        button2.setRequestLocation(true);

        KeyboardButton button3 = new KeyboardButton("⬅️ Ortga");

        KeyboardRow row = new KeyboardRow();
        row.add(button1);
        rows.add(row);

        row = new KeyboardRow();
        row.add(button2);
        row.add(button3);
        rows.add(row);

        buttons.setKeyboard(rows);

        sendMessage.setReplyMarkup(buttons);

        return sendMessage;
    }

    public SendMessage oldAddresses(Update update) {
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        myUser.setState(StateEnum.OLD_ADDRESSES);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());

        Set<Order> userOrders = orders.get(chatId);
        if (userOrders == null) {
            sendMessage.setText("Bo'sh");
            return sendMessage;
        }

        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        for (Order userOrder : userOrders) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(userOrder.getAddress()));
            rows.add(row);
        }

        buttons.setKeyboard(rows);

        sendMessage.setReplyMarkup(buttons);

        return sendMessage;
    }


    public SendMessage getLocation(Update update) {
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        myUser.setState(StateEnum.LOCATION_ACCEPTED);

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Buyurtma bermoqchi bo'lgan manzil: Узбекистан, Ташкент, улица Беруни, 3А Ushbu manzilni tasdiqlaysizmi?");


        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("❌ Yo'q"));
        row.add(new KeyboardButton("✅ Ha"));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton("⬅️ Ortga"));
        rows.add(row);

        buttons.setKeyboard(rows);

        sendMessage.setReplyMarkup(buttons);


        return sendMessage;
    }


    private ReplyKeyboardMarkup mainPage() {
        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardButton button1 = new KeyboardButton("\uD83C\uDF74 Menyu");
        KeyboardButton button2 = new KeyboardButton("\uD83D\uDECD Mening buyurtmalarim");
        KeyboardButton button3 = new KeyboardButton("✍️ Оставить отзыв");
        KeyboardButton button4 = new KeyboardButton("⚙️ Настройки");

        KeyboardRow row = new KeyboardRow();
        row.add(button1);
        rows.add(row);

        row = new KeyboardRow();
        row.add(button2);
        rows.add(row);

        row = new KeyboardRow();
        row.add(button3);
        row.add(button4);
        rows.add(row);

        buttons.setKeyboard(rows);
        return buttons;
    }

    public SendMessage back(Update update) {
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        if (myUser.getState().equals(StateEnum.ADDRESS))
            return mainPage(chatId);

        if (myUser.getState().equals(StateEnum.OLD_ADDRESSES)
                || myUser.getState().equals(StateEnum.LOCATION_ACCEPTED))
            return address(update);


        return new SendMessage(chatId.toString(), "UZE");
    }

    public SendMessage acceptLocation(Update update) {
        return rootCategory(update);
    }

    public SendMessage rootCategory(Update update) {
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        myUser.setState(StateEnum.ROOT_CATEGORY);

        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        buttons.setKeyboard(rows);

        Iterator<Category> iterator = getRoots().iterator();

        while (iterator.hasNext()) {
            KeyboardRow row = new KeyboardRow();

            Category category = iterator.next();
            row.add(new KeyboardButton(category.getName()));

            if (iterator.hasNext()) {
                category = iterator.next();
                row.add(new KeyboardButton(category.getName()));
            }
            rows.add(row);
        }

        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton("\uD83D\uDCE5 Savat"));
        row.add(new KeyboardButton("⬅️ Ortga"));
        rows.add(row);

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Bo'limni tanlang.");
        sendMessage.setReplyMarkup(buttons);

        return sendMessage;
    }

    private Set<Category> getRoots() {
        HashSet<Category> roots = new HashSet<>();
        for (Category category : categories)
            if (category.getParent() == null)
                roots.add(category);

        return roots;
    }

    public SendMessage getCategoriesOrProducts(Update update) {
        String name = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        MyUser myUser = users.get(chatId);
        myUser.setState(StateEnum.CHILD_CATEGORY);//product

        Category lastCategory = myUser.getLastCategory();
        Category category = getCategory(name, lastCategory);
        if (category == null) {
            Product product = getProduct(name, lastCategory);
            return makeProductToBasket(update, product);
        }
        myUser.setLastCategory(category);
        List<Product> productList = getChildrenProducts(category);
        List<Category> children = getChildrenCategory(category);

        if (category.isInline())
            return makeProductsForInline(update, category);

        Iterator<Category> categoryIterator = children.iterator();
        Iterator<Product> productIterator = productList.iterator();
        List<KeyboardRow> rows = new ArrayList<>();
        ReplyKeyboardMarkup buttons = new ReplyKeyboardMarkup();
        buttons.setResizeKeyboard(true);
        buttons.setKeyboard(rows);


        while (categoryIterator.hasNext() || productIterator.hasNext()) {

            KeyboardRow row = new KeyboardRow();

            boolean catAdded = false;
            if (categoryIterator.hasNext()) {
                row.add(new KeyboardButton(categoryIterator.next().getName()));
                catAdded = true;
                if (categoryIterator.hasNext()) {
                    row.add(new KeyboardButton(categoryIterator.next().getName()));
                    rows.add(row);
                    continue;
                }
            }

            if (productIterator.hasNext()) {
                row.add(new KeyboardButton(productIterator.next().getName()));
                if (productIterator.hasNext() && !catAdded)
                    row.add(new KeyboardButton(productIterator.next().getName()));
            }

            rows.add(row);
        }

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Rasm");
        sendMessage.setReplyMarkup(buttons);

        return sendMessage;
    }

    public List<Category> getChildrenCategory(Category parent) {
        List<Category> result = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() == parent)
                result.add(category);
        }

        return result;
    }

    public List<Product> getChildrenProducts(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory() == category)
                result.add(product);
        }

        return result;
    }

    private Category getCategory(String name, Category parent) {
        for (Category category : categories)
            if (category.getName().equals(name) && category.getParent() == parent)
                return category;
        return null;
    }

    private Product getProduct(String name, Category category) {
        for (Product product : products)
            if (product.getName().equals(name) && product.getCategory() == category)
                return product;
        return null;
    }

    private Product getProduct(Integer id) {
        for (Product product : products)
            if (product.getId().equals(id))
                return product;
        return null;
    }

    public SendMessage makeProductToBasket(Update update, Product product) {

        Long chatId = update.getMessage().getChatId();
        BasketProduct basketProduct = createOrGet(chatId, product);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Name: " + product.getName() + "\\n" + "Price: " + product.getPrice());
        InlineKeyboardMarkup keyboardMarkup = makeInlineForProduct(basketProduct);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private InlineKeyboardMarkup makeInlineForProduct(BasketProduct basketProduct) {

        InlineKeyboardButton minus = new InlineKeyboardButton("-");
        minus.setCallbackData(basketProduct.getProduct().getId() + "#" + "-");

        InlineKeyboardButton number = new InlineKeyboardButton(basketProduct.getCount().toString());
        number.setCallbackData("kerakmas");

        InlineKeyboardButton plus = new InlineKeyboardButton("+");
        plus.setCallbackData(basketProduct.getProduct().getId() + "#" + "+");

        InlineKeyboardButton basketButton = new InlineKeyboardButton("\uD83D\uDCE5 Savat qo'shish");
        basketButton.setCallbackData(basketProduct.getProduct().getId() + "#" + "addBasket");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = List.of(minus, number, plus);
        List<InlineKeyboardButton> row2 = List.of(basketButton);

        keyboardMarkup.setKeyboard(List.of(row1, row2));
        return keyboardMarkup;
    }

    public SendMessage makeProductsForInline(Update update, Category category) {
        Long chatId = update.getMessage().getChatId();
        List<Product> productList = getChildrenProducts(category);

        SendMessage message = new SendMessage();
        message.setText("Bu yerda rasm bor");
        message.setChatId(chatId);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            Product product = iterator.next();
            InlineKeyboardButton first = new InlineKeyboardButton(product.getName() + " " + product.getPrice());
            first.setCallbackData(product.getId() + "#" + "choose");
            row.add(first);
            if (iterator.hasNext()) {
                product = iterator.next();
                first = new InlineKeyboardButton(product.getName() + " " + product.getPrice());
                first.setCallbackData(product.getId() + "#" + "choose");
                row.add(first);
            }
            rows.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        return message;
    }

    private BasketProduct createOrGet(Long chatId, Product product) {
        Basket basket = baskets.get(chatId);
        if (basket == null) {
            basket = new Basket(users.get(chatId), LocalDateTime.now());
            baskets.put(chatId, basket);
        }

        List<BasketProduct> basketProducts = basket.getBasketProducts();

        for (BasketProduct basketProduct : basketProducts)
            if (basketProduct.getProduct() == product)
                return basketProduct;

        BasketProduct basketProduct = new BasketProduct(product, 1, false);
        basketProducts.add(basketProduct);

        return basketProduct;
    }

    public SendMessage basketAndBack(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Quyidagilardan birini tanlang");
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton basket = new KeyboardButton("\uD83D\uDCE5 Savat");
        row.add(basket);
        rows.add(row);

        KeyboardButton back = new KeyboardButton("⬅️ Ortga");
        row = new KeyboardRow();
        row.add(back);
        rows.add(row);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public EditMessageText editMessage(Update update) {
        CallbackQuery query = update.getCallbackQuery();
        String data = query.getData();
        Long chatId = query.getMessage().getChatId();

        String[] split = data.split("#");
        String productId = split[0];
        String action = split[1];
        Product product = getProduct(Integer.valueOf(productId));
        BasketProduct basketProduct = createOrGet(chatId, product);
        if (action.equals("addBasket"))
            basketProduct.setClientAdded(true);
        else if (action.equals("-")) {
            if (basketProduct.getCount() > 1)
                basketProduct.setCount(basketProduct.getCount() - 1);
        } else if (action.equals("+"))
            basketProduct.setCount(basketProduct.getCount() + 1);
        InlineKeyboardMarkup inlineKeyboardMarkup = makeInlineForProduct(basketProduct);
        EditMessageText editMessage = new EditMessageText("Bu yerda rasm");
        editMessage.setChatId(chatId);
        editMessage.setReplyMarkup(inlineKeyboardMarkup);

        editMessage.setMessageId(query.getMessage().getMessageId());
        return editMessage;
    }

    public SendPhoto sendRasm(Update update) {


        SendPhoto sendPhoto = new SendPhoto();
        Long chatId = update.getMessage().getChatId();
        sendPhoto.setChatId(chatId.toString());
        try {
            InputFile inputFile = new InputFile(new FileInputStream("lavash.jpg"),
                    "Ketmon");
            sendPhoto.setPhoto(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        sendPhoto.setCaption("Oka qalay");
        return sendPhoto;
    }
}
