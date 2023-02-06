package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScoreList;

/**
 * MapFunction
 * Map QueryDPHScoreList into Query(key)
 * Used for groupByKey function
 */
public class QueryDPHScoreByQuery implements MapFunction<QueryDPHScoreList, Query> {
	private static final long serialVersionUID = -5834681774470307919L;

	@Override
	public Query call(QueryDPHScoreList value) throws Exception {
		return value.getQuery();
	}

}
