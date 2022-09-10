package thaipham.webscraper;

import com.google.gson.Gson;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class Product {

    private String Product_Name;
    private String Product_Price;
    private String Date;
    private String Source;

    // Getter
    public String getProductName() {
        return this.Product_Name;
    }

    // Setter
    public void setProductName(String name) {
        this.Product_Name = name;
    }

    // Getter
    public String getProductPrice() {
        return this.Product_Price;
    }

    // Setter
    public void setProductPrice(String price) {
        this.Product_Price = price;
    }

    // Getter
    public String getDate() {
        return this.Date;
    }

    // Setter
    public void setDate(String date) {
        this.Date = date;
    }

    // Getter
    public String getSource() {
        return this.Source;
    }

    // Setter
    public void setSource(String source) {
        this.Source = source;
    }

}

public class AppTest {

    ArrayList<String> resultIndexList = new ArrayList<>();
    ArrayList<WebElement> results = new ArrayList<>();
    ArrayList<Product> title_price_list = new ArrayList<>();
    WebDriver browser;
    WebElement searchAgent;

    public String changePageUrl(String pageNumber) {
        String url = MessageFormat.format("https://www.amazon.com/s?k=iphone+11&page={0}&crid=19QVE3PB66B8U&qid=1662432049&sprefix=iphone11%2Caps%2C417&ref=sr_pg_{0}", pageNumber);
        return url;
    }

    public void generateResultsIndex() {
        for (int i = 2; i <= 16; i++) {
            String formatNumber = Integer.toString(i);
            String indexId = MessageFormat.format("//*[@id=\"search\"]/div[1]/div[1]/div/span[3]/div[2]/div[{0}]", formatNumber);
            this.resultIndexList.add(indexId);
        }

    }

    public void generateResults() {
        for (int i = 0; i < this.resultIndexList.size(); i++) {
            WebElement result = this.searchAgent.findElement(By.xpath(this.resultIndexList.get(i)));
            this.results.add(result);
        }

    }

    public void formatResults() {
        Product listItem = new Product();
        String source = "Amazon";
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = formatter.format(today);

        for (WebElement result : this.results) {
            try {
                String name = result.findElement(By.cssSelector(".sg-col-inner .a-section  .sg-row .sg-col-inner h2 a span")).getText();
                String price = result.findElement(By.cssSelector(".sg-col-inner .a-section  .sg-row .sg-col-inner .sg-row .sg-col-inner a span span:nth-child(2) .a-price-whole")).getText();

                listItem.setProductName(name);
                listItem.setProductPrice(price);
                listItem.setDate(formattedDate);
                listItem.setSource(source);

                this.title_price_list.add(listItem);
            } catch (Exception e) {
                System.out.println("An exception occurred");
            }

        }

    }

    public void generateData(String page) {
        String url = this.changePageUrl(page);
        this.browser.get(url);
        this.searchAgent = this.browser.findElement(By.xpath("//*[@id=\"search\"]/div[1]/div[1]/div/span[3]/div[2]"));
        this.generateResults();
        this.formatResults();
        this.results.clear();
    }

    public void storeData() {
        String path = "../data.json";
        String json = new Gson().toJson(this.title_price_list);

        try ( PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(json);
        } catch (Exception e) {
            System.out.print("Something Wrong !");
        }
    }

    @Test
    public void run() {
        this.generateResultsIndex();
        WebDriverManager.chromedriver().setup();
        this.browser = new ChromeDriver();
        int counter = 1;
        while (counter < 4) {
            String formattedData = Integer.toString(counter);
            this.generateData(formattedData);
            counter += 1;
        }
        this.browser.quit();
        this.storeData();
        int finalData = this.title_price_list.size();
        System.out.println("Total rows: " + finalData);

    }

}
