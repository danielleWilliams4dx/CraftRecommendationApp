package craftApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//Changes:
//NEW getVisibleItems(): returns full CraftSupply objects (not just names )
// NEW updateItem(): swaps one item and rewrites the file
//NEW deleteItems(): removes one or more items and rewrites the file
// New rewriteInventoryFile(): overwrites inventory.csv from scratch; used by edit and delete
public class Inventory {
	
	//inventory has an ArrayList of items
	static ArrayList<CraftSupply> items = new ArrayList<CraftSupply>();
	static ArrayList<String> justItemNames = new ArrayList<String>();
	
	
	public Inventory() {
		refreshInventory();
	}

	
	//generates the ArrayList for the inventory, code modified from: https://medium.com/@zakariafarih142/mastering-csv-parsing-in-java-comprehensive-methods-and-best-practices-a3b8d0514edf
	private void refreshInventory() {
		items.clear();
		justItemNames.clear();
		genItems();
		sortInventory();
		genJustItemNames();
	}
	
	//Load inventory.csv
	private void genItems() {
		String filePath = "inventory.csv";
		String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
            	if (line.trim().isEmpty()) continue;
            	
            	String[] values = line.split("\\t");
            	
            	if (values.length >= 2) {
            		String name = values[0].trim();
            		String type = values[1].trim();
            		
            		String color = "";
            		String quantity = "";
            		String size = "";
            		
            		//Parse optional fields if they exist
            		for (int i = 2; i< values.length; i++) {
            			String extra = values[i].trim();
            			
            			if (extra.startsWith("Color:")) {
            				color = extra.substring("Color:".length()).trim();
            			}
            			else if (extra.startsWith("Qty:")) {
            			    quantity = extra.substring("Qty:".length()).trim();
            		    }
            			else if (extra.startsWith("Size:")) {
            			    size = extra.substring("Size:".length()).trim();
            	        }
            		}
            		
            		items.add(new CraftSupply (name, type, color, quantity, size));
            	}
            }
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        }
	}
	
	//sorts the inventory items alphabetically
	private void sortInventory() {
		Collections.sort(items, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
	}
	
	public ArrayList<String> getJustItemNames() {
		return justItemNames;
	}
	
	private void genJustItemNames() {
		justItemNames.clear();
		for (CraftSupply item : items ) {
			justItemNames.add(item.getName().toLowerCase().trim());
		}
	}

	//prints contents of inventory
	public void printItems(ArrayList<String> activeFilters) {
		boolean results = false;
		int i = 1;
		
		for (CraftSupply item : items){
			//If active filters is empty or 
			//if the item's type is within the active filters, print it out
			if (activeFilters.isEmpty() || activeFilters.contains(item.getType())) {
				results = true;
				System.out.println(i + ") " +item);
				i++;
			}
		}
		
		if(!results) {
			System.out.println("No results.");
		}
	}
	
	//returns the visible inv names in the same idx order the user sees 
	public ArrayList<String> getVisibleItemNames(ArrayList<String> activeFilters) {
		ArrayList<String> visible = new ArrayList<String>();
		
		for (CraftSupply item : items) {
			if (activeFilters.isEmpty() || activeFilters.contains(item.getType())) {
				visible.add(item.getName().toLowerCase().trim());
			}
		}
		
		return visible;
	}
	
	//Returns visible CraftSupply OBJECTS in the order the user sees them on the screen 
	// Used by edit and delete flows so they can work with the objects
	
	public ArrayList<CraftSupply> getVisibleItems(ArrayList<String> activeFilters) {
		ArrayList<CraftSupply> visible = new ArrayList<CraftSupply>();
		
		for (CraftSupply item: items) {
			if (activeFilters.isEmpty() || activeFilters.contains(item.getType())) {
				visible.add(item);
			}
		}
		
		return visible;
	}
	
	public int size() {
		return items.size();
	}

	
	//add a new item to the inventory and re-sort and updates justNames (doesn't write to file)
	public boolean addItem(CraftSupply cs) {
		boolean added = items.add(cs);
		sortInventory();
		genJustItemNames();
		return added;
	}
	
	//remove an item from the inventory and re-sort
	public boolean removeItem(CraftSupply cs) {
		boolean removed = items.remove(cs);
		if (removed) {
			sortInventory();
			genJustItemNames();
		}
		return removed;
	}
	
	//Replaces oldItem with newItem, re-sorts, and re-writes inventory.csv
	//returns false if oldItem isn't found
	public boolean updateItem(CraftSupply oldItem, CraftSupply newItem) {
		int idx = items.indexOf(oldItem);
		if (idx == -1) return false;
		
		items.set(idx, newItem);
		sortInventory();
		genJustItemNames();
		rewriteInventoryFile();
		return true;
	}
	
	public void deleteItems( ArrayList<CraftSupply> toDelete) {
		items.removeAll(toDelete);
		sortInventory();
		genJustItemNames();
		rewriteInventoryFile();
	}
	
	public void rewriteInventoryFile() {
		try(FileWriter fw = new FileWriter("inventory.csv", false)) {
			for (CraftSupply item : items) {
				fw.write(item.toInventoryFileLine() + System.lineSeparator());
			}
		} catch (IOException e) {
			System.err.println("Error rewriting inventory file: " + e.getMessage());
		}
	}

}
