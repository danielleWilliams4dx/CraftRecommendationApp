package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException; 

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
	}
	
	public String toString() {
		return "1) "+this.name+"\n"
				+ "   Type: "+this.type+"\n"
				+ "   Materials: "+materialsString()+"\n"
				+ "   Skill Level: "+this.level+"\n"
				+ "   Time: "+this.timeEst+"\n"
				+ "   Description:\n"
				+ "   "+this.description+"\n";
	}
	
	public void specialPrint(String[] inventory) {
		System.out.println("1) "+this.name+"\n"
				+ "   Type: "+this.type+"\n"
				+ "   Materials:");
		for (String item: this.materials) {
			for (String thing: inventory) {
				if (item.equals(thing)) {
					System.out.print(item+" ");
				}
				else {
					System.out.print("*"+item+"* ");
				}
			}
		}
		System.out.println("   Skill Level: "+this.level+"\n"
				+ "   Time: "+this.timeEst+"\n"
				+ "   Description:\n"
				+ "   "+this.description+"\n");
		
	}
	
	private String materialsString() {
		String s = "";
		for (String obj: materials) {
			s = s+obj+" ";
		}
		return s;
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

	public void specialPrint(ArrayList<String> inventory) {
		System.out.print("1) "+this.name+"\n"
				+ "   Type: "+this.type+"\n"
				+ "   Materials:\n   ");
		for (String item: this.materials) {
			String printString = "*"+item+"* ";
			for (String thing: inventory) {
				if (item.equals(thing)) {
					printString = item+" ";
				}
			}
			System.out.print(printString);
		}
		System.out.println("\n   Skill Level: "+this.level+"\n"
				+ "   Time: "+this.timeEst+"\n"
				+ "   Description:\n"
				+ "   "+this.description+"\n");
		
	}

	public void save() {
		//adapted from: https://www.w3schools.com/java/java_files_write.asp
		try {
		      FileWriter myWriter = new FileWriter("savedCrafts.csv");
		      myWriter.write(this.line);
		      myWriter.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}
}
