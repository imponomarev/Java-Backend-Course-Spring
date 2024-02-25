package edu.java.clientStackOverflow;

import reactor.core.publisher.Flux;

public interface StackOverflowClient {

    Flux<StackOverflowResponse> fetchQuestion(long questionId);

}
