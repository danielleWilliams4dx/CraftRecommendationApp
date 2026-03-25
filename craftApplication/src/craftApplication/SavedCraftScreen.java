package craftApplication;

import java.io.BufferedReader;
import java.io.FileReader; 
import java.io.IOException; 
import java.util.ArrayList;
import java.util.Scanner;

public class SavedCraftScreen implements Screen {
	
	private ArrayList<Craft> savedCrafts = new ArrayList<Craft>();
	private Inventory inv = new Inventory();
	

	String savedCraftActions = "Saved Craft Actions:\n"
			+ "- Type ‘V’ to view a craft\n";
	
	public void disp() {
		
		loadSavedCrafts();
		inv = new Inventory();
		
		System.out.println("\n");
		System.out.println(navBar);
		System.out.println("Saved Crafts");
		System.out.println("_________\n");
		
		printCrafts();
		
		System.out.println(navActions+"\n");
		System.out.println(savedCraftActions);
		System.out.println("Your Answer: ");
		
	}

	public Screen actionSelect(String input, Screen[] screens) {
		if (input.equals("H")) {
			screens[0].disp();
			return screens[0];
		}
		else if (input.equals("I")) {
			screens[1].disp();
			return screens[1];
		}
		else if (input.equals("C")) {
			screens[2].disp();
			return screens[2];
		}
		else if (input.equals("S")) {
			screens[3].disp();
			return screens[3];
		}
		
		else if (input.equals("V") ) {
			viewCraftFlow();
			this.disp();
			return screens[3];
			
		}
		
		System.out.println("\nInvalid input. Please try again.");
		this.disp();
		return screens[3];
		
	}
	
	private void loadSavedCrafts() {
		savedCrafts.clear();
		String filePath = "savedCrafts.csv";
		String line;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				savedCrafts.add(new Craft(line));
			}
		} catch (IOException e) {
			//if file foesn't exist , treating as empty saved list
		}
	}
	

	public void printCrafts() {
		if(savedCrafts.isEmpty()) {
			System.out.println("(You have no saved crafts yet.)\n");
			return;
		}
		
		System.out.println("Additional supplies that you need are *starred*\n");
		
		for (int i = 0; i < savedCrafts.size(); i++) {
			System.out.println(savedCrafts.get(i).toStringWithIndex(i+1, inv));
		}
	}
	
	private void viewCraftFlow() {
		if (savedCrafts.isEmpty()) {
			System.out.println("You have no saved crafts to view.");
			return;
		}
		
		Scanner kb = new Scanner(System.in);
		boolean success = false;
		System.out.println();
		
		//re-prompt until a valid craft number is entered
		while(!success) {
			System.out.println("Please type the number of the craft you would like to view: ");
			String input = kb.nextLine().trim();
			
			try {
				int idx = Integer.parseInt(input);
				
				if (idx < 1 || idx > savedCrafts.size()) {
					System.out.println("\nInvalid craft number. Please try again.");
					continue;
				}
				
				System.out.println("\n\n" + savedCrafts.get(idx-1).toStringWithIndex(idx, inv));
				success = true;
				
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input. Please enter a number.");
				
			}
		}
		
		boolean returnToSavedCrafts = false;
		System.out.println();
		
		while(!returnToSavedCrafts) {
			System.out.println("Type 'S' to return to your saved crafts.");
			String input = kb.nextLine().trim();
			
			if(input.toUpperCase().equals("S")) {
				returnToSavedCrafts = true;
			} else {
				System.out.println("\nInvalid input.");
			}
		}		
	 }
}
