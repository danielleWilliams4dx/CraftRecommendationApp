package craftApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
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
	public String toStringWithIndex(int index, Inventory inv) {
		return index + ")" + specialPrint(inv);
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
	
	//Star each missing item only once (no duplicates)
	public String specialPrint(Inventory inv) {
		ArrayList<String> justItemNames = inv.getJustItemNames();
		String s = " " + this.name + "\n"
				+ "   Type: " + this.type + "\n"
				+ "   Materials:\n   ";
		
		for (String item: this.materials) {
			String cleaned = item.trim();
			if (justItemNames.contains(cleaned.toLowerCase())) {
				s += cleaned + " ";
			} else {
				s += "*" + cleaned + "* ";
			}

		}
		

		s +=  "\n   Skill Level: " + this.level + "\n"
				+ "   Time: " + this.timeEst + "\n"
				+ "   Description:\n"
				+ "   "+ this.description+"\n";
		
		return s;
		
	}

	public void save() {
		//adapted from: https://www.w3schools.com/java/java_files_write.asp
		try {
		      FileWriter myWriter = new FileWriter("savedCrafts.csv", true);
		      if(!craftAlreadySaved(this.line)) {
		    	  	myWriter.append(this.line + System.lineSeparator());
		      }
		      myWriter.close();
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
	}
	
	private boolean craftAlreadySaved(String line) {
		String filePath = "savedCrafts.csv";
		String currLine;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while((currLine = br.readLine()) != null) {
				if (currLine.trim().isEmpty()) continue;
				if(currLine.equals(line)) {
					return true;
				}
			}
		} catch (IOException e) {}
		
		return false;
	}
}
