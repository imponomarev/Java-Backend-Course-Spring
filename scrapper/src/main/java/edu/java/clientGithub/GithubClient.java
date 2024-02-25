package edu.java.clientGithub;

import reactor.core.publisher.Flux;

public interface GithubClient {

    Flux<GithubResponse> fetchRepositoryInfo(String owner, String repoName);

}
