package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.DbUser;
import guru.qa.niffler.jupiter.UserRepositoryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(UserRepositoryExtension.class)
public class LoginTest extends BaseWebTest {
  UserRepository userRepository;
  @DbUser(username = "jack", password = "12345")
  @Test
  void statisticShouldBeVisibleAfterLogin(UserAuthEntity userAuth) {
    Selenide.open("http://127.0.0.1:3000/main");
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(userAuth.getUsername());
    $("input[name='password']").setValue(userAuth.getPassword());
    $("button[type='submit']").click();
    $(".main-content__section-stats").should(visible);
  }

  //Для самопроверки
  @Test
  @ExtendWith(UserRepositoryExtension.class)
  void checkCRUD() {
    UUID uuid = UUID.fromString("e73f4200-0af8-481e-a7e8-1f44a9df1b29");
    UserAuthEntity user = userRepository.findByIdInAuth(uuid).get();
    user.setEnabled(false);
    userRepository.updateInAuth(user);
  }
}