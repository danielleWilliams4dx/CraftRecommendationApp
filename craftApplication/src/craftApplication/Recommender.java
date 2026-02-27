package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;

public class Recommender {
	
	ArrayList<Craft> recs = new ArrayList<Craft>();
	
	public Recommender(String input, ArrayList<CraftSupply> items, Screen[] screens) {
		
		ArrayList<String> justItemNames = getAllItemNames(items);
		
		//case: use entire inv
		if (input.equals("E")) {
			
			for (Craft craft: ((CatalogScreen) screens[2]).getItems()) {
				
				String[] materials = craft.getMaterials();
				int count = 0;
				
				for (String mat: materials) {
					if (!justItemNames.contains(mat)){
						count++;
					}
				}
				
				//exact match
				if (count==0) {
					System.out.println(craft);
					recs.add(craft);
				}
				
				//+1 or +2 materials
				else if (count ==1 || count==2) {
					craft.specialPrint(justItemNames);
					recs.add(craft);
				}
				
				else {
					System.out.println("Sorry, we could not find any crafts that match your criteria.");
				}
			}
		}
		//user selects subset
		
		else {
			
			String[] itemsUsed = input.split(",");
			
			for (Craft craft: ((CatalogScreen) screens[2]).getItems()) {
				
				String[] materials = craft.getMaterials();
				int count = 0;
				
				for (String mat: materials) {
					if (!Arrays.asList(itemsUsed).contains(mat)){
						count++;
					}
				}
				
				//exact match
				if (count==0) {
					System.out.println(craft);
					recs.add(craft); //fix: storing craft
				}
				
				//+1 or 2 materials 
				else if (count == 1 || count == 2) {
					craft.specialPrint(justItemNames);
					recs.add(craft);
				}
			}
		}
		
	}
	
	private ArrayList<String> getAllItemNames(ArrayList<CraftSupply> items) {
		ArrayList<String> names = new ArrayList<String>();
		
		for(CraftSupply item : items) {
			names.add(item.getName());
		}
		
		return names;
	}
}
