package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class OpeningTheDemoWebsite {
	ExtentSparkReporter reporter;
	ExtentReports extent;
	WebDriver driver;
	@BeforeSuite
	public void reportSetup() {
		reporter = new ExtentSparkReporter("Opening the demo website report.html");
		extent=new ExtentReports();
		extent.attachReporter(reporter);
	}
	@BeforeTest
	public void setUpTest() {
		WebDriverManager.chromedriver().setup();
		
	}
	@Test
	public void openDemoSite() throws Exception {
		ExtentTest test = extent.createTest("Test 1","Opening the demo website");
		driver= new ChromeDriver();
		driver.get("https://www.saucedemo.com/");
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest + "img.png"));
		test.pass("Navigated to saucedemo.com:", MediaEntityBuilder
                .createScreenCaptureFromPath(System.getProperty("user.dir") + "img.png").build());
		//test.pass("Navigated to saucedemo.com",MediaEntityBuilder.createScreenCaptureFromPath("img.png").build());
		//test.addScreenCaptureFromPath("img.png");
	}
	@AfterTest
	public void tearDown() {
		driver.quit();
	}
	  @AfterSuite
	  public void tearDownReport ()
	  {
	 	  extent.flush();
	 	  
	  }
}
