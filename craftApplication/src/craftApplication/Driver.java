package craftApplication;

import java.util.Scanner;
import java.io.File;

public class Driver {
	
	public static void main(String[] args) {
		
		//debugger helper: csv paths are resolved relative to the folder 
		System.out.println("CWD(working directory): " + new File(".").getAbsolutePath());
		System.out.println();
		
		
		
		//create home screen 
		HomeScreen hs = new HomeScreen();
		//create other screens and put them in an array to pass easier
		InventoryScreen is = new InventoryScreen();
		CatalogScreen cs = new CatalogScreen();
		SavedCraftScreen scs = new SavedCraftScreen();
		Screen[] screens = new Screen[4];
		screens[0] = hs;
		screens[1]= is;
		screens[2] = cs;
		screens[3] = scs;
		
		Screen currentScreen = hs;
		currentScreen.disp();
		
		Scanner kb = new Scanner(System.in);
		//loop continuously until user ends program
		boolean end = false;

		while (!end) {
			
			//read full line so inputs like "1, 2, 3" still work 
			String input = kb.nextLine().trim().toUpperCase();
			
			//Empty input handling
			if (input.isEmpty()) {
				System.out.println("Invalid input. Please try again.\n");
				currentScreen.disp();
				continue;
			}
			
			if (input.equals("X")) {
				end = true;
				continue;
			}
			
			currentScreen = currentScreen.actionSelect(input, screens);
			
		}
		
		System.out.println("Exiting application. Goodbye!");
		kb.close();
	}
}
