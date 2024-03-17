package com.jobworker.preprocess.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.*;

@Service
public class FuzzyMatcher {


    private static final String SKILLS = "Skill";

    private static final String INDEXDIR = "/index";

    private Set<String> indexedSkills = new HashSet<String>();

    public Set<String> matcher(String jobDescription) throws IOException{

        // Search for similar terms
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(FileSystems.getDefault().getPath(INDEXDIR)));
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(SKILLS, new StandardAnalyzer());
        Set<String> res = new HashSet<String>();
        Set<String> jobWords = new HashSet<String>(Arrays.asList(jobDescription.split("\\s+")));
        for (String word : jobWords) {
            try{
                Query query = parser.parse(word);
                TopDocs results = searcher.search(query, 1);
                for (ScoreDoc scoreDoc : results.scoreDocs) {
                    Document doc = searcher.doc(scoreDoc.doc);
                    res.add(doc.get(SKILLS));
                }
            }catch (Exception e) {
//                System.out.println("Error matching: "+ word);
            }
        }
        reader.close();
        return res;
    }

    public Map<String, String> createIndex(Set<String> words) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.indexedSkills = this.getExistingWords();
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(FileSystems.getDefault().getPath(INDEXDIR)), config
        );
        Map <String, String> result = new HashMap<String, String>();
        for (String word : words){
            if (!indexedSkills.contains(word)){
                try{
                    Document doc = new Document();
                    doc.add(new TextField(SKILLS, word, Field.Store.YES));
                    indexWriter.addDocument(doc);
                    result.put("Added words", result.get("Added words")+","+word);
                }catch (Exception e){
                    e.printStackTrace();
                    result.put("Failed words", result.get("Failed words")+","+word);
                }
            }
        }
        Set<String> totalWords = this.getExistingWords();
        indexWriter.close();
        return result;
    }

    private Set<String> getExistingWords() throws IOException {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(FileSystems.getDefault().getPath(INDEXDIR)));
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(SKILLS, new StandardAnalyzer());
            Set<String> existingWords = new HashSet<>();
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                String skill = doc.get(SKILLS);
                existingWords.add(skill);
            }
            reader.close();
            return existingWords;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
