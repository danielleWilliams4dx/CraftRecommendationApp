package craftApplication;

public class CraftSupply {

	private String name;
	private String type;
	
	public CraftSupply(String line) {
		String[] values = line.split(",");
        this.name = values[0].trim();
        this.type = values[1].trim();
	}
	
	public CraftSupply(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	//returns a copy of the original craft supply
	public CraftSupply copy() {
		return new CraftSupply(this.getName(), this.getType());
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getNameAndType() {
		return this.name + ", " + this.type;
	}
	
	public String toString() {
		return this.getName();
	}
	
}
