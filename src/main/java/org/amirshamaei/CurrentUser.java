package org.amirshamaei;

public class CurrentUser {
    private static User user;
    public static CurrentUser currentUser = new CurrentUser();

    public CurrentUser() {

    }

    public static void setUser(User user) {
        CurrentUser.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static CurrentUser getInstance() {
        return currentUser;
    }

}
