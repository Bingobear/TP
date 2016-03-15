package modules;

public class InvalidPDF extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidPDF () {
        super("No valid PDF data found!");
        System.out.println("No valid PDF data found!");
    }

}
