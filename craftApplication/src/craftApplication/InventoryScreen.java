package craftApplication;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;
import java.util.HashSet;

//Inventory fixes: Bug-001: Fixed crash after viewing a craft when user enters "V" again 
//ERR-04 / ERR-05: Added bounds checks + numeric validation to prevent IndexOutOfBoundsException and NumberFormatException
//BUG-003: Implemented Inventory filtering functionality 
// - filter menu
// - supports multiple selections (comma-seperated)
// - replace old filters with new selection
// -  'D' clears filters 
// - 'I'returns without changes 

public class InventoryScreen implements Screen{
	//inventory has an ArrayList of items
	ArrayList<String> items = new ArrayList<String>();
	
	//Active filters (interpreted as selected "categories" from the available list)
	private ArrayList<String> activeFilters = new ArrayList<String>();
	
	//actions specific to inventory to be displayed
	String invActions = "Inventory Actions:\n"
			+ "- Type ‘F’ to filter your inventory\n"
			+ "- Type ‘R’ to generate craft recommendations\n";
	
	public InventoryScreen() {
		genItems();
		Collections.sort(this.items);
	}
	
	//display function
	public void disp() {
		System.out.println(navBar);
		System.out.println("Inventory");
		System.out.println("_________\n");
		
		if (activeFilters.isEmpty()) {
			System.out.println("Filter (F)\n");
		} else {
			System.out.println("Filter (F) [Active: " + String.join(", ", activeFilters) + "]\n");
		}
			
		printItems();
		System.out.println("\n"+navActions+"\n");
		System.out.println(invActions+"\n");
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
		else if (input.equals("F")) {
			runFilterMenu();
			this.disp();
			return screens[1];
		}
		
		
		else if (input.equals("R")) {
			runRecommendationFlow(screens);
			//after recommendation flow, always return to inv display
			screens[1].disp();
			return screens[1];
			
		}
		
		//Invalid command handling 
		System.out.println("Invalid input. Please try again.");
		screens[1].disp();
		return screens[1];
	}
		//Inventory filter menu 
		private void runFilterMenu() {
			
			Scanner kb = new Scanner(System.in);
			System.out.println("\nInventory Filter Menu");
			System.out.println("_____________________\n");
			System.out.println("Select one or more categories (comma-separated)");
			System.out.println("Type 'D' to clear filters.");
			System.out.println("Type 'I' to return to Inventory without changes.\n");
			
			//show selectable filter options (1-based)
			for (int i = 0; i < items.size(); i++) {
				System.out.println((i + 1 ) + ") " + items.get(i)); 
			}
			
			System.out.println("\nYour Answer: ");
			
			//input fix: use nextLine so we can accpt comma-separated inputs with spaces 
			String ans = kb.nextLine().trim().toUpperCase();
			
			if (ans.equals("I")) {
				return;
			}
			
			if (ans.equals("D")) {
				activeFilters.clear();
				return;
			}
			
			//parse selections (comma-separated list of numbers)
			ArrayList<String> newFilters = new ArrayList<String>();
			HashSet<Integer> seen = new HashSet<Integer>(); // preventing duplicates 
			
			String[] parts = ans.split(",");
			for (String p : parts) {
				String token = p.trim();
				if(token.isEmpty())
					continue;
				
				try {
					int idx = Integer.parseInt(token);
					
					//error handling: bounds check 
					if (idx < 1 || idx > items.size()) { 
						System.out.println("Invalid selection: " + token);
						continue;
					}
					
					if (seen.add(idx) ) {
						newFilters.add(items.get(idx - 1)); //convert to 0-b
					}
					
				} catch (NumberFormatException e) {
					//ERR handling: non-numeric token 
					System.out.println("Invalid selection " + token);
					
				}
			}
			
			//replace old filters with new selection
			activeFilters = newFilters;
		}
		
		
		private void runRecommendationFlow(Screen[] screens) {
			
			Scanner kb = new Scanner(System.in);
			
			System.out.println("Recommendation Generation Actions:\n"
					+ "- Type a comma separated list of the numbers of certain craft supplies to generate specific recommendations\n"
					+ "- Type‘E’ to generate craft recommendations for your entire inventory \n");
			
			//input fix: nextLine + trim, supports '1,2,3' and avoids newLine skipping 
			String answer = kb.nextLine().trim().toUpperCase();
			
			//generate recommendations
			Recommender rec = new Recommender(answer, this.items, screens);
			
			System.out.println("Craft Recommendation Actions:\n"
					+ "- Type ‘V’ to view a craft\n"
					+ "- Type a comma separated list of the numbers of crafts that you would like to save\n"
					+ "- Type ‘I’ to return to your inventory");
			
			answer = kb.nextLine().trim().toUpperCase();
			
			if (answer.equals("V")) {
				
				System.out.println("Please type the number of the craft you would like to view: ");
				String numLine = kb.nextLine().trim();
				
				try {
					int response = Integer.parseInt(numLine);
					
					if (response < 1 || response > rec.recs.size()) {
						System.out.println("Invalid craft number. Please try again.");
						return;
					}
					
					System.out.println(rec.recs.get(response - 1).toStringWithIndex(response));
					
				} catch (NumberFormatException e) {
					 
					 //non-numeric craft should not crash 
					 System.out.println("Invalid input. Please enter a number.");
				}
				
				return;
				
			} else if (answer.equals("I")) {
				
				return;
				
			} else {
				
				//SAVE-01/SAVE-02: save one or more crafts 
				//Fix (ERR-04, ERR-05, ERR-06): validate tokens, bounds, prevents duplicates 
				HashSet<Integer> seen = new HashSet<Integer>();
				String[] nums = answer.split(",");
				int savedCount = 0;
				
				for (String number: nums) {
					String token = number.trim();
					if (token.isEmpty())
						continue;
					
					try {
						int idx = Integer.parseInt(token);
						
						if (idx < 1 || idx > rec.recs.size()) {
							System.out.println("Invalid craft number: " + token);
							continue;
						}
						
						if (seen.add(idx) ) {
							rec.recs.get(idx - 1).save();
							savedCount++;
						}
						
					} catch (NumberFormatException e) {
						//non-numeric token handling 
						System.out.println("Invalid craft number: " + token);
					}
				}
				
				//confirmation message 
				if (savedCount == 1) 
					System.out.println("Saved 1 craft.");
				else if (savedCount > 1)
					System.out.println("Saved " + savedCount + " crafts.");
				else 
					System.out.println("No crafts were saved.");
			}
		}
		
	
	
	//prints contents of inventory
	private void printItems() {
		for (String item:this.items){
			if (activeFilters.isEmpty() || activeFilters.contains(item)) {
				System.out.println(item);
			}
		}
		
	}
	
	
	//generates the ArrayList for the inventory, code modified from: https://medium.com/@zakariafarih142/mastering-csv-parsing-in-java-comprehensive-methods-and-best-practices-a3b8d0514edf
	private void genItems() {
		String filePath = "inventory.csv";
        String line;
        String delimiter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (String value : values) {
                    String cleaned = value.trim();
                    if (!cleaned.isEmpty())
                    	this.items.add(cleaned);
                }
            }
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        }
	}
}
                
                    








