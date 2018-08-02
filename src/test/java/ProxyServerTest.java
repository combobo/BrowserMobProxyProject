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
import utils.Utils;

public class ProxyServerTest {

    @Test
    public void testMobProxyServer(){

        // create and start proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(5050);

        // Selenium proxy instance
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // configuration web driver options
        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);

        // initialization of web driver
        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);

        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        // creation of new HAR
        proxy.newHar("google.com");

        driver.get("https://www.google.com");

        // getting the HAR and saving it fot analise (http://www.softwareishard.com/har/viewer/)
        Har har = proxy.getHar();
        Utils.writeHarToFile(har);

        // getting some logs
        for (HarEntry entry : har.getLog().getEntries()){
            System.out.println(entry.getRequest().getUrl());
            System.out.println(entry.getTimings().getWait());
            System.out.println(entry.getTimings().getReceive());
        }

        // stop proxy
        driver.quit();
        proxy.stop();
    }
}
