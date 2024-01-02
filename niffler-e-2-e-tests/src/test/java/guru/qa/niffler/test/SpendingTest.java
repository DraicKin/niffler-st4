package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.DisabledByIssue;
import guru.qa.niffler.jupiter.GenerateCategory;
import guru.qa.niffler.jupiter.GenerateSpend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SpendingTest extends BaseWebTest {

  static {
    Configuration.browserSize = "1980x1024";
  }

  private MainPage mainPage;
  @BeforeEach
  void doLogin() {
    LoginPage loginPage = Selenide.open("http://127.0.0.1:3000/main", LoginPage.class);
    mainPage = loginPage.login("duck", "12345");
  }

  @GenerateCategory(
          username = "duck",
          category = "Обучение"
  )
  @GenerateSpend(
          username = "duck",
          description = "QA.GURU Advanced 4",
          amount = 72500.00,
          category = "Обучение",
          currency = CurrencyValues.RUB
  )
  //@DisabledByIssue("74")
  @Test
  void spendingShouldBeDeletedByButtonDeleteSpending(SpendJson spend) {
    mainPage.selectSpendingByDescription(spend.description());

    Allure.step("Delete spending", () -> mainPage.deleteSpending());
    Allure.step("Check that spending was deleted", () -> mainPage.tableShouldBeEmpty());
  }
}
