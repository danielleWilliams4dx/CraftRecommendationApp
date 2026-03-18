package craftApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Inventory {
	//inventory has an ArrayList of items
	static ArrayList<CraftSupply> items = new ArrayList<CraftSupply>();
	static ArrayList<String> justItemNames = new ArrayList<String>();
	static int count = 0;
	
	public Inventory() {
		if(count < 1) {
			genItems();
			sortInventory();
			genJustItemNames();
		}
		count++;
	}
	
	//generates the ArrayList for the inventory, code modified from: https://medium.com/@zakariafarih142/mastering-csv-parsing-in-java-comprehensive-methods-and-best-practices-a3b8d0514edf
	private void genItems() {
		String filePath = "inventory.csv";
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                CraftSupply cs = new CraftSupply(line);
                items.add(cs);
            }
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        }
	}
	
	//sorts the inventory items alphabetically
	private void sortInventory() {
		ArrayList<CraftSupply> sorted = new ArrayList<CraftSupply>();
			
		while(items.size() > 0) {
			CraftSupply first = items.get(0);
			
			for(int i = 0; i < items.size(); i++) {
				//if the item's name comes first in the alphabet, it becomes the new first
				if(first.toString().compareTo(items.get(i).toString()) > 0) {
					first = items.get(i);
				}					
			}
			//add the item in first to the sorted list and remove it from items
			sorted.add(first.copy());
			items.remove(first);
		}
		
		//items should be empty now
		
		//adds all of the sorted items to items
		items.addAll(sorted);
	}
	
	public ArrayList<String> getJustItemNames() {
		return justItemNames;
	}
	
	//generates an ArrayList of the item names in the CraftSupply ArrayList
	private void genJustItemNames() {
		for(CraftSupply item : items) {
			justItemNames.add(item.getName().toLowerCase());
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
				System.out.println(Integer.toString(i)+". " +item);
				i++;
			}
		}
		
		if(results == false) {
			System.out.println("No results.");
		}
	}
	
	public int size() {
		return items.size();
	}

	
	//add a new item to the inventory and re-sort
	public boolean addItem(CraftSupply cs) {
		boolean added = items.add(cs);
		sortInventory();
		genJustItemNames();
		return added;
	}
	
	//remove an item from the inventory and re-sort
	public boolean removeItem(CraftSupply cs) {
		boolean removed = items.remove(cs);
		sortInventory();
		genJustItemNames();
		return removed;
	}
}
