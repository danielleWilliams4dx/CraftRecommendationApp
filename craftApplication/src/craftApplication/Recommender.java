package craftApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;


public class Recommender {
	
	
	//stores current recs so InventoryScreen can view/save them
	ArrayList<Craft> recs = new ArrayList<Craft>();
	
    //stores the exact supply names used to generate the current recs
	//lets recommendation printing star missing items correctly for both 
	// (E) full inv mode
    // subset mode (1,2,3)	
	private ArrayList<String> availableItemNames = new ArrayList<String>();
	
	
	//input = "E" or comma-seperated indices
	//inv = full inventory object
	//visibleItemNames = item names in the same order the user sees on the Inv screen 
	
	public Recommender(String input, Inventory inv, ArrayList<String> visibleItemNames) {
		
		ArrayList<Craft> allCrafts = loadCrafts();
		
		ArrayList<Craft> perfectRecs = new ArrayList<Craft>();
		ArrayList<Craft> closeRecs = new ArrayList<Craft>();
		
		System.out.println("\n");
		
		
		//Generate recommendations using the full inventory
		if (input.equals("E")) {
			availableItemNames = normalizeNames(inv.getJustItemNames());
		}
		
		//Generate recommendations using the full inventory
		else {
			availableItemNames = buildSelectedItems(input, visibleItemNames);
			
			//Fail safely if something unexpected slipped through validation
			if (availableItemNames.isEmpty()) {
				System.out.println("Sorry, we could not generate recommendations from that selection.");
				return;
			}
		}

		for (Craft craft : allCrafts) {
			int missing = countMissingMaterials(craft, availableItemNames);
			
			if (missing == 0) {
				perfectRecs.add(craft);
			}
			else if (missing == 1 || missing == 2) {
				closeRecs.add(craft);
			}
		}
		
		//Perfect matches come first
		if (!perfectRecs.isEmpty()) {
			recs.addAll(perfectRecs);
			
			if (perfectRecs.size() == 1) {
				System.out.println("Here is a craft recommendation:\n");
			} else {
				System.out.println("Here are " + perfectRecs.size() + " craft recommendations:\n");
			}
			
			for (int i = 0; i < perfectRecs.size(); i++) {
				System.out.println(perfectRecs.get(i).toStringWithIndex(i + 1,  availableItemNames));
			}
		}
		
		//If no perf match exist, show close matches (+1/+2 missing materials)
		else if (!closeRecs.isEmpty()) {
			recs.addAll(closeRecs);
			
			System.out.println("We could not find any crafts that perfectly match your criteria.");
			
			if (closeRecs.size() == 1) {
				System.out.println("Here is 1 close match to your criteria.");
			} else {
				System.out.println("Here are " + closeRecs.size() + " close matches to your criteria");
			}
			
			System.out.println("Additional supplies that you need are *starred*\n");
			
			for (int i = 0; i< closeRecs.size(); i++) {
				System.out.println(closeRecs.get(i).toStringWithIndex(i + 1, availableItemNames));
			}
			
		}
		
		
		//No matches at all
		else {
			System.out.println("Sorry, we could not find any crafts that match your criteria.");
		}
	}
	
	//Lets InvScreen view rec details with the correct starred materials
	public ArrayList<String> getAvailableItemNames() {
		return new ArrayList<String>(availableItemNames);
	}
	
	//Crafts are still loaded from catalog.csv because it remains the craft dataset
	private ArrayList<Craft> loadCrafts() {
		ArrayList<Craft> crafts = new ArrayList<Craft>();
		String filePath = "catalog.csv";
		String line;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty());
				crafts.add(new Craft (line));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return crafts;
	}
	
	//subset list from the inv numbers the user selected 
	//if any token is invalid - fail closed and return empty list 
	private ArrayList<String> buildSelectedItems(String input, ArrayList<String> visibleItemNames) {
		ArrayList<String> itemsUsed = new ArrayList<String>();
		HashSet<Integer> seenIndices = new HashSet<Integer>();
		boolean invalidSelectionFound = false;
		
		String[] itemsUsedIndices = input.split(",");
		
		for (String index : itemsUsedIndices) {
			String token = index.trim();
			if(token.isEmpty()) continue;
			

			try {
				int i = Integer.parseInt(token)-1;
				
				if(i >= 0 && i < visibleItemNames.size()) {
					//ignore duplicate index 
					if (seenIndices.add(i)) {
						itemsUsed.add(visibleItemNames.get(i).toLowerCase().trim());
					}
				} else {
					invalidSelectionFound = true;
				}
			} catch (NumberFormatException e) { 
				invalidSelectionFound = true;
			}
	
		}
		
		if (invalidSelectionFound) {
			itemsUsed.clear();
		}
		return normalizeNames(itemsUsed);
	}
	
	//Normalize sipply names so rec matching stays case-imsensitive and clean
	private ArrayList<String> normalizeNames(ArrayList<String> names) {
		ArrayList<String> cleaned = new ArrayList<String>();
		HashSet<String> seen = new HashSet<String>();
		
		for (String name: names) {
			if (name == null) continue;
			
			String normalized = name.trim().toLowerCase();
			
			if(!normalized.isEmpty() && seen.add(normalized)) {
				cleaned.add(normalized);
			}
		}
		
		return cleaned;
	}
	
	//Count how many materials from a craft are missing from the supplied item listt
	private int countMissingMaterials(Craft craft, ArrayList<String> availableItems) {
		int count = 0;
		
		for (String mat : craft.getMaterials()) {
			String cleaned = mat.trim().toLowerCase();
			if(!availableItems.contains(cleaned)) {
				count++;
			}
		}
		
		
		return count;
	}
	
}
		
		
			
