package com.jobworker.preprocess.repositories;

import com.jobworker.preprocess.models.JobDescriptionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobDescriptionRepository extends MongoRepository<JobDescriptionModel, String> {
}

