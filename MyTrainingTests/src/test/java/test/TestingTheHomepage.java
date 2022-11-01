package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

import utils.ExcelUtils;

public class TestingTheHomepage {
	ExtentSparkReporter reporter;
	ExtentReports extent;
	WebDriver driver;
	Boolean isLoggedin=false;
	String productName="";
    String projectPath;
	String firstName;
	String lastName;
	String zipCode;
		@DataProvider(name= "UsersData")
		public  Object[][] getData()
		{
			projectPath=System.getProperty("user.dir");
			String excelPath = projectPath+"/excel/users.xlsx";
		Object data[][] =	testData(excelPath, "Sheet1");
		return data;
		}
	    public  Object[][] testData(String excelPath , String sheetName)
	    {
	    	ExcelUtils excel = new ExcelUtils(excelPath, sheetName);
	    int rowCount = excel.getRowCount();
	    int colCount =	excel.getColCount();
	    Object data[][] = new Object [rowCount-1][colCount];
	    for (int i =1;i<rowCount;i++)
	    {
	    	for (int j=0;j<colCount;j++)
	    	{
	    		String cellData = excel.getCellDataString(i, j);
	    	
	    		data[i-1][j]= cellData;
	    	}
	    	
	    }
	    	return data;
	    }
		@BeforeSuite
		public void reportSetup() {
			reporter = new ExtentSparkReporter("Testing the homepage report.html");
			extent=new ExtentReports();
			extent.attachReporter(reporter);
		}
		@BeforeTest
		public void setUpTest() {
			WebDriverManager.chromedriver().setup();
			ExtentTest test = extent.createTest("Navigating to the website","Opeinig the website");
			try {
			driver= new ChromeDriver();
			 driver.manage().window().maximize();
			driver.get("https://www.saucedemo.com/");
			test.pass("Navigated to saucedemo.com");

			firstName ="Sameer";
			lastName="Ameer";
			zipCode="112233";
			}
			catch (Exception e)
			{
				test.fail("Test failed: "+e.getMessage());
				
			}
		}
	public void logIn(String userName ,String password) throws Exception {
		ExtentTest test2 = extent.createTest(userName+": Login test"," Logging in as user: "+userName);
		try {
	    driver.findElement(By.id("user-name")).clear();	
		driver.findElement(By.id("user-name")).sendKeys(userName);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(password);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
		myLink.click();
		
		
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest +userName+productName+ "LoggedIn.png"));
		String expectedURL="https://www.saucedemo.com/inventory.html";
		String actualURL=driver.getCurrentUrl();
		if (expectedURL.equalsIgnoreCase(actualURL))
		{
			test2.pass("The user logged in as "+userName, MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName +productName+"LoggedIn.png").build());
			  isLoggedin=true;
		}
		else
		{
			test2.fail("Test failed: ", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName +productName+"LoggedIn.png").build());
			driver.findElement(By.id("user-name")).clear();	
			driver.findElement(By.id("password")).clear();
		}
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +userName+productName+ "LoggedIn.png"));
			test2.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName +productName+"LoggedIn.png").build());
			
			
		}
	}
	@Test(dataProvider = "UsersData")
	public void testHomePage(String userName , String password) throws Exception {
		
		logIn(userName, password);
		if (isLoggedin)
		{
			
		addToCart(userName);
		removeFromCart(userName);
		sortTest(userName);
		logOut(userName);
		
		}
		
	}
	public void addToCart(String userName) throws Exception {
		String buttonId;
		String clickedButtonId;
		String itemName;
		String excelPath = projectPath+"/excel/products.xlsx";
		ExcelUtils excel = new ExcelUtils(excelPath, "Sheet1");
		  int rowCount = excel.getRowCount();
		  ExtentTest test3 = extent.createTest(userName+": Add to cart","Add to cart as user: "+userName);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn")));
			myLink.click();
			WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink2 =	wait2.until(ExpectedConditions.elementToBeClickable(By.id("reset_sidebar_link")));
			myLink2.click();
			driver.navigate().refresh();
		for (int i=1;i<rowCount;i++)
		{
			buttonId=excel.getCellDataString(i, 0);
			itemName=excel.getCellDataString(i, 1);
			clickedButtonId=excel.getCellDataString(i, 2);
			try {
				try {
					driver.findElement(By.id(clickedButtonId));
					
				} catch (Exception e) {
				
				
				WebDriverWait wait3 = new WebDriverWait(driver, Duration.ofSeconds(10));
				WebElement myLink3 =	wait3.until(ExpectedConditions.elementToBeClickable(By.id(buttonId)));
				myLink3.click();
				}
			
			
			try {
			   driver.findElement(By.id(buttonId));
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"AddToCart.png"));
				test3.fail("Failed to add "+itemName, MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"AddToCart.png").build());
				// isBlocked=true;

			 
			} catch (Exception e) 
			{
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"AddToCart.png"));
			
				test3.pass("Added "+ itemName +" to the cart", MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"AddToCart.png").build());
				
		
			}
			
			}
			catch (Exception e)
			{
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"AddToCart.png"));
				test3.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"AddToCart.png").build());
				
				
			}
	    }
		
		
		
	}
	public void removeFromCart(String userName) throws Exception {
		String buttonId;
		String clickedButtonId;
		String itemName;
		String excelPath = projectPath+"/excel/products.xlsx";
		ExcelUtils excel = new ExcelUtils(excelPath, "Sheet1");
		  int rowCount = excel.getRowCount();
		  ExtentTest test4 = extent.createTest(userName+": Remove from cart","Remove from cart as user: "+userName);
		
		for (int i=1;i<rowCount;i++)
		{
			buttonId=excel.getCellDataString(i, 0);
			itemName=excel.getCellDataString(i, 1);
			clickedButtonId=excel.getCellDataString(i, 2);
			try {
				try {
					driver.findElement(By.id(buttonId));
					
				} catch (Exception e) {
				
				
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id(clickedButtonId)));
				myLink.click();
				}
			
			
			try {
			   driver.findElement(By.id(clickedButtonId));
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"RemovefromoCart.png"));
				test4.fail("Test failed to remove "+itemName, MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"RemovefromoCart.png").build());
				// isBlocked=true;

			 
			} catch (Exception e) 
			{
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"RemovefromoCart.png"));
			
				test4.pass("Removed "+ itemName +" from the cart", MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"RemovefromoCart.png").build());
				
		
			}
			
			}
			catch (Exception e)
			{
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest + buttonId+userName+"RemovefromoCart.png"));
				test4.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"RemovefromoCart.png").build());
				
				
			}
	    }
		
		
		
	}
	public void logOut(String userName) throws Exception {
		ExtentTest test8 = extent.createTest(userName+": Logging out","Logging out user: "+userName);
		try 
		{
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn")));
		myLink.click();
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink2 =	wait2.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link")));
		myLink2.click();
		String expectedURL="https://www.saucedemo.com/";
		String actualURL=driver.getCurrentUrl();
		if (expectedURL.equalsIgnoreCase(actualURL))
		{	
			
			test8.pass("logged out");
			isLoggedin=false;
		}
	
		else 
		{
			test8.fail("Testing failed");
			logOut(userName);
		}
		
		}
		catch (Exception e) {
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +userName+ "Logout.png"));
			test8.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName+ "Logout.png").build());
		}
		
	}
	public void sortTest(String userName) throws Exception {
		ExtentTest test5 = extent.createTest(userName+": Sort the products","sorting products as user: "+userName);
		WebElement sortDropDown = driver.findElement(By.className("product_sort_container"));
		sortDropDown.click();
		Select selectOption=new Select(sortDropDown);
		List<WebElement> allAvailableOptions=selectOption.getOptions();
	   
		for (int i=0;i<allAvailableOptions.size();i++)
		{
			 sortDropDown = driver.findElement(By.className("product_sort_container"));
			 Select selectOption1=new Select(sortDropDown);
			 allAvailableOptions=selectOption1.getOptions();
		     selectOption1.selectByIndex(i);
		     sortDropDown = driver.findElement(By.className("product_sort_container"));
		     selectOption1=new Select(sortDropDown);
		     allAvailableOptions=selectOption1.getOptions();
		  if(selectOption1.getFirstSelectedOption().equals(allAvailableOptions.get(i)))
		  {
			  File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest +userName+allAvailableOptions.get(i).getText()+ "Sort.png"));
				test5.pass("Selected "+allAvailableOptions.get(i).getText(), MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName+allAvailableOptions.get(i).getText()+ "Sort.png").build());
		  }
		  else 
		  {
			 
				File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage img = ImageIO.read(screen);
				File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
				ImageIO.write(img, "png", new File(filetest +userName+allAvailableOptions.get(i).getText()+ "Sort.png"));
				test5.fail("Failed to select "+allAvailableOptions.get(i).getText(), MediaEntityBuilder
		                .createScreenCaptureFromPath(System.getProperty("user.dir") +userName+allAvailableOptions.get(i).getText()+ "Sort.png").build());
		  }
		 
			
		}
		
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
