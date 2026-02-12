package craftApplication;

import java.util.ArrayList;

public class InventoryScreen implements Screen{
	//inventory has an ArrayList of items
	ArrayList<String> items = new ArrayList<String>();
	
	//actions specific to inventory to be displayed
	String invActions = "Inventory Actions:\n"
			+ "- Type ‘F’ to filter your inventory\n"
			+ "- Type ‘R’ to generate craft recommendations\n";
	
	//display function
	public void disp() {
		System.out.println(navBar);
		System.out.println("Inventory");
		System.out.println("_________\n");
		System.out.println("Filter (F)\n");
		printItems();
		System.out.println(navActions+"\n");
		System.out.println(invActions+"\n");
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
		return screens[1];
	}
	
	//prints contents of inventory
	private void printItems() {
		for (String item:this.items){
			System.out.println(item);
		}
	}
}
