package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.User.UserType.*;

@ExtendWith(UsersQueueExtension.class)
public class InvitationFriendsTest {
    static {
        Configuration.browserSize = "1980x1024";
    }

    private LoginPage loginPage;
    private MenuBar menuBar;

    @BeforeEach
    void doLogin() {
        loginPage = Selenide.open("http://127.0.0.1:3000/main", LoginPage.class);
    }

    @Test
    @DisplayName("Есть отправленный запрос")
    void pendingInvitationShouldExist(@User(INVITATION_SENT) UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        menuBar = new MenuBar();
        PeoplePage peoplePage = menuBar.clickAllPeopleButton();
        peoplePage.pendingInvitationExists();
    }

    @Test
    @DisplayName("Есть входящий запрос")
    void incomingInvitationShouldExist(@User(INVITATION_RECIEVED) UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        menuBar = new MenuBar();
        FriendsPage friendsPage = menuBar.clickFriendsButton();
        friendsPage.friendInvitationExists();
    }

    @Test
    @DisplayName("В списке друзей есть друг")
    void friendShouldExist(@User(WITH_FRIENDS) UserJson user) {
        loginPage.login(user.username(), user.testData().password());
        menuBar = new MenuBar();
        FriendsPage friendsPage = menuBar.clickFriendsButton();
        friendsPage.friendExists();
    }

    @Test
    @DisplayName("В списке всех пользователей есть существующий пользователь")
    void friendShouldExist(@User(WITH_FRIENDS) UserJson user1, @User(COMMON) UserJson user2) {
        loginPage.login(user1.username(), user1.testData().password());
        menuBar = new MenuBar();
        PeoplePage peoplePage = menuBar.clickAllPeopleButton();
        peoplePage.personExistsInAllPeaople(user2.username());
    }


}
