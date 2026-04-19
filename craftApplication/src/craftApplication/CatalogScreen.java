package craftApplication;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

//Catalog Changes: 
// Close-warning: Closing the 'Additional info' prompts once; second 'X' exits without saving
//Required-field: Empty required field prints ""** [FIELD NAME] IS A REQUIRED FIELD"
// Duplicate-add: Adding a supply that;s already in inv. increments its quantity 
// Partial-add: if the user is in the middle of adding multiple items and cancels out (by pressing X twice) before finishing,
// any items that were already successfully added before they cancelled are kept
// they don't get rolled back. Then a message prints summarizing what made it in and what didn't.

public class CatalogScreen implements Screen {
	
    //catalog stores supply items, not crafts
	ArrayList<CraftSupply> catalogItems = new ArrayList<CraftSupply>();
	
	//Active filters 
	private ArrayList<String> activeFilters = new ArrayList<String>();
		
	//actions specific to catalog to be displayed
	String catActions = "Catalog Actions:\n"
			+ "- Type ‘F’ to filter the catalog\n"
			+ "- Type a comma separated list of craft supply item numbers to \n"
			+ "  add supplies to your inventory";
	
	//constructor makes the catalog
	public CatalogScreen(){
		//THIS FUNCTION MUST BE ALTERED
		genCatalogItems();
		sortCatalog();
	}
	
	//display function
	public void disp() {
		System.out.println("\n");
		System.out.println(navBar);
		System.out.println("Catalog");
		System.out.println("_______\n");
		
		if (activeFilters.isEmpty()) {
			System.out.println("Filter (F)\n");
		} else {
			System.out.println("Filter (F) | " + String.join(", ", activeFilters) + "\n");
		}
		
		printCat();
		System.out.println("\n" + navActions+"\n");
		System.out.println(catActions+ "\n");
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
			return screens[2];
		}
		
		//Any other input: treating as catalog supply selection
		addSelectedSupplies(input);
        this.disp();
		return screens[2];
	}
	
	private void sortCatalog() {
		Collections.sort(catalogItems, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
	}
	
	
	//Filter Flow: 
	//- choose category numbers
	//- D clears all the filters 
	// - C returns without changing filters
	private void runFilterMenu() {
		Scanner kb = new Scanner(System.in);
		
		//Building a list of unique "categories" from the catalog 
		ArrayList<String> categories = getCatalogCategories();
		
		System.out.println("\n");
		System.out.println(navBar);
		System.out.println("Catalog");
		System.out.println("_______");
		System.out.println("Filter by:\n");
		
		//show selectable filter options (1-based)
		for (int i = 0; i < categories.size(); i++) {
			System.out.println("   " + (i + 1 ) + ") " + categories.get(i)); 
		}
		
		System.out.println("\n" + navActions + "\n");
		System.out.println("Filter Actions:");
		System.out.println("- Type a comma separated list of the numbers of your desired filters");
		System.out.println("- Type 'D' to delete all filters");
		System.out.println("- Type 'C' to return to the catalog\n");
		System.out.println("Your Answer: ");
		
		String ans = kb.nextLine().trim().toUpperCase();
		
		if(ans.equals("C")) {
			return;
		}
		
		if (ans.equals("D")) {
			activeFilters.clear(); 
			return;
		}
		
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
				if (idx < 1 || idx > categories.size()) { 
					System.out.println("\nInvalid selection: " + token);
					continue;
				}
				
				if (seen.add(idx) ) {
					newFilters.add(categories.get(idx - 1)); //convert to 0-b
				}
				
			} catch (NumberFormatException e) {
				//ERR handling: non-numeric token 
				System.out.println("\nInvalid selection " + token);
				
			}
		}
		
		//replace old filters with new selection
		activeFilters = newFilters;
	}
	
	//Builds the category list for the filter menu
	private ArrayList<String> getCatalogCategories() {
		HashSet<String> set = new HashSet<String>();
		for (CraftSupply c : catalogItems) {
			//Craft Types as the category for filtering
			if (c.getType() != null && !c.getType().isEmpty()) {
				set.add(c.getType());
			}
		}
		ArrayList<String> out = new ArrayList<String>(set);
		Collections.sort(out);
		return out;
	}
	
	private ArrayList<CraftSupply> getFilteredCatalog() {
		if (activeFilters.isEmpty()) return catalogItems;
		
		ArrayList<CraftSupply> filtered = new ArrayList<CraftSupply>();
		for (CraftSupply c : catalogItems) {
			if (activeFilters.contains(c.getType())) {
				filtered.add(c);
			}
		}
		return filtered;
	}
	
	

	//Add-to-ionventory flow:
	//New behaviours: 
	// 1. Close warningL typing 'X' during additional-info prompts warns once, 
	// second 'X' exits without saving the item (items that were processed are kept)
	// 2. Empty submission prints "** [FIELD] IS A REQUIRED FIELD."
	// 3. If the added item (name + attr) is already in inv., it's quantity is incremented by 1
	// 4. If the user exits mid-flow, already added items are kept and a summary is printed

	private void addSelectedSupplies(String input) {
		Scanner kb = new Scanner(System.in);
		
		//refresh inv before checking duplicates 
		new Inventory();
		
		ArrayList<CraftSupply> filtered = getFilteredCatalog();
		HashSet<Integer> seen = new HashSet<Integer>();
		
		String[] nums = input.split(",");
		
		//Track:
		// addedItems - names of supplies that were added
		// skippedItems - names that couldn't be added
		
		ArrayList<String> addedItems = new ArrayList<String>();
		ArrayList<String> skippedItems = new ArrayList<String>();
		
		boolean userCancelledMidFlow = false;
		
		for (String number: nums) {
			if (userCancelledMidFlow) break;
			
			String token = number.trim();
			if (token.isEmpty()) continue;

			
			try {
				int idx = Integer.parseInt(token);
				
				if (idx < 1 || idx > filtered.size()) {
					System.out.println("\nInvalid selection: " + token);
					continue;
				}
				
				//ignore duplicate input 
				if (!seen.add(idx)) {
					continue;
				}
				
				CraftSupply selected = filtered.get(idx - 1);
				
				String color = "";
				String quantity = "";
				String size = "";
				boolean cancelled = false;
				
				if (selected.needsColor() || selected.needsQuantity() || selected.needsSize()) {
					System.out.println("\n[" + selected.getName() + "] requires some additional information:" );
					System.out.println("(Type 'X' to skip this item and stop adding.\n" 
					+ " Type 'X' again to confirm skipping.)\n");
				}
				
				if (selected.needsColor()) {
					String result = promptRequiredField(kb, "Color", "", false);
					if (result == null) {
						cancelled = true;
					}
					else {
						color = result;
					}
				}
				
				if (!cancelled && selected.needsQuantity()) {
					String result = promptRequiredField(kb, "Quantity", "", false);
					if (result == null) {
						cancelled = true;
					}
					else {
						quantity = result;
					}
				}
				

				if (!cancelled && selected.needsSize()) {
					String result = promptRequiredField(kb, "Size", "", false);
					if (result == null) {
						cancelled = true;
					}
					else {
						size = result;
					}
				}
				
				if (cancelled) {
					skippedItems.add(selected.getName());
					userCancelledMidFlow = true;
					break;
				}

				
				//Final inv. item with any entered attributes
				CraftSupply itemToAdd = new CraftSupply(
						selected.getName(), selected.getType(),
						color, quantity, size
				);
				
				//Duplicate Check:
				CraftSupply existing = findExistingItem(itemToAdd);
				
				if (existing != null ) {
					handleDuplicateAdd(existing, itemToAdd, quantity, selected.needsQuantity());
					addedItems.add(selected.getName() + " (quantity updated)");
					
				} else {
					Inventory.items.add(itemToAdd);
					if (!Inventory.justItemNames.contains(itemToAdd.getName().toLowerCase().trim())) {
						Inventory.justItemNames.add(itemToAdd.getName().toLowerCase().trim());
					}
					appendToInventoryFile(itemToAdd);
					addedItems.add(selected.getName());
				}
				
			} catch (NumberFormatException e ) {
				System.out.println("\nInvalid selection: " + token);
			}
		}
				
		Collections.sort(Inventory.items, (a,b) -> a.getName().compareToIgnoreCase(b.getName()));
		
		//Summary message: 
		System.out.println();
		
		int addedCount = addedItems.size();
		
		if(!skippedItems.isEmpty()) {
			//Partial-add summary
			StringBuilder skippedList = new StringBuilder();
			for(int i = 0; i < skippedItems.size(); i++) {
				if (i > 0) skippedList.append(", ");
				skippedList.append(skippedItems.get(i));
			}
			
			if (addedCount > 0) {
				System.out.println(addedCount + " craft " + (addedCount == 1 ? "supply was" : "supplies were")
						+ " added to your invenroty.");
			}
			
			System.out.println(skippedList + " could not be added due to insufficient information.");
			
		} else if (addedCount == 1) {
			System.out.println("[" + addedItems.get(0) + "] was successfully added to your inventory");
			
		} else if (addedCount > 1) {
			System.out.println(addedCount + " craft supplies were added to your inventory");
			
		} else {
			System.out.println("No supplies were added to your inventory");
		}
	}
	
	//Duplicate handling 
	//Finds the first inventory item whose name matches
	
	private CraftSupply findExistingItem(CraftSupply cs) {
		for (CraftSupply item : Inventory.items) {
			//If the names match, compare the other attributes
			//If they match, return the item
			if (item.getName().equalsIgnoreCase(cs.getName())) {
				if(item.getColor().equalsIgnoreCase(cs.getColor()) && item.getSize().equalsIgnoreCase(cs.getSize())){
					return item;
				}
			}
		}
		return null;
	}
	
	//Increments quantity of an existing inv. item 
	//If qnt is a required field - we add the newly added entered qnt value to the existing one
	//otherwise we add 1 to whatever numeric quntity is stored 
	private void handleDuplicateAdd(CraftSupply existing, CraftSupply incoming, 
			String enteredQuantity, boolean quantityIsAttribute) {
		
		String currentQtyStr = existing.getQuantity();
		int currentQty = 1;
		try {
			currentQty = Integer.parseInt(currentQtyStr.trim());
		} catch (NumberFormatException e) { 
			currentQty = 1; 
		}
		
		int increment = 1;
		
		if (quantityIsAttribute && !enteredQuantity.isEmpty()) {
			try {
				increment = Integer.parseInt(enteredQuantity.trim());
			} catch (NumberFormatException e ){
				increment = 1;
			}
		}
		
		int newQty = currentQty + increment;
		
		CraftSupply updated = new CraftSupply(
				existing.getName(), existing.getType(),
				existing.getColor(), String.valueOf(newQty), 
				existing.getSize());
		
		//Replace in-memory and rewrite file
		int itemIdx = Inventory.items.indexOf(existing);
		if(itemIdx != -1) {
			Inventory.items.set(itemIdx, updated);
		}
		
		//Rewrite the whole file to keep it in sync
		try (FileWriter fw = new FileWriter("inventory.csv", false)) {
			for (CraftSupply item : Inventory.items) {
				fw.write(item.toInventoryFileLine() + System.lineSeparator());
			}
		} catch (IOException e) {
			System.err.println("Error updating inventory file: " + e.getMessage());
		}
				
	}
	
	//Required field prompt
	//Returns the entered string or null if the user cancelled
	//if required = true, empty input prints the "IS A REQUIRED FIELD" warning and re-prompts.
	//Close- warning : 1. 'X' appears, 2. 'X' returns null
	
	private String promptRequiredField (Scanner kb, String fieldName, 
			String currentValue, boolean allowEmpty) {
		boolean warnedClose = false;
		
		while(true) {
			if (currentValue.isEmpty()) {
				System.out.print( fieldName + ": ");
			} else {
				System.out.print(fieldName + " [" + currentValue + "]: ");
			}
			
			String input = kb.nextLine().trim();
			
			if(input.equalsIgnoreCase("X")) {
				if (!warnedClose) {
					System.out.println("\n** IF YOU CLOSE THE MENU, ANY CHANGES WILL NOT BE SAVED.");
					System.out.println("Type 'X' again to confirm, or enter a value to continue.\n");
					warnedClose = true;
				} else {
					return null;
				}
				continue;
			}
			
			if (input.isEmpty()) {
				if (allowEmpty) return "";
				System.out.println("** " + fieldName.toUpperCase() + " IS A REQUIRED FIELD.");
				continue;
			}
			
			return input;
		}
	}
	
	private void appendToInventoryFile(CraftSupply item) {
		try (FileWriter fw = new FileWriter("inventory.csv", true)) {
			fw.append(item.toInventoryFileLine()).append(System.lineSeparator());	
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}


	//prints contents of catalog
	private void printCat() {
		ArrayList<CraftSupply> list = getFilteredCatalog();
		if(list.isEmpty()) {
			System.out.println("(No items match your filter.)\n");
			return;
		}
		for (int i = 0; i< list.size(); i++){
			System.out.println(list.get(i).toStringWithIndex(i+1));
		}
	}
	
	//makes a catalog full of craft objects
	private void genCatalogItems() {
		//EITHER USE NEW CSV OR CHANGE CONTENTS
		String filePath = "materialcatalog.csv";
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
            	if (line.trim().isEmpty()) continue;
            	CraftSupply cs = new CraftSupply(line);
                catalogItems.add(cs);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
	}
	
	public ArrayList<CraftSupply> getItems(){
		return this.catalogItems;
	}
}
