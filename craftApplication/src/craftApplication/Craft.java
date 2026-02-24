package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException; 
import java.util.HashSet;

public class Craft {
	
	private String name;
	private String type;
	private String level;
	private String timeEst;
	private String description;
	private String[] materials;
	private String line;
	
	
	public Craft(String line) {
		this.line = line;
        String[] values = line.split(",");
        this.name = values[0];
        this.type = values[1];
        this.level = values[2];
        this.timeEst = values[3];
        this.description = values[4];
        this.materials = Arrays.copyOfRange(values, 5, values.length);
        
        //trim materials so comparisons work reliably 
        for (int i = 0; i< this.materials.length; i++ ) {
        	this.materials[i] = this.materials[i].trim();
        }
	}
	
	//FIX: keeping default toString(), without hardcoding 1) for every craft
	public String toString() {
		return this.name+ "\n"
				+ "   Type: "+ this.type + "\n"
				+ "   Materials: "+ materialsString()+ "\n"
				+ "   Skill Level: "+ this.level+ "\n"
				+ "   Time: "+ this.timeEst + "\n"
				+ "   Description:\n"
				+ "   " + this.description + "\n";
	} 
	
	//fix: allowing screens to print correct numbering (1 based)
	public String toStringWithIndex(int index) {
		return index + ") " + this.name + "\n"
				+ "   Type: " + this.type + "\n"
				+ "   Materials: "+ materialsString()+ "\n"
				+ "   Skill Level: "+ this.level+ "\n"
				+ "   Time: "+ this.timeEst + "\n"
				+ "   Description:\n"
				+ "   " + this.description + "\n";
	}
	
	//Star each missing item only once (no duplicates)
	public void specialPrint(String[] inventory) {
		
		HashSet<String> invSet = new HashSet<>();
		for (String thing : inventory) {
			if (thing != null) invSet.add(thing.trim());
		}
		
		System.out.println("   "+ this.name + "\n"
				+ "   Type: " + this.type + "\n"
				+ "   Materials:\n   ");
		
		for (String item: this.materials) {
			String cleaned = item.trim();
			if (invSet.contains(cleaned)) {
				System.out.print(cleaned + " ");
			} else {
				System.out.print("*" + cleaned + "* ");
				
			}
		}
			
		System.out.println("\n   Skill Level: " + this.level+ "\n"
				+ "   Time: "+ this.timeEst+ "\n"
				+ "   Description:\n"
				+ "   " + this.description+ "\n");
		
	}
	
	private String materialsString() {
		String s = "";
		for (String obj: materials) {
			s = s + obj + " ";
		}
		return s.trim();
	}
	
	public String[] getMaterials() {
		return materials;
	}
	
	public Boolean isThisOne(String name) {
		return this.name.equals(name);
	}
	
	public String getLine() {
		return this.line;
	}
//fix: star logic but for ArrayList
	
	public void specialPrint(ArrayList<String> inventory) {
		
		HashSet<String> invSet = new HashSet<>();
		for (String thing : inventory) {
			if (thing != null) invSet.add(thing.trim());
		}
		
		System.out.print("   " + this.name + "\n"
				+ "   Type: " + this.type + "\n"
				+ "   Materials:\n   ");
		
		for (String item: this.materials) {
			String cleaned = item.trim();
			if (invSet.contains(cleaned)) {
				System.out.print(cleaned + " ");
			} else {
				System.out.print("*" + cleaned + "* ");
			}

		}
		

		System.out.println("\n   Skill Level: " + this.level + "\n"
				+ "   Time: " + this.timeEst + "\n"
				+ "   Description:\n"
				+ "   "+ this.description+"\n");
		
	}

	public void save() {
		//adapted from: https://www.w3schools.com/java/java_files_write.asp
		//fix: appending mode so saving doesn't overwrite the file
		//fix: adding new line, so craft displays 
		try {
		      FileWriter myWriter = new FileWriter("savedCrafts.csv");
		      myWriter.write(this.line + System.lineSeparator());
		      myWriter.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}
}
