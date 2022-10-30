package test;

import static org.testng.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

public class BuyProducts {
	ExtentSparkReporter reporter;
	ExtentReports extent;
	WebDriver driver;
	String userName;
	String password;
	String firstName;
	String lastName;
	String zipCode;
	String projectPath;
    String	productName="";
    String buttonId="";
    String clickedButtonId="";
	int testNumber=1;
	Boolean isLoggedin=false;
	Boolean isBlocked=false;
	
	@DataProvider(name= "productsData")
	public  Object[][] getData()
	{
		projectPath=System.getProperty("user.dir");
		String excelPath = projectPath+"/excel/products.xlsx";
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
    	//	System.out.print(cellData+ " | " );
    		data[i-1][j]= cellData;
    	}
    	//System.out.println();
    }
    	return data;
    }
	@BeforeSuite
	public void reportSetup() {
		reporter = new ExtentSparkReporter("Buying products report.html");
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
	//	userName="standard_user";
		//password="secret_sauce";
		firstName ="Sameer";
		lastName="Ameer";
		zipCode="112233";
		}
		catch (Exception e)
		{
			test.fail("Test failed: "+e.getMessage());
			
		}
	}
	@Test (priority = 2,dataProvider = "productsData" )
	public void buyingProducts(String buttonId1 ,String productName1 , String clickedButtonId1 ) throws Exception {
		String excelPath = projectPath+"/excel/users.xlsx";
		ExcelUtils excel = new ExcelUtils(excelPath, "Sheet1");
		  int rowCount = excel.getRowCount();
		   
		for (int i=1;i<rowCount;i++)
		{
		 userName=excel.getCellDataString(i, 0);
		 password=excel.getCellDataString(i, 1);
		 productName=productName1;
		 buttonId=buttonId1;
		 clickedButtonId=clickedButtonId1;
		 isBlocked=false;
	    logIn(userName,password);	
	    if (isLoggedin)
	    {
		addToCart(buttonId,productName);
		if (!isBlocked)
		openTheCart(buttonId, productName);
		if (!isBlocked)
		checkOut(buttonId, productName);
		if (!isBlocked)
		fillingInfo(buttonId, productName);
		if (!isBlocked)
		finish(buttonId, productName);
		if (!isBlocked)
		backToHome(buttonId, productName);
		//if (!isBlocked)
		logOut(buttonId, productName);
	    }
		
		}
	}
	//@Test (priority = 1 )
	public void logIn(String userName ,String password) throws Exception {
		ExtentTest test2 = extent.createTest("Login test","Logging in as user: "+userName);
		try {
	    driver.findElement(By.id("user-name")).clear();	
		driver.findElement(By.id("user-name")).sendKeys(userName);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(password);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
		myLink.click();
		//driver.findElement(By.id("login-button")).click();
		
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
//	@Test (priority = 2)
	public void addToCart(String buttonId , String poductName) throws Exception {
		ExtentTest test3 = extent.createTest(poductName+": Add to cart","Add to cart as user: "+userName);
		try {
			try {
				driver.findElement(By.id(clickedButtonId));
				
			} catch (Exception e) {
			
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id(buttonId)));
			myLink.click();
			}
		//driver.findElement(By.id(buttonId)).click();
		
		try {
		   driver.findElement(By.id(buttonId));
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest + buttonId+userName+"AddToCart.png"));
			test3.fail("Test failed: ", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") + buttonId+userName+"AddToCart.png").build());
			 isBlocked=true;

		 
		} catch (Exception e) 
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest + buttonId+userName+"AddToCart.png"));
			test3.pass("Added the item to the cart", MediaEntityBuilder
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
//	@Test (priority = 3)
	public void openTheCart(String buttonId, String poductName) throws Exception
	{
		ExtentTest test3 = extent.createTest(poductName+": Open the cart","Open the cart as user: "+userName);
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("shopping_cart_container")));
			myLink.click();
		//driver.findElement(By.id("")).click();
		String expectedURL="https://www.saucedemo.com/cart.html";
		String actualURL=driver.getCurrentUrl();
		
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "OpenedCart.png"));
		if (expectedURL.equalsIgnoreCase(actualURL))
		{
		test3.pass("Opened the cart", MediaEntityBuilder
                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "OpenedCart.png").build());
		}
		else 
		{
			test3.fail("Test failed:", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "OpenedCart.png").build());
			 isBlocked=true;
		}
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "OpenedCart.png"));
			test3.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "OpenedCart.png").build());
			
			
		}
	}
	//@Test (priority = 4)
	public void checkOut(String buttonId, String poductName) throws Exception {
		ExtentTest test4 = extent.createTest(poductName+": Checkout","Click on checkout user: "+userName);
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout")));
			myLink.click();
	//	 driver.findElement(By.id("checkout")).click();
		String expectedURL="https://www.saucedemo.com/checkout-step-one.html";
		String actualURL=driver.getCurrentUrl();
		if (expectedURL.equalsIgnoreCase(actualURL))
		{
		test4.pass("Clicked on checkout");
		}
		else 
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+ "Checkout.png"));
			test4.fail("Test failed", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "Checkout.png").build());
			 isBlocked=true;
		}
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "Checkout.png"));
			test4.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "Checkout.png").build());
			
			
		}
	}
//	@Test (priority = 5)
	public void fillingInfo(String buttonId, String poductName) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		ExtentTest test5 = extent.createTest(poductName+": Filling the info","Filling the information user: "+userName);
		try {
    	wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
		
		driver.findElement(By.id("first-name")).sendKeys(firstName);
		driver.findElement(By.id("last-name")).sendKeys(lastName);
		driver.findElement(By.id("postal-code")).sendKeys(zipCode);
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink =	wait2.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
		myLink.click();
		//driver.findElement(By.id("continue")).click();
		String expectedURL="https://www.saucedemo.com/checkout-step-two.html";
		String actualURL=driver.getCurrentUrl();
		
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "FilledInfo.png"));
		if (expectedURL.equalsIgnoreCase(actualURL))
		{
		test5.pass("Filled the information successfully", MediaEntityBuilder
                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "FilledInfo.png").build());
		}
		else 
		{
			test5.fail("Test failed", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "FilledInfo.png").build());
			 isBlocked=true;
		}
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "FilledInfo.png"));
			test5.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "FilledInfo.png").build());
			
			
		}
	}
	//@Test (priority = 6)
	public void finish(String buttonId, String poductName) throws Exception {
		ExtentTest test6 = extent.createTest(poductName+": Order completion","Completing the order user: "+userName);
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
			myLink.click();
	//	driver.findElement(By.id("finish")).click();
	
		WebElement element = driver.findElement(By.id("checkout_complete_container"));

		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].scrollIntoView();", element); 
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "Ordercompleted.png"));
		test6.pass("Order completed",MediaEntityBuilder
                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "Ordercompleted.png").build());
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "Ordercompleted.png"));
			test6.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+"Ordercompleted.png").build());
			
			
		}
	}
//	@Test (priority = 7)
	public void backToHome(String buttonId, String poductName) throws Exception {
		ExtentTest test7 = extent.createTest(poductName+": Navigating home","Navigating back to the products page user: "+userName);
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("back-to-products")));
			myLink.click();
		//driver.findElement(By.id("")).click();
		String expectedURL="https://www.saucedemo.com/inventory.html";
		String actualURL=driver.getCurrentUrl();
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
		ImageIO.write(img, "png", new File(filetest +buttonId+userName+"Back to home page.png"));
		if (expectedURL.equalsIgnoreCase(actualURL))
		{	
			test7.pass("Back to home page", MediaEntityBuilder
                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+"Back to home page.png").build());
		}
	
		else 
		{
			test7.fail("Testing failed", MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+"Back to home page.png").build());
		}
		}
		catch (Exception e)
		{
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "BackHome.png"));
			test7.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "BackHome.png").build());
			
			
		}
		
	}
	//@Test (priority = 3)
	public void logOut(String buttonId, String poductName) throws Exception {
		ExtentTest test8 = extent.createTest(poductName+": Logging out","Logging out user: "+userName);
		try 
		{
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink =	wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn")));
		myLink.click();
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement myLink2 =	wait2.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link")));
		//driver.findElement(By.id("logout_sidebar_link")).click();
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
			logOut(buttonId,poductName);
		}
		
		}
		catch (Exception e) {
			File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage img = ImageIO.read(screen);
			File filetest = Paths.get(".").toAbsolutePath().normalize().toFile();
			ImageIO.write(img, "png", new File(filetest +buttonId+userName+ "Logout.png"));
			test8.fail("Test failed: "+e.getMessage(), MediaEntityBuilder
	                .createScreenCaptureFromPath(System.getProperty("user.dir") +buttonId+userName+ "Logout.png").build());
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
