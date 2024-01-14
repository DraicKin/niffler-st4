package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MenuBar;
import guru.qa.niffler.pages.PeoplePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.User.UserType.*;

@ExtendWith(UsersQueueExtension.class)
public class InvTestWithBothAnnotations {
    static {
        Configuration.browserSize = "1980x1024";
    }


    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        LoginPage loginPage = Selenide.open("http://127.0.0.1:3000/main", LoginPage.class);
        loginPage.login(user.username(), user.testData().password());
    }

    @Test
    @DisplayName("В списке всех пользователей есть существующий пользователь")
    void friendShouldExist(@User(COMMON) UserJson user2) {
        MenuBar menuBar = new MenuBar();
        PeoplePage peoplePage = menuBar.clickAllPeopleButton();
        peoplePage.personExistsInAllPeople(user2.username());
    }
}
