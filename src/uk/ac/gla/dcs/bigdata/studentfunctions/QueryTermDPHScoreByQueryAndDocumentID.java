package uk.ac.gla.dcs.bigdata.studentfunctions;


import org.apache.spark.api.java.function.MapFunction;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryTermDPHScore;


/**
 * MapFunction
 * Map QueryTermDPHScore into Tuple2<Query, DocumentID>(key)
 * Used for groupByKey function
 */
public class QueryTermDPHScoreByQueryAndDocumentID implements MapFunction<QueryTermDPHScore, Tuple2<Query, String>> {


	private static final long serialVersionUID = -7319704420174241448L;

	@Override
	public Tuple2<Query, String> call(QueryTermDPHScore value) throws Exception {
		return new Tuple2<Query, String>(value.getQuery(), value.getDocumentID());
	}

}
