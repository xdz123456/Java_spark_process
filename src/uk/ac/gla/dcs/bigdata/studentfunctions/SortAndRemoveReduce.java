package uk.ac.gla.dcs.bigdata.studentfunctions;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.spark.api.java.function.ReduceFunction;

import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScoreList;

/**
 * ReduceFunction
 * sort QueryDPHScore by DPH score from highest to lower and remove QueryDPHScore with similar document title and lower DPH score
 * return QueryDPHScore with top 10 DPH scores
 */
public class SortAndRemoveReduce implements ReduceFunction<QueryDPHScoreList>{

	private static final long serialVersionUID = -7863523712057591319L;

	@Override
	public QueryDPHScoreList call(QueryDPHScoreList v1, QueryDPHScoreList v2) throws Exception {
		
		
		//sort
		List<QueryDPHScore> v1List = v1.getQueryDPHScoreList();
		List<QueryDPHScore> v2List = v2.getQueryDPHScoreList();
		int v1Size = v1List.size();
		int v2Size = v2List.size();
		int count1 = 0;
		int count2 = 0;
		QueryDPHScore q1;
		QueryDPHScore q2;
		List<QueryDPHScore> sortedResult = new ArrayList<QueryDPHScore>(v1Size+v2Size);
		while(count1<v1Size &&  count2<v2Size ) {
			q1 =  v1List.get(count1);
			q2 =  v2List.get(count2);
			if(q1.getRankedResult().getScore() > q2.getRankedResult().getScore()) {
				sortedResult.add(q1);
				count1+=1;
			}
			else {
				sortedResult.add(q2);
				count2+=1;
			}
		}
		
		if( count1 == v1Size) {
			sortedResult.addAll(v2List.subList(count2, v2Size));
		}
		else {
			sortedResult.addAll(v1List.subList(count1, v1Size));
		}
		
		
		//remove news with the similar title
		LinkedHashSet<QueryDPHScore> filteredResult = new LinkedHashSet<QueryDPHScore>();
		for(int i=0; i<sortedResult.size()-1; i++) {
			QueryDPHScore queryDPHScore1 = sortedResult.get(i);
			for(int j=i+1; j<sortedResult.size(); j++) {
				QueryDPHScore queryDPHScore2 = sortedResult.get(j);
				double titleDistance = TextDistanceCalculator.similarity(queryDPHScore1.getRankedResult().getArticle().getTitle(), queryDPHScore2.getRankedResult().getArticle().getTitle());
				if(titleDistance<0.5) {
					int compareResult = queryDPHScore1.getRankedResult().compareTo(queryDPHScore2.getRankedResult());
					if( compareResult > 0) {
						filteredResult.add(queryDPHScore1);
					}
					else {
						filteredResult.add(queryDPHScore2);
					}
				}
				else {
					filteredResult.add(queryDPHScore1);
					filteredResult.add(queryDPHScore2);
				}
			}
		}
		
		ArrayList<QueryDPHScore> filteredResultList = new ArrayList<QueryDPHScore>(filteredResult);
		//only return top 10 results
		if(filteredResultList.size() > 10) 
			return new QueryDPHScoreList(filteredResultList.subList(0, 10));
		return new QueryDPHScoreList(filteredResultList);
		
	}
}


