package craftApplication;

public interface Screen {
	//navigation bar and actions to be displayed on each screen
	String navBar = "Home (H)   Saved Crafts (S)   Inventory (I)   Catalog (C)\n";
	String navActions = "Navigation Actions:\n"
			+ "- Type ‘H’ to access the homepage\n"
			+ "- Type ‘S’ to access your saved crafts\n"
			+ "- Type ‘I’ to access your inventory\n"
			+ "- Type ‘C’ to access the catalog\n"
			+ "- Type ‘X’ to exit the application";
	//each screen should have a display
	void disp();
	//actionSelect controls what is being displayed based on user input
	Screen actionSelect(String input, Screen[] screens);
}
