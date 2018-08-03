import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
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
    public void testMobProxyServer() {

        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start();
        int port = proxy.getPort();

        proxy.addRequestFilter((request, contents, messageInfo) -> {
            System.out.println("--> --> --> --> --> --> --> --> --> --> --> --> --> --> --> -->");
            System.out.println("Intercepting request for url: " + messageInfo.getOriginalUrl());
            System.out.println("Request method: " + request.getMethod());
            System.out.println("Request uri: " + request.getUri());
            System.out.println("ChannelHandlerContext: " + messageInfo.getChannelHandlerContext().name());
            System.out.println("Content type: " + contents.getContentType());
            System.out.println("Content text: " + contents.getTextContents());

            return null;
        });

        proxy.addResponseFilter((response, contents, messageInfo) -> {
            System.out.println("<-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <-- <--");
            System.out.println("Intercepting response for url :" + messageInfo.getOriginalUrl());
            System.out.println("Response status: " + response.getStatus());
            System.out.println("ChannelHandlerContext: " + messageInfo.getChannelHandlerContext().name());
            System.out.println("Content type: " + contents.getContentType());
            if (!contents.getContentType().contains("image"))
                System.out.println("Content text: " + contents.getTextContents());
        });

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);

        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        proxy.newHar("googleStartPage");

        driver.get("https://www.google.com");
        Har har = proxy.getHar();
        Utils.writeHarToFile(har, "home");

        proxy.newHar("searchPage");
        GoogleHomePage page = new GoogleHomePage(driver);
        page.lookForAutomation();
        har = proxy.getHar();
        Utils.writeHarToFile(har, "search");


        System.out.println("Proxy started at port: " + port);
        for (HarEntry entry : har.getLog().getEntries()) {
            System.out.println("Request URL: " + entry.getRequest().getUrl());
            System.out.println("Server response in: " + entry.getTimings().getWait());
            System.out.println("Response status: " + entry.getResponse().getStatus());
        }


        driver.quit();
        proxy.stop();
    }
}
