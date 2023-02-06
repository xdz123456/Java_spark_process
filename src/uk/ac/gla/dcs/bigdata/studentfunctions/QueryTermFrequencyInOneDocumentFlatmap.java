package uk.ac.gla.dcs.bigdata.studentfunctions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDataContainer;


/**
 * FlatMapFunction
 * Map documentID to QueryDataContainer that contains DocumentID, Query, QueryTerm, QueryTermCountInOneDocument, QueryTermCountInAllDocuments(0)
 */
public class QueryTermFrequencyInOneDocumentFlatmap implements FlatMapFunction<String, QueryDataContainer>{

	private static final long serialVersionUID = -4277450212763344068L;

	Broadcast<List<Query>> broadcastQueries;
	Broadcast<HashMap<String, List<String>>> broadcastNews;

	public QueryTermFrequencyInOneDocumentFlatmap(Broadcast<List<Query>> broadcastQueries, Broadcast<HashMap<String, List<String>>> broadcastNews) {
		this.broadcastQueries = broadcastQueries;
		this.broadcastNews = broadcastNews;
	}
	
	@Override
	public Iterator<QueryDataContainer> call(String documentID) throws Exception {
		
		HashMap<String, List<String>> news = broadcastNews.getValue();
		List<Query> queries = broadcastQueries.getValue();
		
		List<QueryDataContainer> result = new ArrayList<QueryDataContainer>();
		for(Query q: queries) {
			for(String queryTerm: q.getQueryTerms()) {
				short termCount = 0;
				for(String documentTerm: news.get(documentID)) {
					if (queryTerm.equals(documentTerm)) {
						termCount += 1;
					}
				}
				result.add(new QueryDataContainer(documentID, q, queryTerm, termCount, 0));
			}
		}
		
		return result.iterator();
		
	}
	
	

}

