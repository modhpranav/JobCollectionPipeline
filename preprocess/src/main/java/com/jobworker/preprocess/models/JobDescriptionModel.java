package com.jobworker.preprocess.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "job")
public class JobDescriptionModel {
    @Id
    private String id;
    private String jobDescription;
    private String jobTitle;
    private String company;
    private String postingUrl;
    private List jobSkills;

    // Constructors, getters, and setters
    public JobDescriptionModel() {}

    public JobDescriptionModel(String jobDescription, String jobTitle, String company, String postingUrl, List jobSkills) {
        this.jobDescription = jobDescription;
        this.company = company;
        this.jobSkills = jobSkills;
        this.jobTitle = jobTitle;
        this.postingUrl = postingUrl;
    }
}
