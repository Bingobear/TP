package models;

import java.util.ArrayList;

import Util.AlgorithmUtil;

/** Category Catalog (consist of the category and all its associated words)
 * @author Simon Bruns
 *
 */
public class CategoryCatalog {
private Category category;
private ArrayList<WordProperty> keywordList = new ArrayList<WordProperty>();
//Move this to calculation rather than inremental (at the end calc totalW)
private int totalW=0;
	public CategoryCatalog(Category cat, ArrayList<WordProperty> keys) {
		this.category = cat;
		this.setKeywordList(keys);
		for(WordProperty key : keys){
			this.totalW = this.totalW+key.getOcc();
		}
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public ArrayList<WordProperty> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(ArrayList<WordProperty> keywordList) {
		this.keywordList = keywordList;
	}
	public int getTotalW() {
		return totalW;
	}
	public void setTotalW(int totalW) {
		this.totalW = totalW;
	}
	
	public void incTotalW(int totalW) {
		this.totalW = this.totalW+totalW;
	}
	public void calculateTF_IDF() {
		WordProperty word = null;
		for (int ii = 0; ii < keywordList.size(); ii++) {
			word = keywordList.get(ii);
			double tf = AlgorithmUtil
					.calcTF((double) word.getOcc(), (double) this.totalW);
			word.setCatTF(tf);
			double tfidf = AlgorithmUtil.calcTFIDF(tf, (double) word.getCatIDF());
			word.setCatTFIDF(tfidf);
		}
		
	}

}
