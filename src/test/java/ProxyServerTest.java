import io.restassured.path.json.JsonPath;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.GoogleHomePage;
import utils.Utils;

import java.util.concurrent.TimeUnit;

public class ProxyServerTest {

    @Test
    public void testMobProxyServer() throws InterruptedException {

        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start();

        proxy.addRequestFilter((request, contents, messageInfo) -> {
            System.out.println("--> --> --> --> --> --> --> --> --> --> --> --> --> --> --> -->");
            System.out.println("Intercepting request for url: " + messageInfo.getOriginalUrl());
            System.out.println("Request method: " + request.getMethod());
            System.out.println("Request uri: " + request.getUri());
            System.out.println("ChannelHandlerContext: " + messageInfo.getChannelHandlerContext().name());
            System.out.println("Content type: " + contents.getContentType());

            if (request.getUri().contains("Automation")) {
                String original = request.getUri();
                String changed = original.replaceAll("Automation", "Bla-Bla-Bla");
                request.setUri(changed);
            }

            JsonPath content = JsonPath.given(contents.getTextContents());
            System.out.println("Content is:\n" + content.prettyPrint());

            return null;
        });

        proxy.addResponseFilter((response, contents, messageInfo) -> {
            System.out.println("<-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <--");
            System.out.println("Intercepting response for url :" + messageInfo.getOriginalUrl());
            System.out.println("Response status: " + response.getStatus());
            System.out.println("ChannelHandlerContext: " + messageInfo.getChannelHandlerContext().name());
            System.out.println("Content type: " + contents.getContentType());

            //contents.setTextContents("I am trying to manipulate with response text))");

            if (!contents.getContentType().contains("image")) {
                JsonPath content = JsonPath.given(contents.getTextContents());
                System.out.println("Content text:\n" + content.prettyPrint());
            }
        });

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);

        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //proxy.enableHarCaptureTypes(CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_CONTENT);

        proxy.newHar("googleStartPage");

        driver.get("https://www.google.com");
        Har har = proxy.getHar();
        Utils.writeHarToFile(har, "home");

        proxy.newHar("searchPage");
        GoogleHomePage page = new GoogleHomePage(driver);
        page.lookForAutomation();
        har = proxy.getHar();
        Utils.writeHarToFile(har, "search");


        Thread.sleep(2000);
        driver.quit();
        proxy.stop();
    }
}
