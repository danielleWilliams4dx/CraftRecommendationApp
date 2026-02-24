package craftApplication;

import java.io.BufferedReader;
import java.io.FileReader; 
import java.io.IOException; 
import java.util.ArrayList;
import java.util.Scanner;

public class SavedCraftScreen implements Screen {
	
	private ArrayList<Craft> savedCrafts = new ArrayList<Craft>();
	

	String savedCraftActions = "Saved Craft Actions:\n"
			+ "- Type ‘V’ to view a craft\n"
	        + "- Type 'S' to return to Saved Crafts\n";
	
	public void disp() {
		
		loadSavedCrafts();
		
		System.out.println(navBar);
		System.out.println("Saved Crafts");
		System.out.println("_________\n");
		System.out.println("Additional supplies that you need are *starred*\n");
		
		printCrafts();
		
		System.out.println(navActions+"\n");
		System.out.println(savedCraftActions+"\n");
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
		
		System.out.println("Iinvalid input. Please try again.");
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
		for (int i = 0; i < savedCrafts.size(); i++) {
			System.out.println(savedCrafts.get(i).toStringWithIndex(i+1));
		}
	}
	
	private void viewCraftFlow() {
		if (savedCrafts.isEmpty()) {
			System.out.println("You have no saved crafts to view.");
			return;
		}
		
		Scanner kb = new Scanner(System.in);
		System.out.println("Please type the number of the craft you would like to view: ");
		String input = kb.nextLine().trim();
		
		try {
			int idx = Integer.parseInt(input);
			if (idx<1 || idx > savedCrafts.size()) {
				System.out.println("Invalid craft number. Please try again.");
				return;
			}
			System.out.println(savedCrafts.get(idx-1).toStringWithIndex(idx));
			System.out.println("Type 'S' to return to your saved crafts.");
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
			
		}
	 }

}
