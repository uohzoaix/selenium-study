package test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WBSearch {

	private static WebDriver driver;

	private WBSearch() {
	}

	public static WebDriver getDriver() {
		if (driver == null) {
			driver = new ChromeDriver();
		}
		return driver;
	}

	public static void init(String url, String titleKeyword) {
		driver.manage().window().maximize();
		driver.get(url);
		if (waitForSuccess(10, titleKeyword)) {
			System.out.println("页面加载成功");
		}
	}

	public static void login(String username, String password) {
		waitForClikable(10, driver.findElement(ByXPath.xpath("//a[@node-type='loginBtn']"))).click();
		WebElement nameElement = driver.findElement(ByXPath.xpath("//input[@name='username']"));
		waitForVisiblity(10, nameElement);
		sendText(nameElement, username);
		WebElement passElement = driver.findElement(ByXPath.xpath("//input[@name='password']"));
		waitForVisiblity(10, passElement);
		sendText(passElement, password);
		waitForClikable(10, driver.findElement(ByXPath.xpath("//div[6]/a/span"))).click();
	}

	public static void search(String searchText) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WebElement element = driver.findElement(By.className("gn_name"));
		waitForVisiblity(10, element);
		sendText(driver.findElement(By.className("searchInp_form")), searchText.trim());
		driver.findElement(By.className("searchBtn")).click();
	}

	public static StringBuilder getText() {
		StringBuilder contents = new StringBuilder();
		WebElement pageElement = driver.findElement(By.className("search_page_M"));
		waitForVisiblity(10, pageElement);
		List<WebElement> elements = driver.findElements(ByXPath
				.xpath("//dl[@action-type='feed_list_item']/dd[@class='content']/p[@node-type='feed_list_content']/em"));
		for (WebElement element : elements) {
			System.out.println(element.getText());
			contents.append(element.getText());
		}
		return contents;
	}

	public static void nextPage() {
		waitForVisiblity(10, driver.findElement(By.className("search_page_M")));
		if (waitForVisiblity(10, driver.findElement(ByXPath.xpath("//ul[@class='search_page_M']"))) != null) {
			Integer pages = driver.findElements(ByXPath.xpath("//ul[@class='search_page_M']/li")).size();
			WebElement pgElement = driver.findElement(ByXPath.xpath("//ul[@class='search_page_M']/li[" + pages + "]/a"));
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + pgElement.getLocation().y + 100 + ");");
			new Actions(driver).click(pgElement).perform();
		}
	}

	public static Boolean waitForSuccess(Integer seconds, String titleKeyword) {
		return new WebDriverWait(driver, seconds).withMessage("页面未加载成功，无法继续").until(ExpectedConditions.titleContains(titleKeyword));
	}

	public static WebElement waitForVisiblity(Integer seconds, WebElement element) {
		return new WebDriverWait(driver, seconds).withTimeout(0, TimeUnit.SECONDS).withMessage("元素：" + element.getText() + "未出现在可视区域，无法继续")
				.until(ExpectedConditions.visibilityOf(element));
	}

	public static WebElement waitForClikable(Integer seconds, WebElement element) {
		return new WebDriverWait(driver, seconds).withMessage("元素：" + element.getText() + "尚不能点击，无法继续").until(
				ExpectedConditions.elementToBeClickable(element));
	}

	public static void sendText(WebElement element, String text) {
		element.clear();
		element.sendKeys(text);
	}

	public static void main(String[] args) throws InterruptedException {
		getDriver();
		Random rand = new Random(5);
		init("http://s.weibo.com/", "微博搜索");
		login("username", "password");
		search("电影");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			result.append(getText());
			Thread.sleep(2 * 1000);
			nextPage();
		}
		System.out.println(result.length());
	}
}
