package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement redirect = $("a[href*='redirect']");
    private final SelenideElement username = $("input[name='username']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");

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
        return new MainPage();
    }

}
