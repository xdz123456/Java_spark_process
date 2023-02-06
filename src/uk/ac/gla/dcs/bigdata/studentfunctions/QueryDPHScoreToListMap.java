package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScoreList;

/**
 * MapFunction
 * Convert QueryDPHScore to QueryDPHScoreList
 */
public class QueryDPHScoreToListMap implements MapFunction<QueryDPHScore,QueryDPHScoreList> {

	private static final long serialVersionUID = -1446626513915191944L;

	@Override
	public QueryDPHScoreList call(QueryDPHScore queryDPHScore) throws Exception {
		List<QueryDPHScore> asList = new ArrayList<QueryDPHScore>(1);
		asList.add(queryDPHScore);
		return new QueryDPHScoreList(asList);
	}

}
