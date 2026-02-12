package craftApplication;

public class HomeScreen implements Screen{
	//product name and slogan are final because they shouldn't change while running
	final String prodName = "Craft Overflow";
	final String slogan = "Personalized crafting starts here.";
	
	//display function
	public void disp() {
		System.out.println(navBar);
		System.out.println(prodName);
		System.out.println("______________\n");
		System.out.println(slogan+"\n\n");
		System.out.println(navActions+"\n");
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
		return screens[0];
	}
}
