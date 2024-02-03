package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MenuBar;
import guru.qa.niffler.pages.PeoplePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.User.UserType.INVITATION_SENT;

@ExtendWith(UsersQueueExtension.class)
public class InvitationFriendsTestWithAnnotationInBeforeEach {
    static {
        Configuration.browserSize = "1980x1024";
    }


    @BeforeEach
    void doLogin(@User(INVITATION_SENT) UserJson user) {
        LoginPage loginPage = Selenide.open("http://127.0.0.1:3000/main", LoginPage.class);
        loginPage.login(user.username(), user.testData().password());
    }

    @Test
    @DisplayName("Есть отправленный запрос")
    void pendingInvitationShouldExist() {
        MenuBar menuBar = new MenuBar();
        PeoplePage peoplePage = menuBar.clickAllPeopleButton();
        peoplePage.pendingInvitationExists();
    }
}
