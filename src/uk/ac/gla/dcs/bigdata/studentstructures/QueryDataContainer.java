package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;

/**
 * QueryDataContainer
 * contains five attributes:
 * a document ID
 * a Query
 * a String of query term String
 * a short of query term count in the current document
 * an integer of query term count in the all documents
 */
public class QueryDataContainer implements Serializable {

	private static final long serialVersionUID = -4242628168683373557L;
	
	String documentID;
	Query query; // 
	String queryTerm;
	short termCountInCurrentDocument;
	int termCountInAllDocuments;
	
	
	public QueryDataContainer() {

	}
	
	public QueryDataContainer(String documentID, Query query, String queryTerm, short termCountInCurrentDocument, int termCountInAllDocuments) {
		super();
		this.documentID = documentID;
		this.query = query;
		this.queryTerm = queryTerm;
		this.termCountInCurrentDocument = termCountInCurrentDocument;
		this.termCountInAllDocuments = termCountInAllDocuments;

	}

	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getQueryTerm() {
		return queryTerm;
	}

	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}

	public short getTermCountInCurrentDocument() {
		return termCountInCurrentDocument;
	}

	public void setTermCountInCurrentDocument(short termCountInCurrentDocument) {
		this.termCountInCurrentDocument = termCountInCurrentDocument;
	}

	public int getTermCountInAllDocuments() {
		return termCountInAllDocuments;
	}

	public void setTermCountInAllDocuments(int termCountInAllDocuments) {
		this.termCountInAllDocuments = termCountInAllDocuments;
	}

	
	




	

	


	
	
}
