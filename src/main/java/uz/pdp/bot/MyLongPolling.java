package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import uz.pdp.model.MyUser;
import uz.pdp.model.StateEnum;
import uz.pdp.service.DBService;

import static uz.pdp.service.DBService.users;

public class MyLongPolling extends TelegramLongPollingBot {

    private MyLogic myLogic = new MyLogic();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();

                if (message.hasLocation()) {
                    SendMessage sendMessage = myLogic.getLocation(update);
                    execute(sendMessage);
                } else if (message.hasText()) {
                    String text = message.getText();
                    switch (text) {
                        case "/start":
                            SendMessage sendMessage = myLogic.startMsg(update);
                            execute(sendMessage);
                            break;
                        case "\uD83C\uDF74 Menyu":
                            sendMessage = myLogic.address(update);
                            execute(sendMessage);
                            break;
                        case "\uD83D\uDDFA Mening manzillarim":
                            sendMessage = myLogic.oldAddresses(update);
                            execute(sendMessage);
                            break;
                        case "✅ Ha":
                            sendMessage = myLogic.acceptLocation(update);
                            execute(sendMessage);
                            break;
                        case "❌ Yo'q":
                            sendMessage = myLogic.address(update);
                            execute(sendMessage);
                            break;
                        case "⬅️ Ortga":
                            sendMessage = myLogic.back(update);
                            execute(sendMessage);
                            break;
                        case "⚙️ Настройки":
                            SendPhoto sendPhoto = myLogic.sendRasm(update);
                            execute(sendPhoto);
                            break;
                        default:
                            MyUser myUser = users.get(update.getMessage().getChatId());
                            if (myUser != null) {
                                if (myUser.getState().equals(StateEnum.ROOT_CATEGORY) ||
                                        myUser.getState().equals(StateEnum.CHILD_CATEGORY)
                                ) {

                                    sendMessage = myLogic.getCategoriesOrProducts(update);

                                    execute(sendMessage);

                                    ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
                                    if (replyMarkup instanceof InlineKeyboardMarkup) {
                                        sendMessage = myLogic.basketAndBack(update);
                                        execute(sendMessage);
                                    }
                                }
                            } else {
                                sendMessage = myLogic.defaultMessage(update);
                                execute(sendMessage);
                            }
                            break;

                    }
                }
            } else if (update.hasCallbackQuery()) {
                //todo if condition ko'p bo'lsa, state bilan ishlaysiz
                EditMessageText editMessageText = myLogic.editMessage(update);
                execute(editMessageText);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {

    }

    @Override
    public String getBotUsername() {
        return "https://t.me/akmal85bot";
    }

    @Override
    public String getBotToken() {
        return "6323763054:AAE1XOelcxAa4M194joiA33-aXCS15K2bH8";
    }
}
