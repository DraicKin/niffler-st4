package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement table = $(".spendings-table tbody");
    private final SelenideElement deleteSelectedButton
            = $(By.xpath("//button[contains(text(),'Delete selected')]"));

    public void selectSpendingByDescription(String description) {
        table
                .$$("tr")
                .find(text(description))
                .$("td")
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
