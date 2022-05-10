
package utils;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import java.util.logging.Level;
import org.apache.xerces.impl.dv.util.Base64;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cucumber.listener.Reporter;
import com.google.common.io.Files;

import consts.Browsers;
import consts.ElementStatus;
import consts.Global;
import core.Initializer;
import core.TestRunner;
import cucumber.api.DataTable;
import cucumber.api.Scenario;

/**
 * The Class BrowserDriver.
 */
public class BrowserDriver {

	/**
	 * The Class BrowserCleanup.
	 */

	private static class BrowserCleanup implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			quit();
		}
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/** The m driver. */
	private static EventFiringWebDriver mDriver;

	/** The res folder name. */
	private static String resFolderName;

	/** The log file name. */
	private static String logFileName;

	/** The parent window handler. */
	private static String parentWindowHandler;

	/** The popup window handler status. */
	private static boolean popupWindowHandlerStatus = false;

	/** The current scenario download folder creation. */
	private static boolean currentScenarioDownloadFolderCreation = false;

	/** The Constant myDownloadsPath. */
	private static final String myDownloadsPath = System.getProperty("user.home") + "\\Downloads\\";

	/** The cucumber scenario download folder. */
	private static String cucumberScenarioDownloadFolder;

	/** The extent scenario download folder. */
	private static String extentScenarioDownloadFolder;

	/** The browser toggle flag. */
	private static boolean browserToggleFlag = false;

	/** The toggle count. */
	private static int toggleCount = 0;

	/** The Constant waitForDownloadCompleteSeconds. */
	private static final int waitForDownloadCompleteSeconds = 60;

	/** The Constant JSFOLDERPATH. */
	public static final String JSFOLDERPATH = "src/test/resources/js/utils/";

	/** The Constant JSCSSPARENTSELECTORFILENAME. */
	public static final String JSCSSPARENTSELECTORFILENAME = "jQuery.cssParentSelector.js";

	/** The Constant JSDRAGANDDROPHELPERFILENAME. */
	public static final String JSDRAGANDDROPHELPERFILENAME = "jQuery.dragAndDropHelper.js";

	/** The Constant JSJQUERYLOADHELPERFILENAME. */
	public static final String JSJQUERYLOADHELPERFILENAME = "jQuery.loadHelper.js";

	/**
	 * Checks if is browser toggle flag.
	 *
	 * @return true, if is browser toggle flag
	 */
	public static final boolean isBrowserToggleFlag() {
		return browserToggleFlag;
	}

	/**
	 * Sets the browser toggle flag.
	 *
	 * @param browserToggleFlag
	 *            the new browser toggle flag
	 */
	public static final void setBrowserToggleFlag(boolean browserToggleFlag) {
		BrowserDriver.browserToggleFlag = browserToggleFlag;
	}

	/**
	 * Checks if is current scenario download folder creation.
	 *
	 * @return true, if is current scenario download folder creation
	 */
	public static final boolean isCurrentScenarioDownloadFolderCreation() {
		return currentScenarioDownloadFolderCreation;
	}

	/**
	 * Sets the current scenario download folder creation.
	 *
	 * @param currentScenarioDownloadFolderCreation
	 *            the new current scenario download folder creation
	 */
	public static final void setCurrentScenarioDownloadFolderCreation(boolean currentScenarioDownloadFolderCreation) {
		BrowserDriver.currentScenarioDownloadFolderCreation = currentScenarioDownloadFolderCreation;
	}

	/**
	 * Alert accept.
	 *
	 * @return true, if successful
	 */
	public static boolean alertAccept() {
		if (isAlertPresent()) {
			getCurrentDriver().switchTo().alert().accept();
			return true;
		}
		LOGGER.info("Alert message was not displayed");
		return false;
	}

	/**
	 * Reset driver.
	 */
	public static void resetDriver() {
		mDriver = null;
	}

	/**
	 * Js update field.
	 *
	 * @param ele
	 *            the ele
	 * @param attr
	 *            the attr
	 * @param str
	 *            the str
	 */
	public static void jsUpdateField(WebElement ele, String attr, String str) {
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript("arguments[0].setAttribute('" + attr + "', '" + str + "');", ele);
	}

	/**
	 * Reload element.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void reloadElement(WebElement ele) {
		loadJQuery();
		String js = "";
		js = "$('" + getElementCss(ele) + "').load(document.URL +  ' " + getElementCss(ele) + "');";
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript(js);
	}

	/**
	 * Load J query.
	 */
	public static void loadJQuery() {
		String fileContents;
		try {
			fileContents = Files.toString(new File(JSFOLDERPATH + JSJQUERYLOADHELPERFILENAME), Charsets.UTF_8);
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			js.executeScript(fileContents);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		waitForPageLoadjs();
	}

	/**
	 * Java script drag and drop web elements using css.
	 *
	 * @param sourceCss
	 *            the source css
	 * @param targetCss
	 *            the target css
	 */
	public static void javaScriptDragAndDropWebElementsUsingCss(String sourceCss, String targetCss) {
		loadJQuery();
		String fileContents;
		try {
			fileContents = Files.toString(new File(JSFOLDERPATH + JSDRAGANDDROPHELPERFILENAME), Charsets.UTF_8);
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			js.executeScript(
					fileContents + "$('" + sourceCss + "').simulateDragDrop({ dropTarget: '" + targetCss + "'});");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		waitForPageLoadjs();
	}

	/**
	 * Java script drag and drop web elements.
	 *
	 * @param sourceElement
	 *            the source element
	 * @param targetElement
	 *            the target element
	 */
	public static void javaScriptDragAndDropWebElements(WebElement sourceElement, WebElement targetElement) {
		loadJQuery();
		String fileContents;
		try {
			fileContents = Files.toString(new File(JSFOLDERPATH + JSDRAGANDDROPHELPERFILENAME), Charsets.UTF_8);
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			js.executeScript(fileContents + "$('arguments[0]').simulateDragDrop({ dropTarget: 'arguments[1]'});",
					sourceElement, targetElement);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		waitForPageLoadjs();
	}

	/**
	 * Alert dismiss.
	 *
	 * @return true, if successful
	 */
	public static boolean alertDismiss() {
		if (isAlertPresent()) {
			getCurrentDriver().switchTo().alert().dismiss();
			return true;
		}
		return false;
	}

	/**
	 * Gets the alert text.
	 *
	 * @return the alert text
	 */
	public static String getAlertText() {
		if (isAlertPresent()) {
			Alert alert = getCurrentDriver().switchTo().alert();
			LOGGER.info("Actual text of alert message is: "+alert.getText());
			return alert.getText();
		}
		LOGGER.info("Alert Message did not displayed ");
		return null;
	}

	/**
	 * Gets the random string.
	 *
	 * @param len
	 *            the len
	 * @return the random string
	 */
	public static String getRandomString(int len) {
		return RandomStringUtils.randomAlphanumeric(len).toUpperCase();
	}

	/**
	 * Mouse over and clickjs.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void mouseOverAndClickjs(WebElement ele) {
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
		String onClickScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('click', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onclick');}";
		executor.executeScript(mouseOverScript, ele);
		executor.executeScript(onClickScript, ele);
	}

	/**
	 * Sets the value in text boxjs.
	 *
	 * @param ele
	 *            the ele
	 * @param value
	 *            the value
	 */
	public static void setValueInTextBoxjs(WebElement ele, String value) {
		JavascriptExecutor myExecutor = ((JavascriptExecutor) getCurrentDriver());
		myExecutor.executeScript("arguments[0].value='" + value + "';", ele);
	}

	/**
	 * Sets the value in text box web driver exe script.
	 *
	 * @param ele
	 *            the ele
	 * @param value
	 *            the value
	 */
	public static void setValueInTextBoxWebDriverExeScript(WebElement ele, String value) {
		getCurrentDriver().executeScript("arguments[0].value='" + value + "';", ele);
	}

	/**
	 * Execute parent selector script.
	 */
	public static void executeParentSelectorScript() {
		String fileContents;
		try {
			fileContents = Files.toString(new File(JSFOLDERPATH + JSCSSPARENTSELECTORFILENAME), Charsets.UTF_8);
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			js.executeScript(fileContents);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}

	}

	/**
	 * Attach CSV.
	 *
	 * @param csvFilePath
	 *            the csv file path
	 */
	public static void attachCSV(String csvFilePath) {
		try {
			Reporter.addScreenCaptureFromPath(csvFilePath);
			embedCSVToReport(Initializer.getScenario(), csvFilePath);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Backspace action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void backspaceAction(WebElement ele) {
		ele.sendKeys(Keys.BACK_SPACE);
		LOGGER.info("Backspace action performed");
	}

	/**
	 * Clear text using send keys.
	 *
	 * @param toClear
	 *            the to clear
	 */
	public static void clearTextUsingSendKeys(WebElement toClear) {
		toClear.sendKeys(Keys.CONTROL + "a");
		toClear.sendKeys(Keys.DELETE);
	}

	/**
	 * Clear text action.
	 *
	 * @param toClear
	 *            the to clear
	 */
	public static void clearTextAction(WebElement toClear) {
		Actions action = new Actions(getCurrentDriver());
		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
		action.keyDown(Keys.DELETE).keyUp(Keys.DELETE).build().perform();
	}

	/**
	 * Robot clear text.
	 *
	 * @param toClear
	 *            the to clear
	 */
	public static void robotClearText(WebElement toClear) {
		toClear.sendKeys("");
		try {
			System.out.println("Clearing the text");
			Robot rb = new Robot();
			// select the text
			rb.keyPress(KeyEvent.VK_CONTROL);
			rb.keyPress(KeyEvent.VK_A);
			rb.keyRelease(KeyEvent.VK_A);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			// delete the text
			rb.keyPress(KeyEvent.VK_DELETE);
			rb.keyRelease(KeyEvent.VK_DELETE);
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}

	}

	/**
	 * Clear text.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clearText(WebElement ele) {
		ele.clear();
		LOGGER.info("Clear the existing Text");
	}

	/**
	 * Click.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void click(WebElement ele) {
		// waitForVisibilityOfElementLocated(ele);
		// waitForElementToBeClickable(ele);
		ele.click();
		LOGGER.info("Clicked successfully");
	}

	/**
	 * Click action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickAction(WebElement ele) {
		Actions builder = new Actions(getCurrentDriver());
		builder.moveToElement(ele).click(ele);
		builder.perform();
	}

	/**
	 * Fire js event.
	 *
	 * @param elementRef
	 *            the element ref
	 * @param eventName
	 *            the event name
	 */
	public static void fireJsEvent(String elementRef, String eventName) {
		String script = "" + " function eventFire(element, eventName)" + " {" + "  if (element.fireEvent)"
				+ "  { element.fireEvent('on' + eventName); }" + "  else" + "  {"
				+ "    var eventObject = document.createEvent('Events');" +
				// parameters: type, bubbles, cancelable
				"    eventObject.initEvent(eventName, true, false);" + "    element.dispatchEvent(eventObject);" + "  }"
				+ " };";

		String eventCall = String.format("eventFire(%s, '%s');", elementRef, eventName);
		String exec = script + eventCall;
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript(exec);
	}

	/**
	 * Click and accept.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickAndAccept(WebElement ele) {
		try {
			clickUsingSendKeysEnter(ele);
			takeScreenshotUsingRobot(getResFolderName());
			alertAccept();
		} catch (UnhandledAlertException uae) {
			LOGGER.log(Level.SEVERE, uae.getStackTrace().toString(), uae);
		}
	}

	/**
	 * Click and dismiss.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickAndDismiss(WebElement ele) {
		try {
			clickUsingSendKeysEnter(ele);
			takeScreenshotUsingRobot(getResFolderName());
			alertDismiss();
		} catch (UnhandledAlertException uae) {
			LOGGER.log(Level.SEVERE, uae.getStackTrace().toString(), uae);
		}
	}

	/**
	 * Click first element.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickFirstElement(WebElement ele) {
		ele.findElements(By.xpath("//*")).get(0).click();
		LOGGER.info("Clicked successfully");
	}

	/**
	 * Click on element.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickOnElement(WebElement ele) {
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript("arguments[0].click();", ele);
		//executor.executeScript("var elem=arguments[0]; setTimeout(function() {elem.click();}, 100)", ele);
	}

	/**
	 * Click submenu from menu.
	 *
	 * @param SummaryList
	 *            the summary list
	 * @param value
	 *            the value
	 */
	public static void clickSubmenuFromMenu(WebElement SummaryList, String value) {
		String singleValue = null;
		List<WebElement> summaryView = getCurrentDriver().findElements(By.xpath(".//*"));
		for (WebElement element : summaryView) {
			singleValue = element.getText();
			if (singleValue.trim().equalsIgnoreCase(value.trim())) {
				element.click();
				break;
			}
		}
		LOGGER.info(value + " is selected from the dropown");
	}

	/**
	 * Click using send keys enter.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickUsingSendKeysEnter(WebElement ele) {
		waitForVisibilityOfElementLocated(ele);
		waitForElementToBeClickable(ele);
		ele.sendKeys(Keys.ENTER);
	}

	/**
	 * Click using send keys space.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickUsingSendKeysSpace(WebElement ele) {
		waitForVisibilityOfElementLocated(ele);
		waitForElementToBeClickable(ele);
		ele.sendKeys(Keys.SPACE);
	}

	/**
	 * Send alt down using robot.
	 */
	public static void sendAltDownUsingRobot() {
		Robot robot = null;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ALT);
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}

		try {
			Thread.sleep(1000);
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_DOWN);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}

	}

	/**
	 * Click using xpath.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void clickUsingXpath(WebElement ele) {
		String xpath = getElementXPath(ele);
		ele = getCurrentDriver().findElement(By.xpath(xpath));
		waitForVisibilityOfElementLocated(ele);
		waitForElementToBeClickable(ele);
		ele.click();
		LOGGER.info("Clicked successfully");
	}

	/**
	 * Close remember password dialog.
	 */
	public static void closeRememberPasswordDialog() {
		wait(3000);
		try {
			Robot robot = new Robot();
			if (TestRunner.getBrowser().equalsIgnoreCase("firefox")) {
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
				wait(1000);
			} else if (TestRunner.getBrowser().equalsIgnoreCase("ie")) {
				// press alt+n key to focus on yes button
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_ALT);
				wait(1000);

				// press tab to shift focus to not for this site button
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				wait(1000);

				// press tab to shift focus cross
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				wait(1000);

				// press enter to click cross
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				wait(1000);
			}
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Close dialog after download.
	 */
	public static void closeDialogAfterDownload() {
		wait(3000);
		try {
			Robot robot = new Robot();
			if (TestRunner.getBrowser().equalsIgnoreCase("firefox")) {
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
				wait(1000);
			} else if (TestRunner.getBrowser().equalsIgnoreCase("ie")) {
				// press alt+n key to focus on download dialog
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_ALT);
				wait(1000);

				// press tab to shift focus to open folder button
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				wait(1000);

				// press tab to shift focus to view downloads button
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				wait(1000);

				// press tab to shift focus to cross
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				wait(1000);

				// press enter to click cross
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				wait(1000);
			}
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}

	}

	/**
	 * Convert table data in to map with header as key.
	 *
	 * @param dataWithHeader
	 *            the data with header
	 * @return the hash map
	 */
	public static HashMap<String, List<String>> convertTableDataInToMapWithHeaderAsKey(List<String> dataWithHeader) {

		HashMap<String, List<String>> TableMap = new HashMap<String, List<String>>();
		String[] headerArray = dataWithHeader.get(0).split(";");
		for (int i = 0; i < headerArray.length; i++) {
			List<String> columnList = new ArrayList<String>();
			for (int j = 1; j < dataWithHeader.size(); j++) {
				String[] rowArray = dataWithHeader.get(j).split(";");
				columnList.add("@".equalsIgnoreCase(rowArray[i]) ? " " : rowArray[i]);
			}
			TableMap.put(headerArray[i], columnList);
			columnList = null;
		}
		return TableMap;
	}

	/**
	 * Csv filename.
	 *
	 * @return the string
	 */
	public static String csvFilename() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		Date d = new Date();
		String filename = d.toString().replace(":", "_").replace(" ", "_") + ".csv";
		return filename;
	}

	/**
	 * Deselect all.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void deselectAll(WebElement ele) {
		Select deselect = new Select(ele);
		deselect.deselectAll();
		LOGGER.info("Deselected all the items");
	}

	/**
	 * Deselect by index.
	 *
	 * @param ele
	 *            the ele
	 * @param index
	 *            the index
	 */
	public static void deselectByIndex(WebElement ele, int index) {
		Select deselect = new Select(ele);
		deselect.deselectByIndex(index);
		LOGGER.info("Deselected index is:: " + index);
	}

	/**
	 * Deselect by value.
	 *
	 * @param ele
	 *            the ele
	 * @param value
	 *            the value
	 */
	public static void deselectByValue(WebElement ele, String value) {
		Select deselect = new Select(ele);
		deselect.deselectByValue(value);
		LOGGER.info("Deselected value is:: " + value);
	}

	/**
	 * Deselect by visible text.
	 *
	 * @param ele
	 *            the ele
	 * @param visibleText
	 *            the visible text
	 */
	public static void deselectByVisibleText(WebElement ele, String visibleText) {
		Select deselect = new Select(ele);
		deselect.deselectByVisibleText(visibleText);
		LOGGER.info("Deselected visible text is:: " + visibleText);
	}

	/**
	 * Deselect value from drop down list.
	 *
	 * @param summaryList
	 *            the summary list
	 * @param value
	 *            the value
	 */
	public static void deselectValueFromDropDownList(WebElement summaryList, String value) {
		String singleValue = null;
		List<WebElement> summaryView = summaryList.findElements(By.xpath("//*"));
		for (WebElement element : summaryView) {
			singleValue = element.getText();
			if (singleValue.trim().equalsIgnoreCase(value.trim())) {
				if (element.isEnabled()) {
					element.click();
					wait(Integer.parseInt(Global.IMPLICITWAIT.toString()));
					break;
				}
			}
		}
		LOGGER.info(value + " :: is De-selected from the dropown");
	}

	/**
	 * Download file.
	 *
	 * @param elementToBeClicked
	 *            the element to be clicked
	 * @param filename
	 *            the filename
	 */
	public static void downloadFile(WebElement elementToBeClicked, String filename) {
		int timer = 1;
		if (!currentScenarioDownloadFolderCreation) {
			FileUtilities.createDownloadFolder();
			cucumberScenarioDownloadFolder = FileUtilities.targetCucumberReportDownloadFolder + "Scenario_"
					+ String.format("%04d", Initializer.getScenarioCounter());
			extentScenarioDownloadFolder = getResFolderName() + FileUtilities.outputExtentReportDownloadFolder + "\\"
					+ "Scenario_" + String.format("%04d", Initializer.getScenarioCounter());
			FileUtilities.createDir(cucumberScenarioDownloadFolder);
			FileUtilities.createDir(extentScenarioDownloadFolder);
			setCurrentScenarioDownloadFolderCreation(true);
		}
		try {
			Robot robot = new Robot();
			if (elementToBeClicked.getTagName().toLowerCase().toString().equals("a")
					|| elementToBeClicked.getTagName().toLowerCase().toString().equals("button")
					|| elementToBeClicked.getTagName().toLowerCase().toString().equals("img")) {
				elementToBeClicked.sendKeys("");
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(2000);
				takeScreenshotUsingRobot(getResFolderName());
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_S);
				robot.keyRelease(KeyEvent.VK_S);
				robot.keyRelease(KeyEvent.VK_ALT);
				Thread.sleep(2000);
				takeScreenshotUsingRobot(getResFolderName());
				if (TestRunner.getBrowser().equalsIgnoreCase("firefox")) {
					robot.keyPress(KeyEvent.VK_ENTER);
					robot.keyRelease(KeyEvent.VK_ENTER);
				}

				do {
					Thread.sleep(1000);
					timer++;
					if (timer == waitForDownloadCompleteSeconds)
						break;
				} while (!(isFileExist(myDownloadsPath, filename)));
				
			//	closeDialogAfterDownload();
			//	LOGGER.info("Closed download dialog");
				
				if (isFileExist(myDownloadsPath, filename)){
				LOGGER.info("Downloaded file: " + filename);
				closeDialogAfterDownload();
				LOGGER.info("Closed download dialog");
				moveFileToScenario(filename);
				LOGGER.info("Moved downloaded file " + filename + " to download folder");
				}
				else{
					LOGGER.log(Level.SEVERE, "Dowloaded file not found!");
				//	 Assert.fail("\nFAILED : .zip file not found");
				}
			}
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Download file with PopUp Window.
	 *
	 * @param elementToBeClicked
	 *            the element to be clicked
	 * @param filename
	 *            the filename
	 */
	public static void downloadFileWithPopUpWindow(WebElement elementToBeClicked, String filename) {
		int timer = 1;
		if (!currentScenarioDownloadFolderCreation) {
			FileUtilities.createDownloadFolder();
			cucumberScenarioDownloadFolder = FileUtilities.targetCucumberReportDownloadFolder + "Scenario_"
					+ String.format("%04d", Initializer.getScenarioCounter());
			extentScenarioDownloadFolder = getResFolderName() + FileUtilities.outputExtentReportDownloadFolder + "\\"
					+ "Scenario_" + String.format("%04d", Initializer.getScenarioCounter());
			FileUtilities.createDir(cucumberScenarioDownloadFolder);
			FileUtilities.createDir(extentScenarioDownloadFolder);
			setCurrentScenarioDownloadFolderCreation(true);
		}
		try {
			Robot robot = new Robot();

			elementToBeClicked.click();
			Thread.sleep(2000);
			Alert alert = BrowserDriver.getCurrentDriver().switchTo().alert();
			alert.accept();
			Thread.sleep(2000);
			takeScreenshotUsingRobot(getResFolderName());
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_ALT);
			Thread.sleep(2000);
			takeScreenshotUsingRobot(getResFolderName());
			if (TestRunner.getBrowser().equalsIgnoreCase("firefox")) {
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			}

			do {
				Thread.sleep(1000);
				timer++;
				if (timer == waitForDownloadCompleteSeconds)
					break;
			} while (!(isFileExist(myDownloadsPath, filename)));
			LOGGER.info("Downloaded file: " + filename);
			closeDialogAfterDownload();
			LOGGER.info("Closed download dialog");
			moveFileToScenario(filename);
			LOGGER.info("Moved downloaded file " + filename + " to download folder");

		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Maximize browser.
	 */
	public static void maximizeBrowser() {
		getCurrentDriver().manage().window().maximize();
	}

	/**
	 * Reset browser.
	 */
	public static void resetBrowser() {
		BrowserFactory.addAllBrowserSetup(getCurrentDriver());
	}

	/**
	 * Embed CSV to report.
	 *
	 * @param scenario
	 *            the scenario
	 * @param csvFilePath
	 *            the csv file path
	 */
	public static void embedCSVToReport(Scenario scenario, String csvFilePath) {
		File file = new File(csvFilePath);
		FileInputStream fin = null;
		int bytesRead = 0;
		try {
			fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			bytesRead = fin.read(fileContent);
			LOGGER.info("Number of bytes read: " + bytesRead);
			String s = new String(fileContent);
			scenario.embed(s.getBytes(), "text/plain");
			LOGGER.info("CSV file embedded");
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, ioe.getStackTrace().toString(), ioe);
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE, ioe.getStackTrace().toString(), ioe);
			}
		}
	}

	/**
	 * Embed screenshot to report.
	 *
	 * @param scenario
	 *            the scenario
	 */
	public static void embedScreenshotToReport(Scenario scenario) {
		//if (!isAlertPresent()) {
		//	scenario.embed(((TakesScreenshot) getCurrentDriver()).getScreenshotAs(OutputType.BYTES), "image/png");
		//} else {
			BufferedImage image = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			try {
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(image, "png", baos);
				baos.flush();
				String base64String = Base64.encode(baos.toByteArray());
				byte[] bytearray = Base64.decode(base64String);
				scenario.embed(bytearray, "image/png");
			} catch (HeadlessException e2) {
				LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
			} catch (AWTException e2) {
				LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
			}
		//}
		LOGGER.info("Screenshot captured and embedded");
	}

	/**
	 * Embed screenshot to report robot.
	 *
	 * @param scenario
	 *            the scenario
	 */
	public static void embedScreenshotToReportRobot(Scenario scenario) {
		BufferedImage image = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		try {
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(image, "png", baos);
			baos.flush();
			String base64String = Base64.encode(baos.toByteArray());
			byte[] bytearray = Base64.decode(base64String);
			scenario.embed(bytearray, "image/png");
		} catch (HeadlessException e2) {
			LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
		} catch (AWTException e2) {
			LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		LOGGER.info("Screenshot captured and embedded");
	}

	/**
	 * Enter action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void enterAction(WebElement ele) {
		ele.sendKeys(Keys.ENTER);
		LOGGER.info("Enter action performed");
	}

	/**
	 * Generate XPATH.
	 *
	 * @param ele
	 *            the ele
	 * @param current
	 *            the current
	 * @return the string
	 */
	public static String generateXPATH(WebElement ele, String current) {
		String childTag = ele.getTagName();
		if (childTag.equals("html")) {
			return "/html[1]" + current;
		}
		WebElement parentElement = ele.findElement(By.xpath(".."));
		List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
		int count = 0;
		for (int i = 0; i < childrenElements.size(); i++) {
			WebElement childrenElement = childrenElements.get(i);
			String childrenElementTag = childrenElement.getTagName();
			if (childTag.equals(childrenElementTag)) {
				count++;
			}
			if (ele.equals(childrenElement)) {
				return generateXPATH(parentElement, "/" + childTag + "[" + count + "]" + current);
			}
		}
		return null;
	}

	/**
	 * Gets the attribute.
	 *
	 * @param ele
	 *            the ele
	 * @param value
	 *            the value
	 * @return the attribute
	 */
	public static String getAttribute(WebElement ele, String value) {
		String attribute = ele.getAttribute(value);
		LOGGER.info("Attribute is :: " + attribute);
		return attribute;
	}

	/**
	 * Gets the count list of web elements.
	 *
	 * @param ele
	 *            the ele
	 * @return the count list of web elements
	 */
	public static int getCountListOfWebElements(WebElement ele) {
		List<WebElement> list = ele.findElements(By.xpath("//*"));
		return list.size();
	}

	/**
	 * Gets the current driver.
	 *
	 * @return the current driver
	 */
	public synchronized static EventFiringWebDriver getCurrentDriver() {
		if (!Initializer.isScenarioExecutionCompletionStatus()) {
			if (mDriver == null) {
				try {
					mDriver = BrowserFactory.getBrowser();
				} catch (UnreachableBrowserException e) {
					LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
					mDriver = BrowserFactory.getBrowser();
				} catch (WebDriverException e) {
					LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
					mDriver = BrowserFactory.getBrowser();
				} finally {
					Runtime.getRuntime().addShutdownHook(new Thread(new BrowserCleanup()));
				}
			}
		}
		return mDriver;
	}

	/**
	 * Gets the data table as list of string.
	 *
	 * @param datatable
	 *            the datatable
	 * @return the data table as list of string
	 */
	public static List<String> getDataTableAsListOfString(DataTable datatable) {
		List<String> listOfString = datatable.asList(String.class);
		return listOfString;
	}

	/**
	 * Gets the dropdown all option.
	 *
	 * @param ele
	 *            the ele
	 * @return the dropdown all option
	 */
	public static List<WebElement> getDropdownAllOption(WebElement ele) {
		Select select = new Select(ele);
		List<WebElement> allOptions = select.getOptions();
		return allOptions;
	}

	/**
	 * Gets the drop down option.
	 *
	 * @param webElement
	 *            the web element
	 * @param value
	 *            the value
	 * @return the drop down option
	 */
	public static WebElement getDropDownOption(WebElement webElement, String value) {
		WebElement option = null;
		List<WebElement> options = getDropDownOptions(webElement);
		for (WebElement element : options) {
			if (element.getAttribute("value").equalsIgnoreCase(value)) {
				option = element;
				break;
			}
		}
		return option;
	}

	/**
	 * Gets the drop down options.
	 *
	 * @param webElement
	 *            the web element
	 * @return the drop down options
	 */
	public static List<WebElement> getDropDownOptions(WebElement webElement) {
		Select select = new Select(webElement);
		return select.getOptions();
	}
	
	
	public static boolean verifyDropDownValueExists(WebElement webElement, String dropdownValue){
		Boolean isDropDownValueFound = false;
		Select select = new Select(webElement);;
		List<WebElement> allOptions = select.getOptions();
		//List<WebElement> allOptions = webElement.findElements(By.xpath("//*"));
		for(int i=0; i<allOptions.size(); i++) {
		    if(allOptions.get(i).getText().equals(dropdownValue)) {
		    	isDropDownValueFound=true;
		        break;
		    }
		}
		
		if(isDropDownValueFound) {
		    LOGGER.info(dropdownValue+"Dropdown Value exists");
		}
		else{
			LOGGER.info(dropdownValue+"Dropdown Value does not exists");
		}
		return isDropDownValueFound;
	}
	/**
	 * Gets the dropdown selected option.
	 *
	 * @param ele
	 *            the ele
	 * @return the dropdown selected option
	 */
	public static String getDropdownSelectedOption(WebElement ele) {
		Select select = new Select(ele);
		return select.getFirstSelectedOption().getText();
	}

	/**
	 * Gets the element X path.
	 *
	 * @param ele
	 *            the ele
	 * @return the element X path
	 */
	public static String getElementXPath(WebElement ele) {

		String javaScript = "function getElementXPath(elt){" + "var path = \"\";"
				+ "for (; elt && elt.nodeType == 1; elt = elt.parentNode){" + "idx = getElementIdx(elt);"
				+ "xname = elt.tagName;" + "if (idx > 1){" + "xname += \"[\" + idx + \"]\";" + "}"
				+ "path = \"/\" + xname + path;" + "}" + "return path;" + "}" + "function getElementIdx(elt){"
				+ "var count = 1;" + "for (var sib = elt.previousSibling; sib ; sib = sib.previousSibling){"
				+ "if(sib.nodeType == 1 && sib.tagName == elt.tagName){" + "count++;" + "}" + "}" + "return count;"
				+ "}" + "return getElementXPath(arguments[0]).toLowerCase();";
		// return (String) ((JavascriptExecutor)
		// getCurrentDriver()).executeScript(javaScript, ele);
		// fluientWaitforElement(ele, (int) Global.PAGELOADTIME.getValue(), 2);
		return new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
				.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
					return (String) ((JavascriptExecutor) d).executeScript(javaScript, ele);
				});
	}

	/**
	 * Gets the element css.
	 *
	 * @param ele
	 *            the ele
	 * @return the element css
	 */
	public static String getElementCss(WebElement ele) {
		String javaScript = "function getUniqueSelector(elSrc) {" + "  if (!(elSrc instanceof Element)) return;"
				+ "  var sSel," + "    aAttr = ['name', 'value', 'title', 'placeholder', 'data-*']," + "    aSel = [],"
				+ "    getSelector = function(el) {" + "      if (el.id) {" + "        aSel.unshift('#' + el.id);"
				+ "        return true;" + "      }" + "      aSel.unshift(sSel = el.nodeName.toLowerCase());"
				+ "      if (el.className) {"
				+ "        aSel[0] = sSel += '.' + el.className.trim().replace(/ +/g, '.');"
				+ "        if (uniqueQuery()) return true;" + "      }" + "      for (var i=0; i<aAttr.length; ++i) {"
				+ "        if (aAttr[i]==='data-*') {"
				+ "          var aDataAttr = [].filter.call(el.attributes, function(attr) {"
				+ "            return attr.name.indexOf('data-')===0;" + "          });"
				+ "          for (var j=0; j<aDataAttr.length; ++j) {"
				+ "			 aSel[0] = sSel += '[' + aDataAttr[j].name + '=\"' + aDataAttr[j].value + '\"]';"
				+ "            if (uniqueQuery()) return true;" + "          }" + "        } else if (el[aAttr[i]]) {"
				+ "          aSel[0] = sSel += '[' + aAttr[i] + '=\"' + el[aAttr[i]] + '\"]';"
				+ "          if (uniqueQuery()) return true;" + "        }" + "      }" + "      var elChild = el,"
				+ "        sChild," + "        n = 1;" + "      while (elChild = elChild.previousElementSibling) {"
				+ "        if (elChild.nodeName===el.nodeName) ++n;" + "      }"
				+ "      aSel[0] = sSel += ':nth-of-type(' + n + ')';" + "      if (uniqueQuery()) return true;"
				+ "      elChild = el;" + "      n = 1;" + "      while (elChild = elChild.previousElementSibling) ++n;"
				+ "      aSel[0] = sSel = sSel.replace(/:nth-of-type\\(\\d+\\)/, n>1 ? ':nth-child(' + n + ')' : ':first-child');"
				+ "      if (uniqueQuery()) return true;" + "      return false;" + "    },"
				+ "    uniqueQuery = function() {"
				+ "      return document.querySelectorAll(aSel.join('>')||null).length===1;" + "    };"
				+ "  while (elSrc.parentNode) {" + "    if (getSelector(elSrc)) return aSel.join(' > ');"
				+ "    elSrc = elSrc.parentNode;" + "  }" + "}"
				+ "return getUniqueSelector(arguments[0]).toLowerCase();";
		return new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
				.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
					return (String) ((JavascriptExecutor) d).executeScript(javaScript, ele);
				});
	}

	/**
	 * Checks if is element disabled js.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element disabled js
	 */
	public static boolean isElementDisabledJs(WebElement ele) {
		JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
		String javaScript = "return ($('" + BrowserDriver.getElementCss(ele) + "').is(':disabled') ||" + " $('"
				+ BrowserDriver.getElementCss(ele) + "').attr('disabled') || " + "$('"
				+ BrowserDriver.getElementCss(ele) + "').prop('disabled'));";
		return (boolean) js.executeScript(javaScript);
	}

	/**
	 * Checks if is element disabled DOM.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element disabled DOM
	 */
	public static boolean isElementDisabledDOM(WebElement ele) {
		JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
		String javaScript = "return $(\"document.querySelector('" + BrowserDriver.getElementCss(ele) + "').disabled);";
		return (boolean) js.executeScript(javaScript);
	}

	/**
	 * Gets the list details.
	 *
	 * @param ele
	 *            the ele
	 * @return the list details
	 */
	public static List<String> getListDetails(WebElement ele) {
		List<WebElement> listOfWebElement = ele.findElements(By.xpath("//*"));
		List<String> list = new ArrayList<String>();

		for (WebElement webelement : listOfWebElement) {
			list.add(webelement.getText());
		}
		return list;
	}

	/**
	 * Gets the parent.
	 *
	 * @param element
	 *            the element
	 * @return the parent
	 */
	public static WebElement getParent(WebElement element) {
		return element.findElement(By.xpath(".."));
	}

	/**
	 * Gets the res folder name.
	 *
	 * @return the res folder name
	 */
	public static String getResFolderName() {
		return resFolderName;
	}

	/**
	 * Gets the log file name.
	 *
	 * @return the log file name
	 */
	public static String getLogFileName() {
		return logFileName;
	}

	/**
	 * Gets the tagname.
	 *
	 * @param ele
	 *            the ele
	 * @return the tagname
	 */
	public static String getTagname(WebElement ele) {
		return ele.getTagName();
	}

	/**
	 * Gets the date from string.
	 *
	 * @param dateFormat
	 *            the date format
	 * @param dateStr
	 *            the date str
	 * @return the date from string
	 */
	public static Date getDateFromString(DateFormat dateFormat, String dateStr) {
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
			dateFormat.format(date);
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		return date;
	}

	/**
	 * Gets the tag name.
	 *
	 * @param ele
	 *            the ele
	 * @return the tag name
	 */
	public static String getTagName(WebElement ele) {
		String tagname = ele.getTagName();
		LOGGER.info("TagName is :: " + tagname);
		return tagname;
	}

	/**
	 * Gets the text.
	 *
	 * @param ele
	 *            the ele
	 * @return the text
	 */
	public static String getText(WebElement ele) {
		String text = ele.getText();
		LOGGER.info("Text is :: " + text);
		return text;
	}

	/**
	 * Gets the updated string.
	 *
	 * @param strvalue
	 *            the strvalue
	 * @param randomInteger
	 *            the random integer
	 * @return the updated string
	 */
	public static String getUpdatedString(String strvalue, int randomInteger) {
		String value = null;
		String randomNumbers = RandomStringUtils.randomNumeric(randomInteger);
		if (randomNumbers != null) {
			value = strvalue + randomNumbers;
		}
		return value;
	}

	/**
	 * Highlight element.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void HighlightElement(WebElement ele) {
		JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
		js.executeScript("arguments[0].style.border='3px solid red'", ele);
		js.executeScript("arguments[0].style.border='1px white'", ele);
	}

	/**
	 * Highlight element js.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void HighlightElementJs(WebElement ele) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			for (int iCnt = 0; iCnt < 1; iCnt++) {
				js.executeScript("arguments[0].style.border='2px dashed yellow'", ele);
				Thread.sleep(1000);
				js.executeScript("arguments[0].style.border=''", ele);
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Hover and click.
	 *
	 * @param elementToHover
	 *            the element to hover
	 * @param elementToClick
	 *            the element to click
	 */
	public static void hoverAndClick(WebElement elementToHover, WebElement elementToClick) {
		Actions action = new Actions(getCurrentDriver());
		action.moveToElement(elementToHover).build().perform();
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.IMPLICITWAIT.getValue());
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(getElementXPath(elementToClick)))); // until
																											// this
																											// element
																											// is
																											// found
		elementToClick.click();
	}

	/**
	 * Un hide div by id.
	 *
	 * @param divId
	 *            the div id
	 */
	public static void unHideDivById(String divId) {
		String javaScript = "getEval | window.document.getElementById('" + divId + "').set_attribute('style','');";
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript(javaScript);
	}

	/**
	 * Simultaneous hover and click.
	 *
	 * @param elementToHover
	 *            the element to hover
	 * @param elementToClick
	 *            the element to click
	 */
	public static void simultaneousHoverAndClick(WebElement elementToHover, WebElement elementToClick) {
		Actions action = new Actions(getCurrentDriver());
		int Width = elementToHover.getLocation().getX();
		int Height = elementToHover.getLocation().getY();
		int MyX = (Width * 95) / 100;// spot to click is at 95% of the width
		int MyY = Height / 2;// anywhere above Height/2 works
		action.moveToElement(elementToHover, MyX, MyY).clickAndHold().moveToElement(elementToClick).click().build()
				.perform();
	}

	/**
	 * Simultaneous hover click and hold.
	 *
	 * @param elementToHover
	 *            the element to hover
	 * @param elementToClick
	 *            the element to click
	 */
	public static void simultaneousHoverClickAndHold(WebElement elementToHover, WebElement elementToClick) {
		Actions action = new Actions(getCurrentDriver());
		int Width = elementToHover.getLocation().getX();
		int Height = elementToHover.getLocation().getY();
		int MyX = (Width * 95) / 100;// spot to click is at 95% of the width
		int MyY = Height / 2;// anywhere above Height/2 works
		action.moveToElement(elementToHover, MyX, MyY).clickAndHold().moveToElement(elementToClick).clickAndHold()
				.build().perform();
	}

	/**
	 * Drag and drop action.
	 *
	 * @param elementToHover
	 *            the element to hover
	 * @param elementToClick
	 *            the element to click
	 */
	public static void dragAndDropAction(WebElement elementToHover, WebElement elementToClick) {
		Actions action = new Actions(getCurrentDriver());
		action.dragAndDrop(elementToHover, elementToClick).build().perform();
	}

	/**
	 * Click by width coordinates.
	 *
	 * @param ele
	 *            the ele
	 * @param divisor
	 *            the divisor
	 */
	public static void clickByWidthCoordinates(WebElement ele, double divisor) {
		int width = ele.getSize().getWidth();
		Actions act = new Actions(getCurrentDriver());
		int widthOffset = (int) (width / divisor);
		act.moveToElement(ele).moveByOffset(widthOffset, 0).click().build().perform();
	}

	/**
	 * Click by offset js.
	 *
	 * @param ele
	 *            the ele
	 * @param offsetX
	 *            the offset X
	 * @param offsetY
	 *            the offset Y
	 */
	public static void clickByOffsetJs(WebElement ele, int offsetX, int offsetY) {
		/*
		 * String scriptText = "function clickOnElem(elem, offsetX, offsetY) { "
		 * + "var rect = elem.getBoundingClientRect(), " +
		 * "posX = rect.left, posY = rect.top; " +
		 * "if (typeof offsetX == 'number') " +
		 * "posX += offsetX; else if (offsetX == 'center') { " +
		 * "posX += rect.width / 2; " + "if (offsetY == null) " +
		 * "posY += rect.height / 2; } " + "if (typeof offsetY == 'number') " +
		 * "posY += offsetY; " +
		 * "var evt = new MouseEvent('click', {bubbles: true, clientX: posX, clientY: posY}); "
		 * + "elem.dispatchEvent(evt); }";
		 */

		Point pos = ele.getLocation();
		int elementLeft = pos.getX();
		int elementTop = pos.getY();
		int elementWidth = ele.getSize().getWidth();
		int elementHeight = ele.getSize().getHeight();

		String scriptText = "function clickOnElem() { " + "var posX = " + elementLeft + ", posY = " + elementTop + ", "
				+ "offsetX = " + offsetX + ", offsetY = " + offsetY + "; " + "if (typeof offsetX == 'number') "
				+ "posX += offsetX; else if (offsetX == 'center') { " + "posX += " + elementWidth + " / 2; "
				+ "if (offsetY == null) " + "posY += " + elementHeight + " / 2; } " + "if (typeof offsetY == 'number') "
				+ "posY += offsetY; "
				+ "var evt = new MouseEvent('click', {bubbles: true, clientX: posX, clientY: posY});"
				+ "alert(\"event intialized\");" + "document.dispatchEvent(evt);" + "alert(\"event dispatched\");} "
				+ "clickOnElem();";

		JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
		// js.executeScript(scriptText + "clickOnElem(" + ele + ", " + offsetX +
		// ", " + offsetY + ");", ele);
		LOGGER.info(scriptText);
		js.executeScript(scriptText);
	}

	/**
	 * Implicit wait.
	 *
	 * @param seconds
	 *            the seconds
	 */
	public static void implicitWait(int seconds) {
		getCurrentDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Checks if is alert present.
	 *
	 * @return true, if is alert present
	 */

	public static boolean isAlertPresent() {
		try {
			getCurrentDriver().switchTo().alert();
			LOGGER.info("Alert message displayed");
			return true;
		} catch (NoSuchWindowException nsw) {
			LOGGER.log(Level.FINE, nsw.getStackTrace().toString(), nsw);
			LOGGER.info("No Alert message displayed");
			return false;
		} catch (TimeoutException te) {
			LOGGER.log(Level.FINE, te.getStackTrace().toString(), te);
			LOGGER.info("No Alert message displayed");
			return false;
		} catch (NullPointerException ne) {
			LOGGER.log(Level.FINE, ne.getStackTrace().toString(), ne);
			LOGGER.info("No Alert message displayed");
			return false;
		} catch (NoAlertPresentException ex) {
			LOGGER.log(Level.FINE, ex.getStackTrace().toString(), ex);
			LOGGER.info("No Alert message displayed");
			return false;
		}
	}

	/**
	 * Checks if is element displayed.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element displayed
	 */
	public static boolean isElementDisplayed(WebElement ele) {
		if (ele.isDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is element enabled.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element enabled
	 */
	public static boolean isElementEnabled(WebElement ele) {
		if (ele.isEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is element disabled.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element disabled
	 */
	public static boolean isElementDisabled(WebElement ele) {
		if (!ele.isEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is element not present.
	 *
	 * @param by
	 *            the by
	 * @return true, if is element not present
	 */
	public static boolean isElementNotPresent(By by) {
		int size = getCurrentDriver().findElements(by).size();
		if (size == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if is element present.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element present
	 */
	public static boolean isElementPresent(WebElement ele) {
		int size = ele.findElements(By.xpath("//*")).size();
		if (size == 0) {
			return false;
		} else
			return true;
	}

	/**
	 * Checks if is element selected.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is element selected
	 */
	public static boolean isElementSelected(WebElement ele) {
		if (ele.isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is file downloaded.
	 *
	 * @param downloadPath
	 *            the download path
	 * @param fileName
	 *            the file name
	 * @return true, if is file downloaded
	 */
	public static boolean isFileExist(String downloadPath, String fileName) {
		boolean flag = false;
		File dir = new File(downloadPath);
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			if (dir_contents[i].getName().equals(fileName))
				return flag = true;
		}
		return flag;
	}

	/**
	 * Checks if is list empty.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if is list empty
	 */
	public static boolean isListEmpty(WebElement ele) {
		int countInList = getCountListOfWebElements(ele);
		if (countInList > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if is sorted list.
	 *
	 * @param list
	 *            the list
	 * @return true, if is sorted list
	 */
	public static boolean isSortedList(List<String> list) {

		if (list == null || list.isEmpty()) {
			LOGGER.info("List is EMPTY");
			return false;
		}
		if (list.size() == 1) {
			LOGGER.info("List has one element, hence list is SORTED");
			return true;
		}
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).compareToIgnoreCase(list.get(i)) > 0) {
				LOGGER.info("Not Sorted elements are:: " + list.get(i) + " :: " + list.get(i - 1));
				LOGGER.info("List is NOT SORTED");
				return false;
			}
		}
		LOGGER.info("List is SORTED");
		return true;
	}

	/**
	 * Checks if is two list equal.
	 *
	 * @param list1
	 *            the list 1
	 * @param list2
	 *            the list 2
	 * @return true, if is two list equal
	 */
	public static boolean isTwoListEqual(List<String> list1, List<String> list2) {
		if (list1 == null && list2 == null) {
			return true;
		}
		if ((list1 == null && list2 != null) || list1 != null && list2 == null || list1.size() != list2.size()) {
			return false;
		}
		Collections.sort(list1);
		Collections.sort(list2);
		return list1.equals(list2);
	}

	/**
	 * Checks if is two numbers equal.
	 *
	 * @param value1
	 *            the value 1
	 * @param value2
	 *            the value 2
	 * @return true, if is two numbers equal
	 */
	public static boolean isTwoNumbersEqual(int value1, int value2) {
		if (value1 == value2) {
			return true;
		}
		return false;
	}

	/**
	 * Less timedriver load.
	 *
	 * @param duration
	 *            the duration
	 */
	public static void lessTimedriverLoad(int... duration) {
		int durationNewValue = 0;
		if (duration.length == 0) {
			durationNewValue = 5;
		} else {
			durationNewValue = duration[0];
		}
		getCurrentDriver().manage().timeouts().implicitlyWait(durationNewValue, TimeUnit.SECONDS);
		getCurrentDriver().manage().timeouts().setScriptTimeout(durationNewValue, TimeUnit.SECONDS);
		getCurrentDriver().manage().timeouts().pageLoadTimeout(durationNewValue, TimeUnit.SECONDS);
	}

	/**
	 * Load page.
	 *
	 * @param url
	 *            the url
	 */
	public static void loadPage(String url) {
		LOGGER.info("Directing browser to:" + url);
		LOGGER.info("try to loadPage [" + url + "]");
		if (Browsers.browserForName(TestRunner.getBrowser()).equals(Browsers.IE))
			getCurrentDriver().navigate().to(url);
		else if (Browsers.browserForName(TestRunner.getBrowser()).equals(Browsers.FIREFOX)) {
			String parent = getCurrentDriver().getWindowHandle();
			((JavascriptExecutor) getCurrentDriver()).executeScript("window.open(' " + url + " ');");

			getCurrentDriver().switchTo().window(parent);
			getCurrentDriver().close();

			Set<String> windows = getCurrentDriver().getWindowHandles();
			Iterator<String> windowIterator = windows.iterator();
			while (windowIterator.hasNext()) {
				String child = windowIterator.next();
				getCurrentDriver().switchTo().window(child);
				break;
			}
		}
	}

	/**
	 * Authenticate windows popup.
	 *
	 * @param username
	 *            the username
	 * @param pwd
	 *            the pwd
	 */
	public static void authenticateWindowsPopup(String username, String pwd) {

		if (Browsers.browserForName(TestRunner.getBrowser()).equals(Browsers.IE)) {
			waitForAlert();
			Alert alert = getCurrentDriver().switchTo().alert();
			alert.authenticateUsing(new UserAndPassword(username, pwd));
		} else if (Browsers.browserForName(TestRunner.getBrowser()).equals(Browsers.FIREFOX)) {
			wait(10000);
			robotSendKeys(username);
			wait(1000);
			robotTabKeyPress();
			wait(1000);
			robotSendKeys(pwd);
			wait(1000);
			robotEnterKeyPress();
		}

	}

	/**
	 * Ignore certificate error IE.
	 */
	public static void ignoreCertificateErrorIE() {
		if (getCurrentDriver().findElements(By.id("overridelink")).size() > 0) {
			getCurrentDriver().get("javascript:document.getElementById('overridelink').click();");
		}
	}

	/**
	 * More timedriver load.
	 */
	public static void moreTimedriverLoad() {
		getCurrentDriver().manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		getCurrentDriver().manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS);
		getCurrentDriver().manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
	}

	/**
	 * Mouse hover by java script.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void MouseHoverByJavaScript(WebElement ele) {
		String javaScript = "var evObj = document.createEvent('MouseEvents');"
				+ "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "arguments[0].dispatchEvent(evObj);";
		JavascriptExecutor executor = (JavascriptExecutor) getCurrentDriver();
		executor.executeScript(javaScript, ele);
	}

	/**
	 * Move file to scenario.
	 *
	 * @param filename
	 *            the filename
	 */
	public static void moveFileToScenario(String filename) {
		File f = new File(myDownloadsPath + filename);
		System.out.println("From location: " + f); //Yash
		try {
			FileUtils.copyFileToDirectory(f, new File(cucumberScenarioDownloadFolder));
			FileUtils.copyFileToDirectory(f, new File(extentScenarioDownloadFolder));
			// f.delete(); // commeneted for SRDL application files because all
			// three files has same name

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Mouse hover andclick.
	 *
	 * @param elementToHover
	 *            the element to hover
	 * @param elementToClick
	 *            the element to click
	 */
	public static void mouseHoverAndclick(WebElement elementToHover, WebElement elementToClick) {

		try {
			String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); "
					+ "arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
			((JavascriptExecutor) getCurrentDriver()).executeScript(mouseOverScript, elementToHover);
			Thread.sleep(1000);
			((JavascriptExecutor) getCurrentDriver()).executeScript(mouseOverScript, elementToClick);
			Thread.sleep(1000);
			((JavascriptExecutor) getCurrentDriver()).executeScript("arguments[0].click();", elementToClick);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Mouse hover J script.
	 *
	 * @param HoverElement
	 *            the hover element
	 */
	public static void mouseHoverJScript(WebElement HoverElement) {
		try {
			if (isElementPresent(HoverElement)) {
				String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); "
						+ "arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
				((JavascriptExecutor) getCurrentDriver()).executeScript(mouseOverScript, HoverElement);
			} else {
				LOGGER.info("Element was not visible to hover " + "\n");
			}
		} catch (StaleElementReferenceException e) {
			/*
			 * LOGGER.info("Element with " + HoverElement +
			 * "is not attached to the page document" + e.getStackTrace());
			 */
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		} catch (NoSuchElementException e) {
			/*
			 * LOGGER.info("Element " + HoverElement + " was not found in DOM" +
			 * e.getStackTrace());
			 */
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Move to element action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void moveToElementAction(WebElement ele) {
		Actions actions = new Actions(getCurrentDriver());
		actions.moveToElement(ele);
		actions.build().perform();
	}

	/**
	 * Navigate back.
	 */
	public static void navigateBack() {

		LOGGER.info("Navigating to back page :: started");
		getCurrentDriver().navigate().forward();
		LOGGER.info("Navigating to back page :: completed");
	}

	/**
	 * Navigate forward.
	 */
	public static void navigateForward() {

		LOGGER.info("Navigating to forward page :: started");
		getCurrentDriver().navigate().forward();
		LOGGER.info("Navigating to forward page :: completed");
	}

	/**
	 * Quit.
	 */
	public static void quit() {
		if (mDriver != null) {
			try {
				getCurrentDriver().unregister(BrowserFactory.getHandler());
				getCurrentDriver().quit();
				mDriver = null;

				LOGGER.info("closing the browser");
			} catch (UnreachableBrowserException e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
			} catch (NullPointerException npe) {
				LOGGER.log(Level.WARNING, npe.getStackTrace().toString(), npe);
			}
		}
		/**
		 * try { Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe"
		 * ); }catch (IOException e){ e.printStackTrace(); }
		 */

	}

	public static void close() {
		if (mDriver == null) {
			return;
		}
		getCurrentDriver().quit();

		mDriver = null;

	}

	/**
	 * Reopen and load page.
	 *
	 * @param url
	 *            the url
	 */
	public static void reopenAndLoadPage(String url) {
		mDriver = null;
		getCurrentDriver();
		loadPage(url);
	}

	/**
	 * Wait and get all options text.
	 *
	 * @param ele
	 *            the ele
	 * @param options
	 *            the options
	 * @return the list
	 */
	public static List<String> waitAndGetAllOptionsText(WebElement ele, List<WebElement> options) {
		List<String> getAllOptions = new ArrayList<String>();
		final int listSize;

		listSize = new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
				.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
					int size = options.size();
					return size;
				});

		for (int i = 0; i < listSize; i++) {
			String optionXpath;
			final int j = i;

			optionXpath = new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
					.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {

						String xpath = getElementXPath(options.get(j));
						return xpath;

					});

			new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
					.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
						getAllOptions.add(d.findElement(By.xpath(optionXpath)).getText());
						return true;
					});
		}
		return getAllOptions;
	}

	/**
	 * Fluient waitfor element.
	 *
	 * @param element
	 *            the element
	 * @param timoutSec
	 *            the timout sec
	 * @param pollingSec
	 *            the polling sec
	 * @return the web element
	 */
	public static WebElement fluientWaitforElement(WebElement element, int timoutSec, int pollingSec) {
		FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(getCurrentDriver())
				.withTimeout(timoutSec, TimeUnit.SECONDS).pollingEvery(pollingSec, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class, TimeoutException.class)
				.ignoring(StaleElementReferenceException.class);
		for (int i = 0; i < 2; i++)
			fWait.until(ExpectedConditions.visibilityOf(element));
		return element;
	}

	/**
	 * Wait and get all option elements.
	 *
	 * @param ele
	 *            the ele
	 * @return the list
	 */
	public static List<WebElement> waitAndGetAllOptionElements(WebElement ele) {
		List<WebElement> allOptions = new ArrayList<WebElement>();
		allOptions = new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
				.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {

					String elementXpath = getElementXPath(ele);
					Select select = new Select(d.findElement(By.xpath(elementXpath)));
					return select.getOptions();
				});
		return allOptions;
	}

	/**
	 * Wait and multiple select.
	 *
	 * @param ele
	 *            the ele
	 * @param values
	 *            the values
	 */
	public static void waitAndMultipleSelect(WebElement ele, List<String> values) {
		for (String value : values) {
			new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
					.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
						String elementXpath = getElementXPath(ele);
						Select select = new Select(d.findElement(By.xpath(elementXpath)));
						select.selectByVisibleText(value);
						//ele.sendKeys(Keys.CONTROL);

						return true;
					});
		}
	}

	/**
	 * 
	 * @param ele
	 * @param derivedFileList
	 */
	public static void waitAndMultipleSelectByIndex(WebElement ele, List<Integer> derivedFileList) {
		try {
			for (int value : derivedFileList) {
				new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
						.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
							String elementXpath = getElementXPath(ele);
							Select select = new Select(d.findElement(By.xpath(elementXpath)));
							select.selectByIndex(value);
							//ele.sendKeys(Keys.CONTROL);
							return true;
						});
			}

		} catch (TimeoutException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);

		} catch (StaleElementReferenceException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);

		}

	}

	/**
	 * Wait and select all.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void waitAndSelectAll(WebElement ele) {
		try {
			waitAndMultipleSelect(ele, waitAndGetAllOptionsText(ele, waitAndGetAllOptionElements(ele)));
		} catch (TimeoutException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);

		} catch (StaleElementReferenceException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);

		}
	}

	/**
	 * Wait and click.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void waitAndClick(WebElement ele) {
		new WebDriverWait(getCurrentDriver(), Global.PAGELOADTIME.getValue())
				.ignoring(StaleElementReferenceException.class).until((WebDriver d) -> {
					String elementXpath = getElementXPath(ele);
					WebElement eleTemp = d.findElement(By.xpath(elementXpath));
					eleTemp.click();
					return true;
				});
	}

	/**
	 * Robot send keys.
	 *
	 * @param str
	 *            the str
	 */
	public static void robotSendKeys(String str) {
		StringSelection stringSelection = new StringSelection(str);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, stringSelection);

		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Robot tab key press.
	 */
	public static void robotTabKeyPress() {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Robot enter key press.
	 */
	public static void robotEnterKeyPress() {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Screenshot filename.
	 *
	 * @return the string
	 */
	public static String screenshotFilename() {
		Date d = new Date();
		String filename = d.toString().replace(":", "_").replace(" ", "_") + ".png";
		return filename;
	}

	/**
	 * Scroll to element action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void scrollToElementAction(WebElement ele) {
		int Width = ele.getLocation().getX();
		int Height = ele.getLocation().getY();
		int MyX = (Width * 95) / 100;// spot to click is at 95% of the width
		int MyY = Height / 2;// anywhere above Height/2 works
		Actions Actions = new Actions(getCurrentDriver());
		Actions.moveToElement(ele, MyX, MyY);
		Actions.clickAndHold();
		Actions.release();
		Actions.perform();
	}

	/**
	 * Scroll to elementjs.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void scrollToElementjs(WebElement ele) {
		((JavascriptExecutor) getCurrentDriver()).executeScript("arguments[0].scrollIntoView();", ele);
	}

	/**
	 * Scroll to element point.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void scrollToElementPoint(WebElement ele) {
		try {
			Point hoverItem = ele.getLocation();
			((JavascriptExecutor) getCurrentDriver()).executeScript("return window.title;");
			Thread.sleep(6000);
			((JavascriptExecutor) getCurrentDriver()).executeScript("window.scrollBy(0," + (hoverItem.getY()) + ");");
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Toggle browser window.
	 */
	public static void toggleBrowserWindow() {
		Robot r;
		try {
			r = new Robot();
			r.keyPress(KeyEvent.VK_F11);
			toggleCount++;
			if (toggleCount % 2 == 0) {
				setBrowserToggleFlag(false);
			} else {
				setBrowserToggleFlag(true);
			}
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Mouse move point robot.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void mouseMovePointRobot(WebElement ele) {
		Point p = ele.getLocation();
		int x = p.getX();
		int y = p.getY();
		Dimension d = ele.getSize();
		int h = d.getHeight();
		int w = d.getWidth();
		Robot r;
		try {
			r = new Robot();
			r.mouseMove((x + (w / 2)), (y + (h / 2)));
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Scroll using event firing web driver.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void scrollUsingEventFiringWebDriver(WebElement ele) {
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(getCurrentDriver());
		new Actions(eventFiringWebDriver).moveToElement(ele).build().perform();
	}

	/**
	 * Scroll within element.
	 *
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 */
	public static void scrollWithinElement(WebElement parent, WebElement child) {
		((JavascriptExecutor) getCurrentDriver()).executeScript("arguments[0].scrollTop=arguments[1].offsetTop", parent,
				child);
	}

	/**
	 * Checks if is visible in viewport.
	 *
	 * @param element
	 *            the element
	 * @return the boolean
	 */
	public static Boolean isVisibleInViewport(WebElement element) {
		return (Boolean) ((JavascriptExecutor) getCurrentDriver()).executeScript(
				"var elem = arguments[0],                 " + "  box = elem.getBoundingClientRect(),    "
						+ "  cx = box.left + box.width / 2,         " + "  cy = box.top + box.height / 2,         "
						+ "  e = document.elementFromPoint(cx, cy); " + "for (; e; e = e.parentElement) {         "
						+ "  if (e === elem)                        " + "    return true;                         "
						+ "}                                        " + "return false;                            ",
				element);
	}

	/**
	 * Checks if is element inside.
	 *
	 * @param elements
	 *            the elements
	 * @return true, if is element inside
	 */
	public static boolean isElementInside(List<WebElement> elements) {
		for (int i = 0; i < elements.size(); i++) {
			int top = Integer.parseInt(elements.get(i).getAttribute("offsetTop"));
			int left = Integer.parseInt(elements.get(i).getAttribute("offsetLeft"));
			if (top < 0 || left < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Mouse over.
	 *
	 * @param element
	 *            the element
	 */
	public static void mouseOver(WebElement element) {
		Actions builder = new Actions(getCurrentDriver());
		builder.moveToElement(element).build().perform();

	}

	/**
	 * Mouse over J query.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void mouseOverJQuery(WebElement ele) {
		mouseOver(ele);
		String jQuery = "jQuery('";
		String mouseOver = "').mouseover();";
		JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
		js.executeScript(jQuery + getElementXPath(ele) + mouseOver);
		try {
			Thread.sleep(500); // thread sleeps for 5 seconds so we can catch if
								// element displayed or not.
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Scroll within element robot.
	 *
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 * @param lastChild
	 *            the last child
	 * @param p
	 *            the p
	 * @return true, if successful
	 */
	public static boolean scrollWithinElementRobot(WebElement parent, WebElement child, WebElement lastChild, Point p) {

		Robot bot = null;
		try {
			bot = new Robot();
			bot.setAutoDelay(1);
			bot.mouseMove(p.x, p.y);
			bot.mousePress(InputEvent.BUTTON1_MASK);
			bot.mouseRelease(InputEvent.BUTTON1_MASK);
			while (!isVisibleInViewport(child)) {
				bot.keyPress(KeyEvent.VK_PAGE_DOWN);
				bot.keyRelease(KeyEvent.VK_PAGE_DOWN);
				waitForPageLoadjs();
				if (isVisibleInViewport(child))
					return true;
				if (isVisibleInViewport(lastChild))
					return false;
			}
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
		return false;
	}

	/**
	 * Gets the element absolute location.
	 *
	 * @param ele
	 *            the ele
	 * @return the element absolute location
	 */
	public static Point getElementAbsoluteLocation(WebElement ele) {
		Point p;
		Point elementLoc = ele.getLocation();
		int x = elementLoc.getX();
		int y = elementLoc.getY();
		p = new Point(x, y);
		return p;
	}

	/**
	 * Scroll within element action.
	 *
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 * @return true, if successful
	 */
	public static boolean scrollWithinElementAction(WebElement parent, WebElement child) {
		Actions dragger = new Actions(getCurrentDriver());
		// drag downwards
		int numberOfPixelsToDragTheScrollbarDown = 10;
		while (isVisibleInViewport(child)) {
			try {
				dragger.moveToElement(parent).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown)
						.release(parent).build().perform();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Select by index.
	 *
	 * @param ele
	 *            the ele
	 * @param index
	 *            the index
	 */
	public static void selectByIndex(WebElement ele, int index) {
		Select select = new Select(ele);
		select.selectByIndex(index);
		LOGGER.info("Selected index is:: " + index);
	}

	/**
	 * Select by value.
	 *
	 * @param ele
	 *            the ele
	 * @param value
	 *            the value
	 */
	public static void selectByValue(WebElement ele, String value) {
		Select select = new Select(ele);
		select.selectByValue(value.trim());
		LOGGER.info("Selected value is:: " + value);
	}

	/**
	 * Select by visible text.
	 *
	 * @param ele
	 *            the ele
	 * @param visibleText
	 *            the visible text
	 */
	public static void selectByVisibleText(WebElement ele, String visibleText) {
		System.out.println("Inside selectByVisibleText " + visibleText);
		Select select = new Select(ele);
		System.out.println(select.getFirstSelectedOption().getText());
		select.selectByVisibleText(visibleText);
		LOGGER.info("Selected visible text is:: " + visibleText);
		System.out.println("Exiting selectByVisibleText" + visibleText);	
	}
	
	/**
	 * Select item.
	 *
	 * @param ele
	 *            the ele
	 * @param optionStr
	 *            the option str
	 */
	public static void SelectItem(WebElement ele, String optionStr) {
		List<WebElement> options = ele.findElements(By.tagName("option"));
		for (WebElement option : options) {
			System.out.println(option);
			if (option.getText().equals(optionStr)) {

				option.click();
			}
		}
	}

	/**
	 * Select multiple values.
	 *
	 * @param ele
	 *            the ele
	 * @param values
	 *            the values
	 */
	public static void selectMultipleValues(WebElement ele, List<String> values) {
		for (String value : values) {
			System.out.println("Selected items are " + value);
			// new Select(ele).selectByVisibleText(value.trim());
			new Select(ele).deselectByVisibleText(value);
			ele.sendKeys(Keys.CONTROL);
		}
	}

	/**
	 * Select value from drop down list.
	 *
	 * @param SummaryList
	 *            the summary list
	 * @param value
	 *            the value
	 */
	public static void selectValueFromDropDownList(WebElement SummaryList, String value) {
		boolean drpValue = false;
		String singleValue = null;
		List<WebElement> summaryView = SummaryList.findElements(By.xpath("//*"));
		for (WebElement element : summaryView) {
			singleValue = element.getText();
			if (singleValue.trim().equalsIgnoreCase(value.trim())) {
				element.click();
				drpValue = true;
				break;
			}
		}
		if (drpValue)
			LOGGER.info(value + " :: is selected from the dropown");
		else {
			LOGGER.severe(value + " :: is not selected from the dropown");
			Assert.fail(value.toUpperCase() + " value was not selected from dropdown "
					+ getText(SummaryList).toUpperCase());
		}
	}

	/**
	 * Send keys.
	 *
	 * @param ele
	 *            the ele
	 * @param text
	 *            the text
	 */
	public static void sendKeys(WebElement ele, String text) {
		ele.sendKeys(text);
		LOGGER.info("Text Entered:: " + text);
	}

	/**
	 * Send secure.
	 *
	 * @param ele
	 *            the ele
	 * @param text
	 *            the text
	 */
	public static void sendSecure(WebElement ele, String text) {
		ele.sendKeys(text);
		LOGGER.info("Password Entered:: " + getMaskedPassword(text));
	}

	/**
	 * Gets the masked password.
	 *
	 * @param text
	 *            the text
	 * @return the masked password
	 */
	public static String getMaskedPassword(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++)
			sb.append("*");
		return sb.toString();
	}

	/**
	 * Sets the parent window handle.
	 */
	public static void setParentWindowHandle() {
		parentWindowHandler = getCurrentDriver().getWindowHandle();
	}

	/**
	 * Sets the res folder name.
	 *
	 * @param resFolderName
	 *            the new res folder name
	 */
	public static void setResFolderName(String resFolderName) {
		BrowserDriver.resFolderName = resFolderName;
	}

	/**
	 * Sets the log file name.
	 *
	 * @param logFileName
	 *            the new log file name
	 */
	public static void setLogFileName(String logFileName) {
		BrowserDriver.logFileName = logFileName;
	}

	/**
	 * Switch to parent window.
	 */
	public static void switchToParentWindow() {
		if (popupWindowHandlerStatus) {
			getCurrentDriver().switchTo().window(parentWindowHandler);
			waitForPageLoadjs();
			popupWindowHandlerStatus = false;
		}
	}

	/**
	 * Switch to popup window.
	 */
	public static void switchToPopupWindow() {
		try {
			Thread.sleep(2000);
			String subWindowHandler = null;
			Set<String> handles = getCurrentDriver().getWindowHandles(); // get
																			// all
																			// window
																			// handles
			LOGGER.info("Total window handles: " + handles.size());
			Iterator<String> iterator = handles.iterator();
			while (iterator.hasNext()) {
				subWindowHandler = iterator.next();
				String popupHandle = subWindowHandler.toString();
				if (!popupHandle.contains(parentWindowHandler)) {
					getCurrentDriver().switchTo().window(popupHandle);
					break;
				}
			}
			waitForPageLoadjs();
			popupWindowHandlerStatus = true;
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Switchwindow.
	 *
	 * @param pageTitle
	 *            the page title
	 */
	public static void switchwindow(String pageTitle) {
		String currentWindow = getCurrentDriver().getWindowHandle();
		Set<String> handles = getCurrentDriver().getWindowHandles();
		LOGGER.info("Total window handles: " + handles.size());
		for (String winHandle : handles) {
			String currentWindowTitle = getCurrentDriver().switchTo().window(winHandle).getTitle();
			LOGGER.info("Current window title: " + currentWindowTitle);
			if (currentWindowTitle.equals(pageTitle)) {
				break;
			} else {
				getCurrentDriver().switchTo().window(currentWindow);
			}
		}
	}

	public static void switctoChildhwindow() {
		String parentWindow = getCurrentDriver().getWindowHandle();
		Set<String> allhandles = getCurrentDriver().getWindowHandles();
		LOGGER.info("Total window handles: " + allhandles.size());
		for (String winHandle : allhandles) {

			if (!winHandle.equals(parentWindow)) {
				getCurrentDriver().switchTo().window(winHandle);
			}
		}
	}

	/**
	 * Tab action.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void tabAction(WebElement ele) {
		ele.sendKeys(Keys.TAB);
		LOGGER.info("Tab action performed");
	}

	/**
	 * Tab action.
	 */
	public static void tabAction() {
		Actions builder = new Actions(getCurrentDriver());
		builder.sendKeys(Keys.TAB).build().perform();
		builder.release().perform();
		LOGGER.info("Tab action performed");
	}

	/**
	 * Take screenshot.
	 *
	 * @param resultFolderPath
	 *            the result folder path
	 */
	public static void takeScreenshot(String resultFolderPath) {
		// File scrFile =
		// ((TakesScreenshot)getCurrentDriver()).getScreenshotAs(OutputType.FILE);
		byte[] bytes = ((TakesScreenshot) getCurrentDriver()).getScreenshotAs(OutputType.BYTES);
		try {
			// FileUtils.copyFile(scrFile, new File(resultFolderPath +
			// "/image/png/" + screenshotFilename()));
			String screenshotFileName = screenshotFilename();
			FileUtils.writeByteArrayToFile(new File(resultFolderPath + "/image/png/" + screenshotFileName), bytes);
			Reporter.addScreenCaptureFromPath(getResFolderName() + "\\image\\png\\" + screenshotFileName);
			embedScreenshotToReport(Initializer.getScenario());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Take screenshot using robot.
	 *
	 * @param resultFolderPath
	 *            the result folder path
	 */
	public static void takeScreenshotUsingRobot(String resultFolderPath) {
		BufferedImage image = null;
		try {
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			String screenshotFileName = screenshotFilename();
			ImageIO.write(image, "png", new File(resultFolderPath + "\\image\\png\\" + screenshotFileName));
			Reporter.addScreenCaptureFromPath(getResFolderName() + "\\image\\png\\" + screenshotFileName);
			embedScreenshotToReportRobot(Initializer.getScenario());
		} catch (HeadlessException e2) {
			LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
		} catch (AWTException e2) {
			LOGGER.log(Level.SEVERE, e2.getStackTrace().toString(), e2);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Verify attribute value.
	 *
	 * @param ele
	 *            the ele
	 * @param expectedString
	 *            the expected string
	 */
	public static void verifyAttributeValue(WebElement ele, String expectedString) {
		waitForVisibilityOfElementLocated(ele);
		String actualString = ele.getAttribute("value").toString();
		verifyText(actualString, expectedString);
	}

	/**
	 * Verify dropdown all values.
	 *
	 * @param byActualValues
	 *            the by actual values
	 * @param expectedValues
	 *            the expected values
	 */
	public static void verifyDropdownAllValues(WebElement byActualValues, String[] expectedValues) {

		List<WebElement> weValues = byActualValues.findElements(By.xpath("//*"));
		List<Object> actualValues = new ArrayList<Object>();

		// To get all the values in list
		for (int i = 0; i < weValues.size(); i++) {
			actualValues.add(weValues.get(i).getText());
		}
		LOGGER.info("Actual Value in dropdown ::" + actualValues);

		// Compare actualValues list with expectedValues string array
		boolean compareValues = false;
		if ((actualValues.size() == expectedValues.length)) {
			for (int j = 0; j < actualValues.size(); j++) {
				if (expectedValues[j].equals(actualValues.get(j))) {
					compareValues = true;
				} else {
					compareValues = false;
					break;
				}
			}
		}
		Assert.assertTrue(compareValues);
	}

	/**
	 * Verify dropdown selected text.
	 *
	 * @param byDropdownElement
	 *            the by dropdown element
	 * @param expectedString
	 *            the expected string
	 */
	public static void verifyDropdownSelectedText(WebElement byDropdownElement, String expectedString) {
		String actualString = getDropdownSelectedOption(byDropdownElement);
		verifyText(actualString, expectedString);
	}

	/**
	 * Verify element is displayed.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsDisplayed(WebElement ele) {
		Assert.assertTrue("Element is displayed ", isElementDisplayed(ele));
	}

	/**
	 * Verify element is enabled.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsEnabled(WebElement ele) {
		Assert.assertTrue("Element is enabled ", isElementEnabled(ele));
	}

	/**
	 * Verify element is not displayed.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsNotDisplayed(WebElement ele) {
		Assert.assertFalse("Element is displayed, It should not be displayed ", isElementDisplayed(ele));
	}

	/**
	 * Verify element is not enabled.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsNotEnabled(WebElement ele) {
		Assert.assertFalse("Element is enabled, It should not be enabled ", isElementEnabled(ele));
	}

	/**
	 * Verify element is not present.
	 *
	 * @param by
	 *            the by
	 */
	public static void verifyElementIsNotPresent(By by) {
		Assert.assertFalse("Element is present, It should not be present ", isElementNotPresent(by));
	}

	/**
	 * Verify element is not selected.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsNotSelected(WebElement ele) {
		Assert.assertFalse("Element is selected, It should not be selected ", isElementSelected(ele));
	}

	/**
	 * Verify element is present.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsPresent(WebElement ele) {
		Assert.assertTrue("Element is present ", isElementPresent(ele));
	}

	/**
	 * Verify element is selected.
	 *
	 * @param ele
	 *            the ele
	 */
	public static void verifyElementIsSelected(WebElement ele) {
		Assert.assertTrue("Element is selected ", isElementSelected(ele));
	}

	/**
	 * Verify label text.
	 *
	 * @param ele
	 *            the ele
	 * @param expectedString
	 *            the expected string
	 */
	public static void verifyLabelText(WebElement ele, String expectedString) {
		waitForVisibilityOfElementLocated(ele);
		String actualString = ele.getText();
		verifyText(actualString, expectedString);
	}

	/**
	 * Verify partial text.
	 *
	 * @param ele
	 *            the ele
	 * @param strText
	 *            the str text
	 * @return true, if successful
	 */
	public static boolean verifyPartialText(WebElement ele, String strText) {
		boolean text = false;
		List<WebElement> partialText = ele.findElements(By.xpath(".//*"));
		for (WebElement option : partialText) {
			if (option.getText().replaceAll("[^\\w\\s]", "").trim()
					.contains(strText.trim().replaceAll("[^\\w\\s]", ""))) {
				LOGGER.info(strText + " => message is displayed");
				text = true;
				break;

			}
		}
		return text;
	}

	/**
	 * Verify table data.
	 *
	 * @param TableMap
	 *            the table map
	 * @param columnName
	 *            the column name
	 * @param value
	 *            the value
	 */
	public static void verifyTableData(HashMap<String, List<String>> TableMap, String columnName, String value) {
		for (int i = 0; i < TableMap.get(columnName).size(); i++) {
			if (TableMap.get(columnName).get(i).toLowerCase().contains(value.toLowerCase()))
				LOGGER.info("Verified:: " + value + " is displayed for table row:: " + i);
			else {
				LOGGER.severe(value + " is not displayed");
				Assert.fail(value.toUpperCase() + " was not displayed");
			}
		}
	}

	/**
	 * Verify tab visible.
	 *
	 * @param ele
	 *            the ele
	 * @param tabName
	 *            the tab name
	 */
	public static void verifyTabVisible(WebElement ele, String tabName) {
		wait(1000);
		if (!ele.getAttribute("class").contains("disabled")) {
			LOGGER.info("Verified:: " + tabName + " enabled ");
		} else {
			LOGGER.severe("Verified:: " + tabName + " disabled");
			Assert.fail(tabName + " is disabled ");
		}
	}

	/**
	 * Verify text.
	 *
	 * @param actualString
	 *            the actual string
	 * @param expectedString
	 *            the expected string
	 */
	public static void verifyText(String actualString, String expectedString) {
		Assert.assertEquals("Actual and Expected label text does not match", expectedString, actualString);
		LOGGER.info(actualString + " => Label Text is correct");
	}

	/**
	 * Verify text in textbox.
	 *
	 * @param ele
	 *            the ele
	 * @param expectedString
	 *            the expected string
	 */
	public static void verifyTextInTextbox(WebElement ele, String expectedString) {
		String actualString = getAttribute(ele, "value");
		verifyText(actualString, expectedString);
	}

	/**
	 * Verify text present in list.
	 *
	 * @param eleValues
	 *            the ele values
	 * @param header
	 *            the header
	 * @param strHeader
	 *            the str header
	 * @param strText
	 *            the str text
	 */
	public static void verifyTextPresentInList(WebElement eleValues, WebElement header, String strHeader,
			String strText) {
		boolean match = false;
		List<WebElement> valueText = eleValues.findElements(By.xpath("//*"));
		List<WebElement> headerText = header.findElements(By.xpath("//*"));
		for (int i = 0; i < valueText.size(); i++) {
			if (valueText.get(i).getText().toLowerCase().replaceAll("[^\\w\\s]", "").trim()
					.equals(strText.toLowerCase().trim().replaceAll("[^\\w\\s]", ""))) {
				if (headerText.get(i).getText().toLowerCase().replaceAll("[^\\w\\s]", "").trim()
						.equals(strHeader.toLowerCase().trim().replaceAll("[^\\w\\s]", ""))) {
					match = true;
					break;
				}
			}
		}

		if (match)
			LOGGER.info(strHeader + ": " + strText + " => message is displayed");
		else {
			LOGGER.severe(strHeader + ": " + strText + " => message is not displayed");
			Assert.fail(strText.toUpperCase() + "message is not displayed");
		}
	}

	/**
	 * Wait.
	 *
	 * @param milliseconds
	 *            the milliseconds
	 */
	public static void wait(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Wait for alert.
	 */
	public static void waitForAlert() {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		wait.until(ExpectedConditions.alertIsPresent());
	}

	/**
	 * wait fo Stalenessof Webelemnt
	 * 
	 * @param ele
	 */
	public static void waitForStalenessOfWebelement(WebElement ele) {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		wait.until(ExpectedConditions.stalenessOf(ele));
	}

	/**
	 * Wait for alert and accept.
	 */
	public static void waitForAlertAndAccept() {
		int i = 0;
		while (i++ < 5) {
			Alert alert;
			try {
				System.out.println("this is inside alert");
				alert = getCurrentDriver().switchTo().alert();
				System.out.println("this is after switch alert");
				alert.accept();
				break;
			} catch (UnhandledAlertException uae) {
				LOGGER.log(Level.SEVERE, uae.getStackTrace().toString(), uae);
				alert = getCurrentDriver().switchTo().alert();
				alert.accept();
				break;
			} catch (NoAlertPresentException nape) {
				LOGGER.log(Level.SEVERE, nape.getStackTrace().toString(), nape);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
				}
				continue;
			}
		}
	}

	/**
	 * Wait for element.
	 *
	 * @param ele
	 *            the ele
	 * @return the web element
	 */
	public static WebElement waitForElement(WebElement ele) {
		return waitForElement(ele, null);
	}

	/**
	 * Wait for element.
	 *
	 * @param elementToWaitFor
	 *            the element to wait for
	 * @param waitTimeInSeconds
	 *            the wait time in seconds
	 * @return the web element
	 */
	public static WebElement waitForElement(WebElement elementToWaitFor, Integer waitTimeInSeconds) {
		if (waitTimeInSeconds == null) {
			waitTimeInSeconds = 240;
		}

		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), waitTimeInSeconds);
		return wait.until(ExpectedConditions.visibilityOf(elementToWaitFor));
	}

	/**
	 * Wait for element to be clickable.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if successful
	 */
	public static boolean waitForElementToBeClickable(WebElement ele) {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		WebElement waitElement = wait.until(ExpectedConditions.elementToBeClickable(ele));
		if (waitElement != null) {
			return true;
		} else {
			LOGGER.info(
					"Class: BrowserDriver | method:waitForElementToBeClickable | message: Element is not clickable after waiting for "
							+ Global.WEBDRIVERWAIT + " seconds");
			return false;
		}
	}

	/**
	 * Wait for element to be visible for minimun polling.
	 *
	 * @param ele
	 *            the ele
	 * @param duration
	 *            the duration
	 * @return the web element
	 */
	public static WebElement waitForElementToBeVisibleForMinimunPolling(WebElement ele, int... duration) {
		int durationNewValue = 0;
		if (duration.length == 0) {
			durationNewValue = 50;
		} else {
			durationNewValue = duration[0];
		}
		Wait<WebDriver> wait = new FluentWait<WebDriver>(getCurrentDriver())
				.withTimeout(durationNewValue, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		WebElement webElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(ele, null));
		return webElement;
	}

	/**
	 * Wait for javascript.
	 *
	 * @param maxWaitMillis
	 *            the max wait millis
	 * @param pollDelimiter
	 *            the poll delimiter
	 */
	public static void waitForJavascript(int maxWaitMillis, int pollDelimiter) {
		double startTime = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() < startTime + maxWaitMillis) {
				String prevState = getCurrentDriver().getPageSource();
				Thread.sleep(pollDelimiter);
				if (prevState.equals(getCurrentDriver().getPageSource())) {
					return;
				}
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Wait for page loadjs.
	 */
	public static void waitForPageLoadjs() {
		if (!isAlertPresent()) {
			waitForJavascript(10000, 1000);
			JavascriptExecutor js = (JavascriptExecutor) getCurrentDriver();
			js.executeScript("return document.readyState").toString().equals("complete");
		}
	}

	/**
	 * Wait for presence of element located.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if successful
	 */
	public static boolean waitForPresenceOfElementLocated(WebElement ele) {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		WebElement waitElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(ele, null));
		if (waitElement != null) {
			return true;
		} else {
			LOGGER.info(
					"Class: BrowserDriver | method:waitForElement | message: Element is not present after waiting for "
							+ Global.WEBDRIVERWAIT + " seconds");
			return false;
		}
	}

	/**
	 * Wait for title of page.
	 *
	 * @param title
	 *            the title
	 * @return true, if successful
	 */
	public static boolean waitForTitleOfPage(String title) {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		Boolean waitTitle = wait.until(ExpectedConditions.titleContains(title));
		if (!waitTitle) {
			LOGGER.info(
					"Class: BrowserDriver | method:waitForElement | message: Element is not present after waiting for "
							+ Global.WEBDRIVERWAIT + " seconds");
			return false;
		} else
			return true;
	}

	/**
	 * Wait for visibility of element located.
	 *
	 * @param ele
	 *            the ele
	 * @return true, if successful
	 */
	public static boolean waitForVisibilityOfElementLocated(WebElement ele) {
		WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Global.WEBDRIVERWAIT.getValue());
		WebElement waitElement = wait.until(ExpectedConditions.visibilityOf(ele));
		if (waitElement != null) {
			return true;
		} else {
			LOGGER.info(
					"Class: BrowserDriver | method:waitForElement | message: Element is not present after waiting for "
							+ Global.WEBDRIVERWAIT + " seconds");
			return false;
		}
	}

	/**
	 * Wait for element not present.
	 *
	 * @param by
	 *            the by
	 */
	public static void waitForElementNotPresent(By by) {
		try {
			while (!(ElementStatus.isElementVisible(by).equals(ElementStatus.NOTPRESENT))) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
		}
	}

	/**
	 * Write to file.
	 *
	 * @param contents
	 *            the contents
	 * @return true, if successful
	 */
	public static boolean writeToFile(String contents) {
		String resultFolder = getResFolderName() + "\\extract\\csv\\";
		FileUtilities.createDir(resultFolder);
		String csvFilePath = resultFolder + csvFilename();
		PrintWriter writer;
		try {
			writer = new PrintWriter(csvFilePath, "UTF-8");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString(), e);
			return false;
		}
		writer.print(contents);
		writer.close();
		attachCSV(csvFilePath);
		return true;
	}

	/**
	 * Write to results.
	 *
	 * @param msg
	 *            the msg
	 */
	public static void writeToResults(String msg) {
		Reporter.addStepLog(msg);
		Initializer.getScenario().write(msg);
	}

	/**
	 * Checks if is attribtue present.
	 *
	 * @param element
	 *            the element
	 * @param attribute
	 *            the attribute
	 * @return true, if is attribtue present
	 */
	public static boolean isAttribtuePresent(WebElement element, String attribute) {
		Boolean result = false;
		String value = element.getAttribute(attribute);
		if (value != null)
			result = true;
		return result;
	}

}
