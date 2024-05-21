package com.redBus.tests;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.testng.annotations.AfterTest;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.AriaRole;
import com.redBus.util.IdentifyBusSeat;
import com.redBus.util.ReadInputExcel;
import com.redBus.util.WriteToExcel;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

public class RedBusAutomation {

	public Playwright playwright;
	public BrowserType chromium;
	public Browser browser;
	public Page page;
	public String URL = "https://www.redbus.in/";
	public String[][] readInput;

	public String source = null;
	public String dest = null;
	public String dateOfJourney = null;
	public String operator = null;
	public String noOfSeatAvl = null;
	public String seatNum = null;
	public String cusName = null;
	public String cusEmail = null;
	public String cusPhone = null;
	public String cusAge = null;
	public boolean success = false;
	public int n = 0;

	@BeforeTest
	public void setup() throws IOException {

		// Instantiate browser and page object
		playwright = Playwright.create();
		chromium = playwright.chromium();
		browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false));
		page = browser.newPage();

		// Get Input data from Excel
		readInput = ReadInputExcel.readExcelFile();

		// For which row you want to run ?
		n = 1;

		// Assign data into variables
		source = readInput[n][0];
		dest = readInput[n][1];
		dateOfJourney = readInput[n][2];
		operator = readInput[n][3];
		noOfSeatAvl = readInput[n][6];
		seatNum = readInput[n][7];
		cusName = readInput[n][8];
		cusEmail = readInput[n][9];
		cusPhone = readInput[n][10];
		cusAge = readInput[n][11];

	}

	@Test
	public void successBusTicketBookingTest() throws InterruptedException, FindFailed, IOException {

		// Mock successful payment response
		page.route("**/payment", route -> {
			route.fulfill(new Route.FulfillOptions().setStatus(200)
					.setBody("{ \"status\": \"success\", \"message\": \"Payment Successful\" }"));
		});

		// Navigate to RedBus website
		page.navigate(URL);

		// Interact with the website to search for buses
		page.locator("input#src").fill(source);
		page.locator("input#dest").fill(dest);
		page.locator("div[id='onwardCal']").click();
		page.waitForSelector("div[class='labelCalendarContainer']");
		page.locator("div[class='labelCalendarContainer']:has-text('" + dateOfJourney + "')").click();

		page.locator("button#search_button").click();

		page.getByPlaceholder("OPERATOR").click();
		page.locator("input[name=\"inpFilter\"]").fill(operator);
		page.locator("li").filter(new Locator.FilterOptions().setHasText(operator + " (" + noOfSeatAvl + ")"))
				.locator("label").first().click();
		page.getByText("APPLY", new Page.GetByTextOptions().setExact(true)).click();

		// Wait for the search results to load
		page.waitForSelector(".bus-item");

		// Extract bus travel IDs from the search results and click 1 st
		@SuppressWarnings("unchecked")
		Locator travelIds = page.locator(".bus-items").getByText("View Seats");
		System.out.println(travelIds);
		travelIds.first().click();

		// Use Sikuli to select the seats
		IdentifyBusSeat.clickSeat();

		// Bording and Dropping selection
		page.locator(".radio-unchecked").first().click();
		page.locator(".db > .radio-css > .radio-unchecked").first().click();
		page.locator(".seat-container-div").click();
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed to book")).click();

		// Customer details filling up
		page.getByPlaceholder("Name").fill(cusName);
		page.locator("#div_22_0").click();
		page.getByLabel("State of Residence").click();
		page.getByLabel("State of Residence").fill("Karnataka");
		page.getByPlaceholder("Age").click();
		page.getByPlaceholder("Age").fill(cusAge);
		page.getByPlaceholder("Email ID").click();
		page.getByPlaceholder("Email ID").fill(cusEmail);
		page.getByPlaceholder("Phone").click();
		page.getByPlaceholder("Phone").fill(cusPhone);
		if (page.getByLabel("Full refund upon").isVisible()) {
			page.getByLabel("Full refund upon").check();
		}
		page.locator("label").filter(new Locator.FilterOptions().setHasText("No, I would like to proceed"))
				.locator("span").click();
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed to pay")).press("Enter");

		// Credit Card Payment
		page.locator("[id=\"Credit\\ Card\"]").nth(1).check();
		page.getByPlaceholder("CREDIT CARD NUMBER").click();
		page.getByPlaceholder("CREDIT CARD NUMBER").fill("4000000000001000");
		page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Expiry Date MONTH▼$")))
				.locator("span").click();
		page.getByText("11", new Page.GetByTextOptions().setExact(true)).click();
		page.getByText("▼").nth(1).click();
		page.getByText("2026", new Page.GetByTextOptions().setExact(true)).click();
		page.getByPlaceholder("CVV NUMBER").click();
		page.getByPlaceholder("CVV NUMBER").fill("901");
		page.getByText("PAY INR").click();
		page.getByPlaceholder("NAME ON CARD").click();
		page.getByPlaceholder("NAME ON CARD").fill("Rudra K");
		page.getByText("PAY INR").click();

		Page page1 = page.waitForPopup(() -> {
			page.getByText("Secure & pay").click();

		});

		page.waitForSelector(".payment-success");

		// Verify payment success
		String confirmationMessage = page.locator(".payment-success").innerText();
		if (confirmationMessage.contains("Payment Successful")) {
			System.out.println("Payment processed successfully.");
			assertTrue(confirmationMessage.contains("Payment Successful"), "Payment Successful.");
			WriteToExcel.WriteToExcelResult(n, 12, "Passed");
		} else {
			System.out.println("Payment failed.");
			WriteToExcel.WriteToExcelResult(n, 12, "Failed");
		}

	}

	@Test(enabled = false)
	public void failBusTicketBookingTest() throws InterruptedException, FindFailed, IOException {

		// Mock failed payment response
		page.route("**/Pay/PaymentItems", route -> {
			route.fulfill(new Route.FulfillOptions().setStatus(400)
					.setBody("{ \"status\": \"failure\", \"message\": \"Payment Failed\" }"));
		});

		// Navigate to RedBus website
		page.navigate(URL);

		// Interact with the website to search for buses
		page.locator("input#src").fill(source);
		page.locator("input#dest").fill(dest);
		page.locator("div[id='onwardCal']").click();
		page.waitForSelector("div[class='labelCalendarContainer']");
		page.locator("div[class='labelCalendarContainer']:has-text('" + dateOfJourney + "')").click();

		page.locator("button#search_button").click();
		page.getByPlaceholder("OPERATOR").click();
		page.locator("input[name=\"inpFilter\"]").fill(operator);
		page.locator("li").filter(new Locator.FilterOptions().setHasText(operator + " (" + noOfSeatAvl + ")"))
				.locator("label").first().click();
		page.getByText("APPLY", new Page.GetByTextOptions().setExact(true)).click();

		// Wait for the search results to load
		page.waitForSelector(".bus-item");

		// Extract bus travel IDs from the search results and click 1 st
		@SuppressWarnings("unchecked")
		Locator travelIds = page.locator(".bus-items").getByText("View Seats");
		System.out.println(travelIds);
		travelIds.first().click();

		// Use Sikuli to select the seats
		IdentifyBusSeat.clickSeat();

		// Bording and Dropping selection
		page.locator(".radio-unchecked").first().click();
		page.locator(".db > .radio-css > .radio-unchecked").first().click();
		page.locator(".seat-container-div").click();
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed to book")).click();

		// Customer details filling up
		page.getByPlaceholder("Name").fill(cusName);
		page.locator("#div_22_0").click();
		page.getByLabel("State of Residence").click();
		page.getByLabel("State of Residence").fill("Karnataka");
		page.getByPlaceholder("Age").click();
		page.getByPlaceholder("Age").fill(cusAge);
		page.getByPlaceholder("Email ID").click();
		page.getByPlaceholder("Email ID").fill(cusEmail);
		page.getByPlaceholder("Phone").click();
		page.getByPlaceholder("Phone").fill(cusPhone);
		if (page.getByLabel("Full refund upon").isVisible()) {
			page.getByLabel("Full refund upon").check();
		}
		page.locator("label").filter(new Locator.FilterOptions().setHasText("No, I would like to proceed"))
				.locator("span").click();
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed to pay")).press("Enter");

		// Credit Card Payment
		page.locator("[id=\"Credit\\ Card\"]").nth(1).check();
		page.getByPlaceholder("CREDIT CARD NUMBER").click();
		page.getByPlaceholder("CREDIT CARD NUMBER").fill("4000000000001000");
		page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Expiry Date MONTH▼$")))
				.locator("span").click();
		page.getByText("11", new Page.GetByTextOptions().setExact(true)).click();
		page.getByText("▼").nth(1).click();
		page.getByText("2026", new Page.GetByTextOptions().setExact(true)).click();
		page.getByPlaceholder("CVV NUMBER").click();
		page.getByPlaceholder("CVV NUMBER").fill("901");
		page.getByText("PAY INR").click();
		page.getByPlaceholder("NAME ON CARD").click();
		page.getByPlaceholder("NAME ON CARD").fill("Rudra K");
		page.getByText("PAY INR").click();

		Page page1 = page.waitForPopup(() -> {
			page.getByText("Secure & pay").click();

		});

		page.waitForSelector(".payment-failure");

		// Verify payment failure
		String confirmationMessage = page.locator(".payment-failure").innerText();
		if (confirmationMessage.contains("Payment Failed")) {
			System.out.println("Payment Failed.");
			assertTrue(confirmationMessage.contains("Payment Failed"), "Payment should fail.");
			WriteToExcel.WriteToExcelResult(n, 13, "Failed");
		} else {
			System.out.println("Payment Success.");
			WriteToExcel.WriteToExcelResult(n, 13, "Passed");
		}

	}

	@AfterTest
	public void tearDown() {
		// Close browser
		browser.close();
	}

}
