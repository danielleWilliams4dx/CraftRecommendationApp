package craftApplication;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

public class InventoryScreen implements Screen{
	//inventory has an ArrayList of items
	ArrayList<String> items = new ArrayList<String>();
	
	//actions specific to inventory to be displayed
	String invActions = "Inventory Actions:\n"
			+ "- Type ‘F’ to filter your inventory\n"
			+ "- Type ‘R’ to generate craft recommendations\n";
	
	public InventoryScreen() {
		genItems();
		Collections.sort(this.items);
	}
	
	//display function
	public void disp() {
		System.out.println(navBar);
		System.out.println("Inventory");
		System.out.println("_________\n");
		System.out.println("Filter (F)\n");
		printItems();
		System.out.println("\n"+navActions+"\n");
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
		else if (input.equals("S")) {
			screens[3].disp();
			return screens[3];
		}
		else if (input.equals("R")) {
			Scanner kb = new Scanner(System.in);
			System.out.println("Recommendation Generation Actions:\n"
					+ "- Type a comma separated list of the numbers of certain craft supplies to generate specific recommendations\n"
					+ "- Type‘E’ to generate craft recommendations for your entire inventory \n");
			String answer = kb.nextLine();
			Recommender rec = new Recommender(answer, this.items, screens);
			System.out.println("Craft Recommendation Actions:\n"
					+ "- Type ‘V’ to view a craft\n"
					+ "- Type a comma separated list of the numbers of crafts that you would like to save\n"
					+ "- Type ‘I’ to return to your inventory");
			answer = kb.nextLine().toUpperCase();
			if (answer.equals("V")) {
				System.out.println("Please type the number of the craft you would like to view: ");
				int response = kb.nextInt();
				System.out.println(rec.recs.get(response-1));
			}
			if (answer.equals("I")) {
				screens[1].disp();
			}
			else {
				String[] nums = answer.split(",");
				for (String number: nums) {
					rec.recs.get(Integer.parseInt(number)).save();
				}
			}
		kb.close();
		}
		return screens[1];
	}
	
	//prints contents of inventory
	private void printItems() {
		for (String item:this.items){
			System.out.println(item);
		}
	}
	
	//generates the ArrayList for the inventory, code modified from: https://medium.com/@zakariafarih142/mastering-csv-parsing-in-java-comprehensive-methods-and-best-practices-a3b8d0514edf
	private void genItems() {
		String filePath = "inventory.csv";
        String line;
        String delimiter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (String value : values) {
                    this.items.add(value);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
	}
}
