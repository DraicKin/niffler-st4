package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import static com.codeborne.selenide.Selenide.page;

public class LoginPage {
    @FindBy(how = How.CSS,using = "a[href*='redirect']")
    private SelenideElement redirect;

    @FindBy(how = How.CSS,using = "input[name='username']")
    private SelenideElement username;

    @FindBy(how = How.CSS,using = "input[name='password']")
    private SelenideElement password;

    @FindBy(how = How.CSS,using = "button[type='submit']")
    private SelenideElement submitButton;

    public void clickRedirectButton() {
        redirect.click();
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }
    public MainPage login(String username, String password){
        clickRedirectButton();
        setUsername(username);
        setPassword(password);
        clickSubmitButton();
        MainPage mainPage = page(MainPage.class);
        mainPage.waitForLoad();
        return mainPage;
    }

}
