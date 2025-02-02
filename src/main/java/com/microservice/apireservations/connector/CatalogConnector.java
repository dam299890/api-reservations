package com.microservice.apireservations.connector;

import com.microservice.apireservations.connector.configuration.EndpointConfiguration;
import com.microservice.apireservations.connector.configuration.HostConfiguration;
import com.microservice.apireservations.connector.configuration.HttpConnectorConfiguration;
import com.microservice.apireservations.connector.response.CityDTO;
import com.microservice.apireservations.controller.ReservationController;
import com.microservice.apireservations.enums.APIError;
import com.microservice.apireservations.exception.EdteamException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Component
public class CatalogConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogConnector.class);

    private final String HOST = "api-catalog";
    private final String ENDPOINT = "get-city";
    private HttpConnectorConfiguration configuration;
    @Autowired
    public CatalogConnector(HttpConnectorConfiguration configuration) {
        this.configuration = configuration;
    }

    @CircuitBreaker(name = "api-catalog", fallbackMethod = "fallbackGetCity")
    public CityDTO getCity(String code) {

        HostConfiguration hostConfiguration= configuration.getHosts().get(HOST);
        EndpointConfiguration endpointConfiguration = hostConfiguration.getEndPoints().get(ENDPOINT);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(endpointConfiguration.getConnectionTimeout()))
                .doOnConnected(conn -> conn
                        .addHandler(new ReadTimeoutHandler(endpointConfiguration.getReadTimeout(), TimeUnit.MILLISECONDS))
                        .addHandler(new WriteTimeoutHandler(endpointConfiguration.getWriteTimeout(), TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder()
                .baseUrl("http://" + hostConfiguration.getHost() + ":" + hostConfiguration.getPort() + endpointConfiguration.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        return client.get()
                .uri(urlEncoder -> urlEncoder.build(code))
                .retrieve()
                .bodyToMono(CityDTO.class)
                .share()
                .block();
    }

    public CityDTO fallbackGetCity(String code, CallNotPermittedException ex) {
        LOGGER.info("calling to fallbackGetCity-1");

        return new CityDTO();
    }

    public CityDTO fallbackGetCity(String code, Exception ex) {
        LOGGER.info("calling to fallbackGetCity-2");

        throw new EdteamException(APIError.VALIDATION_ERROR);
    }

}
