/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package javaism;

import org.json.JSONObject;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import javaism.FuzzyMatchWords;


public class App {

    private Set<String> existingWords;


    public App() {
        this.existingWords = new HashSet<String>();
        loadSkills();
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

    public void loadSkills() {
        String[] csvfiles = {"configData/suggestedSkills.csv", "configData/Skills.csv"};

        for (String filename : csvfiles) {
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

    public List<String> extractSkills(String text){
        text = text.toLowerCase();
        Set<String> words = new HashSet();
        for (String line : text.split("\\.")){
            Set<String> fetchedWords = this.getWords(line, " ");
            // fetchedWords.retainAll(this.existingWords);
            words.addAll(fetchedWords);
        }
        return new ArrayList<>(words);
    }

    public List<String> run(String text){
        return this.extractSkills(text);
    }

    public static void main(String[] args) {
        App instance = new App();
        FuzzyMatch fMatch = new FuzzyMatchWords(instance.existingWords);
        List<String> skillsFound = instance.run("Some Python Java job description.");
        skillsFound = fMatch.matcher(skillsFound);
        System.out.println("Final Result:");
        System.out.println(skillsFound);
    }
}