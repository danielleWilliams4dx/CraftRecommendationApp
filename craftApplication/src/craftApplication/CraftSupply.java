package craftApplication;

import java.util.Objects;

// CraftSupply now represents only ONE supply item
// it's used for: 1. catalog entries loaded from materialcatalog.csv
// 2. inventory entries loaded from inventory.csv

//class supports optional attributes for: color, qnt, size

//also supports catalog only prompt flags: needsColor, needsQuantity, needsSize

public class CraftSupply {

	private String name;
	private String type;
	
	//Flags that are only meaningful for the catalog(materialcatalog.csv)
	private boolean needsColor;
	private boolean needsQuantity;
	private boolean needsSize;
	
	//Values that are only meaningful when the item is added to the inv.
	private String color;
	private String quantity;
	private String size;
	
	//Constructor for materialcatalog rows
	//Expected format: name,category,needsColor,needsQuantity,needsSize
	public CraftSupply(String line) {
		String[] values = line.split(",");
		
        this.name = values[0].trim();
        this.type = values[1].trim();
        
        this.needsColor = values.length > 2 && Boolean.parseBoolean(values[2].trim());
        this.needsQuantity = values.length > 3 && Boolean.parseBoolean(values[3].trim());
        this.needsSize = values.length > 4 && Boolean.parseBoolean(values[4].trim());
        
        this.color = "";
        this.quantity = "";
        this.size = "";
	}
	
	//Constructor for simple inv items with no extra attribuutes
	public CraftSupply(String name, String type) {
		this.name = name;
		this.type = type;
		this.needsColor = false;
		this.needsQuantity = false;
		this.needsSize = false;
		this.color = "";
		this.quantity = "";
		this.size = "";
	}
	
	//Constructor for inv items with optional attributes chosen
	public CraftSupply (String name, String type, String color, String quantity, String size) {
		this.name = name;
		this.type = type;
		this.needsColor = false;
		this.needsQuantity = false;
		this.needsSize = false;
		this.color = color == null ? "" : color.trim();
		this.quantity = quantity == null ? "" : quantity.trim();
		this.size = size == null ? "" : size.trim();
	}
	
	//returns a copy of the original craft supply
	public CraftSupply copy() {
		CraftSupply copy = new CraftSupply(this.name, this.type, this.color, this.quantity, this.size);
		copy.needsColor = this.needsColor;
		copy.needsQuantity = this.needsQuantity;
		copy.needsSize = this.needsSize;
		return copy;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public boolean needsColor() {
		return this.needsColor;
	}
	
	public boolean needsQuantity() {
		return this.needsQuantity;
	}
	
	public boolean needsSize() {
		return this.needsSize;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public String getQuantity() {
		return this.quantity;
	}
	
	public String getSize() {
		return this.size;
	}
	
	public String getNameAndType() {
		return this.name + ", " + this.type;
	}
	
	//used when printing a catalog entry with a selected number 
	public String toStringWithIndex(int index) {
		return "   " + index + ") " + this.name;
	}
	
	//used for printing inv. items
	//shows extra attributes only when they exist
	public String toString() {
		String result = this.name;
		
		if (!color.isEmpty()) {
			result += " | Color: " + color;
		}
		if (!quantity.isEmpty()) {
			result += " | Qty: " + quantity;
		}
		if (!size.isEmpty()) {
			result += " | Size: " + size;
		}
		
		return result;
	}
	
	//converts the item to a line that can be written into inv.csv
	//inventory.csv stays tab-seperated so Inventory.java can reload it 
	public String toInventoryFileLine() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\t").append(type);
		
		if (!color.isEmpty()) {
			sb.append("\tColor: ").append(color);
		}
		if (!quantity.isEmpty()) {
			sb.append("\tQty: ").append(quantity);
		}
		if (!size.isEmpty()) {
			sb.append("\tSize: ").append(size);
		}
		
		return sb.toString();
	}
	
	//Duplicate prevention: 
	//two supplies are considereed the same if name, type, and all attributes match
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		CraftSupply other = (CraftSupply) obj;
		
		return name.equalsIgnoreCase(other.name) 
				&& type.equalsIgnoreCase(other.type)
				&& color.equalsIgnoreCase(other.color) 
				&& quantity.equalsIgnoreCase(other.quantity) 
				&& size.equalsIgnoreCase(other.size);
				
	}
	
	@Override 
	public int hashCode() {
		return Objects.hash(name.toLowerCase(), type.toLowerCase(), color.toLowerCase(), 
				quantity.toLowerCase(), size.toLowerCase()
				);
	
	}
	
}