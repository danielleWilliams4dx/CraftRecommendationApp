package craftApplication;

import java.util.ArrayList;
import java.util.Scanner;
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
		ArrayList<Craft> closeRecsPlus1 = new ArrayList<Craft>();
		ArrayList<Craft> closeRecsPlus2 = new ArrayList<Craft>();
		
		ArrayList<String> allInventoryItemNames = normalizeNames(inv.getJustItemNames());
		
		System.out.println("\n");
		
		
		
		//Generate recommendations using the full inventory
		if (input.equals("E")) {
			availableItemNames = normalizeNames(inv.getJustItemNames());
		}
		
		//Generate recommendations using the user input
		else {
			availableItemNames = buildSelectedItems(input, visibleItemNames);
			
			//Fail safely if something unexpected slipped through validation
			if (availableItemNames.isEmpty()) {
				System.out.println("Sorry, we could not generate recommendations from that selection.");
				return;
			}
		}

		for (Craft craft : allCrafts) {
			int[] res = countMissingMaterials(craft, availableItemNames, allInventoryItemNames);
			int missing = res[0];
			int matInInv = res[1];
			
			if (missing == 0) {
				perfectRecs.add(craft);
			}
			// if the material is not in the selection but is in the inventory, 
			// add it to the front of the recommendation tier
			else if (missing == 1) {
				if(matInInv == 1) {
					closeRecsPlus1.add(0, craft);
				}
				else {
					closeRecsPlus1.add(craft);
				}
			}
			else if (missing == 2) {
				if(matInInv == 1) {
					closeRecsPlus2.add(0, craft);
				}
				else {
					closeRecsPlus2.add(craft);
				}
			}
		}
		
		//Perfect matches come first
		Boolean showMore = true;
		
		if (!perfectRecs.isEmpty()) {
			recs.addAll(perfectRecs);
			
			if (perfectRecs.size() == 1) {
				System.out.println("Here is 1 craft recommendation:\n");
			} else {
				System.out.println("Here are " + perfectRecs.size() + " craft recommendations:\n");
			}
			
			for (int i = 0; i < recs.size(); i++) {
				System.out.println(recs.get(i).toStringWithIndex(i + 1,  availableItemNames));
			}
//			check if user wants more matches, if there are any
			Scanner kb =new Scanner(System.in);
			if(!closeRecsPlus1.isEmpty() || !closeRecsPlus2.isEmpty()) {
				System.out.println("Type 'Y' to show close matches or any other key to continue.\n\nYour Answer: ");
				if (kb.next().toUpperCase().equals("Y")){
					showMore = true;
					System.out.println();
				}else {
					System.out.println();
					return;
				}
			}else {
				System.out.println();
				return;
			}
		}
		
		//If no perf match exist, show close matches (+1/+2 missing materials)
		if (!closeRecsPlus1.isEmpty() || !closeRecsPlus2.isEmpty()) {
			
			//only add close matches if showMore is true if we have perfect matches
			if (perfectRecs.isEmpty() || showMore) {
				int startIdx = recs.size();
				recs.addAll(closeRecsPlus1);
				recs.addAll(closeRecsPlus2);
				
				int totalClose = closeRecsPlus1.size() + closeRecsPlus2.size();
				System.out.println("Showing " + totalClose + " close matches. Additional supplies needed are *starred*\n");
				
				for (int i = startIdx; i < recs.size(); i++) {
					System.out.println(recs.get(i).toStringWithIndex(i + 1, allInventoryItemNames));
				}
			}
		}
		
		if (recs.isEmpty()) {
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
	
	//Count how many materials from a craft are missing from the supplied item list
	private int[] countMissingMaterials(Craft craft, ArrayList<String> availableItems , ArrayList<String> allInventoryItems) {
		int count = 0;
		int matInInv = 0;
		
		for (String mat : craft.getMaterials()) {
			String cleaned = mat.trim().toLowerCase();
			if(!availableItems.contains(cleaned)) {
				count++;
				// Note it the material is not in the material selection but is in the inventory
				if(allInventoryItems.contains(cleaned)) {
					matInInv = 1;
				}
			}
		}
		
		int[] res = {count, matInInv};
		return res;
	}
	
}
		
		
			
