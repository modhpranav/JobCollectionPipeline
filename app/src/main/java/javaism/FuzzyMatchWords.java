package javaism;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.*;


public class FuzzyMatchWords {

    private final Directory directory;

    public FuzzyMatchWords(List<String> words) throws Exception {
        
        // Create an index
        this.directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(this.directory, config);
        for (String word : words){
            addDocument(indexWriter, word);
        }
        indexWriter.close();
    }
    
    public List<String> matcher(List<String> jobWords){

        // Search for similar terms
        IndexSearcher searcher = new IndexSearcher(this.directory);
        QueryParser parser = new QueryParser("field", new StandardAnalyzer());
        List<String> res = new ArrayList<String>();
        for (String word : jobWords) {
            Query query = parser.parse(word); // ~ indicates fuzzy search
            TopDocs results = searcher.search(query, 2);
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("field"));
            }
        }return res;
    }

    private static void addDocument(IndexWriter indexWriter, String text) throws Exception {
        Document doc = new Document();
        doc.add(new Field("field", text, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(doc);
    }
}
