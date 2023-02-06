package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.util.LongAccumulator;

import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;
import uk.ac.gla.dcs.bigdata.studentstructures.SimplifiedNewsArticle;

/**
 * FlatMapFunction
 * Converts a News to a simple version SimplifiedNewsArticle,
 * Only contain an id, title, total terms (title terms, content terms), and total term count
 */
public class NewsSimplifyMap implements FlatMapFunction<NewsArticle, SimplifiedNewsArticle> {


	private static final long serialVersionUID = 4356536776369428912L;
	private transient TextPreProcessor processor;
	
	LongAccumulator lengthAccumulator;
	LongAccumulator documentNumAccumulator;
	
	public NewsSimplifyMap(LongAccumulator lengthAccumulator, LongAccumulator documentNumAccumulator) {
		// TODO Auto-generated constructor stub
		this.lengthAccumulator = lengthAccumulator;
		this.documentNumAccumulator = documentNumAccumulator;
	}

	@Override
	public Iterator<SimplifiedNewsArticle> call(NewsArticle news) throws Exception {
		if (processor==null) processor = new TextPreProcessor();
		

		// Init the variable
		String id = news.getId();
		String title = news.getTitle();
		if(title == null) {
			List<SimplifiedNewsArticle> newsList  = new ArrayList<SimplifiedNewsArticle>(0); // create an empty array of size 0
			lengthAccumulator.add(0);
			documentNumAccumulator.add(0);
			return newsList.iterator(); // return the iter
		}
		
		
		int paragraph_counter = 0;
		StringBuilder contentBuilder = new StringBuilder();
		// Get the paragraph list, only consider first 5
		for(ContentItem ci : news.getContents()) {
			if(ci.getSubtype() != null && ci.getSubtype().equals("paragraph")) {
				paragraph_counter += 1;
				contentBuilder.append(ci.getContent()).append(" ");
			}
			if(paragraph_counter == 5) {
				break;
			}
		}
		
		// Remove stops words and do stem
		String content = contentBuilder.toString();
		List<String> contentTerms = processor.process(content);
		List<String> titleTerms = processor.process(title);
		
		//add contentTerms and titleTerms together into totalTerms
		List<String> totalTerms = new ArrayList<String>(titleTerms);
		totalTerms.addAll(contentTerms);

		//count term number
		int termCount = totalTerms.size();
		// Return the Simplified news article
		lengthAccumulator.add(termCount);
		documentNumAccumulator.add(1);
		
		List<SimplifiedNewsArticle> newsList  = new ArrayList<SimplifiedNewsArticle>(1);
		newsList.add(new SimplifiedNewsArticle(id, title, totalTerms, termCount));
		
		return newsList.iterator();
	}

	
}
