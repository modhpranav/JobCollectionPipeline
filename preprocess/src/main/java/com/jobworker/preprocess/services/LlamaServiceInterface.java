package com.jobworker.preprocess.services;

import com.jobworker.preprocess.models.LlamaResponse;

public interface LlamaServiceInterface {

    void initiate(String base);
    LlamaResponse getSkills(String prompt);
}
