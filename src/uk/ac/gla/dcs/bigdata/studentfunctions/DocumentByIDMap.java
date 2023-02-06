package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.MapFunction;
import uk.ac.gla.dcs.bigdata.studentstructures.SimplifiedNewsArticle;


/**
 * MapFunction: map SimplifiedNewsArticle to the ID of the document
 */
public class DocumentByIDMap implements MapFunction<SimplifiedNewsArticle, String> {
	
	private static final long serialVersionUID = 2401292813062918546L;

	@Override
	public String call(SimplifiedNewsArticle n) throws Exception {
		return n.getId();
	}
	
}
