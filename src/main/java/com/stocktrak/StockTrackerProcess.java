package com.stocktrak;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.stocktrak.transactional.AccountCash;
import com.stocktrak.transactional.Transaction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Calendar;
import java.util.Queue;


/**
 * Created by Douglas on 2/13/2015.
 */
public class StockTrackerProcess extends Thread {
    private Queue<Transaction> transactionQueue = AnalysisProcess.transactionQueue;
    private AccountCash accountCash = AnalysisProcess.accountCash;

    public StockTrackerProcess() {
    }

    private static final String LOG_TAG = "StockTrackerProcess";
    private static final String USERNAME = "qcampbell3";
    private static final String PASSWORD = "Dresser5";

    public void run() {
        Calendar cal = Calendar.getInstance();
        Calendar eob = Calendar.getInstance();
        eob.set(2015, Calendar.FEBRUARY, 14, 18, 52);
        WebDriver driver = new FirefoxDriver();
        login(driver, USERNAME, PASSWORD);
        log(cal.getTime());
        while(!transactionQueue.isEmpty()) {
            log(cal.getTime());
            log(eob.getTime());
            try {
                makeTransaction(driver, transactionQueue.remove());
            } catch (ElementNotFoundException e) {
                log(e.getClass() + ": " + e.getMessage());
            }
        }
        driver.close();
        driver.quit();
    }

    private void login(WebDriver driver, String username, String password) {
        driver.navigate().to("http://stocktrak.com");
        System.out.println(driver.getTitle());
        WebElement usernameField = driver.findElement(By.id("Login1_UserName"));
        WebElement passwordField = driver.findElement(By.id("Login1_Password"));
        WebElement submitLogin = driver.findElement(By.id("Login1_Login"));
        usernameField.sendKeys(USERNAME);
        passwordField.sendKeys(PASSWORD);
        submitLogin.click();
    }

    private void makeTransaction(WebDriver driver, Transaction transaction) throws ElementNotFoundException {
        driver.navigate().to("http://stocktrak.com/private/trading/equities.aspx");

        WebDriverWait wait = new WebDriverWait(driver, 4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("ContentPlaceHolder1_Equities_tbSymbol"))));

        WebElement action = driver.findElement(By.id("ContentPlaceHolder1_Equities_ddlOrderSides"));
        action.sendKeys(transaction.getType().toString());

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

        Double transactionDollarAmount = Double.parseDouble(
                driver.findElement(By.xpath("/html/body/form[@id='form1']" +
                    "/div[@id='wrapper']/div[@class='inner-wrapper']" +
                    "/div[@class='content bg-pageContent']/div[@class='index']/div[@class='left-col']" +
                    "/div[@class='introduction-box']/div[2]/div[@id='ContentPlaceHolder1_Equities_TradePanel']" +
                    "/div[@id='ContentPlaceHolder1_Equities_UpdatePanel1']/table[@class='data']/tbody/tr[2]/td[7]"))
                .getText());
        WebElement placeOrderButton = driver.findElement(By.id("ContentPlaceHolder1_Equities_btnPlaceOrder"));
        placeOrderButton.click();

        if(transaction.equals(Transaction.Type.BUY)) {
            accountCash.decreaseCurrentBy(transactionDollarAmount);
        } else if(transaction.equals(Transaction.Type.SELL)) {
            accountCash.increaseCurrentBy(transactionDollarAmount);
        }
    }

    public void log(Object str) {
        System.out.println(LOG_TAG + ": " + (str != null ? str.toString() : null));
    }
}
