package com.tdb.back.Indice;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class IndiceInvertido {
	
	private ArrayList<String> tweets;
	
	public void crear() {
		try {
			MongoClient mongo= new MongoClient(new MongoClientURI("mongodb://kimbo:feseja64@206.189.184.79:23305/twitter"));
			DB db= mongo.getDB("twitter");
			DBCollection col=(DBCollection) db.getCollection("statusJSONImpl");
			DBCursor cur=col.find();
			System.out.println("ESTOS TWEETS LLEVO"+col.count());
			
			System.out.println(1);
			
			 Directory dir = FSDirectory.open(Paths.get("indice/"));
			 System.out.println(2);
		     Analyzer analyzer = new StandardAnalyzer();
		     System.out.println(3);
		     IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		     System.out.println(4);
		     iwc.setOpenMode(OpenMode.CREATE);
		     System.out.println(5);
		     IndexWriter writer = new IndexWriter(dir, iwc);
		     System.out.println(7);
		     Document document=null;
		     System.out.println(6);
		     
		     while(cur.hasNext()) {
		    	 document= new Document();
		    	 DBObject elemento=cur.next();
		    	 
		    	 
		    	 document.add(new StringField("id",elemento.get("_id").toString(),Field.Store.YES));
		    	 document.add(new TextField("text",elemento.get("text").toString(),Field.Store.YES));
		    	if(elemento.containsField("locationUser")){
		    		if(elemento.get("locationUser")!=null){
		    		// System.out.println("entre aqui");
		    		 document.add(new TextField("pais",elemento.get("locationUser").toString(),Field.Store.YES));
		    		}
		    	}
		    	if(elemento.containsField("name")) {
		    		if(elemento.get("name")!=null) {
		    			document.add(new TextField("name",elemento.get("name").toString(),Field.Store.YES));
		    		}
		    	}
		    	
		    	 
		    	 
		    	 if(writer.getConfig().getOpenMode() == OpenMode.CREATE) {
    				//System.out.println("Indexando el archivo: "+elemento.get("_id"));
    				 //System.out.println("cuyo texto es: "+elemento.get("text"));
    				 writer.addDocument(document);
    			 }
		    	 else {
    				 writer.updateDocument(new Term("text"+elemento.get("text")), document);
    			 }
		     }
		     writer.close();		     
		}
		catch(IOException ioe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, ioe);
		}		
	}
	
	public ArrayList<String> buscar(String consulta) {
		
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indice")));
			IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new StandardAnalyzer();
		    
		    QueryParser parser = new QueryParser("text", analyzer);
		    Query query = parser.parse(consulta);
		  
		    TopDocs results = searcher.search(query,999999999);
		    ScoreDoc[] hits = results.scoreDocs;
		   this.tweets=new ArrayList<String>();
		    
		    for(int i = 0; i < hits.length; i++) {
		    	Document doc = searcher.doc(hits[i].doc);
		    	this.tweets.add(doc.get("text"));
		    	String id = doc.get("id");
		    	//System.out.println((i+1)+".- score="+hits[i].score + " doc="+hits[i].doc+" id="+id);
		    	
		    	//System.out.println(doc.get("text"));
		    }
		    reader.close();
		    
		}
		catch(IOException ioe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, ioe);
		}
		catch(ParseException pe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, pe);
		}
		return tweets;
	}
	
	public ArrayList<String> buscarPorPais(String videoGame,String pais) {
		
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indice")));
			IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new StandardAnalyzer();
		    
		    QueryParser parser = new QueryParser("text", analyzer);
		    Query query = parser.parse("(text:"+videoGame+") AND (pais:"+pais+")");
		  
		    TopDocs results = searcher.search(query,999999999);
		    ScoreDoc[] hits = results.scoreDocs;
		   this.tweets=new ArrayList<String>();
		    
		    for(int i = 0; i < hits.length; i++) {
		    	Document doc = searcher.doc(hits[i].doc);
		    	this.tweets.add(doc.get("text"));
		    	String id = doc.get("id");
		    	//System.out.println((i+1)+".- score="+hits[i].score + " doc="+hits[i].doc+" id="+id);
		    	
		    	//System.out.println(doc.get("text"));
		    	//System.out.println(doc.get("pais"));
		    	//System.out.println("--------.----------------");
		    }
		    reader.close();
		    
		}
		catch(IOException ioe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, ioe);
		}
		catch(ParseException pe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, pe);
		}
		return tweets;
	}
	
public ArrayList<String> buscarPorUsuario(String videoGame,String usuarioName) {
		
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indice")));
			IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new StandardAnalyzer();
		    
		    QueryParser parser = new QueryParser("text", analyzer);
		    Query query = parser.parse("(text:"+videoGame+") AND (name:"+usuarioName+")");
		  
		    TopDocs results = searcher.search(query,999999999);
		    ScoreDoc[] hits = results.scoreDocs;
		   this.tweets=new ArrayList<String>();
		    
		    for(int i = 0; i < hits.length; i++) {
		    	Document doc = searcher.doc(hits[i].doc);
		    	this.tweets.add(doc.get("text"));
		    	String id = doc.get("id");
		    	//System.out.println((i+1)+".- score="+hits[i].score + " doc="+hits[i].doc+" id="+id);
		    	
		    	//System.out.println(doc.get("text"));
		    	//System.out.println(doc.get("pais"));
		    	//System.out.println("--------.----------------");
		    }
		    reader.close();
		    
		}
		catch(IOException ioe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, ioe);
		}
		catch(ParseException pe) {
			Logger.getLogger(IndiceInvertido.class.getName()).log(Level.SEVERE, null, pe);
		}
		return tweets;
	}
		
}
	
	
	
	
	


