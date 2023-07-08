package uz.pdp.model;

import org.telegram.telegrambots.meta.api.objects.User;

public class MyUser {

    private final User user;

    private StateEnum state = StateEnum.MAIN_PAGE;

    private Category lastCategory;//lavash

    public MyUser(User user, StateEnum state) {
        this.user = user;
        this.state = state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public void setLastCategory(Category lastCategory) {
        this.lastCategory = lastCategory;
    }

    public User getUser() {
        return user;
    }

    public StateEnum getState() {
        return state;
    }

    public Category getLastCategory() {
        return lastCategory;
    }
}
