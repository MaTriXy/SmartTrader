package com.stocktrak;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static com.stocktrak.AnalysisProcess.stockQueue;
import static com.stocktrak.Transaction.Type.*;
import java.util.Calendar;


/**
 * Created by Douglas on 2/13/2015.
 */
public class StockTrackerProcess extends Thread {
    public StockTrackerProcess() {

    }

    private static final String USERNAME = "qcampbell3";
    private static final String PASSWORD = "Dresser5";

    public void run() {
        Calendar cal = Calendar.getInstance();
        Calendar eob = Calendar.getInstance();
        eob.set(2015, Calendar.FEBRUARY, 14, 18, 52);
        WebDriver driver = new FirefoxDriver();
        driver.navigate().to("http://stocktrak.com");
        System.out.println(driver.getTitle());
        WebElement usernameField = driver.findElement(By.id("Login1_UserName"));
        WebElement passwordField = driver.findElement(By.id("Login1_Password"));
        WebElement submitLogin = driver.findElement(By.id("Login1_Login"));
        usernameField.sendKeys(USERNAME);
        passwordField.sendKeys(PASSWORD);
        submitLogin.click();
        log(cal.getTime());
        while(cal.getTime().before(eob.getTime()) || !stockQueue.isEmpty()) {
            log(cal.getTime());
            log(eob.getTime());
            try {
                makeTransaction(driver, new Transaction(1, "AAPL", BUY));
            } catch (ElementNotFoundException e) {

            }
        }
        driver.close();
        driver.quit();
    }

    public void makeTransaction(WebDriver driver, Transaction transaction) throws ElementNotFoundException {
        driver.navigate().to("http://stocktrak.com/private/trading/equities.aspx");

        WebDriverWait wait = new WebDriverWait(driver, 4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("ContentPlaceHolder1_Equities_tbSymbol"))));

        WebElement symbolField = driver.findElement(By.id("ContentPlaceHolder1_Equities_tbSymbol"));
        symbolField.sendKeys(transaction.getSymbol());
        symbolField.sendKeys("\t");
        wait = new WebDriverWait(driver, 4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("ContentPlaceHolder1_Equities_pPriceResult"))));

        WebElement quantityField = driver.findElement(By.id("ContentPlaceHolder1_Equities_tbQuantity"));
        quantityField.sendKeys(""+transaction.getQuantity());

        WebElement previewOrderButton = driver.findElement(By.id("ContentPlaceHolder1_Equities_btnPreviewOrder"));
        previewOrderButton.click();
        wait = new WebDriverWait(driver, 4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("ContentPlaceHolder1_Equities_btnPlaceOrder"))));

        WebElement placeOrderButton = driver.findElement(By.id("ContentPlaceHolder1_Equities_btnPlaceOrder"));
        placeOrderButton.click();
    }

    public void log(Object str) {
        System.out.println("STOCK_TRACKER: " + str.toString());
    }
    public void log(Number n) {
        System.out.println("STOCK_TRACKER: " + n.toString());
    }
}
