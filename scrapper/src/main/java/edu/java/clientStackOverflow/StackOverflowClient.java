package edu.java.clientStackOverflow;

import reactor.core.publisher.Flux;

public interface StackOverflowClient {

    public Flux<StackOverflowResponse> fetchQuestion(long questionId);

}
