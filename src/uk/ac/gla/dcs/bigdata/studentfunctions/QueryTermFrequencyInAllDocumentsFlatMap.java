package uk.ac.gla.dcs.bigdata.studentfunctions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapGroupsFunction;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDataContainer;

/**
 * FlatMapFunction
 * calculate a query term count in all documents and update QueryDataContainer
 */
public class QueryTermFrequencyInAllDocumentsFlatMap implements FlatMapGroupsFunction<Tuple2<Query, String>,QueryDataContainer,QueryDataContainer>{

	
	private static final long serialVersionUID = -484810270146328326L;
	
	
	@Override
	public Iterator<QueryDataContainer> call(Tuple2<Query, String> key, Iterator<QueryDataContainer> values) throws Exception {
		
		List<QueryDataContainer> result = new ArrayList<QueryDataContainer>();
		List<QueryDataContainer> result2 = new ArrayList<QueryDataContainer>();
		
		int count = 0;
		while (values.hasNext()) {
			QueryDataContainer value = values.next();
			count += value.getTermCountInCurrentDocument();
			result.add(new QueryDataContainer(value.getDocumentID(), value.getQuery(), value.getQueryTerm(), value.getTermCountInCurrentDocument(), 0));
		}
		
		for(QueryDataContainer value2 : result) {
			result2.add(new QueryDataContainer(value2.getDocumentID(), value2.getQuery(), value2.getQueryTerm(), value2.getTermCountInCurrentDocument(), count));
			
		}
			
		return result2.iterator();
	}

}
