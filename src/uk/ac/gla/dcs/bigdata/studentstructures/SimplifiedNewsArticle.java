package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

/**
 * SimplifiedNewsArticle
 * contains four attributes
 * a string of the document ID
 * a string of the document title
 * a list of terms
 * a integer of term numbers
 */
public class SimplifiedNewsArticle implements Serializable {

	private static final long serialVersionUID = -2117451147053047957L;

	
	String id;
	String title; 
	List<String> totalTerms;
	int termCount;

	
	public SimplifiedNewsArticle(String id, String title, List<String> totalTerms, int termCount ) {
		super();
		this.id = id;
		this.title = title;
		this.totalTerms = totalTerms;
		this.termCount = termCount;
	}


	public SimplifiedNewsArticle() {

	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}




	public List<String> getTotalTerms() {
		return totalTerms;
	}


	public void setTotalTerms(List<String> totalTerms) {
		this.totalTerms = totalTerms;
	}




	public int getTermCount() {
		return termCount;
	}




	public void setTermCount(int termCount) {
		this.termCount = termCount;
	}


	


	
	
	
}
