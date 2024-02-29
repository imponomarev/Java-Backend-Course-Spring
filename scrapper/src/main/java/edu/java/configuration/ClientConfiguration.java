package edu.java.configuration;

import edu.java.clientGithub.GitHubWebClient;
import edu.java.clientGithub.GithubClient;
import edu.java.clientStackOverflow.StackOverflowClient;
import edu.java.clientStackOverflow.StackOverflowWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${api.github.baseUrl}")
    private String githubBaseUrl;

    @Value("${api.stackoverflow.baseUrl}")
    private String stackOverflowBaseUrl;

    @Bean
    GithubClient githubClient() {
        return new GitHubWebClient(githubBaseUrl);
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        return new StackOverflowWebClient(stackOverflowBaseUrl);
    }

}
