package com.jobworker.preprocess.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExtractSkills {
    private Set<String> existingWords = new HashSet<String>();

    public ExtractSkills(List<String> filePaths) throws Exception {
        loadSkills(filePaths);
        new FuzzyMatcher().createIndex(this.existingWords);
    }

    public Set<String> getWords(String text, String sep){
        String[] lineWords = text.split(sep);
        Set<String> words = new HashSet();
        for (String word : lineWords) {
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                if (word.toLowerCase().contains("(")) {
                    words.addAll(Arrays.asList(word.split("\\(|\\)")));
                } else {
                    words.add(word);
                }
            }
        }return words;
    }

    public void loadSkills(List<String> filePaths){
        for (String filename: filePaths){
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = br.readLine()) != null) {
                    this.existingWords.addAll(this.getWords(line, ","));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
