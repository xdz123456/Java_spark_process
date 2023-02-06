package uk.ac.gla.dcs.bigdata.studentfunctions;


import java.util.HashMap;
import java.util.Iterator;

import org.apache.spark.api.java.function.MapGroupsFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryTermDPHScore;

/**
 * MapGroups function
 * calculate query DPH score and return QueryDPHScore
 */
public class QueryTermDPHScoreToQueryDPHScore implements MapGroupsFunction<Tuple2<Query, String>, QueryTermDPHScore, QueryDPHScore>{
	
	private static final long serialVersionUID = -6456363146611418557L;
	
	
	Broadcast<HashMap<String, NewsArticle>> broadcastOriginalNewsMap;
	
	
	public QueryTermDPHScoreToQueryDPHScore(Broadcast<HashMap<String, NewsArticle>> broadcastOriginalNewsMap) {
		// TODO Auto-generated constructor stub
		this.broadcastOriginalNewsMap = broadcastOriginalNewsMap;
	}

	@Override
	public QueryDPHScore call(Tuple2<Query, String> key, Iterator<QueryTermDPHScore> values) throws Exception {
		
		HashMap<String, NewsArticle> newsMap = broadcastOriginalNewsMap.value();
		
		double averageDPH = 0.0;
		int queryLength = key._1().getQueryTermCounts().length;
		Query query = key._1();
		String documentId = key._2();
		while (values.hasNext()) {
			QueryTermDPHScore value = values.next();
			Double dphScore = value.getDphScore();
			if (! Double.isNaN(dphScore)) {
				averageDPH=averageDPH+dphScore;
			}
		}
		averageDPH = averageDPH/queryLength;
		NewsArticle news = newsMap.get(documentId);
		RankedResult rankedResult = new RankedResult(documentId, news, averageDPH);
		QueryDPHScore queryDPHScore = new QueryDPHScore(query, rankedResult);
		return queryDPHScore;
	}

}
