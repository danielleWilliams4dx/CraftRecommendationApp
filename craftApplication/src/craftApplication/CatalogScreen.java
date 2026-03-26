package craftApplication;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

//
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
	//supports single/multiple selections, spaces like "1, 2, 3" 
	//duplicate protection, extra prompts, singular plural confirmation messages 
	private void addSelectedSupplies(String input) {
		Scanner kb = new Scanner(System.in);
		
		//refresh inv before checking duplicates 
		new Inventory();
		
		ArrayList<CraftSupply> filtered = getFilteredCatalog();
		HashSet<Integer> seen = new HashSet<Integer>();
		
		String[] nums = input.split(",");
		int addedCount = 0;
		String singleAddedName = "";
		
		for (String number: nums) {
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
				
				if (selected.needsColor() || selected.needsQuantity() || selected.needsSize()) {
					System.out.println("\n[" + selected.getName() + "] requires some additional information:");
				}
				
				if (selected.needsColor()) {
					System.out.print("Color: ");
					color = kb.nextLine().trim();
				}
				
				if (selected.needsQuantity()) {
					System.out.print("Quantity: ");
					quantity = kb.nextLine().trim();
				}
				
				if (selected.needsSize()) {
					System.out.print("Size: ");
					size = kb.nextLine().trim();
				}
				
				//Final inv. item with any entered attributes
				CraftSupply itemToAdd = new CraftSupply(
						selected.getName(),
						selected.getType(),
						color,
						quantity, 
						size
				);
				
				//prevent exact duplicates 
				if (!Inventory.items.contains(itemToAdd)) {
					Inventory.items.add(itemToAdd);
					
					//raw supply names for recommender
					if (!Inventory.justItemNames.contains(itemToAdd.getName().toLowerCase().trim())) {
						Inventory.justItemNames.add(itemToAdd.getName().toLowerCase().trim());
					}
					
					appendToInventoryFile(itemToAdd);
					
					addedCount++;
					singleAddedName = selected.getName();
				}
			
			}  catch (NumberFormatException e ) {
				System.out.println("\nInvalid selection: " + token);
			}
		}
		
		Collections.sort(Inventory.items, (a,b) -> a.getName().compareToIgnoreCase(b.getName()));
		
		System.out.println();
		if (addedCount == 1) {
			System.out.println("[" + singleAddedName + "] was successfully added to your inventory");
		}
		else if (addedCount > 1) {
			System.out.println("[" + addedCount + "] craft supplies were added to your inventory");
		}
		else {
			System.out.println("No supplies were added to your inventory");
		}
	}
	
	private void appendToInventoryFile(CraftSupply item) {
		try (FileWriter fw = new FileWriter("inventory.csv", true)) {
			fw.append(item.toInventoryFileLine()).append(System.lineSeparator());
		} catch (IOException e ) {
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
