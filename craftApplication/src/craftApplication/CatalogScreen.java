package craftApplication;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;

public class CatalogScreen implements Screen {
	
	//catalog has an ArrayList of its catalogItems
	ArrayList<Craft> catalogItems = new ArrayList<Craft>();
		
	//actions specific to catalog to be displayed
	String catActions = "Catalog Actions:\n"
			+ "- Type ‘F’ to filter the catalog\n"
			+ "- Type a comma separated list of craft supply item numbers to \n"
			+ "  add supplies to your inventory\n";
	
	//constructor makes the catalog
	public CatalogScreen(){
		genCatalogItems();
	}
	
	//display function
	public void disp() {
		System.out.println(navBar);
		System.out.println("Catalog");
		System.out.println("_______\n");
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
			//filters
		}
		else {
			String[] materials = input.split(",");
			addToCat(materials);
			
		}
		return screens[2];
	}
	
	private void addToCat(String[] materials) {
		try {
		FileWriter myWriter = new FileWriter("catalog.csv");
			for (String m: materials) {
				myWriter.write(m);
			}
			myWriter.close();
		} catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
		
		
	}

	//prints contents of catalog
	private void printCat() {
		for (Craft item:this.catalogItems){
			System.out.println(item);
		}
	}
	
	//makes a catalog full of craft objects
	private void genCatalogItems() {
		String filePath = "catalog.csv";
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
            	Craft c = new Craft(line);
                this.catalogItems.add(c);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
	}
	
	public ArrayList<Craft> getItems(){
		return this.catalogItems;
	}
}
