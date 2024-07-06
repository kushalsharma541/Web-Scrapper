package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.script.Source;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import demo.wrappers.Utilities;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    @Test(alwaysRun = true, enabled = true)
    public void testCase01() throws InterruptedException {

        driver.get("https://www.scrapethissite.com/pages/");

        // Verify the current link with Assert assesment

        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.scrapethissite.com/pages/"), "Unverified URL");

        System.out.println("Verified URL: https://www.scrapethissite.com/pages/");

        WebElement hockeyTeamElement = driver.findElement(By.xpath("//a[contains(text(), 'Hockey Teams')]"));
        Wrappers.clickOnElement(hockeyTeamElement, driver);

        // initialize and declare a HasMap arrayList called dataList;

        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

        // locate page 1
        WebElement page1Element = driver.findElement(By.xpath("(//ul[@class = 'pagination']/li/a)[1]"));

        // click on page 1
        Wrappers.clickOnElement(page1Element, driver);

        // iterate through 4 pages
        for (int i = 1; i <= 4; i++) {

            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class = 'team']"));

            for (WebElement row : rows) {
                // Extract data from each row

                // And get the text from teamName locator

                String teamName = row.findElement(By.xpath(".//td[@class = 'name']")).getText();

                // get the text from year locator
                int year = Integer.parseInt(row.findElement(By.xpath(".//td[@class = 'year']")).getText());

                // get the Win from rows locator

                double winPercentage = Double
                        .parseDouble(row.findElement(By.xpath(".//td[contains(@class, 'pct')]")).getText());

                // Declare epoch Time

                long epoch = System.currentTimeMillis() / 1000;

                // convert epochTime into String
                String epochTime = String.valueOf(epoch);

                // check if win percentage is less than 4%

                if (winPercentage < 0.4) {
                    // create the HashMap to store the data , HashMap of String data type with Key
                    // of Object

                    HashMap<String, Object> dataMap = new HashMap<>();

                    dataMap.put("epochTime", epochTime);
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("winPercentage", winPercentage);

                    // add the HashMap to the arrayList

                    dataList.add(dataMap);
                }
            }
            // Navigate to the nextPage
            if (i < 4) {

                WebElement nextPagWebElement = driver.findElement(By.xpath("//a[@aria-label = 'Next']"));
                Wrappers.clickOnElement(nextPagWebElement, driver);

                // add some wait to ensure the page is fully loaded
                Thread.sleep(5000);
            }
        }

        // Print the collected data

        for (HashMap<String, Object> data : dataList) {
            System.out.println("epoch time of scraper:" + data.get("epochTime") + ", Team Name: " + data.get("teamName")
                    + ", Year" + data.get("year") + ", win percentage: " + data.get("winPercentage"));

        }

        // Store the HashMap data in json File

        ObjectMapper mapper = new ObjectMapper();

        try {

            File jsonFile = new File("src\\test\\resources\\hockey-team-data.json");
            mapper.writeValue(jsonFile, dataList);
            System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());

            Assert.assertTrue(jsonFile.length() != 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(alwaysRun = true, enabled = true)
    public void testCase02() {

        driver.get("https://www.scrapethissite.com/pages/");

        WebElement oscarWinningFiElement = driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));
        Wrappers.clickOnElement(oscarWinningFiElement, driver);

        Utilities.scrape("2015", driver);
        Utilities.scrape("2014", driver);
        Utilities.scrape("2013", driver);
        Utilities.scrape("2012", driver);
        Utilities.scrape("2011", driver);

    }

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest(alwaysRun = true, enabled = true)
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @AfterTest(alwaysRun = true, enabled = true)
    public void endTest() {
        // driver.close();
        driver.quit();

    }
}