package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;

public class Recommender {
	
	//still have error where imperfect crafts are printed in perfect condition, may need to separate lists but need to check with other functions
	ArrayList<Craft> recs = new ArrayList<Craft>();
	
	public Recommender(String input, Inventory inv, Screen[] screens) {
		
		ArrayList<String> justItemNames = inv.getJustItemNames();
		
		System.out.println("\n");
		
		Boolean perfect = false;
		Boolean imperfect = false;
		
		//case: use entire inv
		if (input.equals("E")) {
			for (Craft craft: ((CatalogScreen) screens[2]).getItems()) {
				
				String[] materials = craft.getMaterials();
				int count = 0;
				
				for (String mat: materials) {			
					if (!justItemNames.contains(mat.toLowerCase())){
						count++;
					}
				}
				
				//exact match
				if (count==0) {
					recs.add(craft);
					perfect = true;
				}
				
				//+1 or +2 materials
				else if (count ==1 || count==2) {
					recs.add(craft);
					imperfect = true;
				}
			}
		}
			
		//user selects subset
		
		else {
			
			String[] itemsUsedIndices = input.split(",");
			ArrayList<String> itemsUsed = new ArrayList<>();
			
			//Get all of the item names based on the inputed indices
			//Modified code from viewCraftFlow on SavedCraftScreen
			for(String index : itemsUsedIndices) {
				
				index = index.trim();
				if(index.isEmpty()) {
					continue;
				}
				
				try {
					int i = Integer.parseInt(index)-1;
					if(i >= 0 && i < justItemNames.size()) {
						itemsUsed.add(justItemNames.get(i).toLowerCase());
					}else {
						System.out.println("Invalid craft supply number: " + index + "\n");
					}
				} catch (NumberFormatException e) { 
					//non-numeric token handling 
					System.out.println("Invalid craft supply number: " + index + "\n");
				}
	
			}
			
//			debugging
//			System.out.println("itemsUsed: ");
//			for(String item : itemsUsed) {
//				System.out.println(item);
//			}
			
			
			for (Craft craft: ((CatalogScreen) screens[2]).getItems()) {
				
				String[] materials = craft.getMaterials();
				int count = 0;
				
				for (String mat: materials) {
					if (!itemsUsed.contains(mat.toLowerCase())){
						count++;
					}
				}
				
				//exact match
				if (count==0) {
					recs.add(craft); //fix: storing craft
					perfect = true;
					
				}
				
				//+1 or 2 materials 
				else if (count == 1 || count == 2) {
					recs.add(craft);
					imperfect = true;		
				}
			}
		}
		if (perfect) {
			if (recs.size()==0){
				System.out.println("Here is a craft recommendation:");
			}
			else {
				System.out.println("Here are "+Integer.toString(recs.size())+" craft recommendations:");
			}
			for (Craft craft: recs) {
				System.out.println(craft);
			}
		}
		else if (imperfect) {
			System.out.println("We could not find any crafts that perfectly match your criteria.");
			if (recs.size()==0){
				System.out.println("Here is a craft that require a few additional materials. We *starred* these new materials in your results.");
			}
			else {
				System.out.println("Here are "+Integer.toString(recs.size())+" crafts that require a few additional materials. We *starred* these new materials in your results.");
			}
			for (Craft craft: recs) {
				System.out.println("  " + craft.specialPrint(inv));
			}
		}
		else {
			System.out.println("Sorry, we could not find any crafts that match your criteria.");
		}
		
	}
}
