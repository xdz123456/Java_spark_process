package uk.ac.gla.dcs.bigdata.apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.KeyValueGroupedDataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;

import scala.Tuple2;
import uk.ac.gla.dcs.bigdata.providedfunctions.NewsFormaterMap;
import uk.ac.gla.dcs.bigdata.providedfunctions.QueryFormaterMap;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentfunctions.DocumentByIDMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.DocumentRankingMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.NewsSimplifyMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryDPHScoreByQuery;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryDPHScoreToListMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryDataContainerByQueryAndTerm;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryTermDPHScoreByQueryAndDocumentID;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryTermDPHScoreMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryTermDPHScoreToQueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryTermFrequencyInAllDocumentsFlatMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.QueryTermFrequencyInOneDocumentFlatmap;
import uk.ac.gla.dcs.bigdata.studentfunctions.SortAndRemoveReduce;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDPHScoreList;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryDataContainer;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryTermDPHScore;
import uk.ac.gla.dcs.bigdata.studentstructures.SimplifiedNewsArticle;

/**
 * This is the main class where your Spark topology should be specified.
 * 
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by
 * the spark.master environment variable.
 * @author Richard
 *
 */
public class AssessedExercise {

	
	public static void main(String[] args) {
		
		
		
		// The code submitted for the assessed exerise may be run in either local or remote modes
		// Configuration of this will be performed based on an environment variable
		String sparkMasterDef = System.getenv("SPARK_MASTER");
		if (sparkMasterDef==null) {
			File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get an absolute path for it
			System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark finds it
			sparkMasterDef = "local[2]"; // default is local mode with two executors
		}
		
		String sparkSessionName = "BigDataAE"; // give the session a name
		
		// Create the Spark Configuration 
		SparkConf conf = new SparkConf()
				.setMaster(sparkMasterDef)
				.setAppName(sparkSessionName);
		
		// Create the spark session
		SparkSession spark = SparkSession
				  .builder()
				  .config(conf)
				  .getOrCreate();
	
		
		// Get the location of the input queries
		String queryFile = System.getenv("BIGDATA_QUERIES");
		if (queryFile==null) queryFile = "data/queries.list"; // default is a sample with 3 queries
		
		// Get the location of the input news articles
		String newsFile = System.getenv("BIGDATA_NEWS");
		if (newsFile==null) newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles
		
		// Call the student's code
		List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);
		
		// Close the spark session
		spark.close();
		
		String out = System.getenv("BIGDATA_RESULTS");
		String resultsDIR = "results/";
		if (out!=null) resultsDIR = out;
		
		// Check if the code returned any results
		if (results==null) System.err.println("Topology return no rankings, student code may not be implemented, skiping final write.");
		else {
			
			// Write the ranking for each query as a new file
			for (DocumentRanking rankingForQuery : results) {
				rankingForQuery.write(new File(resultsDIR).getAbsolutePath());
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(resultsDIR).getAbsolutePath()+"/SPARK.DONE")));
			writer.write(String.valueOf(System.currentTimeMillis()));
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
			
		
	
	
	
	
	public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {
		
		// Load queries and news articles
		Dataset<Row> queriesjson = spark.read().text(queryFile);
		Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article
		
		// Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
		Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
		Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle

		//----------------------------------------------------------------
		// Your Spark Topology should be defined here
		//----------------------------------------------------------------
		
		
		// Set an accumulator to calculate the document total length
		LongAccumulator lengthAccumulator = spark.sparkContext().longAccumulator();
		// Set an accumulator to calculate document total number
		LongAccumulator documentNumAccumulator = spark.sparkContext().longAccumulator();
		
		// Simplify the news and count document total length and document total number
		NewsSimplifyMap newsSimlifyMapFunction = new NewsSimplifyMap(lengthAccumulator, documentNumAccumulator);
		Dataset<SimplifiedNewsArticle> simplifiedNews = news.flatMap(newsSimlifyMapFunction, Encoders.bean(SimplifiedNewsArticle.class));
		
		//Make four hashmaps for easy search and retrieve information
		//HashMap(originalNewsMap): Key is the string of document ID, Values is NewArticle type obeject
		HashMap<String, NewsArticle> originalNewsMap = new HashMap<String, NewsArticle>();
		for (NewsArticle n : news.collectAsList()) {
			String id = n.getId();
			originalNewsMap.put(id, n);
		}
		//HashMap(newsTermsMap): Key is the string of document id, Values is a list of all terms contain title and content
		HashMap<String, List<String>> newsTermsMap = new HashMap<String, List<String>>();
		//HashMap(newsTitleMap): Key is the string of document id, Values is the string of document title
		HashMap<String, String> newsTitleMap = new HashMap<String, String>();
		//HashMap(newsTitleMap): Key is the string of document id, Values is the string of document length
		HashMap<String, Integer> newsLengthMap = new HashMap<String, Integer>();
		for (SimplifiedNewsArticle s_news : simplifiedNews.collectAsList()) {
			newsTermsMap.put(s_news.getId(), s_news.getTotalTerms());
			newsTitleMap.put(s_news.getId(), s_news.getTitle());
			newsLengthMap.put(s_news.getId(), s_news.getTermCount());
		}
		
		// Get document total length
		Long documentTotalLength = lengthAccumulator.value();
		// Get document number
		Long documentNum = documentNumAccumulator.value();
		// Get document average length
		double averageDocumentLength = documentTotalLength/documentNum;

		//Set broadcast variables
		//broadcast variable for originalNewsMap
		Broadcast<HashMap<String, NewsArticle>> broadcastOriginalNewsMap = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(originalNewsMap);
		//broadcast variable for query list 
		Broadcast<List<Query>> broadcastQueries = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(queries.collectAsList());
		//broadcast variable for newsTermsMap
		Broadcast<HashMap<String, List<String>>> broadcastNewsTerms = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(newsTermsMap);
		//broadcast variable for newsLengthMap
		Broadcast<HashMap<String, Integer>> broadcastNewsLength = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(newsLengthMap);
		//broadcast variable for averageDocumentLength
		Broadcast<Double> broadcastAverageDoumentLength = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(averageDocumentLength);
		//broadcast variable for documentNum
		Broadcast<Long> broadcastTotalDocumentNum = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(documentNum);
		
		//Get a dataset of documentID
		Dataset<String> documentIDs = simplifiedNews.map(new DocumentByIDMap(), Encoders.STRING());
		
		//Get a dataset of QueryDataContainer about each query term frequency in one specific single document
		QueryTermFrequencyInOneDocumentFlatmap queryDataContainerInOneDocumentFlatmapFunction = new QueryTermFrequencyInOneDocumentFlatmap(broadcastQueries, broadcastNewsTerms);
		Dataset<QueryDataContainer> queryDataContainerDataset = documentIDs.flatMap(queryDataContainerInOneDocumentFlatmapFunction, Encoders.bean(QueryDataContainer.class));
					
		//Update a dataset of QueryDataContainer and add each query term frequency in all documents
		KeyValueGroupedDataset<Tuple2<Query, String>, QueryDataContainer> queryDataContainerByQueryAndQueryTerm = queryDataContainerDataset.groupByKey(new QueryDataContainerByQueryAndTerm(), Encoders.tuple(Encoders.bean(Query.class), Encoders.STRING()));
		Dataset<QueryDataContainer> queryTermCountInAllDocuments = queryDataContainerByQueryAndQueryTerm.flatMapGroups(new QueryTermFrequencyInAllDocumentsFlatMap(), Encoders.bean(QueryDataContainer.class));
		
		//Get a dataset of QueryTermDPHScore about the DPH score of every term in one query 
		QueryTermDPHScoreMap queryTermDPHScoreMapFunction = new QueryTermDPHScoreMap(broadcastNewsLength, broadcastAverageDoumentLength, broadcastTotalDocumentNum);
		Dataset<QueryTermDPHScore> queryTermDPHScoreDatabase = queryTermCountInAllDocuments.map(queryTermDPHScoreMapFunction, Encoders.bean(QueryTermDPHScore.class));
		
		//Get a dataset of QueryDPHScore about the DPH score of every query
		KeyValueGroupedDataset<Tuple2<Query, String>, QueryTermDPHScore> queryTermDPHScoreByQueryAndDocumentID = queryTermDPHScoreDatabase.groupByKey(new QueryTermDPHScoreByQueryAndDocumentID(), Encoders.tuple(Encoders.bean(Query.class), Encoders.STRING()));
		Dataset<QueryDPHScore> queryDPHScore = queryTermDPHScoreByQueryAndDocumentID.mapGroups(new QueryTermDPHScoreToQueryDPHScore(broadcastOriginalNewsMap), Encoders.bean(QueryDPHScore.class) );
		
		//Sort and Remove similar news
		Dataset<QueryDPHScoreList> queryDPHScoreList = queryDPHScore.map(new QueryDPHScoreToListMap(), Encoders.bean(QueryDPHScoreList.class));
		KeyValueGroupedDataset<Query, QueryDPHScoreList> queryDPHScoreListByQuery = queryDPHScoreList.groupByKey(new QueryDPHScoreByQuery(), Encoders.bean(Query.class));
		Dataset<Tuple2<Query, QueryDPHScoreList>> sortedDocumentRankingResult = queryDPHScoreListByQuery.reduceGroups(new SortAndRemoveReduce());
		
		//convert into a dataset of DocumentRanking
		Dataset<DocumentRanking> documentRankingResult = sortedDocumentRankingResult.map(new DocumentRankingMap(), Encoders.bean(DocumentRanking.class));
		
		//covert DocumentRanking dataset to DocumentRanking list (action)
		List<DocumentRanking> documentRankingResultList = documentRankingResult.collectAsList();
		
		return documentRankingResultList; // replace this with the the list of DocumentRanking output by your topology
	}
	
	
}
