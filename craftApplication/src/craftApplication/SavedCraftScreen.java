package craftApplication;

public class SavedCraftScreen implements Screen {

	String savedCraftActions = "Saved Craft Actions:\n"
			+ "- Type ‘V’ to view a craft\n";
	
	public void disp() {
		System.out.println(navBar);
		System.out.println("Saved Crafts");
		System.out.println("_________\n");
		System.out.println("Additional supplies that you need are *starred*\n");
		printCrafts();
		System.out.println(navActions+"\n");
		System.out.println(savedCraftActions+"\n");
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
		return screens[3];
	}
	
	public void printCrafts() {
		
	}

}
