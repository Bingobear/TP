package models;



import java.util.ArrayList;

/**
 * Publication model class
 * 
 * @author Simon Bruns
 *
 */

public class Publication {
private int pubID;
private String title;

	public Publication(int id, String title) {
		this.pubID = id;
		this.title=title;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPubID() {
		return pubID;
	}
	public void setPubID(int pubID) {
		this.pubID = pubID;
	}


}
