package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GoogleHomePage {

    @FindBy(id = "lst-ib")
    WebElement searchField;

    public GoogleHomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void lookForAutomation(){
        searchField.sendKeys("Automation");
        searchField.submit();
    }
}
