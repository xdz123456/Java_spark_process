package uk.ac.gla.dcs.bigdata.studentfunctions;
import java.util.HashMap;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDataContainer;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryTermDPHScore;

/**
 * MapFunction
 * calculate DPH score for each query
 * Map QueryDataContainer into QueryDocumentDPHMap including documentID, Query, oneTerm, DPHScore
 */
public class QueryTermDPHScoreMap implements MapFunction<QueryDataContainer, QueryTermDPHScore> {
	
	
	Broadcast<HashMap<String, Integer>> broadcastNewsLength;
	Broadcast<Double> broadcastAverageDoumentLength;
	Broadcast<Long> broadcastTotalDocumentNum;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3662235125599618856L;

	public QueryTermDPHScoreMap(Broadcast<HashMap<String, Integer>> broadcastNewsLength, Broadcast<Double> broadcastAverageDoumentLength, Broadcast<Long> broadcastTotalDocumentNum) {
		this.broadcastNewsLength = broadcastNewsLength;
		this.broadcastAverageDoumentLength = broadcastAverageDoumentLength;
		this.broadcastTotalDocumentNum = broadcastTotalDocumentNum;
	}
	
	
	@Override
	public QueryTermDPHScore call(QueryDataContainer value) throws Exception {
		
		
		
		HashMap<String, Integer> newsLength = broadcastNewsLength.getValue();
		
		String documentID = value.getDocumentID();
		Query query = value.getQuery();
		String term = value.getQueryTerm();
		short termCountInOneDocument  = value.getTermCountInCurrentDocument();
		int termCountInAllDocuments = value.getTermCountInAllDocuments();
		int currentDocumentLength = newsLength.get(documentID);
		double averageDocumentLength = broadcastAverageDoumentLength.getValue();
		long totalDocumentNum = broadcastTotalDocumentNum.getValue();
		
		double dphScore = DPHScorer.getDPHScore(termCountInOneDocument, termCountInAllDocuments, currentDocumentLength, averageDocumentLength, totalDocumentNum);
		return new QueryTermDPHScore(documentID, query, term, dphScore);
		
	}
	
		
		
	
}
