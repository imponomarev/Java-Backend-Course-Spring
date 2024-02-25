package edu.java.configuration;

import edu.java.clientGithub.GitHubClientImplementation;
import edu.java.clientGithub.GithubClient;
import edu.java.clientStackOverflow.StackOverflowClient;
import edu.java.clientStackOverflow.StackOverflowClientImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    GithubClient githubClient() {
        return new GitHubClientImplementation();
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        return new StackOverflowClientImplementation();
    }

}
