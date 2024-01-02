package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.*;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class MainPage {
    @FindBy(how=How.CSS, using = ".spendings-table tbody")
    private SelenideElement table;

    @FindBy(how=How.XPATH, using = "//button[contains(text(),'Delete selected')]")
    private SelenideElement deleteSelectedButton;

    public void waitForLoad() {
        table.shouldBe(visible);
    }
    public void selectSpendingByDescription(String description) {
        table
                .$$("tr")
                .find(text(description))
                .$$("td")
                .first()
                .scrollIntoView(true)
                .click();
    }

    public void deleteSpending() {
        deleteSelectedButton
                .scrollIntoView(true)
                .click();
    }

    public void tableShouldBeEmpty() {
        table
                .$$("tr").shouldHave(size(0));
    }
}
