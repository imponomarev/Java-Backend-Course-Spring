package edu.java.clientGithub;

import reactor.core.publisher.Flux;

public interface GithubClient {

    public Flux<GithubResponse> fetchRepositoryInfo(String owner, String repoName);

}
