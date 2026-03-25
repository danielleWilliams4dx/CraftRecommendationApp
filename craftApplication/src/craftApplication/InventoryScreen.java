package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	Inventory inv = new Inventory();
	
	//Active filters (interpreted as selected "categories" from the available list)
	private ArrayList<String> activeFilters = new ArrayList<String>();
	
	//actions specific to inventory to be displayed
	String invActions = "Inventory Actions:\n"
			+ "- Type ‘F’ to filter your inventory\n"
			+ "- Type ‘R’ to generate craft recommendations\n";
	
	public InventoryScreen() {}
	
	//display function
	public void disp() {
		//always refresh inventory from file before displaying
		inv = new Inventory();
		
		System.out.println("\n");
		System.out.println(navBar);
		System.out.println("Inventory");
		System.out.println("_________\n");
		
		if (activeFilters.isEmpty()) {
			System.out.println("Filter (F)\n");
		} else {
			System.out.println("Filter (F) [Active: " + String.join(", ", activeFilters) + "]\n");
		}
			
		inv.printItems(activeFilters);
		System.out.println("\n"+navActions+"\n");
		System.out.println(invActions);
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
			runRecommendationFlow();
			//after recommendation flow, always return to inv display
			this.disp();
			return screens[1];
		}
		
		//Invalid command handling 
		System.out.println("\nInvalid input. Please try again.");
		this.disp();
		return screens[1];
	}
		
	//Inventory filter menu 
	private void runFilterMenu() {			
			
		Scanner kb = new Scanner(System.in);
		System.out.println("\n\nInventory Filter Menu");
		System.out.println("_____________________\n");

		//filters are based on all CraftSupply types
		ArrayList<String> filters = new ArrayList<String>(
				Arrays.asList("Adhesives", "Drawing", "Jewelry", "Painting", "Paper", "Sewing", "Tools"));
			
		System.out.println("   Filter By:");
		//show selectable filter options (1-based)
		for (int i = 0; i < filters.size(); i++) {
			System.out.println("   " + (i + 1 ) + ") " + filters.get(i)); 
		}
			
		System.out.println("\n\nFilter Actions:");
		System.out.println("- Select one or more categories (comma-separated)");
		System.out.println("- Type 'D' to clear filters");
		System.out.println("- Type 'I' to return to Inventory without changes\n");
			
		System.out.println("Your Answer: ");
			
		//input fix: use nextLine so we can accept comma-separated inputs with spaces 
		String ans = kb.nextLine().trim().toUpperCase();
			
		if (ans.equals("I")) {
			return;
		}
			
		if (ans.equals("D")) {
			activeFilters.clear();
			System.out.println("\nAll filters cleared.");
			return;
		}
			
		//parse selections (comma-separated list of numbers)
		ArrayList<String> newFilters = new ArrayList<String>();
		HashSet<Integer> seen = new HashSet<Integer>(); // preventing duplicates 
			
		String[] parts = ans.split(",");
		for (String p : parts) {
			String token = p.trim();
			if(token.isEmpty())	continue;
				
			try {
				int idx = Integer.parseInt(token);
					
				//error handling: bounds check 
				if (idx < 1 || idx > filters.size()) { 
					System.out.println("\nInvalid selection: " + token + "\n");
					continue;
				}
					
				if (seen.add(idx) ) {
					newFilters.add(filters.get(idx - 1)); //convert to 0-b
				}
					
			} catch (NumberFormatException e) {
				//ERR handling: non-numeric token 
				System.out.println("Invalid selection " + token);
					
			}
		}
			
		//replace old filters with new selection
		activeFilters = newFilters;
		
	}
	
		
	// Recommendation Flow
	// Fixes: 
	//1. Invalid subset like 999 is rejected before recs are generated 
	// The recommendation action menu now only accepts V, I, or a valid number list
	// Inputs like S or E are no longer treated as a craft-number save input
		
	private void runRecommendationFlow() {
		Scanner kb = new Scanner(System.in);
			
		inv = new Inventory();
		
		ArrayList<String> visibleItemNames = inv.getVisibleItemNames(activeFilters);
		String answer = "";
			
		boolean validSelection = false;
		while (!validSelection) {	
			System.out.println("\nRecommendation Generation Actions:\n"
					+ "- Type a comma separated list of the numbers of certain craft supplies to generate specific recommendations\n"
					+ "- Type ‘E’ to generate craft recommendations for your entire inventory \n\n"
			        + "Your Answer: ");
			
			//input fix: nextLine + trim, supports '1,2,3' and avoids newLine skipping 
			answer = kb.nextLine().trim().toUpperCase();
			
			if (answer.equals("E")) {
			    validSelection = true;
			}
		    else if (isValidNumberList(answer, visibleItemNames.size())) {
			    validSelection = true;
			}
			else {
			    System.out.println("\nInvalid craft supply selection. Please try again.\n");
		    }
			
		}
			
		//pass the visible inventory order so 1,2 matches what the user sees on the screen
		Recommender rec = new Recommender(answer, this.inv, visibleItemNames);
			
		//if no recommendations exist, let the user return cleanly 
		if (rec.recs.isEmpty()) {
			boolean returnToInventory = false;
			System.out.println("\nType 'I' to return to your Inventory.");
			while (!returnToInventory) {
				String inputBack = kb.nextLine().trim().toUpperCase();
				if (inputBack.equals("I")) {
					returnToInventory = true;
				} else {
					System.out.println("Invalid Input.");					
				}
			}
			System.out.println("\n");
			return;
		}
			
		runRecommendationActionMenu(rec, kb);
	}
			
	//Handles actions after recommendations are shown
	//Only V, I, or a valid number list are accepted here 
			
	private void runRecommendationActionMenu(Recommender rec, Scanner kb) {
		boolean done = false;
				
		while (!done) {
			System.out.println("\nCraft Recommendation Actions:\n"
					+ "- Type ‘V’ to view a craft\n"
					+ "- Type a comma separated list of the numbers of crafts that you would like to save\n"
					+ "- Type ‘I’ to return to your inventory\n"
					+ "\nYour Answer: ");
					
			String answer = kb.nextLine().trim().toUpperCase();
					
			if (answer.equals("V")) {
				viewRecommendation(rec, kb);
				done = true;
			}
			else if (answer.equals("I")) {
				done = true;
			}
			else if (isValidNumberList(answer, rec.recs.size())) {
				saveRecommendations(answer, rec);
				done = true;
			}
			else {
				System.out.println("\nInvalid input. Please try again.\n");
			}
		}
	}
		
	//let the user view one rec by number 
	//use the same item set that was used to generate res
	//so subset sec mode starts missing materials correctly 
	private void viewRecommendation(Recommender rec, Scanner kb) {
		boolean success = false;
		System.out.println();
				
		//re-prompt until a valid craft number is entered
		while(!success) {
			System.out.println("Please type the number of the craft you would like to view: ");
			String input = kb.nextLine().trim();
					
			try {
				int idx = Integer.parseInt(input);
				
				if (idx <1 || idx > rec.recs.size()) {
					System.out.println("\nInvalid craft number. Please try again.");
					continue;
				}
					
				System.out.println("\n\n" + rec.recs.get(idx - 1).toStringWithIndex(idx, inv));
				success = true;
						
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input. Please enter a number.");
			}
		}
				
		boolean returnToInventory = false;
		System.out.println();
				
		while(!returnToInventory) {
			System.out.println("Type 'I' to return to your Inventory.");
			String input = kb.nextLine().trim().toUpperCase();
					
			if(input.equals("I")) {
				returnToInventory = true;
			} else {
				System.out.println("\nInvalid input.");
			}
		}
				
		System.out.println("\n");
			
	}
		
	//saves one or more selected recs 
	//duplicate save numbers like 1,1,1 are ignored
	private void saveRecommendations(String answer, Recommender rec) {
		//SAVE-01/SAVE-02: save one or more crafts 
		//Fix (ERR-04, ERR-05, ERR-06): validate tokens, bounds, prevents duplicates 
		HashSet<Integer> seen = new HashSet<Integer>();
		String[] nums = answer.split(",");
		int savedCount = 0;
				
		for (String number : nums) {
			String token = number.trim();
			if (token.isEmpty()) continue;
					
			try {
				int idx = Integer.parseInt(token);
						
				if (idx < 1 || idx > rec.recs.size()) {
					System.out.println("\nInvalid craft number: " + token);
					continue;
				}
						
				if (seen.add(idx)) {
					rec.recs.get(idx - 1).save();
					savedCount++;
				}
						
			} catch (NumberFormatException e) {
				//non-numeric token handling 
				System.out.println("\nInvalid craft number: " + token);					
			}
		}
			
		//confirmation message 
		if (savedCount == 1) {
			System.out.println("\nSaved 1 craft.");
		}
		else if (savedCount > 1) {
			System.out.println("\nSaved " + savedCount + " crafts.");
		}
		else {
			System.out.println("\nNo crafts were saved.");
		}
	}
		
		//validate
	private boolean isValidNumberList(String input, int maxIndex) {
		if (input == null || input.trim().isEmpty()) {
			return false;
		}
		
		String[] parts = input.split(",");
		boolean foundAtLeastOneNumber = false;
		
		for (String part: parts) {
			String token = part.trim();
			
			if (token.isEmpty()) {
				continue;
			}
			
			try {
				int idx = Integer.parseInt(token);
				
				if (idx < 1 || idx > maxIndex) {
					return false;
				}
				
				foundAtLeastOneNumber = true;
				
			} catch (NumberFormatException e) {
				return false;
			}
		}
		
		return foundAtLeastOneNumber;
	}
}
                
                    








