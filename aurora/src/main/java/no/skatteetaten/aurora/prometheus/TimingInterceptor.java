package no.skatteetaten.aurora.prometheus;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import no.skatteetaten.aurora.prometheus.collector.HttpMetricsCollector;

@Component
public class TimingInterceptor implements ClientHttpRequestInterceptor {

    private HttpMetricsCollector collector;

    public TimingInterceptor(@Qualifier("client") HttpMetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        long startTime = System.nanoTime();
        int statusCode = 0;

        try {
            ClientHttpResponse response = execution.execute(request, body);
            statusCode = response.getRawStatusCode();
            return response;
        } finally {
            collector.record(request.getMethod().name(), request.getURI().toString(), statusCode, startTime);
        }
    }
}
