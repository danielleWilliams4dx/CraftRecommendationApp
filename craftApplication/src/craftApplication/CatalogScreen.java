package craftApplication;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;


public class CatalogScreen implements Screen {
	
	//catalog has an ArrayList of its catalogItems
	//NEEDS TO BECOME NOT OF CRAFTS
	//test push 
	ArrayList<Craft> catalogItems = new ArrayList<Craft>();
	
	//get the user's inventory (items are static)
	Inventory inv = new Inventory();
	
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
			System.out.println("Filter (F) [Active: " + String.join(", ", activeFilters) + "]\n");
		}
		
		printCat();
		System.out.println(navActions+"\n");
		System.out.println(catActions+"\n");
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
		//Any other input: treating as the "view craft" selection by number
		viewSelectedCrafts(input);
        this.disp();
		return screens[2];
	}
	
	private void runFilterMenu() {
		
		Scanner kb = new Scanner(System.in);
		
		//Building a list of unique "categories" from the catalog 
		ArrayList<String> categories = getCatalogCategories();
		System.out.println("\n\nCatalog Filter Menu");
		System.out.println("_____________________\n");
		System.out.println("Select one or more categories (comma-separated)");
		System.out.println("Type 'D' to clear filters.");
		System.out.println("Type 'C' to return to Catalog without changes.\n");
		
		//show selectable filter options (1-based)
		for (int i = 0; i < categories.size(); i++) {
			System.out.println((i + 1 ) + ") " + categories.get(i)); 
		}
		
		System.out.println("\nYour Answer: ");
		String ans = kb.nextLine().trim().toUpperCase();
		
		if(ans.equals("C")) {
			kb.close();
			return;
		}
		if (ans.equals("D")) {
			activeFilters.clear(); 
			System.out.println("\nAll filters cleared.");
			kb.close();
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
		kb.close();
	}
	
	private ArrayList<String> getCatalogCategories() {
		HashSet<String> set = new HashSet<String>();
		for (Craft c : catalogItems) {
			//Craft Types as the category for filtering 
			String s = c.toString(); //extracting Type Line
			String type = extractTypeFromString(s);
			if (type != null && !type.isEmpty()) set.add(type);
		}
		ArrayList<String> out = new ArrayList<String>(set);
		Collections.sort(out);
		return out;
	}
	
	private String extractTypeFromString(String s) {
		//Craft.toStringIndex prints like a "Type: <type>"
		for (String line: s.split("\\n")) {
			line = line.trim();
			if (line.startsWith("Type:")) return line.substring("Type:".length()).trim();
			if (line.startsWith("Type")) {
				int idx = line.indexOf(":");
				if (idx >= 0) 
					return line.substring(idx+1).trim();
				
			}
		}
		return "";
	}
	
	private void viewSelectedCrafts(String input) {
		String[] nums = input.split(",");
		HashSet<Integer> seen = new HashSet<Integer>();
		System.out.println();
		
		for (String number : nums) {
			String token = number.trim();
			if (token.isEmpty()) continue;
			try {
				int idx = Integer.parseInt(token);
				if (!seen.add(idx)) continue;
				Craft c = getFilteredCatalog().get(idx-1);
				System.out.println(c.toStringWithIndex(idx, inv));
			} catch (Exception e) {
				System.out.println("Invalid selection: " + token + "\n");
			}
		}
	}
	
	private ArrayList<Craft> getFilteredCatalog() {
		if (activeFilters.isEmpty()) return catalogItems;
		
		ArrayList<Craft> filtered = new ArrayList<Craft>(); 
		for (Craft c : catalogItems) {
			String type = extractTypeFromString(c.toString());
			if (activeFilters.contains(type)) filtered.add(c);
		}
		return filtered;
	}


	//prints contents of catalog
	private void printCat() {
		ArrayList<Craft> list = getFilteredCatalog();
		if(list.isEmpty()) {
			System.out.println("(No items match your filter.)\n");
			return;
		}
		for (int i = 0; i< list.size(); i++){
			System.out.println(list.get(i).toStringWithIndex(i+1, inv));
		}
	}
	
	//makes a catalog full of craft objects
	private void genCatalogItems() {
		//EITHER USE NEW CSV OR CHANGE CONTENTS
		String filePath = "catalog.csv";
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
            	//SHOULD NOT BE CRAFTS
            	Craft c = new Craft(line);
                this.catalogItems.add(c);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
	}
	
	//SHOULD NOT BE CRAFTS
	public ArrayList<Craft> getItems(){
		return this.catalogItems;
	}
}
