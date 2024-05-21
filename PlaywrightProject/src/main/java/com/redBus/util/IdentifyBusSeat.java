package com.redBus.util;

import java.io.File;
import java.io.IOException;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class IdentifyBusSeat {

	public static void clickSeat() throws IOException {
		// Initialize Sikuli Screen object
		Screen screen = new Screen();

		// Define the path to the seat image

		String seatImagePath = getAbsolutePath("src/test/resources/TestData/available_seat.PNG");
		System.out.println(seatImagePath);

		Pattern seatPattern = new Pattern(seatImagePath);

		try {
			// Attempt to find and click the seat
			if (screen.exists(seatPattern) != null) {
				System.out.println("Bus seat found. Selecting the seat...");
				screen.click(seatPattern);
				System.out.println("Bus seat selected.");
			} else {
				System.out.println("Bus seat not found.");
			}
		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}

	public static String getAbsolutePath(String relativePath) {
		// Create a File object with the relative path
		File file = new File(relativePath);

		// Get the absolute path
		return file.getAbsolutePath();
	}

}
