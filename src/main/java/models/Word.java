package models;

import java.util.ArrayList;

/**
 * Word model class
 * 
 * @author Simon Bruns
 *
 */

public class Word {
	private String text;
	private String stem;
	private String type;
	private ArrayList<Category> category;

	
	public Word() {
	}
	
	public Word(String text, String stem, String type, ArrayList<Category> cat) {
		this.text = text;
		this.stem = stem;
		this.type = type;
		this.category = cat;
	}

	public Word(Word word) {
		this.text = word.text;
		this.stem = word.stem;
		this.type = word.type;
		this.category = word.category;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStem() {
		return stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Category> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<Category> category) {
		this.category = category;
	}


}
