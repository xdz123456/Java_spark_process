package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.MapFunction;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDataContainer;

/**
 * MapFunction
 * Map QueryDataContainer to QueryTerm(key)
 * Used for GroupByKey Function
 */
public class QueryDataContainerByQueryAndTerm implements MapFunction<QueryDataContainer, Tuple2<Query, String>> {

	private static final long serialVersionUID = -6273351406858601276L;

	@Override
	public Tuple2<Query, String> call(QueryDataContainer value) throws Exception {
		return new Tuple2<Query, String>(value.getQuery(), value.getQueryTerm());
	}

}
