package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;

/**
 * QueryDPHScoreList
 * contains a list of QueryDPHScore:
 */
public class QueryDPHScoreList implements Serializable{

	private static final long serialVersionUID = 4748136889982475711L;
	
	List<QueryDPHScore> queryDPHScoreList;

	public QueryDPHScoreList() {}
	
	public QueryDPHScoreList(List<QueryDPHScore> queryDPHScoreList) {
		super();
		this.queryDPHScoreList = queryDPHScoreList;
	}

	public List<QueryDPHScore> getQueryDPHScoreList() {
		return queryDPHScoreList;
	}

	public void setQueryDPHScoreList(List<QueryDPHScore> queryDPHScoreList) {
		this.queryDPHScoreList = queryDPHScoreList;
	}
	
	public Query getQuery() {
		return queryDPHScoreList.get(0).getQuery();
	}
}
