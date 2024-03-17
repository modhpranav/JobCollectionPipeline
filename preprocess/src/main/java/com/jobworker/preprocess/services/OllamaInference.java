package com.jobworker.preprocess.services;

import com.jobworker.preprocess.models.LlamaResponse;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaInference implements LlamaServiceInterface {

    private final ChatClient chatClient;

    @Autowired
    public OllamaInference(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void initiate(String base){
        final String llamaMessage = chatClient.call("I am building an api to fetch required job skills from job descriptions, I will provide you job descriptions fetch fifteen most relevant skills from it, assume you are my api responsible for only providing skills thats it.");
        System.out.print(llamaMessage);

    }

    @Override
    public LlamaResponse getSkills(String text) {
        final String llamaMessage = chatClient.call("Job description: "+text);
        return new LlamaResponse().setMessage(llamaMessage);
    }
}