package com.jobworker.preprocess.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class TextPreprocessor {

    private TokenizerME tokenizer;
    private List<String> stopWords;

    public TextPreprocessor() throws IOException {
        // Load tokenizer model
        try (InputStream modelIn = getClass().getResourceAsStream("/en-token.bin")) {
            TokenizerModel model = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(model);
        }

        // Load stop words
        try (InputStream stopWordsStream = getClass().getResourceAsStream("/stopwords.txt")) {
            stopWords = Arrays.asList(IOUtils.toString(stopWordsStream).split("\\s+"));
        }
    }

    public String preprocessText(String text) {
        // Tokenize text
        String[] tokens = tokenizer.tokenize(text);

        // Remove stop words
        StringBuilder cleanedText = new StringBuilder();
        for (String token : tokens) {
            if (!stopWords.contains(token.toLowerCase())) {
                cleanedText.append(token).append(" ");
            }
        }

        return cleanedText.toString().trim();
    }

    public String process(String text) throws IOException {
        TextPreprocessor preprocessor = new TextPreprocessor();
        String cleanedDescription = preprocessor.preprocessText(text);
        return cleanedDescription;
    }
}

