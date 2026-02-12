package craftApplication;

import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		//create home screen and display it
		HomeScreen hs = new HomeScreen();
		hs.disp();
		//create other screens and put them in an array to pass easier
		InventoryScreen is = new InventoryScreen();
		CatalogScreen cs = new CatalogScreen();
		Screen[] screens = new Screen[3];
		screens[0] = hs;
		screens[1]= is;
		screens[2] = cs;
		//loop continuously until user ends program
		Boolean end = false;
		String input;
		//keep track of which screen you are viewing
		Screen currentScreen = hs;
		while (!end) {
			input = kb.next().toUpperCase();
			if (input.equals("X"))
				end = true;
			else
				currentScreen = currentScreen.actionSelect(input, screens);
		}
		kb.close();
	}
}
