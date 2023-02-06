package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;


/**
 * QueryDPHScore
 * contains two attributes:
 * a Query
 * a RankedResult
 */
public class QueryDPHScore implements Serializable {

	private static final long serialVersionUID = 1302135955188231585L;
	
	Query query; // 
	RankedResult rankedResult;
	
	public QueryDPHScore() {

	}
	
	public QueryDPHScore(Query query, RankedResult rankedResult) {
		super();
		this.query = query;
		this.rankedResult = rankedResult;

	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public RankedResult getRankedResult() {
		return rankedResult;
	}

	public void setRankedResult(RankedResult rankedResult) {
		this.rankedResult = rankedResult;
	}
	
	




	

	


	
	
}
