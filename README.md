# Job Collection Pipeline

## Overview

The Job Collection Pipeline is a comprehensive system designed to crawl job postings from LinkedIn and Indeed, process the data in real-time, and store it in MongoDB. The system utilizes Docker, Kafka, Puppeteer, Spring Boot, and LLM (Large Language Model) integration to achieve its goals.

## Components

1. **Docker Compose Configuration (Kafka-compose.yml)**
   - Contains services for MongoDB, Zookeeper, Kafka, Kafdrop, and the Ollama LLM model.
   - Allows easy deployment and management of the required services using Docker.

2. **Model Initialization (Entrypoint.sh)**
   - Executes the necessary steps to pull the required model for Ollama inside the container.
   - Ensures that the LLM model is available for use within the system.

3. **Crawler (Crawler Package)**
   - Provides a comprehensive package for crawling job postings from LinkedIn and Indeed.
   - Utilizes Puppeteer to scrape data efficiently from the target websites.

4. **Kafka Streaming (Kafka Integration)**
   - Streams the crawled job data in real-time to the Spring Boot application using Kafka.
   - Generates CSV files containing all crawled postings and unscrapable URLs for further analysis.

5. **Kafka Management (Kafdrop, Docker Terminal)**
   - Facilitates the management and monitoring of Kafka topics using Kafdrop or Docker terminal.
   - Allows users to inspect message queues and debug any issues that may arise.

6. **Spring Boot Application**
   - Consumes data from Kafka and processes it.
   - Extracts skills from job descriptions using text processing and fuzzy analysis.
   - Integrates with the LLM model for advanced text analysis (optional).
   - Inserts processed data into MongoDB using the MongoDB API in Java.

## Usage

1. **Setup Docker Compose**
   - Ensure Docker Compose is installed on your system.
   - Run `docker-compose -f Kafka-compose.yml up` to start all required services.

2. **Initialize Model**
   - Execute `entrypoint.sh` to pull the required model for Ollama.

3. **Run Crawler**
   - Navigate to the crawler package and execute the crawling script.
   - Monitor the process and check generated CSV files for crawled data and unscrapable URLs.

4. **Manage Kafka**
   - Access Kafdrop or use Docker terminal to manage Kafka topics and messages.

5. **Configure Spring Boot**
   - Modify application.properties to configure MongoDB URI and other settings as needed.

6. **Run Spring Boot Application**
   - Start the Spring Boot application to consume Kafka messages and process job postings.
   - Verify that data is correctly inserted into MongoDB.

## Integration with LLM Model (Optional)

1. Uncomment the relevant code in the Kafka consumer service file to enable integration with the LLM model.
2. Follow the instructions provided in the code comments to chat with the LLM model and extract specific information from job descriptions.

## Video Demonstration


https://github.com/modhpranav/JobCollectionPipeline/assets/47596415/99f79650-dc49-4b20-9120-78b711e2fb91


## Credits
1. FuzzyMatching - [Medium Article](https://medium.com/wenable/end-to-end-use-of-java-lucene-fuzzy-to-search-a-name-5224653bde77)
2. LLM Model - [Ollama](https://ollama.com/)

## Contributing

Contributions to the Job Collection Pipeline are welcome! Feel free to submit bug reports, feature requests, or pull requests to help improve the pipeline.

## License

This project is licensed under the [MIT License](LICENSE).
