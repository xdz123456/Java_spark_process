package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;


/**
 * QueryTermDPHScore
 * contains four attributes
 * a string of the document ID
 * a Query
 * a string of the query term
 * a double of DPH Score
 */
public class QueryTermDPHScore implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5865636534275666704L;
	
	String documentID;
	Query query; // 
	String queryTerm;
	Double dphScore;
	
	public QueryTermDPHScore() {

	}
	
	public QueryTermDPHScore(String documentID, Query query, String queryTerm, Double dphScore) {
		super();
		this.documentID = documentID;
		this.query = query;
		this.queryTerm = queryTerm;
		this.dphScore = dphScore;

	}

	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getQueryTerm() {
		return queryTerm;
	}

	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}

	public Double getDphScore() {
		return dphScore;
	}

	public void setDphScore(Double dphScore) {
		this.dphScore = dphScore;
	}

	
	
}
