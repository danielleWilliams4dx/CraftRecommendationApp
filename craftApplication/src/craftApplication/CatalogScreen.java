package craftApplication;

import java.util.ArrayList;

public class CatalogScreen implements Screen {
	
	//catalog has an ArrayList of its catalogItems
	ArrayList<String> catalogItems = new ArrayList<String>();
		
	//actions specific to catalog to be displayed
	String catActions = "Catalog Actions:\n"
			+ "- Type ‘F’ to filter the catalog\n"
			+ "- Type a comma separated list of craft supply item numbers to \n"
			+ "  add supplies to your inventory\n";
	
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
		return screens[2];
	}
	
	//prints contents of catalog
	private void printCat() {
		for (String item:this.catalogItems){
			System.out.println(item);
		}
	}
}
