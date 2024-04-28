package edu.java.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class RetryGenerator {

    private RetryGenerator() {
    }

    public static Retry generate(
        BackoffType backoffType,
        int count,
        int interval,
        List<HttpStatus> statuses,
        String retryName
    ) {

        RetryConfig retryConfig = switch (backoffType) {
            case CONSTANT -> setConstantRetryPolicy(count, interval, statuses);
            case LINEAR -> setLinearRetryPolicy(count, interval, statuses);
            case EXPONENTIAL -> setExponentialRetryPolicy(count, statuses);
        };

        return Retry.of(retryName, retryConfig);

    }

    private static RetryConfig setConstantRetryPolicy(int count, int interval, List<HttpStatus> statuses) {
        return RetryConfig.custom()
            .maxAttempts(count)
            .waitDuration(Duration.ofSeconds(interval))
            .retryOnException(
                exception -> exception instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) exception).getStatusCode())
            )
            .build();
    }

    private static RetryConfig setLinearRetryPolicy(int count, int interval, List<HttpStatus> statuses) {
        return RetryConfig.custom()
            .maxAttempts(count)
            .intervalFunction(IntervalFunction.of(
                Duration.ofSeconds(interval),
                attempt -> attempt * interval
            ))
            .retryOnException(
                exception -> exception instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) exception).getStatusCode())
            )
            .build();
    }

    private static RetryConfig setExponentialRetryPolicy(int count, List<HttpStatus> statuses) {
        return RetryConfig.custom()
            .maxAttempts(count)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(
                IntervalFunction.DEFAULT_INITIAL_INTERVAL,
                IntervalFunction.DEFAULT_MULTIPLIER
            ))
            .retryOnException(
                exception -> exception instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) exception).getStatusCode())
            )
            .build();
    }
}
