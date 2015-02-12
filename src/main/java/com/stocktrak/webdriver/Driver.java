package com.stocktrak.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by Douglas on 2/10/2015.
 */
public class Driver {
    public Driver() {
    }

    public static void main(String[] args) {
        WebDriver driver = new FirefoxDriver();
        driver.navigate().to("http://stocktrak.com");
        System.out.println(driver.getTitle());
        WebElement usernameField = driver.findElement(By.id("Login1_UserName"));
        WebElement passwordField = driver.findElement(By.id("Login1_Password"));
        WebElement submitLogin = driver.findElement(By.id("Login1_Login"));
        usernameField.sendKeys("qcampbell3");
        passwordField.sendKeys("Dresser5");
        submitLogin.click();
        driver.close();
        driver.quit();
    }

}
