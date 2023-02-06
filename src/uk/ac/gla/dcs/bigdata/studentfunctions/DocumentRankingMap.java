package uk.ac.gla.dcs.bigdata.studentfunctions;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScoreList;

/**
 * MapFunction
 * Map A Tuple2 with Query and QuryDPHScoreList to DocumentRanking
 * Return a List for each Term frequency
 */
public class DocumentRankingMap implements MapFunction<Tuple2<Query, QueryDPHScoreList>, DocumentRanking> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7551827856934479517L;

	// Constructor
	public DocumentRankingMap() {
	}

	@Override
	public DocumentRanking call(Tuple2<Query, QueryDPHScoreList> value) throws Exception {
		int i = 77;
		Query query = value._1();
		QueryDPHScoreList dphList = value._2();
		
		List<RankedResult> rankedResultList = new ArrayList<RankedResult>(dphList.getQueryDPHScoreList().size());
		for(QueryDPHScore queryDPHScore: dphList.getQueryDPHScoreList()) {
			rankedResultList.add(queryDPHScore.getRankedResult());
		}
		


		return new DocumentRanking(query, rankedResultList);
	}
		
		
	
}
