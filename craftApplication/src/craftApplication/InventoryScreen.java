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

// NEW EDIT : "E 1,2" or bare "E" - edit one or more craft supplies
// non-editable items are blocked with a clear message 
// fields are pre-filled with current values; 
// typing 'x' shows close warning; second 'x' cancels without saving 
// 'SUBMIT' on the final page applies all changes 

//NEW DELETE: "D 1,2" or "D" - delete one or more craft supplies
// Confirmation message lists the items and warns the action can't be undone 
// DELETE confirms; X cancels 


public class InventoryScreen implements Screen{
	
	Inventory inv = new Inventory();
	
	//Active filters (interpreted as selected "categories" from the available list)
	private ArrayList<String> activeFilters = new ArrayList<String>();
	
	//actions specific to inventory to be displayed
	String invActions = "Inventory Actions:\n"
			+ "- Type ‘F’ to filter your inventory\n"
			+ "- Type ‘R’ to generate craft recommendations\n"
			+ "- Type ‘E’ to edit craft supplies\n"
			+ "- Type ‘D’ to delete craft supplies\n";;
	
	
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
		
		// EDIT: "E", "E 1,2", or "E1,2"
		//"E" alone prompts for numbers interactively
		//"E 1,2" or "E1,2" passes numbers directly
		if (input.equals("E") || input.startsWith("E ") || (input.length() > 1 && input.charAt(0) == 'E')) {
			String rest = input.equals("E") ? "" : input.substring(1).trim();
			if (rest.isEmpty()) {
				rest = promptForItemNumbers("edit");
			}
			if (rest != null && !rest.isEmpty()) {
				runEditFlow(rest);
			}
			this.disp();
			return screens[1];
		}
		
		//DELETE: "D", "D 1,2" or "D1,2" 
		//The filter menu handles its own D input internally before returning here, 
		// so a bare D at the main inv prompt always means delete
		if (input.equals("D") || input.startsWith("D ") || (input.length() > 1 && input.charAt(0) == 'D')) {
			String rest = input.equals("D") ? "" : input.substring(1).trim();
			if (rest.isEmpty()) {
				rest = promptForItemNumbers("delete");
			}
			if (rest != null && !rest.isEmpty()) {
				runDeleteFlow(rest);
			}
			this.disp();
			return screens[1];
		}
		
		
		//Invalid command handling 
		System.out.println("\nInvalid input. Please try again.");
		this.disp();
		return screens[1];
	}
	
	// EDIT FLOW
	private void runEditFlow(String selectionInput) {
		inv = new Inventory();
		ArrayList<CraftSupply> visibleItems = inv.getVisibleItems(activeFilters);
		
		if (visibleItems.isEmpty()) {
			System.out.println("\nYour inventory is empty.");
			return;
		}
		
		ArrayList<CraftSupply> selectedItems = parseItemSelection(selectionInput, visibleItems);
		if (selectedItems == null) return;
		
		//block if any selected item has no editable attributes 
		ArrayList<CraftSupply> nonEditable = getNonEditableItems(selectedItems);
		if (!nonEditable.isEmpty()) {
			if (nonEditable.size() == 1) {
				System.out.println("\n" + nonEditable.get(0).getName() + " does not have any editable attributes.");
			} else {
				StringBuilder msg = new StringBuilder("\n");
				if(nonEditable.size() == 2) {
					msg.append(nonEditable.get(0).getName());
					msg.append(" and ");
					msg.append(nonEditable.get(1).getName());
				}else {
					for (int i = 0; i < nonEditable.size(); i++) {
						if (i > 0 && i == nonEditable.size() - 1) msg.append(", and ");
						else if (i > 0) msg.append(", ");
						msg.append(nonEditable.get(i).getName());
					}
				}
				msg.append(" do not have any editable attributes.");
				System.out.println(msg.toString());
			}
			System.out.println("Please deselect the non-editable items and try again");
			return;
		}
		
		Scanner kb = new Scanner(System.in);
		ArrayList<CraftSupply> editedItems = new ArrayList<CraftSupply>();
		
		for (int page = 0; page < selectedItems.size(); page++ ) {
			CraftSupply original = selectedItems.get(page);
			
			System.out.println("\n--- Editing: " + original.getName() 
			    + " (" + (page + 1) + " of " + selectedItems.size() + ") ---");
			System.out.println("(Press Enter to keep the current value. Type 'X' to cancel.)\n");
			
			CraftSupply catalogEntry = loadCatalogEntry(original.getName());
			boolean needsColor = catalogEntry != null && catalogEntry.needsColor();
			boolean needsQuantity = catalogEntry != null && catalogEntry.needsQuantity();
			boolean needsSize = catalogEntry != null && catalogEntry.needsSize();
			
			String newColor = original.getColor();
			String newQuantity = original.getQuantity();
			String newSize = original.getSize();
			
			if (needsColor) {
				String result = promptEditField(kb, "Color", original.getColor());
				if (result == null) return; 
				if (!result.isEmpty()) newColor = result;
			}
			
			if (needsQuantity) {
				String result = promptEditField(kb, "Quantity", original.getQuantity());
				if (result == null) return; 
				if (!result.isEmpty()) newQuantity = result;
			}
			
			if (needsSize) {
				String result = promptEditField(kb, "Size", original.getSize());
				if (result == null) return; 
				if (!result.isEmpty()) newSize = result;
			}
			
			editedItems.add(new CraftSupply(
					original.getName(), original.getType(),
					newColor, newQuantity, newSize));
			
			//Between pages: ask user to continue or cancel
			if (page < selectedItems.size() - 1) {
				boolean advanced = false;
				boolean warnedClose = false;
				System.out.println("\nType 'NEXT' to continue to the next item, or 'X' to cancel.\n\nYour Answer:");
				
				while (!advanced) {
					String nav = kb.nextLine().trim().toUpperCase();
					if (nav.equals("NEXT")) {
						advanced = true;
					} else if (nav.equals("X")) {
						if (!warnedClose) {
							System.out.println("\n** IF YOU CLOSE THE MENU, ANY CHANGES WILL NOT BE SAVED.");
							System.out.println("Type 'X' again to confirm, or 'NEXT' to continue.\n\nYour Answer:");
							warnedClose = true;
						} else {
							System.out.println("\nEdit cancelled. No changes were saved.");
							return;
						}
					} else {
						System.out.println("Invalid input. Type 'NEXT' to continue or 'X' to cancel.");
					}
;				}
			}
		}
		
		//Final submit page
		System.out.println("\n---Review & Submit ___");
		System.out.println("Type 'SUBMIT' to save all changes, or 'X' to cancel.\n\nYour Answer:");
		boolean submitted = false;
		boolean warnedClose = false;
		
		while (!submitted) {
			String ans = kb.nextLine().trim().toUpperCase();
			if (ans.equals("SUBMIT")) {
				submitted = true;
			} else if (ans.equals("X")) {
				if (!warnedClose) {
					System.out.println("\n** IF YOU CLOSE THE MENU, ANY CHANGES WILL NOT BE SAVED.");
					System.out.println("Type 'X' again to confirm, or 'SUBMIT' to save.\n\nYour Answer: ");
					warnedClose = true;
				} else {
					System.out.println("\nEdit cancelled. No changes were saved.");
					return;
				}
			} else {
				System.out.println("Invalid input. Type 'SUBMIT' to save or 'X' to cancel.");
			}
		}
		
		//Apply all changes 
		int updatedCount = 0;
		for (int i = 0; i < selectedItems.size(); i++) {
			if (inv.updateItem(selectedItems.get(i), editedItems.get(i))) {
				updatedCount++;
			}
		}
		
		if (updatedCount ==1) {
			System.out.println("\n1 craft supply was successfully updated.");
		} else {
			System.out.println("\n" + updatedCount  + " craft supplies were successfully updated.");
		}
	}
	
	//Delete Flow
	private void runDeleteFlow(String selectionInput) {
		inv = new Inventory();
		ArrayList<CraftSupply> visibleItems = inv.getVisibleItems(activeFilters);
		
		if (visibleItems.isEmpty()) {
			System.out.println("\nYour inventory is empty.");
			return;
		}
		
		ArrayList<CraftSupply> selectedItems = parseItemSelection(selectionInput, visibleItems);
		if (selectedItems == null) return;
		
		StringBuilder nameList = new StringBuilder();
		if(selectedItems.size() == 2) {
			nameList.append(selectedItems.get(0).getName());
			nameList.append(" and ");
			nameList.append(selectedItems.get(1).getName());
		}else {
			for (int i = 0; i < selectedItems.size(); i++) {
				if (i > 0 && i == selectedItems.size() - 1) nameList.append(", and ");
				else if (i > 0) nameList.append(", ");
				nameList.append(selectedItems.get(i).getName());
			}
		}
		
		System.out.println("\nAre you sure that you would like to delete your "
				+ nameList + "? This action cannot be undone.");
		System.out.println("Type 'DELETE' to confirm, or 'X' to cancel \n\nYour Answer: ");
		
		Scanner kb = new Scanner(System.in);
		boolean done = false;
		
		while (!done) {
			String ans = kb.nextLine().trim().toUpperCase();
			if (ans.equals("DELETE")) {
				inv.deleteItems(selectedItems);
				int count = selectedItems.size();
				System.out.println(count == 1 ? "\n1 craft supply was deleted." 
						: "\n" + count + " craft supplies were deleted.");
				done = true;
			} else if (ans.equals("X")) {
				System.out.println("\nDeletion cancelled.");
				done = true;
			} else {
				System.out.println("Invalid input. Type 'DELETE' to confirm or 'X' to cancel.\n\nYour Answer:");
			}
		}
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
				System.out.println("\nInvalid selection " + token);
					
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
	
	//HELPERS
	private String promptForItemNumbers(String action) {
		inv = new Inventory();
		ArrayList<CraftSupply> visibleItems = inv.getVisibleItems(activeFilters);
		
		if (visibleItems.isEmpty()) {
			System.out.println("\nYour inventory is empty.");
			return null;
		}
		
		System.out.println("\nEnter the item number(s) you want to " + action +
				" (comma-seperated), or 'X' to cancel.");
		System.out.println("\nYour Answer: ");
		
		Scanner kb = new Scanner(System.in);
		String answer = kb.nextLine().trim().toUpperCase();
		
		if (answer.equals("X") || answer.isEmpty()) return null;
		return answer;
	}
	
	//Parses a comma-seperated string of 1-based indices into CraftSupply objects 
	//Returns null and print an error if any token is out of range or non-numberic 
	private ArrayList<CraftSupply> parseItemSelection(String input, ArrayList<CraftSupply> visibleItems) {
		
		ArrayList<CraftSupply> selected = new ArrayList<CraftSupply>();
		HashSet<Integer> seen = new HashSet<Integer>();
		
		for (String token : input.split(",")) {
			String t = token.trim();
			if(t.isEmpty()) continue;
			
			try {
				int idx = Integer.parseInt(t);
				if (idx < 1 || idx > visibleItems.size()) {
					System.out.println("\nInvalid item number " + t + 
							". Please use numbers between 1 and " + visibleItems.size() + ".");
					return null;
				}
				if (seen.add(idx)) {
					selected.add(visibleItems.get(idx -1));
				}
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input: \"" + t + "\". Please enter item number only.");
				return null;
			}
		}
		
		if(selected.isEmpty()) {
			System.out.println("\nNo vallid items selected.");
			return null;
		}
		
		return selected;
	}
	
	//Returns items that have no editable additional attributes
	private ArrayList<CraftSupply> getNonEditableItems(ArrayList<CraftSupply> items) {
		ArrayList<CraftSupply> nonEditable =  new ArrayList<CraftSupply>();
		
		for (CraftSupply item : items) {
			CraftSupply catalogEntry = loadCatalogEntry(item.getName());
			if(catalogEntry == null
					|| (!catalogEntry.needsColor() 
							&& !catalogEntry.needsQuantity()
							&& !catalogEntry.needsSize())) {
				nonEditable.add(item);
			}
		}
		return nonEditable;
	}
	
	private CraftSupply loadCatalogEntry(String itemName ) {
		for (CraftSupply cs : new CatalogScreen().getItems()) {
			if (cs.getName().equalsIgnoreCase(itemName)) return cs;
		}
		return null;
	}
	
	//Prompts for a single editable field , 
	// Enter = keep current value (returns "")
	//First 'X' = close warning, second 'x' = cancel
	private String promptEditField(Scanner kb, String fieldName, String currentValue) {
		boolean warnedClose = false;
		
		while (true) {
			System.out.print(fieldName + " [" 
					+ (currentValue.isEmpty() ? "none" : currentValue) + "]: ");
			String input = kb.nextLine().trim();
			
			if (input.equalsIgnoreCase("X") ) {
				if (!warnedClose) {
					System.out.println("\n** IF YOU CLOSE THE MENU, ANY CHANGES WILL NOT BE SAVED.");
					System.out.println("Type 'X' again to confirm, or enter a value to continue.\n");
					warnedClose = true;
				} else {
					System.out.println("\nEdit cancelled. No changes were saved.");
					return null;
				}
				continue;
			}
			return input;
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
                
                    







